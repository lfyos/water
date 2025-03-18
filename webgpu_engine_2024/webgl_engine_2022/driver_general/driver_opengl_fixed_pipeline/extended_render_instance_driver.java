package driver_opengl_fixed_pipeline;

import kernel_render.render;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_file_manager.file_reader;
import kernel_driver.render_instance_driver;

public class extended_render_instance_driver extends render_instance_driver
{
	private String light_file_name,file_charset;

	public extended_render_instance_driver(String my_light_file_name,String my_file_charset)
	{
		super();
		
		file_charset				=my_file_charset;
		light_file_name				=my_light_file_name;
	}
	public void destroy()
	{
		super.destroy();
		
		file_charset				=null;
		light_file_name				=null;
	}
	
	public void response_init_render_data(render r,scene_kernel sk,client_information ci)
	{
		file_reader.get_text(ci.request_response,light_file_name,file_charset);
	}
	public String[] response_render_event(render r,scene_kernel sk,client_information ci)
	{
		return super.response_render_event(r,sk,ci);
	}
}