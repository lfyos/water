package kernel_part;

import java.util.ArrayList;

import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_file_manager.file_directory;
import kernel_common_class.debug_information;
import kernel_common_class.tree_string_locker_container;

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
			system_parameter system_par,scene_parameter scene_par)
	{
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
	}

	
	public static void wait_for_completion(
			ArrayList<part_loader>already_loaded_part,
			system_parameter system_par,scene_parameter scene_par)
	{
		for(part_loader pl;already_loaded_part.size()>0;)
			for(int i=already_loaded_part.size()-1;i>=0;i--){
				if((pl=already_loaded_part.get(i)).test_loading_flag())
					wait_for_part_loader_termination(pl,system_par,scene_par);
				else 
					already_loaded_part.remove(i);
			}
	}
	
	synchronized private void load_routine(
		part my_part,part my_copy_from_part,long last_modified_time,
		system_parameter system_par,scene_parameter scene_par,
		tree_string_locker_container string_locker_container,
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
					wait_for_part_loader_termination(pl,system_par,scene_par);
				}
			if(part_loader_list.size()<max_part_load_thread_number)
				break;
			wait_for_part_loader_termination(part_loader_list.get(0),system_par,scene_par);
		}while(true);
		
		for(int i=already_loaded_part.size()-1;i>=0;i--)
			if(!((pl=already_loaded_part.get(i)).test_loading_flag())){
				already_loaded_part.remove(i);
				wait_for_part_loader_termination(pl,system_par,scene_par);	
			}
		pl=new part_loader(my_part,my_copy_from_part,last_modified_time,
					system_par,scene_par,string_locker_container);
		part_loader_list.add(pl);
		already_loaded_part.add(pl);
	}
	public void load(part my_part,part my_copy_from_part,long last_modified_time,
			system_parameter system_par,scene_parameter scene_par,
			tree_string_locker_container string_locker_container,
			ArrayList<part_loader> already_loaded_part,
			ArrayList<buffer_object_file_modify_time_and_length_container> boftal_container)
	{
		int boftal_number;
		if((boftal_number=boftal_container.size())>0) {
			String boftal_token_str=file_directory.part_file_directory(my_part,system_par,scene_par).
						substring(system_par.temporary_file_par.temporary_root_directory_name.length());
			ArrayList<buffer_object_file_modify_time_and_length> my_list;
			for(int i=0;i<boftal_number;i++)
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
		try{
			load_routine(my_part,my_copy_from_part,last_modified_time,
				system_par,scene_par,string_locker_container,already_loaded_part);
		}catch(Exception e){
			e.printStackTrace();
			debug_information.println("load of part_loader_container fail");
			debug_information.println(e.toString());
		}
	}
}
