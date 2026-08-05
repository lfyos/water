package kernel_scene;

import kernel_interface.client_interface;
import kernel_network.network_implementation;
import kernel_common_class.debug_information;
import kernel_interface.file_download_manager;
import kernel_network.client_request_response;
import kernel_program_javascript.javascript_program;
import kernel_interface.client_interface_search_tree;
import kernel_common_class.tree_string_locker_container;

public class system_scene
{
	private client_interface_search_tree client_interface_search_tree_array[];
	private scene_kernel_container_search_tree scene_kernel_search_tree;
	private tree_string_locker_container string_locker_container;
	private javascript_program program_javascript;
	private create_scene_counter scene_counter;
	private system_parameter system_par;
	
	public void destroy()
	{
		if(client_interface_search_tree_array!=null) {
			for(int i=0,ni=client_interface_search_tree_array.length;i<ni;i++)
				if(client_interface_search_tree_array[i]!=null) {
					client_interface_search_tree_array[i].destroy(scene_counter);
					client_interface_search_tree_array[i]=null;
				}
			client_interface_search_tree_array=null;
		}
		if(scene_kernel_search_tree!=null) {
			scene_kernel_search_tree.destroy();
			scene_kernel_search_tree=null;
		}
		if(string_locker_container!=null) {
			string_locker_container.destroy();
			string_locker_container=null;
		}
		if(program_javascript!=null) {
			program_javascript.destroy();
			program_javascript=null;
		}
		if(system_par!=null)
			system_par=null;
		if(scene_counter!=null)
			scene_counter=null;
	}
	private volatile int creation_scene_lock_number;
	synchronized private int test_creation_scene_lock_number(int modify_number)
	{
		creation_scene_lock_number+=modify_number;
		return creation_scene_lock_number;
	}
	private scene_call_result system_call_switch(client_request_response request_response)
	{
		scene_call_result ecr;
		client_interface client;

		switch(request_response.channel_string){
		case "switch":
		{
			String switch_url=system_par.switch_server.get_switch_server_url(request_response);
			if(switch_url!=null)
				if((switch_url=switch_url.trim()).length()>0){
					debug_information.println();
					debug_information.println("client 		",		request_response.client_id);
					debug_information.println("switch from	",		request_response.implementor.get_url());
					debug_information.println("to		",			switch_url);
					request_response.implementor.redirect_url(switch_url+"?channel=javascript");
					break;
				}
		}
		case "javascript":
			return program_javascript.create(request_response);
		case "buffer":
			return file_download_manager.download(request_response,system_par,string_locker_container);
		case "process_bar":
			if((client=client_interface_search_tree_array[request_response.container_id].
				get_client_interface(request_response,
					scene_kernel_search_tree,string_locker_container,scene_counter))!=null)
						return client.process_process_bar_system_call(request_response);
			break;
		case "creation":
			if((client=client_interface_search_tree_array[request_response.container_id].
				get_client_interface(request_response,
						scene_kernel_search_tree,string_locker_container,scene_counter))==null)
			{
				request_response.reset().println("\"get_client_interface fail\"");
				return new scene_call_result();
			}
			if(test_creation_scene_lock_number(1)>=system_par.create_scene_concurrent_number){
				request_response.reset().println("[]");
				client.set_process_bar(request_response,true,"wait_for_other_exit","",1,2);
				ecr=new scene_call_result();
			}else if((ecr=client.execute_create_call(request_response))==null){
				request_response.reset().println("\"execute_create_call fail\"");
				ecr=new scene_call_result();
			}
			test_creation_scene_lock_number(-1);
			return ecr;
		default:
			if((client=client_interface_search_tree_array[request_response.container_id].
				get_client_interface(request_response,
					scene_kernel_search_tree,string_locker_container,scene_counter))!=null)
						return client.execute_system_call(request_response);
			break;
		}
		return null;
	}

	public void process_system_call(network_implementation network_implementor)
	{
		scene_call_result ecr;
		String request_charset_name;
		
		if((request_charset_name=network_implementor.get_request_charset())==null)
			request_charset_name=system_par.network_data_charset;
		client_request_response request_response=new client_request_response(
			request_charset_name,network_implementor,system_par);
		
		if((ecr=system_call_switch(request_response))!=null) {
			if(ecr.result_file_name==null)
				request_response.response_network_data(ecr,system_par);
			else 
				request_response.response_file_data(ecr,system_par,string_locker_container);
		}
		request_response.destroy();
		return;
	}
	public void process_option(network_implementation network_implementor)
	{
		network_implementor.set_option_http_header(system_par.access_control_max_age);
	}
	public system_scene(
			String scene_data_path_name,
			String scene_temparatory_path_name,
			String scene_environment_path_name)
	{
		system_par=new system_parameter(
				scene_data_path_name,
				scene_temparatory_path_name,
				scene_environment_path_name);
		
		int number=system_par.max_client_container_number;
		client_interface_search_tree_array=new client_interface_search_tree[number];
		for(int i=0;i<number;i++)
			client_interface_search_tree_array[i]=new client_interface_search_tree(system_par);
		
		scene_kernel_search_tree	=new scene_kernel_container_search_tree();
		string_locker_container		=new tree_string_locker_container();
		program_javascript			=new javascript_program(system_par);
		scene_counter				=new create_scene_counter();
		creation_scene_lock_number	=0;
	}
}
