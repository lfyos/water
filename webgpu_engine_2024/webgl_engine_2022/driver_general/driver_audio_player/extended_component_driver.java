package driver_audio_player;

import kernel_part.part;
import kernel_component.component;
import kernel_engine.engine_kernel;
import kernel_driver.component_driver;
import kernel_file_manager.file_reader;
import kernel_driver.component_instance_driver;
import kernel_network.client_request_response;

public class extended_component_driver  extends component_driver
{
	private String audio_file_name;
	private boolean on_off_flag,terminate_flag;
	public void destroy()
	{
		super.destroy();
		audio_file_name=null;
	}
	public extended_component_driver(part my_component_part)
	{
		super(my_component_part);
		audio_file_name=null;
		on_off_flag=true;
		terminate_flag=true;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;
		
		return;
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_component_instance_driver(comp,driver_id);
	}
	public void mark_terminate_flag()
	{
		terminate_flag=true;
	}
	public boolean get_terminate_flag()
	{
		return terminate_flag;
	}
	public void set_audio(String new_audio_file_name)
	{
		if(new_audio_file_name!=null)
			new_audio_file_name=file_reader.separator(new_audio_file_name);

		if(audio_file_name==null){
			if(new_audio_file_name==null)
				return;
			if(file_reader.is_exist(new_audio_file_name)){
				audio_file_name=new_audio_file_name;
				terminate_flag=false;
				update_component_parameter_version();
			}
			return;
		}
		if(new_audio_file_name==null){
			audio_file_name=new_audio_file_name;
			update_component_parameter_version();
			return;
		}
		if(audio_file_name.compareTo(new_audio_file_name)==0)
			return;
		if(file_reader.is_exist(new_audio_file_name)){
			audio_file_name=new_audio_file_name;
			terminate_flag=false;
			update_component_parameter_version();
		}
	}
	public String get_audio_file_name()
	{
		if(on_off_flag)
			if(file_reader.is_exist(audio_file_name))
				return audio_file_name;
		return null;
	}
	public void turn_on_off(boolean my_on_off_flag)
	{
		on_off_flag=my_on_off_flag;
		update_component_parameter_version();
	}
	public boolean get_state()
	{
		return on_off_flag;
	}
}