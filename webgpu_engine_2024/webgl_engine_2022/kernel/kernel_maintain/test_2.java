package kernel_maintain;

import kernel_common_class.debug_information;

public class test_2 
{
	public static void main(String args[])
	{
		debug_information.println("Begin:");
		
		format_convert.cadex_converter.do_convert(
				"E:\\tmp\\plant_modeling.obj","E:\\temp\\", 
				null,null,0.01,0.01,50);
		
		debug_information.println("End");
	}
}
