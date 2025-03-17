package kernel_engine;

import java.io.File;
import java.util.ArrayList;

import kernel_common_class.cut_string;
import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;

public class scene_parameter 
{
	public String change_part_string,part_type_string;
	
	public String type_sub_directory[],scene_sub_directory;

	public String directory_name,extra_directory_name,parameter_charset,extra_parameter_charset;
	public long parameter_last_modified_time,scene_last_modified_time;
	
	public String scene_temporary_directory_name;
	public String type_shader_directory_name,type_shader_file_name;
	
	public String scene_shader_directory_name,scene_shader_file_name;
	public String camera_file_name;
	
	public change_name change_component_name,client_parameter_name;
	
	public int part_lru_in_list_number;
	
	public String inserted_component_name,inserted_part_name;
	public int inserted_component_and_part_id,max_child_number;
	
	public String scene_cors_string;
	
	public int multiparameter_number;
	public long default_display_bitmap;
	
	public String scene_component_name;
	
	public String component_collector_stack_file_name;
	public int component_collector_parameter_channel_id[];
	public int max_component_collector_number;

	public int max_camera_return_stack_number,max_modifier_container_number;
	
	public int max_target_number;
	
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
	
	public boolean not_do_ancestor_render_flag,fast_load_flag;
	
	private String[] get_directory_name_and_file_name(file_reader fr)
	{
		String path_file_name=((path_file_name=fr.get_string())==null)?"":path_file_name.trim();
		String path_directory_name=((path_directory_name=fr.get_string())==null)?"relative_directory":path_directory_name.trim();
		
		switch(path_directory_name.toLowerCase()){
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
				if((path_directory_name=System.getenv(path_directory_name))!=null)
					if((path_directory_name=file_reader.separator(path_directory_name.trim())).length()>0) {
						if(path_directory_name.charAt(path_directory_name.length()-1)!=File.separatorChar)
							path_directory_name+=File.separatorChar;
						break;
					}
			path_directory_name=directory_name;
			break;
		}
		return new String[] 
		{
			path_directory_name,
			file_reader.separator(path_file_name)
		};
	}
	
	private void get_type_sub_directory(client_request_response request_response)
	{
		String my_str,str;
		if((str=request_response.get_parameter("type_sub_directory"))==null) {
			type_sub_directory=new String[] {};
			return;
		}
		ArrayList<String> list=new ArrayList<String>();
		for(int index_id;((str=str.trim()).length()>0);){
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
			if((my_str=cut_string.do_cut(my_str)).length()<=0)
				continue;
			my_str+=File.separator;
			for(int j=0,nj=list.size();j<nj;j++)
				if(list.get(j).compareTo(my_str)==0) {
					my_str=null;
					break;
				}
			if(my_str!=null)
				list.add(my_str);
		}
		type_sub_directory=list.toArray(new String[list.size()]);
	}
	private void get_client_parameter_name(
		file_reader parameter_fr,client_request_response request_response)
	{
		ArrayList<String[]> my_client_parameter_name=new ArrayList<String[]>();
		for(String parameter_name,parameter_value;!(parameter_fr.eof());) {
			if((parameter_name=parameter_fr.get_string())==null)
				continue;
			if((parameter_name=cut_string.do_cut(parameter_name.trim())).length()<=0)
				continue;
			if((parameter_value=request_response.get_parameter(parameter_name))==null)
				continue;
			if((parameter_value=parameter_value.trim()).length()<=0)
				continue;
			my_client_parameter_name.add(new String[]{parameter_name,parameter_value});
		}
		client_parameter_name=new change_name();
		client_parameter_name.data_array=my_client_parameter_name.toArray(
				new String[my_client_parameter_name.size()][]);
		client_parameter_name.do_sort();
	}
	private void caculate_scene_temporary_directory_name(
			String scene_name,client_request_response request_response,
			String change_component_string,system_parameter system_par)
	{
		String str;
		
		scene_temporary_directory_name=system_par.temporary_file_par.temporary_root_directory_name;
		scene_temporary_directory_name+="scene_directory"+File.separator;
		
		if((str=request_response.get_parameter("scene_tmp_directory"))!=null)
			if((str=cut_string.do_cut(str)).length()>0){
				scene_temporary_directory_name+=file_reader.separator(str)+File.separator;
				return;
			}
		scene_temporary_directory_name+=file_reader.separator(scene_name)+File.separator;
		
		String my_temporary_directory_name="";
		
		for(int i=0,ni=type_sub_directory.length;i<ni;i++)
			for(str=type_sub_directory[i];str.length()>0;str=str.substring(1))
				if(str.charAt(0)!=File.separatorChar){
					my_temporary_directory_name+=str;
					break;
				}
		if(scene_sub_directory.length()>0){
			if(scene_sub_directory.charAt(0)==File.separatorChar)
				my_temporary_directory_name+=scene_sub_directory.substring(1);
			else
				my_temporary_directory_name+=scene_sub_directory;
		}
		
		String str_array[]={change_part_string,change_component_string,part_type_string};
		for(int str_len,i=0,ni=str_array.length;i<ni;i++) {
			if(str_array[i]==null)
				continue;
			str_array[i]=new String(str_array[i]).replace(':','/').replace(';','/').
					replace('/',File.separatorChar).replace('\\',File.separatorChar).
					replace(" ", "").replace("\t","").replace("\r","").replace("\n","");
			str_array[i]=file_reader.separator(str_array[i]);
			if((str_len=str_array[i].length())<=0)
				continue;
			if(str_array[i].charAt(str_len-1)!=File.separatorChar)
				str_array[i]+=File.separatorChar;
			my_temporary_directory_name+=str_array[i];
		}
		for(int i=0,ni=client_parameter_name.get_number();i<ni;i++) {
			str =client_parameter_name.data_array[i][0]+File.separatorChar;
			str+=client_parameter_name.data_array[i][1]+File.separatorChar;
			my_temporary_directory_name+=file_reader.separator(str);
		}
		
		if(my_temporary_directory_name.length()<=0)
			my_temporary_directory_name="no_parameter_directory"+File.separatorChar;;
		
		scene_temporary_directory_name+=my_temporary_directory_name;
	}
	
