package kernel_maintain;

import kernel_common_class.debug_information;
import old_convert.gltf_converter;

public class test_3 {
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		new gltf_converter(
				"E:\\1010-D-016º‰≈≈πﬁ.gltf",
				"c:\\temp\\",
				"UTF-8","GBK");

		debug_information.println("End");
	}
}
