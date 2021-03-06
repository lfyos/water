package driver_opengl_fixed_pipeline;

import java.io.File;


import kernel_driver.part_driver;
import kernel_driver.render_driver;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_parameter;


public class extended_render_driver extends render_driver
{
	public light_parameter light_par;
	public render_material_parameter render_material_par;
	
	public extended_render_driver()
	{
		super(	"javascript.decode.txt",
				"javascript.draw.txt",
				"vertex.shader.txt",
				"fragment.shader.txt",
				"geometry.shader.txt",
				"tess_control.shader.txt",
				"tess_evalue.shader.txt");
		light_par=null;
		render_material_par=null;
	}
	public void destroy()
	{
		super.destroy();
		light_par=null;
		render_material_par=null;
	}
	public String[] get_part_list(boolean giveup_part_load_flag,file_reader render_fr,
			String load_sub_directory_name,String par_list_file_name,String extract_file_directory,
			part_parameter part_par,system_parameter system_par,client_request_response request_response)
	{
		light_par=new light_parameter(
			render_fr.directory_name+render_fr.get_string(),render_fr.get_charset());
		render_material_par=new render_material_parameter(
			render_fr.directory_name+render_fr.get_string(),render_fr.get_charset());
		
		return new String[] {render_fr.directory_name+par_list_file_name,render_fr.get_charset()};
	}
	public part_driver create_part_driver(file_reader part_fr,part p,
			system_parameter system_par,client_request_response request_response)
	{
		long last_modified_time;
		if(light_par.last_modified_time<render_material_par.last_modified_time)
			last_modified_time=render_material_par.last_modified_time;
		else
			last_modified_time=light_par.last_modified_time;
	
		File f=new File(p.directory_name+p.mesh_file_name);
		if(f.exists())
			if(f.lastModified()<last_modified_time)
				f.setLastModified(last_modified_time);
		f=new File(p.directory_name+p.material_file_name);
		if(f.exists())
			if(f.lastModified()<last_modified_time)
				f.setLastModified(last_modified_time);
		
		return new extended_part_driver(light_par,render_material_par,null,null);
	}
}
