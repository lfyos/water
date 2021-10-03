package kernel_information;

import kernel_engine.system_parameter;
import kernel_engine.client_information;

public class system_parameter_information extends jason_creator
{
	private system_parameter sp;
	
	public void print()
	{	
		print("data_root_directory_name",				sp.data_root_directory_name);
		print("proxy_data_root_directory_name",			sp.proxy_par.proxy_data_root_directory_name);
		
		print("compress_data_root_directory_name",		sp.proxy_par.compress_data_root_directory_name);
		print("compress_proxy_data_root_directory_name",sp.proxy_par.compress_proxy_data_root_directory_name);
		
		print("shader_file_name",						sp.shader_file_name);
		print("user_file_name",							sp.user_file_name);
		print("default_parameter_directory",			sp.default_parameter_directory);

		print("data_storage_charset",					sp.local_data_charset);
		print("server_response_charset",				sp.network_data_charset);		
		print("response_block_size",					sp.response_block_size);

		print("engine_expire_time_length",				sp.engine_expire_time_length);
		print("engine_touch_time_length",				sp.engine_touch_time_length);
		
		print("max_client_interface_number",			sp.max_client_interface_number);
		
		print("max_engine_kernel_number",				sp.max_engine_kernel_number);
		print("max_engine_component_number",			sp.max_engine_component_number);
		print("max_engine_part_face_number",			sp.max_engine_part_face_number);
		print("max_engine_part_edge_number",			sp.max_engine_part_edge_number);
		
		print("file_download_cors_string",				sp.file_download_cors_string);
		print("debug_mode_flag",						sp.debug_mode_flag);
		
		return;
	}
	public system_parameter_information(system_parameter my_sp,client_information my_ci)
	{
		super(my_ci.request_response);
		sp=my_sp;
	}
}
