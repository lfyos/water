package kernel_component;

import kernel_common_class.change_name;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;

public class component_core_7 extends component_core_6
{
	public boolean children_location_modify_flag;
	
	public void destroy()
	{
		super.destroy();
	}
	public void caculate_children_location_modify_flag()
	{
		int n;
		if((n=children_number())<=0){	
			children_location_modify_flag=false;
			return;
		}
		for(int i=0;i<n;i++)
			if(children[i].children_location_modify_flag){
				children_location_modify_flag=true;
				return;
			}
		for(int i=0;i<n;i++)
			if(children[i].move_location.is_not_identity_matrix()){
				children_location_modify_flag=true;
				return;
			}
		children_location_modify_flag=false;
		return;
	}
	
	public component_core_7(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			change_name reverse_mount_component_name,part_type_string_sorter type_string_sorter,
			boolean part_list_flag,long default_display_bitmap,int max_child_number)
	{
		super(token_string,ek,request_response,fr,pcfps,
			change_part_name,mount_component_name,reverse_mount_component_name,
			type_string_sorter,part_list_flag,default_display_bitmap,max_child_number);

		children_location_modify_flag=false;
	}
}
