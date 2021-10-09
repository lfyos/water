package driver_ruler;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;


public class extended_instance_driver extends instance_driver
{
	private double region_data[];
	public void destroy()
	{
		super.destroy();
		region_data=null;
	}
	public extended_instance_driver(component my_comp,int my_driver_id,double my_region_data[])
	{
		super(my_comp,my_driver_id);
		region_data=my_region_data;
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		return ((ci.display_camera_result.target.target_id)!=(cr.target.target_id))?true:false;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(region_data);
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		return null;
	}
}
