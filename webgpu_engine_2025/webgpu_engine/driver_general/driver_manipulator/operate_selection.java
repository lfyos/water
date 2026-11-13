package driver_manipulator;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_scene.client_information;
import kernel_component.component_array;
import kernel_component.component_selection;

public class operate_selection
{
	public static void selection_request(int parameter_channel_id,scene_kernel sk,client_information ci)
	{
		String str,component_str;
		component my_comp=null;
		
		if((str=ci.request_response.get_parameter("event_operation"))==null)
			return;
		component_selection comp_select=new component_selection(sk);
		
		switch(str=str.trim()) {
		case "parent":
			my_comp=sk.component_cont.get_latest_selected_component();
			comp_select.set_parent_selected((my_comp!=null)?my_comp:(sk.component_cont.root_component));
			break;
		case "child":
			my_comp=sk.component_cont.get_latest_selected_component();
			comp_select.set_child_selected((my_comp!=null)?my_comp:(sk.component_cont.root_component));
			break;
		case "move":
			my_comp=sk.component_cont.get_latest_selected_component();
			comp_select.set_moved_descendant_selected((my_comp!=null)?my_comp:(sk.component_cont.root_component));
			break;
		case "display":
			comp_select.set_collector_selected(ci.display_component_collector);
			break;
		case "visible":
			component_array comp_cont=new component_array();
			comp_cont.add_visible_component(sk.component_cont.root_component,parameter_channel_id,true);
			comp_select.set_component_container_selected(comp_cont);
			break;
		case "all":
			comp_select.set_selected_flag(sk.component_cont.root_component);
			break;
		case "latest":
			comp_select.set_selected_flag(sk.component_cont.get_latest_selected_component());
			break;
		case "part_list":
			if((str=ci.request_response.get_parameter("list_id"))!=null)
				comp_select.set_collector_selected(sk.collector_stack.get_collector_by_list_id(Long.decode(str)));
			else if((str=ci.request_response.get_parameter("list_title"))!=null){
				String request_charset=ci.request_response.implementor.get_request_charset();
				try{
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e) {
					break;
				}
				comp_select.set_collector_selected(sk.collector_stack.get_collector_by_list_title(str));
			}else
				comp_select.set_collector_selected(sk.collector_stack.get_top_collector());
			break;
		case "clear_all":
			comp_select.clear_selected_flag(sk.component_cont.root_component);
			break;
		case "clear_component":
		case "swap_component":
		case "select_component":
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
				if((my_comp=sk.component_cont.get_latest_selected_component())==null)
					break;
			switch(str) {
			case "clear_component":
				comp_select.clear_selected_flag(my_comp);
				break;
			case "swap_component":
				comp_select.switch_selected_flag(my_comp);	
				break;
			case "select_component":
				comp_select.set_selected_flag(my_comp);
				break;
			}
			break;
		}
	}
}
