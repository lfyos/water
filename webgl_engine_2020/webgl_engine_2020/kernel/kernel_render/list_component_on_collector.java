package kernel_render;

import kernel_camera.camera_result;
import kernel_common_class.const_value;
import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_part.part;
import kernel_transformation.box;
import kernel_driver.instance_driver;


public class list_component_on_collector
{
	public	component_collector collector;
	private engine_kernel ek;
	private client_information ci;
	private camera_result cam_result;
	private boolean do_discard_lod_flag,do_selection_lod_flag,discard_cross_clip_plane_flag,discard_unload_component_flag,add_number_flag;
	private double camera_lod_precision_scale;
	
	private int no_driver_component_number;
	
	private boolean register(int render_buffer_id,component comp,int driver_id,double lod_precision2)
	{
		part my_part;
		instance_driver in_dr;
		int driver_number=comp.driver_number();
		if((driver_id<0)||(driver_id>=driver_number))
			return false;
		if((comp.fix_render_driver_id>=0)&&(comp.fix_render_driver_id<driver_number))
			driver_id=comp.fix_render_driver_id;

		if((my_part=comp.driver_array[driver_id].component_part)==null)
			return false;
		if((comp.clip.clip_plane!=null)&&discard_cross_clip_plane_flag)
			return (comp.children_number()<=0)?true:false;
		if((in_dr=ci.instance_container.get_driver(comp, driver_id))==null)
			return false;
		
		boolean abandon_display_flag=true;
		int data_buffer_id		=comp.driver_array[driver_id].same_part_component_driver_id;
		int parameter_channel_id=cam_result.target.parameter_channel_id;
		try{
			abandon_display_flag=in_dr.check(render_buffer_id,
					parameter_channel_id,data_buffer_id,ek,ci,cam_result,collector);
		}catch(Exception e){
			e.printStackTrace();
			
			debug_information.println("Execute check in instance_driver fail:	",e.toString());
		
			debug_information.println("Component name:	",	comp.component_name);
			debug_information.println("Driver ID:		",	driver_id);
			debug_information.println("Part user name:	",	my_part.user_name);
			debug_information.println("Part system name:",	my_part.system_name);
			debug_information.println("Mesh file name:	",	my_part.directory_name+my_part.mesh_file_name);
		}
		if(abandon_display_flag){
			if(add_number_flag)
				ci.statistics_client.abandon_component_number++;
			return false;
		}
		if(discard_unload_component_flag)
			if(ci.render_buffer.mesh_loader.load_test(ek.process_part_sequence,my_part)){
				if(add_number_flag)
					ci.statistics_client.not_load_component_number++;
				return false;
			}
		if(add_number_flag){
			if(lod_precision2<0)
				ci.statistics_client.lod_component_number[0]++;
			else if(my_part.mesh_file_name==null){
				if(my_part.top_box_part_flag)
					ci.statistics_client.top_box_component_number++;
				else
					ci.statistics_client.bottom_box_component_number++;
			}else{
				if((driver_id+1)>=ci.statistics_client.lod_component_number.length){
					int bak[]=ci.statistics_client.lod_component_number;
					ci.statistics_client.lod_component_number=new int[driver_id+2];
					for(int i=0,n=bak.length;i<n;i++)
						ci.statistics_client.lod_component_number[i]=bak[i];
					for(int i=bak.length,n=ci.statistics_client.lod_component_number.length;i<n;i++)
						ci.statistics_client.lod_component_number[i]=0;
				}
				ci.statistics_client.lod_component_number[driver_id+1]++;
			}
			if(comp.clip.clip_plane!=null)
				ci.statistics_client.clip_plane_component_number++;
		}
		
		collector.register_component(comp,driver_id);
		
		return true;
	}
	
