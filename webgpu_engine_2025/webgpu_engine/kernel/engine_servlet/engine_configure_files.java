package engine_servlet;

import java.io.File;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;

public class engine_configure_files 
{
	public boolean configure_files_exist_flag;
	public String scene_data_path_name,scene_temparatory_path_name,scene_environment_path_name;
	
	public engine_configure_files(String my_scene_data_path_name,
			String my_scene_temparatory_path_name,String my_scene_environment_path_name)
	{
		scene_data_path_name		=(my_scene_data_path_name==null)		?"":my_scene_data_path_name;
		scene_temparatory_path_name	=(my_scene_temparatory_path_name==null)	?"":my_scene_temparatory_path_name;
		scene_environment_path_name	=(my_scene_environment_path_name==null)	?"":my_scene_environment_path_name;
		
		scene_data_path_name		=file_reader.separator(scene_data_path_name);
		scene_temparatory_path_name	=file_reader.separator(scene_temparatory_path_name);
		scene_environment_path_name	=file_reader.separator(scene_environment_path_name);
    	
    	debug_information.println("scene_data_path_name:	",scene_data_path_name);
    	debug_information.println("temparatory_path_name:	",scene_temparatory_path_name);
    	debug_information.println("environment_path_name:	",scene_environment_path_name);
    	debug_information.println();
    	
    	if(new File(scene_data_path_name).exists())
    		configure_files_exist_flag=true;
    	else{
    		debug_information.println("scene_data_path_name is NOT exist: ",scene_data_path_name);
    		configure_files_exist_flag=false;
    	}
		return;
	}
}
