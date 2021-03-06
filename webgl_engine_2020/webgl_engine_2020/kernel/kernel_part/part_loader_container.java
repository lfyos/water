package kernel_part;

import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.debug_information;
import kernel_engine.system_parameter;
import kernel_file_manager.file_directory;

import kernel_engine.scene_parameter;

public class part_loader_container
{
	private volatile part_loader part_loader_array[];
	private ReentrantLock part_loader_container_lock;
	
	public void destroy()
	{
		if(part_loader_array!=null) {
			for(int i=0,ni=part_loader_array.length;i<ni;i++)
				if(part_loader_array[i]!=null) {
					part_loader_array[i].destroy();
					part_loader_array[i]=null;
				}
			part_loader_array=null;
		}
		if(part_loader_container_lock!=null)
			part_loader_container_lock=null;
	}
	public part_loader_container()
	{
		part_loader_array=new part_loader[]{};
		part_loader_container_lock=new ReentrantLock();
	}
	private static void wait_for_part_loader_termination(part_loader pl,
			boolean display_flag,system_parameter system_par,scene_parameter scene_par)
	{
		if(display_flag) {
			debug_information.println(
					"Begin:\twait_for_completion:\t"+pl.loaded_part.system_name+"\t:\t"+
					pl.loaded_part.directory_name+pl.loaded_part.mesh_file_name+"\t:\t",
					file_directory.part_file_directory(pl.loaded_part, system_par, scene_par));
		}
		try{			
			pl.join(10000);
		}catch(Exception e){
			debug_information.println(
					"Error:\twait_for_completion:\t"+pl.loaded_part.system_name+"\t:\t"+
					pl.loaded_part.directory_name+pl.loaded_part.mesh_file_name+"\t:\t",
					file_directory.part_file_directory(pl.loaded_part, system_par, scene_par));
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		if(display_flag)
			if(!(pl.isAlive())) {
				debug_information.println(
					"End:\twait_for_completion:\t"+pl.loaded_part.system_name+"\t\t"+
					pl.loaded_part.directory_name+pl.loaded_part.mesh_file_name+"\t\t",
					file_directory.part_file_directory(pl.loaded_part, system_par, scene_par));
			}
	}
	public static void wait_for_completion(part_loader already_loaded_part[],
			system_parameter system_par,scene_parameter scene_par)
	{
		debug_information.println();
		debug_information.println("Begin wait_for_completion");
		debug_information.println();
		for(int wait_number=0;;wait_number++){
			int number=0;
			for(int i=0,ni=already_loaded_part.length;i<ni;i++){
				if(already_loaded_part[i]!=null){
					if(already_loaded_part[i].isAlive()){
						if(number==i)
							number++;
						else {
							already_loaded_part[number++]=already_loaded_part[i];
							already_loaded_part[i]=null;
						}
					}else{
						wait_for_part_loader_termination(already_loaded_part[i],false,system_par,scene_par);
						already_loaded_part[i]=null;
					}	
				}
			}
			if(number<=0)
				break;
			for(int i=0;i<number;i++)
				if(already_loaded_part[i].isAlive()){
					String str=already_loaded_part[i].loaded_part.system_name;
					debug_information.println(str+" is Waiting for completion:",wait_number);
					wait_for_part_loader_termination(already_loaded_part[i],true,system_par,scene_par);
					break;
				}
		}
		debug_information.println();
		debug_information.println("End wait_for_completion");
		debug_information.println();
	}
	private part_loader[] load_routine(part my_part,part my_copy_from_part,
			long last_modified_time,system_parameter system_par,scene_parameter scene_par,
			part_loader already_loaded_part[],part_container_for_part_search pcps)
	{
		int max_part_load_thread_number=my_part.part_par.max_part_load_thread_number;
		if(max_part_load_thread_number<1)
			max_part_load_thread_number=1;
		
		if(part_loader_array.length<max_part_load_thread_number){
			part_loader bak[]=part_loader_array;
			part_loader_array=new part_loader[max_part_load_thread_number];
			for(int i=0,ni=bak.length;i<ni;i++)
				part_loader_array[i]=bak[i];
			for(int i=bak.length,ni=part_loader_array.length;i<ni;i++)
				part_loader_array[i]=null;
		}
		for(long last_display_time=0;;){
			int number=0;
			for(int i=0,ni=part_loader_array.length;i<ni;i++){
				if(part_loader_array[i]!=null){
					if(part_loader_array[i].isAlive()){
						if((number++)!=i){
							part_loader_array[number-1]=part_loader_array[i];
							part_loader_array[i]=null;
						}
					}else{
						wait_for_part_loader_termination(part_loader_array[i],false,system_par,scene_par);
						part_loader_array[i]=null;
					}
				}
			}
			long current_display_time=System.nanoTime()/(1000*1000*1000);
			if(number<max_part_load_thread_number)
				last_display_time=current_display_time;
			else{
				if((current_display_time-last_display_time)>5){
					last_display_time=current_display_time;
					debug_information.print  ("Current part loading number is ",	number);
					debug_information.println(", Maximum part loading number is ",	max_part_load_thread_number);				
				}
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				Thread.yield();
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
				continue;
			}
			part_loader my_loader=new part_loader(my_part,
				my_copy_from_part,last_modified_time,system_par,scene_par,pcps);
			part_loader_array[number++]=my_loader;
				
			for(int i=0,ni=already_loaded_part.length;i<ni;i++){
				if(already_loaded_part[i]==null){
					already_loaded_part[i]=my_loader;
					return already_loaded_part;
				}
				if(!(already_loaded_part[i].isAlive())) {
					wait_for_part_loader_termination(already_loaded_part[i],false,system_par,scene_par);
					already_loaded_part[i]=my_loader;
					return already_loaded_part;
				}
			}
			part_loader bak[]=already_loaded_part;
			already_loaded_part=new part_loader[bak.length+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				already_loaded_part[i]=bak[i];
			already_loaded_part[already_loaded_part.length-1]=my_loader;
			return already_loaded_part;
		}
	}
	public part_loader[] load(part my_part,part my_copy_from_part,
			long last_modified_time,system_parameter system_par,scene_parameter scene_par,
			part_loader already_loaded_part[],part_container_for_part_search pcps)
	{
		part_loader ret_val[]=already_loaded_part;
		part_loader_container_lock.lock();
		try{
			ret_val=load_routine(my_part,my_copy_from_part,
				last_modified_time,system_par,scene_par,already_loaded_part,pcps);
		}catch(Exception e) {
			debug_information.println("load of part_loader_container fail");
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		part_loader_container_lock.unlock();
		return ret_val;
	}
}
