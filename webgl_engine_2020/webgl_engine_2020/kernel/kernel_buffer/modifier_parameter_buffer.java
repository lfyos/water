package kernel_buffer;

public class modifier_parameter_buffer
{
	public long timer_version;
	public long timer_adjust_value;
	public double speed;
	
	public modifier_parameter_buffer(
			long my_timer_version,long my_timer_adjust_value,double my_speed)
	{
		timer_version		=my_timer_version;
		timer_adjust_value	=my_timer_adjust_value;
		speed				=my_speed;
	}
}
