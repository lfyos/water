package driver_manipulator;

import kernel_common_class.change_name;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

import kernel_file_manager.file_reader;

public class extended_component_driver  extends component_driver
{
	private change_name language_change_name;
	private String audio_component_name;
	private int camera_modifier_id;
	private long touch_time_length;
	private boolean save_component_name_or_id_flag;
	
	public void destroy()
	{
		super.destroy();
		language_change_name=null;
	}
	public extended_component_driver(part my_component_part,
			change_name my_language_change_name,String my_audio_component_name,
			int my_camera_modifier_id,long my_touch_time_length,
			boolean my_save_component_name_or_id_flag)
	{
		super(my_component_part);
		language_change_name=my_language_change_name;
		audio_component_name=my_audio_component_name;
		camera_modifier_id=my_camera_modifier_id;
		touch_time_length=my_touch_time_length;
		save_component_name_or_id_flag=my_save_component_name_or_id_flag;
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
		int audio_component_id=-1;
		component audio_comp=ek.component_cont.search_component(audio_component_name);
		if(audio_comp!=null)
			if(audio_comp.driver_number()>0)
				if(audio_comp.driver_array[0] instanceof driver_audio.extended_component_driver)
					audio_component_id=audio_comp.component_id;
		
		return new extended_instance_driver(
				comp,driver_id,language_change_name,audio_component_id,
				camera_modifier_id,touch_time_length,save_component_name_or_id_flag);
	}
}