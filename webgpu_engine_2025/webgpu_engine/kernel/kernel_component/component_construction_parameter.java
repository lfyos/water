package kernel_component;

import java.util.ArrayList;

import kernel_scene.scene_kernel;
import kernel_common_class.change_name;
import kernel_scene.part_type_string_sorter;
import kernel_network.client_request_response;

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
		int index_id;
		if((index_id=type_string_sorter_list.size()-1)>=0)
			type_string_sorter_list.remove(index_id);
	}
	
	public scene_kernel sk;
	public client_request_response request_response;
	
	public component_load_source_container clsc;
	
	public long default_display_bitmap;
	
	public component_construction_parameter(
			scene_kernel my_sk,client_request_response my_request_response,
			component_load_source_container my_clsc,long my_default_display_bitmap)
	{
		sk						=my_sk;
		request_response		=my_request_response;
		
		change_part_name_list	=new ArrayList<change_name>();
		if(sk.scene_par.change_part_string!=null)
			if(sk.scene_par.change_part_string.length()>0)
				change_part_name_list.add(
					new change_name(new String[]{},sk.scene_par.change_part_string,null));
		
		type_string_sorter_list	=new ArrayList<part_type_string_sorter>();
		if(sk.scene_par.part_type_string!=null)
			if(sk.scene_par.part_type_string.length()>0)
				type_string_sorter_list.add(
					new part_type_string_sorter(null,sk.scene_par.part_type_string,null));

		clsc					=my_clsc;
		default_display_bitmap	=my_default_display_bitmap;
	}
}
