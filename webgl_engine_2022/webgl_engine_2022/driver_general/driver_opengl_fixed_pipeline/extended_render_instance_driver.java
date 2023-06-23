package driver_opengl_fixed_pipeline;

import kernel_render.render;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_engine.client_information;

import java.io.File;

import kernel_driver.render_instance_driver;

public class extended_render_instance_driver extends render_instance_driver
{
	private String shader_material_file_name,light_file_name,file_charset;
	
	public extended_render_instance_driver(
			String my_shader_material_file_name,String my_light_file_name,String my_file_charset)
	{
		super();
		shader_material_file_name	=my_shader_material_file_name;
		light_file_name				=my_light_file_name;
		file_charset				=my_file_charset;
	}
	public void destroy()
	{
		super.destroy();
	}
	public void response_init_render_data(render r,engine_kernel ek,client_information ci)
	{	
		ci.request_response.println("		{");
		ci.request_response.println(file_reader.get_text(light_file_name,file_charset));
		if(!(new File(shader_material_file_name).exists()))
			ci.request_response.println("			\"material\"	:	[]");
		else
			ci.request_response.println(file_reader.get_text(shader_material_file_name,file_charset));
		ci.request_response.println("		}");
	}
	public String[] response_render_event(render r,engine_kernel ek,client_information ci)
	{
		return super.response_render_event(r,ek,ci);
	}
}