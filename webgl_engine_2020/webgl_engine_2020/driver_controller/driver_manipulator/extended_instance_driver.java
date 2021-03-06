package driver_manipulator;

import kernel_camera.camera_result;
import kernel_common_class.change_name;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class extended_instance_driver extends instance_driver
{
	private change_name language_change_name;
	private int audio_component_id,camera_modifier_id;
	private long touch_time_length;
	private boolean save_component_name_or_id_flag;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id,
			change_name my_language_change_name,int my_audio_component_id,
			int my_camera_modifier_id,long my_touch_time_length,
			boolean my_save_component_name_or_id_flag)
	{
		super(my_comp,my_driver_id);
		language_change_name=my_language_change_name;
		audio_component_id=my_audio_component_id;
		camera_modifier_id=my_camera_modifier_id;
		touch_time_length=my_touch_time_length;
		save_component_name_or_id_flag=my_save_component_name_or_id_flag;
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
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
		ci.request_response.print(comp.component_id);
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
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
		case "transparency":
			transparent_component.do_transparency(ek, ci);
			break;
		case "explosion":
			operate_component_explosion.do_explosion(camera_modifier_id,touch_time_length,ek, ci);
			break;
		case "display_value":
			operate_display_value.set_display_value_request(ek, ci);
			break;
		case "show_hide_component":
			operate_show_hide_component.show_hide_component_request(parameter_channel_id,ek, ci);
			break;
		case "show_hide_parameter":
			operate_show_hide_parameter.show_hide_parameter_request(parameter_channel_id,ek, ci);
			break;
		case "lod":
			operate_lod_scale.lod_scale_request(ek, ci);
			break;
		case "fix_driver":
			operate_fix_render_driver_id.fix_render_driver_id_request(ek, ci);
			break;
		case "render_assemble":
			operate_display_assembly_flag.display_assembly_flag_request(ek, ci);
			break;
		case "selection":
			operate_selection.selection_request(parameter_channel_id,ek, ci);
			break;
		case "part_list":
			if((my_comp=ek.component_cont.get_component(audio_component_id))!=null)
				if(my_comp.driver_number()>0)
					if(my_comp.driver_array[0] instanceof driver_audio.extended_component_driver) {
						operate_part_list.part_list_request(save_component_name_or_id_flag,
							comp,ek,ci,camera_modifier_id,language_change_name,
							(driver_audio.extended_component_driver)(my_comp.driver_array[0]));
						return null;
					}
			break;
		case "clip":
			operate_clip_plane.clip_plane_request(camera_modifier_id,ek, ci);
			break;
		}
		return null;
	}
}
