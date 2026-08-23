package kernel_scene;

import java.io.File;

import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_directory;
import kernel_common_class.debug_information;
import kernel_common_class.name_exist_tester;
import kernel_network.client_request_response;

public class scene_parameter 
{
	public change_name scene_environment;
	
	public String change_part_string,part_type_string;
	
	public String type_sub_directory[],scene_sub_directory;

	public String directory_name,extra_directory_name,parameter_charset,extra_parameter_charset;
	public long parameter_last_modified_time,scene_last_modified_time;
	
	public String scene_temporary_directory_name;
	public String type_shader_directory_name,type_shader_file_name;
	
	public String scene_shader_directory_name,scene_shader_file_name;
	public String camera_file_name;

	public change_name change_component_name;
	
	public int part_lru_in_list_number;
	
	public String inserted_component_name,inserted_part_name;
	public int inserted_component_and_part_id,max_child_number;
	
	public int multiparameter_number;
	public long default_display_bitmap;
	
	public String scene_component_name;
	
	public String component_collector_stack_file_name;
	public int component_collector_parameter_channel_id[];
	public int max_component_collector_number;

	public int max_camera_return_stack_number,max_modifier_container_number;
	
	public int max_target_number,multisample_number;
	
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
	public String component_sort_type;
	public double component_sort_min_distance;
	
	public boolean not_do_ancestor_render_flag;
	
	private void setup_scene_environment(
			file_reader parameter_fr,file_reader extra_parameter_fr,
			client_request_response request_response,system_parameter system_par)
	{
		String file_name,path_name;
		file_reader environment_file_reader[]={null,null,null};
		
		path_name=system_par.data_root_directory_name+system_par.environment_file_name;
		if(new File(path_name).exists())
			environment_file_reader[0]=new file_reader(path_name,system_par.local_data_charset);
		else
			debug_information.println("system scene_environment file NOT exist:	",path_name);
		
		file_name=parameter_fr.get_string();
		file_name=(file_name==null)?"":file_name.trim();
		path_name=parameter_fr.directory_name+file_directory.replace_special_char(file_name);
		if(new File(path_name).exists())
			environment_file_reader[1]=new file_reader(path_name,parameter_fr.get_charset());
		else
			debug_information.println("parameter scene_environment file NOT exist:	",path_name);
		
		file_name=extra_parameter_fr.get_string();
		file_name=(file_name==null)?"":file_name.trim();
		path_name=extra_parameter_fr.directory_name+file_directory.replace_special_char(file_name);
		if(new File(path_name).exists())
			environment_file_reader[2]=new file_reader(path_name,extra_parameter_fr.get_charset());
		else
			debug_information.println("extra parameter system scene_environment file NOT exist:	",path_name);
		
		scene_environment=new change_name();
		
		for(int i=0,ni=environment_file_reader.length;i<ni;i++)
			if(environment_file_reader[i]!=null) {
				while(!(environment_file_reader[i].eof())) {
					String	parameter_name	=environment_file_reader[i].get_string();
					String	parameter_value	=environment_file_reader[i].get_string();
					boolean	parameter_flag	=environment_file_reader[i].get_boolean();
					if((parameter_name==null)||(parameter_value==null))
						continue;
					if((parameter_name=parameter_name.trim()).length()<=0)
						continue;
					if((parameter_value=parameter_value.trim()).length()<=0)
						continue;
					if(parameter_flag) {
						if((parameter_value=request_response.get_parameter(parameter_value))==null)
							continue;
						if((parameter_value=parameter_value.trim()).length()<=0)
							continue;
					}
					scene_environment.add(parameter_name,parameter_value);
				}
				environment_file_reader[i].close();	
			}
	}
	private String[] get_directory_name_and_file_name(file_reader fr,system_parameter system_par)
	{
		String path_file_name=((path_file_name=fr.get_string())==null)
				?"":file_directory.replace_special_char(path_file_name);
		String path_directory_name=((path_directory_name=fr.get_string())==null)
				?"relative_directory":path_directory_name.trim();
		
		switch(path_directory_name){
		default:
		case "relative_directory":
			path_directory_name=directory_name;
			break;
		case "extra_relative_directory":
			path_directory_name=extra_directory_name;
			break;
		case "absolute_directory":
			path_directory_name="";
			break;
		case "environment_directory":
			if((path_directory_name=fr.get_string())!=null)
				if((path_directory_name=scene_environment.search_change_name(path_directory_name,null))!=null) {
					path_directory_name=file_directory.replace_special_char(path_directory_name);
					if(path_directory_name.length()>0) {
						if(path_directory_name.charAt(path_directory_name.length()-1)!=File.separatorChar)
							path_directory_name+=File.separatorChar;
						break;
					}
				}
			path_directory_name=directory_name;
			break;
		}
		return new String[]{path_directory_name,path_file_name};
	}
	private void get_type_sub_directory(client_request_response request_response)
	{
		String my_str,str;
		if((str=request_response.get_parameter("type_sub_directory"))==null) {
			type_sub_directory=new String[] {};
			return;
		}
		name_exist_tester tester=new name_exist_tester();
		for(int index_id;str.length()>0;){
			if((index_id=str.indexOf(';'))==0){
				str=str.substring(1);
				continue;
			}
			if(index_id<0){
				my_str=str;
				str="";
			}else {	
				my_str=str.substring(0,index_id);
				str=str.substring(index_id+1);
			}
			my_str=file_directory.replace_special_char(my_str);
			if(my_str.length()<=0)
				continue;
			if(my_str.charAt(my_str.length()-1)!=File.separatorChar)
				my_str+=File.separatorChar;
			
			tester.add(my_str);
		}
		type_sub_directory=tester.name_array();
	}
	
