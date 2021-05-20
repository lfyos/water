package kernel_client_interface;

import kernel_common_class.const_value;
import kernel_common_class.debug_information;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;


public class dispatch_modifier_request
{
	private static int get_modifier_id(engine_kernel ek,client_information ci)
	{
		String str;
		int modifier_id=-1;
		if((str=ci.request_response.get_parameter("modifier"))==null){
			debug_information.println(
					"modifier ID is NULL in do_dispatch() of dispatch_modifier_request");
			return -1;
		}
		try{
			modifier_id=Integer.decode(str);
		}catch(Exception e){
			return -1;
		}
		if(modifier_id<0)
			return -1;
		if(modifier_id>=ek.modifier_cont.length)
			return -1;
		return modifier_id;
	}
	public static String[] do_dispatch(int main_call_id,engine_kernel ek,client_information ci)
	{
		String str;
		int modifier_id=-1;
		
		if((str=ci.request_response.get_parameter("method"))==null){
			debug_information.println("method is NULL in do_dispatch() of dispatch_modifier_request");
			return null;
		}
		switch(str){
		case "clear":
			ci.statistics_client.register_system_call_execute_number(main_call_id,0);
			if((modifier_id=get_modifier_id(ek,ci))>=0)
				ek.modifier_cont[modifier_id].clear_modifier(ek,ci);
			else
				for(int i=0,ni=ek.modifier_cont.length;i<ni;i++)
					ek.modifier_cont[i].clear_modifier(ek,ci);
			return null;
		case "set_speed":
			ci.statistics_client.register_system_call_execute_number(main_call_id,1);
			if((str=ci.request_response.get_parameter("speed"))==null){
				debug_information.println("No speed in do_dispatch() of dispatch_modifier_request");
				return null;
			}
			double new_speed=Double.parseDouble(str);
			if(new_speed<=const_value.min_value){
				debug_information.println(
					"too low new speed in do_dispatch() of dispatch_modifier_request\t:\t",new_speed);
				return null;
			}
			if((modifier_id=get_modifier_id(ek,ci))>=0)
				ek.modifier_cont[modifier_id].get_timer().modify_speed(new_speed,ek.current_time);
			else
				for(int i=0;i<ek.modifier_cont.length;i++)
					ek.modifier_cont[i].get_timer().modify_speed(new_speed,ek.current_time);
			return null;
		case "get_speed":
			ci.statistics_client.register_system_call_execute_number(main_call_id,2);
			ci.request_response.print("[");
			for(int i=0;i<ek.modifier_cont.length;i++) {
				if(i>0)
					ci.request_response.print(",");
				ci.request_response.print(ek.modifier_cont[i].get_timer().get_speed());
			}
			ci.request_response.print("]");
			return null;
		case "set_time":
			ci.statistics_client.register_system_call_execute_number(main_call_id,3);
			if((str=ci.request_response.get_parameter("time"))==null){
				debug_information.println("No time value in do_dispatch() of dispatch_modifier_request");
				return null;
			}
			long new_current_time=Long.decode(str);
			if((modifier_id=get_modifier_id(ek,ci))>=0)
				ek.modifier_cont[modifier_id].get_timer().modify_current_time(new_current_time,ek.current_time);
			else
				for(int i=0;i<ek.modifier_cont.length;i++)
					ek.modifier_cont[i].get_timer().modify_current_time(new_current_time,ek.current_time);
			return null;
		}
		return null;
	}
}
