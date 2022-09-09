package kernel_driver;

import kernel_part.part;
import kernel_render.render;
import kernel_part.part_parameter;
import kernel_engine.engine_kernel;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;
import kernel_component.component_load_source_container;

public class render_driver
{
	public String javascript_decode,javascript_draw;
	public String vertex_shader,fragment_shader,geometry_shader,tess_control_shader,tess_evalue_shader;
	
	public void destroy()
	{
		javascript_decode	=null;
		javascript_draw		=null;
		vertex_shader		=null;
		fragment_shader		=null;
		geometry_shader		=null;
		tess_control_shader	=null;
		tess_evalue_shader	=null;
	}
	public void initialize_render_driver(int render_id,engine_kernel ek,client_request_response request_response)
	{
	}
	public render_driver clone(render parent_render,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		return new render_driver(javascript_decode,javascript_draw,
				vertex_shader,fragment_shader,geometry_shader,tess_control_shader,tess_evalue_shader);
	}
	public render_driver(					
			String my_javascript_decode,	String my_javascript_draw,
			String my_vertex_shader,		String my_fragment_shader,
			String my_geometry_shader,	
			String my_tess_control_shader,	String my_tess_evalue_shader)
	{
		javascript_decode	=my_javascript_decode;
		javascript_draw		=my_javascript_draw;
		
		vertex_shader		=my_vertex_shader;
		fragment_shader		=my_fragment_shader;
		
		geometry_shader		=my_geometry_shader;
		
		tess_control_shader	=my_tess_control_shader;
		tess_evalue_shader	=my_tess_evalue_shader;
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
	public String create_include_shader_program(String shader_type_string,
			//vertex,fragment,geometry,tess_control,tess_evalue
			render rr,system_parameter system_par,scene_parameter scene_par)
	{
		return null;
	}
	public void create_shader_data(file_writer fw,render rr,system_parameter system_par,scene_parameter scene_par)
	{
		fw.println("	null");
	}
	public part_driver create_part_driver(file_reader part_fr,part p,
			component_load_source_container component_load_source_cont,
			system_parameter system_par,client_request_response request_response)
	{
		return new part_driver();
	}
	public render_instance_driver create_render_instance_driver(render r,
			engine_kernel ek,client_request_response request_response)
	{
		return new render_instance_driver();
	}
}
