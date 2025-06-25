package driver_location_modifier;

import kernel_part.part;
import kernel_render.render;
import kernel_scene.scene_kernel;
import kernel_driver.part_driver;
import kernel_part.part_parameter;
import kernel_driver.render_driver;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_file_manager.file_reader;
import kernel_driver.render_instance_driver;
import kernel_network.client_request_response;
import kernel_component.component_load_source_container;

public class extended_render_driver extends render_driver
{
	public extended_render_driver(file_reader shader_fr,render ren,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		super(shader_fr,ren,request_response,system_par,scene_par);
	}
	public void destroy()
	{
		super.destroy();
	}
	public render_driver clone(render ren,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		return new extended_render_driver(null,ren,request_response,system_par,scene_par);
	}
	public void initialize_render_driver(int render_id,scene_kernel sk,client_request_response request_response)
	{
	}
	public String[] get_render_list(file_reader shader_fr,render ren,
			component_load_source_container component_load_source_cont,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		String render_list_file_name=file_reader.separator(shader_fr.get_string());
		return new String[] {shader_fr.directory_name+render_list_file_name,shader_fr.get_charset()};
	}
	public String[] get_part_list(
			render ren,file_reader render_fr,part_parameter part_par,
			component_load_source_container component_load_source_cont,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		String par_list_file_name=file_reader.separator(render_fr.get_string());
		return new String[] {render_fr.directory_name+par_list_file_name,render_fr.get_charset()};
	}
	public String[][] shader_file_name_array()
	{
		return super.shader_file_name_array();
	}
	public part_driver create_part_driver(file_reader part_fr,part p,render ren,
			component_load_source_container component_load_source_cont,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		return new extended_part_driver();
	}
	public render_instance_driver create_render_instance_driver(render ren,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_render_instance_driver();
	}
}
