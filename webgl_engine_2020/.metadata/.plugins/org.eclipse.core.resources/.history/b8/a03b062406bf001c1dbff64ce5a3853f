package kernel_client_interface;

import kernel_common_class.debug_information;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;


public class dispatch_modifier_request
{
	public static String[] do_dispatch(int main_call_id,engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("method"))==null){
			debug_information.println("method is NULL in do_dispatch() of dispatch_modifier_request");
			return null;
		}
		switch(str){
		case "clear":
			ci.statistics_client.register_system_call_execute_number(main_call_id,0);
			ek.modifier_cont.clear_modifier(ek,ci);
			return null;
		case "set_time":
			ci.statistics_client.register_system_call_execute_number(main_call_id,1);
			if((str=ci.request_response.get_parameter("time"))==null){
				debug_information.println("No time value in do_dispatch() of dispatch_modifier_request");
				return null;
			}
			long new_current_time=Long.decode(str);
			ek.modifier_cont.get_timer().modify_current_time(new_current_time,ek.current_time);
			return null;
		}
		return null;
	}
}
