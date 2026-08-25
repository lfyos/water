package driver_background;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;
import kernel_driver.component_instance_driver;

public class extended_component_driver  extends component_driver
{
	private int mode;
	private String directory_name;
	
	public void destroy()
	{
		super.destroy();
		directory_name=null;
	}
	
	public extended_component_driver(part my_component_part,int my_mode,String my_directory_name)
	{
		super(my_component_part);
		
		mode=my_mode;
		directory_name=my_directory_name;
	}
	public void initialize_component_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
//		String component_directory_name			=comp.component_directory_name;
//		String scene_directory_name				=sk.create_parameter.scene_directory_name;
//		String parameter_directory_name			=sk.scene_par.directory_name;
//		String extra_parameter_directory_name	=sk.scene_par.extra_directory_name;
		
		return;
	}
	public void create_component_driver_initialization_data(
			file_writer fw,component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_component_instance_driver(comp,driver_id,mode,directory_name);
	}
}