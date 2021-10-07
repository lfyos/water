package driver_movement;

import kernel_part.part;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_common_class.debug_information;

public class extended_component_driver extends component_driver
{
	private movement_manager m;
	
	private String movement_file_charset;
	private String sound_pre_string;
	
	public void destroy()
	{
		super.destroy();
		
		if(m!=null) {
			m.destroy();
			m=null;
		};
		movement_file_charset=null;
		sound_pre_string=null;
	}
	public extended_component_driver(part my_component_part,
			file_reader f,String my_sound_pre_string,client_request_response request_response)
	{
		super(my_component_part);
		sound_pre_string=my_sound_pre_string;
		movement_file_charset=f.get_charset();
		m=null;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;
		
		part p=comp.driver_array[driver_id].component_part;
		file_reader fr=new file_reader(p.directory_name+p.material_file_name,p.file_charset);
		String parameter_file_name=fr.directory_name+file_reader.separator(fr.get_string());
		fr.close();
		
		fr=new file_reader(parameter_file_name,p.file_charset);
		String movement_file_name		=file_reader.separator(fr.get_string());
		String design_file_name			=file_reader.separator(fr.get_string());
		String temporary_file_directory	=file_reader.separator(fr.get_string());
		String location_component_name	=fr.get_string();
		String audio_component_name		=fr.get_string();
		String render_window_name		=fr.get_string();
		fr.close();
		
		debug_information.println("Begin loading movement information\t",movement_file_name);
		m=new movement_manager(ek,
				new movement_configuration_parameter(
						ek,comp,driver_id,movement_file_charset,
						component_directory_name+movement_file_name,
						component_directory_name+design_file_name,
						component_directory_name+temporary_file_directory,
						sound_pre_string,render_window_name,
						location_component_name,audio_component_name));
		debug_information.println("End loading movement information\t",design_file_name);
		return;
	}
	public instance_driver create_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_instance_driver(comp,driver_id,m);
	}
}