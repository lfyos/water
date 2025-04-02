package kernel_program_reader;

import kernel_common_class.common_reader;
import kernel_scene.system_parameter;
import kernel_common_class.class_file_reader;

public class program_file_reader
{
	private static String get_text(String file_name,system_parameter system_par)
	{
		String ret_string="";
		common_reader reader=class_file_reader.get_reader(file_name,
			program_file_reader.class,system_par.text_class_charset,
			system_par.text_jar_file_charset);
		if(reader!=null){
			if(!(reader.error_flag()))
				ret_string=reader.get_text();
			reader.close();
		}
		return ret_string;
	}
	public static String get_common_shader_data_structure(system_parameter system_par)
	{
		return get_text("common_shader_data_structure.txt",system_par);
	}
	public static String get_common_shader_variable_declaration(system_parameter system_par)
	{
		return get_text("common_shader_variable_declaration.txt",system_par);
	}
	public static String get_location_shader_program(system_parameter system_par)
	{
		return get_text("location_shader_program.txt",system_par);
	}
	public static long get_system_program_last_time(system_parameter system_par)
	{
		long t1=class_file_reader.get_last_time("common_shader_data_structure.txt",
					program_file_reader.class,system_par.text_jar_file_charset);
		long t2=class_file_reader.get_last_time("common_shader_variable_declaration.txt",
				program_file_reader.class,system_par.text_jar_file_charset);
		long t3=class_file_reader.get_last_time("location_shader_program.txt",
				program_file_reader.class,system_par.text_jar_file_charset);
		
		long t12=(t1>=t2)?t1:t2;
		long t123=(t12>=t3)?t12:t3;
		
		return t123;
	}
}
