package driver_show_target;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_driver.component_instance_driver;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class extended_component_instance_driver extends component_instance_driver
{
	private double position[];
	public void destroy()
	{
		super.destroy();
		position=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,double my_position[])
	{
		super(my_comp,my_driver_id);
		position=my_position;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		return false;
	}
	public void create_render_parameter(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print("[");
		for(int i=0,ni=position.length;i<ni;i++)
			ci.request_response.print((i<=0)?"":",",position[i]);
		ci.request_response.print("]");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		return null;
	}
}
