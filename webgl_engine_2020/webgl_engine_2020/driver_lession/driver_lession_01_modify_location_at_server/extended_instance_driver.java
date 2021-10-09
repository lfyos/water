package driver_lession_01_modify_location_at_server;

import kernel_camera.camera_result;
import kernel_common_class.nanosecond_timer;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.location;


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
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		double t=(double)(nanosecond_timer.absolute_nanoseconds());

		t/=1000.0;
		t/=1000.0;
		t/=1000.0;
		
		t-=Math.floor(t);
				
		comp.modify_location(location.move_rotate(0,0,2.0*t-1.0,0,0,0),ek.component_cont);
		
		return false;
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
		return null;
	}
}
