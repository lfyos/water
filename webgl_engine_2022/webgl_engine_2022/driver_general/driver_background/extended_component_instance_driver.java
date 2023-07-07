package driver_background;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_common_class.jason_string;
import kernel_engine.engine_kernel;
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
		extended_component_driver ecd=(extended_component_driver)(comp.driver_array.get(driver_id));
		return (cr.target.parameter_channel_id!=ecd.user_parameter_channel_id);
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{	
		extended_component_driver ecd=(extended_component_driver)(comp.driver_array.get(driver_id));
		ci.request_response.print("[",ecd.mode).print(",",jason_string.change_string(ecd.directory_name)).print("]");
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;

		extended_component_driver ecd=(extended_component_driver)(comp.driver_array.get(driver_id));
		
		switch(str) {
		case "mode":
			if((str=ci.request_response.get_parameter("mode"))==null)
				break;
			int new_mode=Integer.decode(str);
			if(ecd.mode!=new_mode) {
				ecd.mode=new_mode;
				ecd.update_component_parameter_version();
			}
			break;
		case "directory":
			if((str=ci.request_response.get_parameter("directory"))==null)
				break;
			if(ecd.directory_name.compareTo(str)!=0) {
				ecd.directory_name=str;
				ecd.update_component_parameter_version();
			}
			break;
		}
		return null;
	}
}