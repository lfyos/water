package driver_water;


import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_file_manager.file_reader;

public class extended_component_driver extends  component_driver{
	private double height,amplitude,wavelength,attenuation,left,right,down,up;
	private int user_parameter_channel_id[];
	private int texture_width,texture_height;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_driver(part component_part,file_reader fr,int my_user_parameter_channel_id[])
	{
		super(component_part);
		
		height=fr.get_double();
		amplitude=fr.get_double();
		wavelength=fr.get_double();
		attenuation=fr.get_double();
		
		left=fr.get_double();
		right=fr.get_double();
		down=fr.get_double();
		up=fr.get_double();
		
		texture_width=fr.get_int();
		texture_height=fr.get_int();

		user_parameter_channel_id=my_user_parameter_channel_id;

		return;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;
		
		return;
	}
	public instance_driver create_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_instance_driver(comp,driver_id,
				height,amplitude,wavelength,attenuation,
				left,right,down,up,user_parameter_channel_id,texture_width,texture_height);
	}
}