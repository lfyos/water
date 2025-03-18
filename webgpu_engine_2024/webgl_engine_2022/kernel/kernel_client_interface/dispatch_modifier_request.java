package kernel_client_interface;

import kernel_common_class.debug_information;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;


public class dispatch_modifier_request
{
	public static String[] do_dispatch(scene_kernel sk,client_information ci)
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
		if((modifier_id<0)||(modifier_id>=sk.modifier_cont.length)){
			debug_information.println("modifier id is wrong in do_dispatch() of dispatch_modifier_request:	",modifier_id);
			return null;
		}
		switch(str){
		case "clear":
			sk.modifier_cont[modifier_id].process(sk,ci,true);
			return null;
		case "set_time":
			if((str=ci.request_response.get_parameter("time"))==null){
				debug_information.println("No time value in do_dispatch() of dispatch_modifier_request");
				return null;
			}
			long new_current_time=Long.decode(str);
			sk.modifier_cont[modifier_id].get_timer().modify_current_time(new_current_time,sk.current_time);
			return null;
		}
		return null;
	}
}
