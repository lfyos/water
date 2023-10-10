package kernel_interface;

import kernel_common_class.debug_information;
import kernel_common_class.nanosecond_timer;
import kernel_engine.engine_call_result;
import kernel_engine.create_engine_counter;
import kernel_engine.system_parameter;
import kernel_network.client_request_response;
import kernel_network.network_implementation;
import kernel_program_javascript.javascript_program;

public class client_request_switcher
{
	private system_parameter system_par;
	private javascript_program program_javascript;
	private client_interface_container client_container[];
	private int next_client_container_id;
	
	private engine_interface_container engine_container;
	private proxy_downloader download_proxy;
	private create_engine_counter engine_counter;
	
	private volatile int creation_engine_lock_number;
	synchronized private int test_creation_engine_lock_number(int modify_number)
	{
		creation_engine_lock_number+=modify_number;
		return creation_engine_lock_number;
	}
	public void destroy()
	{
		if(system_par!=null)
			system_par=null;
		if(program_javascript!=null) {
			program_javascript.destroy();
			program_javascript=null;
		}
		if(client_container!=null) {
			for(int i=0,ni=client_container.length;i<ni;i++)
				if(client_container[i]!=null) {
					client_container[i].destroy();
					client_container[i]=null;
				}
			client_container=null;
		}
		if(engine_container!=null) {
			engine_container.destroy();
			engine_container=null;
		}
		if(download_proxy!=null) {
			download_proxy.destroy();
			download_proxy=null;
		}
		if(engine_counter!=null)
			engine_counter=null;
		creation_engine_lock_number=0;
	}
	private client_interface get_client_interface(client_request_response request_response)
	{
		String my_user_name,my_pass_word,my_client_id;
		String my_request_charset=request_response.implementor.get_request_charset();

		if((my_user_name=request_response.get_parameter("user_name"))==null)
			my_user_name="NoName";
		else
			try{
				my_user_name=java.net.URLDecoder.decode(my_user_name,my_request_charset);
				my_user_name=java.net.URLDecoder.decode(my_user_name,my_request_charset);
			}catch(Exception e) {
				;
			}
		if((my_pass_word=request_response.get_parameter("pass_word"))==null)
			my_pass_word="NoPassword";
		else
			try{
				my_pass_word=java.net.URLDecoder.decode(my_pass_word,my_request_charset);
				my_pass_word=java.net.URLDecoder.decode(my_pass_word,my_request_charset);
			}catch(Exception e) {
				;
			}
		
		if((my_client_id=request_response.implementor.get_client_id())==null)
			my_client_id="NoClientID";
		
		int my_container_id;
		do {
			String my_container_str;
			if((my_container_str=request_response.get_parameter("container"))!=null)
				try{
					if((my_container_id=Integer.decode(my_container_str))>=0)
						break;
				}catch(Exception e){
					;
				}
			my_container_id=next_client_container_id++;
			next_client_container_id%=client_container.length;
		}while(false);
		
		my_container_id%=client_container.length;
		
		return client_container[my_container_id].get_client_interface(
					my_user_name,my_pass_word,my_client_id,my_container_id,system_par);
	}
	private engine_call_result system_call_switch(client_request_response request_response)
	{
		engine_call_result ecr=null;
		client_interface client=null;
		String channel_string=request_response.get_parameter("channel");
		switch((channel_string==null)?"switch":channel_string){
		case "switch":
			if((channel_string=system_par.switch_server.get_switch_server_url(request_response,system_par))!=null){
				debug_information.println();
				debug_information.println("client 		",		request_response.implementor.get_client_id());
				debug_information.println("switch from	",		request_response.implementor.get_url());
				debug_information.println("to		",			channel_string);
				request_response.implementor.redirect_url(channel_string+"?channel=javascript","*");
				break;
			}
		case "javascript":
			ecr=program_javascript.create(request_response,system_par);
			break;
		case "readme":
			ecr=download_readme_file.download_driver_readme(request_response,
				system_par.data_root_directory_name+system_par.shader_file_name,
				system_par.local_data_charset,system_par.file_download_cors_string,
				Long.toString(system_par.file_buffer_expire_time_length),
				system_par.text_class_charset,system_par.text_jar_file_charset);
			break;
		case "buffer":
			ecr=download_proxy.download(request_response,system_par);
			break;
		case "proxy":
			if((ecr=download_proxy.download(request_response,system_par))!=null)
				ecr.date_string=null;
			break;
		case "process_bar":
			if((client=get_client_interface(request_response))!=null)
				ecr=client.process_process_bar_system_call(request_response);
			break;
		case "creation":
			if((client=get_client_interface(request_response))!=null){
				if(test_creation_engine_lock_number(1)<system_par.create_engine_concurrent_number)
					ecr=client.execute_create_call(request_response,engine_container,engine_counter);
				else{
					request_response.println("false");
					ecr=new engine_call_result(null,null,null,null,null,"*");
					client.get_process_bar(request_response).set_process_bar(true,"wait_for_other_exit","",1,2);
				}
				test_creation_engine_lock_number(-1);
			}
			break;
		default:
			long channel_id;
			try{
				channel_id=Long.decode(channel_string);
			}catch(Exception e) {
				debug_information.println("Channel id is wrong");
				debug_information.println("client:",request_response.implementor.get_client_id());
				debug_information.println("Channel:",channel_string);
				debug_information.println("exception:",e.toString());
				e.printStackTrace();
				break;
			}
			if((client=get_client_interface(request_response))!=null)
				ecr=client.execute_system_call(channel_id,request_response,engine_container,engine_counter);
			break;
		}
		long current_time=nanosecond_timer.absolute_nanoseconds();
		if(client!=null)
			client.touch_time=current_time;
		if(request_response.request_time>0)
			request_response.request_time=current_time;
		return ecr;
	}
	synchronized private void init(network_implementation network_implementor,
		String data_configure_environment_variable,String proxy_configure_environment_variable)
	{
		if(system_par==null){
			system_parameter my_system_par=new system_parameter(
					data_configure_environment_variable,proxy_configure_environment_variable);
			program_javascript=new javascript_program(my_system_par);
			
			client_container=new client_interface_container[my_system_par.max_client_container_number];
			for(int i=0,ni=client_container.length;i<ni;i++)
				client_container[i]=new client_interface_container();
			system_par=my_system_par;
		}
	}
	public void process_system_call(
			String data_configure_environment_variable,
			String proxy_configure_environment_variable,
			network_implementation network_implementor)
	{
		if(system_par==null)
			init(network_implementor,data_configure_environment_variable,proxy_configure_environment_variable);
		
		client_request_response request_response=new client_request_response(
			system_par.network_data_charset,network_implementor);
		
		engine_call_result ecr;
		if((ecr=system_call_switch(request_response))!=null){
			String compress_response_header;
			if(ecr.compress_file_name==null)
					compress_response_header=null;
			else if((compress_response_header=request_response.implementor.get_header("Accept-Encoding"))==null)
					ecr.compress_file_name=null;
			else if((compress_response_header=compress_response_header.toLowerCase()).indexOf("gzip")>=0)
					compress_response_header="gzip";
			else if(compress_response_header.indexOf("deflate")>=0)
					compress_response_header="deflate";
			else if(compress_response_header.indexOf("br")>=0)
					compress_response_header="br";
			else{
					compress_response_header=null;
					ecr.compress_file_name=null;
			}
			if(ecr.file_name!=null)
				request_response.response_file_data(compress_response_header,ecr,system_par);
			else
				request_response.response_network_data(compress_response_header,ecr,system_par);
		}
		request_response.destroy();
		return;
	}
	public client_request_switcher()
	{
		system_par			=null;
		program_javascript	=null;
		client_container	=null;
		
		engine_container	=new engine_interface_container();
		download_proxy		=new proxy_downloader();
		engine_counter		=new create_engine_counter();
		
		creation_engine_lock_number	=0;
		next_client_container_id=0;
	}
}