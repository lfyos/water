package driver_interface;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.component_instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

public class extended_component_driver  extends component_driver
{
	private boolean menu_type,always_show_flag;
	private double dx,dy,depth;
	private String file_name,file_charset;

	public void destroy()
	{
		super.destroy();
		file_name=null;
		file_charset=null;
	}
	public extended_component_driver(part my_component_part,
			boolean my_menu_type,double my_depth,double my_dx,double my_dy,
			String my_file_name,String my_file_charset,boolean my_always_show_flag)
	{
		super(my_component_part);

		menu_type=my_menu_type;
		dx=my_dx;
		dy=my_dy;
		depth=my_depth;
		file_name=my_file_name;
		file_charset=my_file_charset;
		always_show_flag=my_always_show_flag;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;
		
		comp.uniparameter.display_part_name_or_component_name_flag=false;
		
		return;
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_component_instance_driver(comp,driver_id,
				menu_type,depth,dx,dy,file_name,file_charset,always_show_flag);
	}
}