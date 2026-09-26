package kernel_maintain;

import kernel_file_manager.file_writer;
import kernel_common_class.debug_information;
import kernel_file_manager.travel_through_directory;

public class test_1 extends travel_through_directory
{
	public void operate_file(String file_name)
	{
		file_writer.file_touch(file_name);
	}
	public test_1()
	{
		super(new String[]
		{
			"G:\\water_all\\.git"
		});
	}
	public static void main(String args[])
	{
		debug_information.println("start");
		
		new test_1().do_travel("G:\\water_all",false);
		
		debug_information.println("end");
	}
}
