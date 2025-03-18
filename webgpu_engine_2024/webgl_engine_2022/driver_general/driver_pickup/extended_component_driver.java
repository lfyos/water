package driver_pickup;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.component_instance_driver;
import kernel_network.client_request_response;

public class extended_component_driver  extends component_driver
{
	private String pickup_target_name;
	
	public void destroy()
	{
		super.destroy();
		pickup_target_name=null;
	}
	public extended_component_driver(part my_component_part,String my_pickup_target_name)
	{
		super(my_component_part);
		pickup_target_name=my_pickup_target_name;
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
		return new extended_component_instance_driver(comp,driver_id,pickup_target_name);
	}
}