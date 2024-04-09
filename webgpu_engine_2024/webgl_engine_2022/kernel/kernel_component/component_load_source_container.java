package kernel_component;

import java.io.File;
import java.util.ArrayList;
import java.nio.charset.Charset;

import kernel_file_manager.file_reader;

public class component_load_source_container 
{
	private ArrayList<component_load_source_item> list;
	
	public component_load_source_container()
	{
		list=new ArrayList<component_load_source_item>();
	}
	public component_load_source_container(component_load_source_container clsc)
	{
		list=new ArrayList<component_load_source_item>();
		for(int i=0,ni=clsc.list.size();i<ni;i++)
			list.add(i,clsc.list.get(i));
	}
	public void destroy()
	{
		if(list!=null) {
			list.clear();
			list=null;
		}
	}
	public component[] get_source_item(String component_name,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		for(int last_id=list.size()-1,begin_pointer=0,end_pointer=last_id;begin_pointer<=end_pointer;){
			int middle_pointer=(begin_pointer+end_pointer)/2;
			int compare_result=list.get(middle_pointer).component_name.compareTo(component_name);
			if(compare_result<0)
				begin_pointer=middle_pointer+1;
			else if(compare_result>0)
				end_pointer=middle_pointer-1;
			else {
				for(begin_pointer=middle_pointer;begin_pointer>0;begin_pointer--)
					if(list.get(begin_pointer-1).component_name.compareTo(component_name)!=0)
						break;
				for(end_pointer=middle_pointer;end_pointer<last_id;end_pointer++)
					if(list.get(end_pointer+1).component_name.compareTo(component_name)!=0)
						break;
				component ret_val[]=new component[end_pointer-begin_pointer+1];
				for(int i=0,ni=ret_val.length;i<ni;i++){
					component_load_source_item p=list.remove(begin_pointer);
					file_reader fr=new file_reader(
						p.component_file_name,p.component_file_charset);
					ret_val[i]=new component(p.token_string,fr,
						part_list_flag,normalize_location_flag,ccp);
					fr.close();
				}
				return ret_val;
			}
		}
		return null;
	}
	public void add_source_item(String component_name,String token_string,
					String component_file_name,String component_file_charset)
	{
		if(component_name==null)
			return;
		if(!(new File(component_file_name).exists()))
			return;
		if(component_file_charset==null)
			component_file_charset=Charset.defaultCharset().name();
		
		int insert_pointer;
		for(insert_pointer=list.size()-1;insert_pointer>=0;insert_pointer--)
			if(list.get(insert_pointer).component_name.compareTo(component_name)<=0)
				break;
		
		component_load_source_item clsi=new component_load_source_item(
			component_name,token_string,component_file_name,component_file_charset);
		list.add(insert_pointer+1,clsi);
	}
	public int get_source_item_number()
	{
		return list.size();
	}
}
