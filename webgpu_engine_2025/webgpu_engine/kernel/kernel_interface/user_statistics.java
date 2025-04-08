package kernel_interface;

import kernel_file_manager.file_reader;

public class user_statistics 
{
	public int user_scene_kernel_number,user_scene_component_number;
	public int user_max_scene_kernel_number,user_max_scene_component_number;

	public user_statistics(file_reader f)
	{
		user_scene_kernel_number=0;
		user_scene_component_number=0;
		user_max_scene_kernel_number	=f.get_int();
		user_max_scene_component_number=f.get_int();
	}
}
