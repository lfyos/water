package kernel_interface;

import java.io.File;
import java.util.Date;
import java.util.Calendar;
import java.util.concurrent.locks.ReentrantLock;

import kernel_component.component;
import kernel_engine.engine_kernel;
import kernel_security.delay_manager;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_engine.engine_call_result;
import kernel_common_class.jason_string;
import kernel_engine.create_engine_counter;
import kernel_common_class.nanosecond_timer;
import kernel_engine.engine_kernel_container;
import kernel_engine.engine_kernel_container_search_tree;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_common_class.tree_string_search_container;
import kernel_engine.engine_kernel_and_client_information_container;

public class client_interface
{
	private tree_string_search_container<engine_kernel_and_client_information_container> tree;

	private system_parameter system_par;
	private client_process_bar_container process_bar_cont;

	private delay_manager manager_delay;
	private user_statistics statistics_user;
	private String client_scene_file_name,client_scene_file_charset;

	private ReentrantLock client_interface_lock;
	
	private engine_call_result create_engine_routine(long delay_time_length,
			engine_kernel_container_search_tree engine_search_tree,ReentrantLock my_client_interface_lock,
			client_request_response request_response,create_engine_counter engine_counter)
	{
		if(statistics_user.user_engine_kernel_number>statistics_user.user_max_engine_kernel_number) {
			debug_information.print  ("client id:",request_response.client_id);
			debug_information.println(",container id:",request_response.container_id);

			debug_information.print  ("Create too many engine\t\t\t:\t",statistics_user.user_engine_kernel_number);
			debug_information.println("/",statistics_user.user_max_engine_kernel_number);
			return null;
		}
		if(statistics_user.user_engine_component_number>statistics_user.user_max_engine_component_number) {
			debug_information.print  ("client id:",request_response.client_id);
			debug_information.println(",container id:",request_response.container_id);
		
			debug_information.print  ("Create too many component\t\t:\t",statistics_user.user_engine_component_number);
			debug_information.println("/",statistics_user.user_max_engine_component_number);
			return null;
		}
		client_process_bar cpb=get_process_bar(request_response);
		
		cpb.set_process_bar(true,"start_create_kernel","",1,2);

		engine_kernel_container created_engine_kernel_only=null;

		my_client_interface_lock.unlock();
		try{
			created_engine_kernel_only=engine_search_tree.create_engine_kernel_container(
				request_response,client_scene_file_name,client_scene_file_charset,
				engine_counter,system_par);
		}catch(Exception e) {
			e.printStackTrace();

			created_engine_kernel_only=null;
			debug_information.println(
					"engine_container.get_kernel_container(request_response,system_par,user_par.scene_file_name) fail");
			debug_information.println(e.toString());
		}
		my_client_interface_lock.lock();
		
		if(created_engine_kernel_only==null) {
			debug_information.println("Create engine result is null");
			return null;
		}

		debug_information.println();
		debug_information.print  ("client id:",request_response.client_id);
		debug_information.print  (",container id:",request_response.container_id);
		debug_information.print  (",link number is ",created_engine_kernel_only.get_link_number());
		debug_information.println((created_engine_kernel_only.ek.component_cont==null)
				?",assemble has not been loaded yet":",assemble has already been loaded");

		engine_kernel_and_client_information_container created_ek_and_ci;
		created_ek_and_ci=new engine_kernel_and_client_information_container(created_engine_kernel_only);
		
		created_ek_and_ci.lock_number(1);
		my_client_interface_lock.unlock();
		
		cpb.set_process_bar(true,"start_load_scene","",1,2);
		
		engine_call_result ecr=null;
		try{
			ecr=created_ek_and_ci.get_engine_result(
					cpb,engine_search_tree.system_boftal_container,
					engine_search_tree.component_load_source_cont,
					request_response,delay_time_length,statistics_user,engine_counter);
		}catch(Exception e){
			e.printStackTrace();
			ecr=null;
			debug_information.println("ec[channel_id].engine_kernel_link_list.get_engine_result fail");
		}
		
		my_client_interface_lock.lock();
		created_ek_and_ci.lock_number(-1);
		
		tree.add(new String[]{created_ek_and_ci.client_information.channel_id},created_ek_and_ci);
		
		if(created_engine_kernel_only.ek!=null)
			if(created_engine_kernel_only.ek.component_cont!=null)
				if(created_engine_kernel_only.ek.component_cont.root_component!=null) {
					component root_component=created_engine_kernel_only.ek.component_cont.root_component;
					statistics_user.user_engine_kernel_number++;
					statistics_user.user_engine_component_number+=root_component.component_id+1;
				}
		debug_information.print  ("Current user_engine_kernel_number is ",statistics_user.user_engine_kernel_number);
		debug_information.println("/",statistics_user.user_max_engine_kernel_number);
		debug_information.print  ("Current user_engine_component_number is ",statistics_user.user_engine_component_number);
		debug_information.println("/",statistics_user.user_max_engine_component_number);

		return ecr;
	}
	private engine_call_result create_engine(long delay_time_length,
			engine_kernel_container_search_tree engine_search_tree,ReentrantLock my_client_interface_lock,
			client_request_response request_response,create_engine_counter engine_counter)
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

		
		debug_information.println("Request user name		:	",	request_response.user_name);
		debug_information.println("Request Client ID		:	",	request_response.client_id);
		debug_information.println("Container ID			:	",		request_response.container_id);
		debug_information.println();
		
