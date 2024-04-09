package driver_camera_operation;

import kernel_render.render;
import kernel_engine.engine_kernel;
import kernel_engine.client_information;
import kernel_driver.render_instance_driver;

public class extended_render_instance_driver extends render_instance_driver
{
	private double depth_start,depth_end;
	
	public extended_render_instance_driver(double my_depth_start,double my_depth_end)
	{
		super();
		
		depth_start	=my_depth_start;
		depth_end	=my_depth_end;
	}
	public void destroy()
	{
		super.destroy();
	}
	public void response_init_render_data(render r,engine_kernel ek,client_information ci)
	{	
		ci.request_response.print("[",depth_start).print(",",depth_end).print("]");
	}
	public String[] response_render_event(render r,engine_kernel ek,client_information ci)
	{
		return super.response_render_event(r,ek,ci);
	}
}