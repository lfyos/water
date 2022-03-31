package driver_opengl_fixed_pipeline;

import java.io.File;

import kernel_common_class.change_name;
import kernel_part.part;
import kernel_driver.part_driver;
import kernel_driver.render_driver;
import kernel_part.part_parameter;
import kernel_render.render;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;

public class extended_render_driver extends render_driver
{
	private String render_list_directory_name,part_list_directory_name;
	private String shader_material_file_name,light_file_name,file_charset;
	
	private void release_all()
	{
		render_list_directory_name=null;
		part_list_directory_name=null;
		shader_material_file_name=null;
		light_file_name=null;
		file_charset=null;
	}
	public extended_render_driver()
	{
		super(	"voxel.txt",
				"javascript.draw.txt",
				"vertex.shader.txt",
				"fragment.shader.txt",
				"geometry.shader.txt",
				"tess_control.shader.txt",
				"tess_evalue.shader.txt");
		release_all();
	}
	public void destroy()
	{
		super.destroy();
		release_all();
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
		extended_render_driver ret_val=new extended_render_driver();
		
		ret_val.render_list_directory_name=render_list_directory_name;
		ret_val.part_list_directory_name=part_list_directory_name;
		ret_val.shader_material_file_name=shader_material_file_name;
		ret_val.light_file_name=light_file_name;
		ret_val.file_charset=file_charset;
		
		return ret_val;
	}
	public String[] get_render_list(int part_type_id,
			file_reader shader_fr,String load_sub_directory_name,
			system_parameter system_par,scene_parameter scene_par,
			change_name mount_component_name_and_assemble_file_name,
			client_request_response request_response)
	{
		String str;
		File render_f;
		String render_list_file_name=shader_fr.directory_name+file_reader.separator(shader_fr.get_string());
		if(!((render_f=new File(render_list_file_name)).exists()))
			return null;
		String render_list_directory_name=file_reader.separator(render_f.getParent());
		if(render_list_directory_name.charAt(render_list_directory_name.length()-1)!=File.separatorChar)
			render_list_directory_name+=File.separator;

		switch(((str=shader_fr.get_string())==null)?"":str){
		default:
			return null;
		case "absulate":
			part_list_directory_name=file_reader.separator(shader_fr.get_string());
			break;
		case "relative":
			part_list_directory_name=render_list_directory_name+file_reader.separator(shader_fr.get_string());
			break;
		case "environment":
			part_list_directory_name=shader_fr.get_string();
			if((str=System.getenv(part_list_directory_name))!=null)
				part_list_directory_name=str;
			part_list_directory_name=file_reader.separator(part_list_directory_name);
			break;
		}
		if(part_list_directory_name.charAt(part_list_directory_name.length()-1)!=File.separatorChar)
			part_list_directory_name+=File.separator;
		if(part_type_id==2)
			part_list_directory_name+=load_sub_directory_name;
		
		shader_material_file_name	=part_list_directory_name+file_reader.separator(shader_fr.get_string());
		light_file_name				=render_list_directory_name+file_reader.separator(shader_fr.get_string());
		file_charset				=shader_fr.get_charset();
		
		return new String[] {render_list_file_name,shader_fr.get_charset()};
	}
	public String[] get_part_list(int part_type_id,file_reader render_fr,String load_sub_directory_name,
			part_parameter part_par,system_parameter system_par,scene_parameter scene_par,
			change_name mount_component_name_and_assemble_file_name,client_request_response request_response)
	{
		String par_list_file_name=render_fr.get_string();
		String part_list_component_name=render_fr.get_string();
		String not_part_list_component_name=render_fr.get_string();
		
		if((par_list_file_name==null)||(part_list_component_name==null)||(not_part_list_component_name==null))
			return null;
		if(part_list_component_name.toLowerCase().compareTo("null")==0)
			part_list_component_name=null;
		if(not_part_list_component_name.toLowerCase().compareTo("null")==0)
			not_part_list_component_name=null;
		if(mount_component_name_and_assemble_file_name!=null) {
			if(not_part_list_component_name!=null)
				mount_component_name_and_assemble_file_name.insert(new String[]
					{not_part_list_component_name,	part_list_directory_name+"movement.assemble"});
			if(part_list_component_name!=null)
				mount_component_name_and_assemble_file_name.insert(new String[] 
					{part_list_component_name,		part_list_directory_name+"assemble.assemble"});
		}
		return new String[]{file_reader.separator(part_list_directory_name+par_list_file_name),render_fr.get_charset()};
	}
	public part_driver create_part_driver(file_reader part_fr,part p,system_parameter system_par,
			change_name mount_component_name_and_assemble_file_name,client_request_response request_response)
	{
		return new extended_part_driver(p);
	}
	public void create_shader_data(file_writer fw,render rr,system_parameter system_par,scene_parameter scene_par)
	{
		fw.println("		{");
		fw.println(file_reader.get_text(light_file_name,file_charset));
		if(!(new File(shader_material_file_name).exists()))
			fw.println("			\"material\"	:	[]");
		else
			fw.println(file_reader.get_text(shader_material_file_name,file_charset));
		fw.println("		}");
		return;
	}
}
