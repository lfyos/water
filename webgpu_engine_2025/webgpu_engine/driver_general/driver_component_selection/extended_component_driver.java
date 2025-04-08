package driver_component_selection;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.component_instance_driver;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_scene.scene_kernel;

public class extended_component_driver  extends component_driver
{
	private String screen_rectangle_component_name,audio_component;
	private int modifier_container_id;
	
	public void destroy()
	{
		super.destroy();

		audio_component=null;
		screen_rectangle_component_name=null;
	}
	public extended_component_driver(part my_component_part,
			String my_screen_rectangle_component_name,
			String my_audio_component,int my_modifier_container_id)
	{
		super(my_component_part);
		
		screen_rectangle_component_name	=my_screen_rectangle_component_name;
		audio_component					=my_audio_component;
		modifier_container_id			=my_modifier_container_id;
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
		int screen_rectangle_component_id=-1,audio_component_id=-1;
		component my_comp;
		if((my_comp=sk.component_cont.search_component(screen_rectangle_component_name))!=null)
			screen_rectangle_component_id=my_comp.component_id;
		if((my_comp=sk.component_cont.search_component(audio_component))!=null)
			if(my_comp.driver_number()>0)
				if(my_comp.driver_array.get(0) instanceof driver_audio_player.extended_component_driver)
					audio_component_id=my_comp.component_id;
		
		return new extended_component_instance_driver(comp,driver_id,
				screen_rectangle_component_id,audio_component_id,modifier_container_id);
	}
}