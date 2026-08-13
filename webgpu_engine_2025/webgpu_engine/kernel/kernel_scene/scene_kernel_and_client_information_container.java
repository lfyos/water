package kernel_scene;

import java.util.concurrent.locks.ReentrantLock;

import kernel_client_interface.dispatch_request_main;
import kernel_common_class.debug_information;
import kernel_common_class.tree_string_locker_container;
import kernel_component.component_load_source_container;
import kernel_interface.client_process_bar;
import kernel_interface.user_statistics;
import kernel_network.client_request_response;
import kernel_part.buffer_object_file_modify_time_and_length_container;

public class scene_kernel_and_client_information_container
{
	private volatile int sk_and_ci_processing_number;
	synchronized public int update_sk_and_ci_processing_number(int update_number)
	{
		sk_and_ci_processing_number+=update_number;
		return sk_and_ci_processing_number;
	}
	
	public scene_kernel_container	scene_kernel_cont;
	public client_information 		client_information;
	private volatile int 			kernel_and_client_information_lock_number;
	
	public scene_kernel_and_client_information_container(
			scene_kernel_container my_scene_kernel_cont)
	{
		sk_and_ci_processing_number					=0;
		scene_kernel_cont							=my_scene_kernel_cont;
		client_information							=null;
		kernel_and_client_information_lock_number	=0;
	}
	synchronized public int modify_kernel_and_client_information_lock_number(int modify_number) 
	{
		kernel_and_client_information_lock_number+=modify_number;
		return kernel_and_client_information_lock_number;
	}
	private scene_call_result get_scene_result_routine(client_process_bar process_bar,
			component_load_source_container system_component_load_source_cont,
			buffer_object_file_modify_time_and_length_container system_boftal_container,
			client_request_response my_request_response,long delay_time_length,
			user_statistics statistics_user,create_scene_counter scene_counter,
			tree_string_locker_container string_locker_container)
	{
		if(scene_kernel_cont.sk==null){
			debug_information.println("(sk==null) in function get_scene_result_routine() of scene_kernel_and_client_information_container");
			return null;
		}
		if(scene_kernel_cont.initilization_flag){
			scene_kernel_cont.initilization_flag=false;
			if(scene_kernel_cont.sk.component_cont==null){
				component_load_source_container scene_component_load_source_cont=
							new component_load_source_container(system_component_load_source_cont);
				boolean load_scene_fail_flag=scene_kernel_cont.sk.load(
							scene_component_load_source_cont,my_request_response,
							process_bar,system_boftal_container,string_locker_container);
				scene_component_load_source_cont.destroy();
				
				if(load_scene_fail_flag) {
					scene_kernel_cont.sk.destroy();
					scene_kernel_cont.sk=null;
					return null;
				}
				if(scene_kernel_cont.sk.component_cont.root_component!=null) {
					scene_counter.update_kernel_component_number(1,
							scene_kernel_cont.sk.component_cont.component_number);

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

		return dispatch_request_main.get_scene_result(delay_time_length,
				scene_kernel_cont.sk,client_information,string_locker_container);
	}
	public scene_call_result get_scene_result(client_process_bar process_bar,
			buffer_object_file_modify_time_and_length_container system_boftal_container,
			component_load_source_container system_component_load_source_cont,
			client_request_response my_request_response,long delay_time_length,
			user_statistics statistics_user,create_scene_counter scene_counter,
			tree_string_locker_container string_locker_container)
	{
		scene_call_result ret_val=null;
		ReentrantLock my_lock;
		
		if((my_lock=scene_kernel_cont.scene_kernel_container_lock)!=null){
			my_lock.lock();
			update_sk_and_ci_processing_number(1);
			try{
				ret_val=get_scene_result_routine(
						process_bar,system_component_load_source_cont,
						system_boftal_container,my_request_response,delay_time_length,
						statistics_user,scene_counter,string_locker_container);
			}catch(Exception e){
				e.printStackTrace();
				debug_information.println(
						"get_scene_result function of scene_kernel_link_list fail!");
				debug_information.println(e.toString());
				ret_val=null;
			};
			update_sk_and_ci_processing_number(-1);
			my_lock.unlock();
		}
		return ret_val;
	}
}
