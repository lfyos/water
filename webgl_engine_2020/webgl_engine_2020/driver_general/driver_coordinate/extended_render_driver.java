package driver_coordinate;


import kernel_driver.part_driver;
import kernel_driver.render_driver;
import kernel_engine.system_parameter;
import kernel_part.part;
import kernel_part.part_parameter;

import kernel_file_manager.file_reader;
import kernel_network.client_request_response;

public class extended_render_driver extends	render_driver
{
	public extended_render_driver()
	{
		super(	"no_frame.txt",
				"javascript.draw.txt",
				"vertex.shader.txt",
				"fragment.shader.txt",
				"geometry.shader.txt",
				"tess_control.shader.txt",
				"tess_evalue.shader.txt");
	}
	public void destroy()
	{
		super.destroy();
	}
	public String[] get_part_list(boolean giveup_part_load_flag,file_reader render_fr,
			String load_sub_directory_name,String par_list_file_name,String extract_file_directory,
			part_parameter part_par,system_parameter system_par,client_request_response request_response)
	{
		return new String[] {render_fr.directory_name+par_list_file_name,render_fr.get_charset()};
	}
	public part_driver create_part_driver(file_reader part_fr,part p,
			system_parameter system_par,client_request_response request_response)
	{
		String file_name=p.directory_name+p.material_file_name;
		file_reader f=new file_reader(file_name,part_fr.get_charset());
		double camera_length_scale=f.get_double();
		double selection_length_scale=f.get_double();
		f.close();
		
		return new extended_part_driver(camera_length_scale,selection_length_scale);
	}
}
