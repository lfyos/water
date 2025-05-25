package kernel_render;

import kernel_camera.camera_result;
import kernel_common_class.const_value;
import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_part.part;
import kernel_part.part_parameter;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_transformation.box;
import kernel_driver.component_instance_driver;

public class list_component_on_collector
{
	public	component_collector collector;
	
	private scene_kernel sk;
	private client_information ci;
	private camera_result cam_result;
	
	private int no_driver_component_number;
	
	private boolean register(component comp,int driver_id)
	{
		if(cam_result.target.parameter.part_list_only_flag)
			if(!(comp.uniparameter.part_list_flag))
				return (comp.children.size()<=0)?true:false;
		
		part my_part;
		component_instance_driver in_dr;
		int driver_number=comp.driver_array.size();
		if((driver_id<0)||(driver_id>=driver_number))
			return false;
		if((my_part=comp.driver_array.get(driver_id).component_part)==null)
			return false;
		
		if(cam_result.target.parameter.discard_cross_clip_plane_flag)
			if(comp.clip.clip_plane.size()>0)
				return (comp.children.size()<=0)?true:false;
	
		if((in_dr=ci.component_instance_driver_cont.
			get_component_instance_driver(comp, driver_id))==null)
				return false;
		
		try{
			if(in_dr.check(sk,ci,cam_result))
				return false;
		}catch(Exception e){
			e.printStackTrace();
			
			debug_information.println("Execute check in instance_driver fail:	",e.toString());
		
			debug_information.println("Component name:	",	comp.component_name);
			debug_information.println("Driver ID:	",		driver_id);
			debug_information.println("Part user name:	",	my_part.user_name);
			debug_information.println("Part system name:",	my_part.system_name);
			debug_information.println("Mesh file name:	",	my_part.directory_name+my_part.mesh_file_name);
			
			return false;
		}
		
		if(cam_result.target.parameter.discard_unload_component_flag)
			if(ci.render_buffer.mesh_loader.load_test(sk.process_part_sequence,my_part))
				return false;
		
		collector.register_component(comp,driver_id);
		
		return true;
	}
	
	private boolean do_lod(component comp)
	{
		box my_box;
		if((my_box=comp.get_component_box(false))==null)
			return false;
		
		my_box=cam_result.matrix.multiply(my_box);
		my_box.p[1].z=my_box.p[0].z;
		double lod_precision2=my_box.distance2();
		
		double lod_precision_scale=1.0;
		double my_lod_precision_scale=comp.uniparameter.component_driver_lod_precision_scale;
		if(my_lod_precision_scale>const_value.min_value)
			lod_precision_scale*=my_lod_precision_scale;
		my_lod_precision_scale=ci.component_instance_driver_cont.get_lod_precision_scale(comp);
		if(my_lod_precision_scale>const_value.min_value)
			lod_precision_scale*=my_lod_precision_scale;
		my_lod_precision_scale=cam_result.target.parameter.lod_precision_scale;
		if(my_lod_precision_scale>const_value.min_value)
			lod_precision_scale*=my_lod_precision_scale;

		lod_precision2*=lod_precision_scale*lod_precision_scale;

		if(cam_result.target.parameter.do_discard_lod_flag)
			if(lod_precision2<=comp.uniparameter.discard_precision2)
				return true;
		
		if(cam_result.target.parameter.do_selection_lod_flag)
			if(comp.multiparameter[cam_result.target.parameter_channel_id].can_display_assembly_flag)
				for(int i=0,driver_number=comp.driver_array.size();i<driver_number;i++) {
					part_parameter part_par=comp.driver_array.get(i).component_part.part_par;
					if(part_par.discard_precision2<=lod_precision2){
						if(comp.children.size()>0)
							if(part_par.assembly_precision2<=lod_precision2)
								return false;
						if(register(comp,i))
							return true;
					}
				}
		return false;
	}
	private void collect_routine(component comp,int clipper_test_depth)
	{
		if(comp.clip==null)
			return;
		
		comp.clip.clipper_test_depth=clipper_test_depth;
				
		if(!(comp.multiparameter[cam_result.target.parameter_channel_id].effective_display_flag))
			return;
	
		comp.clip.has_done_clip_flag=false;
		if(cam_result.clipper_test(comp,sk.component_cont,cam_result.target.parameter_channel_id))
			return;
		
		if((cam_result.target.parameter.do_discard_lod_flag)||(cam_result.target.parameter.do_selection_lod_flag))
			if(!(comp.selected_component_family_flag))			
				if(do_lod(comp))
					return;
		
		int children_number	=comp.children.size();
		int driver_number	=comp.driver_array.size();
		if(children_number<=0){
			for(int i=0;i<driver_number;i++)
				if(register(comp,i))
					return;
			no_driver_component_number++;
			return;
		}
		
		int old_no_driver_component_number=no_driver_component_number;
		for(int i=0;i<children_number;i++)
			collect(comp.children.get(i),clipper_test_depth+1);
		
		if(sk.scene_par.not_do_ancestor_render_flag)
			return;
		if((driver_number<=0)||(children_number<=0)||(no_driver_component_number<=old_no_driver_component_number))
			return;
		if(comp.multiparameter[cam_result.target.parameter_channel_id].can_display_assembly_flag)
			for(int i=0;i<driver_number;i++)
				if(comp.driver_array.get(i).component_part.is_normal_part())
					if(register(comp,i)){
						no_driver_component_number=old_no_driver_component_number;
						return;
					}
		return;
	}
	private void collect(component comp,int clipper_test_depth)
	{
		comp.caculate_location(sk.component_cont,false);
		
		collect_routine(comp,clipper_test_depth);
		
		comp.caculate_effective_display_flag(cam_result.target.parameter_channel_id);
		comp.caculate_assembly_flag(cam_result.target.parameter_channel_id);
		comp.caculate_box(sk.component_cont);
	}
	public list_component_on_collector(scene_kernel my_sk,client_information my_ci,camera_result my_cam_result)
	{
		sk				=my_sk;
		ci				=my_ci;
		cam_result		=my_cam_result;	
		
		collector		=new component_collector(sk.render_cont.renders);
		
		no_driver_component_number=0;
	
		component pickup_comp=ci.parameter.comp;
		for(component p=pickup_comp;p!=null;p=sk.component_cont.get_component(p.parent_component_id))
			p.selected_component_family_flag=true;
		for(int i=0,ni=cam_result.target.comp.length;i<ni;i++)
			cam_result.target.comp[i].recurse_caculate_location(sk.component_cont);
		for(int i=0,ni=cam_result.target.comp.length;i<ni;i++)
			collect(cam_result.target.comp[i],0);
		for(component p=pickup_comp;p!=null;p=sk.component_cont.get_component(p.parent_component_id))
			p.selected_component_family_flag=false;
	}
}	
		
		
