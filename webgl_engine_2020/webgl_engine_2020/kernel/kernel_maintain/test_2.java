package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;

public class test_2
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		file_reader fr=new file_reader("E:\\cad\\test\\part.obj.mesh",null);
		
		new kernel_part.part_rude(fr);
		
		fr.close();
		
		debug_information.println("End");
	}
}
