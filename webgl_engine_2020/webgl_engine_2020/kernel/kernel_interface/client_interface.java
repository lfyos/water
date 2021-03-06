package kernel_interface;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.locks.ReentrantLock;

import kernel_security.delay_manager;
import kernel_common_class.debug_information;
import kernel_common_class.balance_tree;
import kernel_common_class.balance_tree_item;
import kernel_common_class.nanosecond_timer;
import kernel_engine.engine_kernel_link_list;
import kernel_engine.engine_kernel_link_list_and_client_information;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_engine.engine_call_result;
import kernel_engine.interface_statistics;
import kernel_engine.engine_kernel;
import kernel_security.client_session;


final class ek_ci_node extends balance_tree_item
{
	public long channel_id;
	public engine_kernel_link_list_and_client_information ek_ci;
	public ek_ci_node front,back;
	
	public void destroy()
	{
		ek_ci=null;
		front=null;
		back=null;
	}
	public int compare(balance_tree_item t)
	{
		int ret_val=1;
		if(t instanceof ek_ci_node) {
			ek_ci_node p=(ek_ci_node)t;
			ret_val=(channel_id<p.channel_id)?-1:(channel_id>p.channel_id)?1:0;
		}
		return ret_val;
	}
	public balance_tree_item clone()
	{
		ek_ci_node ret_val=new ek_ci_node(channel_id);
		ret_val.ek_ci=ek_ci;
		return ret_val;
	}
	public ek_ci_node(long my_channel_id)
	{
		channel_id=my_channel_id;
		ek_ci=null;
		front=null;
		back=null;
	}
}

public class client_interface
{
	public long touch_time;

	private system_parameter system_par;
	private delay_manager manager_delay;
	private client_session session;

	private balance_tree bt;
	private ek_ci_node first,last;

	private volatile ReentrantLock client_interface_lock;
	private volatile boolean has_been_destroyd_flag;
	
	public void destroy()
	{
		ReentrantLock my_client_interface_lock=client_interface_lock;
		my_client_interface_lock.lock();
		has_been_destroyd_flag=true;
		try{
			destroy_routine();
		}catch(Exception e) {
			debug_information.println("destroy of client_interface_base fail:\t",e.toString());
			e.printStackTrace();
		}
		my_client_interface_lock.unlock();
	}
	private void destroy_routine()
	{	
		for(ek_ci_node p=first;p!=null;p=p.back)
			if(p.ek_ci!=null)
				if(p.ek_ci.client_information!=null) {
					p.ek_ci.engine_kernel_link_list.destroy_client_information(p.ek_ci.client_information);
					p.ek_ci.client_information=null;
				}
		for(ek_ci_node p=first;p!=null;p=p.back)
			if(p.ek_ci!=null)
				if(p.ek_ci.engine_kernel_link_list!=null) {
					p.ek_ci.engine_kernel_link_list.decrease_link_number();
					p.ek_ci.engine_kernel_link_list=null;
				}
		for(ek_ci_node p=first,q;p!=null;){
			q=p;
			p=p.back;
			q.destroy();
		}
		first=null;
		last=null;
		
		if(bt!=null) {
			bt.destroy();
			bt=null;
		}
		
		if(system_par!=null)
			system_par=null;

		if(manager_delay!=null)
			manager_delay=null;

		client_interface_lock=null;
	}
	
