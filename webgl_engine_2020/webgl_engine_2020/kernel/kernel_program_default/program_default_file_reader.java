package kernel_program_default;

import kernel_common_class.class_file_reader;
import kernel_common_class.common_reader;

public class program_default_file_reader
{
	public static common_reader get_default_reader(String file_name)
	{
		return class_file_reader.get_reader(file_name,program_default_file_reader.class);
	}
}
