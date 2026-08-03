package kernel_security;

import java.util.ArrayList;

import kernel_file_manager.file_reader;
import kernel_common_class.nanosecond_timer;

public class delay_manager 
{
	private long time_increase_step,max_time_length,last_touch_time;
	private long delay_time_length_array[];

	public delay_manager(file_reader f)
	{
		time_increase_step	=f.get_long();
		long max_time_step	=f.get_long();
		max_time_length		=max_time_step*time_increase_step;
		last_touch_time		=0;

		ArrayList<Long> list=new ArrayList<Long>();
		for(long my_delay_time_length;!(f.eof());) 
			if((my_delay_time_length=f.get_long())>0)
				if(!(f.error_flag()))
					list.add(my_delay_time_length);
		delay_time_length_array=new long[list.size()];
		for(int i=0,n=delay_time_length_array.length;i<n;i++)
			delay_time_length_array[i]=list.get(i);
	}
	public long process_delay_time_length()
	{
		last_touch_time+=time_increase_step;
		
		long my_current_time=nanosecond_timer.absolute_nanoseconds();
		long time_distance=my_current_time-last_touch_time;
		double delay_position=((double)time_distance)/((double)max_time_length);
		
		int list_length=delay_time_length_array.length;
		int deley_index=(int)((double)list_length*delay_position);

		if(deley_index<0)
			deley_index=0;
		if(deley_index>=list_length){
			deley_index=list_length-1;
			last_touch_time=my_current_time-max_time_length;
		}
		return delay_time_length_array[deley_index];
	}
}
