package driver_component_selection;

import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_render.render_target;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_transformation.point;
import kernel_camera.locate_camera;
import kernel_driver.component_driver;
import kernel_common_class.const_value;
import kernel_component.component_array;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_component.component_selection;
import kernel_driver.component_instance_driver;
import kernel_render.list_component_on_collector;

public class extended_component_instance_driver extends component_instance_driver
{
	private int screen_rectangle_component_id,audio_component_id,modifier_container_id;
	private boolean change_type_flag;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,
			int my_screen_rectangle_component_id,int my_audio_component_id,int my_modifier_container_id)
	{
		super(my_comp,my_driver_id);
		
		screen_rectangle_component_id=my_screen_rectangle_component_id;
		audio_component_id=my_audio_component_id;
		modifier_container_id=my_modifier_container_id;
		change_type_flag=true;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
		ci.request_response.print(screen_rectangle_component_id);
	}
	public boolean check(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		if((cr.target.main_display_target_flag)&&(ci.display_camera_result!=null)){
			if(ci.display_camera_result.cam.parameter.change_type_flag^change_type_flag){
				change_type_flag=change_type_flag?false:true;
				update_component_parameter_version(0);
			}
			return false;
		}
		return true;
	}
	public void create_render_parameter(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print(change_type_flag?"1":"0");
	}
	private void select_many_component(component comp,int driver_id,
			int control_code,scene_kernel sk,client_information ci)
	{
		String str;
		
		if(ci.display_camera_result.target==null)
			return;
		if((str=ci.request_response.get_parameter("function"))==null)
			return ;
		int function_id=Integer.decode(str);
		if((str=ci.request_response.get_parameter("x0"))==null)
			return ;
		double my_x0=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("y0"))==null)
			return ;
		double my_y0=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("x1"))==null)
			return ;
		double my_x1=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("y1"))==null)
			return ;
		double my_y1=Double.parseDouble(str);
		
		if(my_x0>my_x1) 
			{double p=my_x0;my_x0=my_x1;my_x1=p;}
		if(my_y0>my_y1) 
			{double p=my_y0;my_y0=my_y1;my_y1=p;}
		
		render_target t=ci.display_camera_result.target;
		double local_xy[]=t.target_view.caculate_view_local_xy(my_x0, my_y0);
		my_x0=local_xy[0];	my_y0=local_xy[1];
		
		local_xy=t.target_view.caculate_view_local_xy(my_x1, my_y1);
		my_x1=local_xy[0];	my_y1=local_xy[1];
		
		box view_volume_box=t.view_volume_box;
		point center=view_volume_box.center(),diff=view_volume_box.p[1].sub(center);
		view_volume_box=new box(
				center.x+diff.x*my_x0,center.y+diff.y*my_y0,view_volume_box.p[0].z,
				center.x+diff.x*my_x1,center.y+diff.y*my_y1,view_volume_box.p[1].z);
		
		render_target cam_target=new render_target(true,null,comp.component_id,driver_id,0,
				new component[]{sk.component_cont.root_component},null,	t.camera_id,t.parameter_channel_id,
				null,view_volume_box,ci.clip_plane,null,false,false);
	
		if(cam_target.view_volume_box.distance2()<const_value.min_value2)
			return ;
		
		camera_result cam_result=new camera_result(ci.display_camera_result.cam,cam_target,sk.component_cont);
		component_collector collector=(new list_component_on_collector(
			true,false,false,((function_id%2)==0)?true:false,false,sk,ci,cam_result)).collector;
		
		if(collector.component_number<=0)
			return;

		switch(function_id){
		default:
			return;
		case 0:
		case 1:
			component_selection cs=new component_selection(sk); 
			for(int i=0,ni=collector.component_collector.length;i<ni;i++)
				if(collector.component_collector[i]!=null)
					for(int j=0,nj=collector.component_collector[i].length;j<nj;j++)
						for(component_link_list p=collector.component_collector[i][j];p!=null;p=p.next_list_item)
							cs.set_selected_flag(p.comp,sk.component_cont);
			break;
		case 2:
		case 3:
			component_array comp_cont;
			box my_box;
			if((comp_cont=collector.get_component_array())!=null)
				if((my_box=comp_cont.get_box())!=null)
					(new locate_camera(ci.display_camera_result.cam)).locate_on_components(
							sk.modifier_cont[modifier_container_id],my_box,null,
							ci.display_camera_result.cam.parameter.scale_value,true,true,true);
			break;
		}
		if(control_code==0) 
			return;
		
		sk.collector_stack.push_collector(true,
			sk.system_par,sk.scene_par,collector,sk.component_cont,sk.render_cont.renders);
		collector.title=Long.toString(collector.list_id);
		
		return;
	}
	
	private void select_single_component(
			component comp,int control_code,scene_kernel sk,client_information ci)
	{
		String str;

		if((str=ci.request_response.get_parameter("function"))==null)
			return;
		
		driver_audio_player.extended_component_driver acd=null;
		component_driver cd=sk.component_cont.get_component(audio_component_id).driver_array.get(0);
		if(cd!=null)
			acd=(driver_audio_player.extended_component_driver)cd;
		
		switch(Integer.decode(str)){
		default:
			break;
		case 0:
		case 1:
			if(acd!=null)
				acd.set_audio(null);
			component_selection cs=new component_selection(sk);
			
			if(ci.parameter.comp==null)
				cs.clear_selected_flag(sk.component_cont);
			else
				cs.switch_selected_flag(ci.parameter.comp,sk.component_cont);
			break;
		case 2:
		case 3:
			(new locate_camera(ci.display_camera_result.cam)).locate_on_components(
					sk.modifier_cont[modifier_container_id],sk.component_cont,ci.display_camera_result,
					ci.parameter,null,ci.display_camera_result.cam.parameter.scale_value,
					sk.modifier_cont[modifier_container_id].get_timer().get_current_time(),
					true,true,true,null,null);
			break;
		}
		if(control_code==0)
			return;
		if(ci.parameter.comp.children_number()>0)
			return;
		if(ci.parameter.comp.driver_number()<=0)
			return;

		component_array comp_array=new component_array();
		component_collector collector=sk.collector_stack.get_top_collector();
		
		if(collector!=null)
			if(collector.component_number==1){
				comp_array.add_collector(collector);
				if(comp_array.comp_list.get(0).component_id==ci.parameter.comp.component_id){
					if(acd!=null)
						acd.set_audio(collector.audio_file_name);
					return;
				}
			}
		comp_array.clear_compoment();
		comp_array.add_component(ci.parameter.comp);
		sk.collector_stack.push_component_array(true,sk.system_par,
			sk.scene_par,comp_array,sk.component_cont,sk.render_cont.renders);
		if((collector=sk.collector_stack.get_top_collector())!=null)							
			if(acd!=null)
				acd.set_audio(collector.audio_file_name);
		return;
	}
	private void view_scale(component comp,int control_code,scene_kernel sk,client_information ci)
	{
		String str;
		
		if(ci.display_camera_result.target==null)
			return;
		if((str=ci.request_response.get_parameter("distance"))!=null)
			ci.display_camera_result.cam.parameter.distance=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("half_fovy_tanl"))!=null)
			ci.display_camera_result.cam.parameter.half_fovy_tanl=Double.parseDouble(str);
		ci.render_buffer.cam_buffer.synchronize_camera_buffer(
				sk.camera_cont,ci.display_camera_result.target.camera_id);
		return;
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		String str;
		int control_code=0;

		if((str=ci.request_response.get_parameter("control"))!=null)
			control_code=Integer.decode(str);
		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;
		switch(str) {
		default:
			break;
		case "single":
			select_single_component(comp,control_code,sk,ci);
			break;
		case "many":
			select_many_component(comp,driver_id,control_code,sk,ci);
			break;
		case "scale":
			view_scale(comp,control_code,sk,ci);
			break;
		}
		return null;
	}
}
