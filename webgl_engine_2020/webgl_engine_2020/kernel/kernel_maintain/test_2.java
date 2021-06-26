package kernel_maintain;

import kernel_common_class.debug_information;
import old_convert.cadex_converter;

public class test_2
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		cadex_converter.main(new String[]
		{
				"F:\\海科相关文件\\沉管隧道建模8.4\\",
				"两段相连的沉管.STEP",
				"E:\\cad\\sinked_pipe\\",
				"E:\\cad\\",
				"GBK",
				"0","0","100",
				"noexternal"
		});
		
		debug_information.println("End");
	}
}