		debug_information.println("data_root_directory_name	:	",			system_par.data_root_directory_name);
		debug_information.println("shader_file_name		:	",				system_par.shader_file_name);
		debug_information.println("user_file_name			:	",			system_par.user_file_name);
		debug_information.println("default_parameter_directory	:	",		system_par.default_parameter_directory);
		debug_information.println("temporary_root_directory_name	:	",	system_par.temporary_file_par.temporary_root_directory_name);
		
		engine_call_result ret_val=create_engine_routine(delay_time_length,
			engine_search_tree,my_client_interface_lock,request_response,engine_counter);
		
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
	
	private engine_call_result execute_system_call_routine(
			client_request_response request_response,
			engine_kernel_container_search_tree engine_search_tree,
			create_engine_counter engine_counter,ReentrantLock my_client_interface_lock)
	{
		long delay_time_length;
		if((delay_time_length=manager_delay.process_delay_time_length())<0){
			debug_information.println("TIME OUT FOUND,Client ID is ",request_response.client_id);
			return null;
		}
		engine_kernel_and_client_information_container p;
		String key[]=new String[] {request_response.channel_string};
		if((p=tree.search(key))==null) {
			debug_information.print  ("Search client_interface fail,Client ID is ",request_response.client_id);
			debug_information.println(",channel_id is ",request_response.channel_string);
			return null;
		};
		if(p.client_information==null){
			tree.move_to_first(key);
			debug_information.println("ecn.ek_ci.client_information==null,Client ID is ",request_response.client_id);
			debug_information.println(",channel_id is ",request_response.channel_string);
			return null;
		}
		if(p.engine_kernel_cont==null){
			tree.move_to_first(key);
			debug_information.println("ecn.ek_ci.engine_kernel_link_list==null,Client ID is ",request_response.client_id);
			debug_information.println(",channel_id is ",request_response.channel_string);
			return null;
		}

		engine_call_result ecr=null;
		
		my_client_interface_lock.unlock();
		try{
			ecr=p.get_engine_result(get_process_bar(request_response),
					engine_search_tree.system_boftal_container,
					engine_search_tree.component_load_source_cont,
					request_response,delay_time_length,statistics_user,engine_counter);
		}catch(Exception e){
			e.printStackTrace();
			
			ecr=null;
			debug_information.println("ecn.ek_ci.engine_kernel_link_list.get_engine_result fail");
			debug_information.println(e.toString());
			
		}
		my_client_interface_lock.lock();
		
		return ecr;
	}
	public engine_call_result execute_system_call(
			client_request_response request_response,
			engine_kernel_container_search_tree engine_search_tree,
			create_engine_counter engine_counter)
	{
		ReentrantLock my_client_interface_lock=client_interface_lock;
		my_client_interface_lock.lock();
		engine_call_result ret_val=null;
		
		String str=request_response.get_parameter("command");
		switch((str==null)?"":str.trim()) {
		case "termination":
			client_process_bar process_bar;
			if((process_bar=get_process_bar(request_response))!=null)
				process_bar.touch_time=0;
			tree.move_to_first(new String[] {request_response.channel_string});
			break;
		default:
			try{
				ret_val=execute_system_call_routine(request_response,
						engine_search_tree,engine_counter,my_client_interface_lock);
			}catch(Exception e) {
				e.printStackTrace();
				
				ret_val=null;
				debug_information.println("execute_system_call of client_interface_base fail");
				debug_information.println(e.toString());
				
			}
			break;
		}
		process_timeout(request_response,engine_search_tree,engine_counter);
		my_client_interface_lock.unlock();
		return ret_val;
	}
	public engine_call_result execute_create_call(
			client_request_response request_response,
			engine_kernel_container_search_tree engine_search_tree,
			create_engine_counter engine_counter)
	{
		ReentrantLock my_client_interface_lock=client_interface_lock;
		my_client_interface_lock.lock();

		engine_call_result ret_val=null;
		try{
			long delay_time_length;
			if((delay_time_length=manager_delay.process_delay_time_length())<0)
				debug_information.println("TIME OUT FOUND,Client ID is ",request_response.client_id);
			else
				ret_val=create_engine(delay_time_length,
					engine_search_tree,my_client_interface_lock,request_response,engine_counter);
		}catch(Exception e){
			e.printStackTrace();
			
			ret_val=null;
			debug_information.println(
					"execute_create_call of client_interface_base fail");
			debug_information.println(e.toString());
		}
		process_timeout(request_response,engine_search_tree,engine_counter);
		my_client_interface_lock.unlock();
	
		return ret_val;
	}
	private engine_call_result process_process_bar_system_call_routine(
					client_request_response request_response)
	{
		String str;
		client_process_bar process_bar;
		if((str=request_response.get_parameter("command"))!=null)
			switch(str){
			case "request":
				process_bar=process_bar_cont.request_process_bar();
				process_bar.set_process_bar(true,"start_create_scene","", 0, 1);

				request_response.println("{");
				request_response.println("	\"container_id\"				:	",	request_response.container_id+",");
				request_response.println("	\"process_bar_id\"				:	",	process_bar.process_bar_id+",");
				request_response.println("	\"show_process_bar_interval\"	:	",	system_par.show_process_bar_interval);
				request_response.println("}");
				break;
			case "data":
				if((process_bar=get_process_bar(request_response))==null)
					break;
				str=process_bar.process_title+"+"+request_response.language_str;
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
					search_change_name("unit+"+request_response.language_str,"unit")).							 println("\"");
				request_response.println("}");
				break;	
			}
		return new engine_call_result(system_par.system_cors_string,request_response.response_content_type);
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
	
