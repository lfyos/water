package kernel_engine;

import kernel_common_class.nanosecond_timer;

public class display_message
{
	private String display_message;
	private long set_display_message_time;
	
	public String get_display_message()
	{
		if(set_display_message_time>0)
			if(nanosecond_timer.absolute_nanoseconds()>set_display_message_time)
				return "";
		return (display_message==null)?"":display_message;
	}
	public void set_display_message(String new_display_message,long my_message_time_length)
	{
		if(my_message_time_length>0)
			set_display_message_time=nanosecond_timer.absolute_nanoseconds()+my_message_time_length;
		else
			set_display_message_time=-1;
		display_message=new_display_message;
	}
	public display_message()
	{
		display_message				="";
		set_display_message_time	=0;
	}
}
