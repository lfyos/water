package kernel_program_reader;

import kernel_common_class.common_reader;
import kernel_scene.system_parameter;
import kernel_common_class.class_file_reader;

public class program_file_reader
{
	private static String program_file_name[]= {
			"common_shader_data_structure.txt",
			"common_shader_variable_declaration.txt",
			"location_shader_program.txt"
	};
	private static String get_text(String file_name,system_parameter system_par)
	{
		String ret_string="";
		common_reader reader=class_file_reader.get_reader(file_name,
			program_file_reader.class,system_par.text_class_charset);
		if(reader!=null){
			if(!(reader.error_flag()))
				ret_string=reader.get_text();
			reader.close();
		}
		return ret_string;
	}
	public static String get_common_shader_data_structure(system_parameter system_par)
	{
		return get_text(program_file_name[0],system_par);
	}
	public static String get_common_shader_variable_declaration(system_parameter system_par)
	{
		return get_text(program_file_name[1],system_par);
	}
	public static String get_location_shader_program(system_parameter system_par)
	{
		return get_text(program_file_name[2],system_par);
	}
	public static long get_system_program_last_time(system_parameter system_par)
	{
		long ret_val=0;
		for(int i=0,ni=program_file_name.length;i<ni;i++) {
			long current_file_time=class_file_reader.get_last_time(
					program_file_name[i],program_file_reader.class,system_par.text_class_charset);
			if(current_file_time>ret_val)
				ret_val=current_file_time;
		}
		return ret_val;
	}
}
