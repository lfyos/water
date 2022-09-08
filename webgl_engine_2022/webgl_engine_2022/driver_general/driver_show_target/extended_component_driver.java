package driver_show_target;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.component_instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

public class extended_component_driver  extends component_driver
{
	private String target_name;
	private int texture_id;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_driver(part my_component_part,String my_target_name,int my_texture_id)
	{
		super(my_component_part);
		target_name=my_target_name;
		texture_id=my_texture_id;
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
		return new extended_component_instance_driver(comp,driver_id,target_name,texture_id);
	}
}