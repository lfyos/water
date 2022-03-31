package kernel_driver;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class modifier_driver
{
	private boolean clear_modifier_flag;
	public long start_time,terminate_time;
	
	public void set_clear_modifier_flag()
	{
		clear_modifier_flag=true;
	}
	public boolean get_clear_modifier_flag()
	{
		if(clear_modifier_flag) {
			clear_modifier_flag=false;
			return true;
		}
		return false;
	}
	public void destroy()
	{
	}
	public void modify(long my_current_time,engine_kernel ek,client_information ci)
	{
	}
	public void last_modify(long my_current_time,engine_kernel ek,client_information ci,boolean terminated_flag)
	{
	}
	public boolean can_start(long my_current_time,engine_kernel ek,client_information ci)
	{
		return true;
	}
	public modifier_driver(long my_start_time,long my_terminate_time)
	{
		clear_modifier_flag=false;
		start_time=my_start_time;
		terminate_time=my_terminate_time;
	}
}
