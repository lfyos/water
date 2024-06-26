package kernel_interface;

import kernel_common_class.nanosecond_timer;

public class client_process_bar 
{
	public String process_title,ex_process_title;
	public long max_process,current_process,touch_time,original_time,start_time;
	public int process_bar_id;
	
	public client_process_bar(int my_process_bar_id,long my_time)
	{
		process_title	="";
		ex_process_title="";
		current_process	=0;
		max_process		=1;
		process_bar_id	=my_process_bar_id;
		touch_time		=my_time;
		original_time	=nanosecond_timer.absolute_nanoseconds();
		start_time		=original_time;
	}
	public void set_process_bar(boolean reset_time_flag,
			String my_process_title,String my_ex_process_title,
			long my_current_process,long my_max_process)
	{
		if(reset_time_flag)
			start_time	=nanosecond_timer.absolute_nanoseconds();
		process_title	=(my_process_title==null)		?"":my_process_title.trim();
		ex_process_title=(my_ex_process_title==null)	?"":my_ex_process_title.trim();
		max_process		=(my_max_process<1)?1:my_max_process;
		current_process	=(my_current_process<0)?0:(my_current_process>max_process)?max_process:my_current_process;
	}
}
