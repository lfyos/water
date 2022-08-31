package driver_coordinate;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

public class extended_component_driver extends component_driver
{
	private boolean abandon_camera_display_flag,abandon_selected_display_flag;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_driver(part component_part,
			boolean my_abandon_camera_display_flag,
			boolean my_abandon_selected_display_flag)
	{
		super(component_part);
		abandon_camera_display_flag=my_abandon_camera_display_flag;
		abandon_selected_display_flag=my_abandon_selected_display_flag;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;

		return;
	}
	public instance_driver create_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_instance_driver(
				comp,driver_id,abandon_camera_display_flag,abandon_selected_display_flag);
	}
}