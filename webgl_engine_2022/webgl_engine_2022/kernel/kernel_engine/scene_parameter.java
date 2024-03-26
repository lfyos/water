package kernel_engine;

import java.io.File;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;

public class scene_parameter 
{
	public String change_part_string,change_component_string,mount_component_string,part_type_string;
	
	public String scene_sub_directory;

	public String directory_name,extra_directory_name,parameter_charset,extra_parameter_charset;
	public long parameter_last_modified_time,scene_last_modified_time;
	
	public String type_temporary_directory_name,scene_temporary_directory_name;
	public String type_shader_directory_name,type_shader_file_name;
	public String scene_shader_directory_name,scene_shader_file_name;
	public String camera_file_name;
	public String change_part_file_name,change_component_file_name;
	public String type_string_file_name;
	
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

	public scene_parameter(client_request_response request_response,
			system_parameter system_par,engine_kernel_create_parameter ekcp)
	{
		
		String str,request_charset=request_response.implementor.get_request_charset();

		if((change_part_string=request_response.get_parameter("change_part"))==null)
			change_part_string="";
		else
			try {
				change_part_string=java.net.URLDecoder.decode(change_part_string,request_charset);
				change_part_string=java.net.URLDecoder.decode(change_part_string,request_charset);
				change_part_string=change_part_string.trim();
			}catch(Exception e) {
				;
			}

		if((change_component_string=request_response.get_parameter("change_component"))==null)
			change_component_string="";
		else
			try {
				change_component_string=java.net.URLDecoder.decode(change_component_string,request_charset);
				change_component_string=java.net.URLDecoder.decode(change_component_string,request_charset);
				change_component_string=change_component_string.trim();
			}catch(Exception e) {
				;
			}
		if((mount_component_string=request_response.get_parameter("mount_component"))==null)
			mount_component_string="";
		else
			try {
				mount_component_string=java.net.URLDecoder.decode(mount_component_string,request_charset);
				mount_component_string=java.net.URLDecoder.decode(mount_component_string,request_charset);
				mount_component_string=mount_component_string.trim();
			}catch(Exception e) {
				;
			}
		if((part_type_string=request_response.get_parameter("part_type"))==null)
			part_type_string="";
		else
			try {
				part_type_string=java.net.URLDecoder.decode(part_type_string,request_charset);
				part_type_string=java.net.URLDecoder.decode(part_type_string,request_charset);
				part_type_string=part_type_string.trim();
			}catch(Exception e) {
				;
			}
		if((scene_sub_directory=request_response.get_parameter("sub_directory"))==null)
			scene_sub_directory="";
		else{
			try{
				scene_sub_directory=java.net.URLDecoder.decode(scene_sub_directory,request_charset);
				scene_sub_directory=java.net.URLDecoder.decode(scene_sub_directory,request_charset);
				scene_sub_directory=scene_sub_directory.trim();
			}catch(Exception e) {
			}
			if((scene_sub_directory=file_reader.separator(scene_sub_directory.trim())).length()>0)
				if(scene_sub_directory.charAt(scene_sub_directory.length()-1)!=File.separatorChar)
					scene_sub_directory+=File.separator;
		}
		file_reader fr=new file_reader(ekcp.parameter_file_name,ekcp.parameter_charset);
		
		if((fr.error_flag())||(fr.eof()))
			debug_information.println("Open assemble configure file fail : ",ekcp.parameter_file_name);
		
		directory_name=fr.directory_name;
		parameter_charset=fr.get_charset();
		if((parameter_last_modified_time=fr.lastModified_time)<system_par.last_modified_time)
			parameter_last_modified_time=system_par.last_modified_time;
		if(parameter_last_modified_time<ekcp.scene_list_file_last_modified_time)
			parameter_last_modified_time=ekcp.scene_list_file_last_modified_time;

		if((type_temporary_directory_name=fr.get_string())==null)
			type_temporary_directory_name="no_directory";
		else
			type_temporary_directory_name=file_reader.separator(type_temporary_directory_name);
		type_temporary_directory_name=system_par.temporary_file_par.temporary_root_directory_name
				+"scene_directory"+File.separator+type_temporary_directory_name+File.separator;
		
		scene_temporary_directory_name=type_temporary_directory_name+file_reader.separator(ekcp.scene_name);
		if(scene_sub_directory.length()<=0)
			scene_temporary_directory_name+=File.separator;
		else if(scene_sub_directory.charAt(0)==File.separatorChar)
			scene_temporary_directory_name+=scene_sub_directory;
		else
			scene_temporary_directory_name+=File.separator+scene_sub_directory;

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
			scene_temporary_directory_name+=str_array[i];
		}
		
		if((type_shader_file_name=fr.get_string())==null)
			type_shader_file_name="";
		else
			type_shader_file_name=file_reader.separator(type_shader_file_name);
		
		switch(((type_shader_directory_name=fr.get_string())==null)?"relative":type_shader_directory_name){
		case "absolute_sub_directory":
			type_shader_file_name=scene_sub_directory+type_shader_file_name;
		case "absolute":
			type_shader_directory_name="";
			break;
		case "environment_sub_directory":
			type_shader_file_name=scene_sub_directory+type_shader_file_name;
		case "environment":
			if((type_shader_directory_name=fr.get_string())!=null)
				if((type_shader_directory_name=System.getenv(type_shader_directory_name))!=null)
					if((type_shader_directory_name=file_reader.separator(type_shader_directory_name)).length()>0)
						break;
			type_shader_directory_name=directory_name;
			break;
		case "relative_sub_directory":
			type_shader_file_name=scene_sub_directory+type_shader_file_name;
		case "relative":
		default:
			type_shader_directory_name=directory_name;
			break;
		}

