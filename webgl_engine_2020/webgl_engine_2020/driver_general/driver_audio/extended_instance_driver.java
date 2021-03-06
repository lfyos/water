package driver_audio;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class extended_instance_driver extends instance_driver
{
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		return cr.target.main_display_target_flag?false:true;
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
		ci.request_response.print(",",driver_id);
		ci.request_response.print("]");
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		String str;
		
		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;
		
		extended_component_driver acd=(extended_component_driver)(comp.driver_array[driver_id]);
		
		switch(str) {
		case "audio":
			return new String[] {acd.get_audio_file_name(),null};
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
			ci.request_response.print(acd.get_state()?"true":"false");
			break;
		case "play":
			if((str=ci.request_response.get_parameter("file"))!=null)
				acd.set_audio(comp.component_directory_name+str);
		default:
			break;
		}
		return null;
	}
}
