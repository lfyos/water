package driver_lession_00_start_driver;

import kernel_render.render;
import kernel_engine.engine_kernel;
import kernel_engine.client_information;
import kernel_driver.render_instance_driver;

public class extended_render_instance_driver extends render_instance_driver
{
	public extended_render_instance_driver()
	{
		super();
	}
	public void destroy()
	{
		super.destroy();
	}
	public void response_init_render_data(render r,engine_kernel ek,client_information ci)
	{	
	}
	public String[] response_event(render r,engine_kernel ek,client_information ci)
	{
		return super.response_event(r,ek,ci);
	}
}