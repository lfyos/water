package driver_mouse_modify_location;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.component_instance_driver;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_scene.scene_kernel;

public class extended_component_driver  extends component_driver
{
	private double view_range[],low_precision_scale,mouse_rotate_scale;
	private boolean rotate_type_flag,exchange_point_flag,change_type_flag;
	private int modifier_container_id;
	
	public void destroy()
	{
		super.destroy();
		
		view_range=null;
	}
	public extended_component_driver(part my_component_part,double my_view_range[],
			double my_low_precision_scale,double my_mouse_rotate_scale,
			boolean my_rotate_type_flag,boolean my_exchange_point_flag,boolean my_change_type_flag,
			int my_modifier_container_id)
	{
		super(my_component_part);
		
		view_range			=my_view_range;
		low_precision_scale	=my_low_precision_scale;
		mouse_rotate_scale	=my_mouse_rotate_scale;
		rotate_type_flag	=my_rotate_type_flag;
		exchange_point_flag	=my_exchange_point_flag;
		change_type_flag	=my_change_type_flag;
		modifier_container_id=my_modifier_container_id;
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
				view_range,low_precision_scale,mouse_rotate_scale,
				rotate_type_flag,exchange_point_flag,change_type_flag,modifier_container_id);
	}
}