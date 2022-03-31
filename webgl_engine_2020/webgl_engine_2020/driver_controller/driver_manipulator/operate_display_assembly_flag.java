package driver_manipulator;

import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_component.component_array;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class operate_display_assembly_flag
{
	public static void set_clear_display_assembly_flag_request(engine_kernel ek,client_information ci)
	{
		if(ek.component_cont==null)
			return;
		if(ek.component_cont.root_component==null)
			return;
		
		String str=ci.request_response.get_parameter("value");
		boolean can_display_assembly_set_flag;
		
		switch((str==null)?"":(str.toLowerCase().trim())){
		case "true":
		case "yes":
		case "on":
			can_display_assembly_set_flag=true;
			break;
		case "false":
		case "no":
		case "off":
			can_display_assembly_set_flag=false;
			break;
		default:
			return;
		}
		component_array comp_cont=new component_array(ek.component_cont.root_component.component_id+1);
		if((str=ci.request_response.get_parameter("component"))==null){
			comp_cont.add_selected_component(ek.component_cont.root_component,false);
			if(comp_cont.component_number<=0)
				comp_cont.add_component(ek.component_cont.root_component);
		}else {
			component my_comp=ek.component_cont.search_component(str);
			if(my_comp!=null)
				comp_cont.add_component(my_comp);
		}
		int number=0;
		for(component comp;comp_cont.component_number>0;number++){
			comp=comp_cont.comp[--(comp_cont.component_number)];
			comp_cont.comp[comp_cont.component_number]=null;
			for(int child_id=0,child_number=comp.children_number();child_id<child_number;child_id++)
				comp_cont.add_component(comp.children[child_id]);
			comp.can_display_assembly_set_flag=can_display_assembly_set_flag;
			ek.mark_reset_flag();
		}
		
		debug_information.println("set_clear_display_assembly_flag:	",
				 number+" components "+(can_display_assembly_set_flag?"set":"clear"));
		return;
	}
}
