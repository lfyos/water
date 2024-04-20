package kernel_interface;

import java.util.Comparator;

import kernel_common_class.debug_information;
import kernel_common_class.nanosecond_timer;
import kernel_common_class.tree_search_container;
import kernel_engine.create_engine_counter;
import kernel_engine.engine_kernel_container_search_tree;
import kernel_engine.system_parameter;

public class client_interface_search_tree 
{
	private tree_search_container<String[],client_interface> tree;
	
	synchronized public client_interface get_client_interface(
		String my_user_name,String my_pass_word,String my_client_id,
		int my_container_id,system_parameter my_system_par,
		engine_kernel_container_search_tree engine_search_tree,
		create_engine_counter engine_counter)
	{
		for(long my_touch_time;(my_touch_time=tree.first_touch_time())>0;){
			int size;
			long time_length=nanosecond_timer.absolute_nanoseconds()-my_touch_time;
			if((size=tree.size())<my_system_par.max_client_interface_number)
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
		
		client_interface ret_val;
		
		if((ret_val=tree.search(new String[] {my_client_id,my_user_name}))!=null) 
			return ret_val;

		ret_val=new client_interface(my_user_name,my_pass_word,
					my_client_id,my_container_id,my_system_par);
		tree.add(new String[] {my_client_id,my_user_name},ret_val);
		
		debug_information.println("Create client_interface,creation request from ",my_client_id);
		debug_information.println("creation user name from is ",my_user_name);
		debug_information.print  ("Active container_number is ",my_container_id);
		debug_information.println("/",my_system_par.max_client_container_number);
		debug_information.print  ("Active client_interface number is  ",tree.size());
		debug_information.println("/",my_system_par.max_client_interface_number);
		
		return ret_val;
	}
	
	public void destroy(
			engine_kernel_container_search_tree engine_search_tree,
			create_engine_counter engine_counter)
	{
		while(tree.first_touch_time()>0) 
			tree.remove(tree.search_key).destroy(engine_search_tree,engine_counter);
	}
	
	public client_interface_search_tree()
	{
		class comparator implements Comparator<String[]>
		{
			public int compare(String[] obj1, String[] obj2)
			{
				int ret_val;
				if((ret_val=obj1[0].compareTo(obj2[0]))!=0)
					return ret_val;
				else
					return obj1[1].compareTo(obj2[1]);
			}
		}
		tree=new tree_search_container<String[],client_interface>(new comparator());
	}
}