	public scene_parameter(
			String my_scene_name,client_request_response request_response,
			system_parameter system_par,engine_kernel_create_parameter ekcp)
	{
		String str;

		if((change_part_string=request_response.get_parameter("change_part"))==null)
			change_part_string="";
		else
			change_part_string=change_part_string.trim();
		
		String change_component_string=request_response.get_parameter("change_component");
		change_component_string=(change_component_string==null)?"":change_component_string.trim();

		if((part_type_string=request_response.get_parameter("part_type"))==null)
			part_type_string="";
		else
			part_type_string=part_type_string.trim();
		
		get_type_sub_directory(request_response);

		if((scene_sub_directory=request_response.get_parameter("scene_sub_directory"))==null)
			scene_sub_directory="";
		else if((scene_sub_directory=file_reader.separator(scene_sub_directory.trim())).length()>0)
				if(scene_sub_directory.charAt(scene_sub_directory.length()-1)!=File.separatorChar)
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

		String gdnafa[];
		gdnafa=get_directory_name_and_file_name(parameter_fr);
		type_shader_directory_name	=gdnafa[0];
		type_shader_file_name		=gdnafa[1];

		gdnafa=get_directory_name_and_file_name(parameter_fr);
		scene_shader_directory_name	=gdnafa[0];
		scene_shader_file_name		=scene_sub_directory+gdnafa[1];
		
		gdnafa=get_directory_name_and_file_name(parameter_fr);
		change_component_name=new change_name(
			new String[]{gdnafa[0]+scene_sub_directory+gdnafa[1]},
			change_component_string,parameter_fr.get_charset());
		
		if((camera_file_name=parameter_fr.get_string())==null)
			camera_file_name="";
		else
			camera_file_name=file_reader.separator(camera_file_name);
		
		get_client_parameter_name(parameter_fr,request_response);
		
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
		
		if((scene_cors_string=extra_parameter_fr.get_string())==null)
			scene_cors_string="*";
		else if(new File(str=extra_parameter_fr.directory_name+file_reader.separator(scene_cors_string)).exists()?false:true)
			scene_cors_string=scene_cors_string.trim();
		else{
			scene_cors_string="";
			for(file_reader cors_fr=new file_reader(str,extra_parameter_fr.get_charset());;) {
				if(cors_fr.eof()){
					cors_fr.close();
					break;
				}
				if((str=cors_fr.get_line())!=null)
					if((str=str.trim()).length()>0)
						scene_cors_string+=str;
			}
		}

		if((multiparameter_number=extra_parameter_fr.get_int())<1)
			multiparameter_number=1;
		
		default_display_bitmap=extra_parameter_fr.get_long();
		
		if((scene_component_name=extra_parameter_fr.get_string())==null)
			scene_component_name="";
		
		if((component_collector_stack_file_name=extra_parameter_fr.get_string())==null)
			component_collector_stack_file_name="";
		else
			component_collector_stack_file_name=file_reader.separator(component_collector_stack_file_name);
		
		component_collector_parameter_channel_id=new int[extra_parameter_fr.get_int()];
		for(int i=0,ni=component_collector_parameter_channel_id.length;i<ni;i++)
			component_collector_parameter_channel_id[i]=extra_parameter_fr.get_int();
		
		if((max_component_collector_number=extra_parameter_fr.get_int())<=0)
			max_component_collector_number=1;
		if((max_camera_return_stack_number=extra_parameter_fr.get_int())<=0)
			max_camera_return_stack_number=1;
		if((max_modifier_container_number=extra_parameter_fr.get_int())<=0)
			max_modifier_container_number=1;
		
		if((max_target_number=extra_parameter_fr.get_int())<=0)
			max_target_number=1;
		
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

		if((str=extra_parameter_fr.get_string())==null)
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

		component_sort_min_distance				=extra_parameter_fr.get_double();
		
		not_do_ancestor_render_flag				=extra_parameter_fr.get_boolean();
		
		fast_load_flag=request_response.get_boolean("fast_load",true);

		extra_parameter_fr.close();

		return;
	}
}
