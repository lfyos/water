package kernel_render;

import java.util.ArrayList;

public class render_target_container 
{
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
		target_array	=new ArrayList<render_target>();
		search_array	=new ArrayList<render_target>();
	}
	public int get_render_target_number()
	{
		return (target_array==null)?0:(target_array.size());
	}
	public render_target[]get_render_target()
	{
		return target_array.toArray(new render_target[target_array.size()]);
	}
	static private int compart_target(render_target s,render_target t)
	{
		int compare_result;
		String my_s_target_name=s.target_name.trim();
		String my_t_target_name=t.target_name.trim();
		
		if(my_s_target_name==null)
			my_s_target_name="no_target_name";
		else if(my_s_target_name.compareTo("")==0)
			my_s_target_name="no_target_name";
		
		if(my_t_target_name==null)
			my_t_target_name="no_target_name";
		else if(my_t_target_name.compareTo("")==0)
			my_t_target_name="no_target_name";
		if((compare_result=my_s_target_name.compareTo(my_t_target_name))!=0)
			return compare_result;
			
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
	public void register_target(render_target new_rt)
	{
		int search_target_id;

		if((search_target_id=search_target(new_rt))>=0){
			render_target old_rt=search_array.get(search_target_id);
			new_rt.target_id=old_rt.target_id;
			search_array.set(search_target_id,new_rt);
			target_array.set(new_rt.target_id,new_rt);
		}else{
			int target_number=target_array.size();
			target_array.	add(target_number,new_rt);
			search_array.	add(target_number,new_rt);
			new_rt.target_id=target_number;
			
			for(int i=target_number-1,j=target_number;i>=0;i--,j--){
				render_target rt_i=search_array.get(i);
				render_target rt_j=search_array.get(j);
				if(compart_target(rt_i,rt_j)<=0)
					break;
				search_array.set(i,rt_j);
				search_array.set(j,rt_i);
			}
		}
	}
}