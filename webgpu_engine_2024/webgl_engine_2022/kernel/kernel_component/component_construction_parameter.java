package kernel_component;

import java.util.ArrayList;

import kernel_engine.engine_kernel;
import kernel_common_class.change_name;
import kernel_engine.part_type_string_sorter;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;

public class component_construction_parameter 
{
	private ArrayList<change_name> change_part_name_list;
	
	public change_name get_change_part_name()
	{
		int index_id=change_part_name_list.size()-1;
		return (index_id<0)?null:change_part_name_list.get(index_id);
	}
	public void push_change_part_name(change_name my_change_part_name)
	{
		change_part_name_list.add(my_change_part_name);
	}
	public void pop_change_part_name()
	{
		int index_id=change_part_name_list.size()-1;
		if(index_id>=0)
			change_part_name_list.remove(index_id);
	}
	
	private ArrayList<part_type_string_sorter> type_string_sorter_list;
	
	public part_type_string_sorter get_part_type_string_sorter()
	{
		int index_id=type_string_sorter_list.size()-1;
		return (index_id<0)?null:type_string_sorter_list.get(index_id);
	}
	public void push_part_type_string_sorter(part_type_string_sorter ptss)
	{
		type_string_sorter_list.add(ptss);
	}
	public void pop_part_type_string_sorter()
	{
		int index_id=type_string_sorter_list.size()-1;
		if(index_id>=0)
			type_string_sorter_list.remove(index_id);
	}
	
	public engine_kernel ek;
	public client_request_response request_response;
	public part_container_for_part_search pcfps;
	
	component_load_source_container clsc;
	
	public long default_display_bitmap;
	
	public component_construction_parameter(
			engine_kernel my_ek,
			client_request_response my_request_response,
			part_container_for_part_search my_pcfps,
			component_load_source_container my_clsc,
			long my_default_display_bitmap)
	{
		ek						=my_ek;
		request_response		=my_request_response;
		pcfps					=my_pcfps;
		
		change_part_name_list	=new ArrayList<change_name>();
		change_part_name_list.add(
			new change_name(new String[]{},ek.scene_par.change_part_string,null));
		type_string_sorter_list	=new ArrayList<part_type_string_sorter>();
		
		clsc					=my_clsc;
		default_display_bitmap	=my_default_display_bitmap;
	}
}
