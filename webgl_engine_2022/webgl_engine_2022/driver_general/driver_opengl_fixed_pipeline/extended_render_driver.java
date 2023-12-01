package driver_opengl_fixed_pipeline;

import java.io.File;

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
import kernel_network.client_request_response;
import kernel_component.component_load_source_container;

public class extended_render_driver extends render_driver
{
	private String light_file_name,file_charset;
	
	public void destroy()
	{
		super.destroy();
		light_file_name=null;
		file_charset=null;
	}
	public extended_render_driver(file_reader f_shader,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		light_file_name=null;
		file_charset=null;
	}
	public render_driver clone(render parent_render,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		extended_render_driver ret_val=new extended_render_driver(null,request_response,system_par,scene_par);
		ret_val.light_file_name	=this.light_file_name;
		ret_val.file_charset	=this.file_charset;
		return ret_val;
	}
	public void initialize_render_driver(int render_id,engine_kernel ek,client_request_response request_response)
	{	
	}
	public String[] get_render_list(
			int part_type_id,file_reader shader_fr,String load_sub_directory_name,
			component_load_source_container component_load_source_cont,
			system_parameter system_par,scene_parameter scene_par,client_request_response request_response)
	{
		String render_list_file_name=shader_fr.directory_name+file_reader.separator(shader_fr.get_string());
		light_file_name				=shader_fr.directory_name+file_reader.separator(shader_fr.get_string());
		file_charset				=shader_fr.get_charset();
		return (new File(render_list_file_name).exists())?new String[] {render_list_file_name,file_charset}:null;
	}
	public String[] get_part_list(
			int part_type_id,file_reader render_fr,String load_sub_directory_name,part_parameter part_par,
			component_load_source_container component_load_source_cont,
			system_parameter system_par,scene_parameter scene_par,client_request_response request_response)
	{
		String part_list_file_name=render_fr.get_string();
		
		switch((part_list_file_name==null)?"":part_list_file_name){
		default:
			return null;
		case "absulate":
			part_list_file_name="";
			break;
		case "relative":
			part_list_file_name=render_fr.directory_name;
			break;
		case "environment":
			if((part_list_file_name=System.getenv(render_fr.get_string()))==null){
				part_list_file_name=render_fr.directory_name;
				break;
			}
			part_list_file_name=file_reader.separator(part_list_file_name);
			if(part_list_file_name.charAt(part_list_file_name.length()-1)!=File.separatorChar)
				part_list_file_name+=File.separatorChar;
			if(part_type_id==2)
				part_list_file_name+=load_sub_directory_name;
			break;
		}
		part_list_file_name+=file_reader.separator(render_fr.get_string());
		
		return new String[] {part_list_file_name,render_fr.get_charset()};
	}
	public String[][] shader_file_name_array()
	{
		return super.shader_file_name_array();
	}
	public part_driver create_part_driver(file_reader part_fr,part p,
			component_load_source_container component_load_source_cont,
			system_parameter system_par,client_request_response request_response)
	{
		return new extended_part_driver();
	}
	public render_instance_driver create_render_instance_driver(render r,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_render_instance_driver(	light_file_name,file_charset);
	}
}
