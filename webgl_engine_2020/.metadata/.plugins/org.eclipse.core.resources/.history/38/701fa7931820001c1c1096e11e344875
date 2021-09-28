package kernel_engine;

import java.io.File;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;

public class scene_parameter 
{
	public String change_part_string,change_component_string,mount_component_string,part_type_string;

	public String directory_name,extra_directory_name;
	public String parameter_charset,extra_parameter_charset;
	public long parameter_last_modified_time,scene_last_modified_time;
	
	public String scene_sub_directory;
	
	public String type_proxy_directory_name,scene_proxy_directory_name;
	public String type_shader_file_name,scene_shader_file_name,camera_file_name;
	public String change_part_file_name,change_component_file_name,mount_component_file_name;
	public String type_string_file_name;
	
	public String proxy_initialization_file_name;
	
	public String inserted_component_name,inserted_part_name;
	public int inserted_component_and_part_id,max_child_number;
	
	public String scene_cors_string;
	
	public int multiparameter_number;
	public int initial_parameter_channel_id;
	public long default_display_bitmap;
	
	public String component_collector_stack_file_name;
	public int component_collector_parameter_channel_id[];
	public int max_component_collector_number;

	public int max_camera_return_stack_number,max_modifier_container_number;
	
	public double create_top_part_assembly_precision2;
	public double create_top_part_discard_precision2;
	public double discard_top_part_component_precision2;
	
	public long touch_time_length;
	public int most_component_delete_number;
	public int most_component_append_number;
	public int most_update_parameter_number;
	public int most_update_location_number;

	public int display_precision;
	
	public int display_assemble_depth;
	public int component_sort_type;
	public double component_sort_min_distance;
	
	public boolean not_do_ancestor_render_flag,do_discard_lod_flag,do_selection_lod_flag,scene_fast_load_flag;
	
	public long proxy_response_length,compress_response_length;
	
