package kernel_interface;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import kernel_security.delay_manager;
import kernel_common_class.debug_information;
import kernel_common_class.jason_string;
import kernel_common_class.balance_tree;
import kernel_common_class.balance_tree_item;
import kernel_common_class.nanosecond_timer;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_engine.system_parameter;
import kernel_engine.engine_kernel;
import kernel_engine.engine_call_result;
import kernel_engine.create_engine_counter;
import kernel_engine.engine_kernel_container;
import kernel_engine.engine_kernel_and_client_information_container;

public class client_interface
{
	class ek_ci_balance_tree_node extends balance_tree_item<Long>
	{
		public engine_kernel_and_client_information_container ek_ci;
		public ek_ci_balance_tree_node front,back;
		
		public void destroy()
		{
			super.destroy();
			
			ek_ci=null;
			front=null;
			back=null;
		}
		public int compare(Long t)
		{
			return (super.compare_data<t)?-1:(super.compare_data>t)?1:0;
		}
		public ek_ci_balance_tree_node(long my_channel_id)
		{
			super(my_channel_id);
			
			ek_ci=null;
			front=null;
			back=null;
		}
	}

	public long touch_time;
	public int container_id;
	
	private system_parameter system_par;
	private client_process_bar_container process_bar_cont;

	private delay_manager manager_delay;
	private user_statistics statistics_user;
	private String client_scene_file_name,client_scene_file_charset;

	private balance_tree<Long,ek_ci_balance_tree_node> bt;
	private ek_ci_balance_tree_node first,last;

	private ReentrantLock client_interface_lock;
	
