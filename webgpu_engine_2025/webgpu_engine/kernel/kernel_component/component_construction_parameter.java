package kernel_component;

import java.util.ArrayList;

import kernel_scene.scene_kernel;
import kernel_common_class.change_name;
import kernel_common_class.name_exist_tester;
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
		int index_id;
		if((index_id=change_part_name_list.size()-1)>=0)
			change_part_name_list.remove(index_id);
	}
	
	private ArrayList<name_exist_tester> part_type_string_tester_list;
	
	public name_exist_tester get_part_type_string_tester()
	{
		int index_id=part_type_string_tester_list.size()-1;
		return (index_id<0)?null:part_type_string_tester_list.get(index_id);
	}
	public void push_part_type_string_tester(name_exist_tester tester)
	{
		part_type_string_tester_list.add(tester);
	}
	public void pop_part_type_string_tester()
	{
		int index_id;
		if((index_id=part_type_string_tester_list.size()-1)>=0)
			part_type_string_tester_list.remove(index_id);
	}
	
	public scene_kernel sk;
	public client_request_response request_response;
	
	public component_load_source_container clsc;
	
	public component_construction_parameter(scene_kernel my_sk,
			client_request_response my_request_response,component_load_source_container my_clsc)
	{
		sk				=my_sk;
		request_response=my_request_response;
		clsc			=my_clsc;
		
		change_part_name_list	=new ArrayList<change_name>();
		if(sk.scene_par.change_part_string!=null)
			if(sk.scene_par.change_part_string.length()>0)
				change_part_name_list.add(
					new change_name(new String[]{},sk.scene_par.change_part_string,null));
		
		part_type_string_tester_list	=new ArrayList<name_exist_tester>();
		if(sk.scene_par.part_type_string!=null)
			if(sk.scene_par.part_type_string.length()>0)
				part_type_string_tester_list.add(
					new name_exist_tester(null,sk.scene_par.part_type_string,null));
	}
}
