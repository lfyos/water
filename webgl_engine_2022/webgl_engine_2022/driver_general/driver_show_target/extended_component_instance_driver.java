package driver_show_target;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private double position[];
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,double my_position[])
	{
		super(my_comp,my_driver_id);
		position=my_position;
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
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[");
		for(int i=0,ni=position.length;i<ni;i++)
			ci.request_response.print((i<=0)?"":",",position[i]);
		ci.request_response.print("]");
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		return null;
	}
}
