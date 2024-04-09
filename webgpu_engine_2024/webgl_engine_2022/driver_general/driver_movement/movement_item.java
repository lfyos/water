package driver_movement;

import kernel_transformation.location;

public class movement_item 
{
	public String start_parameter[],terminate_parameter[];
	public location start_location,terminate_location;
	public long start_time,terminate_time;
	public double time_length;
	
	public void destroy()
	{
		start_parameter=null;
		terminate_parameter=null;
		start_location=null;
		terminate_location=null;
	}
	
	public void reverse()
	{
		location bak=start_location;
		start_location=terminate_location;
		terminate_location=bak;
		
		String p_s[]=start_parameter;
		String p_t[]=terminate_parameter;
		
		start_parameter=p_t;
		terminate_parameter=p_s;
	}
	
	public movement_item(double my_time_length,
			String my_start_parameter[],	location my_start_location,
			String my_terminate_parameter[],location my_terminate_location)
	{
		time_length			=my_time_length;
		start_parameter		=my_start_parameter;
		start_location		=my_start_location;
		terminate_parameter	=my_terminate_parameter;
		terminate_location	=my_terminate_location;
		start_time			=0;
		terminate_time		=(long)my_time_length;
	}
}
