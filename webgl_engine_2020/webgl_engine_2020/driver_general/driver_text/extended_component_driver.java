package driver_text;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

import kernel_file_manager.file_reader;

public class extended_component_driver extends component_driver
{
	private text_item dt;
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_driver(part component_part)
	{
		super(component_part);
		dt=new text_item();
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
		return new extended_instance_driver(comp,driver_id);
	}
	public text_item get_text_item()
	{
		return dt;
	};
	public void set_text(text_item new_dt)
	{
		dt=new_dt;
		update_component_parameter_version();
		update_component_render_version();
	}
	public void clear_text()
	{
		dt.display_information=null;
		update_component_parameter_version();
		update_component_render_version();
	}
}