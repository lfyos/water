package kernel_part;

import java.util.ArrayList;
import java.util.Comparator;

import kernel_common_class.const_value;
import kernel_common_class.tree_string_search_container;

class part_value_comparator_for_part_search implements Comparator<part>
{
	public int compare(part pi,part pj)
	{
		int name_compare_value;
		if((name_compare_value=pi.system_name.compareTo(pj.system_name))!=0)
			return name_compare_value;
		
		double diff;
		
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

public class part_container_for_part_search extends tree_string_search_container<part>
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
		super(new part_value_comparator_for_part_search());
		
		if(my_part_list!=null)
			for(var my_part:my_part_list)
				add(my_part.system_name,my_part);
	}
}