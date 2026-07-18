package kernel_scene;

import java.io.File;
import java.nio.charset.Charset;

import kernel_file_manager.file_reader;
import kernel_common_class.change_name;
import kernel_interface.switch_scene_server;
import kernel_common_class.http_date_string;
import kernel_common_class.debug_information;
import kernel_content_type.get_content_type_change_name;
import kernel_network.network_implementation_default_parameter;

public class system_parameter
{
	public String data_root_directory_name;
	public long last_modified_time;
	
	public String local_data_charset,network_data_charset;
	public String text_class_charset,text_jar_file_charset,js_class_charset,js_jar_file_charset;
	
	public String user_file_name,shader_file_name;
	public String parameter_directory,default_system_mount_component_name;
	
	public int default_max_loading_number,max_loading_number,max_material_id,max_method_number;
	
	public int response_block_size;
	
	public long scene_expire_time_length,scene_touch_time_length,part_load_sleep_time_length;
	
	public int create_scene_concurrent_number;
	public double create_scene_sleep_time_length_scale;
	public long create_scene_sleep_time_length,create_scene_max_sleep_time_length;
	
	public long show_process_bar_interval,file_buffer_expire_time_length,access_control_max_age;
	
	public int max_client_container_number,max_client_interface_number;
	
	public int max_scene_kernel_number,max_scene_component_number;
	
	public long max_buffer_object_head_package_length;
	public long max_file_response_length,min_compress_response_length;
	
	public int max_process_component_load_number,max_process_modifier_number;
	
	public double box_distance_difference_scale,buffer_data_length_difference_scale; 
	
	public String link_file_extend_name;
	public change_name scene_environment,language_change_name;
	public temporary_file_parameter temporary_file_par;
	public switch_scene_server		switch_server;
	public http_date_string 		http_date_str;
	
	private change_name content_type_change_name;
	public search_file_content_type_result search_file_content_type(String path_name)
	{
		int index_id;
		for(String link_token=null,str,ext_str,zip_link_str;;){
			if((index_id=path_name.lastIndexOf('.'))<0)
				break;
			ext_str=path_name.substring(index_id+1);
			if((str=content_type_change_name.search_change_name(ext_str,null))==null) 
				break;
			if((index_id=str.indexOf(':'))<0)
				break;
			if((zip_link_str=str.substring(0,index_id).trim()).compareTo("link")==0) {
				file_reader fr=new file_reader(path_name,local_data_charset);
				if((path_name=fr.get_string())!=null)
					if((path_name=path_name.trim()).length()>0) {
						fr.close();
						link_token=zip_link_str;
						continue;
					}			
				fr.close();
				break;
			}
			return new search_file_content_type_result(zip_link_str,
						str.substring(index_id+1).trim(),ext_str,link_token,path_name);
		};
		return null;
	}

