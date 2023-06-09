package kernel_maintain;

import kernel_common_class.debug_information;

public class test_2 
{
	public static void main(String args[])
	{
		debug_information.println("Begin:");
		
		new format_convert.inp_converter(
				"F:\\water_all\\data\\project\\part\\other_part\\part_inp\\part_1\\part.inp", null,
				"F:\\temp\\tmp.mesh", null);
		
		debug_information.println("End");
	}
}
