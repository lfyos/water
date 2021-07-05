package kernel_common_class;

import java.util.concurrent.locks.ReentrantLock;

public class exclusive_number_mutex 
{
	private volatile int lock_number_value;
	private ReentrantLock number_mutex_lock;
	
	public void destroy()
	{
		if(number_mutex_lock!=null) {
			number_mutex_lock.unlock();
			number_mutex_lock=null;
		}
	}
	public int unlock_number()
	{
		number_mutex_lock.lock();
		int ret_val=(--lock_number_value);
		number_mutex_lock.unlock();
		return ret_val;
	}
	public int lock_number(int max_lock_number)
	{
		for(long sleep_time_length=4,max_time_length=2000;;){
			number_mutex_lock.lock();
			if(lock_number_value<((max_lock_number<1)?1:max_lock_number)){
				int ret_val=(++lock_number_value);
				number_mutex_lock.unlock();
				return ret_val;
			}
			number_mutex_lock.unlock();
			if((sleep_time_length=(long)Math.round(1.2*sleep_time_length))>max_time_length)
				sleep_time_length=max_time_length;
			long this_sleep_time_length=(long)(Math.round(Math.random()*sleep_time_length));
			this_sleep_time_length=(this_sleep_time_length<=0)?1:this_sleep_time_length;
			try {
				Thread.sleep(this_sleep_time_length);
				continue;
			}catch(Exception e) {
				debug_information.println("lock_number:max_lock_number/"+max_lock_number,
						",sleep_time_length/"+this_sleep_time_length+"\t"+e.toString());
				e.printStackTrace();
				Thread.yield();
			}
		}
	}
	public int get_lock_number()
	{
		return lock_number_value;
	}
	public exclusive_number_mutex()
	{
		lock_number_value	=0;
		number_mutex_lock	=new ReentrantLock();
	}
}
