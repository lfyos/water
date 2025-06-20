package driver_audio_player;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_file_manager.file_reader;
import kernel_scene.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		return cr.target.main_display_target_flag?false:true;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print("0");
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print("0");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		String str;
		
		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;
		
		extended_component_driver acd=(extended_component_driver)(comp.driver_array.get(driver_id));
		
		switch(str){
		case "audio":
			if((str=acd.get_audio_file_name())!=null)
				return new String[] {file_reader.separator(str),null};
			break;
		case "ended":
			acd.mark_terminate_flag();
			break;
		case "turn_on":
			acd.turn_on_off(true);
			break;
		case "turn_off":
			acd.turn_on_off(false);
			break;
		case "state":
			ci.request_response.print(acd.get_on_off_flag()?"true":"false");
			break;
		default:
			break;
		}
		return null;
	}
}
