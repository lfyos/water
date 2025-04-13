package kernel_part;

import java.util.ArrayList;

import kernel_file_manager.file_reader;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_file_manager.file_directory;
import kernel_common_class.debug_information;

public class part_loader_container
{
	private ArrayList<part_loader> part_loader_list;
	
	public void destroy()
	{
		part_loader pl;
		if(part_loader_list!=null) {
			for(int i=0,ni=part_loader_list.size();i<ni;i++)
				if((pl=part_loader_list.get(i))!=null)
					pl.destroy();
			part_loader_list.clear();
			part_loader_list=null;
		}
	}
	public part_loader_container()
	{
		part_loader_list			=new ArrayList<part_loader>();
	}
	
	private static void wait_for_part_loader_termination(part_loader pl,
			boolean display_flag,system_parameter system_par,scene_parameter scene_par)
	{
		if(display_flag) {
			debug_information.println("Begin:\twait_for_completion:\t",pl.loaded_part.system_name);
			debug_information.println("		",pl.loaded_part.directory_name+pl.loaded_part.mesh_file_name);
			debug_information.println("		",pl.loaded_part.directory_name+pl.loaded_part.material_file_name);
			debug_information.println("		",file_directory.part_file_directory(pl.loaded_part, system_par, scene_par));
		}
		try{			
			pl.join(system_par.part_load_sleep_time_length);
		}catch(Exception e){
			e.printStackTrace();
			
			debug_information.println(e.toString());
			debug_information.println("Error:\twait_for_completion:\t"+pl.loaded_part.system_name);
			debug_information.println("		",pl.loaded_part.directory_name+pl.loaded_part.mesh_file_name);
			debug_information.println("		",pl.loaded_part.directory_name+pl.loaded_part.material_file_name);
			debug_information.println("		",file_directory.part_file_directory(pl.loaded_part,system_par, scene_par));
		}
		if(display_flag)
			if(!(pl.test_loading_flag())) {
				debug_information.println("End:\twait_for_completion:\t"+pl.loaded_part.system_name);
				debug_information.println("		",pl.loaded_part.directory_name+pl.loaded_part.mesh_file_name);
				debug_information.println("		",pl.loaded_part.directory_name+pl.loaded_part.material_file_name);
				debug_information.println("		",file_directory.part_file_directory(pl.loaded_part,system_par, scene_par));
			}
	}

	
	public static void wait_for_completion(
			ArrayList<part_loader>already_loaded_part,
			system_parameter system_par,scene_parameter scene_par)
	{
		part_loader pl;
		
		debug_information.println();
		debug_information.println("Begin wait_for_completion");
		debug_information.println();
		
		while(already_loaded_part.size()>0)
			for(int i=already_loaded_part.size()-1;i>=0;i--){
				if((pl=already_loaded_part.get(i)).test_loading_flag()) {
					debug_information.println(pl.loaded_part.system_name+" is Waiting for completion");
					wait_for_part_loader_termination(pl,true,system_par,scene_par);
				}else {
					debug_information.println(pl.loaded_part.system_name+" has done Waiting for completion");
					already_loaded_part.remove(i);
				}
			}
		
		debug_information.println();
		debug_information.println("End wait_for_completion");
		debug_information.println();
	}
	
	synchronized private void load_routine(
		part my_part,part my_copy_from_part,long last_modified_time,
		system_parameter system_par,scene_parameter scene_par,
		ArrayList<part_loader> already_loaded_part)
	{
		int max_part_load_thread_number;
		if((max_part_load_thread_number=my_part.part_par.max_part_load_thread_number)<1)
			max_part_load_thread_number=1;

		part_loader pl;
		
		do{
			for(int i=part_loader_list.size()-1;i>=0;i--) 
				if((pl=part_loader_list.get(i)).test_loading_flag()?false:true){
					part_loader_list.remove(i);
					wait_for_part_loader_termination(pl,false,system_par,scene_par);
				}
			if(part_loader_list.size()<max_part_load_thread_number)
				break;
			wait_for_part_loader_termination(part_loader_list.get(0),false,system_par,scene_par);
		}while(true);
		
		for(int i=already_loaded_part.size()-1;i>=0;i--)
			if(!((pl=already_loaded_part.get(i)).test_loading_flag())){
				already_loaded_part.remove(i);
				wait_for_part_loader_termination(pl,false,system_par,scene_par);	
			}
		pl=new part_loader(my_part,my_copy_from_part,last_modified_time,system_par,scene_par);
		part_loader_list.add(pl);
		already_loaded_part.add(pl);
	}
	public void load_part_mesh_head_only(part my_part,
			system_parameter my_system_par,scene_parameter my_scene_par)
	{
		if(my_part.is_normal_part()){
			String part_temporary_file_directory=file_directory.part_file_directory(my_part,my_system_par,my_scene_par);
			String my_lock_key[]=new String[] {file_reader.separator(part_temporary_file_directory+"part.lock")};
			my_system_par.string_locker_container.lock(my_lock_key);
			my_part.load_part_mesh();
			my_system_par.string_locker_container.unlock(my_lock_key);
		}
	}
	public void load(part my_part,part my_copy_from_part,long last_modified_time,
			system_parameter system_par,scene_parameter scene_par,
			ArrayList<part> part_list_for_delete_file,ArrayList<part_loader> already_loaded_part,
			ArrayList<buffer_object_file_modify_time_and_length_container> boftal_container)
	{
		int boftal_number;
		String part_temporary_file_directory=file_directory.part_file_directory(my_part,system_par,scene_par);
		if((boftal_number=boftal_container.size())>0) {
			String boftal_token_str=part_temporary_file_directory.substring(
					system_par.temporary_file_par.temporary_root_directory_name.length());
			for(int i=0;i<boftal_number;i++){
				ArrayList<buffer_object_file_modify_time_and_length> my_list;
				if((my_list=boftal_container.get(i).search(new String[]{boftal_token_str}))!=null)
					if(my_list.size()>0){
						my_part.boftal=my_list.get(0);
						if(my_part.part_mesh==null)
							my_part.part_mesh=my_part.boftal.simple_part_mesh;
						if(my_part.part_mesh!=null)
							my_part.part_mesh.free_memory();
						return;
					}
			}
		}
		
		try{
			load_routine(my_part,my_copy_from_part,
				last_modified_time,system_par,scene_par,already_loaded_part);
		}catch(Exception e){
			e.printStackTrace();
			debug_information.println("load of part_loader_container fail");
			debug_information.println(e.toString());
		}
		part_list_for_delete_file.add(my_part);
	}
}
