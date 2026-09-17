package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_writer;
public class test_5 
{
	public static void main(String args[])
	{
		debug_information.println("start");
		file_writer fw=new file_writer("G:\\temp\\y.mesh","GBK");
		new kernel_part_mesh_convert.part_rude_2021_07_15("G:\\temp\\x.mesh","GBK").write_out(fw);
		fw.close();
		debug_information.println("end");
	}
}
