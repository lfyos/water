package driver_distance_tag;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;
import kernel_driver.component_instance_driver;

public class extended_component_driver  extends component_driver
{
	private String tag_root_menu_component_name;
	private distance_tag_list tag_list;
	
	public void destroy()
	{
		super.destroy();

		tag_root_menu_component_name=null;
		
		if(tag_list!=null) {
			tag_list.destroy();
			tag_list=null;
		}
	}
	public extended_component_driver(part my_component_part,file_reader fr)
	{
		super(my_component_part);
		tag_root_menu_component_name=fr.get_string();	
		tag_list=new distance_tag_list(
			fr.get_string(),	fr.get_string(),
			fr.get_int(),		fr.get_double(),	fr.get_int());
	}
	public void initialize_component_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
//		String component_directory_name			=comp.component_directory_name;
//		String scene_directory_name				=sk.create_parameter.scene_directory_name;
//		String parameter_directory_name			=sk.scene_par.directory_name;
//		String extra_parameter_directory_name	=sk.scene_par.extra_directory_name;

		tag_list.load(sk);
	}
	public void create_component_driver_initialization_data(
			file_writer fw,component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_component_instance_driver(
				comp,driver_id,tag_root_menu_component_name,tag_list);
	}
}