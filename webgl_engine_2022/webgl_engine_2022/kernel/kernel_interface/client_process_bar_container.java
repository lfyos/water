package kernel_interface;

import kernel_common_class.nanosecond_timer;

public class client_process_bar_container
{
	private client_process_bar process_bar_array[],default_process_bar;
	private long max_time_length;
	
	public void destroy()
	{
		for(int i=0,ni=process_bar_array.length;i<ni;i++)
			process_bar_array[i]=null;
		process_bar_array=null;
		default_process_bar=null;
	}
	public client_process_bar get_process_bar(int process_bar_id)
	{
		if((process_bar_id>=0)&&(process_bar_id<process_bar_array.length))
			if(process_bar_array[process_bar_id]!=null) {
				process_bar_array[process_bar_id].touch_time=nanosecond_timer.absolute_nanoseconds();
				return process_bar_array[process_bar_id];
			}
		default_process_bar.touch_time=nanosecond_timer.absolute_nanoseconds();
		return default_process_bar;
	};
	synchronized public int request_process_bar()
	{
		long t=nanosecond_timer.absolute_nanoseconds();
		for(int i=0,ni=process_bar_array.length;i<ni;i++) {
			if(process_bar_array[i]!=null)
				if((t-process_bar_array[i].touch_time)<max_time_length)
					continue;
			process_bar_array[i]=new client_process_bar(i,t);

			return i;
		}
		client_process_bar bak[]=process_bar_array;
		process_bar_array=new client_process_bar[bak.length+1];
		for(int i=0,ni=bak.length;i<ni;i++)
			process_bar_array[i]=bak[i];
		process_bar_array[bak.length]=new client_process_bar(bak.length,t);

		return bak.length;
	};
	public client_process_bar_container(long my_max_time_length)
	{
		default_process_bar	=new client_process_bar(-1,0);
		process_bar_array	=new client_process_bar[0];
		max_time_length=my_max_time_length;
	}
}