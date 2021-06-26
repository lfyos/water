package kernel_maintain;

import java.io.File;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class test_6 
{
	private static boolean  flag=true;
	
	private static void f3(String file_name)
	{
		if(new File(file_name+".bak").exists()) {
			file_writer.file_delete(file_name);
			file_writer.file_rename(file_name+".bak", file_name);
		}
	}
	private static void f2(String file_name)
	{
		file_reader fr=new file_reader(file_name,"GBK");
		file_writer fw=new file_writer(file_name+".bak","GBK");
		
		fw.println("/*	1:name	*/	system_root_component");
		fw.println("/*	1:type	*/	system_root_part");
		fw.println("/*	1:location	*/	1	0	0	0	0	1	0	0	0	0	1	0	0	0	0	1");
		
		while(!(fr.eof())){
			boolean part_list_flag=fr.get_boolean();
			fr.get_boolean();
			if((file_name=fr.get_string())==null)
				continue;
			if(file_name.trim().length()<=0)
				continue;
			fw.print  ("		",				part_list_flag?"part_list	":"not_part_list");
			fw.println("		mount		",	file_name);
		}
		fw.println("/*	1:child_number	*/	0");
		
		fw.close();
		fr.close();
		
		debug_information.println(fr.directory_name+fr.file_name);
	}
	private static void f1(String file_name)
	{
		file_reader fr=new file_reader(file_name,"GBK");
		while(!(fr.eof())){
			fr.get_string();
			fr.get_string();
			if((file_name=fr.get_string())==null)
				continue;
			if((file_name=file_name.trim()).length()<=0)
				continue;
			fr.get_string();
			fr.get_string();
			fr.get_string();
			fr.get_string();
			
			if(flag)
				f2(fr.directory_name+file_reader.separator(file_name));
			else
				f3(fr.directory_name+file_reader.separator(file_name));
		}
		fr.close();
	}
	public static void main(String args[])
	{
		debug_information.println("Begin");
		String file_name="E:\\water_all\\data\\users\\users\\NoName\\assemble.txt";
		file_reader fr=new file_reader(file_name,"GBK");
		while(!(fr.eof())){
			if((file_name=fr.get_string())==null)
				continue;
			if((file_name=file_name.trim()).length()<=0)
				continue;
			f1(fr.directory_name+file_reader.separator(file_name));
		}
		
		fr.close();
		
		debug_information.println("End");
	}
}
