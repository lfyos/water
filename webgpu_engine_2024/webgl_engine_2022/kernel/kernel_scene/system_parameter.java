package kernel_scene;

import java.io.File;
import java.nio.charset.Charset;

import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_content_type.get_content_type_change_name;
import kernel_interface.switch_scene_server;
import kernel_network.network_implementation_default_parameter;

public class system_parameter
{
	public String data_root_directory_name;
	public long last_modified_time;
	
	public String local_data_charset,network_data_charset;
	public String text_class_charset,text_jar_file_charset,js_class_charset,js_jar_file_charset;
	
	public String user_file_name,shader_file_name,default_parameter_directory;
	
	public int normal_loading_number,max_loading_number,max_material_id;
	
	public int response_block_size;
	
	public long scene_expire_time_length,scene_touch_time_length;
	
	public int create_scene_concurrent_number;
	public double create_scene_sleep_time_length_scale;
	public long create_scene_sleep_time_length,create_scene_max_sleep_time_length;
	
	public long show_process_bar_interval,file_buffer_expire_time_length;
	
	public int max_client_container_number,max_client_interface_number;
	
	public int max_scene_kernel_number,max_scene_component_number;
	
	public long max_buffer_object_head_package_length;
	public long max_file_response_length,min_compress_response_length;
	
	public String system_cors_string;
	
	public int max_process_component_load_number,max_process_modifier_number;
	
	public change_name language_change_name,content_type_change_name;
	public temporary_file_parameter temporary_file_par;
	public switch_scene_server	switch_server;

