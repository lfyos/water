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
			if(str.indexOf("driver_opengl_fixed_pipeline")>=0)
				debug_information.println(file_name);
		}
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
//		String path_name="F:\\water_all\\data";
		String path_name="E:\\project_data";
		
		debug_information.println("Begin:	",	path_name);
		new test_1().do_travel(path_name,false);
		debug_information.println("End:	",		path_name);
	}
}
