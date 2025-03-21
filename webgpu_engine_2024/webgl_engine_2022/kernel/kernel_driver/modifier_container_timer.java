package kernel_driver;

import kernel_common_class.nanosecond_timer;

public class modifier_container_timer
{
	private long timer_adjust_value,last_current_time;

	public long get_timer_adjust_value()
	{
		return timer_adjust_value;
	}
	public long get_current_time()
	{
		return last_current_time;
	}
	public long caculate_current_time(nanosecond_timer current_time)
	{
		return (last_current_time=current_time.nanoseconds()-timer_adjust_value);
	}
	public void modify_current_time(long my_current_time,nanosecond_timer current_time)
	{
		timer_adjust_value=current_time.nanoseconds()-my_current_time;
	}
	public modifier_container_timer(long my_timer_adjust_value)
	{
		timer_adjust_value=my_timer_adjust_value;
		last_current_time=0;
	}
}
