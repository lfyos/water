package kernel_component;

import kernel_common_class.change_name;
import kernel_file_manager.file_reader;

public class component_core_9 extends component_core_8
{
	private boolean lock_location_modification_flag;
	
	public void destroy()
	{
		super.destroy();
	}
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
	public component_core_9(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,change_name change_part_name,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,change_part_name,ccp);

		lock_location_modification_flag=false;
	}
}
