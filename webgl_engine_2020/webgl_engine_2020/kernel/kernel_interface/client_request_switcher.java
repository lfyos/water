package kernel_interface;

import kernel_common_class.debug_information;
import kernel_common_class.nanosecond_timer;
import kernel_engine.engine_call_result;
import kernel_engine.interface_statistics;
import kernel_engine.system_parameter;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;
import kernel_network.network_implementation;
import kernel_program_javascript.javascript_program;

public class client_request_switcher
{
	private volatile int creation_engine_lock_number;
	synchronized private int test_creation_engine_lock_number(int modify_number)
	{
		creation_engine_lock_number+=modify_number;
		return creation_engine_lock_number;
	}
	private volatile system_parameter system_par;
	
	private engine_interface ei;
	private javascript_program program_javascript;
	private client_interface_container client_container;
	private proxy_downloader download_proxy;
	private interface_statistics statistics_interface;
	
	public void destroy()
	{
		if(system_par!=null)
			system_par=null;
		
		if(ei!=null) {
			ei.destroy();
			ei=null;
		}
		if(program_javascript!=null) {
			program_javascript.destroy();
			program_javascript=null;
		}
		if(client_container!=null) {
			client_container.destroy();
			client_container=null;
		}
	}
	
	private class system_call_switch_result
	{
		public client_interface client;
		public engine_call_result ecr;
		
