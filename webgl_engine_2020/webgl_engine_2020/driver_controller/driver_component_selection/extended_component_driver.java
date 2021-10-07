package driver_component_selection;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

public class extended_component_driver extends component_driver
{
	private String screen_rectangle_component_name,audio_component;
	private int camera_modifier_id;

	public void destroy()
	{
		super.destroy();
	}
	public extended_component_driver(part my_component_part,
			String my_screen_rectangle_component_name,String my_audio_component,int my_camera_modifier_id)
	{
		super(my_component_part);
		screen_rectangle_component_name=my_screen_rectangle_component_name;
		audio_component=my_audio_component;
		camera_modifier_id=my_camera_modifier_id;
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
		int screen_rectangle_component_id=-1,audio_component_id=-1;
		component my_comp;
		if((my_comp=ek.component_cont.search_component(screen_rectangle_component_name))!=null)
			screen_rectangle_component_id=my_comp.component_id;
		if((my_comp=ek.component_cont.search_component(audio_component))!=null)
			if(my_comp.driver_number()>0)
				if(my_comp.driver_array[0] instanceof driver_audio.extended_component_driver)
					audio_component_id=my_comp.component_id;
		
		return new extended_instance_driver(comp,driver_id,
				screen_rectangle_component_id,audio_component_id,camera_modifier_id);
	}
}