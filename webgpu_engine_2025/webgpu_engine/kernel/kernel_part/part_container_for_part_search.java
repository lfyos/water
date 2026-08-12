package kernel_part;

import java.util.ArrayList;
import java.util.Comparator;

import kernel_common_class.const_value;
import kernel_common_class.tree_search_container;

class part_comparator_for_part_search implements Comparator<part>
{
	public int compare(part pi,part pj)
	{
		int result;
		double diff;
		
		if((result=pi.system_name.compareTo(pj.system_name))<0)
			return -5;
		if(result>0)
			return 5;
		
		diff=pi.part_par.discard_precision2-pj.part_par.discard_precision2;
		if(Math.abs(diff)>const_value.min_value)
			return (diff<0.0)?4:-4;
		
		diff=pi.part_par.bottom_box_discard_precision2-pj.part_par.bottom_box_discard_precision2;
		if(Math.abs(diff)>const_value.min_value)
			return (diff<0.0)?3:-3;
		
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
}

public class part_container_for_part_search extends tree_search_container<part,part>
{
	public ArrayList<part>get_part_list()
	{
		var data_list=tree_get_tree_value_list();
		
		for(int i=0,j=0,id=0,n=data_list.size();i<n;){
			part id_part,j_part;
			for(id=i,j=i;j<n;j++){
				id_part	=data_list.get(id);
				j_part	=data_list.get(j);
				if(id_part.system_name.compareTo(j_part.system_name)!=0)
					break;
				if(id_part.part_par.assembly_precision2>j_part.part_par.assembly_precision2)
					id=j;
			}
			id_part=data_list.get(id);
			for(;i<j;i++)
				data_list.get(i).part_par.assembly_precision2=id_part.part_par.assembly_precision2;
		}
		
		return data_list;
	}
	public void destroy()
	{
		super.clear();
	}
	public part_container_for_part_search(ArrayList<part> my_part_list)
	{
		super(new part_comparator_for_part_search());
		if(my_part_list!=null)
			for(var my_part:my_part_list)
				add(my_part,my_part);
	}
	public ArrayList<part> search_part(String my_part_system_name)
	{
		var data_list=get_part_list();
		
		for(int begin_pointer=0,end_pointer=data_list.size()-1;;){
			if(begin_pointer>end_pointer)
				return new ArrayList<part>();
				
			int search_id=(begin_pointer+end_pointer)/2;
			int result=data_list.get(search_id).system_name.compareTo(my_part_system_name);
			if(result<0) {
				begin_pointer=search_id+1;
				continue;
			}
			if(result>0) {
				end_pointer=search_id-1;
				continue;
			}
			for(begin_pointer=search_id;;) {
				if(data_list.get(begin_pointer).system_name.compareTo(my_part_system_name)!=0) {
					begin_pointer++;
					break;
				}else if(begin_pointer>0)
					begin_pointer--;
				else
					break;
			}
			int last_pointer=data_list.size()-1;
			for(end_pointer=search_id;;) {
				if(data_list.get(end_pointer).system_name.compareTo(my_part_system_name)!=0) {
					end_pointer--;
					break;
				}else if(end_pointer<last_pointer)
					end_pointer++;
				else
					break;
			}
			ArrayList<part> ret_part=new ArrayList<part>();
			boolean top_flag=false,bottom_flag=false;
			for(int i=begin_pointer,ni=end_pointer;i<=ni;i++){
				part my_part=data_list.get(i);
				if(my_part.is_bottom_box_part()){
					if(bottom_flag)
						continue;
					bottom_flag=true;
				}
				if(my_part.is_top_box_part()){
					if(top_flag)
						continue;
					top_flag=true;
				}
				ret_part.add(my_part);
			}
			return ret_part;
		}
	}
}