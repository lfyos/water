package kernel_maintain;

import kernel_common_class.debug_information;
public class test_5
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		convert_old_part_rude_2021_07_01.part_rude.convert("e:\\x.mesh","GBK");
		
		debug_information.println("End");
	}
}