package kernel_interface;

import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.debug_information;
import kernel_common_class.nanosecond_timer;
import kernel_common_class.tree_string_search_container;
import kernel_network.client_request_response;
import kernel_scene.create_scene_counter;
import kernel_scene.scene_kernel_container_search_tree;
import kernel_scene.system_parameter;

public class client_interface_search_tree 
{
	private volatile ReentrantLock client_interface_search_tree_lock;
	private tree_string_search_container<client_interface> tree;
	
	private void process_timeout_client_interface(
			boolean test_timeout_flag,system_parameter my_system_par,
			scene_kernel_container_search_tree scene_search_tree,
			create_scene_counter scene_counter)
	{
		for(long my_touch_time;(my_touch_time=tree.first_touch_time())>0;){
			String 				my_key[]=tree.get_first_key();
			client_interface 	my_value=tree.get_first_value();
			
			int size=tree.size();
			long time_length=nanosecond_timer.absolute_nanoseconds()-my_touch_time;
			
			if(test_timeout_flag)
				if(size<my_system_par.max_client_interface_number)
					if(time_length<my_system_par.scene_expire_time_length)
						break;
			debug_information.println("Delete client_interface, client id is ",my_key[0]);
			debug_information.println("Delete client_interface, user name is ",my_key[1]);
			debug_information.print  ("Time interval ",time_length);
			debug_information.println(", max time interval  ",my_system_par.scene_expire_time_length);
			debug_information.print  ("Still active client_interface number is  ",size-1);
			debug_information.println("/",my_system_par.max_client_interface_number);
			
			my_value.destroy(scene_search_tree,scene_counter);
			tree.remove(my_key);
		}
	}
	public client_interface get_client_interface(
			client_request_response request_response,
			system_parameter my_system_par,
			scene_kernel_container_search_tree scene_search_tree,
			create_scene_counter scene_counter)
	{
		client_interface ret_val;
		
		ReentrantLock my_lock;
		if((my_lock=client_interface_search_tree_lock)==null)
			return null;
		my_lock.lock();
		
		process_timeout_client_interface(true,my_system_par,scene_search_tree,scene_counter);

		if((ret_val=tree.search(new String[] {request_response.client_id,request_response.user_name}))==null){
			ret_val=client_interface.create(request_response,my_system_par,scene_search_tree,scene_counter);
			
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
			scene_kernel_container_search_tree scene_search_tree,
			create_scene_counter scene_counter)
	{
		ReentrantLock my_lock;
		if((my_lock=client_interface_search_tree_lock)==null)
			return;
		my_lock.lock();
		
		process_timeout_client_interface(false,
			my_system_par,scene_search_tree,scene_counter);

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
