package kernel_driver;

import kernel_common_class.const_value;
import kernel_common_class.nanosecond_timer;

public class modifier_container_timer
{
	private long timer_adjust_value,last_current_time,timer_version;
	private  double speed;

	public long get_version()
	{
		return timer_version;
	}
	public long get_timer_adjust_value()
	{
		return timer_adjust_value;
	}
	public double get_speed()
	{
		return speed;
	}
	public void modify_speed(double new_speed,nanosecond_timer current_time)
	{
		timer_version++;
		if((speed=new_speed)<const_value.min_value)
			speed=const_value.min_value;
		timer_adjust_value=current_time.nanoseconds()-(long)(((double)last_current_time)/speed);
	}
	public void modify_current_time(long my_current_time,nanosecond_timer current_time)
	{
		timer_version++;
		timer_adjust_value=current_time.nanoseconds()-(long)(my_current_time/speed);
	}
	public long caculate_current_time(nanosecond_timer current_time)
	{
		return (long)((current_time.nanoseconds()-timer_adjust_value)*speed);
	}
	public long get_current_time()
	{
		return last_current_time;
	}
	public long mark_current_time(nanosecond_timer current_time)
	{
		last_current_time=caculate_current_time(current_time);
		return get_current_time();
	}
	public modifier_container_timer(long my_timer_adjust_value)
	{
		timer_adjust_value=my_timer_adjust_value;
		last_current_time=0;
		timer_version=1;
		speed=1.0;
	}
}
