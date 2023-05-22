package kernel_part;

import java.util.ArrayList;

import kernel_common_class.sorter;

public class part_container_for_part_search extends sorter<part,String>
{
	private ArrayList<part>append_part_array;
	
	public void append_one_part(part new_part)
	{
		if(new_part!=null)
			append_part_array.add(new_part);
	}
	public void execute_append()
	{
		int append_part_number,old_part_number;
		if((append_part_number=append_part_array.size())<=0)
			return;
		
		part_container_for_part_search pcps;
		pcps=new part_container_for_part_search(append_part_array);
		append_part_array.clear();
		
		if((old_part_number=get_number())<=0) {
			data_array=pcps.data_array;
			return;
		}
		part old_data_array[]=data_array;
		data_array=new part[old_part_number+append_part_number];
		for(int old_part_pointer=0,append_part_pointer=0,new_pointer=0;;) {
			if(old_part_pointer>=old_part_number) {
				if(append_part_pointer>=append_part_number)
					break;
				data_array[new_pointer++]=pcps.data_array[append_part_pointer++];
			}else if(append_part_pointer>=append_part_number)
				data_array[new_pointer++]=old_data_array[old_part_pointer++];
			else if(compare_data(old_data_array[old_part_pointer],pcps.data_array[append_part_pointer])<=0)
				data_array[new_pointer++]=old_data_array[old_part_pointer++];
			else
				data_array[new_pointer++]=pcps.data_array[append_part_pointer++];
		}
	}
	
	public void destroy()
	{
		super.destroy();
		append_part_array=null;
	}
	public int compare_key(part s,String t)
	{
		return s.system_name.compareTo(t);
	}
	public int compare_data(part pi,part pj)
	{
		int result;
		
		if((result=pi.system_name.compareTo(pj.system_name))<0)
			return -5;
		if(result>0)
			return 5;
		
		if(pi.part_par.discard_precision2<pj.part_par.discard_precision2)
			return 4;
		if(pi.part_par.discard_precision2>pj.part_par.discard_precision2)
			return -4;

		if(pi.part_par.bottom_box_discard_precision2<pj.part_par.bottom_box_discard_precision2)
			return 3;
		if(pi.part_par.bottom_box_discard_precision2>pj.part_par.bottom_box_discard_precision2)
			return -3;
		
		boolean i_flag,j_flag;
		
		i_flag=pi.is_normal_part();
		j_flag=pj.is_normal_part();
		if(i_flag^j_flag)
			return i_flag?-2:2;
		
		i_flag=pi.is_bottom_box_part();
		j_flag=pj.is_bottom_box_part();
		if(i_flag^j_flag)
			return i_flag?-1:1;
		
		i_flag=pi.is_top_box_part();
		j_flag=pj.is_top_box_part();
		if(i_flag^j_flag)
			return i_flag?-1:1;
		
		return 0;
	}
	public part_container_for_part_search(ArrayList<part> my_parts)
	{
		super(my_parts.toArray(new part[my_parts.size()]));
		
		for(int i=0,j=0,id=0,n=get_number();i<n;){
			for(id=i,j=i;j<n;j++){
				if(data_array[id].system_name.compareTo(data_array[j].system_name)!=0)
					break;
				if(data_array[id].part_par.assembly_precision2>data_array[j].part_par.assembly_precision2)
					id=j;
			}
			for(;i<j;i++)
				data_array[i].part_par.assembly_precision2=data_array[id].part_par.assembly_precision2;
		}
		append_part_array=new ArrayList<part>();
	}
	
	public ArrayList<part> search_part(String my_part_system_name)
	{
		if(append_part_array.size()>0)
			execute_append();
		
		int search_id[];
		if((search_id=range(my_part_system_name))==null)
			return null;
		
		ArrayList<part> ret_part=new ArrayList<part>();
		boolean top_flag=false,bottom_flag=false;
		for(int i=0,ni=search_id[1]-search_id[0]+1,j=search_id[0],k=0;i<ni;i++,j++) {
			if(data_array[j].is_bottom_box_part()){
				if(bottom_flag)
					continue;
				bottom_flag=true;
			}
			if(data_array[j].is_top_box_part()){
				if(top_flag)
					continue;
				top_flag=true;
			}
			ret_part.add(k++,data_array[j]);
		}
		return (ret_part.size()<=0)?null:ret_part;
	}
}