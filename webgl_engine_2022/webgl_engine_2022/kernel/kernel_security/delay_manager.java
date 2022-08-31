package kernel_security;

import kernel_common_class.nanosecond_timer;
import kernel_file_manager.file_reader;

public class delay_manager 
{
	private long max_time_length,time_increase_step,last_touch_time;
	private long delay_time_length_array[];
		
	public delay_manager(file_reader f)
	{
		long my_delay_time_length[]=new long[10000];

		time_increase_step	=f.get_long();
		long max_time_step	=f.get_long();
		max_time_length		=max_time_step*time_increase_step;
		last_touch_time		=0;
		
		for(int i=0;i<my_delay_time_length.length;i++){
			if((my_delay_time_length[i]=f.get_long())<0)
				my_delay_time_length[i]=0;
			if(f.eof()){
				f.close();
				delay_time_length_array=new long[i];
				
				for(int j=0;j<i;j++)
					delay_time_length_array[j]=my_delay_time_length[j];

				return;
			}
		}
		delay_time_length_array=my_delay_time_length;
		return;
	}
	public long process_delay_time_length()
	{
		last_touch_time+=time_increase_step;
		
		long my_current_time=nanosecond_timer.absolute_nanoseconds();
		long time_distance=my_current_time-last_touch_time;
		double delay_position=((double)time_distance)/((double)max_time_length);
		int deley_index=(int)((double)(delay_time_length_array.length)*delay_position);

		if(deley_index<0)
			deley_index=0;
		if(deley_index>=delay_time_length_array.length){
			deley_index=delay_time_length_array.length-1;
			last_touch_time=my_current_time-max_time_length;
		}
		return delay_time_length_array[deley_index];
	}
}
