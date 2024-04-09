package kernel_client_interface;

import kernel_common_class.component_collector_jason_part;
import kernel_component.component_collector;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class dispatch_collector_request
{
	static public String[] do_dispatch(engine_kernel ek,client_information ci)
	{
		String str;
		component_collector cc,all_part_list[];
		boolean simple_list_flag=false,single_collector_flag=false,location_flag=false;
		
		if((str=ci.request_response.get_parameter("simple"))!=null)
			switch(str.toLowerCase()) {
			case "true":
			case "yes":
				simple_list_flag=true;
			}
		if((str=ci.request_response.get_parameter("single"))!=null)
			switch(str.toLowerCase()) {
			case "true":
			case "yes":
				single_collector_flag=true;
			}
		if((str=ci.request_response.get_parameter("location"))!=null)
			switch(str.toLowerCase()) {
			case "true":
			case "yes":
				location_flag=true;
			}
	
		ci.request_response.println("[");
		if(single_collector_flag) {
			if((cc=ek.collector_stack.get_top_collector())!=null)
				new component_collector_jason_part("",
						simple_list_flag,true,location_flag,false,cc,null,ci,ek);
		}else {
			if((all_part_list=ek.collector_stack.get_all_collector())!=null)
				for(int i=0,ni=all_part_list.length;i<ni;i++)
					new component_collector_jason_part("",
							simple_list_flag,i==(ni-1),location_flag,false,all_part_list[i],null,ci,ek);
		}
		ci.request_response.println("]");
		return null;
	}
}
