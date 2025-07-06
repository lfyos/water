package driver_coordinate;

import kernel_part.part;
import kernel_render.render;
import kernel_scene.scene_kernel;
import kernel_driver.part_driver;
import kernel_part.part_parameter;
import kernel_driver.render_driver;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_driver.render_instance_driver;
import kernel_network.client_request_response;
import kernel_component.component_load_source_container;

public class extended_render_driver extends	render_driver
{
	public extended_render_driver(file_reader shader_fr,
			render ren,client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		super(shader_fr,ren,request_response,system_par,scene_par);
	}
	public void destroy()
	{
		super.destroy();
	}
	public render_driver clone(
			render ren,client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		return new extended_render_driver(null,ren,request_response,system_par,scene_par);
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
		String ret_val[]=super.get_render_list(shader_fr,ren,
				component_load_source_cont,request_response,system_par,scene_par);
		
		return ret_val;
	}
	public String[] get_part_list(
			render ren,file_reader render_fr,part_parameter part_par,
			component_load_source_container component_load_source_cont,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		String ret_val[]=super.get_part_list(ren,render_fr,part_par,
				component_load_source_cont,request_response,system_par,scene_par);
		
		return ret_val;
	}
	public String[][] shader_file_name_array()
	{
		return super.shader_file_name_array();
	}
	public part_driver create_part_driver(file_reader part_fr,part p,render ren,
			component_load_source_container component_load_source_cont,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		return new extended_part_driver();
	}
	public render_instance_driver create_render_instance_driver(render ren,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_render_instance_driver();
	}
}
