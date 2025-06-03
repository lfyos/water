package driver_movement;

import kernel_file_manager.file_writer;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;

public class movement_buffer_file_manager 
{
	private int movement_component_id,movement_driver_id;
	private system_parameter system_par;
	private scene_parameter scene_par;
	
	public movement_buffer_file_manager(
			int my_movement_component_id,int my_movement_driver_id,
			system_parameter my_system_par,scene_parameter my_scene_par)
	{
		movement_component_id	=my_movement_component_id;
		movement_driver_id		=my_movement_driver_id;
		system_par				=my_system_par;
		scene_par				=my_scene_par;
	}
	
	public String buffer_file_manager(String directory_name,String file_name)
	{
		return file_writer.component_temparatory_link_path_name(
					movement_component_id, movement_driver_id,
					directory_name,file_name,system_par,scene_par);
	}
}
