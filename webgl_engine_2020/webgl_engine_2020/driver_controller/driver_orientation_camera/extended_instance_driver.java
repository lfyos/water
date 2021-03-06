package driver_orientation_camera;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class extended_instance_driver extends instance_driver
{
	private boolean turn_on_flag,orientation_camera_type;
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id,
			boolean my_turn_on_flag,boolean my_orientation_camera_type)
	{
		super(my_comp,my_driver_id);
		turn_on_flag=my_turn_on_flag;
		orientation_camera_type=my_orientation_camera_type;
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
		ci.request_response.print(turn_on_flag?"[true":"[false",orientation_camera_type?",true]":",false]");
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		String str=ci.request_response.get_parameter("onoff");
		switch((str==null)?"":str) {
		default:
			return null;
		case "on":
		case "yes":
		case "true":
			turn_on_flag=true;
			break;
		case "off":	
		case "no":	
		case "false":	
			turn_on_flag=false;
			break;
		}
		str=ci.request_response.get_parameter("type");
		switch((str==null)?"":str) {
		default:
			break;
		case "on":
		case "yes":
		case "true":
			orientation_camera_type=true;
			break;
		case "off":	
		case "no":	
		case "false":	
			orientation_camera_type=false;
			break;
		}
		update_component_parameter_version(0);
		return null;
	}
}
