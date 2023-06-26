package kernel_render;

import java.util.ArrayList;

public class render_target_container 
{
	private ArrayList<Long> render_number;
	private ArrayList<render_target> target_array;
	private ArrayList<render_target> search_array;

	public void destroy()
	{
		render_target rt;
		if(target_array!=null) {
			for(int i=0,ni=target_array.size();i<ni;i++)
				if((rt=target_array.get(i))!=null){
					rt.destroy();
					target_array.set(i,null);
				}
			target_array.clear();
			target_array=null;
		}
		if(search_array!=null) {
			for(int i=0,ni=search_array.size();i<ni;i++)
				if((rt=search_array.get(i))!=null){
					rt.destroy();
					search_array.set(i,null);
				}
			search_array.clear();
			search_array=null;
		}
	}
	public render_target_container()
	{
		render_number	=new ArrayList<Long>();
		target_array	=new ArrayList<render_target>();
		search_array	=new ArrayList<render_target>();
	}
	public int get_render_target_number()
	{
		return (render_number==null)?0:(render_number.size());
	}
	public long get_do_render_number(int target_id)
	{
		return (render_number==null)?0:
			((target_id<0)||(target_id>=render_number.size()))?0:
			render_number.get(target_id);
	}
	public ArrayList<render_target>get_render_target()
	{
		render_target rt;
		long my_render_number;
		ArrayList<render_target> ret_val=new ArrayList<render_target>();
		
		for(int i=0,j=0,ni=search_array.size();i<ni;i++)
			if((rt=search_array.get(i))!=null)
				if((my_render_number=render_number.get(rt.target_id))!=0){
					render_number.set(rt.target_id,(my_render_number>0)?(my_render_number-1):-1);
					ret_val.add(j++,rt);
				}
		
		return ret_val;	
	}
	static private int compart_target(render_target s,render_target t)
	{
		if(s.target_comonent_id<t.target_comonent_id)
			return -3;
		if(s.target_comonent_id>t.target_comonent_id)
			return 3;
		
		if(s.target_driver_id<t.target_driver_id)
			return -2;
		if(s.target_driver_id>t.target_driver_id)
			return 2;
		
		if(s.target_texture_id<t.target_texture_id)
			return -1;
		if(s.target_texture_id>t.target_texture_id)
			return 1;
		
		return 0;
	}
	private int search_target(render_target rt)
	{
		for(int start_pointer=0,end_pointer=search_array.size()-1;start_pointer<=end_pointer;){
			int mid_pointer=(start_pointer+end_pointer)/2;
			int compare_result=compart_target(rt,search_array.get(mid_pointer));
			if(compare_result<0)
				end_pointer=mid_pointer-1;
			else if(compare_result>0)
				start_pointer=mid_pointer+1;
			else
				return mid_pointer;
		}	
		return -1;
	}
	public int register_target(render_target target,long do_render_number)
	{
		int search_target_id;
		
		if((search_target_id=search_target(target))>=0){
			render_target rt=search_array.get(search_target_id);
			target.target_id=rt.target_id;
			search_array.	set(search_target_id,target);
			target_array.	set(target.target_id,target);
			render_number.	set(rt.target_id,do_render_number);
			
			return target.target_id;
		}

		int target_number=target_array.size();
		
		target_array.	add(target_number, target);
		search_array.	add(target_number, target);
		render_number.	add(target_number,do_render_number);
		
		target.target_id=target_number;
		
		for(int i=target_number-1,j=target_number;i>=0;i--,j--){
			render_target rt_i=search_array.get(i);
			render_target rt_j=search_array.get(j);
			if(compart_target(rt_i,rt_j)<=0)
				return target.target_id;
			search_array.set(i,rt_j);
			search_array.set(j,rt_i);
		}
		return target.target_id;
	}
}