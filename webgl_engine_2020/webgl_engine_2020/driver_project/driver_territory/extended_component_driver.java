package driver_territory;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

import kernel_file_manager.file_reader;

public class extended_component_driver  extends  component_driver
{
	private int user_parameter_channel_id[];
	
	public void destroy()
	{
		super.destroy();
		user_parameter_channel_id=null;
	}
	public extended_component_driver(part component_part,int my_channel_id[])
	{
		super(component_part);
		user_parameter_channel_id=my_channel_id;
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
		return new extended_instance_driver(comp,driver_id,user_parameter_channel_id);
	}
}