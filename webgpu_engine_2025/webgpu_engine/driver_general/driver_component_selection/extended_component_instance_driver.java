package driver_component_selection;

import kernel_scene.scene_kernel;
import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_transformation.point;
import kernel_camera.locate_camera;
import kernel_render.render_target;
import kernel_driver.component_driver;
import kernel_scene.client_information;
import kernel_component.component_array;
import kernel_component.component_collector;
import kernel_component.component_selection;
import kernel_render.render_target_parameter;
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
		
		screen_rectangle_component_id	=my_screen_rectangle_component_id;
		audio_component_id				=my_audio_component_id;
		modifier_container_id			=my_modifier_container_id;
		change_type_flag				=true;
	}
	public void response_initialization_component_data(scene_kernel sk,client_information ci)
	{
		ci.request_response.print(screen_rectangle_component_id);
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		if(cr.target.main_display_target_flag) {
			if(ci.display_camera_result.cam.parameter.change_type_flag^change_type_flag){
				change_type_flag=change_type_flag?false:true;
				update_component_parameter_version(0);
			}
			return false;
		}
		return true;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print(change_type_flag?"1":"0");
	}
	private void select_many_component(int control_code,scene_kernel sk,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("function"))==null)
			return;
		int function_id=Integer.decode(str);
		if((str=ci.request_response.get_parameter("x0"))==null)
			return;
		double my_x0=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("y0"))==null)
			return;
		double my_y0=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("x1"))==null)
			return;
		double my_x1=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("y1"))==null)
			return;
		double my_y1=Double.parseDouble(str);
		
		if(my_x0>my_x1) 
			{double p=my_x0;my_x0=my_x1;my_x1=p;}
		if(my_y0>my_y1) 
			{double p=my_y0;my_y0=my_y1;my_y1=p;}
		
		render_target my_target=ci.display_camera_result.target;
		box view_volume_box=my_target.view_volume_box;
		point center=view_volume_box.center();
		point diff=view_volume_box.p[1].sub(center);
		view_volume_box=new box(
			center.x+diff.x*my_x0,	center.y+diff.y*my_y0,	view_volume_box.p[0].z,
			center.x+diff.x*my_x1,	center.y+diff.y*my_y1,	view_volume_box.p[1].z);
		render_target_parameter my_target_parameter=render_target_parameter.
				create_selection_parameter(((function_id%2)==0)?true:false);
		render_target cam_target=new render_target(-1,
				my_target_parameter,null,comp.component_id,driver_id,0,
				new component[]{sk.component_cont.root_component},
				my_target.camera_id,my_target.parameter_channel_id,null,
				view_volume_box,ci.clip_plane,null);
		camera_result camera_result=new camera_result(
				ci.display_camera_result.cam,cam_target,sk.component_cont);
		var collector=new list_component_on_collector(sk,ci,camera_result).collector;
		if(collector==null)
			return;
		if(collector.component_number<=0)
			return;
		switch(function_id){
		default:
			return;
		case 0:
		case 1:
			new component_selection(sk).set_collector_selected(collector);
			break;
		case 2:
		case 3:
			component_array comp_cont;
			if((comp_cont=collector.get_component_array())==null)
				break;
			box my_box;
			if((my_box=comp_cont.get_box())==null)
				break;
			double my_scale_value=ci.display_camera_result.cam.parameter.scale_value;
			locate_camera lc=new locate_camera(ci.display_camera_result.cam);
			lc.locate_on_box_and_scale(sk.modifier_cont[modifier_container_id],
					my_box,null,my_scale_value,true,true,true);
			break;
		}
		if(control_code==0)
			return;
		sk.collector_stack.push_collector(true,
			sk.system_par,sk.scene_par,collector,
			sk.component_cont,sk.render_cont.renders);
		collector.title=Long.toString(collector.list_id);
		return;
	}
	private void select_single_component(int control_code,scene_kernel sk,client_information ci)
	{
		String function_str;
		if((function_str=ci.request_response.get_parameter("function"))==null)
			return;

		switch(Integer.decode(function_str)){
		default:
			break;
		case 0:
		case 1:
			component_selection cs=new component_selection(sk);
			if(ci.parameter.comp==null)
				cs.clear_selected_flag(sk.component_cont.root_component);
			else
				cs.switch_selected_flag(ci.parameter.comp);
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
		if(ci.parameter.comp.children.size()>0)
			return;
		if(ci.parameter.comp.driver_array.size()<=0)
			return;
		do{
			component comp;
			if((comp=sk.collector_stack.get_top_collector_onlyone_component())!=null)
				if(comp.component_id==ci.parameter.comp.component_id)
					break;
			sk.collector_stack.push_component_component(true,sk.system_par,
					sk.scene_par,ci.parameter.comp,sk.component_cont,sk.render_cont.renders);
		}while(false);
		
		component_driver cd;
		if((cd=sk.component_cont.get_component(audio_component_id).driver_array.get(0))==null)
			return;
		if(!(cd instanceof driver_audio_player.extended_component_driver))
			return;
			
		var acd=(driver_audio_player.extended_component_driver)cd;
		acd.set_audio(null,false);
		
		component_collector collector;
		if((collector=sk.collector_stack.get_top_collector())!=null)
			if(collector.audio_file_name!=null)
				acd.set_audio(collector.audio_file_name,true);
		return;
	}
	private void view_scale(int control_code,scene_kernel sk,client_information ci)
	{
		String str;

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
			try {
				control_code=Integer.decode(str);
			}catch(Exception e) {
				return null;
			}
		switch(((str=ci.request_response.get_parameter("operation"))==null)?"":str){
		default:
			break;
		case "single":
			select_single_component(control_code,sk,ci);
			break;
		case "many":
			select_many_component(control_code,sk,ci);
			break;
		case "scale":
			view_scale(control_code,sk,ci);
			break;
		}
		return null;
	}
}