	public system_parameter(system_parameter sp)
	{
		data_root_directory_name			=new String(sp.data_root_directory_name);
		
		last_modified_time					=sp.last_modified_time;

		local_data_charset					=new String(sp.local_data_charset);
		network_data_charset				=new String(sp.network_data_charset);
		text_class_charset					=new String(sp.text_class_charset);
		text_jar_file_charset				=new String(sp.text_jar_file_charset);
		js_class_charset					=new String(sp.js_class_charset);
		js_jar_file_charset					=new String(sp.js_jar_file_charset);
		
		user_file_name						=new String(sp.user_file_name);
		shader_file_name					=new String(sp.shader_file_name);
		parameter_directory					=new String(sp.parameter_directory);
		default_system_mount_component_name	=new String(sp.default_system_mount_component_name);
		
		default_max_loading_number			=sp.default_max_loading_number;
		max_loading_number					=sp.max_loading_number;
		max_method_number					=sp.max_method_number;
		
		max_material_id						=sp.max_material_id;
		
		response_block_size					=sp.response_block_size;
		
		scene_expire_time_length			=sp.scene_expire_time_length;
		scene_touch_time_length				=sp.scene_touch_time_length;
		part_load_sleep_time_length			=sp.part_load_sleep_time_length;
		
		create_scene_concurrent_number		=sp.create_scene_concurrent_number;
		create_scene_sleep_time_length_scale=sp.create_scene_sleep_time_length_scale;
		create_scene_sleep_time_length		=sp.create_scene_sleep_time_length;
		create_scene_max_sleep_time_length	=sp.create_scene_max_sleep_time_length;
		
		show_process_bar_interval			=sp.show_process_bar_interval;
		file_buffer_expire_time_length		=sp.file_buffer_expire_time_length;
		access_control_max_age				=sp.access_control_max_age;

		max_client_container_number			=sp.max_client_container_number;
		max_client_interface_number			=sp.max_client_interface_number;
		
		max_scene_kernel_number				=sp.max_scene_kernel_number;
		max_scene_component_number			=sp.max_scene_component_number;
		
		max_file_response_length			=sp.max_file_response_length;
		min_compress_response_length		=sp.min_compress_response_length;
		max_buffer_object_head_package_length=sp.max_buffer_object_head_package_length;
		
		max_process_component_load_number	=sp.max_process_component_load_number;
		max_process_modifier_number			=sp.max_process_modifier_number;
		
		box_distance_difference_scale		=sp.box_distance_difference_scale;
		buffer_data_length_difference_scale	=sp.buffer_data_length_difference_scale;
		
		link_file_extend_name				=sp.link_file_extend_name;
		scene_environment					=new change_name(sp.scene_environment,false);
		language_change_name				=new change_name(sp.language_change_name,false);
		content_type_change_name			=new change_name(sp.content_type_change_name,false);

		temporary_file_par					=sp.temporary_file_par;
		switch_server						=sp.switch_server;
		http_date_str						=sp.http_date_str;
	}
	public system_parameter(String scene_data_path_name,
			String scene_temparatory_path_name,String scene_environment_path_name)
	{
		debug_information.println();
		debug_information.println("data_file_configure_file_name:		",		scene_data_path_name);
		debug_information.println("temporary_file_configure_file_name:	",		scene_temparatory_path_name);
		debug_information.println("scene_environment_configure_file_name:	",	scene_environment_path_name);

		file_reader f=new file_reader(scene_data_path_name,Charset.defaultCharset().name());
		
		if(f.error_flag()){
			debug_information.println("Can't not open system_parameter file	",scene_data_path_name);
			debug_information.println("do System.exit(0)");
			System.exit(0);
			return;
		}
		
		if((local_data_charset=f.get_string())==null)
			local_data_charset=Charset.defaultCharset().name();
		else if(local_data_charset.compareTo("default_charset")==0)
			local_data_charset=Charset.defaultCharset().name();
		f.close();

		f=new file_reader(scene_data_path_name,local_data_charset);
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
		
		if((parameter_directory=f.get_string())==null)
			parameter_directory="";
		else {
			parameter_directory=file_reader.separator(parameter_directory.trim());
			int str_length=parameter_directory.length();
			if(parameter_directory.charAt(str_length-1)!=File.separatorChar)
				parameter_directory+=File.separatorChar;
			parameter_directory=f.directory_name+parameter_directory;
		}
		if((default_system_mount_component_name=f.get_string())==null)
			default_system_mount_component_name="default_system_mount_component";

		if(!(new File(scene_environment_path_name).exists())) {
			debug_information.println(
				"scene_environment file NOT exist:	",scene_environment_path_name);
			scene_environment=new change_name(null,null);
		}else {
			file_reader env_f=new file_reader(
				scene_environment_path_name,Charset.defaultCharset().name());
			String file_charset=env_f.get_string();
			env_f.close();
				
			env_f=new file_reader(scene_environment_path_name,file_charset);
			env_f.get_string();
			scene_environment=new change_name(new file_reader[] {env_f},null);
			env_f.close();
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

		default_max_loading_number				=f.get_int();
		max_loading_number						=f.get_int();
		max_method_number						=f.get_int();
		
		max_material_id							=f.get_int();
		
		response_block_size						=f.get_int();
		
		scene_expire_time_length				=f.get_long();
		scene_touch_time_length					=f.get_long();
		part_load_sleep_time_length				=f.get_long();
		
		create_scene_concurrent_number			=f.get_int();
		create_scene_sleep_time_length_scale	=f.get_double();
		create_scene_sleep_time_length			=f.get_long();
		create_scene_max_sleep_time_length		=f.get_long();
		
		show_process_bar_interval				=f.get_long();
		file_buffer_expire_time_length			=f.get_long();
		access_control_max_age					=f.get_long();
		
		max_client_container_number				=f.get_int();
		max_client_interface_number				=f.get_int();
		
		max_scene_kernel_number					=f.get_int();
		max_scene_component_number				=f.get_int();

		max_file_response_length				=f.get_long();
		min_compress_response_length			=f.get_long();
		max_buffer_object_head_package_length	=f.get_long();
		
		max_process_component_load_number		=f.get_int();
		max_process_modifier_number				=f.get_int();
		
		box_distance_difference_scale			=f.get_double();
		buffer_data_length_difference_scale		=f.get_double();
		
		f.close();
		
		temporary_file_par=new temporary_file_parameter(scene_temparatory_path_name,local_data_charset);
		language_change_name=new change_name(
				new String[]{data_root_directory_name+language_change_file_name},null,local_data_charset);
		content_type_change_name=get_content_type_change_name.get_change_name(text_class_charset,text_jar_file_charset);
		switch_server=new switch_scene_server(data_root_directory_name+switch_server_url_file_name,local_data_charset);
		http_date_str=new http_date_string();

		for(int i=0,ni=content_type_change_name.data_list.size();i<ni;i++){
			String p[]=content_type_change_name.data_list.get(i);
			if(p[1].indexOf("link:")==0){
				link_file_extend_name=p[0];
				break;
			}
		}
		return;
	}
}
