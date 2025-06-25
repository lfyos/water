package driver_manipulator;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_scene.client_information;
import kernel_component.component_array;
import kernel_component.component_container;

public class operate_show_hide_component
{
	private static void show_hide(component comp,int parameter_channel_id,
			boolean visible_flag,component_container component_cont)
	{
		comp.modify_display_flag(
				new int []{parameter_channel_id},new boolean[] {visible_flag},component_cont);
		for(int i=0,child_number=comp.children.size();i<child_number;i++)
			show_hide(comp.children.get(i),parameter_channel_id,visible_flag,component_cont);
	}
	
	public static void show_hide_component_request(int parameter_channel_id,scene_kernel sk,client_information ci)
	{
		String str;
		
		if((str=ci.request_response.get_parameter("event_operation"))==null)
			return;
		
		boolean visible_flag;
		switch(str) {
		case "show":
		case "true":
		case "yes":
		case "on":
			visible_flag=true;
			break;
		case "hide":
		case "false":
		case "no":
		case "off":
			visible_flag=false;
			break;
		default:
			return;
		}
		component_array comp_cont=new component_array();
		boolean selected_operation_flag;
		if((str=ci.request_response.get_parameter("component_name"))!=null) {
			String request_charset=ci.request_response.implementor.get_request_charset();
			try {
				str=java.net.URLDecoder.decode(str,request_charset);
				str=java.net.URLDecoder.decode(str,request_charset);
			}catch(Exception e) {
				;
			}
			component my_comp;
			if((my_comp=sk.component_cont.search_component(str))==null)
				return;
			comp_cont.add_component(my_comp);
			selected_operation_flag=false;
		}else if((str=ci.request_response.get_parameter("component_id"))!=null) {
			int component_id;
			try {
				component_id=Integer.decode(str);
			}catch(Exception e) {
				return;
			}
			component my_comp;
			if((my_comp=sk.component_cont.get_component(component_id))==null)
				return;
			comp_cont.add_component(my_comp);
			selected_operation_flag=false;
		}else{
			comp_cont.add_selected_component(sk.component_cont.root_component,false);
			selected_operation_flag=true;
		}
		if(comp_cont.comp_list.size()>0)
			for(int i=0,ni=comp_cont.comp_list.size();i<ni;i++)
				show_hide(comp_cont.comp_list.get(i),
						parameter_channel_id,visible_flag,sk.component_cont);
		else if(visible_flag&&selected_operation_flag)
				show_hide(sk.component_cont.root_component,
						parameter_channel_id,visible_flag,sk.component_cont);
		return;
	}
}
