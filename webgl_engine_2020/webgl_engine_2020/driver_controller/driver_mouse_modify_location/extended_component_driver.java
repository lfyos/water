package driver_mouse_modify_location;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

import kernel_file_manager.file_reader;

public class extended_component_driver extends component_driver
{
	private double view_range[],low_precision_scale,mouse_rotate_scale;
	private boolean rotate_type_flag,exchange_point_flag,change_type_flag;
	private int camera_modifier_id;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_driver(
			part my_component_part,double my_view_range[],
			double my_low_precision_scale,double my_mouse_rotate_scale,
			boolean my_rotate_type_flag,boolean my_exchange_point_flag,
			boolean my_change_type_flag,int my_camera_modifier_id)
	{
		super(my_component_part);
		
		view_range			=my_view_range;
		low_precision_scale	=my_low_precision_scale;
		mouse_rotate_scale	=my_mouse_rotate_scale;
		rotate_type_flag	=my_rotate_type_flag;
		exchange_point_flag	=my_exchange_point_flag;
		change_type_flag	=my_change_type_flag;
		camera_modifier_id	=my_camera_modifier_id;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;

	}
	public String [][]assemble_file_name_and_file_charset(file_reader fr,
			engine_kernel ek,client_request_response request_response)
	{
		return null;
	}
	public instance_driver create_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_instance_driver(
				comp,driver_id,view_range,low_precision_scale,mouse_rotate_scale,
				rotate_type_flag,exchange_point_flag,change_type_flag,camera_modifier_id);
	}
}