package kernel_component;

import java.util.ArrayList;

import kernel_transformation.point;
import kernel_transformation.box;
import kernel_common_class.sorter;

final class component_info
{
	public component comp;
	public int driver_id;
	public point po;
	
	public component_info(component my_comp,int my_driver_id,point my_po)
	{
		comp=my_comp;
		driver_id=my_driver_id;
		po=my_po;
	}
}
public class component_space_sorter extends sorter<component_info,component_info>
{
	private int sort_type;
	private double min_distance;
	
	public int compare_data(component_info s,component_info t)
	{
		switch(sort_type){
		default:
		case 0://XYZ
			if(Math.abs(s.po.x-t.po.x)>min_distance)
				return (s.po.x<=t.po.x)?-1:1;
			if(Math.abs(s.po.y-t.po.y)>min_distance)
				return (s.po.y<=t.po.y)?-2:2;
			if(Math.abs(s.po.z-t.po.z)>min_distance)
				return (s.po.z<=t.po.z)?-3:3;
			return 0;
		case 1://XZY
			if(Math.abs(s.po.x-t.po.x)>min_distance)
				return (s.po.x<=t.po.x)?-1:1;
			if(Math.abs(s.po.z-t.po.z)>min_distance)
				return (s.po.z<=t.po.z)?-2:2;
			if(Math.abs(s.po.y-t.po.y)>min_distance)
				return (s.po.y<=t.po.y)?-3:3;
			return 0;
		case 2://YXZ
			if(Math.abs(s.po.y-t.po.y)>min_distance)
				return (s.po.y<=t.po.y)?-1:1;
			if(Math.abs(s.po.x-t.po.x)>min_distance)
				return (s.po.x<=t.po.x)?-2:2;
			if(Math.abs(s.po.z-t.po.z)>min_distance)
				return (s.po.z<=t.po.z)?-3:3;
			return 0;
		case 3://YZX
			if(Math.abs(s.po.y-t.po.y)>min_distance)
				return (s.po.y<=t.po.y)?-1:1;
			if(Math.abs(s.po.z-t.po.z)>min_distance)
				return (s.po.z<=t.po.z)?-2:2;
			if(Math.abs(s.po.x-t.po.x)>min_distance)
				return (s.po.x<=t.po.x)?-3:3;
			return 0;
		case 4://ZXY
			if(Math.abs(s.po.z-t.po.z)>min_distance)
				return (s.po.z<=t.po.z)?-1:1;
			if(Math.abs(s.po.x-t.po.x)>min_distance)
				return (s.po.x<=t.po.x)?-2:2;
			if(Math.abs(s.po.y-t.po.y)>min_distance)
				return (s.po.y<=t.po.y)?-3:3;
			return 0;
		case 5://ZYX
			if(Math.abs(s.po.z-t.po.z)>min_distance)
				return (s.po.z<=t.po.z)?-1:1;
			if(Math.abs(s.po.y-t.po.y)>min_distance)
				return (s.po.y<=t.po.y)?-2:2;
			if(Math.abs(s.po.x-t.po.x)>min_distance)
				return (s.po.x<=t.po.x)?-3:3;
			return 0;
		}
	}
	private component_space_sorter(component_link_list list,int my_sort_type,double my_min_distance)
	{
		sort_type		=my_sort_type;
		min_distance	=my_min_distance;
		data_list		=new ArrayList<component_info>();
		
		for(component_link_list p=list;p!=null;p=p.next_list_item) {
			point po;
			box b=p.comp.get_component_box(true);
			if(b==null)
				po=p.comp.absolute_location.multiply(new point(0,0,0));
			else
				po=b.center();
			data_list.add(new component_info(p.comp,p.driver_id,po));
		}
		do_sort();
	}
	public static component_link_list sort_component(
		component_link_list list,int sort_type,double min_distance)
	{
		component_link_list ret_val=null;
		if(list!=null){
			component_space_sorter cws=new component_space_sorter(list,sort_type,min_distance);
			for(int i=cws.data_list.size()-1;i>=0;i--) {
				component_info p=cws.data_list.get(i);
				ret_val=new component_link_list(p.comp,p.driver_id,ret_val);
			}
		}
		return ret_val;
	}
}
