package kernel_maintain;

import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;
import kernel_file_manager.travel_through_directory;

public class test_1 extends travel_through_directory
{
	private static final String mode_string[]= {
		"file_program",
		"multifile_program",
		"charset_file_program",
		"charset_multifile_program",
		"token_program"
	};
	
	public void operate_file(String file_name)
	{
		String str;
		if((str=file_reader.get_text(file_name,"GBK"))!=null)
			for(String my_mode_str:mode_string)
				if(str.indexOf(my_mode_str)>=0)
					debug_information.println(my_mode_str+":	",file_name);
	}
	
	public test_1()
	{
		super(new String[]
		{
//			"F:\\water_all\\.git"
		});
	}
	public static void main(String args[])
	{
		String path_name[]={
				"E:\\project_data",
				"G:\\water_all\\data"
		};
		
		debug_information.println("start search");
		
		for(String my_path_name:path_name) {
			debug_information.println("Begin:		",	my_path_name);
			new test_1().do_travel(my_path_name,false);
			debug_information.println("End:			",	my_path_name);
		}
		
		debug_information.println("end search");
	}
}
