package engine_interface;

import kernel_engine.system_parameter;
import kernel_engine.engine_call_result;
import kernel_interface.client_interface;
import kernel_engine.create_engine_counter;
import kernel_network.network_implementation;
import kernel_common_class.debug_information;
import kernel_interface.file_download_manager;
import kernel_network.client_request_response;
import kernel_program_javascript.javascript_program;
import kernel_interface.client_interface_search_tree;
import kernel_engine.engine_kernel_container_search_tree;

public class system_engine 
{
	private int next_client_container_id;
	private client_interface_search_tree client_interface_search_tree_array[];
	
	private system_parameter system_par;
	private javascript_program program_javascript;

	private engine_kernel_container_search_tree engine_search_tree;
	private create_engine_counter engine_counter;
	
	private volatile int creation_engine_lock_number;
	synchronized private int test_creation_engine_lock_number(int modify_number)
	{
		creation_engine_lock_number+=modify_number;
		return creation_engine_lock_number;
	}
	public void destroy()
	{
		if(client_interface_search_tree_array!=null) {
			for(int i=0,ni=client_interface_search_tree_array.length;i<ni;i++)
				if(client_interface_search_tree_array[i]!=null) {
					client_interface_search_tree_array[i].destroy(
							system_par,engine_search_tree,engine_counter);
					client_interface_search_tree_array[i]=null;
				}
			client_interface_search_tree_array=null;
		}
		
		if(system_par!=null)
			system_par=null;
		if(program_javascript!=null) {
			program_javascript.destroy();
			program_javascript=null;
		}
		if(engine_search_tree!=null) {
			engine_search_tree.destroy();
			engine_search_tree=null;
		}
		if(engine_counter!=null)
			engine_counter=null;
	}
	private engine_call_result system_call_switch(client_request_response request_response)
	{
		engine_call_result ecr=null;
		client_interface client=null;
		
		switch(request_response.channel_string){
		case "switch":
		{
			String switch_url=system_par.switch_server.get_switch_server_url(request_response,system_par);
			if(switch_url!=null){
				debug_information.println();
				debug_information.println("client 		",		request_response.client_id);
				debug_information.println("switch from	",		request_response.implementor.get_url());
				debug_information.println("to		",			switch_url);
				request_response.implementor.redirect_url(
						switch_url+"?channel=javascript",system_par.system_cors_string);
				break;
			}
		}
		case "javascript":
			ecr=program_javascript.create(request_response,system_par);
			break;
		case "buffer":
			ecr=file_download_manager.download(request_response,system_par);
			break;
		case "process_bar":
			if((client=client_interface_search_tree_array[request_response.container_id].get_client_interface(
					request_response,system_par,engine_search_tree,engine_counter))==null)
				break;
			ecr=client.process_process_bar_system_call(request_response);
			break;
		case "creation":
			if((client=client_interface_search_tree_array[request_response.container_id].get_client_interface(
					request_response,system_par,engine_search_tree,engine_counter))==null)
				break;
			if(test_creation_engine_lock_number(1)<system_par.create_engine_concurrent_number)
				ecr=client.execute_create_call(request_response,engine_search_tree,engine_counter);
			else{
				request_response.println("false");
				ecr=new engine_call_result(system_par.system_cors_string,request_response.response_content_type);
				client.get_process_bar(request_response).set_process_bar(true,"wait_for_other_exit","",1,2);
			}
			test_creation_engine_lock_number(-1);
			break;
		default:
			if((client=client_interface_search_tree_array[request_response.container_id].get_client_interface(
					request_response,system_par,engine_search_tree,engine_counter))==null)
				break;
			ecr=client.execute_system_call(request_response,engine_search_tree,engine_counter);
			break;
		}
		return ecr;
	}
	
	public void process_system_call(network_implementation network_implementor)
	{
		engine_call_result ecr;
		client_request_response request_response;
		
		request_response=new client_request_response(
				system_par.network_data_charset,network_implementor);
		if(request_response.container_id<0) {
			request_response.container_id=next_client_container_id++;
			next_client_container_id%=client_interface_search_tree_array.length;
		};
		request_response.container_id%=client_interface_search_tree_array.length;

		if((ecr=system_call_switch(request_response))!=null) {
			if(ecr.file_name!=null) 
				request_response.response_file_data(ecr,system_par);
			else
				request_response.response_network_data(ecr,system_par);
		}
		request_response.destroy();
	}
	
	public system_engine(
			String data_file_configure_file_name,
			String temporary_file_configure_file_name)
	{
		system_par			=new system_parameter(
									data_file_configure_file_name,
									temporary_file_configure_file_name);
		program_javascript	=new javascript_program(system_par);
		
		client_interface_search_tree_array=new client_interface_search_tree[
		                          system_par.max_client_container_number];
		for(int i=0,ni=client_interface_search_tree_array.length;i<ni;i++)
			client_interface_search_tree_array[i]=new client_interface_search_tree();
		next_client_container_id=0;

		engine_search_tree	=new engine_kernel_container_search_tree();
		engine_counter		=new create_engine_counter();
		
		creation_engine_lock_number	=0;
	}
}
