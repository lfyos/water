package driver_caption;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

import kernel_file_manager.file_reader;

public class extended_component_driver extends component_driver
{
	public String text_component_name;
	public int canvas_width,canvas_height;
	public double text_square_width,text_square_height;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_driver(part my_component_part,
			String my_text_component_name,int my_canvas_width,int my_canvas_height,
			double my_text_square_width,double my_text_square_height)
	{
		super(my_component_part);
		
		text_component_name=my_text_component_name;
		
		canvas_width		=my_canvas_width;
		canvas_height		=my_canvas_height;
		text_square_width	=my_text_square_width;
		text_square_height	=my_text_square_height;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;
		
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
		component text_comp=ek.component_cont.search_component(text_component_name);
		return new extended_instance_driver(comp,driver_id,
			(text_comp==null)?-1:text_comp.component_id);
	}
}