package kernel_interface;

import kernel_common_class.nanosecond_timer;
import kernel_engine.system_parameter;
import kernel_common_class.balance_tree;
import kernel_common_class.balance_tree_item;
import kernel_common_class.debug_information;

public class client_interface_container
{
	class client_interface_balance_tree_node extends balance_tree_item<String>
	{
		public client_interface interface_client;
		public client_interface_balance_tree_node front,back;
		
		public void destroy()
		{
			super.destroy();
			
			if(interface_client!=null) {
				interface_client.destroy();
				interface_client=null;
			}
			front=null;
			back=null;
		}
		public int compare(String t)
		{
			return compare_data.compareTo(t);
		}
		public client_interface_balance_tree_node(String my_client_id)
		{
			super(my_client_id);
			
			interface_client=null;
			front=null;
			back=null;
		}
	}

	private int client_interface_number;
	private balance_tree<String,client_interface_balance_tree_node> bt;
	private client_interface_balance_tree_node first,last;
	
	public void destroy()
	{
		if(bt!=null) {
			bt.destroy();
			bt=null;
		}
		while(first!=null) {
			client_interface_balance_tree_node p=first;
			first=first.back;
			p.destroy();
		}
		while(last!=null) {
			client_interface_balance_tree_node p=last;
			last=last.front;
			p.destroy();
		}
	}
	private void process_timeout(int max_client_interface_number,long client_interface_life_time)
	{
		while(first!=null){
			long time_length=nanosecond_timer.absolute_nanoseconds()-first.interface_client.touch_time;
			if(client_interface_number<max_client_interface_number) 
				if(time_length<client_interface_life_time)
					return;

			debug_information.println("Delete client_interface, client id is ",first.compare_data);
			debug_information.print  ("Time interval ",time_length);
			debug_information.println(", max time interval  ",client_interface_life_time);
			debug_information.print  ("Still active client_interface number is  ",client_interface_number-1);
			debug_information.println("/",max_client_interface_number);

			client_interface_balance_tree_node p;
			p=bt.search(new client_interface_balance_tree_node(first.compare_data),false,true);

			if(first==last){
				bt.destroy();
				bt=null;
				first=null;
				last=null;
				client_interface_number=0;
			}else{
				first=first.back;
				first.front=null;
				client_interface_number--;
			}
			p.destroy();
		}
	}
	private void print_client_interface_information(
			String my_client_id,int my_container_id,int max_client_container_number,int max_client_interface_number)
	{
		debug_information.println("Create client_interface, creation request from ",my_client_id);
		debug_information.print  ("Active container_number is ",my_container_id);
		debug_information.println("/",max_client_container_number);
		debug_information.print  ("Active client_interface number is  ",client_interface_number);
		debug_information.println("/",max_client_interface_number);
	}
	private client_interface get_client_interface_routine(
			String my_user_name,String my_pass_word,String my_client_id,
			int my_container_id,system_parameter my_system_par)
	{
		client_interface_balance_tree_node p,new_p=new client_interface_balance_tree_node(my_client_id+"/"+my_user_name);
		
		if(bt==null){
			p=new_p;
			p.interface_client=new client_interface(my_container_id,my_user_name,my_pass_word,my_client_id,my_system_par);
			
			p.front=null;
			p.back=null;
			
			first=p;
			last=p;
			
			bt=new balance_tree<String,client_interface_balance_tree_node>(p);

			client_interface_number=1;
			print_client_interface_information(my_client_id,my_container_id,
					my_system_par.max_client_container_number,my_system_par.max_client_interface_number);
			
			return p.interface_client;
		}
		if((p=bt.search(new_p,true,false))==null){
			p=new_p;
			p.interface_client=new client_interface(my_container_id,my_user_name,my_pass_word,my_client_id,my_system_par);
			client_interface_number++;
			print_client_interface_information(my_client_id,my_container_id,
					my_system_par.max_client_container_number,my_system_par.max_client_interface_number);
		}else{
			p.interface_client.touch_time=nanosecond_timer.absolute_nanoseconds();
			if(first==last)
				return p.interface_client;
			if(p==first){
				first=first.back;
				first.front=null;
			}else if(p==last){
				last=last.front;
				last.back=null;
			}else{
				client_interface_balance_tree_node front=p.front;
				client_interface_balance_tree_node back=p.back;
				front.back=back;
				back.front=front;
			}
		}
		if(p.interface_client.touch_time<=0) {
			p.front=null;
			p.back=first;
			
			first.front=p;
			first=p;
			return null;
		}else{
			p.front=last;
			p.back=null;
			
			last.back=p;
			last=p;
			return p.interface_client;
		}
	}
	synchronized public client_interface get_client_interface(
		String my_user_name,String my_pass_word,String my_client_id,
		int my_container_id,system_parameter my_system_par)
	{
		try{
			client_interface ret_val=get_client_interface_routine(
					my_user_name,my_pass_word,my_client_id,my_container_id,my_system_par);
			process_timeout(my_system_par.max_client_interface_number,my_system_par.engine_expire_time_length);
			return ret_val;
		}catch(Exception e) {
			debug_information.println("get_client_interface exception:\t",e.toString());
			e.printStackTrace();
			return null;
		}
	}
	public client_interface_container()
	{
		client_interface_number=0;
		bt		=null;
		first	=null;
		last	=null;
	}
}
