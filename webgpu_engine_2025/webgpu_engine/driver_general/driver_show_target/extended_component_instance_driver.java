package driver_show_target;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_scene.client_information;
import kernel_driver.component_instance_driver;

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
		ci.request_response.	
			print("[",position[0]).print(",",position[1]).
			print(",",position[2]).print(",",position[3]).
			print("]");
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		return false;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print(0);
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		return null;
	}
}
