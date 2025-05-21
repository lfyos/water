package kernel_common_class;

import java.util.ArrayList;

public class sorter <DATA_TYPE,KEY_TYPE>
{
	public	ArrayList<DATA_TYPE> data_list;
	private	ArrayList<DATA_TYPE> append_list;
	
	public void destroy()
	{
		for(int i=0,ni=data_list.size();i<ni;i++)
			data_list.set(i,null);
		data_list.clear();
		for(int i=0,ni=append_list.size();i<ni;i++)
			append_list.set(i,null);
		append_list.clear();
	}
	public int compare_data(DATA_TYPE s,DATA_TYPE t)
	{
		return 0;
	}
	public int compare_key(DATA_TYPE s,KEY_TYPE t)
	{
		return 0;
	}
	public int search(KEY_TYPE t)
	{
		for(int begin_pointer=0,end_pointer=data_list.size()-1;begin_pointer<=end_pointer;){
			int result,search_id=(begin_pointer+end_pointer)/2;
			if((result=compare_key(data_list.get(search_id),t))<0)
				begin_pointer=search_id+1;
			else if(result>0)
				end_pointer=search_id-1;
			else
				return search_id;
		}
		return -1;
	}
	public int[] range(KEY_TYPE t)
	{
		int begin_id,end_id,end_number,search_id;
		if((search_id=search(t))<0)
			return null;
		for(begin_id=search_id;begin_id>0;begin_id--)
			if(compare_key(data_list.get(begin_id-1),t)!=0)
				break;
		for(end_id=search_id,end_number=data_list.size()-1;end_id<end_number;end_id++)
			if(compare_key(data_list.get(end_id+1),t)!=0)
				break;
		return new int[]{begin_id,end_id};
	}
	public int get_number()
	{
		return data_list.size();
	}
	public void do_sort()
	{
		if(data_list==null) {
			data_list=new ArrayList<DATA_TYPE>();
			return;
		}
		int part_number=data_list.size();
		int id_array[]=new int[part_number];
		for(int i=0;i<part_number;i++)
			id_array[i]=i;

		for(int i=1;i<part_number;i++)
			for(int j=i,parent,result;j>0;j=parent){
				parent=(j-1)/2;
				if((result=compare_data(data_list.get(parent),data_list.get(j)))==0)
					result=id_array[parent]-id_array[j];
				if(result>=0)
					break;
				
				result=id_array[parent];
				id_array[parent]=id_array[j];
				id_array[j]=result;
				
				DATA_TYPE p=data_list.get(parent);
				data_list.set(parent,data_list.get(j));	
				data_list.set(j,p);
			}
		for(int i=part_number-1;i>0;i--){
			int result=id_array[i];
			id_array[i]=id_array[0];
			id_array[0]=result;
			
			DATA_TYPE p=data_list.get(i);
			data_list.set(i,data_list.get(0));
			data_list.set(0,p);
			
			for(int j=0,select_child,other_child;(select_child=j+j+1)<i;j=select_child){
				DATA_TYPE select_data=data_list.get(select_child);
				if((other_child=select_child+1)<i){
					DATA_TYPE other_data=data_list.get(other_child);
					if((result=compare_data(select_data,other_data))==0)
						result=id_array[select_child]-id_array[other_child];
					if(result<=0) {
						select_child=other_child;
						select_data=other_data;
					}
				}
				if((result=compare_data(p=data_list.get(j),select_data))==0)
					result=id_array[j]-id_array[select_child];
				if(result>=0)
					break;
				
				result=id_array[j];
				id_array[j]=id_array[select_child];
				id_array[select_child]=result;

				data_list.set(j,select_data);
				data_list.set(select_child,p);
			}
		}
	}
	public sorter()
	{
		append_list=new ArrayList<DATA_TYPE>();
		data_list=new ArrayList<DATA_TYPE>();
	}
	public sorter(ArrayList<DATA_TYPE> my_data_list)
	{
		data_list=new ArrayList<DATA_TYPE>();
		append_list=new ArrayList<DATA_TYPE>();
		if(my_data_list!=null)
			for(int i=0,ni=my_data_list.size();i<ni;i++)
				data_list.add(my_data_list.get(i));
		do_sort();
	}

	public void execute_append()
	{
		int append_part_number,old_part_number;
		if((append_part_number=append_list.size())<=0)
			return;
		
		ArrayList<DATA_TYPE> old_data_list=data_list;
		data_list=append_list;
		do_sort();
		
		if((old_part_number=old_data_list.size())<=0){
			append_list=new ArrayList<DATA_TYPE>();
			return;
		}
		append_list=data_list;
		data_list=new ArrayList<DATA_TYPE>();
		for(int old_part_pointer=0,append_part_pointer=0,new_pointer=0;;) {
			if(old_part_pointer>=old_part_number) {
				if(append_part_pointer>=append_part_number)
					break;
				data_list.add(new_pointer++,append_list.get(append_part_pointer++));
			}else if(append_part_pointer>=append_part_number)
				data_list.add(new_pointer++,old_data_list.get(old_part_pointer++));
			else {
				DATA_TYPE old_part=old_data_list.get(old_part_pointer);
				DATA_TYPE app_part=append_list.get(append_part_pointer);
				if(compare_data(old_part,app_part)<=0) {
					data_list.add(new_pointer++,old_part);
					old_part_pointer++;
				}else {
					data_list.add(new_pointer++,app_part);
					append_part_pointer++;
				}
			}
		}
		append_list.clear();
	}
	public void append(DATA_TYPE new_data)
	{
		if(new_data!=null)
			append_list.add(new_data);
	}
	public void append(ArrayList<DATA_TYPE> new_list)
	{
		if(new_list!=null)
			for(int i=0,ni=new_list.size();i<ni;i++)
				append_list.add(new_list.get(i));
	}
}