package kernel_interface;

import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.nanosecond_timer;
import kernel_engine.system_parameter;
import kernel_network.client_request_response;
import kernel_common_class.balance_tree;
import kernel_common_class.balance_tree_item;
import kernel_common_class.debug_information;

final class client_interface_node extends balance_tree_item
{
	public String client_id;
	public client_interface ci;
	
	public client_interface_node front,back;
	
	public void destroy()
	{
		client_id=null;
		ci=null;
		front=null;
		back=null;
	}
	public int compare(balance_tree_item t)
	{
		int ret_val=1;
		if(t instanceof client_interface_node) {
			client_interface_node p=(client_interface_node)t;
			ret_val=client_id.compareTo(p.client_id);
		}
		return ret_val;
	}
	public balance_tree_item clone()
	{
		client_interface_node ret_val=new client_interface_node(client_id);
		ret_val.ci=ci;
		return ret_val;
	}
	public client_interface_node(String my_client_id)
	{
		client_id=my_client_id;
		ci=null;
		front=null;
		back=null;
	}
}

public class client_interface_container
{
	private int client_interface_number;
	private balance_tree bt;
	private client_interface_node first,last;
	private ReentrantLock client_interface_container_lock;
	
	public void destroy()
	{
		if(bt!=null) {
			bt.destroy();
			bt=null;
		}
		if(first!=null) {
			while(first!=null) {
				client_interface_node p=first;
				first=first.back;
				p.destroy();
			}
			first=null;
		}
		
		if(last!=null) {
			while(last!=null) {
				client_interface_node p=last;
				last=last.front;
				p.destroy();
			}
			last=null;
		}
		
		if(client_interface_container_lock!=null) {
			client_interface_container_lock.unlock();
			client_interface_container_lock=null;
		}
	}
	private void process_timeout(int max_client_interface_number,long client_interface_life_time)
	{
		while(first!=null){
			long time_length=nanosecond_timer.absolute_nanoseconds()-first.ci.touch_time;
			if(client_interface_number<max_client_interface_number) 
				if(time_length<client_interface_life_time)
					break;
			debug_information.println(
					"Delete client_interface, client id is ",first.client_id);
			debug_information.print  ("Time interval ",time_length);
			debug_information.println(
					", max time interval  ",client_interface_life_time);
			debug_information.print  (
					"Still active client_interface number is  ",--client_interface_number);
			debug_information.println("/",max_client_interface_number);
			
			client_interface_node p=(client_interface_node)(bt.search(new client_interface_node(first.client_id),-1));
			if(first==last){
				bt.destroy();
				bt=null;
				first=null;
				last=null;
			}else{
				first=first.back;
				first.front=null;
			}
			p.ci.destroy();
			p.destroy();
		}
	}
	private void print_client_interface_information(String my_client_id,int max_client_interface_number)
	{
		debug_information.println(
				"Create client_interface, creation request from ",my_client_id);
		debug_information.print  (
				"Active client_interface number is  ",client_interface_number);
		debug_information.println("/",max_client_interface_number);
	}
	private client_interface get_client_interface_routine(String my_client_id,system_parameter my_system_par)
	{
		client_interface_node front,back,p,new_p=new client_interface_node(my_client_id);
		
		if(bt==null){
			p=new_p;
			p.ci=new client_interface(my_system_par);
			p.front=null;
			p.back=null;
			
			first=p;
			last=p;
			
			bt=new balance_tree(p);

			client_interface_number++;
			print_client_interface_information(my_client_id,my_system_par.max_client_interface_number);
			
			return p.ci;
		}
		
		if((p=(client_interface_node)bt.search(new_p,1))==null){
			p=new_p;
			p.ci=new client_interface(my_system_par);
			
			client_interface_number++;
			print_client_interface_information(my_client_id,my_system_par.max_client_interface_number);
		}else{
			p.ci.touch_time=nanosecond_timer.absolute_nanoseconds();
			if(p==last)
				return p.ci;
			if(p==first){
				first=first.back;
				first.front=null;
			}else{
				front=p.front;
				back=p.back;
				front.back=back;
				back.front=front;
			}
		}
		p.front=last;
		p.back=null;
		
		last.back=p;
		last=p;
		
		return p.ci;
	}
	public client_interface get_client_interface(
			client_request_response request_response,system_parameter my_system_par)
	{
		client_interface ret_val=null;
		
		String my_user_name=request_response.get_parameter("user_name");
		String my_pass_word=request_response.get_parameter("pass_word");
		String my_client_id=request_response.implementor.get_client_id();
		
		my_user_name=(my_user_name==null)?"NoName":my_user_name;
		my_pass_word=(my_pass_word==null)?"NoPassword":my_pass_word;
		
		client_interface_container_lock.lock();
		try {
			if((ret_val=get_client_interface_routine(my_client_id+"/"+my_user_name,my_system_par))!=null)
				if(ret_val.test_client_interface(my_user_name, my_pass_word,my_client_id))
					ret_val=null;
			
			process_timeout(my_system_par.max_client_interface_number,my_system_par.engine_expire_time_length);
		}catch(Exception e) {
			debug_information.println("get_client_interface exception:\t",e.toString());
			e.printStackTrace();
		}
		client_interface_container_lock.unlock();
		
		return ret_val;
	}
	public client_interface_container()
	{
		client_interface_number=0;

		bt		=null;
		first	=null;
		last	=null;
		
		client_interface_container_lock=new ReentrantLock();
	}
}
