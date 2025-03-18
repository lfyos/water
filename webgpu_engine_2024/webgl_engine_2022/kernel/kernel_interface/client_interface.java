package kernel_interface;

import java.io.File;
import java.util.Date;
import java.util.Calendar;
import java.util.concurrent.locks.ReentrantLock;

import kernel_component.component;
import kernel_common_class.jason_string;
import kernel_common_class.nanosecond_timer;
import kernel_common_class.debug_information;
import kernel_common_class.tree_string_search_container;

import kernel_scene.scene_kernel;
import kernel_scene.system_parameter;
import kernel_scene.scene_call_result;
import kernel_scene.create_scene_counter;
import kernel_scene.scene_kernel_container;
import kernel_scene.scene_kernel_container_search_tree;
import kernel_scene.scene_kernel_and_client_information_container;

import kernel_security.delay_manager;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;

public class client_interface
{
	private tree_string_search_container<scene_kernel_and_client_information_container> tree;

	private system_parameter system_par;
	private client_process_bar_container process_bar_cont;

	private delay_manager manager_delay;
	private user_statistics statistics_user;
	private String client_scene_file_name,client_scene_file_charset;

	private volatile ReentrantLock client_interface_lock;
	
	private scene_call_result create_scene_routine(long delay_time_length,
			scene_kernel_container_search_tree scene_search_tree,ReentrantLock my_lock,
			client_request_response request_response,create_scene_counter scene_counter)
	{
		if(statistics_user.user_scene_kernel_number>=statistics_user.user_max_scene_kernel_number) {
			debug_information.print  ("client id:",request_response.client_id);
			debug_information.println(",container id:",request_response.container_id);

			debug_information.print  ("Create too many scene\t\t\t:\t",statistics_user.user_scene_kernel_number);
			debug_information.println("/",statistics_user.user_max_scene_kernel_number);
			return null;
		}
		if(statistics_user.user_scene_component_number>=statistics_user.user_max_scene_component_number) {
			debug_information.print  ("client id:",request_response.client_id);
			debug_information.println(",container id:",request_response.container_id);
		
			debug_information.print  ("Create too many component\t\t:\t",statistics_user.user_scene_component_number);
			debug_information.println("/",statistics_user.user_max_scene_component_number);
			return null;
		}
		client_process_bar cpb=get_process_bar_routine(request_response);
		
		cpb.set_process_bar(true,"start_create_kernel","",1,2);

		scene_kernel_container created_scene_kernel_only=null;

		my_lock.unlock();
		try{
			created_scene_kernel_only=scene_search_tree.create_scene_kernel_container(
				request_response,client_scene_file_name,client_scene_file_charset,
				scene_counter,system_par);
		}catch(Exception e) {
			e.printStackTrace();

			created_scene_kernel_only=null;
			debug_information.println(
					"scene_container.get_kernel_container(request_response,system_par,user_par.scene_file_name) fail");
			debug_information.println(e.toString());
		}
		my_lock.lock();
		
		if(created_scene_kernel_only==null) {
			debug_information.println("Create scene result is null");
			return null;
		}

		debug_information.println();
		debug_information.print  ("client id:",request_response.client_id);
		debug_information.print  (",container id:",request_response.container_id);
		debug_information.print  (",link number is ",created_scene_kernel_only.get_link_number());
		debug_information.println((created_scene_kernel_only.sk.component_cont==null)
				?",assemble has not been loaded yet":",assemble has already been loaded");

		scene_kernel_and_client_information_container created_ek_and_ci;
		created_ek_and_ci=new scene_kernel_and_client_information_container(created_scene_kernel_only);
		
		created_ek_and_ci.lock_number(1);
		my_lock.unlock();
		
		cpb.set_process_bar(true,"start_load_scene","",1,2);
		
		scene_call_result ecr=null;
		try{
			ecr=created_ek_and_ci.get_scene_result(
					cpb,scene_search_tree.system_boftal_container,
					scene_search_tree.component_load_source_cont,
					request_response,delay_time_length,statistics_user,scene_counter);
		}catch(Exception e){
			e.printStackTrace();
			ecr=null;
			debug_information.println("get_scene_result() fail in create_scene_routine");
		}
		
		my_lock.lock();
		created_ek_and_ci.lock_number(-1);
		
		tree.add(new String[]{created_ek_and_ci.client_information.channel_id},created_ek_and_ci);
		
		if(created_scene_kernel_only.sk!=null)
			if(created_scene_kernel_only.sk.component_cont!=null)
				if(created_scene_kernel_only.sk.component_cont.root_component!=null) {
					component root_component=created_scene_kernel_only.sk.component_cont.root_component;
					statistics_user.user_scene_kernel_number++;
					statistics_user.user_scene_component_number+=root_component.component_id+1;
				}
		debug_information.print  ("Current user_scene_kernel_number is ",statistics_user.user_scene_kernel_number);
		debug_information.println("/",statistics_user.user_max_scene_kernel_number);
		debug_information.print  ("Current user_scene_component_number is ",statistics_user.user_scene_component_number);
		debug_information.println("/",statistics_user.user_max_scene_component_number);

		return ecr;
	}
	private scene_call_result create_scene(long delay_time_length,
			scene_kernel_container_search_tree scene_search_tree,ReentrantLock my_lock,
			client_request_response request_response,create_scene_counter scene_counter)
	{
		debug_information.println();
		debug_information.println("\n#####################################################################################################");
		
		long start_time=new Date().getTime();
		
		Calendar now = Calendar.getInstance();  
		debug_information.print  ("Scene creation start time	:	",now.get(Calendar.YEAR));  
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
		
		scene_call_result ret_val=create_scene_routine(delay_time_length,
				scene_search_tree,my_lock,request_response,scene_counter);
		
		now = Calendar.getInstance();  
		long end_time=new Date().getTime();
		
		debug_information.println();
		debug_information.print  ("Scene creation finish time	:	",now.get(Calendar.YEAR));  
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
	
	private scene_call_result execute_system_call_routine(
			client_request_response request_response,
			scene_kernel_container_search_tree scene_search_tree,
			create_scene_counter scene_counter,ReentrantLock my_lock)
	{
		long delay_time_length;
		if((delay_time_length=manager_delay.process_delay_time_length())<0){
			debug_information.println("TIME OUT FOUND,Client ID is ",request_response.client_id);
			return null;
		}
		
		String my_key[]=new String[] {request_response.channel_string};
		scene_kernel_and_client_information_container my_value;
		
		if((my_value=tree.search(my_key))==null) {
			debug_information.print  ("Search client_interface fail,Client ID is ",request_response.client_id);
			debug_information.println(",channel_id is ",request_response.channel_string);
			return null;
		};
		if(my_value.client_information==null){
			tree.move_to_first(my_key);
			debug_information.println("my_value.client_information==null,Client ID is ",request_response.client_id);
			debug_information.println(",channel_id is ",request_response.channel_string);
			return null;
		}
		if(my_value.scene_kernel_cont==null){
			tree.move_to_first(my_key);
			debug_information.println("my_value.scene_kernel_cont==null,Client ID is ",request_response.client_id);
			debug_information.println(",channel_id is ",request_response.channel_string);
			return null;
		}

		scene_call_result ecr=null;
		
		my_lock.unlock();
		try{
			ecr=my_value.get_scene_result(
					get_process_bar_routine(request_response),
					scene_search_tree.system_boftal_container,
					scene_search_tree.component_load_source_cont,
					request_response,delay_time_length,statistics_user,scene_counter);
		}catch(Exception e){
			e.printStackTrace();
			ecr=null;
			debug_information.println("get_scene_result() fail in execute_system_call_routine");
			debug_information.println(e.toString());
		}
		my_lock.lock();
		
		return ecr;
	}
	public scene_call_result execute_system_call(
			client_request_response request_response,
			scene_kernel_container_search_tree scene_search_tree,
			create_scene_counter scene_counter)
	{
		ReentrantLock my_lock;
		
		if((my_lock=client_interface_lock)==null)
			return null;
		my_lock.lock();
		
		scene_call_result ret_val=null;
		
		String str=request_response.get_parameter("command");
		switch((str==null)?"":str.trim()) {
		case "termination":
			client_process_bar process_bar;
			if((process_bar=get_process_bar_routine(request_response))!=null)
				process_bar.touch_time=0;
			tree.move_to_first(new String[] {request_response.channel_string});
			break;
		default:
			try{
				ret_val=execute_system_call_routine(
					request_response,scene_search_tree,scene_counter,my_lock);
			}catch(Exception e) {
				e.printStackTrace();
				ret_val=null;
				debug_information.println("execute_system_call of client_interface_base fail");
				debug_information.println(e.toString());
			}
			break;
		}
		process_timeout(request_response,scene_search_tree,scene_counter);
		my_lock.unlock();
		return ret_val;
	}
	public scene_call_result execute_create_call(
			client_request_response request_response,
			scene_kernel_container_search_tree scene_search_tree,
			create_scene_counter scene_counter)
	{
		ReentrantLock my_lock;
		if((my_lock=client_interface_lock)==null)
			return null;
		my_lock.lock();
		
		scene_call_result ret_val=null;
		
		try{
			long delay_time_length;
			if((delay_time_length=manager_delay.process_delay_time_length())<0)
				debug_information.println("TIME OUT FOUND,Client ID is ",request_response.client_id);
			else
				ret_val=create_scene(delay_time_length,scene_search_tree,
									my_lock,request_response,scene_counter);
		}catch(Exception e){
			e.printStackTrace();
			ret_val=null;
			debug_information.println("execute_create_call of client_interface_base fail");
			debug_information.println(e.toString());
		}
		process_timeout(request_response,scene_search_tree,scene_counter);
		my_lock.unlock();
	
		return ret_val;
	}
	private scene_call_result process_process_bar_system_call_routine(
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
				if((process_bar=get_process_bar_routine(request_response))==null)
					break;
				str=process_bar.process_title+"+"+request_response.language_str;
				str=system_par.language_change_name.search_change_name(str,process_bar.process_title);
				str=jason_string.change_string(str+" "+process_bar.ex_process_title);
				
				long current_time=nanosecond_timer.absolute_nanoseconds();
				long time_length=current_time-process_bar.start_time;
				long scene_time_length=current_time-process_bar.original_time;
	
				request_response.println("{");
				request_response.print  ("	\"caption\":		",		str).						 							 println(",");
				request_response.print  ("	\"current\":		",		process_bar.current_process	).							 println(",");
				request_response.print  ("	\"max\":			",  	(process_bar.max_process<1)?1:(process_bar.max_process)).println(",");
				request_response.print  ("	\"time_length\":	",  	time_length/1000000			).							 println(",");
				request_response.print  ("	\"scene_time_length\":	",  scene_time_length/1000000	).							 println(",");
				request_response.print  ("	\"time_unit\":		\"",  	system_par.language_change_name.
					search_change_name("unit+"+request_response.language_str,"unit")).							 println("\"");
				request_response.println("}");
				
				break;	
			}
		return new scene_call_result(system_par.system_cors_string,request_response.response_content_type);
	}
	private client_process_bar get_process_bar_routine(client_request_response request_response)
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
	public void set_process_bar(
			client_request_response request_response,boolean reset_time_flag,
			String my_process_title,String my_ex_process_title,
			long my_current_process,long my_max_process)
	{
		ReentrantLock my_lock;
		if((my_lock=client_interface_lock)==null)
			return;
		my_lock.lock();
		
		client_process_bar p;
		if((p=get_process_bar_routine(request_response))!=null)
			p.set_process_bar(reset_time_flag,
				my_process_title,my_ex_process_title,
				my_current_process,my_max_process);
		
		my_lock.unlock();
	}
	public scene_call_result process_process_bar_system_call(client_request_response request_response)
	{
		ReentrantLock my_lock;
		if((my_lock=client_interface_lock)==null)
			return null;
		my_lock.lock();
		scene_call_result ret_val=process_process_bar_system_call_routine(request_response);
		my_lock.unlock();
		return ret_val;
	}
	private void process_timeout(client_request_response request_response,
			scene_kernel_container_search_tree scene_search_tree,create_scene_counter scene_counter)
	{
		for(long touch_time;(touch_time=tree.first_touch_time())>=0;){
			String 											my_key[]=tree.get_first_key();
			scene_kernel_and_client_information_container	my_value=tree.get_first_value();
			if(my_value.lock_number(0)>0)
				break;
			if((my_value.client_information==null)||(my_value.scene_kernel_cont==null)) {
				debug_information.println();
				debug_information.println(
						"((my_value.client_information==null)||(my_value.scene_kernel_cont==null))");
				debug_information.print  ("client_interface delete time out client_information found, client id is ");
				debug_information.print  (request_response.client_id);
				debug_information.println(",container ID is ",request_response.container_id);
			}else{
				long time_length=nanosecond_timer.absolute_nanoseconds()-touch_time;
				if(time_length<system_par.scene_expire_time_length)
					break;
				debug_information.println();
				debug_information.print  ("client_interface delete time out client_information found, client id is ");
				debug_information.println(request_response.client_id);
				debug_information.print  ("container ID is ",request_response.container_id);
				debug_information.print  (",Channel is ",	my_value.client_information.channel_id);
				debug_information.print  (",time interval ",time_length);
				debug_information.println(",max time interval  ",system_par.scene_expire_time_length);
			}
			
			if(my_value.scene_kernel_cont!=null) {
				scene_kernel sk;
				if((sk=my_value.scene_kernel_cont.sk)!=null)
					if(sk.component_cont!=null)
						if(sk.component_cont.root_component!=null){
							statistics_user.user_scene_kernel_number--;
							statistics_user.user_scene_component_number-=sk.component_cont.root_component.component_id+1;
						}
			}
			debug_information.println("Execute destroy_ek_ci_node");
			debug_information.print  ("user_scene_kernel_number:",statistics_user.user_scene_kernel_number);
			debug_information.println("/",statistics_user.user_max_scene_kernel_number);
			debug_information.print  ("user_sene_component_number:",statistics_user.user_scene_component_number);
			debug_information.println("/",statistics_user.user_max_scene_component_number);
			
			if(my_value.client_information!=null) {
				try{
					my_value.client_information.destroy();
				}catch(Exception e){
					e.printStackTrace();
					
					debug_information.println("Destroy client_information exception:	",e.toString());
				}
				my_value.client_information=null;
			}
			if(my_value.scene_kernel_cont!=null) {
				if(my_value.scene_kernel_cont.sk!=null)
					scene_search_tree.destroy_scene_kernel_container(
							my_value.scene_kernel_cont.sk.scene_name,
							my_value.scene_kernel_cont.sk.link_name,scene_counter);
				my_value.scene_kernel_cont=null;
			}
			tree.remove(my_key);
		}
	}
	private void destroy_routine(
			scene_kernel_container_search_tree scene_search_tree,
			create_scene_counter scene_counter)
	{	
		String my_key[];
		scene_kernel_and_client_information_container my_value;
		
		if(tree!=null) {
			while(tree.first_touch_time()>0){
				my_key=tree.get_first_key();
				my_value=tree.remove(my_key);
				
				if(my_value.client_information!=null) {
					my_value.client_information.destroy();
					my_value.client_information=null;
				}
				if(my_value.scene_kernel_cont!=null) {
					if(my_value.scene_kernel_cont.sk!=null) {
						scene_search_tree.destroy_scene_kernel_container(
								my_value.scene_kernel_cont.sk.scene_name,
								my_value.scene_kernel_cont.sk.link_name,
								scene_counter);
					};
					my_value.scene_kernel_cont=null;
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
			scene_kernel_container_search_tree scene_search_tree,
			create_scene_counter scene_counter)
	{
		ReentrantLock my_lock;
		if((my_lock=client_interface_lock)==null)
			return;
		
		my_lock.lock();
		try{
			destroy_routine(scene_search_tree,scene_counter);
		}catch(Exception e){
			e.printStackTrace();
			debug_information.println("destroy of client_interface_base fail:\t",e.toString());
		}
		my_lock.unlock();
	}
	private client_interface(
		client_request_response request_response,system_parameter my_system_par)
	{
		tree=new tree_string_search_container<scene_kernel_and_client_information_container>();
		
		system_par	 			=new system_parameter(my_system_par);
		process_bar_cont		=new client_process_bar_container(system_par.scene_expire_time_length);
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
			system_parameter my_system_par,scene_kernel_container_search_tree scene_search_tree,
			create_scene_counter scene_counter)
	{
		client_interface ret_val=new client_interface(request_response,my_system_par);
		if(ret_val.client_scene_file_name!=null)
			if(ret_val.client_scene_file_charset!=null)
				return ret_val;
		ret_val.destroy(scene_search_tree,scene_counter);
		return null;
	}
}
