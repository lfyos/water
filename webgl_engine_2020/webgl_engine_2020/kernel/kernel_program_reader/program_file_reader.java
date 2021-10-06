package kernel_program_reader;

import kernel_common_class.class_file_reader;
import kernel_common_class.common_reader;
import kernel_file_manager.file_reader;
import kernel_program_default.program_default_file_reader;
import kernel_render.render;

public class program_file_reader
{
	public program_file_reader()
	{	
	}
	public static common_reader get_system_program_reader(String type_name)
	{
		return class_file_reader.get_reader(type_name+"_shader.txt",program_file_reader.class);
	}
	public static common_reader get_render_program_reader(render r,String type_name)
	{
		common_reader reader=null;
		
		String file_name;
		
		switch(type_name) {
		default:
		case "decode":
			file_name=r.driver.javascript_decode;
			break;
		case "draw":
			file_name=r.driver.javascript_draw;
			break;
		case "vertex":
			file_name=r.driver.vertex_shader;
			break;
		case "fragment":
			file_name=r.driver.fragment_shader;
			break;
		case "geometry":
			file_name=r.driver.geometry_shader;
			break;
		case "tess_control":
			file_name=r.driver.tess_control_shader;
			break;
		case "tess_evalue":
			file_name=r.driver.tess_evalue_shader;
			break;
		}
		file_name=file_reader.separator(file_name);
		if((reader=class_file_reader.get_reader(file_name,r.driver.getClass()))!=null)
			if(reader.error_flag()){
				reader.close();
				reader=null;
			}
		if(reader!=null)
			return reader;
		
		if((reader=program_default_file_reader.get_default_reader(type_name+"_"+file_name))!=null)
			if(reader.error_flag()){
				reader.close();
				reader=null;
			}
		return reader;
	}
}
