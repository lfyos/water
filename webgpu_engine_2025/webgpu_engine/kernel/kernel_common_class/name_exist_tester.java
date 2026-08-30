package kernel_common_class;

import kernel_file_manager.file_reader;

public class name_exist_tester extends tree_string_search_container<String>
{
	public boolean test_exist(String name)
	{
		return (search_tree_node(name)!=null);
	}
	public void add(String name)
	{
		add(name,name);
	}
	public String[] name_array()
	{
		int index_id=0;
		var tree_node_collection=tree_get_node_collection();
		String ret_val[]=new String[tree_node_collection.size()];
		for(var tree_node:tree_node_collection)
			ret_val[index_id++]=tree_node.key;
		return ret_val;
	}
	public name_exist_tester()
	{
		super(null);
	}
	public name_exist_tester(String file_name[],String type_string,String file_system_charset)
	{
		super(null);
		
		String str;
		if(file_name!=null)
			for(int i=0,ni=file_name.length;i<ni;i++)
				for(file_reader f=new file_reader(file_name[i],file_system_charset);;) {
					if(f.eof()) {
						f.close();
						break;
					}
					str=f.get_string();
					add(str,str);
				}
		if(type_string!=null)
			for(int index_id;type_string.length()>0;)
				if((index_id=type_string.indexOf(";"))==0)
					type_string=type_string.substring(1);
				else if(index_id>0) {
					str=type_string.substring(0,index_id);
					add(str,str);
					type_string=type_string.substring(index_id+1);
				}else{
					add(type_string,type_string);
					break;
				}
	}
}
