package driver_manipulator;

import kernel_common_class.const_value;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_component.component;
import kernel_component.component_array;
import kernel_driver.instance_driver;

public class operate_lod_scale 
{
	public static void lod_scale_request(engine_kernel ek,client_information ci)
	{
		String str;
		component comp;
		double new_lod_scale=1.0;
		
		if((str=ci.request_response.get_parameter("value"))==null)
			return;
		if((new_lod_scale=Double.parseDouble(str))<=const_value.min_value)
			return;
		if(ek.component_cont==null)
			return;
		if((comp=ek.component_cont.root_component)==null)
			return;
		
		component_array comp_array=new component_array(comp.component_id+1);
		comp_array.add_selected_component(ek.component_cont.root_component);
		
		while(comp_array.component_number>0){
			comp=comp_array.comp[--(comp_array.component_number)];
			comp_array.comp[comp_array.component_number]=null;
			
			for(int driver_id=0,driver_number=comp.driver_number();driver_id<driver_number;driver_id++){
				instance_driver in_dr=ci.instance_container.get_driver(comp,driver_id);
				in_dr.instance_driver_lod_precision_scale=new_lod_scale;
			}

			for(int child_id=0,child_number=comp.children_number();child_id<child_number;child_id++)
				comp_array.add_component(comp.children[child_id]);
		}
		ci.instance_container.reset_precision_scale(ek.component_cont.root_component);

		return;
	}
}
