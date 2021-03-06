package kernel_engine;

import java.io.File;
import java.nio.charset.Charset;

import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_common_class.exclusive_number_mutex;
import kernel_common_class.exclusive_name_mutex;
import kernel_file_manager.file_reader;
import kernel_content_type.get_content_type_change_name;

public class system_parameter
{
	public String data_root_directory_name;
	public long last_modified_time;
	
	public String local_data_charset,network_data_charset;
	public String user_file_name,shader_file_name,default_parameter_directory;
	
	public int default_max_loading_number,mesh_decimal_number,max_material_id;
	
	public int response_block_size;
	
	public long engine_expire_time_length,engine_load_time_length,engine_touch_time_length;
	
	public int create_engine_concurrent_number;
	public double create_engine_sleep_time_length_scale;
	public long create_engine_sleep_time_length,create_engine_max_sleep_time_length;
	
	public long file_buffer_expire_time_length;
	
	public int max_client_interface_number,max_engine_kernel_number,max_component_number,max_request_number;
	
	public long max_buffer_object_head_package_length;
	
	public String file_download_cors_string;
	
	public boolean debug_mode_flag;
	
	public exclusive_number_mutex system_exclusive_number_mutex;
	public exclusive_name_mutex   system_exclusive_name_mutex;
	
	public change_name language_change_name,content_type_change_name;
	public proxy_parameter proxy_par;

