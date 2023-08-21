package driver_manipulator;

import kernel_part.part;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private long touch_time_length;
	private boolean save_component_name_or_id_flag;
	private int audio_component_id,modifier_container_id;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,engine_kernel ek)
	{
		super(my_comp,my_driver_id);
		
		part p=comp.driver_array.get(driver_id).component_part;
		file_reader f=new file_reader(p.directory_name+p.material_file_name,p.file_charset);
		String audio_component_name		=f.get_string();
		modifier_container_id			=f.get_int();
		touch_time_length				=f.get_long();
		save_component_name_or_id_flag	=f.get_boolean();
		f.close();
		
		audio_component_id=-1;
		component audio_comp=ek.component_cont.search_component(audio_component_name);
		if(audio_comp!=null)
			if(audio_comp.driver_number()>0)
				if(audio_comp.driver_array.get(0) instanceof driver_audio_player.extended_component_driver)
					audio_component_id=audio_comp.component_id;
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		return false;
	}
	public void create_render_parameter(int render_buffer_id,
			int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{
		ci.request_response.print(0);
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str;
		component my_comp;
		
		if((str=ci.request_response.get_parameter("event_method"))==null)
			return null;
		
		switch(str.toLowerCase()){
		default:
			break;
		case "check_component":
			check_component.check(ek,ci);
			break;
		case "last_component":
			ci.request_response.println(
				((my_comp=ek.component_cont.search_component())==null)?-1:(my_comp.component_id));
			break;
		case "transparency":
			operate_component_transparent.do_transparency(ek, ci);
			break;
		case "explosion":
			ci.request_response.print(ci.display_camera_result.cam.parameter.switch_time_length);
			operate_component_explosion.do_explosion(modifier_container_id,touch_time_length,ek, ci);
			break;
		case "display_value":
			operate_display_value.set_display_value_request(ek, ci);
			break;
		case "show_hide_component":
			operate_show_hide_component.show_hide_component_request(
				ci.display_camera_result.target.parameter_channel_id,ek, ci);
			break;
		case "show_hide_parameter":
			operate_show_hide_parameter.show_hide_parameter_request(
					ci.display_camera_result.target.parameter_channel_id,ek, ci);
			break;
		case "lod":
			operate_lod_scale.lod_scale_request(ek, ci);
			break;
		case "fix_driver":
			operate_fix_render_driver_id.fix_render_driver_id_request(ek, ci);
			break;
		case "render_assemble":
			operate_display_assembly_flag.set_clear_display_assembly_flag_request(ek, ci);
			break;
		case "selection":
			operate_selection.selection_request(
					ci.display_camera_result.target.parameter_channel_id,ek, ci);
			break;
		case "part_list":
			if((my_comp=ek.component_cont.get_component(audio_component_id))==null)
				break;
			operate_part_list.part_list_request(
				modifier_container_id,save_component_name_or_id_flag,comp,ek,ci,
				(driver_audio_player.extended_component_driver)(my_comp.driver_array.get(0)));
			break;
		case "clip":
			operate_clip_plane.clip_plane_request(modifier_container_id,ek, ci);
			break;
		}
		return null;
	}
}
