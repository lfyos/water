package kernel_part;

import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;

import java.io.File;

import kernel_common_class.debug_information;
import kernel_common_class.tree_string_locker_container;

public class part_loader extends Thread
{
	public part loaded_part;
	
	private system_parameter system_par;
	private scene_parameter scene_par;
	private tree_string_locker_container string_locker_container;

	private volatile boolean is_loading_flag;
	
	public boolean test_loading_flag()
	{
		return is_loading_flag;
	}
	
	public void destroy()
	{
		loaded_part=null;
		system_par=null;
		scene_par=null;
	}
	public part_loader(part my_loaded_part,system_parameter my_system_par,
			scene_parameter my_scene_par,tree_string_locker_container my_string_locker_container)
	{
		is_loading_flag			=true;
		loaded_part				=my_loaded_part;
		system_par				=my_system_par;
		scene_par				=my_scene_par;
		string_locker_container	=my_string_locker_container;
		start();
	}
	
	public void run()
	{
		String part_temporary_file_directory=file_directory.part_file_directory(loaded_part,system_par,scene_par);
		String boftal_file_name=part_temporary_file_directory+"mesh.boftal";
		
		String my_lock_key[]=new String[]{part_temporary_file_directory+"part.lock"};
		string_locker_container.read_lock(my_lock_key);

		if(new File(boftal_file_name).lastModified()>=loaded_part.part_par.last_modified_time){
			file_reader fr=new file_reader(boftal_file_name,system_par.local_data_charset);
			loaded_part.boftal=new buffer_object_file_modify_time_and_length(fr);
			fr.close();
			
			if(loaded_part.part_mesh==null)
				loaded_part.part_mesh=loaded_part.boftal.simple_part_mesh;
			if(loaded_part.part_mesh!=null)
				loaded_part.part_mesh.free_memory();
			
			is_loading_flag=false;
			string_locker_container.read_unlock(my_lock_key);
			
			debug_information.println("Load part mesh.boftal:	user name:"+
					loaded_part.user_name+"	system name:"+loaded_part.system_name,
					"	mesh file:"	 +loaded_part.directory_name+loaded_part.mesh_file_name);
			return;
			
		}
	
		string_locker_container.switch_read_lock_to_write_lock(my_lock_key);
	
        try{
        	String str=loaded_part.load_mesh_and_create_buffer_object(system_par,scene_par);
			debug_information.println(str);
		}catch(Exception e){
			String str="Error in load_mesh_and_create_buffer_object_and_material_file:\t";
			debug_information.println(str,loaded_part.system_name);
			debug_information.println(e.toString());
			e.printStackTrace();
		}

        is_loading_flag=false;
        string_locker_container.write_unlock(my_lock_key);
	}
}
