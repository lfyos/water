package kernel_component;

import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_common_class.change_name;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;

public class component_core_10 	extends component_core_9
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
	public component_core_10(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,change_name change_part_name,
			part_type_string_sorter type_string_sorter,boolean normalize_location_flag,
			boolean part_list_flag,long default_display_bitmap)
	{
		super(token_string,ek,request_response,fr,pcfps,change_part_name,
			type_string_sorter,normalize_location_flag,part_list_flag,default_display_bitmap);

		lock_location_modification_flag=false;
	}
}

