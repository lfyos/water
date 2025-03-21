package driver_movement;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_camera.camera_parameter;
import kernel_driver.component_driver;
import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_driver.component_instance_driver;

public class extended_component_driver  extends component_driver
{
	public movement_manager m;
	private String sound_pre_string;
	
	public void destroy()
	{
		super.destroy();
		
		if(m!=null) {
			m.destroy();
			m=null;
		};
		sound_pre_string=null;
	}
	public extended_component_driver(part my_component_part,
			file_reader f,String my_sound_pre_string,client_request_response request_response)
	{
		super(my_component_part);
		sound_pre_string=my_sound_pre_string;
		m=null;
	}
	public void initialize_component_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=sk.scene_par.directory_name;
		
		component scene_comp=sk.component_cont.scene_component;
		String movement_directory_name=((scene_comp==null)?comp:scene_comp).component_directory_name;
		
		part p=comp.driver_array.get(driver_id).component_part;
		file_reader fr=new file_reader(p.directory_name+p.material_file_name,p.file_charset);
		String movement_file_name					=file_reader.separator(fr.get_string());
		String design_file_name						=file_reader.separator(fr.get_string());
		String temporary_file_directory				=file_reader.separator(fr.get_string());
		String location_component_name				=fr.get_string();
		String audio_component_name					=fr.get_string();
		int movement_modifier_container_id			=fr.get_int();
		int camera_modifier_container_id			=fr.get_int();
		String mouse_modify_location_component_name	=fr.get_string();
		String virtual_mount_root_component_name	=fr.get_string();
	
		movement_channel_id move_channel_id			=new movement_channel_id(fr);
		
		fr.close();
		
		debug_information.println("Begin loading movement information\t",movement_directory_name+movement_file_name);
		camera_parameter cam_par=sk.camera_cont.get(0).parameter;
		m=new movement_manager(sk,cam_par.movement_flag?cam_par.switch_time_length:0,
				new movement_configuration_parameter(
						sk,comp,driver_id,comp.component_charset,
						movement_directory_name+movement_file_name,
						movement_directory_name+design_file_name,
						movement_directory_name+temporary_file_directory,
						sound_pre_string,
						location_component_name,audio_component_name,
						movement_modifier_container_id,camera_modifier_container_id,
						mouse_modify_location_component_name,virtual_mount_root_component_name),
				move_channel_id);
		debug_information.println("End loading movement information\t",movement_directory_name+design_file_name);

		return;
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_component_instance_driver(comp,driver_id);
	}
}