	public system_parameter(system_parameter sp)
	{
		data_root_directory_name		=new String(sp.data_root_directory_name);
		
		last_modified_time				=sp.last_modified_time;

		local_data_charset				=new String(sp.local_data_charset);
		network_data_charset			=new String(sp.network_data_charset);
		text_class_charset				=new String(sp.text_class_charset);
		text_jar_file_charset			=new String(sp.text_jar_file_charset);
		js_class_charset				=new String(sp.js_class_charset);
		js_jar_file_charset				=new String(sp.js_jar_file_charset);
		
		user_file_name					=new String(sp.user_file_name);
		shader_file_name				=new String(sp.shader_file_name);
		default_parameter_directory		=new String(sp.default_parameter_directory);
		
		normal_loading_number			=sp.normal_loading_number;
		max_loading_number				=sp.max_loading_number;
		max_material_id					=sp.max_material_id;
		
		response_block_size				=sp.response_block_size;
		
		scene_expire_time_length		=sp.scene_expire_time_length;
		scene_touch_time_length			=sp.scene_touch_time_length;
		
		create_scene_concurrent_number			=sp.create_scene_concurrent_number;
		create_scene_sleep_time_length_scale	=sp.create_scene_sleep_time_length_scale;
		create_scene_sleep_time_length			=sp.create_scene_sleep_time_length;
		create_scene_max_sleep_time_length		=sp.create_scene_max_sleep_time_length;
		
		show_process_bar_interval				=sp.show_process_bar_interval;
		file_buffer_expire_time_length			=sp.file_buffer_expire_time_length;

		max_client_container_number		=sp.max_client_container_number;
		max_client_interface_number		=sp.max_client_interface_number;
		
		max_scene_kernel_number			=sp.max_scene_kernel_number;
		max_scene_component_number		=sp.max_scene_component_number;
		
		max_file_response_length		=sp.max_file_response_length;
		min_compress_response_length	=sp.min_compress_response_length;
		max_buffer_object_head_package_length=sp.max_buffer_object_head_package_length;
		
		system_cors_string				=new String(sp.system_cors_string);
		
		max_process_component_load_number=sp.max_process_component_load_number;
		max_process_modifier_number		=sp.max_process_modifier_number;
		
		content_type_change_name		=new change_name(sp.content_type_change_name,false);
		language_change_name			=new change_name(sp.language_change_name,false);
		
		temporary_file_par				=sp.temporary_file_par;
		switch_server					=sp.switch_server;
	}
	public system_parameter(
			String data_file_configure_file_name,
			String temporary_file_configure_file_name)
	{
		data_file_configure_file_name		=file_reader.separator(data_file_configure_file_name);
		temporary_file_configure_file_name	=file_reader.separator(temporary_file_configure_file_name);

		debug_information.println();
		debug_information.println("data_file_configure_file_name:		",	data_file_configure_file_name);
		debug_information.println("temporary_file_configure_file_name:	",	temporary_file_configure_file_name);

		file_reader f=new file_reader(data_file_configure_file_name,Charset.defaultCharset().name());
		
		if(f.error_flag()){
			debug_information.println("Can't not open system_parameter file	",data_file_configure_file_name);
			debug_information.println("do System.exit(0)");
			System.exit(0);
			return;
		}
		
		if((local_data_charset=f.get_string())==null)
			local_data_charset=Charset.defaultCharset().name();
		else if(local_data_charset.compareTo("default_charset")==0)
			local_data_charset=Charset.defaultCharset().name();
		f.close();

		f=new file_reader(data_file_configure_file_name,local_data_charset);
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
		network_implementation_default_parameter.network_request_charset=network_data_charset;
		
		if((text_class_charset=f.get_string())==null)
			text_class_charset=Charset.defaultCharset().name();
		if(text_class_charset.compareTo("default_charset")==0)
			text_class_charset=Charset.defaultCharset().name();
		
		if((text_jar_file_charset=f.get_string())==null)
			text_jar_file_charset=Charset.defaultCharset().name();
		if(text_jar_file_charset.compareTo("default_charset")==0)
			text_jar_file_charset=Charset.defaultCharset().name();
		
		if((js_class_charset=f.get_string())==null)
			js_class_charset=Charset.defaultCharset().name();
		if(js_class_charset.compareTo("default_charset")==0)
			js_class_charset=Charset.defaultCharset().name();
		
		if((js_jar_file_charset=f.get_string())==null)
			js_jar_file_charset=Charset.defaultCharset().name();
		if(js_jar_file_charset.compareTo("default_charset")==0)
			js_jar_file_charset=Charset.defaultCharset().name();

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
		
		String switch_server_url_file_name;
		if((switch_server_url_file_name=f.get_string())==null)
			switch_server_url_file_name="";
		else
			switch_server_url_file_name=file_reader.separator(switch_server_url_file_name);	
		
		normal_loading_number					=f.get_int();
		max_loading_number						=f.get_int();
		max_material_id							=f.get_int();
		
		response_block_size						=f.get_int();
		
		scene_expire_time_length				=f.get_long();
		scene_touch_time_length					=f.get_long();
		
		create_scene_concurrent_number			=f.get_int();
		create_scene_sleep_time_length_scale	=f.get_double();
		create_scene_sleep_time_length			=f.get_long();
		create_scene_max_sleep_time_length		=f.get_long();
		
		show_process_bar_interval				=f.get_long();
		file_buffer_expire_time_length			=f.get_long();
		
		max_client_container_number				=f.get_int();
		max_client_interface_number				=f.get_int();
		
		max_scene_kernel_number					=f.get_int();
		max_scene_component_number				=f.get_int();

		max_file_response_length				=f.get_long();
		min_compress_response_length			=f.get_long();
		max_buffer_object_head_package_length	=f.get_long();

		if((system_cors_string=f.get_string())==null)
			system_cors_string="*";
		else if((system_cors_string=system_cors_string.trim()).length()<=0)
			system_cors_string="*";
		else{
			String cores_file_name=f.directory_name+file_reader.separator(system_cors_string);
			if(new File(cores_file_name).exists()){
				String file_system_cors_string="";
				for(file_reader cors_fr=new file_reader(cores_file_name,f.get_charset());;) {
					if(cors_fr.eof()){
						cors_fr.close();
						break;
					}
					String str;
					if((str=cors_fr.get_string())!=null)
						if((str=str.trim()).length()>0)
							file_system_cors_string+=str;
				}
				if(file_system_cors_string.length()>0)
					system_cors_string=file_system_cors_string;
			}
		}
		
		max_process_component_load_number		=f.get_int();
		max_process_modifier_number				=f.get_int();
		
		f.close();
		
		temporary_file_par=new temporary_file_parameter(temporary_file_configure_file_name,local_data_charset);
		language_change_name=new change_name(
				new String[]{data_root_directory_name+language_change_file_name},null,local_data_charset);
		content_type_change_name=get_content_type_change_name.get_change_name(text_class_charset,text_jar_file_charset);
		switch_server=new switch_scene_server(data_root_directory_name+switch_server_url_file_name,local_data_charset);
		
		return;
	}
}
