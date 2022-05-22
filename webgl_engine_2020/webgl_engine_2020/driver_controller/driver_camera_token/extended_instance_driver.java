package driver_camera_token;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class extended_instance_driver extends instance_driver
{
	private String texture_file_name;
	public void destroy()
	{
		super.destroy();
		texture_file_name=null;
	}
	public extended_instance_driver(component my_comp,int my_driver_id,String my_texture_file_name)
	{
		super(my_comp,my_driver_id);
		texture_file_name=my_texture_file_name;
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		if(cr.target.selection_target_flag)
			return false;
		if((cr.target.framebuffer_width>0)||(cr.target.framebuffer_height>0))
			return true;
		return false;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(comp.component_id);
	}
	public String[] response_event(engine_kernel ek,client_information ci)
	{
		return new String[] {texture_file_name,ek.system_par.local_data_charset};
	}
}
