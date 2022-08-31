package driver_manipulator;

import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_component.component_array;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class operate_fix_render_driver_id 
{
	public static void fix_render_driver_id_request(engine_kernel ek,client_information ci)
	{
		String str;
		
		if((str=ci.request_response.get_parameter("driver_id"))==null)
			return;
		int fix_render_driver_id=Integer.decode(str);
		if(ek.component_cont==null)
			return;
		if(ek.component_cont.root_component==null)
			return;
		
		component_array comp_cont=new component_array(ek.component_cont.root_component.component_id+1);
		if((str=ci.request_response.get_parameter("component"))==null){
			comp_cont.add_selected_component(ek.component_cont.root_component,false);
			if(comp_cont.component_number<=0)
				comp_cont.add_component(ek.component_cont.root_component);
		}else {
			component comp;
			if((comp=ek.component_cont.search_component(str))==null)
				return;
			comp_cont.add_component(comp);
		}
		for(component comp;comp_cont.component_number>0;){
			comp=comp_cont.comp[--(comp_cont.component_number)];
			comp_cont.comp[comp_cont.component_number]=null;
			for(int child_id=0,child_number=comp.children_number();child_id<child_number;child_id++)
				comp_cont.add_component(comp.children[child_id]);
			if((comp.fix_render_driver_id=fix_render_driver_id)>=0)
				if(fix_render_driver_id>=comp.driver_number())
					comp.fix_render_driver_id=comp.driver_number()-1;
		}
		
		debug_information.println("fix_render_driver_id:	",fix_render_driver_id);
		
		return;
	}
}