	private boolean destroy_ek_ci_node(ek_ci_node ecn)
	{
		if(ecn==null)
			return true;
		if(ecn.ek_ci.lock_number>0)
			return true;
		engine_kernel ek;
		if((ek=ecn.ek_ci.engine_kernel_link_list.ek)!=null){
			session.statistics_user.user_kernel_number--;
			if(ek.component_cont!=null)
				if(ek.component_cont.root_component!=null)
					session.statistics_user.user_component_number-=ek.component_cont.root_component.component_id+1;
		}
		debug_information.println("Execute destroy_ek_ci_node");
		debug_information.print  ("kernel_number:",session.statistics_user.user_kernel_number);
		debug_information.println("/",session.statistics_user.max_user_kernel_number);
		debug_information.print  ("component_number:",session.statistics_user.user_component_number);
		debug_information.println("/",session.statistics_user.max_user_component_number);
		
		if(ecn.ek_ci.client_information!=null) {
			ecn.ek_ci.engine_kernel_link_list.destroy_client_information(ecn.ek_ci.client_information);
			ecn.ek_ci.client_information=null;
		}
		if(ecn.ek_ci.engine_kernel_link_list!=null) {
			ecn.ek_ci.engine_kernel_link_list.decrease_link_number();
			ecn.ek_ci.engine_kernel_link_list=null;
		}
		if(first==last){
			bt.destroy();
			bt=null;

			first=null;
			last=null;
		}else{
			bt.search(ecn,-1);
			if(ecn.front==null){
				first=first.back;
				first.front=null;
			}else if(ecn.back==null){
				last=last.front;
				last.back=null;
			}else{
				ecn.front.back=ecn.back;
				ecn.back.front=ecn.front;
			}
		}
		ecn.destroy();
		return false;
	}
	private void process_timeout(client_request_response request_response)
	{
		while(first!=null){
			if(first.ek_ci.lock_number>0)
				return;
			if((first.ek_ci.client_information==null)||(first.ek_ci.engine_kernel_link_list==null)) {
				debug_information.print  ("Delete time out client_information found, client id is ");
				debug_information.println(request_response.implementor.get_client_id());
				debug_information.println(
					"((first.ek_ci.client_information==null)||(first.ek_ci.engine_kernel_link_list==null))");
			}else {
				long request_time=first.ek_ci.client_information.request_response.request_time;
				long time_length=nanosecond_timer.absolute_nanoseconds()-request_time;
				if(time_length<system_par.engine_expire_time_length)
					return;
				debug_information.print  ("Delete time out client_information found, client id is ");
				debug_information.println(request_response.implementor.get_client_id());
				
				debug_information.print  ("Channel is ",first.ek_ci.client_information.channel_id);
				debug_information.print  (", time interval ",time_length);
				debug_information.println(", max time interval  ",system_par.engine_expire_time_length);
			}
			if(destroy_ek_ci_node(first))
				break;
		}
	}
	private engine_call_result create_engine_routine(
			client_session session,engine_interface ei,
			client_request_response request_response,long delay_time_length,
			interface_statistics statistics_interface)
	{
		if(session.statistics_user.user_component_number>session.statistics_user.max_user_component_number) {
			debug_information.print  ("Create too many component\t\t:\t",session.statistics_user.user_component_number);
			debug_information.println("/",session.statistics_user.max_user_component_number);
			return null;
		}
		if(session.statistics_user.user_kernel_number>session.statistics_user.max_user_kernel_number) {
			debug_information.print  ("Create too many engine\t\t\t:\t",session.statistics_user.user_kernel_number);
			debug_information.println("/",session.statistics_user.max_user_kernel_number);
			return null;
		}

		engine_kernel_link_list create_ekll=null;
		ReentrantLock my_client_interface_lock=client_interface_lock;
		
		my_client_interface_lock.unlock();
		try{
			create_ekll=ei.get_kernel_container(session,request_response,system_par);
		}catch(Exception e) {
			create_ekll=null;
			debug_information.println(
					"ei.get_kernel_container(request_response,system_par,user_par.scene_file_name) fail");
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		my_client_interface_lock.lock();
		
		if(create_ekll==null) {
			debug_information.println("Create engine result is null");
			return null;
		}
		if(has_been_destroyd_flag) {
			create_ekll.destroy();
			debug_information.println("Client_interface has done annihilation when create engine result");
			return null;
		}
		
		engine_kernel_link_list_and_client_information ec;
		ec=new engine_kernel_link_list_and_client_information(create_ekll);

		debug_information.println();
		debug_information.print  (request_response.implementor.get_client_id(),"	succed in creating engine");
		debug_information.print  (", link number is ",create_ekll.get_link_number());
		
		if(create_ekll.ek==null)
			debug_information.println(",create_ekll.ek==null");
		else if(create_ekll.ek.component_cont==null)
			debug_information.println(", assemble has not been loaded yet");
		else
			debug_information.println(", assemble has already been loaded");

		ec.lock_number++;
		my_client_interface_lock.unlock();
		
		engine_call_result ecr=null;
		try{
			ecr=ec.engine_kernel_link_list.get_engine_result(session,ec,request_response,
					delay_time_length,statistics_interface,ei.engine_current_number);
		}catch(Exception e) {
			ecr=null;
			debug_information.println("ec[channel_id].engine_kernel_link_list.get_engine_result fail");
			e.printStackTrace();
		}
		
		my_client_interface_lock.lock();
		ec.lock_number--;
		
		if(has_been_destroyd_flag) {
			create_ekll.destroy();
			debug_information.println("Client_interface has done annihilation when get_engine_result");
			return null;
		}
		if(create_ekll.ek!=null){
			session.statistics_user.user_kernel_number++;
			if(create_ekll.ek.component_cont!=null)
				if(create_ekll.ek.component_cont.root_component!=null)
					session.statistics_user.user_component_number+=create_ekll.ek.component_cont.root_component.component_id+1;
		}
		debug_information.print  (request_response.implementor.get_client_id());
		debug_information.print  (":	Current created engine number is ",session.statistics_user.user_kernel_number);
		debug_information.print  ("/",session.statistics_user.max_user_kernel_number);
		debug_information.print  (",Current created component number is ",session.statistics_user.user_component_number);
		debug_information.println("/",session.statistics_user.max_user_component_number);
		
		ek_ci_node ecn=new ek_ci_node((ec.client_information==null)
				?(-(request_response.request_time)):(ec.client_information.channel_id));
		ecn.ek_ci=ec;
		
		if(bt==null){
			bt=new balance_tree(ecn);
			first=ecn;
			last=ecn;
		}else{
			bt.search(ecn,1);
			ecn.front=last;
			last.back=ecn;
			last=ecn;
		}
		return ecr;
	}
	private engine_call_result create_engine(engine_interface ei,
			client_session session,client_request_response request_response,
			long delay_time_length,interface_statistics statistics_interface)
	{
		debug_information.println();
		debug_information.println("\n#####################################################################################################");
		
		Calendar now = Calendar.getInstance();  
		debug_information.print  ("Request date and time 		:	",now.get(Calendar.YEAR));  
		debug_information.print  ("-",(now.get(Calendar.MONTH) + 1));  
		debug_information.print  ("-",now.get(Calendar.DAY_OF_MONTH));  
		debug_information.print  ("/",now.get(Calendar.HOUR_OF_DAY));  
		debug_information.print  (":",now.get(Calendar.MINUTE));  
		debug_information.println(":",now.get(Calendar.SECOND));
		
		debug_information.println("Request Client ID		:	",	request_response.implementor.get_client_id());
		debug_information.println("Request user name		:	",	request_response.get_parameter("user_name"));
		debug_information.println();
		
		debug_information.println("data_root_directory_name	:	",			system_par.data_root_directory_name);
		debug_information.println("shader_file_name		:	",				system_par.shader_file_name);
		debug_information.println("user_file_name			:	",			system_par.user_file_name);
		debug_information.println("default_parameter_directory	:	",		system_par.default_parameter_directory);
		debug_information.println("proxy_data_root_directory_name:		",	system_par.proxy_par.proxy_data_root_directory_name);
		
		engine_call_result ret_val=create_engine_routine(session,ei,request_response,delay_time_length,statistics_interface);
		
		now = Calendar.getInstance();  
		debug_information.print  ("Finish date and time\t\t\t:\t",now.get(Calendar.YEAR));  
		debug_information.print  ("-",(now.get(Calendar.MONTH) + 1));  
		debug_information.print  ("-",now.get(Calendar.DAY_OF_MONTH));  
		debug_information.print  ("/",now.get(Calendar.HOUR_OF_DAY));  
		debug_information.print  (":",now.get(Calendar.MINUTE));  
		debug_information.println(":",now.get(Calendar.SECOND));
 
		debug_information.println("\n#####################################################################################################");
		debug_information.println();
		
		return ret_val;
	}
	private engine_call_result execute_system_call_routine(long channel_id,
			client_session session,client_request_response request_response,
			interface_statistics statistics_interface,int engine_current_number[])
	{
		long delay_time_length;
		if((delay_time_length=manager_delay.process_delay_time_length())<0){
			debug_information.println("TIME OUT FOUND,Client ID is ",
					request_response.implementor.get_client_id());
			return null;
		}
		if(bt==null){
			debug_information.print  ("No client_interface exist,Client ID is ",
					request_response.implementor.get_client_id());
			debug_information.println(",channel_id is ",channel_id);
			return null;
		}
		
		balance_tree_item bti=bt.search(new ek_ci_node(channel_id),0);
		if(bti==null) {
			debug_information.print  ("Search client_interface fail,Client ID is ",
					request_response.implementor.get_client_id());
			debug_information.println(",channel_id is ",channel_id);
			return null;
		}
		if(!(bti instanceof ek_ci_node)){
			debug_information.print  ("Search client_interface find wrong node,Client ID is ",
					request_response.implementor.get_client_id());
			debug_information.println(",channel_id is ",channel_id);
			return null;
		}
		
		ek_ci_node ecn=(ek_ci_node)bti;
		if(ecn.ek_ci.client_information==null){
			debug_information.println("ecn.ek_ci.client_information==null,Client ID is ",
					request_response.implementor.get_client_id());
			debug_information.println(",channel_id is ",channel_id);
			return null;
		}
		if(ecn.ek_ci.engine_kernel_link_list==null){
			debug_information.println("ecn.ek_ci.engine_kernel_link_list==null,Client ID is ",
					request_response.implementor.get_client_id());
			debug_information.println(",channel_id is ",channel_id);
			return null;
		}

		engine_call_result ecr=null;
		
		ecn.ek_ci.lock_number++;
		client_interface_lock.unlock();
		try{
			ecr=ecn.ek_ci.engine_kernel_link_list.get_engine_result(session,ecn.ek_ci,
					request_response,delay_time_length,statistics_interface,engine_current_number);
		}catch(Exception e){
			ecr=null;
			debug_information.println("ecn.ek_ci.engine_kernel_link_list.get_engine_result fail");
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		client_interface_lock.lock();
		ecn.ek_ci.lock_number--;
		
		if(request_response.request_time<=0) {
			if((ecn==first)||(first==last))
				return ecr;
			if(ecn==last) {
				last=last.front;
				last.back=null;
			}else{
				ecn.front.back=ecn.back;
				ecn.back.front=ecn.front;
			}
			ecn.back=first;
			ecn.front=null;
			first.front=ecn;
			first=ecn;
		}else {	
			if((ecn==last)||(first==last))
				return ecr;
			
			if(ecn==first) {
				first=first.back;
				first.front=null;
			}else{
				ecn.front.back=ecn.back;
				ecn.back.front=ecn.front;
			}
			ecn.front=last;
			ecn.back=null;
			last.back=ecn;
			last=ecn;
		}
		return ecr;
	}
	public engine_call_result execute_system_call(
			long channel_id,client_request_response request_response,
			interface_statistics statistics_interface,int engine_current_number[])
	{
		engine_call_result ret_val=null;
		if(channel_id<0)
			return ret_val;

		client_interface_lock.lock();
		
		if(manager_delay==null)
			debug_information.println("manager_delay==null		",request_response.implementor.get_client_id());
		else {
			try{
				ret_val=execute_system_call_routine(channel_id,
						session,request_response,statistics_interface,engine_current_number);
			}catch(Exception e) {
				ret_val=null;
				debug_information.println(
						"execute_system_call of client_interface_base fail");
				debug_information.println(e.toString());
				e.printStackTrace();
			}
			process_timeout(request_response);
		}
		client_interface_lock.unlock();
		return ret_val;
	}
	public void clear_all_engine()
	{
		client_interface_lock.lock();
		while(true)
			try {
				if(destroy_ek_ci_node(first))
					break;
			}catch(Exception e) {
				debug_information.println("clear_all_engine exception:	",e.toString());
				e.printStackTrace();
			}
		client_interface_lock.unlock();
	}
	public engine_call_result execute_create_call(engine_interface ei,
			client_request_response request_response,interface_statistics statistics_interface)
	{
		engine_call_result ret_val=null;
		
		client_interface_lock.lock();
		if(manager_delay==null)
			debug_information.println("manager_delay==null		",request_response.implementor.get_client_id());
		else {
			process_timeout(request_response);
			try{
				long delay_time_length;
				if((delay_time_length=manager_delay.process_delay_time_length())<0)
					debug_information.println("TIME OUT FOUND,Client ID is ",
							request_response.implementor.get_client_id());
				else
					ret_val=create_engine(ei,session,request_response,delay_time_length,statistics_interface);
			}catch(Exception e){
				ret_val=null;
				debug_information.println(
						"execute_create_call of client_interface_base fail");
				debug_information.println(e.toString());
				e.printStackTrace();
			}
			process_timeout(request_response);
		}
		
		ReentrantLock my_client_interface_lock;
		if((my_client_interface_lock=client_interface_lock)!=null)
			my_client_interface_lock.unlock();
	
		return ret_val;
	}
	public boolean test_client_interface(String my_user_name,String my_pass_word,String my_client_id)
	{
		if(manager_delay!=null)
			return false;
		
		String user_file_name=system_par.data_root_directory_name+system_par.user_file_name;
		file_reader f=new file_reader(user_file_name,system_par.local_data_charset);
		
		if(f.error_flag()){
			debug_information.println("Can't not open user configure file,file name:		",user_file_name);
			f.close();
			return true;
		}
		while(true){
			if((f.error_flag())||(f.eof())){
				f.close();
				return true;
			}
			String user_name=f.get_string();
			String pass_word=f.get_string();
			String parameter_file_name=f.get_string();
			String assemble_file_name=f.get_string();
			
			if(assemble_file_name==null)
				continue;
			if(my_user_name.compareTo(user_name)!=0)
				continue;
			if(my_pass_word.compareTo(pass_word)!=0)
				continue;
			parameter_file_name=file_reader.separator(parameter_file_name);
			assemble_file_name=f.directory_name+file_reader.separator(assemble_file_name);
			
			if(file_reader.is_exist(f.directory_name+parameter_file_name))
				parameter_file_name=f.directory_name+parameter_file_name;
			else
				parameter_file_name=system_par.default_parameter_directory
						+"user_parameter"+File.separator+parameter_file_name;
			f.close();
					
			f=new file_reader(parameter_file_name,f.get_charset());
			user_statistics statistics_user=new user_statistics(f);
			manager_delay=new delay_manager(f);
			f.close();
			
			session=new client_session(assemble_file_name,f.get_charset(),
					my_client_id,my_user_name,statistics_user,system_par);
			
			return false;
		}
	}
	public client_interface(system_parameter my_system_par)
	{
		touch_time	=nanosecond_timer.absolute_nanoseconds();
		
		system_par	 =new system_parameter(my_system_par);
		manager_delay=null;
		session		 =null;
		
		bt		=null;
		first	=null;
		last	=null;
		
		client_interface_lock	=new ReentrantLock();
		has_been_destroyd_flag	=false;
	}
}
