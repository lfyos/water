package kernel_program_reader;

import kernel_engine.system_parameter;
import kernel_common_class.common_reader;
import kernel_common_class.class_file_reader;

public class program_file_reader
{
	public static common_reader get_system_program_reader(system_parameter system_par)
	{
		common_reader reader=class_file_reader.get_reader("common_shader.txt",
			program_file_reader.class,system_par.text_class_charset,
			system_par.text_jar_file_charset);
		if(reader!=null)
			if(reader.error_flag()){
				reader.close();
				reader=null;
			}
		return reader;
	}
	public static long get_system_program_last_time(system_parameter system_par)
	{
		return class_file_reader.get_last_time("common_shader.txt",
					program_file_reader.class,system_par.text_jar_file_charset);
	}
}
