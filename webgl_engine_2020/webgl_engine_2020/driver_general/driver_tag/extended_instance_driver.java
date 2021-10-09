package driver_tag;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;


public class extended_instance_driver extends instance_driver
{
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		extended_component_driver ecd=(extended_component_driver)(comp.driver_array[driver_id]);
		
		if(ci.display_camera_result!=cr)
			if(ci.selection_camera_result!=cr)
				return true;

		for(int i=0,ni=ecd.tag.length;i<ni;i++)
			if(ecd.tag[i].is_display()) 
				ecd.tag[i].set_text(ek,ci,cr,ecd.text_component_id[i],
					ecd.canvas_width,ecd.canvas_height,ecd.view_text_height);
		return false;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		extended_component_driver ecd=(extended_component_driver)(comp.driver_array[driver_id]);
		
		ci.request_response.print("[",	data_buffer_id);
		ci.request_response.print(",[",	comp.component_id);
		ci.request_response.print(",",	ci.display_camera_result.cam.parameter.change_type_flag?1:0);
		
		for(int i=0,ni=ecd.tag.length;i<ni;i++)
			if(ecd.tag[i].is_display()){
				ci.request_response.print(",[");
				ecd.tag[i].p0.			get_point_data(ci.request_response,",");
				ecd.tag[i].right_direct.get_point_data(ci.request_response,",");
				ecd.tag[i].up_direct.	get_point_data(ci.request_response,",");
				ecd.line_color.			get_point_data(ci.request_response,",");
				ecd.point_color.		get_point_data(ci.request_response,",");
				
				ci.request_response.print(i);
				ci.request_response.print(",",(ecd.tag[i].state==3)?1:0);
				ci.request_response.print("]");
			}	
		ci.request_response.print("]]");
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(comp.component_id);
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		return ((extended_component_driver)(comp.driver_array[driver_id])).response_event(comp,parameter_channel_id,ek,ci);
	}
}
