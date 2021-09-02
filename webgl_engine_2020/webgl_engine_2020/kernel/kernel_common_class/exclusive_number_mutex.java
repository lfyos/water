package kernel_common_class;

public class exclusive_number_mutex 
{
	private volatile int lock_number_value;
	
	synchronized private int test_and_lock(int type_id,int max_lock_number)
	{
		switch(type_id) {
		case 0:
			return lock_number_value;
		case 1:
			return --lock_number_value;
		default:
			if(max_lock_number<1)
				max_lock_number=1;
			if(lock_number_value<max_lock_number)
				return ++lock_number_value;
			else
				return -1;
		}
	}
	public int get_lock_number()
	{
		return test_and_lock(0,0);
	}
	public int unlock_number()
	{
		return test_and_lock(1,0);
	}
	public int lock_number(int max_lock_number,String msg)
	{
		int ret_val;
		for(long sleep_time_length=50,max_time_length=2000;(ret_val=test_and_lock(2,max_lock_number))<0;){
			long this_sleep_time_length=(long)(Math.round(Math.random()*sleep_time_length));
			this_sleep_time_length=(this_sleep_time_length<10)?10:this_sleep_time_length;
			try{
				Thread.sleep(this_sleep_time_length);
			}catch(Exception e) {
				debug_information.println("lock_number:max_lock_number/"+max_lock_number,
						",sleep_time_length/"+this_sleep_time_length+"\t"+e.toString());
				e.printStackTrace();
				Thread.yield();
			}
			if((sleep_time_length=(long)(Math.round(1.5*sleep_time_length)))>max_time_length) {
				sleep_time_length=max_time_length;
				if(msg!=null)
					debug_information.println(this_sleep_time_length+":	"+msg);
			}
		}
		return ret_val;
	}
	public exclusive_number_mutex()
	{
		lock_number_value=0;
	}
}
