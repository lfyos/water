package driver_ruler;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

public class extended_component_driver  extends component_driver
{
	private double region_data[];
	public void destroy()
	{
		super.destroy();
		region_data=null;
	}
	public extended_component_driver(part my_component_part,double my_region_data[])
	{
		super(my_component_part);
		region_data=my_region_data;
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
		return new extended_instance_driver(comp,driver_id,region_data);
	}
}