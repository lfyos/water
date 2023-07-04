package kernel_part;

import kernel_common_class.debug_information;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;

public class part_loader extends Thread
{
	public part loaded_part;

	private part copy_from_part;
	private long last_modified_time;
	
	private system_parameter system_par;
	private scene_parameter scene_par;
	private part_container_for_part_search pcps;

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
		pcps=null;
	}
	public part_loader(part my_loaded_part,part my_copy_from_part,long my_last_modified_time,
			system_parameter my_system_par,scene_parameter my_scene_par,part_container_for_part_search my_pcps)
	{
		is_loading_flag			=true;
		loaded_part				=my_loaded_part;
		copy_from_part			=my_copy_from_part;
		last_modified_time		=my_last_modified_time;
		system_par				=my_system_par;
		scene_par				=my_scene_par;
		pcps					=my_pcps;
		
		start();
	}
	public void run()
	{
		debug_information.println("Begin load_mesh_and_create_buffer_object_and_material_file:\t",loaded_part.system_name);
        debug_information.println();
		try{
			debug_information.println(
				loaded_part.load_mesh_and_create_buffer_object(copy_from_part,last_modified_time,system_par,scene_par,pcps));
		}catch(Exception e){
			debug_information.println(
	            	"Error in load_mesh_and_create_buffer_object_and_material_file:\t",loaded_part.system_name);
			debug_information.println(e.toString());
			e.printStackTrace();
		}finally{
			is_loading_flag=false;
		}
    	debug_information.println("End load_mesh_and_create_buffer_object_and_material_file:\t",loaded_part.system_name);
        debug_information.println();
	}
}
