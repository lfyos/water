package kernel_information;

import kernel_engine.system_parameter;
import kernel_engine.client_information;

public class system_parameter_information extends jason_creator
{
	private system_parameter sp;
	
	public void print()
	{	
		print("system_data_root_directory_name",		sp.data_root_directory_name);
		print("temporary_root_directory_name",			sp.temporary_file_par.temporary_root_directory_name);
		
		print("compress_temporary_root_directory_name",	sp.temporary_file_par.compress_temporary_root_directory_name);
		
		print("local_data_charset",						sp.local_data_charset);
		print("network_data_charset",					sp.network_data_charset);
		print("text_class_charset",						sp.text_class_charset);
		print("text_jar_file_charset",					sp.text_jar_file_charset);
		print("js_class_charset",						sp.js_class_charset);
		print("js_jar_file_charset",					sp.js_jar_file_charset);
		
		print("user_file_name",							sp.user_file_name);
		print("shader_file_name",						sp.shader_file_name);
		print("default_parameter_directory",			sp.default_parameter_directory);
		
		
		print("normal_loading_number",					sp.normal_loading_number);
		print("max_loading_number",						sp.max_loading_number);
		print("max_material_id",						sp.max_material_id);

		print("response_block_size",					sp.response_block_size);
		
		print("engine_expire_time_length",				sp.engine_expire_time_length);
		print("engine_touch_time_length",				sp.engine_touch_time_length);
		
		print("create_engine_concurrent_number",		sp.create_engine_concurrent_number);
		print("create_engine_sleep_time_length_scale",	sp.create_engine_sleep_time_length_scale);
		print("create_engine_sleep_time_length",		sp.create_engine_sleep_time_length);
		print("create_engine_max_sleep_time_length",	sp.create_engine_max_sleep_time_length);
		
		print("show_process_bar_interval",				sp.show_process_bar_interval);
		print("file_buffer_expire_time_length",			sp.file_buffer_expire_time_length);
		
		print("max_client_container_number",			sp.max_client_container_number);
		print("max_client_interface_number",			sp.max_client_interface_number);
		
		print("max_engine_kernel_number",				sp.max_engine_kernel_number);
		print("max_engine_component_number",			sp.max_engine_component_number);
		
		print("max_buffer_object_head_package_length",	sp.max_buffer_object_head_package_length);
		print("max_file_response_length",				sp.max_file_response_length);
		print("min_compress_response_length",			sp.min_compress_response_length);
		
		print("system_cors_string",						sp.system_cors_string);
		
		return;
	}
	public system_parameter_information(system_parameter my_sp,client_information my_ci)
	{
		super(my_ci.request_response);
		sp=my_sp;
	}
}
