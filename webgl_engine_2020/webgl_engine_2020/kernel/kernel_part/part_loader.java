package kernel_part;

import kernel_common_class.debug_information;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;


public class part_loader extends Thread
{
	public part loaded_part;
	
	private part copy_from_part;
	private long last_modified_time;
	
	private system_parameter system_par;
	private scene_parameter scene_par;
	private part_container_for_part_search pcps;
	
	public void destroy()
	{
		if(loaded_part!=null) {
			loaded_part.destroy();
			loaded_part=null;
		}
		
		if(copy_from_part!=null) {
			copy_from_part.destroy();
			copy_from_part=null;
		}
		
		system_par=null;
		scene_par=null;
		
		if(pcps!=null) {
			pcps.destroy();
			pcps=null;
		}
	}
	
	public part_loader(part my_loaded_part,
			part my_copy_from_part,long my_last_modified_time,
			system_parameter my_system_par,scene_parameter my_scene_par,
			part_container_for_part_search my_pcps)
	{
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
		String part_temporary_file_directory=file_directory.part_file_directory(loaded_part,system_par,scene_par);
		String lock_file_name=file_reader.separator(part_temporary_file_directory+"part.lock");
		
		debug_information.println();
		debug_information.println(
	    		"Begin load_mesh_and_create_buffer_object_and_material_file:\t",loaded_part.system_name);
		debug_information.println(
				"Begin lock part:\t",loaded_part.system_name+"\t"+lock_file_name);
		
		system_par.system_exclusive_name_mutex.lock(lock_file_name);
		try{
			debug_information.println(
				loaded_part.load_mesh_and_create_buffer_object(
					copy_from_part,last_modified_time,part_temporary_file_directory,
					system_par.local_data_charset,system_par,pcps));
		}catch(Exception e){
			debug_information.println(
	            	"Error in load_mesh_and_create_buffer_object_and_material_file:\t",loaded_part.system_name);
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		system_par.system_exclusive_name_mutex.unlock(lock_file_name);
   
    	debug_information.println(
            	"End load_mesh_and_create_buffer_object_and_material_file:\t",loaded_part.system_name);
        debug_information.println();
	}
}
