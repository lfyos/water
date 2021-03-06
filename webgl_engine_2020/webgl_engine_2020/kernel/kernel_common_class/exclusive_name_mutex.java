package kernel_common_class;

import java.util.concurrent.locks.ReentrantLock;

public class exclusive_name_mutex 
{
	volatile private int name_number;
	volatile private String name_array[];
	
	private ReentrantLock mutex_lock;

	public void lock(String my_name)
	{
		mutex_lock.lock();
		
		long sleep_time_length=4,max_time_length=2000;
		for(int begin_pointer=0,end_pointer=name_number-1;begin_pointer<=end_pointer;) {
			int middle_pointer=(begin_pointer+end_pointer)/2;
			int cmp_result=my_name.compareTo(name_array[middle_pointer]);
			if(cmp_result<0)
				end_pointer=middle_pointer-1;
			else if(cmp_result>0)
				begin_pointer=middle_pointer+1;
			else{
				mutex_lock.unlock();
				
				if((sleep_time_length=(long)Math.round(1.2*sleep_time_length))>max_time_length)
					sleep_time_length=max_time_length;
				long this_sleep_time_length=(long)(Math.round(Math.random()*sleep_time_length));
				this_sleep_time_length=(this_sleep_time_length<=0)?1:this_sleep_time_length;
				try {
					Thread.sleep(this_sleep_time_length);
				}catch(Exception e) {
					debug_information.println("exclusive_name_mutex sleep exception:	",e.toString());
					e.printStackTrace();
					Thread.yield();
				}
				
				mutex_lock.lock();
				
				begin_pointer=0;
				end_pointer=name_number-1;
			}
		}
		if(name_number>=name_array.length) {
			String bak[]=name_array;
			name_array=new String[bak.length+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				name_array[i]=bak[i];
		}
		name_array[name_number++]=new String(my_name);
		for(int i=name_number-1;i>0;i--) {
			if(name_array[i-1].compareTo(name_array[i])<=0)
				break;
			String bak=name_array[i-1];
			name_array[i-1]=name_array[i];
			name_array[i  ]=bak;
		}
		mutex_lock.unlock();
		
		return;
	}
	public void unlock(String my_name)
	{
		mutex_lock.lock();
		
		for(int begin_pointer=0,end_pointer=name_number-1;begin_pointer<=end_pointer;) {
			int middle_pointer=(begin_pointer+end_pointer)/2;
			int cmp_result=my_name.compareTo(name_array[middle_pointer]);
			if(cmp_result<0)
				end_pointer=middle_pointer-1;
			else if(cmp_result>0)
				begin_pointer=middle_pointer+1;
			else{
				for(int i=middle_pointer;i<(name_number-1);i++)
					name_array[i]=name_array[i+1];
				name_array[--name_number]=null;
				break;
			}
		}
		
		mutex_lock.unlock();
		
		return;
	}
	public exclusive_name_mutex()
	{
		name_number	=0;
		name_array	=new String[0];
		mutex_lock	=new ReentrantLock();
	}
}
