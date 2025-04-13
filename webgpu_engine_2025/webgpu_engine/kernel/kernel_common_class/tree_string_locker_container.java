package kernel_common_class;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

class locker_container
{
	public int number;
	public ReentrantLock locker;
	
	public locker_container()
	{
		number=0;
		locker=new ReentrantLock();
	}
}
public class tree_string_locker_container extends tree_string_search_container<locker_container>
{
	synchronized private ReentrantLock get_locker(String locker_name[],boolean do_lock_flag)
	{
		ArrayList<locker_container> list;
		locker_container p;
		if(do_lock_flag) {
			for(list=add(locker_name,null);list.size()>1;)
				list.remove(1);
			if((p=list.get(0))==null)
				list.set(0,p=new locker_container());
			p.number++;
		}else {
			if((list=search(locker_name))==null)
				return null;
			if(list.size()<=0) {
				remove(locker_name);
				return null;
			}
			p=list.get(0);
			if((p.number--)<=1)
				remove(locker_name);
		}
		return p.locker;
	}
	public void lock(String locker_name[])
	{
		get_locker(locker_name,true).lock();
	}
	public void unlock(String locker_name[])
	{
		ReentrantLock p;
		if((p=get_locker(locker_name,false))!=null)
			p.unlock();
	}
}