	private void process_timeout(client_request_response request_response,
			engine_kernel_container_search_tree engine_search_tree,create_engine_counter engine_counter)
	{
		for(long touch_time;(touch_time=tree.first_touch_time())>=0;){
			String 											my_key[]=tree.get_first_key();
			engine_kernel_and_client_information_container	my_value=tree.get_first_value();
			if(my_value.lock_number(0)>0)
				break;
			if((my_value.client_information==null)||(my_value.engine_kernel_cont==null)) {
				debug_information.println();
				debug_information.println(
						"((first.ek_ci.client_information==null)||(first.ek_ci.engine_kernel_link_list==null))");
				debug_information.print  ("client_interface delete time out client_information found, client id is ");
				debug_information.print  (request_response.client_id);
				debug_information.println(",container ID is ",request_response.container_id);
			}else{
				long time_length=nanosecond_timer.absolute_nanoseconds()-touch_time;
				if(time_length<system_par.engine_expire_time_length)
					break;
				debug_information.println();
				debug_information.print  ("client_interface delete time out client_information found, client id is ");
				debug_information.println(request_response.client_id);
				debug_information.print  ("container ID is ",request_response.container_id);
				debug_information.print  (",Channel is ",	my_value.client_information.channel_id);
				debug_information.print  (",time interval ",time_length);
				debug_information.println(",max time interval  ",system_par.engine_expire_time_length);
			}
			
			if(my_value.engine_kernel_cont!=null) {
				engine_kernel ek;
				if((ek=my_value.engine_kernel_cont.ek)!=null)
					if(ek.component_cont!=null)
						if(ek.component_cont.root_component!=null){
							statistics_user.user_engine_kernel_number--;
							statistics_user.user_engine_component_number-=ek.component_cont.root_component.component_id+1;
						}
			}
			debug_information.println("Execute destroy_ek_ci_node");
			debug_information.print  ("user_engine_kernel_number:",statistics_user.user_engine_kernel_number);
			debug_information.println("/",statistics_user.user_max_engine_kernel_number);
			debug_information.print  ("user_engine_component_number:",statistics_user.user_engine_component_number);
			debug_information.println("/",statistics_user.user_max_engine_component_number);
			
			if(my_value.client_information!=null) {
				try{
					my_value.client_information.destroy();
				}catch(Exception e){
					e.printStackTrace();
					
					debug_information.println("Destroy client_information exception:	",e.toString());
				}
				my_value.client_information=null;
			}
			if(my_value.engine_kernel_cont!=null) {
				if(my_value.engine_kernel_cont.ek!=null)
					engine_search_tree.destroy_engine_kernel_container(
							my_value.engine_kernel_cont.ek.scene_name,
							my_value.engine_kernel_cont.ek.link_name,engine_counter);
				my_value.engine_kernel_cont=null;
			}
			tree.remove(my_key);
		}
	}
	private void destroy_routine(
			engine_kernel_container_search_tree engine_search_tree,
			create_engine_counter engine_counter)
	{	
		String my_key[];
		engine_kernel_and_client_information_container my_value;
		
		if(tree!=null) {
			while(tree.first_touch_time()>0){
				my_key=tree.get_first_key();
				my_value=tree.remove(my_key);
				
				if(my_value.client_information!=null) {
					my_value.client_information.destroy();
					my_value.client_information=null;
				}
				if(my_value.engine_kernel_cont!=null) {
					if(my_value.engine_kernel_cont.ek!=null) {
						engine_search_tree.destroy_engine_kernel_container(
								my_value.engine_kernel_cont.ek.scene_name,
								my_value.engine_kernel_cont.ek.link_name,
								engine_counter);
					};
					my_value.engine_kernel_cont=null;
				}
			}
			tree=null;
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
	public void destroy(
			engine_kernel_container_search_tree engine_search_tree,
			create_engine_counter engine_counter)
	{
		ReentrantLock my_client_interface_lock;
		
		if((my_client_interface_lock=client_interface_lock)==null)
			return;
		
		my_client_interface_lock.lock();
		try{
			destroy_routine(engine_search_tree,engine_counter);
		}catch(Exception e){
			e.printStackTrace();
			debug_information.println("destroy of client_interface_base fail:\t",e.toString());
		}
		my_client_interface_lock.unlock();
	}
	private client_interface(
		client_request_response request_response,system_parameter my_system_par)
	{
		tree=new tree_string_search_container<engine_kernel_and_client_information_container>();
		
		system_par	 			=new system_parameter(my_system_par);
		process_bar_cont		=new client_process_bar_container(system_par.engine_expire_time_length);
		manager_delay			=null;
		
		client_scene_file_name	=null;
		client_scene_file_charset=null;
		
		statistics_user			=null;

		client_interface_lock	=new ReentrantLock();
		
		String user_file_name=system_par.data_root_directory_name+system_par.user_file_name;
		file_reader f=new file_reader(user_file_name,system_par.local_data_charset);
		
		if(f.error_flag()){
			debug_information.println("Can't not open user configure file,file name:		",user_file_name);
			debug_information.print  ("user_name:",request_response.user_name);
			debug_information.println(",client_id:",request_response.client_id);
			f.close();
			return;
		}
		
		while(!(f.eof())){
			String user_name			=f.get_string();
			String pass_word			=f.get_string();
			String parameter_file_name	=f.get_string();
			String scene_file_name		=f.get_string();
			
			if(scene_file_name==null)
				continue;
			if(request_response.user_name.compareTo(user_name)!=0)
				continue;
			if(request_response.pass_word.compareTo(pass_word)!=0)
				continue;
			parameter_file_name	=file_reader.separator(parameter_file_name);
			scene_file_name		=f.directory_name+file_reader.separator(scene_file_name);

			if(new File(f.directory_name+parameter_file_name).exists())
				parameter_file_name=f.directory_name+parameter_file_name;
			else {
				parameter_file_name=system_par.default_parameter_directory
						+"user_parameter"+File.separator+parameter_file_name;
				if(!(new File(parameter_file_name).exists()))
					continue;
			}
			
			if(!(new File(scene_file_name).exists()))
				continue;
			
			f.close();
			
			client_scene_file_name		=scene_file_name;
			client_scene_file_charset	=f.get_charset();
					
			f=new file_reader(parameter_file_name,f.get_charset());
			statistics_user=new user_statistics(f);
			manager_delay=new delay_manager(f);
			f.close();

			return;
		};
		
		f.close();
		
		debug_information.println(
				"Can't not find user configure information,file name:		",
				user_file_name);
		debug_information.print  ("user_name:",request_response.user_name);
		debug_information.println(",client_id:",request_response.client_id);
		
		return;
	}
	public static client_interface create(client_request_response request_response,
			system_parameter my_system_par,
			engine_kernel_container_search_tree engine_search_tree,
			create_engine_counter engine_counter)
	{
		client_interface ret_val=new client_interface(request_response,my_system_par);
		if(ret_val.client_scene_file_name!=null)
			if(ret_val.client_scene_file_charset!=null)
				return ret_val;
		ret_val.destroy(engine_search_tree,engine_counter);
		return null;
	}
}
