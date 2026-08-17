package kernel_part;

import java.util.ArrayList;
import java.util.Comparator;

import kernel_common_class.const_value;
import kernel_common_class.tree_search_container;

class part_comparator_for_part_search implements Comparator<part>
{
	public int compare(part pi,part pj)
	{
		int compare_result;
		double diff;
		
		if((compare_result=pi.system_name.compareTo(pj.system_name))<0)
			return -6;
		if(compare_result>0)
			return 6;
		
		diff=pi.part_par.discard_precision2-pj.part_par.discard_precision2;
		if(Math.abs(diff)>const_value.min_value)
			return (diff<0.0)?5:-5;
		
		diff=pi.part_par.bottom_box_discard_precision2-pj.part_par.bottom_box_discard_precision2;
		if(Math.abs(diff)>const_value.min_value)
			return (diff<0.0)?4:-4;
		
		boolean i_flag,j_flag;
		
		i_flag=pi.is_normal_part();
		j_flag=pj.is_normal_part();
		if(i_flag^j_flag)
			return i_flag?-3:3;
		
		i_flag=pi.is_bottom_box_part();
		j_flag=pj.is_bottom_box_part();
		if(i_flag^j_flag)
			return i_flag?-2:2;
		
		i_flag=pi.is_top_box_part();
		j_flag=pj.is_top_box_part();
		if(i_flag^j_flag)
			return i_flag?-1:1;
		
		return 0;
	}
}
public class part_container_for_part_search extends tree_search_container<part,part>
{
	public void destroy()
	{
		super.destroy();
	}
	public void reset_assembly_precision()
	{
		ArrayList<part> search_part_list=tree_get_value_list();
		for(int i=0,j=0,n=search_part_list.size();i<n;){
			part min_precision_part=search_part_list.get(i);
			for(j=i+1;j<n;j++){
				part j_part=search_part_list.get(j);
				if(min_precision_part.system_name.compareTo(j_part.system_name)!=0)
					break;
				if(min_precision_part.part_par.assembly_precision2>j_part.part_par.assembly_precision2)
					min_precision_part=j_part;
			}
			for(double new_precision=min_precision_part.part_par.assembly_precision2;i<j;i++)
				search_part_list.get(i).part_par.assembly_precision2=new_precision;
		}
	}
	public part_container_for_part_search(ArrayList<part> my_part_list)
	{
		super(new part_comparator_for_part_search());
		if(my_part_list!=null)
			for(var my_part:my_part_list)
				add(my_part,my_part,false);
	}
	public ArrayList<part> search_part(String my_part_system_name)
	{
		ArrayList<part> search_part_list=tree_get_value_list();
		
		for(int begin_pointer=0,end_pointer=search_part_list.size()-1;;){
			if(begin_pointer>end_pointer)
				return new ArrayList<part>();

			int search_id=(begin_pointer+end_pointer)/2;
			int result=search_part_list.get(search_id).system_name.compareTo(my_part_system_name);
			if(result<0) {
				begin_pointer=search_id+1;
				continue;
			}
			if(result>0) {
				end_pointer=search_id-1;
				continue;
			}
			for(begin_pointer=search_id;;) {
				if(search_part_list.get(begin_pointer).system_name.compareTo(my_part_system_name)!=0) {
					begin_pointer++;
					break;
				}else if(begin_pointer>0)
					begin_pointer--;
				else
					break;
			}
			int last_pointer=search_part_list.size()-1;
			for(end_pointer=search_id;;) {
				if(search_part_list.get(end_pointer).system_name.compareTo(my_part_system_name)!=0) {
					end_pointer--;
					break;
				}else if(end_pointer<last_pointer)
					end_pointer++;
				else
					break;
			}
			ArrayList<part> return_part_list=new ArrayList<part>();
			for(int i=begin_pointer,ni=end_pointer;i<=ni;i++)
				return_part_list.add(search_part_list.get(i));
			return return_part_list;
		}
	}
}