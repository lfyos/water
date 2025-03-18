package driver_manipulator;

import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_array;
import kernel_driver.component_instance_driver;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class operate_lod_scale 
{
	public static void lod_scale_request(scene_kernel sk,client_information ci)
	{
		String str;
		double new_lod_scale=1.0;
		
		if((str=ci.request_response.get_parameter("value"))==null)
			return;
		if((new_lod_scale=Double.parseDouble(str))<=const_value.min_value)
			return;
		if(sk.component_cont==null)
			return;
		if(sk.component_cont.root_component==null)
			return;
		
		component_array comp_array=new component_array();
		comp_array.add_selected_component(sk.component_cont.root_component,false);
		if(comp_array.comp_list.size()<=0) {
			comp_array.add_component(sk.component_cont.root_component);
			if(comp_array.comp_list.size()<=0)
				return;
		}
		for(int my_size;(my_size=comp_array.comp_list.size())>0;){
			component comp=comp_array.comp_list.remove(my_size-1);
			for(int driver_id=0,driver_number=comp.driver_number();driver_id<driver_number;driver_id++){
				component_instance_driver in_dr=ci.component_instance_driver_cont.get_component_instance_driver(comp,driver_id);
				in_dr.instance_driver_lod_precision_scale=new_lod_scale;
			}
			for(int child_id=0,child_number=comp.children_number();child_id<child_number;child_id++)
				comp_array.add_component(comp.children[child_id]);
		}
		ci.component_instance_driver_cont.reset_precision_scale(sk.component_cont.root_component);

		return;
	}
}
