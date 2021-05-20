package kernel_common_class;

import java.util.concurrent.locks.ReentrantLock;

public class exclusive_name_mutex 
{
	public String name;
	private ReentrantLock mutex_lock;
	
	public exclusive_name_mutex(String my_name)
	{
		name=my_name;
		mutex_lock=new ReentrantLock();
	}
	public void lock()
	{
		mutex_lock.lock();
	}
	public void unlock()
	{
		mutex_lock.unlock();
	}
}