package driver_component_selection;

import kernel_camera.camera_result;
import kernel_camera.locate_camera;
import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_array;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_component.component_selection;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_render.list_component_on_collector;
import kernel_render.render_target;
import kernel_transformation.box;
import kernel_transformation.point;


public class extended_instance_driver extends instance_driver
{
	private int screen_rectangle_component_id,audio_component_id,camera_modifier_id;
	private boolean change_type_flag;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(
			component my_comp,int my_driver_id,
			int my_screen_rectangle_component_id,int my_audio_component_id,int my_camera_modifier_id)
	{
		super(my_comp,my_driver_id);
		screen_rectangle_component_id=my_screen_rectangle_component_id;
		audio_component_id=my_audio_component_id;
		camera_modifier_id=my_camera_modifier_id;
		change_type_flag=true;
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
		
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if(cr.target.main_display_target_flag){
			if(ci.display_camera_result!=null)
				if(ci.display_camera_result.cam.parameter.change_type_flag^change_type_flag){
					change_type_flag=change_type_flag?false:true;
					update_component_parameter_version(0);
				}
			return false;
		}
		return true;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[",comp.component_id);
		ci.request_response.print(",",screen_rectangle_component_id);
		ci.request_response.print(",",change_type_flag?1:0);
		ci.request_response.print("]");
	}

	private void select_many_component(component comp,int parameter_channel_id,int control_code,
			engine_kernel ek,client_information ci)
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
		
		box view_volume_box=ci.display_camera_result.target.view_volume_box;
		point center=view_volume_box.center(),diff=view_volume_box.p[1].sub(center);
		view_volume_box=new box(
				center.x+diff.x*my_x0,center.y+diff.y*my_y0,view_volume_box.p[0].z,
				center.x+diff.x*my_x1,center.y+diff.y*my_y1,view_volume_box.p[1].z);
		render_target cam_target=new render_target(comp.component_name,
				ci.display_camera_result.target.camera_id,parameter_channel_id,
				new component[]{ek.component_cont.root_component},null,ci.clip_plane,0,0,1,
				view_volume_box,null);
	
		if(cam_target.view_volume_box.distance2()<const_value.min_value2)
			return ;
		
		camera_result cam_result=new camera_result(ci.display_camera_result.cam,cam_target,ek.component_cont);
		component_collector collector=(new list_component_on_collector(
				cam_result.get_render_buffer_id(ci),parameter_channel_id,false,false,false,
				((function_id%2)==0)?true:false,false,ek,ci,cam_result)).collector;
		
		if(collector.component_number<=0)
			return;

		switch(function_id){
		default:
			return;
		case 0:
		case 1:
			component_selection cs=new component_selection(ek); 
			for(int i=0,ni=collector.component_collector.length;i<ni;i++)
				if(collector.component_collector[i]!=null)
					for(int j=0,nj=collector.component_collector[i].length;j<nj;j++)
						for(component_link_list p=collector.component_collector[i][j];p!=null;p=p.next_list_item)
							cs.set_selected_flag(p.comp,ek.component_cont);
			break;
		case 2:
		case 3:
			component_array comp_cont;
			box my_box;
			if((comp_cont=collector.get_component_array())!=null)
				if((my_box=comp_cont.get_box())!=null)
					(new locate_camera(ci.display_camera_result.cam)).locate_on_components(
							ek.modifier_cont[camera_modifier_id],my_box,null,
							ci.display_camera_result.cam.parameter.scale_value,
							ci.parameter.aspect,true,true,true);
			break;
		}
		if(control_code==0) 
			return;
		
		ek.collector_stack.push_collector(ek.system_par,ek.scene_par,
				collector,ek.component_cont,ek.render_cont.renders);
		collector.title=Long.toString(collector.list_id);
		
		return;
	}
	private void select_single_component(component comp,int control_code,engine_kernel ek,client_information ci)
	{
		String str;

		if((str=ci.request_response.get_parameter("function"))==null)
			return;
		
		driver_audio.extended_component_driver acd=(driver_audio.extended_component_driver)
				(ek.component_cont.get_component(audio_component_id).driver_array[0]);
		
		switch(Integer.decode(str)){
		default:
			break;
		case 0:
		case 1:
			if(audio_component_id<0)
				break;
			if(acd!=null)
				acd.set_audio(null);
			component_selection cs=new component_selection(ek);
			
			if(ci.parameter.comp==null)
				cs.clear_selected_flag(ek.component_cont);
			else
				cs.switch_selected_flag(ci.parameter.comp,ek.component_cont);
			break;
		case 2:
		case 3:
			(new locate_camera(ci.display_camera_result.cam)).locate_on_components(
					ek.modifier_cont[camera_modifier_id],
					ek.component_cont,ci.display_camera_result,ci.parameter,null,
					ci.display_camera_result.cam.parameter.scale_value,ci.parameter.aspect,
					ek.modifier_cont[camera_modifier_id].get_timer().get_current_time(),
					true,true,true,null,null);
			break;
		}
		if(control_code==0)
			return;
		if(ci.parameter.comp.children_number()>0)
			return;
		if(ci.parameter.comp.driver_number()<=0)
			return;

		component_array comp_array=new component_array(1);
		component_collector collector=ek.collector_stack.get_top_collector();
		
		if(collector!=null)
			if(collector.component_number==1) {
				comp_array.add_collector(collector);
				if(comp_array.comp[0].component_id==ci.parameter.comp.component_id){
					if(acd!=null)
						acd.set_audio(collector.audio_file_name);
					return;
				}
			}
		comp_array.clear_compoment();
		comp_array.add_component(ci.parameter.comp);
		ek.collector_stack.push_component_array(true,ek.system_par,
			ek.scene_par,comp_array,ek.component_cont,ek.render_cont.renders);
		if((collector=ek.collector_stack.get_top_collector())!=null)							
			if(acd!=null)
				acd.set_audio(collector.audio_file_name);
		return;
	}
	private void view_scale(component comp,int control_code,engine_kernel ek,client_information ci)
	{
		String str;
		
		if(ci.display_camera_result.target==null)
			return;
		if((str=ci.request_response.get_parameter("distance"))!=null)
			ci.display_camera_result.cam.parameter.distance=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("half_fovy_tanl"))!=null)
			ci.display_camera_result.cam.parameter.half_fovy_tanl=Double.parseDouble(str);
		ci.render_buffer.cam_buffer.synchronize_camera_buffer(
				ek.camera_cont.camera_array,ci.display_camera_result.target.camera_id);
		return;
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
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
			select_single_component(comp,control_code,ek,ci);
			break;
		case "many":
			select_many_component(comp,parameter_channel_id,control_code,ek,ci);
			break;
		case "scale":
			view_scale(comp,control_code,ek,ci);
			break;
		}
		return null;
	}
}