		public system_call_switch_result()
		{
			client	=null;
			ecr		=null;
		}
	}
	private void process_bar(client_request_response request_response,client_interface client)
	{
		String str;
		client_process_bar process_bar;
		if((client=client_container.get_client_interface(request_response,system_par))==null)
			return;
		if((str=request_response.get_parameter("command"))==null)
			return;
		switch(str){
		case "request":
			process_bar=client.request_process_bar();
			request_response.println(process_bar.process_bar_id);
			process_bar.set_process_bar(true,"wait_for_create_scene", 1,2);
			break;
		case "release":
			if((str=request_response.get_parameter("process_bar"))!=null)
				client.release_process_bar(Integer.parseInt(str));
			break;
		case "data":
			if((process_bar=client.get_process_bar(request_response))==null)
				break;
			str=request_response.get_parameter("language");
			str=process_bar.process_title+"+"+((str==null)?"english":str);
			str=system_par.language_change_name.search_change_name(str,process_bar.process_title);
			long current_time=nanosecond_timer.absolute_nanoseconds();
			long time_length=current_time-process_bar.start_time;
			long engine_time_length=current_time-process_bar.original_time;

			request_response.println("{");
			request_response.print  ("	\"caption\":		\"",	str							).println("\",");
			request_response.print  ("	\"title\":			\"",	process_bar.process_title	).println("\",");
			request_response.print  ("	\"current\":		",		process_bar.current_process	).println(",");
			request_response.print  ("	\"max\":			",  	process_bar.max_process		).println(",");
			request_response.print  ("	\"time_length\":	",  	time_length/1000000			).println(",");
			request_response.print  ("	\"engine_time_length\":	",  engine_time_length/1000000	).println(",");
			request_response.print  ("	\"id\":				",		process_bar.process_bar_id	).println("");
			request_response.println("}");
			break;	
		}
	}
	private system_call_switch_result system_call_switch(client_request_response request_response)
	{
		system_call_switch_result ret_val=new system_call_switch_result();
		String str,channel_string=request_response.get_parameter("channel");
		switch((channel_string==null)?"javascript":channel_string){
		case "log":
			str=((str=request_response.get_parameter("command"))==null)?"":str;
			switch(str) {
			case "switch":
				system_par.proxy_par.switch_log_file();
				debug_information.println("Switch log file");
				break;
			case "delete":
				debug_information.close_log_file();
				file_writer.file_delete(system_par.proxy_par.engine_log_directory);
				file_writer.make_directory(system_par.proxy_par.engine_log_directory);
				system_par.proxy_par.switch_log_file();
				debug_information.println("Delete log file");
				break;
			}
			break;
		case "readme":
			ret_val.ecr=download_readme_file.download_driver_readme(request_response,
					system_par.data_root_directory_name+system_par.shader_file_name,
					system_par.local_data_charset,system_par.file_download_cors_string,
					Long.toString(system_par.file_buffer_expire_time_length));
			break;
		case "javascript":
			ret_val.ecr=program_javascript.create(request_response,
					Long.toString(system_par.file_buffer_expire_time_length),
					system_par.create_engine_sleep_time_length_scale,
					system_par.create_engine_sleep_time_length,
					system_par.create_engine_max_sleep_time_length);
			break;
		case "clear":
			if((ret_val.client=client_container.get_client_interface(request_response,system_par))!=null)
				ret_val.client.clear_all_engine();
			break;
		case "buffer":
			ret_val.ecr=download_proxy.download(request_response,system_par,statistics_interface);
			break;
		case "proxy":
			if((ret_val.ecr=download_proxy.download(request_response,system_par,statistics_interface))!=null)
				ret_val.ecr.date_string=null;
			break;
		case "process_bar":
			ret_val.ecr=new engine_call_result(null,null,null,null,null,"*");
			process_bar(request_response,ret_val.client);
			break;
		case "creation":
			if(test_creation_engine_lock_number(1)>system_par.create_engine_concurrent_number){
				ret_val.ecr=new engine_call_result(null,null,null,null,null,"*");
				request_response.println("true");
			}else if((ret_val.client=client_container.get_client_interface(request_response,system_par))!=null)
					ret_val.ecr=ret_val.client.execute_create_call(ei,request_response,statistics_interface);
			test_creation_engine_lock_number(-1);
			break;
		default:
			if((ret_val.client=client_container.get_client_interface(request_response,system_par))!=null) {
				long channel_id;
				try{
					channel_id=Long.decode(channel_string);
				}catch(Exception e) {
					debug_information.println("Channel id is wrong");
					debug_information.println("client:",request_response.implementor.get_client_id());
					debug_information.println(",Channel:",channel_string);
					debug_information.println(",exception:",e.toString());
					e.printStackTrace();
					return null;
				}
				ret_val.ecr=ret_val.client.execute_system_call(channel_id,
					request_response,statistics_interface,ei.engine_current_number);
			}
			break;
		}
		return ret_val;
	}
	synchronized private void create_system_parameter(network_implementation network_implementor,
			String data_configure_environment_variable,String proxy_configure_environment_variable)
	{
		if(system_par==null)
			system_par=new system_parameter(network_implementor.get_application_directory(),
					data_configure_environment_variable,proxy_configure_environment_variable);
	}
	public void process_system_call(network_implementation network_implementor,
			String data_configure_environment_variable,String proxy_configure_environment_variable)
	{
		if(system_par==null)
			create_system_parameter(network_implementor,
				data_configure_environment_variable,proxy_configure_environment_variable);
	
		client_request_response request_response=new client_request_response(
			system_par.network_data_charset,network_implementor);
		
		system_call_switch_result scrr=system_call_switch(request_response);

		if(scrr.ecr!=null){
			String compress_response_header;
			if(scrr.ecr.compress_file_name==null)
					compress_response_header=null;
			else if((compress_response_header=request_response.implementor.get_header("Accept-Encoding"))==null)
				scrr.ecr.compress_file_name=null;
			else if((compress_response_header=compress_response_header.toLowerCase()).indexOf("gzip")>=0)
					compress_response_header="gzip";
			else if(compress_response_header.indexOf("deflate")>=0)
					compress_response_header="deflate";
			else if(compress_response_header.indexOf("br")>=0)
					compress_response_header="br";
			else{
					compress_response_header=null;
					scrr.ecr.compress_file_name=null;
			}

			long my_statistics[];		
			if(scrr.ecr.file_name!=null){
				statistics_interface.responsing_file_data_number++;
				my_statistics=request_response.response_file_data(compress_response_header,scrr.ecr,system_par);
				statistics_interface.responsing_file_data_number--;
				
				statistics_interface.file_download_number++;
				statistics_interface.file_download_data_uncompress_length+=my_statistics[0];
				statistics_interface.file_download_data_compress_length	 +=my_statistics[1];
			}else{
				statistics_interface.responsing_network_data_number++;
				my_statistics=request_response.response_network_data(compress_response_header,scrr.ecr,system_par);
				statistics_interface.responsing_network_data_number--;
				
				statistics_interface.network_data_number++;
				statistics_interface.network_data_uncompress_length		+=my_statistics[0];
				statistics_interface.network_data_compress_length		+=my_statistics[1];
			}
		}

		request_response.destroy();
		
		if(scrr.client!=null)
			scrr.client.touch_time=nanosecond_timer.absolute_nanoseconds();
		
		return;
	}
	
	public client_request_switcher()
	{
		creation_engine_lock_number	=0;
		system_par					=null;
		
		ei							=new engine_interface();
		program_javascript			=new javascript_program();
		client_container			=new client_interface_container();
		download_proxy				=new proxy_downloader();
		statistics_interface		=new interface_statistics();
	}
}


