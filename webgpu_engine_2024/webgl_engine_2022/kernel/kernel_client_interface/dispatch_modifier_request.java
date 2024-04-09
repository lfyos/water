package kernel_client_interface;

import kernel_common_class.debug_information;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;


public class dispatch_modifier_request
{
	public static String[] do_dispatch(engine_kernel ek,client_information ci)
	{
		String str,modifier_str;
		
		if((str=ci.request_response.get_parameter("method"))==null){
			debug_information.println("method is NULL in do_dispatch() of dispatch_modifier_request");
			return null;
		}
		if((modifier_str=ci.request_response.get_parameter("modifier"))==null){
			debug_information.println("modifier is NULL in do_dispatch() of dispatch_modifier_request");
			return null;
		}
		int modifier_id;
		try {
			modifier_id=Integer.decode(modifier_str);
		}catch(Exception e) {
			debug_information.println("modifier is wrong in do_dispatch() of dispatch_modifier_request:	",modifier_str);
			return null;
		}
		if((modifier_id<0)||(modifier_id>=ek.modifier_cont.length)){
			debug_information.println("modifier id is wrong in do_dispatch() of dispatch_modifier_request:	",modifier_id);
			return null;
		}
		switch(str){
		case "clear":
			ek.modifier_cont[modifier_id].process(ek,ci,true);
			return null;
		case "set_time":
			if((str=ci.request_response.get_parameter("time"))==null){
				debug_information.println("No time value in do_dispatch() of dispatch_modifier_request");
				return null;
			}
			long new_current_time=Long.decode(str);
			ek.modifier_cont[modifier_id].get_timer().modify_current_time(new_current_time,ek.current_time);
			return null;
		}
		return null;
	}
}
