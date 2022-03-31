package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_writer;

public class test_2
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		file_writer.delete_comment("E:\\water_all\\data\\project\\part\\other_part\\part_obj\\grass\\part_1\\part.obj.mesh.face", null);
		file_writer.delete_comment("E:\\water_all\\data\\project\\part\\other_part\\part_obj\\grass\\part_3\\part.obj.mesh.face", null);
		
		debug_information.println("End");
	}
}
