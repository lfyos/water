package driver_gltf;

import java.io.File;
import java.nio.charset.Charset;

import kernel_part.part;
import kernel_part.part_parameter;
import old_convert.gltf_converter;
import kernel_driver.part_driver;
import kernel_driver.render_driver;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;

import kernel_common_class.debug_information;

public class extended_render_driver extends render_driver
{
	private String light_file_name;
	private int part_group_id;
	
	public extended_render_driver()
	{
		super(	"create_frame.txt",
				"javascript.draw.txt",
				"vertex.shader.txt",
				"fragment.shader.txt",
				"geometry.shader.txt",
				"tess_control.shader.txt",
				"tess_evalue.shader.txt");
		part_group_id=0;
	}
	public void destroy()
	{
		super.destroy();
	}
	public String[] get_part_list(boolean giveup_part_load_flag,file_reader render_fr,
			String load_sub_directory_name,String par_list_file_name,String extract_file_directory,
			part_parameter part_par,system_parameter system_par,client_request_response request_response)
	{
		part_group_id++;
		
		String	gltf_charset,new_par_list_file_name;
		par_list_file_name		=render_fr.get_string()+load_sub_directory_name+par_list_file_name;
		gltf_charset			=render_fr.get_string();
		light_file_name			=render_fr.directory_name+render_fr.get_string();
		new_par_list_file_name	=extract_file_directory+load_sub_directory_name+"gltf.list";
		if(giveup_part_load_flag||(load_sub_directory_name.length()<=2))
			return null;

		File f=new File(par_list_file_name);
		if(!(f.exists())) {
			debug_information.println("Part file NOT exist in gltf driver:		",par_list_file_name);
			return null;
		}
		if(f.lastModified()>=(new File(new_par_list_file_name)).lastModified()){
			if(gltf_charset==null)
				gltf_charset=Charset.defaultCharset().name();
			else
				switch((gltf_charset=gltf_charset.toLowerCase())){
				case "null"		:
					gltf_charset=Charset.defaultCharset().name();
					break;
				case "default"	:
					gltf_charset=render_fr.get_charset();
					break;
				}
			file_writer.file_delete(extract_file_directory+load_sub_directory_name);
			new gltf_converter(par_list_file_name,
					extract_file_directory+load_sub_directory_name,
					gltf_charset,render_fr.get_charset());
		}
		return new String[]{new_par_list_file_name,render_fr.get_charset()};
	}
	public part_driver create_part_driver(file_reader part_fr,part p,
			system_parameter system_par,client_request_response request_response)
	{
		return new extended_part_driver(light_file_name,part_group_id);
	}
}