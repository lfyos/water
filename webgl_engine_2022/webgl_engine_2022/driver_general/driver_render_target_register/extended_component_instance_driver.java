package driver_render_target_register;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_driver.component_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_render.render_target;

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
	private void register_target(engine_kernel ek,client_information ci)
	{
		ci.target_container.register_target(
			new render_target(
					true,	comp.component_id,	driver_id,	0,
					new component[] {ek.component_cont.root_component},null,
					0,	2,	null,	null,	null,	true,	true,	true));
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
		register_target(ek,ci);
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		return false;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{
		ci.request_response.print(0);
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		return null;
	}
}
