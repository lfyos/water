package kernel_common_class;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class locker_container
{
	public int number;
	public ReentrantReadWriteLock locker;
	
	public locker_container()
	{
		number=0;
		locker=new ReentrantReadWriteLock();
	}
};

public class tree_string_locker_container extends tree_string_search_container<locker_container>
{
	synchronized private ReentrantReadWriteLock operate_locker_routine(
			boolean do_destroy_flag,String locker_name[],int modify_number)
	{
		if(do_destroy_flag) {
			for(ArrayList<locker_container> p;(p=first_value())!=null;) { 
				for(int index_id;(index_id=p.size()-1)>=0;) {
					locker_container lc=p.remove(index_id);
					while(lc.locker.getReadHoldCount()>0)
						lc.locker.readLock().unlock();
					while(lc.locker.getWriteHoldCount()>0)
						lc.locker.writeLock().unlock();
				}
				remove(first_key());
			}
			return null;
		}
		
		ArrayList<locker_container> list;
		locker_container p;
		
		if(modify_number>=0){
			int last_id;
			for(list=add(locker_name,null);(last_id=list.size()-1)>0;)
				list.remove(last_id);
			if((p=list.get(0))==null) {
				p=new locker_container();
				list.set(0,p);
			}
			p.number+=modify_number;
		}else{
			if((list=search(locker_name))==null)
				return null;
			if(list.size()<=0) {
				remove(locker_name);
				return null;
			}
			p=list.get(0);
			p.number+=modify_number;
			
			if(p.number<=0)
				remove(locker_name);
		}
		return p.locker;
	}
	public void destroy()
	{
		operate_locker_routine(true,null,0);
	}
	public void write_lock(String locker_name[])
	{
		ReentrantReadWriteLock p;
		if((p=operate_locker_routine(false,locker_name,1))!=null)
			p.writeLock().lock();
	}
	public void write_lock(String locker_name)
	{
		write_lock(new String[] {locker_name});
	}
	public void write_unlock(String locker_name[])
	{
		ReentrantReadWriteLock p;
		if((p=operate_locker_routine(false,locker_name,-1))!=null)
			p.writeLock().unlock();
	}
	public void write_unlock(String locker_name)
	{
		write_unlock(new String[] {locker_name});
	}
	public void read_lock(String locker_name[])
	{
		ReentrantReadWriteLock p;
		if((p=operate_locker_routine(false,locker_name,1))!=null)
			p.readLock().lock();
	}
	public void read_lock(String locker_name)
	{
		read_lock(new String[] {locker_name});
	}
	public void read_unlock(String locker_name[])
	{
		ReentrantReadWriteLock p;
		if((p=operate_locker_routine(false,locker_name,-1))!=null)
			p.readLock().unlock();
	}
	public void read_unlock(String locker_name)
	{
		read_unlock(new String[] {locker_name});
	}
	public void switch_read_lock_to_write_lock(String locker_name[])
	{
		ReentrantReadWriteLock p;
		if((p=operate_locker_routine(false,locker_name,0))!=null){
			p.readLock().unlock();
			p.writeLock().lock();
		}
	}
	public void switch_read_lock_to_write_lock(String locker_name)
	{
		switch_read_lock_to_write_lock(new String[] {locker_name});
	}
}
