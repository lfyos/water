package kernel_client_interface;

import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_engine.client_information;
import kernel_common_class.nanosecond_timer;
import kernel_common_class.debug_information;

public class dispatch_request_main
{
	static private String[] get_engine_result_routine(
			long delay_time_length,engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("command"))==null) {
			debug_information.println(
				"command string is null in do_get_engine_result_routine() of dispatch_request_main");
			return null;
		}
		if(ek.component_cont.root_component==null){
			debug_information.println(
				"(ek.component_cont.root_component==null) in do_get_engine_result_routine of dispatch_request_main");
			return null;
		}
		switch(str){
		case "creation":
			dispatch_create_engine_request.do_dispatch(0,ek,ci);
			ci.request_response.request_time=nanosecond_timer.absolute_nanoseconds();
			return null;
		case "initialization":
			ci.statistics_client.register_system_call_execute_number(1,0);
			return new String[] {
				ek.scene_par.scene_proxy_directory_name+"initialization.gzip_text",
				ek.system_par.network_data_charset
			};
		case "buffer":
			str=dispatch_buffer_request.do_dispatch(3,ek,ci);
			return (str==null)?null:(new String[] {str,ek.system_par.local_data_charset});
		case "part":
			return dispatch_part_request.do_dispatch(4,ek,ci);
		case "component":
			return dispatch_component_request.do_dispatch(5,delay_time_length,ek,ci);
		case "modifier":
			return dispatch_modifier_request.do_dispatch(6,ek, ci);
		case "collector":
			return dispatch_collector_request.do_dispatch(7, ek, ci);
		case "information":
			return dispatch_information_request.do_dispatch(8,ek,ci);
		case "termination":
			ci.request_response.request_time=0;
			ci.process_bar.touch_time=0;
			return null;
		default:
			ci.statistics_client.register_system_call_execute_number(9,0);
			debug_information.println(
				"Unknown command in do_get_engine_result_routine() of dispatch_request_main\t:\t",str);
			return null;
		}
	}
	static public String[] get_engine_result(long delay_time_length,
			engine_kernel ek,client_information ci)
	{
		ek.current_time.refresh_timer();
		String ret_val[]=get_engine_result_routine(delay_time_length,ek,ci);
		ek.process_reset();		
		return (ret_val==null)?null:new String[] {file_reader.separator(ret_val[0]),ret_val[1]};
	}
}
