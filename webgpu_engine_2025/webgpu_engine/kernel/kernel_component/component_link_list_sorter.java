package kernel_component;

import java.util.ArrayList;
import java.util.Comparator;

import kernel_transformation.point;
import kernel_common_class.tree_search_container;

class component_link_list_comparator implements Comparator<component_link_list>
{
	private String component_sort_type;
	private double component_sort_min_distance;
	
	public component_link_list_comparator(String sort_type,double sort_min_distance)
	{
		component_sort_type			=sort_type;
		component_sort_min_distance	=sort_min_distance;
		
	}
	public int compare(component_link_list s,component_link_list t)
	{
		point ps=s.comp.absolute_location.multiply(0, 0, 0);
		point pt=t.comp.absolute_location.multiply(0, 0, 0);
		switch(component_sort_type) {
		default:
		case "xyz":
			if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
				return (ps.x<pt.x)?-1:1;
			if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
				return (ps.y<pt.y)?-1:1;
			if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
				return (ps.z<pt.z)?-1:1;
			if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
				return (ps.y<pt.y)?-1:1;
			if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
				return (ps.z<pt.z)?-1:1;
			if(ps.x<pt.x)
				return -1;
			if(ps.x>pt.x)
				return 1;
			if(ps.y<pt.y)
				return -1;
			if(ps.y>pt.y)
				return 1;
			if(ps.z<pt.z)
				return -1;
			if(ps.z>pt.z)
				return 1;
			return 0;
		case "xzy":
			if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
				return (ps.x<pt.x)?-1:1;
			if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
				return (ps.z<pt.z)?-1:1;
			if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
				return (ps.y<pt.y)?-1:1;
			if(ps.x<pt.x)
				return -1;
			if(ps.x>pt.x)
				return 1;
			if(ps.z<pt.z)
				return -1;
			if(ps.z>pt.z)
				return 1;
			if(ps.y<pt.y)
				return -1;
			if(ps.y>pt.y)
				return 1;
			return 0;
		case "yxz":
			if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
				return (ps.y<pt.y)?-1:1;
			if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
				return (ps.x<pt.x)?-1:1;
			if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
				return (ps.z<pt.z)?-1:1;
			if(ps.y<pt.y)
				return -1;
			if(ps.y>pt.y)
				return 1;
			if(ps.x<pt.x)
				return -1;
			if(ps.x>pt.x)
				return 1;
			if(ps.z<pt.z)
				return -1;
			if(ps.z>pt.z)
				return 1;
			return 0;
		case "yzx":
			if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
				return (ps.y<pt.y)?-1:1;
			if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
				return (ps.z<pt.z)?-1:1;
			if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
				return (ps.x<pt.x)?-1:1;
			if(ps.y<pt.y)
				return -1;
			if(ps.y>pt.y)
				return 1;
			if(ps.z<pt.z)
				return -1;
			if(ps.z>pt.z)
				return 1;
			if(ps.x<pt.x)
				return -1;
			if(ps.x>pt.x)
				return 1;
			return 0;
		case "zxy":
			if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
				return (ps.z<pt.z)?-1:1;
			if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
				return (ps.x<pt.x)?-1:1;
			if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
				return (ps.y<pt.y)?-1:1;
			if(ps.z<pt.z)
				return -1;
			if(ps.z>pt.z)
				return 1;
			if(ps.x<pt.x)
				return -1;
			if(ps.x>pt.x)
				return 1;
			if(ps.y<pt.y)
				return -1;
			if(ps.y>pt.y)
				return 1;
			return 0;
		case "zyx":
			if(Math.abs(ps.z-pt.z)>component_sort_min_distance)
				return (ps.z<pt.z)?-1:1;
			if(Math.abs(ps.y-pt.y)>component_sort_min_distance)
				return (ps.y<pt.y)?-1:1;
			if(Math.abs(ps.x-pt.x)>component_sort_min_distance)
				return (ps.x<pt.x)?-1:1;
			if(ps.z<pt.z)
				return -1;
			if(ps.z>pt.z)
				return 1;
			if(ps.y<pt.y)
				return -1;
			if(ps.y>pt.y)
				return 1;
			if(ps.x<pt.x)
				return -1;
			if(ps.x>pt.x)
				return 1;
			return 0;
		}
	}
}

public class component_link_list_sorter extends tree_search_container<component_link_list,component_link_list>
{
	private ArrayList<component_link_list>data_list;
	
	private component_link_list_sorter(component_link_list cll,String sort_type,double sort_min_distance)
	{
		super(new component_link_list_comparator(sort_type,sort_min_distance));
		
		for(component_link_list p=cll;p!=null;p=p.next_list_item)
			add(p,p,false);
		data_list=tree_get_value_list();
		for(int i=0,ni=data_list.size()-1;i<ni;i++)
			data_list.get(i).next_list_item=data_list.get(i+1);
		data_list.get(data_list.size()-1).next_list_item=null;
	}
	public static component_link_list do_sort(
			component_link_list cll,String sort_type,double sort_min_distance)
	{
		return (new component_link_list_sorter(cll,sort_type,sort_min_distance)).data_list.get(0);
	}
};
