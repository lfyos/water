package kernel_maintain;

import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;
import kernel_file_manager.travel_through_directory;

public class test_1 extends travel_through_directory
{
	private static final String mode_string[]= {
		"web_server_render_data_version"
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
			"G:\\water_all\\.git",
			"G:\\water_all\\webgpu_engine_2025\\.metadata",
			"G:\\water_all\\webgpu_engine_2025\\webgpu_engine\\build"
		});
	}
	public static void main(String args[])
	{
		String path_name[]={
				"G:\\water_all",
				"E:\\project_data"
		};
		
		debug_information.println("start search");
		
		for(String my_path_name:path_name) {
			debug_information.println("Begin:		",	my_path_name);
			new test_1().do_travel(my_path_name,false);
			debug_information.println("End: 		",	my_path_name);
		}
		
		debug_information.println("end search");
	}
}
