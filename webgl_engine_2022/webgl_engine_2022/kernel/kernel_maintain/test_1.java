package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.travel_through_directory;

public class test_1 extends travel_through_directory
{
	private static String match[]= {
			"encoder"
	};
	public void operate_file(String file_name)
	{
		String str;
		if((str=file_reader.get_text(file_name,"GBK"))==null)
			return;
		for(int index_id,i=0,ni=match.length;i<ni;i++)
			if((index_id=str.indexOf(match[i]))>=0) {
				index_id+=match[i].length();
				if(str.charAt(index_id)!='_') {
					debug_information.println(file_name);
					return;
				}
			}
	}
	
	public test_1()
	{
		super(new String[]
		{
//			"F:\\water_all\\data\\project",
//			"E:\\water_all\\webgl_engine_2022\\webgl_engine_2022\\build"
		});
	}
	public static void main(String args[])
	{
		debug_information.println("Step	1:");
//		new test_1().do_travel("F:\\water_all\\data\\", false);
		
//		debug_information.println("Step	2:");
		new test_1().do_travel("E:\\webgl_engine_2022\\webgl_engine_2022\\", false);

		debug_information.println("End");
	}
}
