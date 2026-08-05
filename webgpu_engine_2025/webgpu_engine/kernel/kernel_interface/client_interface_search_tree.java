package kernel_interface;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.debug_information;
import kernel_common_class.nanosecond_timer;
import kernel_common_class.tree_string_locker_container;
import kernel_common_class.tree_string_search_container;
import kernel_network.client_request_response;
import kernel_scene.create_scene_counter;
import kernel_scene.scene_kernel_container_search_tree;
import kernel_scene.system_parameter;

public class client_interface_search_tree 
{
	private system_parameter system_par;
	private ReentrantLock client_interface_search_tree_lock;
	private tree_string_search_container<client_interface> tree;
	
	private void process_timeout_client_interface(boolean test_timeout_flag,create_scene_counter scene_counter)
	{
		for(long my_touch_time;(my_touch_time=tree.first_touch_time())>0;){
			String 							my_client_id_and_user_name[]=tree.first_key();
			ArrayList<client_interface> 	my_client_interface_list	=tree.first_value();
			
			int size=tree.size();
			long time_length=nanosecond_timer.absolute_nanoseconds()-my_touch_time;
			
			if(test_timeout_flag)
				if(size<system_par.max_client_interface_number)
					if(time_length<system_par.scene_expire_time_length)
						break;
			debug_information.println("Delete client_interface, client id is ",my_client_id_and_user_name[0]);
			debug_information.println("Delete client_interface, user name is ",my_client_id_and_user_name[1]);
			debug_information.print  ("Time interval ",time_length);
			debug_information.println(", max time interval  ",system_par.scene_expire_time_length);
			debug_information.print  ("Still active client_interface number is  ",size-1);
			debug_information.println("/",system_par.max_client_interface_number);
			
			for(int i=my_client_interface_list.size()-1;i>=0;i--) {
				client_interface p=my_client_interface_list.get(i);
				if(test_timeout_flag)
					if(p.operate_client_interface_in_processing_number(0)>0) {
						tree.search(my_client_id_and_user_name);
						return;
					}
				my_client_interface_list.remove(i);
				p.destroy();
			}
			tree.remove(my_client_id_and_user_name);
		}
	}
	public client_interface get_client_interface(client_request_response request_response,
			scene_kernel_container_search_tree scene_search_tree,
			tree_string_locker_container string_locker_container,create_scene_counter scene_counter)
	{
		ReentrantLock my_lock;
		if((my_lock=client_interface_search_tree_lock)==null)
			return null;
		my_lock.lock();
		
		process_timeout_client_interface(true,scene_counter);
		
		client_interface my_client_interface;
		ArrayList<client_interface> my_client_interface_list=tree.search(
				new String[] {request_response.client_id,request_response.user_name});

		if(my_client_interface_list!=null){
			if(my_client_interface_list.size()>0)
				my_client_interface=my_client_interface_list.get(0);
			else if((my_client_interface=client_interface.create(request_response,
					scene_search_tree,string_locker_container,scene_counter,system_par))==null)
				debug_information.println("Create client_interface fail");
			else{
				debug_information.println("Create client_interface success");
				my_client_interface_list.add(my_client_interface);
			}
		}else{
			if((my_client_interface=client_interface.create(request_response,
					scene_search_tree,string_locker_container,scene_counter,system_par))==null) 
				debug_information.println("Create client_interface fail");
			else{
				debug_information.println("Create client_interface success");
				tree.add(new String[] {request_response.client_id,request_response.user_name},my_client_interface);
			}
			
			debug_information.print  ("Creation request from ",request_response.client_id);
			debug_information.println(",user name is ",request_response.user_name);
			debug_information.print  ("Active container_number is ",request_response.container_id);
			debug_information.println("/",system_par.max_client_container_number);
			debug_information.print  ("Active client_interface number is  ",tree.size());
			debug_information.println("/",system_par.max_client_interface_number);
		}
		
		my_lock.unlock();
		
		return my_client_interface;
	}
	public void destroy(create_scene_counter scene_counter)
	{
		ReentrantLock my_lock;
		if((my_lock=client_interface_search_tree_lock)==null)
			return;
		my_lock.lock();
		
		process_timeout_client_interface(false,scene_counter);

		system_par	=null;
		tree		=null;
		client_interface_search_tree_lock=null;

		my_lock.unlock();
	}
	public client_interface_search_tree(system_parameter my_system_par)
	{
		system_par=new system_parameter(my_system_par);
		tree=new tree_string_search_container<client_interface>();
		client_interface_search_tree_lock=new ReentrantLock();
	}
}