	public system_parameter(system_parameter sp)
	{
		data_root_directory_name		=new String(sp.data_root_directory_name);
		
		last_modified_time				=sp.last_modified_time;

		local_data_charset				=new String(sp.local_data_charset);
		network_data_charset			=new String(sp.network_data_charset);

		user_file_name					=new String(sp.user_file_name);
		shader_file_name				=new String(sp.shader_file_name);
		default_parameter_directory		=new String(sp.default_parameter_directory);
		
		default_max_loading_number		=sp.default_max_loading_number;
		mesh_decimal_number				=sp.mesh_decimal_number;
		max_material_id					=sp.max_material_id;
		
		response_block_size				=sp.response_block_size;
		
		engine_expire_time_length		=sp.engine_expire_time_length;
		engine_load_time_length			=sp.engine_load_time_length;
		engine_touch_time_length		=sp.engine_touch_time_length;
		
		create_engine_concurrent_number			=sp.create_engine_concurrent_number;
		create_engine_sleep_time_length_scale	=sp.create_engine_sleep_time_length_scale;
		create_engine_sleep_time_length			=sp.create_engine_sleep_time_length;
		create_engine_max_sleep_time_length		=sp.create_engine_max_sleep_time_length;
		
		file_buffer_expire_time_length			=sp.file_buffer_expire_time_length;
		
		max_client_interface_number		=sp.max_client_interface_number;
		max_engine_kernel_number		=sp.max_engine_kernel_number;
		max_component_number			=sp.max_component_number;
		max_request_number				=sp.max_request_number;
		
		max_buffer_object_head_package_length=sp.max_buffer_object_head_package_length;
		
		file_download_cors_string		=new String(sp.file_download_cors_string);
		
		debug_mode_flag					=sp.debug_mode_flag;
		
		system_exclusive_number_mutex	=sp.system_exclusive_number_mutex;
		system_exclusive_name_mutex		=sp.system_exclusive_name_mutex;
		content_type_change_name		=new change_name(sp.content_type_change_name,false);
		language_change_name			=new change_name(sp.language_change_name,false);
		
		proxy_par						=sp.proxy_par;
	}
	public system_parameter(String application_directory_name,
			String data_configure_environment_variable,
			String proxy_configure_environment_variable)
	{
		String data_configure_file_name;
		if((data_configure_file_name=System.getenv(data_configure_environment_variable))==null)
			data_configure_file_name=data_configure_environment_variable;
		data_configure_file_name	=file_reader.separator(data_configure_file_name.trim());
		if(data_configure_file_name.charAt(0)=='.')
			data_configure_file_name=application_directory_name+data_configure_file_name;
		
		String proxy_configure_file_name;
		if((proxy_configure_file_name=System.getenv(proxy_configure_environment_variable))==null)
			proxy_configure_file_name=proxy_configure_environment_variable;
		proxy_configure_file_name=file_reader.separator(proxy_configure_file_name.trim());
		if(proxy_configure_file_name.charAt(0)=='.')
			proxy_configure_file_name=application_directory_name+proxy_configure_file_name;

		file_reader f=new file_reader(data_configure_file_name,Charset.defaultCharset().name());
		if(f.error_flag()){
			debug_information.println("Can't not open configure.txt on working directory");
			debug_information.println("Check content in configure.txt on web server please");
			debug_information.println("It probably contain error content or pointer to wrong directory");
			debug_information.println("application_directory_name is ",application_directory_name);
			debug_information.println("data_configure_file_name is ",data_configure_file_name);
			debug_information.println("proxy_configure_file_name is ",proxy_configure_file_name);
		}
		if((local_data_charset=f.get_string())==null)
			local_data_charset=Charset.defaultCharset().name();
		else if(local_data_charset.compareTo("default_charset")==0)
			local_data_charset=Charset.defaultCharset().name();
		f.close();

		f=new file_reader(data_configure_file_name,local_data_charset);
		data_root_directory_name=f.directory_name;
		last_modified_time=f.lastModified_time;
		
		if((local_data_charset=f.get_string())==null)
			local_data_charset=Charset.defaultCharset().name();
		else if(local_data_charset.compareTo("default_charset")==0)
			local_data_charset=Charset.defaultCharset().name();
		
		if((network_data_charset=f.get_string())==null)
			network_data_charset=Charset.defaultCharset().name();
		if(network_data_charset.compareTo("default_charset")==0)
			network_data_charset=Charset.defaultCharset().name();

		if((user_file_name=f.get_string())==null)
			user_file_name="";
		else
			user_file_name=file_reader.separator(user_file_name).trim();
		
		if((shader_file_name=f.get_string())==null)
			shader_file_name="";
		else
			shader_file_name=file_reader.separator(shader_file_name).trim();
		
		if((default_parameter_directory=f.get_string())==null)
			default_parameter_directory="";
		else {
			default_parameter_directory=file_reader.separator(default_parameter_directory).trim();
			int str_length=default_parameter_directory.length();
			if(default_parameter_directory.charAt(str_length-1)!=File.separatorChar)
				default_parameter_directory+=File.separator;
			default_parameter_directory=f.directory_name+default_parameter_directory;
		}

		String language_change_file_name;
		if((language_change_file_name=f.get_string())==null)
			language_change_file_name="";
		else
			language_change_file_name=file_reader.separator(language_change_file_name);	
		
		default_max_loading_number				=f.get_int();
		mesh_decimal_number						=f.get_int();
		max_material_id							=f.get_int();
		
		response_block_size						=f.get_int();
		
		engine_expire_time_length				=f.get_long();
		engine_load_time_length					=f.get_long();
		engine_touch_time_length				=f.get_long();
		
		create_engine_concurrent_number			=f.get_int();
		create_engine_sleep_time_length_scale	=f.get_double();
		create_engine_sleep_time_length			=f.get_long();
		create_engine_max_sleep_time_length		=f.get_long();
		
		file_buffer_expire_time_length			=f.get_long();
		
		max_client_interface_number				=f.get_int();
		max_engine_kernel_number				=f.get_int();
		max_component_number					=f.get_int();
		max_request_number						=f.get_int();
		
		max_buffer_object_head_package_length	=f.get_long();
		
		file_download_cors_string=f.get_string();
		String my_file_name=f.directory_name+file_download_cors_string;
		if(new File(my_file_name).exists())
			file_download_cors_string=file_reader.get_text(my_file_name,f.get_charset()).trim();
		
		debug_mode_flag							=f.get_boolean();
		
		f.close();
		
		system_exclusive_number_mutex=new exclusive_number_mutex();
		system_exclusive_name_mutex=new exclusive_name_mutex();
		
		proxy_par=new proxy_parameter(proxy_configure_file_name,local_data_charset);
		language_change_name=new change_name(
				new String[]{data_root_directory_name+language_change_file_name},
				null,local_data_charset);
		content_type_change_name=get_content_type_change_name.get_change_name();
	}
}