		if((scene_shader_file_name=fr.get_string())==null)
			scene_shader_file_name="";
		else
			scene_shader_file_name=file_reader.separator(scene_shader_file_name);
		
		if((scene_shader_directory_name=fr.get_string())==null)
			scene_shader_directory_name="relative";
		switch(scene_shader_directory_name){
		case "absolute_sub_directory":
			scene_shader_file_name=scene_sub_directory+scene_shader_file_name;
		case "absolute":
			scene_shader_directory_name="";
			break;
		case "environment_sub_directory":
			scene_shader_file_name=scene_sub_directory+scene_shader_file_name;
		case "environment":
			if((scene_shader_directory_name=fr.get_string())!=null)
				if((scene_shader_directory_name=System.getenv(scene_shader_directory_name))!=null)
					if((scene_shader_directory_name=file_reader.separator(scene_shader_directory_name)).length()>0)
						break;
			scene_shader_directory_name=null;
			break;
		case "relative_sub_directory":
			scene_shader_file_name=scene_sub_directory+scene_shader_file_name;
		case "relative":
		default:
			scene_shader_directory_name=null;
			break;
		}

		if((camera_file_name=fr.get_string())==null)
			camera_file_name="";
		else
			camera_file_name=file_reader.separator(camera_file_name);
		
		if((change_part_file_name=fr.get_string())==null)
			change_part_file_name="";
		else
			change_part_file_name=file_reader.separator(change_part_file_name);
		
		if((change_component_file_name=fr.get_string())==null)
			change_component_file_name="";
		else
			change_component_file_name=file_reader.separator(change_component_file_name);
		
		if((type_string_file_name=fr.get_string())==null)
			type_string_file_name="";
		else
			type_string_file_name=file_reader.separator(type_string_file_name);

		fr.close();
		
		fr=new file_reader(ekcp.extra_parameter_file_name,ekcp.extra_parameter_charset);
		if((fr.error_flag())||(fr.eof()))
			debug_information.println("Open assemble extra configure file fail : ",ekcp.extra_parameter_file_name);
		
		extra_directory_name=fr.directory_name;
		extra_parameter_charset=fr.get_charset();

		if(parameter_last_modified_time<fr.lastModified_time)
			parameter_last_modified_time=fr.lastModified_time;
		
		scene_last_modified_time=parameter_last_modified_time;
		
		part_lru_in_list_number=fr.get_int();
		
		if((inserted_component_name=fr.get_string())==null)
			inserted_component_name="";
		if((inserted_part_name=fr.get_string())==null)
			inserted_part_name="";
		inserted_component_and_part_id=0;
		max_child_number=fr.get_int();
		
		if((scene_cors_string=fr.get_string())==null)
			scene_cors_string="*";
		else if(new File(str=fr.directory_name+file_reader.separator(scene_cors_string)).exists()?false:true)
			scene_cors_string=scene_cors_string.trim();
		else{
			scene_cors_string="";
			for(file_reader cors_fr=new file_reader(str,fr.get_charset());;) {
				if(cors_fr.eof()){
					cors_fr.close();
					break;
				}
				if((str=cors_fr.get_line())!=null)
					if((str=str.trim()).length()>0)
						scene_cors_string+=str;
			}
		}

		if((multiparameter_number=fr.get_int())<1)
			multiparameter_number=1;
		
		default_display_bitmap=fr.get_long();
		
		if((scene_component_name=fr.get_string())==null)
			scene_component_name="";
		
		if((component_collector_stack_file_name=fr.get_string())==null)
			component_collector_stack_file_name="";
		else
			component_collector_stack_file_name=file_reader.separator(component_collector_stack_file_name);
		
		component_collector_parameter_channel_id=new int[fr.get_int()];
		for(int i=0,ni=component_collector_parameter_channel_id.length;i<ni;i++)
			component_collector_parameter_channel_id[i]=fr.get_int();
		
		if((max_component_collector_number=fr.get_int())<=0)
			max_component_collector_number=1;
		if((max_camera_return_stack_number=fr.get_int())<=0)
			max_camera_return_stack_number=1;
		if((max_modifier_container_number=fr.get_int())<=0)
			max_modifier_container_number=1;
		
		if((max_target_number=fr.get_int())<=0)
			max_target_number=1;
		
		create_top_part_assembly_precision2		=fr.get_double();
		create_top_part_assembly_precision2		*=create_top_part_assembly_precision2;
		create_top_part_discard_precision2		=fr.get_double();
		create_top_part_discard_precision2		*=create_top_part_discard_precision2;
		discard_top_part_component_precision2	=fr.get_double();
		discard_top_part_component_precision2	*=discard_top_part_component_precision2;
		
		touch_time_length					=fr.get_long();
		
		most_component_delete_number		=fr.get_int();
		most_component_append_number		=fr.get_int();
		most_update_parameter_number		=fr.get_int();
		most_update_location_number			=fr.get_int();
		
		display_precision					=fr.get_int();

		display_assemble_depth				=fr.get_int();

		if((str=fr.get_string())==null)
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

		component_sort_min_distance				=fr.get_double();
		
		not_do_ancestor_render_flag				=fr.get_boolean();
		fast_load_flag							=fr.get_boolean();
		
		fr.close();

		return;
	}
}
