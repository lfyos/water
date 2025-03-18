package driver_manipulator;

import kernel_component.component;
import kernel_component.component_array;
import kernel_component.component_selection;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class operate_selection
{
	public static void selection_request(int parameter_channel_id,scene_kernel sk,client_information ci)
	{
		String str,component_str;

		if(ci.display_camera_result.target==null)
			return;
		if((str=ci.request_response.get_parameter("event_operation"))==null)
			return;
		component_selection cs=new component_selection(sk);
		
		switch(str=str.trim()) {
		case "parent":
			cs.set_parent_selected(sk.component_cont);
			break;
		case "child":
			cs.set_child_selected(sk.component_cont);
			break;
		case "move":
			component comp=sk.component_cont.search_component();
			cs.set_moved_descendant_selected(
					(comp!=null)?comp:(sk.component_cont.root_component),
					sk.component_cont);
			break;
		case "display":
			cs.set_collector_selected(ci.display_component_collector,sk.component_cont);
			break;
		case "visible":
			component_array comp_cont=new component_array();
			comp_cont.add_visible_component(sk.component_cont.root_component,parameter_channel_id,true);
			cs.set_component_container_selected(comp_cont,sk.component_cont);
			break;
		case "all":
			cs.set_selected_flag(sk.component_cont.root_component,sk.component_cont);
			break;
		case "latest":
			cs.set_selected_flag(sk.component_cont.search_component(),sk.component_cont);
			break;
		case "part_list":
			if((str=ci.request_response.get_parameter("list_id"))!=null)
				cs.set_collector_selected(sk.collector_stack.get_collector_by_list_id(Long.decode(str)),sk.component_cont);
			else if((str=ci.request_response.get_parameter("list_title"))!=null){
				String request_charset=ci.request_response.implementor.get_request_charset();
				try{
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e) {
					break;
				}
				cs.set_collector_selected(sk.collector_stack.get_collector_by_list_title(str),sk.component_cont);
			}else
				cs.set_collector_selected(sk.collector_stack.get_top_collector(),sk.component_cont);
			break;
		case "clear_all":
			cs.clear_selected_flag(sk.component_cont);
			break;
		case "clear_component":
		case "swap_component":
		case "select_component":
			component my_comp=null;
			String request_charset=ci.request_response.implementor.get_request_charset();
			if((component_str=ci.request_response.get_parameter("component"))!=null) {
				try{
					component_str=java.net.URLDecoder.decode(component_str,request_charset);
					component_str=java.net.URLDecoder.decode(component_str,request_charset);
				}catch(Exception e) {
					component_str=null;
				}
				if(component_str!=null)
					my_comp=sk.component_cont.search_component(component_str);
			}else if((component_str=ci.request_response.get_parameter("component_id"))!=null) {
				try{
					component_str=java.net.URLDecoder.decode(component_str,request_charset);
					component_str=java.net.URLDecoder.decode(component_str,request_charset);
				}catch(Exception e) {
					component_str=null;
				}
				if(component_str!=null)
					my_comp=sk.component_cont.get_component(Integer.decode(component_str));
			}
			if(my_comp==null)
				if((my_comp=sk.component_cont.search_component())==null)
					break;
			switch(str) {
			case "clear_component":
				cs.clear_selected_flag(my_comp,sk.component_cont);
				break;
			case "swap_component":
				cs.switch_selected_flag(my_comp,sk.component_cont);	
				break;
			case "select_component":
				cs.set_selected_flag(my_comp,sk.component_cont);
				break;
			}
			break;
		}
	}
}
