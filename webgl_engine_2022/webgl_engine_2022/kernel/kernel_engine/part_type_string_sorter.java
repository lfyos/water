package kernel_engine;

import java.util.ArrayList;

import kernel_common_class.sorter;
import kernel_file_manager.file_reader;

public class part_type_string_sorter extends sorter<String,String>
{
	public int compare_data(String s,String t)
	{
		return s.compareTo(t);
	}
	public int compare_key(String s,String t)
	{
		return s.compareTo(t);
	}
	public part_type_string_sorter(String file_name[],String type_string,String file_system_charset)
	{
		int index_id;
		ArrayList<String> list=new ArrayList<String>();
		
		if(file_name!=null)
			for(int i=0,ni=file_name.length;i<ni;i++)
				for(file_reader f=new file_reader(file_name[i],file_system_charset);;) {
					if(f.eof()) {
						f.close();
						break;
					}
					list.add(f.get_string());
				}
		if(type_string!=null)
			while(type_string.length()>0)
				if((index_id=type_string.indexOf(";"))==0)
					type_string=type_string.substring(1);
				else if(index_id>0) {
					list.add(type_string.substring(0,index_id));
					type_string=type_string.substring(index_id+1);
				}else{
					list.add(type_string);
					break;
				}
		if(list.size()<=0)
			data_array=null;
		else if((data_array=list.toArray(new String[list.size()])).length>1)
			do_sort();
	}
}
