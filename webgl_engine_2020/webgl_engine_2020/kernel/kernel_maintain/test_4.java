package kernel_maintain;

import format_convert.cadex_converter;
import kernel_common_class.debug_information;

public class test_4 
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		cadex_converter.main(new String[]
		{
				"F:\\�����\\ģ��\\���׷��͹ܵ�ģ�Ͱ�װ-3D\\",
				"���׷��͹ܵ�ģ�Ͱ�װ-3D.stp",
				"C:\\temp\\",
				"E:\\cad\\",
				"GBK",
				"0","0","100",
				"external"
		});
		
		debug_information.println("End");
	}
}
