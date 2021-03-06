package kernel_maintain;

import java.io.File;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class test_1 
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		file_reader fr=new file_reader("e:\\list.txt","GBK");
		
		int i=0;
		while(!(fr.eof())) {
			String str=fr.get_line();
			if(str==null)
				continue;
			if((str=str.trim()).length()<=0)
				continue;
						
			String s="E:\\water_all\\data\\project\\"+str;
			String d="E:\\temp\\"+str;
			
			if(new File(s).exists()){
				file_writer.file_copy(s, d);
				debug_information.println(i+"From:	",s);
				debug_information.println(i+"TO  :	",d);
				debug_information.println();
			}
			i++;
		}
		debug_information.println("i=",i);
		
		fr.close();
		
		debug_information.println("End");
	}
}
