package kernel_driver;

import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class modifier_driver
{
	public long start_time,terminate_time;
	public void destroy()
	{
	}
	public void modify(long my_current_time,scene_kernel sk,client_information ci)
	{
	}
	public void last_modify(long my_current_time,scene_kernel sk,client_information ci,boolean terminated_flag)
	{
	}
	public boolean can_start(long my_current_time,scene_kernel sk,client_information ci)
	{
		return true;
	}
	public modifier_driver(long my_start_time,long my_terminate_time)
	{
		start_time=my_start_time;
		terminate_time=my_terminate_time;
	}
}
