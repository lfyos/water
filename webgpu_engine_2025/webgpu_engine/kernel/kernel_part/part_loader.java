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
	private part copy_from_part;
	private long last_modified_time;
	
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
		copy_from_part=null;
		system_par=null;
		scene_par=null;
	}
	public part_loader(
			part my_loaded_part,part my_copy_from_part,long my_last_modified_time,
			system_parameter my_system_par,scene_parameter my_scene_par,
			tree_string_locker_container my_string_locker_container)
	{
		is_loading_flag			=true;
		loaded_part				=my_loaded_part;
		copy_from_part			=my_copy_from_part;
		last_modified_time		=my_last_modified_time;
		system_par				=my_system_par;
		scene_par				=my_scene_par;
		string_locker_container	=my_string_locker_container;
		start();
	}
	private boolean test_create_boftal_file(String part_temporary_file_directory)
	{
		long boftal_last_modify_time=new File(part_temporary_file_directory+"mesh.boftal").lastModified();
		
		if(boftal_last_modify_time<=last_modified_time) 
			return false;
		if(boftal_last_modify_time<=loaded_part.part_par.last_modified_time)
			return false;

		if(loaded_part.is_normal_part()) {
			String mesh_file_name=loaded_part.directory_name+loaded_part.mesh_file_name;
			if(boftal_last_modify_time<=new File(mesh_file_name).lastModified())
				return false;
			String material_file_name=loaded_part.directory_name+loaded_part.material_file_name;
			if(boftal_last_modify_time<=new File(material_file_name).lastModified())
				return false;
		}else{
			String mesh_file_name=copy_from_part.directory_name+copy_from_part.mesh_file_name;
			if(boftal_last_modify_time<=new File(mesh_file_name).lastModified())
				return false;
			String material_file_name=copy_from_part.directory_name+copy_from_part.material_file_name;
			if(boftal_last_modify_time<=new File(material_file_name).lastModified())
				return false;
		}
		return true;
	}
	public void run()
	{
		String part_temporary_file_directory=file_directory.part_file_directory(loaded_part,system_par,scene_par);
		String my_lock_key[]=new String[]{part_temporary_file_directory+"part.lock"};
		string_locker_container.lock(my_lock_key);
		
		if(test_create_boftal_file(part_temporary_file_directory)){
			file_reader fr=new file_reader(
				part_temporary_file_directory+"mesh.boftal",system_par.local_data_charset);
			loaded_part.boftal=new buffer_object_file_modify_time_and_length(fr);
			fr.close();

			if(loaded_part.part_mesh==null)
				loaded_part.part_mesh=loaded_part.boftal.simple_part_mesh;
			if(loaded_part.part_mesh!=null)
				loaded_part.part_mesh.free_memory();
			
			is_loading_flag=false;
			string_locker_container.unlock(my_lock_key);
			
			debug_information.println("Load part mesh.boftal:	user name:"+
					loaded_part.user_name+"	system name:"+loaded_part.system_name,
					"	mesh file:"	 +loaded_part.directory_name+loaded_part.mesh_file_name);
			return;
		}
	
        try{
			debug_information.println(
				loaded_part.load_mesh_and_create_buffer_object(copy_from_part,system_par,scene_par));
		}catch(Exception e){
			debug_information.println(
	            "Error in load_mesh_and_create_buffer_object_and_material_file:\t",loaded_part.system_name);
			debug_information.println(e.toString());
			e.printStackTrace();
		}

        is_loading_flag=false;
        string_locker_container.unlock(my_lock_key);
	}
}
