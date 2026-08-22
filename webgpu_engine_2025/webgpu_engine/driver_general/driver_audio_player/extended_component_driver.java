package driver_audio_player;

import java.io.File;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_driver.component_instance_driver;

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
		
		audio_file_name	=null;
		on_off_flag		=true;
		terminate_flag	=true;
	}
	public void initialize_component_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
//		String component_directory_name			=comp.component_directory_name;
//		String scene_directory_name				=sk.create_parameter.scene_directory_name;
//		String parameter_directory_name			=sk.scene_par.directory_name;
//		String extra_parameter_directory_name	=sk.scene_par.extra_directory_name;
		
		return;
	}
	public void create_component_driver_initialization_data(
			file_writer fw,component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_component_instance_driver(comp,driver_id);
	}
	public boolean set_audio(String my_audio_file_name,boolean force_play_audio_flag)
	{
		if(!on_off_flag)
			return true;
		
		String new_audio_file_name;
		if((new_audio_file_name=my_audio_file_name)==null){
			terminate_flag=true;
			audio_file_name=null;
			update_component_parameter_version();
			return true;
		}
		new_audio_file_name=file_directory.
				replace_directory_special_char(new_audio_file_name);
		if(audio_file_name==null){
			if(file_reader.is_exist(audio_file_name=new_audio_file_name)){
				terminate_flag=false;
				update_component_parameter_version();
			}else{
				terminate_flag=true;
				debug_information.println("audio file NOT exist:	",my_audio_file_name);
			}
			return true;
		}
		if(audio_file_name.compareTo(new_audio_file_name)==0)
			return true;
		if(!(new File(new_audio_file_name).exists())){
			debug_information.println("audio file NOT exist:	",my_audio_file_name);
			return true;
		}
		if(terminate_flag||force_play_audio_flag){
			terminate_flag=false;
			audio_file_name=new_audio_file_name;
			update_component_parameter_version();
			return true;
		}
		return false;
	}
	public String get_audio_file_name()
	{
		return on_off_flag?audio_file_name:null;
	}
	public void turn_on_off(boolean my_on_off_flag)
	{
		audio_file_name=null;
		terminate_flag=true;
		on_off_flag=my_on_off_flag;
		update_component_parameter_version();
	}
	public boolean get_on_off_flag()
	{
		return on_off_flag;
	}
	public void mark_terminate_flag()
	{
		terminate_flag=true;
	}
}