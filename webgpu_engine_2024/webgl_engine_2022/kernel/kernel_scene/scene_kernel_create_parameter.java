package kernel_scene;

import java.io.File;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;

public class scene_kernel_create_parameter 
{
	public boolean success_load_parameter_flag;
	
	public double	create_top_part_expand_ratio;
	public double	create_top_part_left_ratio;
	
	public String	scene_directory_name;
	public String	scene_file_name;
	public String	scene_charset;
	public long		scene_list_file_last_modified_time;
	public String	parameter_file_name;
	public String	parameter_charset;
	public String	extra_parameter_file_name;
	public String	extra_parameter_charset;
	
	public scene_kernel_create_parameter(String my_scene_name,
			String client_scene_file_name,String client_scene_file_charset,
			system_parameter system_par)
	{
		success_load_parameter_flag=false;
		
		file_reader f_type=new file_reader(client_scene_file_name,client_scene_file_charset);
		if(f_type.error_flag()){
			f_type.close();
			debug_information.println("Open scene_file_name fail	:	",client_scene_file_name);
			return;
		}
		while(!(f_type.eof())){
			String my_mount_file_name;
			if((my_mount_file_name=f_type.get_string())==null)
				break;
			if(my_mount_file_name.compareTo("")==0)
				break;
			my_mount_file_name=file_reader.separator(my_mount_file_name);
			file_reader f_name=new file_reader(
					f_type.directory_name+my_mount_file_name,f_type.get_charset());
			while(!(f_name.eof())){
				String my_scene_search_name	=f_name.get_string();
				scene_file_name				=f_name.get_string();
				create_top_part_expand_ratio=f_name.get_long();
				create_top_part_left_ratio	=f_name.get_long();
				parameter_file_name			=f_name.get_string();
				extra_parameter_file_name	=f_name.get_string();
				
				if(extra_parameter_file_name==null)
					break;
				extra_parameter_file_name=extra_parameter_file_name.trim();
				if(extra_parameter_file_name.length()<=0)
					break;
				if(my_scene_name!=null)
					if(my_scene_name.compareTo(my_scene_search_name)!=0)
						continue;
				
				scene_file_name				=file_reader.separator(scene_file_name);
				parameter_file_name			=file_reader.separator(parameter_file_name);
				extra_parameter_file_name	=file_reader.separator(extra_parameter_file_name);
				
				if(!(file_reader.is_exist(f_name.directory_name+scene_file_name))) {
					debug_information.println("Find unexist scene_assembly_file_name:",
						my_scene_search_name+"\t"+"\t"+scene_file_name);
					continue;
				}
				if(file_reader.is_exist(f_name.directory_name+parameter_file_name))
					parameter_file_name=f_name.directory_name+parameter_file_name;
				else{
					parameter_file_name=system_par.default_parameter_directory
						+"assemble_parameter"+File.separator+parameter_file_name;
					if(!(file_reader.is_exist(parameter_file_name))) {
						debug_information.println("Find unexist scene parameter_file_name:",
							my_scene_search_name+"\t"+"\t"+parameter_file_name);
						continue;
					}
				}
				if(file_reader.is_exist(f_name.directory_name+extra_parameter_file_name))
					extra_parameter_file_name=f_name.directory_name+extra_parameter_file_name;
				else {
					extra_parameter_file_name=system_par.default_parameter_directory
						+"assemble_parameter"+File.separator+extra_parameter_file_name;
					if(!(file_reader.is_exist(extra_parameter_file_name))) {
						debug_information.println("Find unexist scene extra_parameter_file_name:",
							my_scene_search_name+"\t"+"\t"+extra_parameter_file_name);
						continue;
					}
				}
				
				debug_information.println("scene_name	              	:	",	my_scene_search_name);
				debug_information.println("scene_file_name          	:	",	f_name.directory_name+scene_file_name);
				debug_information.println("parameter_file_name		:	",		parameter_file_name);
				debug_information.println("extra_parameter_file_name	:	",	extra_parameter_file_name);
				
				f_name.close();
				f_type.close();
				
				scene_directory_name				=f_name.directory_name;
				scene_charset						=f_name.get_charset();
				parameter_charset					=f_name.get_charset();
				extra_parameter_charset				=f_name.get_charset();
				scene_list_file_last_modified_time	=(f_type.lastModified_time<f_name.lastModified_time)
													?f_name.lastModified_time:f_type.lastModified_time;
				
				success_load_parameter_flag			=true;
				
				return;
			}
			f_name.close();
		}
		f_type.close();
	}
}
