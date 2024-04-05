package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_file_manager.travel_through_directory;

public class test_3 extends travel_through_directory
{
	public void operate_directory_and_file(String directory_name,String file_name,String path_name)
	{
		if(file_name.compareTo("root.assemble")!=0)
			return;
		int index_id=directory_name.lastIndexOf('\\');
		String height_str=directory_name.substring(0,index_id);
		index_id=height_str.lastIndexOf('\\');
		height_str=height_str.substring(index_id+1);
		
		String text_str=file_reader.get_text(path_name,"GBK");
		if((index_id=text_str.indexOf("30.SLDASM.assemble"))<0)
			return;
		String pre_str=text_str.substring(0,index_id);
		text_str=text_str.substring(index_id+2);
		if(height_str.charAt(height_str.length()-1)=='0')
			height_str=height_str.substring(0,height_str.lastIndexOf('.'));
		text_str=pre_str+height_str+text_str;
		
		debug_information.println(path_name);
		new file_writer(path_name,"GBK").print(text_str).close();
	}
	public test_3()
	{
		super(new String[]
		{
		});
	}
	public static void main(String args[])
	{
		debug_information.println("Begin:");
		
		new test_3().do_travel("E:\\project_data\\user_scene\\65\\65_5_6\\64_0m-32m", true);

		debug_information.println("End");
	}
}
