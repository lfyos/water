package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.travel_through_directory;

public class test_1 extends travel_through_directory
{
	public void operate_file(String file_name)
	{
		String str;
		if((str=file_reader.get_text(file_name,"GBK"))!=null) {
			if(str.indexOf("destroy")>=0)
				debug_information.println(file_name);
		}
	}
	
	public test_1()
	{
		super(new String[]
		{
//			"F:\\water_all\\.git",
//			"F:\\water_all\\webgpu_engine_2024"
		});
	}
	public static void main(String args[])
	{
		debug_information.println("Begin:");
		
		new test_1().do_travel("F:\\water_all\\data", false);

		debug_information.println("End");
	}
}
