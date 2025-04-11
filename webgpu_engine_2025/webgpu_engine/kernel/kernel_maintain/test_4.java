package kernel_maintain;

import java.io.File;

import kernel_common_class.debug_information;

public class test_4 
{
	public static void main(String args[])
	{
		File f=new File("f://////temp/configure1.txt");
		debug_information.println(f.getAbsolutePath());
	}
}
