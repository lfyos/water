package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.travel_through_directory;

public class test_1 extends travel_through_directory
{
	private static final String mode_string[]= {
			"lfy_user_dir"
/*			
			"environment_select_mount",
			"environment_select_charset_mount",
			"environment_parameter_mount",
			"environment_parameter_charset_mount",
			
			"component_mount" ,
			"charset_component_mount" ,
			"absulate_component_mount" ,
			"absulate_charset_component_mount" ,
			"environment_component_mount" ,	
			"environment_charset_component_mount" ,	
			"mount" ,
			"charset_mount" ,
			"absulate_mount" ,
			"absulate_charset_mount" ,
			"environment_select_mount" ,
			"environment_select_charset_mount" ,
			"absulate_environment_select_mount" ,
			"absulate_environment_select_charset_mount" ,
			"environment_parameter_mount" ,
			"environment_parameter_charset_mount" ,
			"absulate_environment_parameter_mount" ,
			"absulate_environment_parameter_charset_mount" ,
			"environment_scene_sub_directory_mount" ,
			"environment_scene_sub_directory_charset_mount" ,
			"absulate_environment_scene_sub_directory_mount" ,
			"absulate_environment_scene_sub_directory_charset_mount" ,
			"part_driver_mount" ,
			"external_part_driver_mount"
*/
	};
	public void operate_file(String file_name)
	{
		String str;
		if((str=file_reader.get_text(file_name,"GBK"))!=null)
			for(String my_mode_str:mode_string)
				if(str.indexOf(my_mode_str)>=0)
					debug_information.println(my_mode_str+":	",file_name);
	}
	
	public test_1()
	{
		super(new String[]
		{
//			"F:\\water_all\\.git"
		});
	}
	public static void main(String args[])
	{
		String path_name[]={
				"E:\\project_data",
				"G:\\water_all\\data"
		};
		
		debug_information.println("start search");
		
		for(String my_path_name:path_name) {
			debug_information.println("Begin:		",	my_path_name);
			new test_1().do_travel(my_path_name,false);
			debug_information.println("End:			",	my_path_name);
		}
		
		debug_information.println("end search");
	}
}