	private engine_call_result create_engine_routine(
			engine_interface_container engine_container,ReentrantLock my_client_interface_lock,
			client_request_response request_response,long delay_time_length,create_engine_counter engine_counter)
	{
		if(statistics_user.user_engine_kernel_number>statistics_user.user_max_engine_kernel_number) {
			debug_information.print  ("client id:",request_response.implementor.get_client_id());
			debug_information.println(",container id:",container_id);

			debug_information.print  ("Create too many engine\t\t\t:\t",statistics_user.user_engine_kernel_number);
			debug_information.println("/",statistics_user.user_max_engine_kernel_number);
			return null;
		}
		if(statistics_user.user_engine_component_number>statistics_user.user_max_engine_component_number) {
			debug_information.print  ("client id:",request_response.implementor.get_client_id());
			debug_information.println(",container id:",container_id);
		
			debug_information.print  ("Create too many component\t\t:\t",statistics_user.user_engine_component_number);
			debug_information.println("/",statistics_user.user_max_engine_component_number);
			return null;
		}
		client_process_bar cpb=get_process_bar(request_response);
		
		cpb.set_process_bar(true,"start_create_kernel","",1,2);

		engine_kernel_container created_engine_kernel_only=null;

		my_client_interface_lock.unlock();
		try{
			created_engine_kernel_only=engine_container.create_engine_kernel_container(request_response,
				client_scene_file_name,client_scene_file_charset,engine_counter,system_par);
		}catch(Exception e) {
			created_engine_kernel_only=null;
			debug_information.println(
					"engine_container.get_kernel_container(request_response,system_par,user_par.scene_file_name) fail");
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		my_client_interface_lock.lock();
		
		if(created_engine_kernel_only==null) {
			debug_information.println("Create engine result is null");
			return null;
		}

		debug_information.println();
		debug_information.print  ("client id:",request_response.implementor.get_client_id());
		debug_information.print  (",container id:",container_id);
		debug_information.print  (",link number is ",created_engine_kernel_only.link_number);
		debug_information.println((created_engine_kernel_only.ek.component_cont==null)
				?",assemble has not been loaded yet":",assemble has already been loaded");

		engine_kernel_and_client_information_container created_ek_and_ci;
		created_ek_and_ci=new engine_kernel_and_client_information_container(created_engine_kernel_only);
		
		created_ek_and_ci.access_lock_number++;
		my_client_interface_lock.unlock();
		
		cpb.set_process_bar(true,"start_load_scene","",1,2);
		
		engine_call_result ecr=null;
		try{
			ecr=created_ek_and_ci.get_engine_result(container_id,cpb,engine_container.system_boftal_container,
					engine_container.component_load_source_cont,client_scene_file_name,client_scene_file_charset,
					request_response,delay_time_length,statistics_user,engine_counter);
		}catch(Exception e) {
			ecr=null;
			debug_information.println("ec[channel_id].engine_kernel_link_list.get_engine_result fail");
			e.printStackTrace();
		}
		
		my_client_interface_lock.lock();
		created_ek_and_ci.access_lock_number--;

		if(created_engine_kernel_only.ek!=null){
			statistics_user.user_engine_kernel_number++;
			if(created_engine_kernel_only.ek.component_cont!=null) {
				if(created_engine_kernel_only.ek.component_cont.root_component!=null)
					statistics_user.user_engine_component_number+=created_engine_kernel_only.ek.component_cont.root_component.component_id+1;
			}
		}
		debug_information.print  ("Current user_engine_kernel_number is ",statistics_user.user_engine_kernel_number);
		debug_information.println("/",statistics_user.user_max_engine_kernel_number);
		debug_information.print  ("Current user_engine_component_number is ",statistics_user.user_engine_component_number);
		debug_information.println("/",statistics_user.user_max_engine_component_number);

		ek_ci_balance_tree_node ecn=new ek_ci_balance_tree_node(
				(created_ek_and_ci.client_information==null)?0:(created_ek_and_ci.client_information.channel_id));
		ecn.ek_ci=created_ek_and_ci;
		if(bt==null){
			bt=new balance_tree<Long,ek_ci_balance_tree_node>(ecn);
			first=ecn;
			last=ecn;
		}else{
			bt.search(ecn,true,false);
			ecn.front=last;
			last.back=ecn;
			last=ecn;
		}
		return ecr;
	}
	private engine_call_result create_engine(
			engine_interface_container engine_container,ReentrantLock my_client_interface_lock,
			client_request_response request_response,long delay_time_length,create_engine_counter engine_counter)
	{
		debug_information.println();
		debug_information.println("\n#####################################################################################################");
		
		long start_time=new Date().getTime();
		
		Calendar now = Calendar.getInstance();  
		debug_information.print  ("Engine creation start time	:	",now.get(Calendar.YEAR));  
		debug_information.print  ("-",(now.get(Calendar.MONTH) + 1));  
		debug_information.print  ("-",now.get(Calendar.DAY_OF_MONTH));  
		debug_information.print  ("/",now.get(Calendar.HOUR_OF_DAY));  
		debug_information.print  (":",now.get(Calendar.MINUTE));  
		debug_information.print  (":",now.get(Calendar.SECOND));
		debug_information.println(":",now.get(Calendar.MILLISECOND));

		String my_user_name=request_response.get_parameter("user_name");
		String my_client_id=request_response.implementor.get_client_id();
		
		debug_information.println("Request user name		:	",	(my_user_name==null)?"NoName":my_user_name);
		debug_information.println("Request Client ID		:	",	(my_client_id==null)?"":my_client_id);
		debug_information.println("Container ID			:	",		container_id);
		debug_information.println();
		
		debug_information.println("data_root_directory_name	:	",			system_par.data_root_directory_name);
		debug_information.println("shader_file_name		:	",				system_par.shader_file_name);
		debug_information.println("user_file_name			:	",			system_par.user_file_name);
		debug_information.println("default_parameter_directory	:	",		system_par.default_parameter_directory);
		debug_information.println("proxy_data_root_directory_name	:	",	system_par.proxy_par.proxy_data_root_directory_name);
		
		engine_call_result ret_val=create_engine_routine(engine_container,
			my_client_interface_lock,request_response,delay_time_length,engine_counter);
		
		now = Calendar.getInstance();  
		long end_time=new Date().getTime();
		
		debug_information.println();
		debug_information.print  ("Engine creation finish time	:	",now.get(Calendar.YEAR));  
		debug_information.print  ("-",(now.get(Calendar.MONTH) + 1));  
		debug_information.print  ("-",now.get(Calendar.DAY_OF_MONTH));  
		debug_information.print  ("/",now.get(Calendar.HOUR_OF_DAY));  
		debug_information.print  (":",now.get(Calendar.MINUTE));  
		debug_information.print  (":",now.get(Calendar.SECOND));
		debug_information.print  (":",now.get(Calendar.MILLISECOND));
		debug_information.println("		Time length(millisecond)	:	",end_time-start_time);
 
		debug_information.println("\n#####################################################################################################");
		debug_information.println();
		
		return ret_val;
	}
	private engine_call_result execute_system_call_routine(long channel_id,client_request_response request_response,
			engine_interface_container engine_container,create_engine_counter engine_counter,ReentrantLock my_client_interface_lock)
	{
		long delay_time_length;
		if((delay_time_length=manager_delay.process_delay_time_length())<0){
			debug_information.println("TIME OUT FOUND,Client ID is ",request_response.implementor.get_client_id());
			return null;
		}
		if(bt==null){
			debug_information.print  ("No client_interface exist,Client ID is ",request_response.implementor.get_client_id());
			debug_information.println(",channel_id is ",channel_id);
			return null;
		}
		
		ek_ci_balance_tree_node ecn;
		if((ecn=bt.search(new ek_ci_balance_tree_node(channel_id),false,false))==null) {
			debug_information.print  ("Search client_interface fail,Client ID is ",request_response.implementor.get_client_id());
			debug_information.println(",channel_id is ",channel_id);
			return null;
		}
		if(ecn.ek_ci.client_information==null){
			debug_information.println("ecn.ek_ci.client_information==null,Client ID is ",request_response.implementor.get_client_id());
			debug_information.println(",channel_id is ",channel_id);
			return null;
		}
		if(ecn.ek_ci.engine_kernel_cont==null){
			debug_information.println("ecn.ek_ci.engine_kernel_link_list==null,Client ID is ",request_response.implementor.get_client_id());
			debug_information.println(",channel_id is ",channel_id);
			return null;
		}

		engine_call_result ecr=null;
		
		ecn.ek_ci.access_lock_number++;
		my_client_interface_lock.unlock();
		try{
			ecr=ecn.ek_ci.get_engine_result(
					container_id,get_process_bar(request_response),
					engine_container.system_boftal_container,
					engine_container.component_load_source_cont,
					client_scene_file_name,client_scene_file_charset,
					request_response,delay_time_length,statistics_user,engine_counter);
		}catch(Exception e){
			ecr=null;
			debug_information.println("ecn.ek_ci.engine_kernel_link_list.get_engine_result fail");
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		my_client_interface_lock.lock();
		ecn.ek_ci.access_lock_number--;
		
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
		}else{
			if((ecn==last)||(first==last))
				return ecr;
			
			if(ecn==first){
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
			engine_interface_container engine_container,create_engine_counter engine_counter)
	{
		if(channel_id<0){
			debug_information.println("client_interface channel_id<0		",channel_id);
			return null;
		}
		if(touch_time<=0) {
			debug_information.println("client_interface touch_time<=0		",request_response.implementor.get_client_id());
			return null;
		}
		ReentrantLock my_client_interface_lock=client_interface_lock;
		my_client_interface_lock.lock();

		engine_call_result ret_val=null;
		try{
			ret_val=execute_system_call_routine(channel_id,request_response,
					engine_container,engine_counter,my_client_interface_lock);
		}catch(Exception e) {
			ret_val=null;
			debug_information.println("execute_system_call of client_interface_base fail");
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		
		process_timeout(request_response,engine_container,engine_counter);
		
		my_client_interface_lock.unlock();
		return ret_val;
	}
	public engine_call_result execute_create_call(client_request_response request_response,
			engine_interface_container engine_container,create_engine_counter engine_counter)
	{
		if(touch_time<=0) {
			debug_information.println("client_interface touch_time<=0		",request_response.implementor.get_client_id());
			return null;
		}
		ReentrantLock my_client_interface_lock=client_interface_lock;
		my_client_interface_lock.lock();
		
		process_timeout(request_response,engine_container,engine_counter);
		
		engine_call_result ret_val=null;
		try{
			long delay_time_length;
			if((delay_time_length=manager_delay.process_delay_time_length())<0)
				debug_information.println("TIME OUT FOUND,Client ID is ",request_response.implementor.get_client_id());
			else
				ret_val=create_engine(engine_container,my_client_interface_lock,request_response,delay_time_length,engine_counter);
		}catch(Exception e){
			ret_val=null;
			debug_information.println(
					"execute_create_call of client_interface_base fail");
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		my_client_interface_lock.unlock();
	
		return ret_val;
	}
	private engine_call_result process_process_bar_system_call_routine(client_request_response request_response)
	{
		String str,language_str;
		client_process_bar process_bar;
		if((str=request_response.get_parameter("command"))!=null)
			switch(str){
			case "request":
				process_bar=process_bar_cont.request_process_bar();
				process_bar.set_process_bar(true,"start_create_scene","", 0, 1);

				request_response.println("{");
				request_response.println("	\"container_id\"				:	",	container_id+",");
				request_response.println("	\"process_bar_id\"				:	",	process_bar.process_bar_id+",");
				request_response.println("	\"show_process_bar_interval\"	:	",	system_par.show_process_bar_interval);
				request_response.println("}");
				break;
			case "data":
				if((process_bar=get_process_bar(request_response))==null)
					break;
				language_str=request_response.get_parameter("language");
				str=process_bar.process_title+"+"+((language_str==null)?"english":language_str);
				str=system_par.language_change_name.search_change_name(str,process_bar.process_title);
				str=jason_string.change_string(str+" "+process_bar.ex_process_title);
				
				long current_time=nanosecond_timer.absolute_nanoseconds();
				long time_length=current_time-process_bar.start_time;
				long engine_time_length=current_time-process_bar.original_time;
	
				request_response.println("{");
				request_response.print  ("	\"caption\":		",		str).						 							 println(",");
				request_response.print  ("	\"current\":		",		process_bar.current_process	).							 println(",");
				request_response.print  ("	\"max\":			",  	(process_bar.max_process<1)?1:(process_bar.max_process)).println(",");
				request_response.print  ("	\"time_length\":	",  	time_length/1000000			).							 println(",");
				request_response.print  ("	\"engine_time_length\":	",  engine_time_length/1000000	).							 println(",");
				request_response.print  ("	\"time_unit\":		\"",  	system_par.language_change_name.
					search_change_name("unit+"+((language_str==null)?"english":language_str),"unit")).							 println("\"");
				request_response.println("}");
				break;	
			}
		return new engine_call_result(null,null,null,null,null,"*");
	}
	public engine_call_result process_process_bar_system_call(client_request_response request_response)
	{
		ReentrantLock my_client_interface_lock=client_interface_lock;
		my_client_interface_lock.lock();
		engine_call_result ret_val=process_process_bar_system_call_routine(request_response);
		my_client_interface_lock.unlock();
		return ret_val;
	}
	public client_process_bar get_process_bar(client_request_response request_response)
	{
		String str;
		int process_bar_id=-1;
		if((str=request_response.get_parameter("process_bar"))!=null)
			try{
				process_bar_id=Integer.parseInt(str);
			}catch(Exception e) {
				process_bar_id=-1;
			}
		return process_bar_cont.get_process_bar(process_bar_id);
	}
	private boolean destroy_ek_ci_node(ek_ci_balance_tree_node ecn,
			engine_interface_container engine_container,create_engine_counter engine_counter)
	{
		engine_kernel ek;
		if(ecn==null)
			return true;
		if(ecn.ek_ci.access_lock_number>0)
			return true;
		if((ek=ecn.ek_ci.engine_kernel_cont.ek)!=null){
			statistics_user.user_engine_kernel_number--;
			if(ek.component_cont!=null)
				if(ek.component_cont.root_component!=null)
					statistics_user.user_engine_component_number-=ek.component_cont.root_component.component_id+1;
		}
		debug_information.println("Execute destroy_ek_ci_node");
		debug_information.print  ("user_engine_kernel_number:",statistics_user.user_engine_kernel_number);
		debug_information.println("/",statistics_user.user_max_engine_kernel_number);
		debug_information.print  ("user_engine_component_number:",statistics_user.user_engine_component_number);
		debug_information.println("/",statistics_user.user_max_engine_component_number);
		
		if(ecn.ek_ci.client_information!=null) {
			try{
				ecn.ek_ci.client_information.destroy();
			}catch(Exception e){
				debug_information.println("Destroy client_information exception:	",e.toString());
				e.printStackTrace();
			}
			ecn.ek_ci.client_information=null;
		}
		if(ecn.ek_ci.engine_kernel_cont!=null) {
			engine_container.destroy_scene(
				ecn.ek_ci.engine_kernel_cont.ek.create_parameter.scene_name,
				ecn.ek_ci.engine_kernel_cont.ek.create_parameter.link_name,
				engine_counter);
			ecn.ek_ci.engine_kernel_cont=null;
		}
		if(first==last){
			bt.destroy();
			bt=null;
			first=null;
			last=null;
		}else{
			bt.search(ecn,false,true);
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
	private void process_timeout(client_request_response request_response,
			engine_interface_container engine_container,create_engine_counter engine_counter)
	{
		while(first!=null){
			if(first.ek_ci.access_lock_number>0)
				return;
			if((first.ek_ci.client_information==null)||(first.ek_ci.engine_kernel_cont==null)) {
				debug_information.println(
						"((first.ek_ci.client_information==null)||(first.ek_ci.engine_kernel_link_list==null))");
				debug_information.print  ("client_interface delete time out client_information found, client id is ");
				debug_information.print  (request_response.implementor.get_client_id());
				debug_information.println(",container ID is ",container_id);
			}else {
				long request_time=first.ek_ci.client_information.request_response.request_time;
				long time_length=nanosecond_timer.absolute_nanoseconds()-request_time;
				if(time_length<system_par.engine_expire_time_length)
					return;
				debug_information.print  ("client_interface delete time out client_information found, client id is ");
				debug_information.println(request_response.implementor.get_client_id());
				
				debug_information.print  ("container ID is ",container_id);
				debug_information.print  (",Channel is ",first.ek_ci.client_information.channel_id);
				debug_information.print  (",time interval ",time_length);
				debug_information.println(",max time interval  ",system_par.engine_expire_time_length);
			}
			if(destroy_ek_ci_node(first,engine_container,engine_counter))
				break;
		}
	}
	private void destroy_routine()
	{	
		for(ek_ci_balance_tree_node p=first;p!=null;) {
			if(p.ek_ci!=null) {
				if(p.ek_ci.client_information!=null) {
					p.ek_ci.client_information.destroy();
					p.ek_ci.client_information=null;
				}
				if(p.ek_ci.engine_kernel_cont!=null)
					p.ek_ci.engine_kernel_cont=null;
			}
			ek_ci_balance_tree_node destroy_node=p;
			p=p.back;
			destroy_node.destroy();
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
		if(client_interface_lock!=null)
			client_interface_lock=null;
		if(process_bar_cont!=null) {
			process_bar_cont.destroy();
			process_bar_cont=null;
		}

		if(client_scene_file_name!=null)
			client_scene_file_name=null;
		if(client_scene_file_charset!=null)
			client_scene_file_charset=null;

		if(statistics_user!=null)
			statistics_user=null;
	}
	public void destroy()
	{
		ReentrantLock my_client_interface_lock=client_interface_lock;
		my_client_interface_lock.lock();
		try{
			destroy_routine();
		}catch(Exception e){
			debug_information.println("destroy of client_interface_base fail:\t",e.toString());
			e.printStackTrace();
		}
		my_client_interface_lock.unlock();
	}
	public client_interface(
			int my_container_id,String my_user_name,String my_pass_word,
			String my_client_id,system_parameter my_system_par)
	{
		container_id			=my_container_id;
		touch_time				=0;
		
		system_par	 			=new system_parameter(my_system_par);
		process_bar_cont		=new client_process_bar_container(system_par.engine_expire_time_length);
		manager_delay			=null;
		
		client_scene_file_name	=null;
		client_scene_file_charset=null;
		
		statistics_user			=null;
		
		bt						=null;
		first					=null;
		last					=null;
		
		client_interface_lock	=new ReentrantLock();
		
		String user_file_name=system_par.data_root_directory_name+system_par.user_file_name;
		file_reader f=new file_reader(user_file_name,system_par.local_data_charset);
		
		if(f.error_flag()){
			debug_information.print  ("user_name:",my_user_name);
			debug_information.println(",client_id:",my_client_id);
			debug_information.println("Can't not open user configure file,file name:		",user_file_name);
			
			f.close();
			return;
		}
		while(true){
			if((f.error_flag())||(f.eof())){
				f.close();
				return;
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
			statistics_user=new user_statistics(f);
			manager_delay=new delay_manager(f);
			f.close();
			
			client_scene_file_name=assemble_file_name;
			client_scene_file_charset=f.get_charset();
			touch_time=nanosecond_timer.absolute_nanoseconds();

			return;
		}
	}
}
