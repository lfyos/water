package driver_territory;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class extended_instance_driver extends instance_driver
{
	private int user_parameter_channel_id[];
	
	public void destroy()
	{
		super.destroy();
		user_parameter_channel_id=null;
	}
	public extended_instance_driver(component my_comp,int my_driver_id,int my_channel_id[])
	{
		super(my_comp,my_driver_id);
		user_parameter_channel_id=my_channel_id;
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if(cr.target.selection_target_flag)
			return true;
		for(int i=0,ni=user_parameter_channel_id.length;i<ni;i++)
			if(parameter_channel_id==user_parameter_channel_id[i])
				return false;
		return true;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(comp.component_id);
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		return null;
	}
}
