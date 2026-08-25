package kernel_driver;

import kernel_part.part;
import kernel_render.render;
import kernel_scene.scene_kernel;
import kernel_part.part_parameter;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_file_manager.file_directory;
import kernel_network.client_request_response;
import kernel_component.component_load_source_container;

public class render_driver
{
	public render_driver(file_reader shader_fr,
			render ren,client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
	}
	public void destroy()
	{
	}
	public render_driver clone(
			render ren,client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		return new render_driver(null,ren,request_response,system_par,scene_par);
	}
	public void initialize_render_driver(render ren,
			scene_kernel sk,client_request_response request_response)
	{
	}
	public void create_render_driver_initialization_data(file_writer fw,
			render ren,scene_kernel sk,client_request_response request_response)
	{
	}
	public String[] get_render_list(file_reader shader_fr,render ren,
			component_load_source_container component_load_source_cont,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		String render_list_file_name=file_directory.replace_special_char(shader_fr.get_string());
		return new String[] {shader_fr.directory_name+render_list_file_name,shader_fr.get_charset()};
	}
	public String[] get_part_list(
			render ren,file_reader render_fr,part_parameter part_par,
			component_load_source_container component_load_source_cont,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		String par_list_file_name=file_directory.replace_special_char(render_fr.get_string());
		return new String[] {render_fr.directory_name+par_list_file_name,render_fr.get_charset()};
	}
	public String[][] shader_file_name_array()
	{
		return 
			new String[][]
			{
				new String[] 
				{
						"component.js","part.js","render.js"
				},
				new String[] 
				{
						"shader.txt"
				},
				new String[] 
				{
						"data.txt"
				}
			};
	}
	public part_driver create_part_driver(file_reader part_fr,part p,render ren,
			component_load_source_container component_load_source_cont,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		return new part_driver();
	}
	public render_instance_driver create_render_instance_driver(render ren,
			scene_kernel sk,client_request_response request_response)
	{
		return new render_instance_driver();
	}
}
