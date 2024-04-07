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
		if(file_name.compareTo("movement.assemble")!=0)
			return;
		file_reader fr=new file_reader(directory_name+"\\bridge_color\\root.assemble","GBK");
		if(fr.eof()) {
			fr.close();
			return;
		}
		
		file_writer fw=new file_writer(directory_name+"\\root.assemble","GBK");
		fw.println("/*	1:name	*/	user_root_component");
		fw.println("/*	1:type	*/	undefined_part");
		fw.println("/*	1:location		*/	1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0");
		fw.println();
		
		for(int index_id;!(fr.eof());) {
			String str=fr.get_line();
			if(str==null)
				continue;
			if(str.indexOf("part_list")<0)
				continue;
			if(str.indexOf("mount")<0)
				continue;
			
			if((index_id=str.indexOf("\\element\\"))<0) 
				continue;
			while(true) {
				switch(str.charAt(index_id)) {
				case '/':
				case '\\':
				case '.':
					index_id--;
					continue;
				default:
					break;
				}
				str=str.substring(index_id+4).trim();
				fw.println("		mount		",str);
				break;
			}
		}
		
		fw.println();
		fw.println("/*	1:child_number	*/	0");
		
		fw.close();
		fr.close();
		
		file_writer.file_delete(directory_name+"\\bridge_color");
		file_writer.file_delete(directory_name+"\\bridge_green");
		file_writer.file_delete(directory_name+"\\bridge_grey");
		
		number++;
		debug_information.println(number+"	"+directory_name);
		debug_information.println(number+"	"+path_name);
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
		
		new test_4().do_travel("E:\\project_data\\user_scene\\83", true);

		debug_information.println("End");
	}
}
