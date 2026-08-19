package kernel_render;

import java.util.ArrayList;
import java.util.Comparator;

import kernel_common_class.tree_search_container;

class render_target_comparator implements Comparator<render_target>
{
	public int compare(render_target s,render_target t)
	{
		if(s.target_comonent_id<t.target_comonent_id)
			return -4;
		if(s.target_comonent_id>t.target_comonent_id)
			return 4;
		
		if(s.target_driver_id<t.target_driver_id)
			return -3;
		if(s.target_driver_id>t.target_driver_id)
			return 3;
		
		if(s.target_texture_id<t.target_texture_id)
			return -2;
		if(s.target_texture_id>t.target_texture_id)
			return 2;
		return s.target_name.compareTo(t.target_name);
	}
}
public class render_target_container extends tree_search_container<render_target,render_target>
{
	private ArrayList<render_target> target_array;
	
	public void destroy()
	{
		if(target_array!=null) {
			target_array.clear();
			target_array=null;
		}
		super.destroy();
	}
	public render_target_container()
	{
		super(new render_target_comparator());
		target_array=new ArrayList<render_target>();
	}
	public render_target[]get_render_target()
	{
		render_target ret_val[]=new render_target[target_array.size()];
		for(int i=0,ni=ret_val.length;i<ni;i++)
			if((ret_val[i]=target_array.get(i)).do_render_flag)
				ret_val[i].do_render_flag=false;
			else
				ret_val[i]=null;
		return ret_val;
	}
	public void register_target(render_target new_rt)
	{
		var my_tree_node=search(new_rt);
		
		if(my_tree_node==null){
			new_rt.target_id=target_array.size();
			target_array.add(new_rt.target_id,new_rt);
			add(new_rt,new_rt);
		}else{
			new_rt.target_id=my_tree_node.list.get(0).target_id;
			target_array.set(new_rt.target_id,new_rt);
			my_tree_node.list.set(0,new_rt);
		}
	}
}
