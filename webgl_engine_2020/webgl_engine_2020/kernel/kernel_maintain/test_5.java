package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_writer;
import kernel_file_manager.travel_through_directory;

public class test_5 extends travel_through_directory
{
	public void operate_file(String file_name)
	{
		file_writer.file_touch(file_name,false);
	}
	public test_5(String my_directory_name)
	{
		do_travel(my_directory_name,false);
	}
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		new test_5("E:\\water_all\\data");
		new test_5("E:\\water_all\\webgl_engine_2020");
		
		debug_information.println("End");
	}
}