package driver_audio_player;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_common_class.debug_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_engine.client_information;
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
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		return cr.target.main_display_target_flag?false:true;
	}
	public void create_render_parameter(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print("0");
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("0");
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str;
		
		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;
		
		extended_component_driver acd=(extended_component_driver)(comp.driver_array.get(driver_id));
		
		switch(str){
		case "audio":
			str=acd.get_audio_file_name();
			str=(str==null)?null:file_reader.separator(str);
			return new String[] {str,null};
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
				if((str=str.trim()).length()>0){
					String request_charset=ci.request_response.implementor.get_request_charset();
					try {
						str=java.net.URLDecoder.decode(str,request_charset);
						str=java.net.URLDecoder.decode(str,request_charset);
					}catch(Exception e){
						acd.set_audio(null);
						debug_information.println("audio play operation fail:	",e.toString());
						break;
					}
					acd.set_audio(comp.component_directory_name+str);
					break;
				}
			acd.set_audio(null);
			debug_information.println("audio play file name is empty");
			break;
		default:
			break;
		}
		return null;
	}
}
