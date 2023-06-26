package driver_lession_10_pickup_component;

import kernel_part.part;
import kernel_render.render;
import kernel_driver.part_driver;
import kernel_part.part_parameter;
import kernel_driver.render_driver;
import kernel_driver.render_instance_driver;
import kernel_engine.engine_kernel;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;
import kernel_component.component_load_source_container;

public class extended_render_driver extends render_driver
{
	public extended_render_driver()
	{
		super();
	}
	public void destroy()
	{
		super.destroy();
	}
	public void initialize_render_driver(int render_id,engine_kernel ek,client_request_response request_response)
	{
	}
	public render_driver clone(render parent_render,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		return new extended_render_driver();
	}
	public String[] get_render_list(
			int part_type_id,file_reader shader_fr,String load_sub_directory_name,
			component_load_source_container component_load_source_cont,
			system_parameter system_par,scene_parameter scene_par,client_request_response request_response)
	{
		String render_list_file_name=file_reader.separator(shader_fr.get_string());
		return new String[] {shader_fr.directory_name+render_list_file_name,shader_fr.get_charset()};
	}
	public String[] get_part_list(
			int part_type_id,file_reader render_fr,String load_sub_directory_name,part_parameter part_par,
			component_load_source_container component_load_source_cont,
			system_parameter system_par,scene_parameter scene_par,client_request_response request_response)
	{
		String par_list_file_name=file_reader.separator(render_fr.get_string());
		return new String[] {render_fr.directory_name+par_list_file_name,render_fr.get_charset()};
	}
	public void create_shader_data(file_writer f,render r,
			engine_kernel ek,client_request_response request_response)
	{
		super.create_shader_data(f,r,ek,request_response);
	}
	public part_driver create_part_driver(file_reader part_fr,part p,
			component_load_source_container component_load_source_cont,
			system_parameter system_par,client_request_response request_response)
	{
		return new extended_part_driver(p,system_par,request_response);
	}
	public render_instance_driver create_render_instance_driver(render r,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_render_instance_driver();
	}
}
