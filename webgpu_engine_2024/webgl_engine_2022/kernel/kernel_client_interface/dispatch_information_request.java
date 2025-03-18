package kernel_client_interface;

import kernel_component.component_collector;

import kernel_information.scene_information;
import kernel_information.scene_camera_information;
import kernel_information.sequence_part_information;
import kernel_information.sort_part_information;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_information.array_part_information;
import kernel_information.component_tree_information;
import kernel_information.component_array_information;
import kernel_information.component_collector_with_component_information;
import kernel_information.component_selected_information;
import kernel_information.component_collector_of_target_information;

public class dispatch_information_request
{
	public static String[] do_dispatch(scene_kernel sk,client_information ci)
	{
		String str;
		component_collector cc;
		
		if((str=ci.request_response.get_parameter("method"))==null)
			str="scene";
		switch(str) {
		case "scene":
			(new scene_information(sk,ci)).output();
			break;
		case "part":
			if((str=ci.request_response.get_parameter("operation"))==null)
				str="sequence";
			switch(str) {
			case "sequence":
				(new sequence_part_information(sk,ci)).output();
				break;
			case "sort":
				(new sort_part_information(sk,ci)).output();
				break;
			case "array":
				(new array_part_information(sk,ci)).output();
				break;
			}
			break;
		case "component":
			if((str=ci.request_response.get_parameter("operation"))==null)
				str="selected";
			if(sk.component_cont.root_component!=null)
				switch(str) {
				case "tree":
					(new component_tree_information(sk.component_cont.root_component,ci)).output();
					break;
				case "selected":
					(new component_selected_information(sk,ci)).output();
					break;
				case "sorted":
					(new component_array_information(sk,ci)).output();
					break;
				}
			break;
		case "collector":
			if((str=ci.request_response.get_parameter("operation"))==null)
				str="list";
			switch(str) {
			case "list":
				if((cc=sk.collector_stack.get_top_collector())!=null)
					(new component_collector_with_component_information(cc,sk,ci)).output();
				break;
			case "display":
				if((cc=ci.display_component_collector)!=null)
					(new component_collector_with_component_information(cc,sk,ci)).output();
				break;
			case "target":
				(new component_collector_of_target_information(sk,ci)).output();
				break;
			}
			break;
		case "camera":
			(new scene_camera_information(sk,ci)).output();
			break;
		}
		return null;
	}
}
