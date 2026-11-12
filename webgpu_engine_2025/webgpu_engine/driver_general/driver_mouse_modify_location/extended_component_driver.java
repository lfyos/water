package driver_mouse_modify_location;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_common_class.jason_string;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_network.client_request_response;
import kernel_driver.component_instance_driver;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class extended_component_driver  extends component_driver
{
	private double view_range[],low_precision_scale,mouse_rotate_scale;
	private boolean rotate_type_flag,exchange_point_flag;
	private int modifier_container_id;
	private String mark_component_name,movement_component_name;
	private String movement_abstract_menu_component_name;
	
	public void destroy()
	{
		super.destroy();
		
		view_range=null;
	}
	public extended_component_driver(part my_component_part,file_reader fr)
	{
		super(my_component_part);
		
		view_range=new double[]{fr.get_double(),fr.get_double(),fr.get_double(),fr.get_double()};
		low_precision_scale		=fr.get_double();
		mouse_rotate_scale		=fr.get_double();
		rotate_type_flag		=fr.get_boolean();
		exchange_point_flag		=fr.get_boolean();
		modifier_container_id	=fr.get_int();
		
		mark_component_name		=fr.get_string();
		movement_component_name	=fr.get_string();
		movement_abstract_menu_component_name=fr.get_string();
	}
	public void initialize_component_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
//		String component_directory_name			=comp.component_directory_name;
//		String scene_directory_name				=sk.create_parameter.scene_directory_name;
//		String parameter_directory_name			=sk.scene_par.directory_name;
//		String extra_parameter_directory_name	=sk.scene_par.extra_directory_name;
		
		return;
	}
	public void create_component_driver_initialization_data(
			file_writer fw,component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
		fw.print("[");
		
		fw.print(		low_precision_scale);
		fw.print(",",	mouse_rotate_scale	);
		fw.print(",",	rotate_type_flag	?"1":"0");
		fw.print(",",	exchange_point_flag?"1":"0");

		fw.print(",",	jason_string.change_string(mark_component_name));
		fw.print(",",	jason_string.change_string(movement_component_name));
		fw.print(",",	jason_string.change_string(movement_abstract_menu_component_name));

		fw.println("]");
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_component_instance_driver(comp,driver_id,view_range,modifier_container_id);
	}
}