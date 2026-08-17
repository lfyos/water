package kernel_scene;

import kernel_file_manager.file_reader;
import kernel_common_class.tree_string_search_container;

public class part_type_string_sorter extends tree_string_search_container<String>
{
	public part_type_string_sorter(String file_name[],String type_string,String file_system_charset)
	{
		String str;
		if(file_name!=null)
			for(int i=0,ni=file_name.length;i<ni;i++)
				for(file_reader f=new file_reader(file_name[i],file_system_charset);;) {
					if(f.eof()) {
						f.close();
						break;
					}
					str=f.get_string();
					add(str,str,false);
				}
		if(type_string!=null)
			for(int index_id;type_string.length()>0;)
				if((index_id=type_string.indexOf(";"))==0)
					type_string=type_string.substring(1);
				else if(index_id>0) {
					str=type_string.substring(0,index_id);
					add(str,str,false);
					type_string=type_string.substring(index_id+1);
				}else{
					add(type_string,type_string,false);
					break;
				}
	}
}
