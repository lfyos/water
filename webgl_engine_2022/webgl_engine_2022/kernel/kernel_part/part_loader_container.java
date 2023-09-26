package kernel_part;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_directory;
import kernel_common_class.debug_information;
import kernel_common_class.exclusive_file_mutex;

public class part_loader_container
{
	private volatile ArrayList<part_loader> part_loader_list;
	private ReentrantLock part_loader_container_lock;
	
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
		if(part_loader_container_lock!=null)
			part_loader_container_lock=null;
	}
	public part_loader_container()
	{
		part_loader_list=new ArrayList<part_loader>();
		part_loader_container_lock=new ReentrantLock();
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
			pl.join();
		}catch(Exception e){
			debug_information.println(e.toString());
			debug_information.println("Error:\twait_for_completion:\t"+pl.loaded_part.system_name);
			debug_information.println("		",pl.loaded_part.directory_name+pl.loaded_part.mesh_file_name);
			debug_information.println("		",pl.loaded_part.directory_name+pl.loaded_part.material_file_name);
			debug_information.println("		",file_directory.part_file_directory(pl.loaded_part,system_par, scene_par));
			e.printStackTrace();
		}
		if(display_flag)
			if(!(pl.test_loading_flag())) {
				debug_information.println("End:\twait_for_completion:\t"+pl.loaded_part.system_name);
				debug_information.println("		",pl.loaded_part.directory_name+pl.loaded_part.mesh_file_name);
				debug_information.println("		",pl.loaded_part.directory_name+pl.loaded_part.material_file_name);
				debug_information.println("		",file_directory.part_file_directory(pl.loaded_part,system_par, scene_par));
			}
	}
	public static void wait_for_completion(ArrayList<part_loader> already_loaded_part,
			system_parameter system_par,scene_parameter scene_par)
	{
		part_loader pl;
		
		debug_information.println();
		debug_information.println("Begin wait_for_completion");
		debug_information.println();
		for(int wait_number=0;already_loaded_part.size()>0;wait_number++){
			for(int i=already_loaded_part.size()-1;i>=0;i--) {
				if((pl=already_loaded_part.get(i)).test_loading_flag()) {
					if((wait_number%2)==0)
						wait_for_part_loader_termination(pl,false,system_par,scene_par);
					else{
						debug_information.println(pl.loaded_part.system_name+" is Waiting for completion:",wait_number);
						wait_for_part_loader_termination(pl,true,system_par,scene_par);
					}
				}else {
					debug_information.println(pl.loaded_part.system_name+" has done Waiting for completion:",wait_number);
					already_loaded_part.remove(i);
				}
			}
		}
		debug_information.println();
		debug_information.println("End wait_for_completion");
		debug_information.println();
	}
	private void load_routine(	part my_part,part my_copy_from_part,
			long last_modified_time,system_parameter system_par,scene_parameter scene_par,
			ArrayList<part_loader> already_loaded_part)
	{
		part_loader pl;
		int max_part_load_thread_number=my_part.part_par.max_part_load_thread_number;
		if(max_part_load_thread_number<1)
			max_part_load_thread_number=1;
		
		for(long last_display_time=0;;){
			for(int i=part_loader_list.size()-1;i>=0;i--) {
				if((pl=part_loader_list.get(i)).test_loading_flag())
					continue;
				part_loader_list.remove(i);
				wait_for_part_loader_termination(pl,false,system_par,scene_par);
			}
			long current_display_time=System.nanoTime()/(1000*1000*1000);
			if(part_loader_list.size()<max_part_load_thread_number)
				last_display_time=current_display_time;
			else{
				if((current_display_time-last_display_time)>5){
					last_display_time=current_display_time;
					debug_information.print  ("Current part loading number is ",	part_loader_list.size());
					debug_information.println(", Maximum part loading number is ",	max_part_load_thread_number);				
				}
				try {
					long my_sleep_time_length=100/max_part_load_thread_number;
					Thread.sleep((my_sleep_time_length>100)?100:(my_sleep_time_length<=10)?10:my_sleep_time_length);
				}catch(Exception e) {
					Thread.yield();
				}
				continue;
			}
			
			pl=new part_loader(my_part,my_copy_from_part,last_modified_time,system_par,scene_par);
			part_loader_list.add(pl);
			already_loaded_part.add(pl);

			for(int i=already_loaded_part.size()-1;i>=0;i--) {
				if((pl=already_loaded_part.get(i)).test_loading_flag())
					continue;
				already_loaded_part.remove(i);
				wait_for_part_loader_termination(pl,false,system_par,scene_par);	
			}

			return;
		}
	}
	private boolean fast_load_routine(long last_modified_time,
			String part_temporary_file_directory,part my_part,part my_copy_from_part,
			system_parameter system_par,scene_parameter scene_par,
			buffer_object_file_modify_time_and_length_container boftal_container)
	{
		if((scene_par!=null)&&(boftal_container!=null)){
			buffer_object_file_modify_time_and_length my_boftal;
			String boftal_token_str=part_temporary_file_directory.substring(
					system_par.proxy_par.proxy_data_root_directory_name.length());
			if((my_boftal=boftal_container.search_boftal(boftal_token_str,0))!=null){
				if(my_part.part_mesh==null)
					my_part.part_mesh=my_boftal.simple_part_mesh;
				my_part.boftal=my_boftal;
				return true;
			}
			boftal_container=null;
		}
		
		String boftal_file_name=part_temporary_file_directory+"mesh.boftal";
		long boftal_last_modify_time=new File(boftal_file_name).lastModified();
		if(boftal_last_modify_time<=last_modified_time) 
			return false;
		if(boftal_last_modify_time<=my_part.part_par.last_modified_time)
			return false;
		String cfp_mesh_file_name=my_copy_from_part.directory_name+my_copy_from_part.mesh_file_name;
		if(boftal_last_modify_time<=new File(cfp_mesh_file_name).lastModified())
			return false;
		String cfp_material_file_name=my_copy_from_part.directory_name+my_copy_from_part.material_file_name;
		if(boftal_last_modify_time<=new File(cfp_material_file_name).lastModified())
			return false;
		
		my_part.boftal=null;
		if(boftal_container!=null){
			String boftal_token_str=part_temporary_file_directory.substring(
					system_par.proxy_par.proxy_data_root_directory_name.length());
			my_part.boftal=boftal_container.search_boftal(boftal_token_str,boftal_last_modify_time);
		}
		if(my_part.boftal==null) {
			file_reader fr=new file_reader(
				part_temporary_file_directory+"mesh.boftal",system_par.local_data_charset);
			my_part.boftal=new buffer_object_file_modify_time_and_length(fr);
			fr.close();
			
			debug_information.println("Load part mesh.boftal:	user name:"+
					my_part.user_name+"	system name:"+my_part.system_name,
					"	mesh file:"	 +my_part.directory_name+my_part.mesh_file_name);
		}
		if(my_part.part_mesh==null)
			my_part.part_mesh=my_part.boftal.simple_part_mesh;

		return true;
	}
	public void load_part_mesh_head_only(part my_part,
			system_parameter my_system_par,scene_parameter my_scene_par)
	{
		if(my_part.is_normal_part()){
			String part_temporary_file_directory=file_directory.part_file_directory(my_part,my_system_par,my_scene_par);
			String lock_file_name=file_reader.separator(part_temporary_file_directory+"part.lock");
			exclusive_file_mutex efm=exclusive_file_mutex.lock(lock_file_name,
					"wait for load_part_mesh_head_only:	"+my_part.directory_name+my_part.mesh_file_name);
			if(my_part.part_mesh!=null)
				my_part.part_mesh.destroy();
			my_part.call_part_driver_for_load_part_mesh();
			efm.unlock();
		}
	}
	
	public void load(part my_part,part my_copy_from_part,long last_modified_time,
			system_parameter system_par,scene_parameter scene_par,
			ArrayList<part> part_list_for_delete_file,ArrayList<part_loader> already_loaded_part,
			buffer_object_file_modify_time_and_length_container boftal_container)
	{
		String part_temporary_file_directory=file_directory.part_file_directory(my_part,system_par,scene_par);
		String lock_file_name=file_reader.separator(part_temporary_file_directory+"part.lock");
		
		if(fast_load_routine(last_modified_time,part_temporary_file_directory,
				my_part,my_copy_from_part,system_par,scene_par,boftal_container))
		{
			if(my_part.part_mesh!=null)
				my_part.part_mesh.free_memory();
			return;
		}
		
		exclusive_file_mutex efm=exclusive_file_mutex.lock(lock_file_name,
				"wait for load_mesh_and_create_buffer_object_and_material_file:	"
				+my_part.directory_name+my_part.mesh_file_name);
		
		part_loader_container_lock.lock();
		try{
			load_routine(my_part,my_copy_from_part,last_modified_time,system_par,scene_par,already_loaded_part);
		}catch(Exception e) {
			debug_information.println("load of part_loader_container fail");
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		part_loader_container_lock.unlock();
		
		efm.unlock();
		
		part_list_for_delete_file.add(my_part);
	}
}
