package kernel_common_class;

import java.util.ArrayList;

import kernel_file_manager.file_reader;

public class change_name extends tree_string_search_container<String>
{
	public String search_change_name(String my_search_name,String fail_result)
	{	
		ArrayList<String> search_result;
		if((search_result=search(new String[]{my_search_name}))==null)
			return fail_result;
		if(search_result.size()<=0)
			return fail_result;
		return search_result.get(0);
	}
	public void append(change_name my_change_name,boolean do_reversion_flag)
	{
		if(my_change_name!=null){
			ArrayList<tree_search_container_tree_node<String[],String>>list;
			list=my_change_name.tree_get_tree_node_list();
			for(int i=0,ni=list.size();i<ni;i++) {
				tree_search_container_tree_node<String[],String> my_tree_node=list.get(i);
				for(int j=0,nj=my_tree_node.list.size();j<nj;j++)
					if(do_reversion_flag)
						add(new String[] {my_tree_node.list.get(j)},my_tree_node.key[0]);
					else
						add(my_tree_node.key,my_tree_node.list.get(j));
			}
		}
	}
	private void init(common_reader f_array[],String change_string)
	{
		if(f_array!=null)
			for(int i=0,ni=f_array.length;i<ni;i++)
				while(!(f_array[i].eof())){
					String my_key=f_array[i].get_string();
					String my_value=f_array[i].get_line();
					if((my_key==null)||(my_value==null))
						continue;
					if((my_key=my_key.trim()).length()<=0)
						continue;
					my_value=my_value.trim();
					add(new String[] {my_key},my_value);
				}
		if(change_string!=null)
			while(change_string.length()>0){
				int index_id=change_string.indexOf(";");
				String my_str=(index_id<0)?change_string:(change_string.substring(0,index_id));
				change_string=(index_id<0)?"":(change_string.substring(index_id+1));
				if((index_id=my_str.indexOf(":"))>0){
					String my_key=my_str.substring(0,index_id).trim();
					String my_value=my_str.substring(index_id+1).trim();
					add(new String[] {my_key},my_value);
				}
			}
	}
	public change_name()
	{
	}
	public change_name(common_reader f_array[],String change_string)
	{
		init(f_array,change_string);
	}
	public change_name(String change_file_name[],String change_string,String file_system_charset)
	{
		file_reader f_array[]=null;
		if(change_file_name!=null) {
			f_array=new file_reader[change_file_name.length];
			for(int i=0,ni=f_array.length;i<ni;i++)
				f_array[i]=new file_reader(change_file_name[i],file_system_charset);
		}
		init(f_array,change_string);
		if(f_array!=null)
			for(int i=0,ni=f_array.length;i<ni;i++)
				f_array[i].close();
	}
	public change_name(change_name my_change_name,boolean do_reversion_flag)
	{
		append(my_change_name,do_reversion_flag);
	}
}
