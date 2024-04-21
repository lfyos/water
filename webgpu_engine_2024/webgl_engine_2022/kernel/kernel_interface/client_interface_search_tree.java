package kernel_interface;

import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.debug_information;
import kernel_common_class.nanosecond_timer;
import kernel_common_class.tree_string_search_container;
import kernel_engine.create_engine_counter;
import kernel_engine.engine_kernel_container_search_tree;
import kernel_engine.system_parameter;
import kernel_network.client_request_response;

public class client_interface_search_tree 
{
	private ReentrantLock client_interface_search_tree_lock;
	private tree_string_search_container<client_interface> tree;
	
	private void delete_client_interface(
			boolean test_timeout_flag,system_parameter my_system_par,
			engine_kernel_container_search_tree engine_search_tree,
			create_engine_counter engine_counter)
	{
		for(long my_touch_time;(my_touch_time=tree.first_touch_time())>0;){
			int size=tree.size();
			long time_length=nanosecond_timer.absolute_nanoseconds()-my_touch_time;
			
			if(test_timeout_flag)
				if(size<my_system_par.max_client_interface_number)
					if(time_length<my_system_par.engine_expire_time_length)
						break;

			debug_information.println("Delete client_interface, client id is ",tree.search_key[0]);
			debug_information.println("Delete client_interface, user name is ",tree.search_key[1]);
			debug_information.print  ("Time interval ",time_length);
			debug_information.println(", max time interval  ",my_system_par.engine_expire_time_length);
			debug_information.print  ("Still active client_interface number is  ",size-1);
			debug_information.println("/",my_system_par.max_client_interface_number);
				
			tree.remove(tree.search_key).destroy(engine_search_tree,engine_counter);
		}
	}
	public client_interface get_client_interface(
			client_request_response request_response,
			system_parameter my_system_par,
			engine_kernel_container_search_tree engine_search_tree,
			create_engine_counter engine_counter)
	{
		ReentrantLock my_lock;
		client_interface ret_val;
		
		if((my_lock=client_interface_search_tree_lock)==null)
			return null;
		
		my_lock.lock();
		
		delete_client_interface(true,my_system_par,engine_search_tree,engine_counter);

		if((ret_val=tree.search(new String[] {request_response.client_id,request_response.user_name}))==null){
			ret_val=client_interface.create(request_response,my_system_par,engine_search_tree,engine_counter);
			
			if(ret_val==null) 
				debug_information.println("Create client_interface fail");
			else{
				debug_information.println("Create client_interface success");
				tree.add(new String[] {request_response.client_id,request_response.user_name},ret_val);
			}
			
			debug_information.print  ("Creation request from ",request_response.client_id);
			debug_information.println(",user name is ",request_response.user_name);
			debug_information.print  ("Active container_number is ",request_response.container_id);
			debug_information.println("/",my_system_par.max_client_container_number);
			debug_information.print  ("Active client_interface number is  ",tree.size());
			debug_information.println("/",my_system_par.max_client_interface_number);
		}
		
		my_lock.unlock();
		
		return ret_val;
	}
	
	public void destroy(system_parameter my_system_par,
			engine_kernel_container_search_tree engine_search_tree,
			create_engine_counter engine_counter)
	{
		ReentrantLock my_lock;
		
		if((my_lock=client_interface_search_tree_lock)==null)
			return;
		
		my_lock.lock();
		
		delete_client_interface(false,
			my_system_par,engine_search_tree,engine_counter);

		tree=null;
		client_interface_search_tree_lock=null;

		my_lock.unlock();
	}
	public client_interface_search_tree()
	{
		tree=new tree_string_search_container<client_interface>();
		client_interface_search_tree_lock=new ReentrantLock();
	}
}
