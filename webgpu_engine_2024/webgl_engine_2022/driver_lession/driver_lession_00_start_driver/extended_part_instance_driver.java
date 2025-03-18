package driver_lession_00_start_driver;

import kernel_driver.part_instance_driver;
import kernel_part.part;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class extended_part_instance_driver extends part_instance_driver
{
	public extended_part_instance_driver()
	{
		super();
	}
	public void destroy()
	{
		super.destroy();
	}
	public void response_init_part_data(part p,scene_kernel sk,client_information ci)
	{
	}
	public String[] response_part_event(part p,scene_kernel sk,client_information ci)
	{			
		return super.response_part_event(p,sk,ci);
	}
}