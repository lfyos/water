package driver_cad;

import kernel_part.part;
import kernel_part.part_parameter;

import java.io.File;

import kernel_common_class.zip_file;
import kernel_driver.part_driver;
import kernel_driver.render_driver;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;

public class extended_render_driver extends render_driver
{
	private String assemble_directory_name,assemble_file_charset,light_file_name,light_file_charset;
	private boolean create_normal_flag;
	
	private void release_all()
	{
		assemble_directory_name=null;
		assemble_file_charset=null;
		light_file_name=null;
		light_file_charset=null;
		create_normal_flag=false;
	}
	public extended_render_driver()
	{
		super(	
				"create_frame.txt",
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
	public String[] get_part_list(boolean giveup_part_load_flag,file_reader render_fr,
			String load_sub_directory_name,String par_list_file_name,String extract_file_directory,
			part_parameter part_par,system_parameter system_par,client_request_response request_response)
	{
		String create_normal_file_name=render_fr.directory_name+render_fr.get_string();
		String assemble_directory_type;
		if((assemble_directory_type=render_fr.get_string())==null)
			assemble_directory_type="relative";
		
		switch(assemble_directory_type){
		default:
			assemble_directory_name=assemble_directory_type;
			break;
		case "absulate":
			assemble_directory_name=render_fr.get_string();
			break;
		case "relative":
			assemble_directory_name=render_fr.directory_name+render_fr.get_string();
			break;
		case "environment":
			String environment_directory_name;
			assemble_directory_name=render_fr.get_string();
			if((environment_directory_name=System.getenv(assemble_directory_name))!=null)
				assemble_directory_name=environment_directory_name;
			break;
		}
		assemble_directory_name=file_reader.separator(assemble_directory_name);
		if(assemble_directory_name.charAt(assemble_directory_name.length()-1)!=File.separatorChar)
			assemble_directory_name+=File.separator;

		file_reader fr=new file_reader(create_normal_file_name,render_fr.get_charset());
		
		create_normal_flag=fr.get_boolean();
		
		int index_id;
		if((index_id=load_sub_directory_name.lastIndexOf('.'))>=0) {
			String type_str=load_sub_directory_name.substring(
					index_id,load_sub_directory_name.length()-1).toLowerCase();
			while(!(fr.eof())) {
				String str=fr.get_string();
				boolean my_create_normal_flag=fr.get_boolean();
				if(str!=null)
					if(type_str.compareTo(str.toLowerCase())==0){
						create_normal_flag=my_create_normal_flag;
						break;
					}
			}
		}
		fr.close();
		
		assemble_directory_name+=load_sub_directory_name;

		assemble_file_charset	=render_fr.get_charset();
		light_file_name			=render_fr.directory_name+par_list_file_name;
		light_file_charset		=render_fr.get_charset();

		zip_file.unzip_directory(assemble_directory_name,
				"cad render driver wait for doing unzip_directory:	"+assemble_directory_name);
		file_writer.file_touch(assemble_directory_name,false);
		
		String movement_assemble_file_name=assemble_directory_name+"movement.assemble";
		String original_movement_assemble_file_name=render_fr.directory_name+"movement.assemble";
		if((new File(original_movement_assemble_file_name).lastModified())>=(new File(movement_assemble_file_name).lastModified()))
			file_writer.file_copy(original_movement_assemble_file_name,movement_assemble_file_name);
		return new String[]{assemble_directory_name+"part.list",render_fr.get_charset()};
	}
	public part_driver create_part_driver(file_reader part_fr,part p,
			system_parameter system_par,client_request_response request_response)
	{
		return new extended_part_driver(p,
			assemble_directory_name,assemble_file_charset,light_file_name,
			light_file_charset,create_normal_flag,system_par,request_response);
	}
}
