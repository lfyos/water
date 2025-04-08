package kernel_scene;

import java.util.concurrent.locks.ReentrantLock;

import kernel_client_interface.dispatch_request_main;
import kernel_common_class.debug_information;
import kernel_component.component_load_source_container;
import kernel_interface.client_process_bar;
import kernel_interface.user_statistics;
import kernel_network.client_request_response;
import kernel_part.buffer_object_file_modify_time_and_length_container;

public class scene_kernel_and_client_information_container
{
	public scene_kernel_container	scene_kernel_cont;
	public client_information 		client_information;
	private volatile int 			access_lock_number;
	
	public scene_kernel_and_client_information_container(scene_kernel_container my_scene_kernel_cont)
	{
		scene_kernel_cont	=my_scene_kernel_cont;
		client_information	=null;
		access_lock_number	=0;
	}
	synchronized public int lock_number(int modify_number) 
	{
		access_lock_number+=modify_number;
		return access_lock_number;
	}
	private scene_call_result get_scene_result_routine(
			component_load_source_container component_load_source_cont,client_process_bar process_bar,
			buffer_object_file_modify_time_and_length_container system_boftal_container,
			client_request_response my_request_response,long delay_time_length,
			user_statistics statistics_user,create_scene_counter scene_counter)
	{
		if(scene_kernel_cont.sk==null){
			debug_information.println("(sk==null) in function get_scene_result_routine() of scene_kernel_and_client_information_container");
			return null;
		}
		if(scene_kernel_cont.initilization_flag){
			scene_kernel_cont.initilization_flag=false;
			if(scene_kernel_cont.sk.component_cont==null){
				component_load_source_cont=new component_load_source_container(component_load_source_cont);
				scene_kernel_cont.sk.load(component_load_source_cont,
						my_request_response,process_bar,system_boftal_container);
				component_load_source_cont.destroy();
				
				if(scene_kernel_cont.sk.component_cont.root_component!=null) {
					scene_counter.update_kernel_component_number(1,
							scene_kernel_cont.sk.component_cont.root_component.component_id+1);

					debug_information.print  ("scene_interface load scene,scene_name:",
							scene_kernel_cont.sk.scene_name);
					debug_information.println(",link_name:",
							scene_kernel_cont.sk.link_name);
					
					debug_information.print  ("scene_interface scene_kernel_number:",
							scene_counter.scene_kernel_number);
					debug_information.println("/",
							scene_kernel_cont.sk.system_par.max_scene_kernel_number);
					
					debug_information.print  ("scene_interface scene_component_number:",
							scene_counter.scene_component_number);
					debug_information.println("/",
							scene_kernel_cont.sk.system_par.max_scene_component_number);
				}
			}
		}
		if(client_information==null){
			if(scene_kernel_cont.sk.component_cont.root_component==null){
				debug_information.println(
					"(sk.component_cont.root_component==null) in function get_scene_result() of scene_kernel_and_client_information_container");
				return null;
			}
			client_information=new client_information(my_request_response,
					process_bar,scene_kernel_cont.sk,statistics_user,scene_counter);
		}
		client_information.request_response=my_request_response;

		return dispatch_request_main.get_scene_result(
					delay_time_length,scene_kernel_cont.sk,client_information);
	}
	
	public scene_call_result get_scene_result(client_process_bar process_bar,
			buffer_object_file_modify_time_and_length_container system_boftal_container,
			component_load_source_container component_load_source_cont,
			client_request_response my_request_response,long delay_time_length,
			user_statistics statistics_user,create_scene_counter scene_counter)
	{
		scene_call_result ret_val=null;
		ReentrantLock my_lock;
		
		if((my_lock=scene_kernel_cont.scene_kernel_container_lock)!=null){
			my_lock.lock();
			
			try{
				ret_val=get_scene_result_routine(
						component_load_source_cont,process_bar,system_boftal_container,
						my_request_response,delay_time_length,statistics_user,scene_counter);
			}catch(Exception e){
				e.printStackTrace();
				debug_information.println(
						"get_scene_result function of scene_kernel_link_list fail!");
				debug_information.println(e.toString());
				ret_val=null;
			};
			my_lock.unlock();
		}
		return ret_val;
	}
}
