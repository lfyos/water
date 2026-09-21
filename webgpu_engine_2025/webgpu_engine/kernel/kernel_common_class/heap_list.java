package kernel_common_class;

import java.util.ArrayList;
import java.util.Comparator;

public class heap_list<DATA> 
{
	private Comparator<DATA>comparator;
	private ArrayList<DATA> data_list;
	
	private void adjust(int this_id)
	{
		for(int parent_id;this_id>0;this_id=parent_id){
			parent_id=(this_id-1)/2;
			DATA this_data	=data_list.get(this_id);
			DATA parent_data=data_list.get(parent_id);
			if(comparator.compare(parent_data,this_data)<=0)
				break;
			data_list.set(parent_id,this_data);
			data_list.set(this_id,parent_data);
		}
		int child_id,left_id,right_id,data_number=data_list.size();
		for(;(left_id=this_id+this_id+1)<data_number;this_id=child_id){
			DATA child_data,this_data=data_list.get(this_id);
			if((right_id=left_id+1)>=data_number){
				child_id=left_id;
				child_data=data_list.get(left_id);
			}else{
				DATA left_data=data_list.get(left_id);
				DATA right_data=data_list.get(right_id);
				if(comparator.compare(left_data,right_data)<=0){
					child_id=left_id;
					child_data=left_data;
				}else {
					child_id=right_id;
					child_data=right_data;
				}
			}
			if(comparator.compare(this_data,child_data)<=0)
				break;
			data_list.set(child_id,this_data);
			data_list.set(this_id,child_data);
		}
	}
	public int size()
	{
		return data_list.size();
	}
	public ArrayList<DATA> get_data_list()
	{
		return data_list;
	}
	public DATA get_data(int index_id)
	{
		return ((index_id<0)||(index_id>=data_list.size()))?null:(data_list.get(index_id));
	}
	public void insert_data(DATA my_data)
	{
		int index_id=data_list.size();
		data_list.add(index_id,my_data);
		adjust(index_id);
	}
	public DATA extract_data(boolean not_delete_flag)
	{
		int last_id;
		if((last_id=data_list.size()-1)<0)
			return null;
		DATA return_data=data_list.get(0);
		if(not_delete_flag)
			return return_data;
		DATA last_data=data_list.remove(last_id);
		if(last_id==0)
			return return_data;
		data_list.set(0,last_data);
		adjust(0);
		return return_data;
	}
	public heap_list(Comparator<DATA> my_comparator)
	{
		comparator	=my_comparator;
		data_list	=new ArrayList<DATA>();
	}
}
