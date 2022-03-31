package kernel_component;

import kernel_common_class.change_name;
import kernel_engine.component_container;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;

public class component_core_1 extends component_core_0
{
	public int component_id,parent_component_id;
	public String component_name,part_name; 
	
	public void destroy()
	{
		super.destroy();
		
		component_name=null;
		part_name=null;
	}
	public void caculate_one_selection(component_container component_cont)
	{
		component parent=component_cont.get_component(parent_component_id);
		uniparameter.effective_selected_flag=uniparameter.selected_flag;
		if(parent!=null)
			uniparameter.effective_selected_flag|=parent.uniparameter.effective_selected_flag;
	}
	public component_core_1(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			part_type_string_sorter type_string_sorter,boolean normalize_location_flag,
			boolean part_list_flag,long default_display_bitmap)
	{
		super(token_string,ek,request_response,fr,pcfps,change_part_name,
			mount_component_name,type_string_sorter,normalize_location_flag,
			part_list_flag,default_display_bitmap);
		
		component_id=-1;
		parent_component_id=-1;

		if((component_name=fr.get_string())==null)
			component_name=new String(token_string);
		else
			component_name=token_string+component_name;

		if((part_name=fr.get_string())==null)
			part_name="";
	}
}