	private boolean do_lod(int render_buffer_id,component comp,int parameter_channel_id)
	{
		if(!(do_discard_lod_flag||do_selection_lod_flag))
			return false;
		
		if(comp.selected_component_family_flag)
			return false;
		
		double lod_precision2;
		{
			box my_box;
			if((my_box=comp.get_component_box(false))==null)
				return false;
			
			double distance2=my_box.center().sub(cam_result.eye_point).distance2();
			double fovy_tanl=cam_result.cam.parameter.half_fovy_tanl*2.0;
			distance2*=fovy_tanl*fovy_tanl;
			
			double diameter2=my_box.distance2();
			lod_precision2=diameter2/distance2;
		}
		{
			lod_precision2*=camera_lod_precision_scale*camera_lod_precision_scale;
			double driver_lod_precision_scale=ci.instance_container.get_lod_precision_scale(comp);
			if(driver_lod_precision_scale>const_value.min_value)
				lod_precision2*=driver_lod_precision_scale*driver_lod_precision_scale;
		}
		
		if(do_discard_lod_flag){
			if(lod_precision2<=comp.uniparameter.discard_precision2){
				if(add_number_flag)
					ci.statistics_client.discard_component_number++;
				return true;
			}
		}
		
		if(do_selection_lod_flag){
			int driver_number=comp.driver_number();
			if(driver_number<=0)
				return false;
			if(comp.children_number()>0)
				if(!(comp.get_can_display_assembly_flag(parameter_channel_id)))
					return false;
			for(int i=0;i<driver_number;i++)
				if(comp.driver_array[i].component_part.part_par.discard_precision2<=lod_precision2){
					if(comp.children_number()>0)
						if(comp.driver_array[i].component_part.part_par.assembly_precision2<=lod_precision2)
							return false;
					if(register(render_buffer_id,comp,i,lod_precision2))
						return true;
				}
			return true;
		}
		return false;
	}
	private void collect(int render_buffer_id,component comp,int parameter_channel_id,int clipper_test_depth)
	{
		if(comp==null)
			return;
		
		if(comp.clip==null)
			return;
		
		comp.clip.clipper_test_depth=clipper_test_depth;
		
		if(add_number_flag)
			ci.statistics_client.collect_component_number++;
		
		if(!(comp.get_effective_display_flag(parameter_channel_id))){
			if(add_number_flag)
				ci.statistics_client.hide_component_number++;
			return;
		}
		comp.clip.has_done_clip_flag=false;
		if(cam_result.clipper_test(comp,ek.component_cont,parameter_channel_id)){
			if(add_number_flag)
				ci.statistics_client.clip_component_number++;
			return;
		}		
		if(do_lod(render_buffer_id,comp,parameter_channel_id))
			return;
		
		int children_number=comp.children_number();
		int driver_number=comp.driver_number();
		if(children_number<=0){
			for(int i=0;i<driver_number;i++)
				if(register(render_buffer_id,comp,i,-1))
					return;
			no_driver_component_number++;
			if(add_number_flag)
				ci.statistics_client.no_driver_component_number++;
			return;
		}
		int old_no_driver_component_number=no_driver_component_number;
		for(int i=0;i<children_number;i++)
			collect(render_buffer_id,comp.children[i],parameter_channel_id,clipper_test_depth+1);
		comp.caculate_box(false);
		
		if(driver_number<=0)
			return ;
		if(ek.scene_par.not_do_ancestor_render_flag)
			return;
		if(no_driver_component_number<=old_no_driver_component_number)
			return;
		if(ek.scene_par.test_display_assembly_flag)
			if(!(comp.get_can_display_assembly_flag(parameter_channel_id)))
				return;
		for(int i=0;i<driver_number;i++){
			part my_part=comp.driver_array[i].component_part;
			if((my_part.mesh_file_name!=null)||(!(my_part.top_box_part_flag)))
				if(register(render_buffer_id,comp,i,-1)){
					no_driver_component_number=old_no_driver_component_number;
					return;
				}
		}
	}
	
	public list_component_on_collector(				int render_buffer_id, 
		int parameter_channel_id,					boolean my_add_number_flag,
		boolean my_do_discard_lod_flag,				boolean my_do_selection_lod_flag,
		boolean my_discard_cross_clip_plane_flag,	boolean my_discard_unload_component_flag,
		engine_kernel my_ek,						client_information my_ci,
		camera_result my_cam_result)
	{
		ek											=my_ek;
		ci											=my_ci;
		cam_result									=my_cam_result;		
		add_number_flag								=my_add_number_flag;
		do_discard_lod_flag							=my_do_discard_lod_flag;
		do_selection_lod_flag						=my_do_selection_lod_flag;
		discard_cross_clip_plane_flag				=my_discard_cross_clip_plane_flag;
		discard_unload_component_flag				=my_discard_unload_component_flag;
		
		collector									=new component_collector(ek.render_cont.renders);
		
		no_driver_component_number=0;
		if(ci.parameter.high_or_low_precision_flag){
			camera_lod_precision_scale=cam_result.cam.parameter.high_precision_scale;
			for(component p=ci.parameter.comp;p!=null;p=ek.component_cont.get_component(p.parent_component_id))
				p.selected_component_family_flag=true;
		}else
			camera_lod_precision_scale=cam_result.cam.parameter.low_precision_scale;
		
		if(cam_result.target.driver_id==null){
			for(int i=0,ni=cam_result.target.comp.length;i<ni;i++)
				if(cam_result.target.comp[i]!=null)
					collect(render_buffer_id,cam_result.target.comp[i],parameter_channel_id,0);
		}else{
			int comp_number=cam_result.target.comp.length;
			int driver_number=cam_result.target.driver_id.length;
			for(int i=0,ni=(comp_number<driver_number)?comp_number:driver_number;i<ni;i++)
				if((cam_result.target.comp[i]!=null)&&(cam_result.target.driver_id[i]>=0))
					register(render_buffer_id,cam_result.target.comp[i],cam_result.target.driver_id[i],-1.0);
		}
		if(ci.parameter.high_or_low_precision_flag)
			for(component p=ci.parameter.comp;p!=null;p=ek.component_cont.get_component(p.parent_component_id))
				p.selected_component_family_flag=false;
	}
}