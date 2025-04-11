package kernel_common_class;

import java.util.concurrent.locks.ReentrantLock;

public class tree_string_locker_container extends tree_string_search_container<ReentrantLock>
{
	synchronized private void lock_routine(String name[],String operation_flag)
	{
		ReentrantLock my_lock;
		if((my_lock=search(name))==null) {
			my_lock=new ReentrantLock();
			add(name,my_lock);
		}
		try {
			switch(operation_flag) {
			case "lock":
				my_lock.lock();
				break;
			case "unlock":
				my_lock.unlock();
				break;
			}
		}catch(Exception e) {
			;
		}
	}
	public void lock(String name[])
	{
		lock_routine(name,"lock");
	}
	public void unlock(String name[])
	{
		lock_routine(name,"unlock");
	}
}
