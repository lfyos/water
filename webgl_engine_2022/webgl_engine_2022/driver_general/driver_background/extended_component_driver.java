package driver_background;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.component_instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

public class extended_component_driver extends  component_driver
{
	private String directory_name;
	private int mode,user_parameter_channel_id;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_driver(part my_component_part,
			int my_mode,String my_directory_name,int my_user_parameter_channel_id)
	{
		super(my_component_part);
	
		mode=my_mode;
		directory_name=my_directory_name;
		user_parameter_channel_id=my_user_parameter_channel_id;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;

		return;
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_component_instance_driver(
				comp,driver_id,mode,directory_name,user_parameter_channel_id);
	}
}