package kernel_part;

import java.util.ArrayList;

import kernel_common_class.const_value;
import kernel_common_class.sorter;

public class part_container_for_part_search extends sorter<part,String>
{
	public void destroy()
	{
		super.destroy();
	}
	public int compare_key(part s,String t)
	{
		return s.system_name.compareTo(t);
	}
	public int compare_data(part pi,part pj)
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
	public part_container_for_part_search(ArrayList<part> my_parts)
	{
		super(my_parts);
		
		for(int i=0,j=0,id=0,n=get_number();i<n;){
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
	}
	public ArrayList<part> search_part(String my_part_system_name)
	{
		execute_append();
		
		int search_id[];
		if((search_id=range(my_part_system_name))==null)
			return null;
		
		ArrayList<part> ret_part=new ArrayList<part>();
		boolean top_flag=false,bottom_flag=false;
		for(int i=search_id[0],ni=search_id[1];i<=ni;i++){
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