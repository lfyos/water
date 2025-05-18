package kernel_component;

public class component_location_modification_locker 
{
	private boolean lock_location_modification_flag;

	public boolean lock_location_modification()
	{
		boolean ret_val=lock_location_modification_flag?false:true;
		lock_location_modification_flag=true;
		return ret_val;
	}
	public boolean unlock_location_modification()
	{
		boolean ret_val=lock_location_modification_flag;
		lock_location_modification_flag=false;
		return ret_val;
	}
	public boolean get_location_modification_lock()
	{
		return lock_location_modification_flag;
	}
	public component_location_modification_locker()
	{
		lock_location_modification_flag=false;
	}
}
