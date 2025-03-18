package driver_component_marker;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.component_instance_driver;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_scene.scene_kernel;

public class extended_component_driver  extends component_driver
{
	private String directory_comp_name,file_name;
	private component_marker_container cmc;
	private int modifier_container_id;
	private boolean global_private_flag,pickup_flag;
	
	public void destroy()
	{
		super.destroy();
		directory_comp_name=null;
		file_name=null;
		if(cmc!=null) {
			cmc.destroy();
			cmc=null;
		}
	}
	public extended_component_driver(
			part my_component_part,int my_modifier_container_id,
			boolean my_global_private_flag,boolean my_pickup_flag,
			String my_directory_comp_name,String my_file_name)
	{
		super(my_component_part);
		modifier_container_id=my_modifier_container_id;
		global_private_flag=my_global_private_flag;
		pickup_flag=my_pickup_flag;
		
		directory_comp_name=my_directory_comp_name;
		file_name=my_file_name;
		
		cmc=null;
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
		if(global_private_flag&&cmc==null)
			cmc=new component_marker_container(directory_comp_name,file_name,sk.component_cont);
		
		return new extended_component_instance_driver(comp,driver_id,
						global_private_flag?cmc:new component_marker_container(pickup_flag),
						modifier_container_id);
	}
}