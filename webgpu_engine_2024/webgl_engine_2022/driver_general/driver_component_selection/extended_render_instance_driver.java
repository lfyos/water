package driver_component_selection;

import kernel_render.render;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
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
	public void response_init_render_data(render r,scene_kernel sk,client_information ci)
	{	
	}
	public String[] response_render_event(render r,scene_kernel sk,client_information ci)
	{
		return super.response_render_event(r,sk,ci);
	}
}