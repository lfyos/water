package kernel_interface;

import java.util.ArrayList;

import kernel_common_class.nanosecond_timer;

public class client_process_bar_container
{
	private ArrayList<client_process_bar> process_bar_array;
	private client_process_bar default_process_bar;
	private long max_time_length;
	
	public void destroy()
	{
		if(process_bar_array!=null) {
			for(int i=0,ni=process_bar_array.size();i<ni;i++)
				process_bar_array.set(i,null);
			process_bar_array=null;
		}
		default_process_bar=null;
	}
	public client_process_bar get_process_bar(int process_bar_id)
	{
		client_process_bar ret_val;
		if((process_bar_id<0)||(process_bar_id>=process_bar_array.size()))
			ret_val=default_process_bar;
		else
			ret_val=process_bar_array.get(process_bar_id);
		ret_val.touch_time=nanosecond_timer.absolute_nanoseconds();
		return ret_val;
	};
	synchronized public int request_process_bar()
	{
		long current_time=nanosecond_timer.absolute_nanoseconds();
		int process_bar_id,process_bar_number=process_bar_array.size();
		for(process_bar_id=0;process_bar_id<process_bar_number;process_bar_id++) {
			if((current_time-process_bar_array.get(process_bar_id).touch_time)>max_time_length) {
				process_bar_array.set(process_bar_id,new client_process_bar(process_bar_id,current_time));
				return process_bar_id;
			}
		}
		process_bar_array.add(process_bar_number,new client_process_bar(process_bar_number,current_time));
		return process_bar_number;
	};
	public client_process_bar_container(long my_max_time_length)
	{
		default_process_bar	=new client_process_bar(-1,0);
		process_bar_array	=new ArrayList<client_process_bar>();
		max_time_length=my_max_time_length;
	}
}