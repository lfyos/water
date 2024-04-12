package kernel_engine;

import java.util.concurrent.locks.ReentrantLock;

import kernel_client_interface.dispatch_request_main;
import kernel_common_class.debug_information;
import kernel_component.component_load_source_container;
import kernel_interface.client_process_bar;
import kernel_interface.user_statistics;
import kernel_network.client_request_response;
import kernel_part.buffer_object_file_modify_time_and_length_container;

public class engine_kernel_and_client_information_container
{
	public engine_kernel_container	engine_kernel_cont;
	public client_information 		client_information;
	public volatile int 			access_lock_number;
	
	public engine_kernel_and_client_information_container(engine_kernel_container my_engine_kernel_cont)
	{
		engine_kernel_cont	=my_engine_kernel_cont;
		client_information	=null;
		access_lock_number	=0;
	}
	private engine_call_result get_engine_result_routine(int my_container_id,
			component_load_source_container component_load_source_cont,client_process_bar process_bar,
			buffer_object_file_modify_time_and_length_container system_boftal_container,
			String client_scene_file_name,String client_scene_file_charset,
			client_request_response my_request_response,long delay_time_length,
			user_statistics statistics_user,create_engine_counter engine_counter)
	{
		if(engine_kernel_cont.ek==null){
			debug_information.println("(ek==null) in function get_engine_result() of engine_container");
			return null;
		}
		if(engine_kernel_cont.initilization_flag){
			engine_kernel_cont.initilization_flag=false;
			if(engine_kernel_cont.ek.component_cont==null){
				component_load_source_cont=new component_load_source_container(component_load_source_cont);
				engine_kernel_cont.ek.load(component_load_source_cont,
						my_request_response,process_bar,system_boftal_container);
				component_load_source_cont.destroy();
				
				if(engine_kernel_cont.ek.component_cont.root_component!=null) {
					engine_counter.update_kernel_component_number(1,
							engine_kernel_cont.ek.component_cont.root_component.component_id+1);

					debug_information.print  ("engine_interface load scene,scene_name:",
							engine_kernel_cont.ek.create_parameter.scene_name);
					debug_information.println(",link_name:",engine_kernel_cont.ek.create_parameter.link_name);
					
					debug_information.print  ("engine_interface engine_kernel_number:",
							engine_counter.engine_kernel_number);
					debug_information.println("/",engine_kernel_cont.ek.system_par.max_engine_kernel_number);
					
					debug_information.print  ("engine_interface engine_component_number:",
							engine_counter.engine_component_number);
					debug_information.println("/",engine_kernel_cont.ek.system_par.max_engine_component_number);
				}
			}
		}
		if(client_information==null){
			if(engine_kernel_cont.ek.component_cont.root_component==null){
				debug_information.println(
					"(ek.component_cont.root_component==null) in function get_engine_result() of engine_container");
				return null;
			}
			client_information=new client_information(my_container_id,
					my_request_response,process_bar,engine_kernel_cont.ek,statistics_user,engine_counter);
		}
		client_information.request_response=my_request_response;

		return dispatch_request_main.get_engine_result(	delay_time_length,engine_kernel_cont.ek,client_information);
	}
	
	public engine_call_result get_engine_result(
			int my_container_id,client_process_bar process_bar,
			buffer_object_file_modify_time_and_length_container system_boftal_container,
			component_load_source_container component_load_source_cont,
			String client_scene_file_name,String client_scene_file_charset,
			client_request_response my_request_response,long delay_time_length,
			user_statistics statistics_user,create_engine_counter engine_counter)
	{
		engine_call_result ret_val=null;
		ReentrantLock my_engine_kernel_container_lock;
		
		if((my_engine_kernel_container_lock=engine_kernel_cont.engine_kernel_container_lock)!=null){
			my_engine_kernel_container_lock.lock();
			
			try{
				ret_val=get_engine_result_routine(my_container_id,
						component_load_source_cont,process_bar,system_boftal_container,
						client_scene_file_name,client_scene_file_charset,my_request_response,
						delay_time_length,statistics_user,engine_counter);
			}catch(Exception e){
				e.printStackTrace();
				debug_information.println(
						"get_engine_result function of engine_kernel_link_list fail!");
				debug_information.println(e.toString());
				ret_val=null;
			};
			
			my_engine_kernel_container_lock.unlock();
		}
		
		return ret_val;
	}
}
