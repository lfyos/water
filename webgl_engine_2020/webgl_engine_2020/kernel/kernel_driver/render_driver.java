package kernel_driver;

import kernel_part.part;
import kernel_part.part_parameter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_engine.system_parameter;



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
	public String[] get_part_list(boolean giveup_part_load_flag,file_reader render_fr,
			String load_sub_directory_name,String par_list_file_name,String extract_file_directory,
			part_parameter part_par,system_parameter system_par,client_request_response request_response)
	{
		return new String[] {render_fr.directory_name+par_list_file_name,render_fr.get_charset()};
	}
	public part_driver create_part_driver(file_reader part_fr,part p,
			system_parameter system_par,client_request_response request_response)
	{
		return new part_driver();
	}
}