	private void caculate_scene_temporary_directory_name(
			String scene_name,client_request_response request_response,
			String change_component_string,system_parameter system_par)
	{
		String str;
		
		scene_temporary_directory_name =system_par.temporary_file_par.temporary_root_directory_name;
		scene_temporary_directory_name+="scene_directory"+File.separator;
		
		if((str=request_response.get_parameter("scene_tmp_directory"))!=null)
			if((str=file_directory.replace_special_char(str)).length()>0){
				str=str.replace(':',File.separatorChar);
				if(str.charAt(str.length()-1)!=File.separatorChar)
					str+=File.separatorChar;
				scene_temporary_directory_name+=str;
				return;
			}

		if((str=file_directory.replace_special_char(scene_name)).length()>0) {
			str=str.replace(':',File.separatorChar);
			if(str.charAt(str.length()-1)!=File.separatorChar)
				str+=File.separatorChar;
			scene_temporary_directory_name+=str;
		}
		String my_temporary_directory_name="";
		
		for(int i=0,ni=type_sub_directory.length;i<ni;i++) 
			if((str=file_directory.replace_special_char(type_sub_directory[i])).length()>0){
				str=str.replace(':',File.separatorChar);
				if(str.charAt(str.length()-1)!=File.separatorChar)
					str+=File.separatorChar;
				scene_temporary_directory_name+=str;
			}
		if((str=file_directory.replace_special_char(scene_sub_directory)).length()>0){
			str=str.replace(':',File.separatorChar);
			if(str.charAt(str.length()-1)!=File.separatorChar)
				str+=File.separatorChar;
			scene_temporary_directory_name+=str;
		}
		
		String str_array[]={change_part_string,change_component_string,part_type_string};
		for(int i=0,ni=str_array.length;i<ni;i++)
			if((str=str_array[i])!=null)
				if((str=file_directory.replace_special_char(str)).length()>0) {
					str=str.replace(':',File.separatorChar);
					if(str.charAt(str.length()-1)!=File.separatorChar)
						str+=File.separatorChar;
					scene_temporary_directory_name+=str;
				}

		for(var my_node:scene_environment.tree_get_node_collection()) {
			if((str=file_directory.replace_special_char(my_node.key)).length()>0){
				str=str.replace(':',File.separatorChar);
				if(str.charAt(str.length()-1)!=File.separatorChar)
					str+=File.separatorChar;
				scene_temporary_directory_name+=str;
			}
			for(var my_list_item:my_node.list)
				if((str=file_directory.replace_special_char(my_list_item)).length()>0){
					str=str.replace(':',File.separatorChar);
					if(str.charAt(str.length()-1)!=File.separatorChar)
						str+=File.separatorChar;
					scene_temporary_directory_name+=str;
				}
		}
		
		if(my_temporary_directory_name.length()<=0)
			my_temporary_directory_name="no_parameter_directory"+File.separatorChar;
		
		scene_temporary_directory_name+=my_temporary_directory_name;
	}
	public scene_parameter(
			String my_scene_name,client_request_response request_response,
			system_parameter system_par,scene_kernel_create_parameter ekcp)
	{
		change_part_string=request_response.get_parameter("change_part");
		change_part_string=(change_part_string==null)?"":change_part_string.trim();
		
		String change_component_string=request_response.get_parameter("change_component");
		change_component_string=(change_component_string==null)?"":change_component_string.trim();

		if((part_type_string=request_response.get_parameter("part_type"))==null)
			part_type_string="";
		else
			part_type_string=part_type_string.trim();
		
		get_type_sub_directory(request_response);

		if((scene_sub_directory=request_response.get_parameter("scene_sub_directory"))==null)
			scene_sub_directory="";
		else if((scene_sub_directory=file_directory.replace_special_char(scene_sub_directory)).length()<=0)
			scene_sub_directory="";
		else if(scene_sub_directory.charAt(scene_sub_directory.length()-1)!=File.separatorChar)
			scene_sub_directory+=File.separator;

		file_reader parameter_fr=new file_reader(ekcp.parameter_file_name,ekcp.parameter_charset);
		
		if((parameter_fr.error_flag())||(parameter_fr.eof()))
			debug_information.println("Open assemble configure file fail : ",ekcp.parameter_file_name);
		directory_name=parameter_fr.directory_name;
		parameter_charset=parameter_fr.get_charset();
		
		file_reader extra_parameter_fr=new file_reader(ekcp.extra_parameter_file_name,ekcp.extra_parameter_charset);
		if((extra_parameter_fr.error_flag())||(extra_parameter_fr.eof()))
			debug_information.println("Open assemble extra configure file fail : ",ekcp.extra_parameter_file_name);
		extra_directory_name=extra_parameter_fr.directory_name;
		extra_parameter_charset=extra_parameter_fr.get_charset();

		parameter_last_modified_time=system_par.last_modified_time;
		if(parameter_last_modified_time<parameter_fr.lastModified_time)
			parameter_last_modified_time=parameter_fr.lastModified_time;
		if(parameter_last_modified_time<extra_parameter_fr.lastModified_time)
			parameter_last_modified_time=extra_parameter_fr.lastModified_time;
		if(parameter_last_modified_time<ekcp.scene_list_file_last_modified_time)
			parameter_last_modified_time=ekcp.scene_list_file_last_modified_time;
		
		scene_last_modified_time=parameter_last_modified_time;
		
		setup_scene_environment(parameter_fr,extra_parameter_fr,request_response,system_par);

		String gdnafa[];
		
		gdnafa=get_directory_name_and_file_name(parameter_fr,system_par);
		type_shader_directory_name	=gdnafa[0];
		type_shader_file_name		=gdnafa[1];

		gdnafa=get_directory_name_and_file_name(parameter_fr,system_par);
		scene_shader_directory_name	=gdnafa[0];
		scene_shader_file_name		=scene_sub_directory+gdnafa[1];
		
		gdnafa=get_directory_name_and_file_name(parameter_fr,system_par);
		change_component_name=new change_name(
			new String[]{gdnafa[0]+scene_sub_directory+gdnafa[1]},
			change_component_string,parameter_fr.get_charset());
		
		if((camera_file_name=parameter_fr.get_string())==null)
			camera_file_name="";
		else
			camera_file_name=file_directory.replace_special_char(camera_file_name);

		caculate_scene_temporary_directory_name(my_scene_name,
				request_response,change_component_string,system_par);
		parameter_fr.close();

		part_lru_in_list_number=extra_parameter_fr.get_int();
		
		if((inserted_component_name=extra_parameter_fr.get_string())==null)
			inserted_component_name="";
		if((inserted_part_name=extra_parameter_fr.get_string())==null)
			inserted_part_name="";
		inserted_component_and_part_id=0;
		max_child_number=extra_parameter_fr.get_int();
		
		if((multiparameter_number=extra_parameter_fr.get_int())<1)
			multiparameter_number=1;
		
		default_display_bitmap=extra_parameter_fr.get_long();
		
		if((scene_component_name=extra_parameter_fr.get_string())==null)
			scene_component_name="";

		if((component_collector_stack_file_name=extra_parameter_fr.get_string())==null)
			component_collector_stack_file_name="";
		else
			component_collector_stack_file_name=file_directory.
				replace_special_char(component_collector_stack_file_name);
		
		component_collector_parameter_channel_id=new int[extra_parameter_fr.get_int()];
		for(int i=0,ni=component_collector_parameter_channel_id.length;i<ni;i++)
			component_collector_parameter_channel_id[i]=extra_parameter_fr.get_int();
		
		if((max_component_collector_number=extra_parameter_fr.get_int())<=0)
			max_component_collector_number=1;
		if((max_camera_return_stack_number=extra_parameter_fr.get_int())<=0)
			max_camera_return_stack_number=1;
		if((max_modifier_container_number=extra_parameter_fr.get_int())<=0)
			max_modifier_container_number=1;
		
		if((max_target_number=extra_parameter_fr.get_int())<4)
			max_target_number=4;
		if((multisample_number=extra_parameter_fr.get_int())<4)
			multisample_number=4;
		
		create_top_part_assembly_precision2		=extra_parameter_fr.get_double();
		create_top_part_assembly_precision2		*=create_top_part_assembly_precision2;
		create_top_part_discard_precision2		=extra_parameter_fr.get_double();
		create_top_part_discard_precision2		*=create_top_part_discard_precision2;
		discard_top_part_component_precision2	=extra_parameter_fr.get_double();
		discard_top_part_component_precision2	*=discard_top_part_component_precision2;
		
		touch_time_length					=extra_parameter_fr.get_long();
		
		most_component_delete_number		=extra_parameter_fr.get_int();
		most_component_append_number		=extra_parameter_fr.get_int();
		most_update_parameter_number		=extra_parameter_fr.get_int();
		most_update_location_number			=extra_parameter_fr.get_int();
		
		display_precision					=extra_parameter_fr.get_int();

		display_assemble_depth				=extra_parameter_fr.get_int();

		component_sort_type=extra_parameter_fr.get_string();
		component_sort_type=(component_sort_type==null)?"xyz":(component_sort_type.trim().toLowerCase());
		
		component_sort_min_distance			=extra_parameter_fr.get_double();
		
		not_do_ancestor_render_flag			=extra_parameter_fr.get_boolean();
		
		extra_parameter_fr.close();
		
		return;
	}
}
