package driver_movement;

import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_engine.engine_kernel;
import kernel_part.part;
import kernel_component.component;
import kernel_file_manager.file_reader;

public class movement_configuration_parameter
{
	public String movement_file_charset,movement_file_name,design_file_name;
	public String temporary_file_directory,sound_pre_string,render_window_name;
	
	public change_name language_change_name;
	
	public int component_id,location_component_id,audio_component_id; 

	public void destroy()
	{
		movement_file_charset=null;
		movement_file_name=null;
		design_file_name=null;
		temporary_file_directory=null;
		sound_pre_string=null;
		render_window_name=null;
		
		if(language_change_name!=null) {
			language_change_name.destroy();
			language_change_name=null;
		}
	}
	public driver_audio.extended_component_driver get_audio_component_driver(engine_kernel ek)
	{
		component my_comp=ek.component_cont.get_component(audio_component_id);
		if(my_comp!=null)
			if(my_comp.driver_number()>0)
				if(my_comp.driver_array[0] instanceof driver_audio.extended_component_driver)
					return (driver_audio.extended_component_driver)(my_comp.driver_array[0]);
		return null;
	}
	public movement_configuration_parameter(engine_kernel ek,component my_comp,int my_driver_id,
			String my_movement_file_charset,String my_movement_file_name,
			String my_design_file_name,String my_temporary_file_directory,
			String my_sound_pre_string,String my_render_window_name,
			String location_component_name,String audio_component_name)
	{
		movement_file_charset=my_movement_file_charset;
		movement_file_name=my_movement_file_name;
		design_file_name=my_design_file_name;
		temporary_file_directory=my_temporary_file_directory;
		sound_pre_string=my_sound_pre_string;
		render_window_name=my_render_window_name;
		
		part p=my_comp.driver_array[my_driver_id].component_part;
		file_reader fr=new file_reader(p.directory_name+p.material_file_name,p.file_charset);
		fr.get_string();
		String change_name_file_name=fr.directory_name+file_reader.separator(fr.get_string());
		fr.close();
		language_change_name=new change_name(new String[]{change_name_file_name},null,p.file_charset);
		
		component_id=my_comp.component_id;
		
		location_component_id=-1;
		if((my_comp=ek.component_cont.search_component(location_component_name))!=null)
			location_component_id=my_comp.component_id;
		else
			debug_information.println("location component not exist: ",location_component_name);

		audio_component_id=-1;
		if((my_comp=ek.component_cont.search_component(audio_component_name))!=null)
			if(my_comp.driver_number()>0)
				if(my_comp.driver_array[0] instanceof driver_audio.extended_component_driver)
					audio_component_id=my_comp.component_id;
		if(audio_component_id<0)
			debug_information.println("audio component not exist: ",audio_component_name);
	}
}
