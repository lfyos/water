package driver_movement;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;


public class extended_instance_driver extends instance_driver
{
	private movement_manager m;

	public void destroy()
	{
		super.destroy();
		m=null;
	}
	public extended_instance_driver(component my_comp,int my_driver_id,movement_manager my_m)
	{
		super(my_comp,my_driver_id);
		m=my_m;
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if(ci.display_camera_result.target==null)
			return true;
		if(cr.target.main_display_target_flag)
			return false;
		else
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
		return (new movement_function_switch(ek,ci,m)).get_engine_result();
	}
}
