package driver_manipulator;

import kernel_component.component;
import kernel_component.component_array;
import kernel_driver.component_driver;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class operate_show_hide_parameter
{
	private static void set_frame_and_edge_flag(component comp,
			int parameter_channel_id,long hide_code,long show_code)
	{
		if(comp.children!=null)
			for(int i=0;i<(comp.children.length);i++)
				set_frame_and_edge_flag(comp.children[i],parameter_channel_id,hide_code,show_code);
		
		long old_display_bitmap=comp.multiparameter[parameter_channel_id].display_bitmap;
		comp.multiparameter[parameter_channel_id].display_bitmap&=(~hide_code);
		comp.multiparameter[parameter_channel_id].display_bitmap|=show_code;
		if(comp.multiparameter[parameter_channel_id].display_bitmap!=old_display_bitmap)
			for(int i=0,ni=comp.driver_number();i<ni;i++){
				component_driver c_d=comp.driver_array.get(i);
				c_d.update_component_render_version();
				c_d.update_component_render_version();
			}
	}
	public static void show_hide_parameter_request(int parameter_channel_id,scene_kernel sk,client_information ci)
	{
		String str;
		long hide_code=0,show_code=0;
		
		if((str=ci.request_response.get_parameter("hide_code"))!=null)
			hide_code=Long.decode(str);		
		if((str=ci.request_response.get_parameter("show_code"))!=null)
			show_code=Long.decode(str);

		component_array comp_cont=new component_array();
		
		if((str=ci.request_response.get_parameter("component"))!=null){
			component my_comp=sk.component_cont.search_component(str);
			if(my_comp!=null)
				comp_cont.add_component(my_comp);
		}
		
		if(comp_cont.comp_list.size()<=0)
			comp_cont.add_selected_component(sk.component_cont.root_component,false);
		
		if(comp_cont.comp_list.size()<=0)
			comp_cont.add_component(sk.component_cont.root_component);
		for(int i=0,ni=comp_cont.comp_list.size();i<ni;i++)
			set_frame_and_edge_flag(comp_cont.comp_list.get(i),parameter_channel_id,hide_code,show_code);
	}
}
