package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_file_manager.travel_through_directory;

public class test_4 extends travel_through_directory
{
	private int number;
	
	public void operate_directory_and_file(String directory_name,String file_name,String path_name)
	{
		if(file_name.compareTo("root.assemble")!=0)
			return;
		
		String replace_str="ÉÏµæÁ»3_4_4²ãµæÁº_ÁùËÄÁ»_0m-32m.SLDASM.assemble";
		String target_str ="ÉÏµæÁ»3_4_5²ãµæÁº_²ğ×°Ê½Á»_40m-64m_ËÄÆ¬Áº¶Ë.SLDASM.assemble";
		
		String text_str=file_reader.get_text(path_name,"GBK");
		
		if(text_str.indexOf(replace_str)<0)
			debug_information.println((++number)+":"+"NO find string:	"+path_name);
		else{
			text_str=text_str.replace(replace_str,target_str);
		
			new file_writer(path_name,"GBK").println(text_str).close();
		
			debug_information.println((++number)+":"+"write file:	"+path_name);
		}
	}
	public test_4()
	{
		super(new String[]
		{
		});
		number=0;
	}
	public static void main(String args[])
	{
		debug_information.println("Begin:");
		
		new test_4().do_travel("E:\\project_data\\user_scene\\65\\65_5_6\\bridge_40m_64m", true);

		debug_information.println("End");
	}
}
