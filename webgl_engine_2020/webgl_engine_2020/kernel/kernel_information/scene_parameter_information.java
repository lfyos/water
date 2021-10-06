package kernel_information;

import kernel_engine.scene_parameter;
import kernel_engine.client_information;

public class scene_parameter_information extends jason_creator
{
	private scene_parameter sp;
	
	public void print()
	{
		print("change_part_string",							sp.change_part_string);
		print("change_component_string",					sp.change_component_string);
		print("part_type_string",							sp.part_type_string);
		
		print("directory_name",								sp.directory_name);
		print("parameter_charset",							sp.parameter_charset);
		print("parameter_last_modified_time",				sp.parameter_last_modified_time);
		print("scene_last_modified_time",					sp.scene_last_modified_time);
		
		print("scene_sub_directory",						sp.scene_sub_directory);
		
		print("type_proxy_directory_name",					sp.type_proxy_directory_name);
		print("scene_proxy_directory_name",					sp.scene_proxy_directory_name);
		print("type_shader_file_name",						sp.type_shader_file_name);
		print("scene_shader_file_name",						sp.scene_shader_file_name);
		print("camera_file_name",							sp.camera_file_name);
		print("change_part_file_name",						sp.change_part_file_name);
		print("change_component_file_name",					sp.change_component_file_name);
		print("type_string_file_name",						sp.type_string_file_name);
		
		print("inserted_component_name",					sp.inserted_component_name);
		print("inserted_part_name",							sp.inserted_part_name);
		print("inserted_component_and_part_id",				sp.inserted_component_and_part_id);
		print("max_child_number",							sp.max_child_number);
		
		print("scene_cors_string",							sp.scene_cors_string);
		
		print("multiparameter_number",						sp.multiparameter_number);
		print("initial_parameter_channel_id",				sp.initial_parameter_channel_id);
		print("default_display_bitmap",						sp.default_display_bitmap);
		
		print("component_collector_stack_file_name",		sp.component_collector_stack_file_name);
		print("component_collector_parameter_channel_id",	sp.component_collector_parameter_channel_id);
		print("max_component_collector_number",				sp.max_component_collector_number);

		print("max_camera_return_stack_number",				sp.max_camera_return_stack_number);
		print("max_modifier_container_number",				sp.max_modifier_container_number);
		
		print("create_top_part_assembly_precision2",		sp.create_top_part_assembly_precision2);
		print("create_top_part_discard_precision2",			sp.create_top_part_discard_precision2);
		print("discard_top_part_component_precision2",		sp.discard_top_part_component_precision2);
		
		print("touch_time_length",							sp.touch_time_length);
		print("most_component_delete_number",				sp.most_component_delete_number);
		print("most_component_append_number",				sp.most_component_append_number);
		print("most_update_parameter_number",				sp.most_update_parameter_number);
		print("most_update_location_number",				sp.most_update_location_number);
		
		print("display_precision",							sp.display_precision);
		
		print("display_assemble_depth",						sp.display_assemble_depth);
		print("component_sort_type",						sp.component_sort_type);
		print("component_sort_min_distance",				sp.component_sort_min_distance);
		
		print("not_do_ancestor_render_flag",				sp.not_do_ancestor_render_flag);
		print("do_discard_lod_flag",						sp.do_discard_lod_flag);
		print("do_selection_lod_flag",						sp.do_selection_lod_flag);
		
		print("proxy_response_length",						sp.proxy_response_length);
		print("compress_response_length",					sp.compress_response_length);
	}
	public scene_parameter_information(scene_parameter my_sp,client_information my_ci)
	{
		super(my_ci.request_response);
		sp=my_sp;
	}
}
