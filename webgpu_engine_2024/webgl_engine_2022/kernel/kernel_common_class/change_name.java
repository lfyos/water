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
	public String []get_target_name()
	{
		if(data_array==null)
			return null;
		String ret_val[]=new String[data_array.length];
		for(int i=0,ni=ret_val.length;i<ni;i++)
			ret_val[i]=data_array[i][1];
		return ret_val;
	}
	public String get_search_result(int search_id,String fail_result)
	{	
		return ((search_id>=0)&&(search_id<data_array.length))?(data_array[search_id][1]):fail_result;
	}
	public String search_change_name(String my_search_name,String fail_result)
	{	
		int search_id=search(my_search_name);
		return ((search_id>=0)&&(search_id<data_array.length))?(data_array[search_id][1]):fail_result;
	}
	public void insert(String t[])
	{
		String bak[][];
		if((bak=data_array)==null) {
			data_array=new String[][] {t};
			return;
		}
		int n=bak.length;
		data_array=new String[n+1][];
		for(int i=0;i<n;i++)
			data_array[i]=bak[i];
		
		for(int i=n-1;i>=0;i--)
			if(compare_data(t,data_array[i])<0)
				data_array[i+1]=data_array[i];
			else{
				data_array[i+1]=t;
				return;
			}
		data_array[0]=t;
		return;
	}
	public void delete(int id)
	{
		String bak[][];
		if((bak=data_array)==null)
			return;
		int n=bak.length;
		if((id<0)||(id>=n))
			return;
		data_array=new String[n-1][];
		for(int i=0;i<id;i++)
			data_array[i]=bak[i];
		for(int i=id+1;i<n;i++)
			data_array[i-1]=bak[i];
		return;
	}
	public void append(change_name a)
	{
		if(a==null)
			return;
		if(a.data_array==null)
			return;
		if(a.data_array.length<=0)
			return;
		if(data_array==null) {
			data_array=new String[a.data_array.length][];
			for(int i=0,ni=a.data_array.length;i<ni;i++)
				data_array[i]=new String[] {
					new String(a.data_array[i][0]),
					new String(a.data_array[i][1])
				};
			return;
		}
		String bak[][]=data_array;
		data_array=new String[a.data_array.length+bak.length][];
		for(int i=0,ni=bak.length;i<ni;i++)
			data_array[i]=bak[i];
		for(int i=0,j=bak.length,ni=a.data_array.length;i<ni;i++,j++)
			data_array[j]=new String[] {
					new String(a.data_array[i][0]),
					new String(a.data_array[i][1])
			};
		do_sort();
	}
	private void init(common_reader f_array[],String change_string)
	{
		String change_pair[];
		ArrayList<String[]>my_data_array=new ArrayList<String[]>();
		
		if(f_array!=null)
			for(int i=0,ni=f_array.length;i<ni;i++)
				while(!(f_array[i].eof())){
					change_pair=new String[]{f_array[i].get_string(),f_array[i].get_line()};
					if((change_pair[0]==null)||(change_pair[1]==null))
						continue;
					if((change_pair[0]=change_pair[0].trim()).length()<=0)
						continue;
					change_pair[1]=change_pair[1].trim();
					my_data_array.add(change_pair);
				}

		if(change_string!=null)
			while(change_string.length()>0){
				int index_id=change_string.indexOf(";");
				String my_str=(index_id<0)?change_string:(change_string.substring(0,index_id));
				change_string=(index_id<0)?"":(change_string.substring(index_id+1));
				if((index_id=my_str.indexOf(":"))>0){
					change_pair=new String[]{my_str.substring(0,index_id).trim(),my_str.substring(index_id+1).trim()};
					my_data_array.add(change_pair);
				}
			}
		int number=my_data_array.size();
		switch(number) {
		case 0:
			data_array=null;
			break;
		case 1:
			data_array=new String[][]{my_data_array.get(0)};
			break;
		default:
			data_array=my_data_array.toArray(new String[number][]);
			do_sort();
		}
	}
	
	public change_name()
	{
		init(null,null);
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
	public change_name(change_name cn,boolean do_reversion_flag)
	{
		if(cn.data_array==null) {
			data_array=null;
			return;
		}
		data_array=new String[cn.data_array.length][];
		for(int i=0,ni=cn.data_array.length;i<ni;i++)
			data_array[i]=new String[]
			{
				do_reversion_flag?new String(cn.data_array[i][1]):new String(cn.data_array[i][0]),
				do_reversion_flag?new String(cn.data_array[i][0]):new String(cn.data_array[i][1]),
			};
	}
}
