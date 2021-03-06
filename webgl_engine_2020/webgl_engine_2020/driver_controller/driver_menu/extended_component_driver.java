package driver_menu;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

import kernel_file_manager.file_reader;

public class extended_component_driver  extends component_driver
{
	private String file_name;
	private boolean file_type;
	private double dx,dy,min_x,max_x,min_y,max_y;
	
	public void destroy()
	{
		super.destroy();
		
		file_name=null;
	}
	public extended_component_driver(part my_component_part,
			String my_file_name,boolean my_file_type,double my_dx,double my_dy,
			double my_min_x,double my_max_x,double my_min_y,double my_max_y)
	{
		super(my_component_part);
		file_name=my_file_name;
		file_type=my_file_type;
		dx=my_dx;
		dy=my_dy;
		min_x=my_min_x;
		max_x=my_max_x;
		min_y=my_min_y;
		max_y=my_max_y;
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
	public String [][]assemble_file_name_and_file_charset(file_reader fr,
			engine_kernel ek,client_request_response request_response)
	{
		return null;
	}
	public instance_driver create_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_instance_driver(comp,driver_id,
				file_name,file_type,dx,dy,min_x,max_x,min_y,max_y);
	}
}