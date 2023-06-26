package kernel_client_interface;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_component.component_collector;

import kernel_information.engine_information;
import kernel_information.engine_camera_information;
import kernel_information.sequence_part_information;
import kernel_information.sort_part_information;
import kernel_information.array_part_information;
import kernel_information.component_tree_information;
import kernel_information.component_array_information;
import kernel_information.component_collector_with_component_information;
import kernel_information.component_selected_information;
import kernel_information.component_collector_of_target_information;

public class dispatch_information_request
{
	public static String[] do_dispatch(int main_call_id,engine_kernel ek,client_information ci)
	{
		String str;
		component_collector cc;
		
		if((str=ci.request_response.get_parameter("method"))==null)
			str="engine";
		switch(str) {
		case "engine":
			ci.statistics_client.register_system_call_execute_number(main_call_id,0);
			if((str=ci.request_response.get_parameter("operation"))==null)
				str="get";
			switch(str) {
			case "reset":
				ci.statistics_client.clear();
				ci.statistics_engine.clear();
			case "get":
				break;
			}
			
			(new engine_information(ek,ci)).output();
			break;
		case "part":
			ci.statistics_client.register_system_call_execute_number(main_call_id,1);
			if((str=ci.request_response.get_parameter("operation"))==null)
				str="sequence";
			switch(str) {
			case "sequence":
				(new sequence_part_information(ek,ci)).output();
				break;
			case "sort":
				(new sort_part_information(ek,ci)).output();
				break;
			case "array":
				(new array_part_information(ek,ci)).output();
				break;
			}
			break;
		case "component":
			ci.statistics_client.register_system_call_execute_number(main_call_id,2);
			if((str=ci.request_response.get_parameter("operation"))==null)
				str="selected";
			if(ek.component_cont.root_component!=null)
				switch(str) {
				case "tree":
					(new component_tree_information(ek.component_cont.root_component,ci)).output();
					break;
				case "selected":
					(new component_selected_information(ek,ci)).output();
					break;
				case "sorted":
					(new component_array_information(ek,ci)).output();
					break;
				}
			break;
		case "collector":
			ci.statistics_client.register_system_call_execute_number(main_call_id,3);
			if((str=ci.request_response.get_parameter("operation"))==null)
				str="list";
			switch(str) {
			case "list":
				if((cc=ek.collector_stack.get_top_collector())!=null)
					(new component_collector_with_component_information(cc,ek,ci)).output();
				break;
			case "display":
				if((cc=ci.display_component_collector)!=null)
					(new component_collector_with_component_information(cc,ek,ci)).output();
				break;
			case "target":
				(new component_collector_of_target_information(ek,ci)).output();
				break;
			}
			break;
		case "camera":
			ci.statistics_client.register_system_call_execute_number(main_call_id,4);
			(new engine_camera_information(ek,ci)).output();
			break;
		}
		return null;
	}
}