	public scene_parameter(client_request_response request_response,
			String scene_name,system_parameter system_par,String my_scene_sub_directory,
			String my_parameter_file_name,String my_parameter_charset,
			String my_extra_parameter_file_name,String my_extra_parameter_charset,
			long my_scene_list_file_last_modified_time)
	{
		String str;
		
		if((change_part_string=request_response.get_parameter("change_part"))==null)
			change_part_string="";
		else
			change_part_string=change_part_string.trim();
		if((change_component_string=request_response.get_parameter("change_component"))==null)
			change_component_string="";
		if((mount_component_string=request_response.get_parameter("mount_component"))==null)
			mount_component_string="";
		if((part_type_string=request_response.get_parameter("part_type"))==null)
			part_type_string="";
		
		if((scene_sub_directory=my_scene_sub_directory)==null)
			scene_sub_directory="";
		else if((scene_sub_directory=file_reader.separator(scene_sub_directory.trim())).length()>0)
			if(scene_sub_directory.charAt(scene_sub_directory.length()-1)!=File.separatorChar)
				scene_sub_directory+=File.separator;
		
		file_reader f=new file_reader(my_parameter_file_name,my_parameter_charset);
		
		if((f.error_flag())||(f.eof()))
			debug_information.println("Open assemble configure file fail : ",my_parameter_file_name);
		
		directory_name=f.directory_name;
		parameter_charset=f.get_charset();
		if((parameter_last_modified_time=f.lastModified_time)<system_par.last_modified_time)
			parameter_last_modified_time=system_par.last_modified_time;
		if(parameter_last_modified_time<my_scene_list_file_last_modified_time)
			parameter_last_modified_time=my_scene_list_file_last_modified_time;

		if((type_proxy_directory_name=f.get_string())==null)
			type_proxy_directory_name="no_directory";
		else
			type_proxy_directory_name=file_reader.separator(type_proxy_directory_name);
		type_proxy_directory_name=system_par.proxy_par.proxy_data_root_directory_name
				+"scene_directory"+File.separator+type_proxy_directory_name+File.separator;
		
		scene_proxy_directory_name=type_proxy_directory_name+file_reader.separator(scene_name);
		if(scene_sub_directory.length()<=0)
			scene_proxy_directory_name+=File.separator;
		else if(scene_sub_directory.charAt(0)==File.separatorChar)
			scene_proxy_directory_name+=scene_sub_directory;
		else
			scene_proxy_directory_name+=File.separator+scene_sub_directory;
		String str_array[]={change_part_string,change_component_string,mount_component_string,part_type_string};
		for(int str_len,i=0,ni=str_array.length;i<ni;i++) {
			if(str_array[i]==null)
				continue;
			str_array[i]=str_array[i].replace(':','/').replace(';','/').
					replace(" ", "").replace("\t","").replace("\r","").replace("\n","");
			str_array[i]=file_reader.separator(str_array[i]);
			if((str_len=str_array[i].length())<=0)
				continue;
			if(str_array[i].charAt(str_len-1)!=File.separatorChar)
				str_array[i]+=File.separatorChar;
			scene_proxy_directory_name+=str_array[i];
		}

		if((type_shader_file_name=f.get_string())==null)
			type_shader_file_name="";
		else
			type_shader_file_name=file_reader.separator(type_shader_file_name);
		
		if((scene_shader_file_name=f.get_string())==null)
			scene_shader_file_name="";
		else
			scene_shader_file_name=file_reader.separator(scene_shader_file_name);
		
		if((camera_file_name=f.get_string())==null)
			camera_file_name="";
		else
			camera_file_name=file_reader.separator(camera_file_name);
		
		if((change_part_file_name=f.get_string())==null)
			change_part_file_name="";
		else
			change_part_file_name=file_reader.separator(change_part_file_name);
		
		if((change_component_file_name=f.get_string())==null)
			change_component_file_name="";
		else
			change_component_file_name=file_reader.separator(change_component_file_name);
		
		if((mount_component_file_name=f.get_string())==null)
			mount_component_file_name="";
		else
			mount_component_file_name=file_reader.separator(mount_component_file_name);
		
		if((type_string_file_name=f.get_string())==null)
			type_string_file_name="";
		else
			type_string_file_name=file_reader.separator(type_string_file_name);
		
		f.close();
		
		f=new file_reader(my_extra_parameter_file_name,my_extra_parameter_charset);
		if(parameter_last_modified_time<f.lastModified_time)
			parameter_last_modified_time=f.lastModified_time;
		
		scene_last_modified_time=parameter_last_modified_time;
		
		extra_directory_name=f.directory_name;
		extra_parameter_charset=f.get_charset();

		if((proxy_initialization_file_name=f.get_string())==null)
			proxy_initialization_file_name="";
		else
			proxy_initialization_file_name=file_reader.separator(proxy_initialization_file_name);
		
		if((inserted_component_name=f.get_string())==null)
			inserted_component_name="";
		if((inserted_part_name=f.get_string())==null)
			inserted_part_name="";
		inserted_component_and_part_id=0;
		max_child_number=f.get_int();
		
		if((scene_cors_string=f.get_string())==null)
			scene_cors_string="*";
		else{
			scene_cors_string=file_reader.separator(scene_cors_string);
			if((new File(str=f.directory_name+scene_cors_string)).exists()) {
				scene_cors_string="";
				for(file_reader cors_f=new file_reader(str,f.get_charset());;) {
					if(cors_f.eof()){
						cors_f.close();
						break;
					}
					if((str=cors_f.get_line())!=null)
						if((str=str.trim()).length()>0)
							scene_cors_string+=str;
				}
			}
		}
		if((multiparameter_number=f.get_int())<1)
			multiparameter_number=1;
		
		if((initial_parameter_channel_id=f.get_int())<0)
			initial_parameter_channel_id=0;
		initial_parameter_channel_id%=multiparameter_number;
		
		default_display_bitmap=f.get_long();
		
		if((component_collector_stack_file_name=f.get_string())==null)
			component_collector_stack_file_name="";
		else
			component_collector_stack_file_name=file_reader.separator(component_collector_stack_file_name);
		
		component_collector_parameter_channel_id=new int[f.get_int()];
		for(int i=0,ni=component_collector_parameter_channel_id.length;i<ni;i++)
			component_collector_parameter_channel_id[i]=f.get_int();
		
		if((max_component_collector_number=f.get_int())<=0)
			max_component_collector_number=1;
		if((max_camera_return_stack_number=f.get_int())<=0)
			max_camera_return_stack_number=1;
		if((max_modifier_container_number=f.get_int())<=0)
			max_modifier_container_number=1;
	
		create_top_part_assembly_precision2		=f.get_double();
		create_top_part_assembly_precision2		*=create_top_part_assembly_precision2;
		create_top_part_discard_precision2		=f.get_double();
		create_top_part_discard_precision2		*=create_top_part_discard_precision2;
		discard_top_part_component_precision2	=f.get_double();
		discard_top_part_component_precision2	*=discard_top_part_component_precision2;
		
		touch_time_length					=f.get_long();
		
		most_component_delete_number		=f.get_int();
		most_component_append_number		=f.get_int();
		most_update_parameter_number		=f.get_int();
		most_update_location_number			=f.get_int();
		
		display_precision					=f.get_int();

		display_assemble_depth				=f.get_int();

		if((str=f.get_string())==null)
			component_sort_type=0;
		else if((str=str.toLowerCase().trim()).compareTo("xyz")==0)
			component_sort_type=0;
		else if(str.compareTo("xzy")==0)
			component_sort_type=1;
		else if(str.compareTo("yxz")==0)
			component_sort_type=2;
		else if(str.compareTo("yzx")==0)
			component_sort_type=3;
		else if(str.compareTo("zxy")==0)
			component_sort_type=4;
		else if(str.compareTo("zyx")==0)
			component_sort_type=5;
		else
			component_sort_type=0;

		component_sort_min_distance				=f.get_double();
		
		proxy_response_length					=f.get_long();
		compress_response_length				=f.get_long();
		
		not_do_ancestor_render_flag				=f.get_boolean();
		do_discard_lod_flag						=f.get_boolean();
		do_selection_lod_flag					=f.get_boolean();
		scene_fast_load_flag					=f.get_boolean();
		
		f.close();

		return;
	}
}
