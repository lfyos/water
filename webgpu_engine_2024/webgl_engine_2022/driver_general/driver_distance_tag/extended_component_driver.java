package driver_distance_tag;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.component_instance_driver;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_scene.scene_kernel;

public class extended_component_driver  extends component_driver
{
	private distance_tag_array tag_array;
	
	public void destroy()
	{
		super.destroy();

		if(tag_array!=null) {
			tag_array.destroy();
			tag_array=null;
		}
	}
	public extended_component_driver(part my_component_part,file_reader fr)
	{
		super(my_component_part);
		tag_array=new distance_tag_array(
			fr.get_string(),	fr.get_string(),	fr.get_string(),
			fr.get_int(),		fr.get_double(),	fr.get_int());
	}
	public void initialize_component_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=sk.scene_par.directory_name;

		tag_array.load(sk);
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_component_instance_driver(comp,driver_id,tag_array);
	}
}