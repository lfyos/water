package kernel_part;

import kernel_file_manager.file_reader;
import kernel_interface.client_process_bar;
import kernel_common_class.tree_string_search_container;

public class buffer_object_file_modify_time_and_length_container 
	extends tree_string_search_container<buffer_object_file_modify_time_and_length>
{
	public void load(client_process_bar process_bar,String process_title,
			String my_boftal_file_name,String boftal_file_charset)
	{
		file_reader fr	=new file_reader(my_boftal_file_name,boftal_file_charset);
		int number		=fr.get_int();

		if(process_bar!=null)
			process_bar.set_process_bar(true,process_title,"",0,number);
		for(int i=0;i<number;i++) {
			String my_key=fr.get_string();
			if(fr.eof()||(my_key==null))
				break;
			if(process_bar!=null)
				process_bar.set_process_bar(false,process_title,my_key,i,number);
			add(new String[]{my_key},new buffer_object_file_modify_time_and_length(fr));
		}
		if(process_bar!=null)
			process_bar.set_process_bar(true,process_title,"",number,number);
		fr.close();
	}
}
