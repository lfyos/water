package driver_movement;


import kernel_component.component;
import kernel_driver.component_driver;
import kernel_scene.scene_kernel;
import kernel_common_class.debug_information;

public class movement_configuration_parameter
{
	public String movement_file_charset,movement_file_name,design_file_name;
	public String temporary_file_directory,sound_pre_string;
	public int component_id,location_component_id,audio_component_id;
	public int movement_modifier_container_id,camera_modifier_container_id;
	public int virtual_mount_root_component_id,mouse_modify_location_component_id; 

	public void destroy()
	{
		movement_file_charset=null;
		movement_file_name=null;
		design_file_name=null;
		temporary_file_directory=null;
		sound_pre_string=null;
	}
	public driver_audio_player.extended_component_driver get_audio_component_driver(scene_kernel sk)
	{
		component my_comp=sk.component_cont.get_component(audio_component_id);
		if(my_comp!=null)
			if(my_comp.driver_number()>0) {
				component_driver c_d=my_comp.driver_array.get(0);
				if(c_d instanceof driver_audio_player.extended_component_driver)
					return (driver_audio_player.extended_component_driver)c_d;
			}
		return null;
	}
	public movement_configuration_parameter(scene_kernel sk,component my_comp,int my_driver_id,
			String my_movement_file_charset,String my_movement_file_name,
			String my_design_file_name,String my_temporary_file_directory,String my_sound_pre_string,
			String location_component_name,String audio_component_name,
			int my_movement_modifier_container_id,int my_camera_modifier_container_id,
			String mouse_modify_location_component_name,String virtual_mount_root_component_name)
	{
		movement_file_charset=my_movement_file_charset;
		movement_file_name=my_movement_file_name;
		design_file_name=my_design_file_name;
		temporary_file_directory=my_temporary_file_directory;
		sound_pre_string=my_sound_pre_string;
		movement_modifier_container_id=my_movement_modifier_container_id;
		camera_modifier_container_id=my_camera_modifier_container_id;
	
		component_id=my_comp.component_id;
		
		location_component_id=-1;
		if((my_comp=sk.component_cont.search_component(location_component_name))!=null)
			location_component_id=my_comp.component_id;
		else
			debug_information.println("location component not exist: ",location_component_name);

		audio_component_id=-1;
		if((my_comp=sk.component_cont.search_component(audio_component_name))!=null)
			if(my_comp.driver_number()>0)
				if(my_comp.driver_array.get(0) instanceof driver_audio_player.extended_component_driver)
					audio_component_id=my_comp.component_id;
		if(audio_component_id<0)
			debug_information.println("audio component not exist: ",audio_component_name);
		
		mouse_modify_location_component_id=-1;
		if((my_comp=sk.component_cont.search_component(mouse_modify_location_component_name))!=null)
			mouse_modify_location_component_id=my_comp.component_id;
		else
			debug_information.println("mouse modify location component not exist: ",mouse_modify_location_component_name);
		
		virtual_mount_root_component_id=-1;
		if((my_comp=sk.component_cont.search_component(virtual_mount_root_component_name))!=null)
			virtual_mount_root_component_id=my_comp.component_id;
		else
			debug_information.println("virtual mount_root component not exist: ",virtual_mount_root_component_name);
	}
}
