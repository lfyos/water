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
}
public class tree_string_locker_container extends tree_string_search_container<locker_container>
{
	synchronized private ReentrantReadWriteLock get_locker(
			String locker_name[],boolean do_lock_flag,int modify_number)
	{
		ArrayList<locker_container> list;
		locker_container p;
		
		if(do_lock_flag){
			for(list=add(locker_name,null);list.size()>1;)
				list.remove(1);
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
		for(ArrayList<locker_container> p;(p=get_first_value())!=null;) { 
			for(int index_id;(index_id=p.size())>0;) {
				locker_container lc=p.get(index_id);
				p.remove(index_id);
				while(lc.locker.getReadHoldCount()>0)
					lc.locker.readLock().unlock();
				while(lc.locker.getWriteHoldCount()>0)
					lc.locker.writeLock().unlock();
			}
			remove(get_first_key());
		}
	}
	
	private void write_lock(String locker_name[])
	{
		ReentrantReadWriteLock p;
		if((p=get_locker(locker_name,true,1))!=null)
			p.writeLock().lock();
	}
	public void write_lock(String locker_name)
	{
		write_lock(new String[] {locker_name});
	}
	
	public void write_unlock(String locker_name[])
	{
		ReentrantReadWriteLock p;
		if((p=get_locker(locker_name,false,-1))!=null)
			p.writeLock().unlock();
	}
	public void write_unlock(String locker_name)
	{
		write_unlock(new String[] {locker_name});
	}
	
	public void read_lock(String locker_name[])
	{
		ReentrantReadWriteLock p;
		if((p=get_locker(locker_name,true,1))!=null)
			p.readLock().lock();
	}
	public void read_lock(String locker_name)
	{
		read_lock(new String[] {locker_name});
	}
	
	
	public void read_unlock(String locker_name[])
	{
		ReentrantReadWriteLock p;
		if((p=get_locker(locker_name,false,-1))!=null)
			p.readLock().unlock();
	}
	public void read_unlock(String locker_name)
	{
		read_unlock(new String[] {locker_name});
	}
	
	public void switch_read_lock_to_write_lock(String locker_name[])
	{
		ReentrantReadWriteLock p;
		if((p=get_locker(locker_name,true,0))!=null){
			p.readLock().unlock();
			p.writeLock().lock();
		}
	}
	public void switch_read_lock_to_write_lock(String locker_name)
	{
		switch_read_lock_to_write_lock(new String[] {locker_name});
	}
}
