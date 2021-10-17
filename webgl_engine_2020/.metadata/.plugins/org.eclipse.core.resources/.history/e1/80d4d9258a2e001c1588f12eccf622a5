package driver_menu;

import kernel_common_class.change_name;
import kernel_driver.part_driver;
import kernel_driver.render_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_parameter;
import kernel_render.render;


public class extended_render_driver extends render_driver
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
	public void initialize_render_driver(int render_id,engine_kernel ek,client_request_response request_response)
	{
	}
	public void response_init_render_data(int render_id,engine_kernel ek,client_information ci)
	{
	}
	public render_driver clone(render parent_render,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		return new extended_render_driver();
	}
	public String[] get_part_list(int part_type_id,
			file_reader render_fr,String load_sub_directory_name,String par_list_file_name,
			part_parameter part_par,system_parameter system_par,scene_parameter scene_par,
			change_name mount_component_name_and_assemble_file_name,client_request_response request_response)
	{
		String install_component_name=render_fr.get_string();
		String install_file_name=render_fr.get_string();
		install_file_name=render_fr.directory_name+file_reader.separator(install_file_name);
		mount_component_name_and_assemble_file_name.insert(new String[] {install_component_name,install_file_name});
		
		return new String[] {render_fr.directory_name+par_list_file_name,render_fr.get_charset()};
	}
	public part_driver create_part_driver(file_reader part_fr,part p,system_parameter system_par,
			change_name mount_component_name_and_assemble_file_name,client_request_response request_response)
	{
		return new extended_part_driver();
	}
}
