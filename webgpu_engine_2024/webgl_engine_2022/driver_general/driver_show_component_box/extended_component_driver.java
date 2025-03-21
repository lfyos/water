package driver_show_component_box;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.component_instance_driver;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_scene.scene_kernel;

public class extended_component_driver  extends component_driver
{
	private boolean show_type_flag;
	private long time_length;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_driver(part my_component_part,
			boolean my_show_type_flag,long my_time_length)
	{
		super(my_component_part);
		show_type_flag=my_show_type_flag;
		time_length=my_time_length;
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
		return new extended_component_instance_driver(comp,driver_id,show_type_flag,time_length);
	}
}