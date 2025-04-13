package kernel_common_class;

import java.util.ArrayList;

import kernel_file_manager.file_reader;

public class change_name extends sorter<String[],String>
{
	public void destroy()
	{
		super.destroy();
	}
	public int compare_data(String s[],String t[])
	{
		return s[0].compareTo(t[0]);
	}
	public int compare_key(String s[],String t)
	{
		return s[0].compareTo(t);
	}
	public String get_search_result(int search_id,String fail_result)
	{	
		return ((search_id>=0)&&(search_id<data_list.size()))?(data_list.get(search_id)[1]):fail_result;
	}
	public String search_change_name(String my_search_name,String fail_result)
	{	
		int search_id=search(my_search_name);
		return ((search_id>=0)&&(search_id<data_list.size()))?(data_list.get(search_id)[1]):fail_result;
	}
	public void insert(String t[])
	{
		data_list.add(t);
		for(int i=data_list.size()-1;i>0;i--) {
			String this_str[]=data_list.get(i);
			String pre_str[]=data_list.get(i-1);
			if(compare_data(pre_str,this_str)<=0)
				break;
			data_list.set(i,pre_str);
			data_list.set(i-1,this_str);
		}
		return;
	}
	public void delete(int id)
	{
		data_list.remove(id);
	}
	public void append(change_name a)
	{
		if(a!=null){
			for(int i=0,ni=a.data_list.size();i<ni;i++)
				data_list.add(a.data_list.get(i));
			do_sort();
		}
	}
	private void init(common_reader f_array[],String change_string)
	{
		String change_pair[];
		data_list=new ArrayList<String[]>();
		
		if(f_array!=null)
			for(int i=0,ni=f_array.length;i<ni;i++)
				while(!(f_array[i].eof())){
					change_pair=new String[]{f_array[i].get_string(),f_array[i].get_line()};
					if((change_pair[0]==null)||(change_pair[1]==null))
						continue;
					if((change_pair[0]=change_pair[0].trim()).length()<=0)
						continue;
					change_pair[1]=change_pair[1].trim();
					data_list.add(change_pair);
				}

		if(change_string!=null)
			while(change_string.length()>0){
				int index_id=change_string.indexOf(";");
				String my_str=(index_id<0)?change_string:(change_string.substring(0,index_id));
				change_string=(index_id<0)?"":(change_string.substring(index_id+1));
				if((index_id=my_str.indexOf(":"))>0){
					change_pair=new String[]{my_str.substring(0,index_id).trim(),my_str.substring(index_id+1).trim()};
					data_list.add(change_pair);
				}
			}
		do_sort();
	}
	
	public change_name()
	{
	}
	public change_name(common_reader f_array[],String change_string)
	{
		init(f_array,change_string);
		do_sort();
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
		
		do_sort();
	}
	public change_name(change_name cn,boolean do_reversion_flag)
	{
		data_list=new ArrayList<String[]>();
		for(int i=0,ni=cn.data_list.size();i<ni;i++) {
			String p[]=cn.data_list.get(i);
			data_list.add(new String[]
			{
				do_reversion_flag?new String(p[1]):new String(p[0]),
				do_reversion_flag?new String(p[0]):new String(p[1]),
			});
		}
		do_sort();
	}
}
