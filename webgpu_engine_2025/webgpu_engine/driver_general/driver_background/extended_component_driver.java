package driver_background;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.component_instance_driver;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_scene.scene_kernel;

public class extended_component_driver  extends component_driver
{
	private int mode,user_parameter_channel_id;
	private String directory_name;
	
	public void destroy()
	{
		super.destroy();
		directory_name=null;
	}
	
	public extended_component_driver(part my_component_part,
			int my_mode,int my_user_parameter_channel_id,String my_directory_name)
	{
		super(my_component_part);
		
		mode=my_mode;
		directory_name=my_directory_name;
		user_parameter_channel_id=my_user_parameter_channel_id;
	}
	public void initialize_component_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=sk.scene_par.directory_name;
		
		return;
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_component_instance_driver(comp,driver_id,
						mode,user_parameter_channel_id,directory_name);
	}
}