package kernel_part;

import kernel_common_class.debug_information;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;

public class part_loader extends Thread
{
	public part loaded_part;

	private part copy_from_part;
	
	private system_parameter system_par;
	private scene_parameter scene_par;

	private volatile boolean is_loading_flag;
	public boolean test_loading_flag()
	{
		return is_loading_flag;
	}
	
	public void destroy()
	{
		loaded_part=null;
		copy_from_part=null;
		system_par=null;
		scene_par=null;
	}
	public part_loader(part my_loaded_part,part my_copy_from_part,
			system_parameter my_system_par,scene_parameter my_scene_par)
	{
		is_loading_flag			=true;
		loaded_part				=my_loaded_part;
		copy_from_part			=my_copy_from_part;
		system_par				=my_system_par;
		scene_par				=my_scene_par;
		
		start();
	}
	public void run()
	{
		debug_information.println("Begin load_mesh_and_create_buffer_object_and_material_file:\t",loaded_part.system_name);
        debug_information.println();
		try{
			debug_information.println(
				loaded_part.load_mesh_and_create_buffer_object(copy_from_part,system_par,scene_par));
		}catch(Exception e){
			
			e.printStackTrace();
			
			debug_information.println(
	            	"Error in load_mesh_and_create_buffer_object_and_material_file:\t",loaded_part.system_name);
			debug_information.println(e.toString());
			
		}finally{
			is_loading_flag=false;
		}
    	debug_information.println("End load_mesh_and_create_buffer_object_and_material_file:\t",loaded_part.system_name);
        debug_information.println();
	}
}
