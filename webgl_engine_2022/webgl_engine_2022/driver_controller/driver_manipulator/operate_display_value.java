package driver_manipulator;

import kernel_component.component;
import kernel_component.component_array;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_driver.component_instance_driver;

public class operate_display_value 
{
	private static void set_display_value(engine_kernel ek,client_information ci,
			component comp,int new_display_value_id)
	{
		for(int i=0,ni=comp.children_number();i<ni;i++)
			set_display_value(ek,ci,comp.children[i],new_display_value_id);
		for(int i=0,ni=comp.driver_number();i<ni;i++){
			component_instance_driver in_dr=ci.component_instance_driver_cont.get_component_instance_driver(comp,i);
			in_dr.display_parameter.display_value_id=new_display_value_id;
			in_dr.update_component_parameter_version(0);
		}
	}
	
	public static void set_display_value_request(engine_kernel ek,client_information ci)
	{
		String str;
		component_array comp_cont=new component_array(ek.component_cont.root_component.component_id+1);
		
		if((str=ci.request_response.get_parameter("component"))==null){
			comp_cont.add_selected_component(ek.component_cont.root_component,false);
			if(comp_cont.comp_list.size()<=0)
				comp_cont.add_component(ek.component_cont.root_component);
		}else {
			component my_comp=ek.component_cont.search_component(str);
			if(my_comp!=null)
				comp_cont.add_component(my_comp);
		}
		if((str=ci.request_response.get_parameter("display_value"))!=null){
			int new_display_value_id=Integer.decode(str);
			for(int i=0,ni=comp_cont.comp_list.size();i<ni;i++)
				set_display_value(ek,ci,comp_cont.comp_list.get(i),new_display_value_id);
		}	
	}
}
