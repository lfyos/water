package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.travel_through_directory;
import kernel_file_manager.file_writer;

public class test_1 extends travel_through_directory
{
	private String file_name;
	private int file_name_length;
	private int id;
	
	public void operate_file(String file_pathname)
	{
		String my_file_pathname=file_pathname.toLowerCase().trim();
		int my_file_pathlength=my_file_pathname.length();
		if(my_file_pathlength<=file_name_length)
			return;
		String my_file_name=my_file_pathname.substring(my_file_pathlength-file_name_length);
		if(my_file_name.compareTo(file_name)!=0)
			return;
		debug_information.println((++id)+"	:	"+file_pathname);
		file_writer.file_copy("c:\\temp\\movement.assemble",file_pathname);
	}
	
	public test_1(String my_directory_name)
	{
		file_name="movement.assemble";
		file_name_length=file_name.length();
		id=0;
		do_travel(my_directory_name,false);
	}
	
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		new test_1("E:\\water_all");
		new test_1("E:\\cad");
		
		debug_information.println("End");
	}
}
