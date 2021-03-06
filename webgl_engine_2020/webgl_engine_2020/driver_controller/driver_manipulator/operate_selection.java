package driver_manipulator;

import kernel_component.component;
import kernel_component.component_array;
import kernel_component.component_selection;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class operate_selection
{
	public static void selection_request(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		String str;
		
		if(ci.display_camera_result.target==null)
			return;
		if((str=ci.request_response.get_parameter("event_operation"))==null)
			return;
		component_selection cs=new component_selection(ek);
		
		switch(str=str.trim()) {
		case "parent":
			cs.set_parent_selected(ek.component_cont);
			break;
		case "child":
			cs.set_child_selected(ek.component_cont);
			break;
		case "move":
			component comp=ek.component_cont.search_component();
			cs.set_moved_descendant_selected(
					(comp!=null)?comp:(ek.component_cont.root_component),
					ek.component_cont);
			break;
		case "display":
			cs.set_collector_selected(ci.display_component_collector,ek.component_cont);
			break;
		case "select":
			cs.set_collector_selected(ci.selection_component_collector,ek.component_cont);
			break;
		case "visible":
			component_array comp_cont=new component_array(ek.component_cont.root_component.component_id+1);
			comp_cont.add_visible_component(ek.component_cont.root_component,parameter_channel_id,true);
			cs.set_component_container_selected(comp_cont,ek.component_cont);
			break;
		case "all":
			cs.set_selected_flag(ek.component_cont.root_component,ek.component_cont);
			break;
		case "latest":
			cs.set_selected_flag(ek.component_cont.search_component(),ek.component_cont);
			break;
		case "switch":
		case "component":
			component my_comp=null;
			boolean flag=(str.compareTo("component")==0)?true:false;
			if((str=ci.request_response.get_parameter("component"))!=null) {
				try{
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				}catch(Exception e) {
					str=null;
				}
				if(str!=null)
					my_comp=ek.component_cont.search_component(str);
			}else if((str=ci.request_response.get_parameter("component_id"))!=null) {
				try{
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				}catch(Exception e) {
					str=null;
				}
				if(str!=null)
					my_comp=ek.component_cont.get_component(Integer.decode(str));
			}
			if(flag)
				cs.set_selected_flag(my_comp,ek.component_cont);
			else
				cs.switch_selected_flag(my_comp,ek.component_cont);	
			break;
		case "part_list":
			cs.set_collector_selected(ek.collector_stack.get_top_collector(),ek.component_cont);
			break;
		case "clear":
			cs.clear_selected_flag(ek.component_cont);
			break;
		}
	}
}
