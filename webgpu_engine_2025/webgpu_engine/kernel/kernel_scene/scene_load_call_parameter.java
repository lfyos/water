package kernel_scene;

import kernel_interface.client_process_bar;
import kernel_network.client_request_response;

import java.util.ArrayList;

import kernel_common_class.tree_string_locker_container;
import kernel_component.component_load_source_container;
import kernel_part.buffer_object_file_modify_time_and_length_container;

public class scene_load_call_parameter 
{
	public client_process_bar process_bar;
	public client_request_response request_response;
	public tree_string_locker_container string_locker_cont;
	public component_load_source_container scene_component_load_source_cont;
	public ArrayList<buffer_object_file_modify_time_and_length_container> boftal_cont;
	
	public scene_load_call_parameter(
			client_process_bar my_process_bar,
			client_request_response my_request_response,
			tree_string_locker_container my_string_locker_cont,
			buffer_object_file_modify_time_and_length_container my_boftal_cont,
			component_load_source_container my_scene_component_load_source_cont)
	{
		process_bar			=my_process_bar;
		request_response	=my_request_response;
		string_locker_cont	=my_string_locker_cont;
		scene_component_load_source_cont=my_scene_component_load_source_cont;
		boftal_cont			=new ArrayList<buffer_object_file_modify_time_and_length_container>();
		boftal_cont.add(my_boftal_cont);
	}
}
