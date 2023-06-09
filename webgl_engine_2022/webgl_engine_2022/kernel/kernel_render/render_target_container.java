package kernel_render;

import java.util.ArrayList;

public class render_target_container 
{
	private ArrayList<render_target> target_array;
	private ArrayList<render_target> search_array;

	public void destroy()
	{
		render_target rt;
		for(int i=0,ni=target_array.size();i<ni;i++)
			if((rt=target_array.get(i))!=null){
				rt.destroy();
				target_array.set(i,null);
			}
		for(int i=0,ni=search_array.size();i<ni;i++)
			if((rt=search_array.get(i))!=null){
				rt.destroy();
				search_array.set(i,null);
			}
		target_array=null;
		search_array=null;
	}
	public render_target_container()
	{
		target_array			=new ArrayList<render_target>();
		search_array			=new ArrayList<render_target>();
	}
	public int get_render_target_number()
	{
		return target_array.size();
	}
	public long get_do_render_number(int target_id)
	{
		return target_array.get(target_id).do_render_number;
	}
	public ArrayList<render_target>get_render_target()
	{
		ArrayList<render_target> ret_val=new ArrayList<render_target>();
		
		for(int i=0,j=0,ni=search_array.size();i<ni;i++) {
			render_target rt=search_array.get(i);
			if(rt==null)
				continue;
			if(rt.do_render_number==0)
				continue;
			rt.do_render_number--;
			ret_val.add(j++,rt);
		}
		return ret_val;	
	}
	private int search_target(String my_target_name)
	{
		if(search_array!=null)
			for(int start_pointer=0,end_pointer=search_array.size()-1;start_pointer<=end_pointer;){
				int mid_pointer=(start_pointer+end_pointer)/2;
				int compare_result=search_array.get(mid_pointer).target_name.compareTo(my_target_name);
				if(compare_result==0)
					return mid_pointer;
				if(compare_result<0)
					start_pointer=mid_pointer+1;
				else
					end_pointer=mid_pointer-1;
			}
		return -1;
	}
	public render_target get_target(String my_target_name)
	{
		int search_target_id;
		return ((search_target_id=search_target(my_target_name))<0)?null:(search_array.get(search_target_id));
	}
	public int register_target(render_target target,render_target main_target)
	{
		main_target=(main_target==null)?target:main_target;
		int search_target_id;
		if((search_target_id=search_target(target.target_name))>=0){
			render_target rt=search_array.get(search_target_id);
			
			target.target_id=rt.target_id;
			target.render_target_id	=main_target.target_id;
			
			search_array.set(search_target_id,target);
			target_array.set(target.target_id,target);
	
			return target.target_id;
		}

		int target_number=target_array.size();
		
		target_array.add(target_number, target);
		search_array.add(target_number, target);
		target.target_id		=target_number;
		target.render_target_id	=main_target.target_id;
		
		for(int i=target_number-1,j=target_number;i>=0;i--,j--){
			render_target rt_i=search_array.get(i);
			render_target rt_j=search_array.get(j);
			if(rt_i.target_name.compareTo(rt_j.target_name)<=0)
				break;
			search_array.set(i,rt_j);
			search_array.set(j,rt_i);
		}
		return target.target_id;
	}
}