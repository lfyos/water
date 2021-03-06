package driver_manipulator;

import kernel_common_class.change_name;

import kernel_driver.part_driver;
import kernel_driver.render_driver;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_parameter;


public class extended_render_driver extends render_driver
{
	private change_name language_change_name;
	
	public extended_render_driver()
	{
		super(	"discard_all.txt",
				"javascript.draw.txt",
				"vertex.shader.txt",
				"fragment.shader.txt",
				"geometry.shader.txt",
				"tess_control.shader.txt",
				"tess_evalue.shader.txt");
		language_change_name=null;
	}
	public void destroy()
	{
		super.destroy();
		if(language_change_name!=null) {
			language_change_name.destroy();
			language_change_name=null;
		}
	}
	public String[] get_part_list(boolean giveup_part_load_flag,file_reader render_fr,
			String load_sub_directory_name,String par_list_file_name,String extract_file_directory,
			part_parameter part_par,system_parameter system_par,client_request_response request_response)
	{
		if(language_change_name!=null)
			language_change_name.destroy();
		language_change_name=new change_name(
			new String[]{render_fr.directory_name+render_fr.get_string()},
			null,render_fr.get_charset());
		
		return new String[] {render_fr.directory_name+par_list_file_name,render_fr.get_charset()};
	}
	public part_driver create_part_driver(file_reader part_fr,part p,
			system_parameter system_par,client_request_response request_response)
	{
		file_reader f=new file_reader(p.directory_name+p.material_file_name,part_fr.get_charset());
		int camera_modifier_id=f.get_int();
		long touch_time_length=f.get_long();
		boolean save_component_name_or_id_flag=f.get_boolean();
		f.close();
		
		return new extended_part_driver(language_change_name,
				camera_modifier_id,touch_time_length,save_component_name_or_id_flag);
	}
}
