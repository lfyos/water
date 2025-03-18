package driver_show_target;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.component_instance_driver;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_scene.scene_kernel;

public class extended_component_driver  extends component_driver
{
	private double position[];
	public void destroy()
	{
		super.destroy();
		position=null;
	}
	public extended_component_driver(part my_component_part,double my_position[])
	{
		super(my_component_part);
		position=my_position;
	}
	public void initialize_component_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=sk.scene_par.directory_name;
		
		comp.uniparameter.display_part_name_or_component_name_flag=false;
		
		return;
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_component_instance_driver(comp,driver_id,position);
	}
}