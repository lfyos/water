package kernel_scene;

import java.util.ArrayList;

import kernel_render.render_container;
import kernel_part.part_loader_container;
import kernel_interface.client_process_bar;
import kernel_common_class.tree_string_locker_container;
import kernel_component.component_load_source_container;
import kernel_part.buffer_object_file_modify_time_and_length_container;

public class scene_load_call_parameter 
{
	public client_process_bar process_bar;
	public render_container original_render;
	public part_loader_container part_loader_cont;
	public tree_string_locker_container string_locker_cont;
	public component_load_source_container component_load_source_cont;
	public ArrayList<buffer_object_file_modify_time_and_length_container> boftal_cont;

	public scene_load_call_parameter(
			client_process_bar my_process_bar,
			render_container my_original_render,
			part_loader_container my_part_loader_cont,
			tree_string_locker_container my_string_locker_cont,
			component_load_source_container my_system_component_load_source_cont,
			buffer_object_file_modify_time_and_length_container my_system_boftal_cont)
	{
		process_bar				=my_process_bar;
		original_render			=my_original_render;
		part_loader_cont		=my_part_loader_cont;
		string_locker_cont		=my_string_locker_cont;
		component_load_source_cont=my_system_component_load_source_cont;
		boftal_cont				=new ArrayList<buffer_object_file_modify_time_and_length_container>();
		if(my_system_boftal_cont!=null)
			boftal_cont.add(my_system_boftal_cont);
	}
}
