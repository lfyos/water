package kernel_component;

import kernel_common_class.change_name;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;

public class component_construction_parameter 
{
	public engine_kernel ek;
	public client_request_response request_response;
	public part_container_for_part_search pcfps;
	public change_name change_part_name;
	public part_type_string_sorter type_string_sorter;
	
	component_load_source_container clsc;
	
	public long default_display_bitmap;
	
	public component_construction_parameter(
			engine_kernel my_ek,
			client_request_response my_request_response,
			part_container_for_part_search my_pcfps,
			change_name my_change_part_name,
			part_type_string_sorter my_type_string_sorter,
			component_load_source_container my_clsc,
			long my_default_display_bitmap)
	{
		ek						=my_ek;
		request_response		=my_request_response;
		pcfps					=my_pcfps;
		change_part_name		=my_change_part_name;
		type_string_sorter		=my_type_string_sorter;
		clsc					=my_clsc;
		default_display_bitmap	=my_default_display_bitmap;
	}
}
