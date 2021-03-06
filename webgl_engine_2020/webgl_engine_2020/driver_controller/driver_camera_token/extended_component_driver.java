package driver_camera_token;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

import kernel_file_manager.file_reader;

public class extended_component_driver  extends component_driver
{
	private String texture_file_name;
	
	public void destroy()
	{
		super.destroy();
		texture_file_name=null;
	}
	public extended_component_driver(part my_component_part,String my_texture_file_name)
	{
		super(my_component_part);
		texture_file_name=my_texture_file_name;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;
		
		return;
	}
	public String [][]assemble_file_name_and_file_charset(file_reader fr,
			engine_kernel ek,client_request_response request_response)
	{
		return null;
	}
	public instance_driver create_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		comp.uniparameter.display_part_name_or_component_name_flag=false;
		return new extended_instance_driver(comp,driver_id,texture_file_name);
	}
}