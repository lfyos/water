package kernel_common_class;

public class nanosecond_timer
{
	private long current_time;
	
	static public long absolute_nanoseconds()
	{
		return System.nanoTime();
	}
	public long nanoseconds()
	{
		return current_time;
	}
	public void refresh_timer()
	{
		current_time=absolute_nanoseconds();
	}
	public nanosecond_timer()
	{
		current_time=absolute_nanoseconds();
	}
}