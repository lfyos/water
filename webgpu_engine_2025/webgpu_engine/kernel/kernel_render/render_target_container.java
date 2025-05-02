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
		
		String my_s_target_name=(s.target_name==null)?"no_target_name":s.target_name;
		String my_t_target_name=(t.target_name==null)?"no_target_name":t.target_name;
		
		return my_s_target_name.compareTo(my_t_target_name);
	}
}
public class render_target_container extends tree_search_container<render_target,render_target>
{
	private ArrayList<render_target> target_array;
	
	public void destroy()
	{
		if(target_array==null)
			return;
		
		for(int i=0,ni=target_array.size();i<ni;i++) {
			var rt=target_array.get(i);
			if(rt!=null)
				rt.destroy();
		}
		target_array.clear();
		target_array=null;
		
		var list=get_tree_node_list(false,true);
		for(int i=0,ni=list.size();i<ni;i++) {
			var p=list.get(i);
			if(p==null)
				continue;
			p.key=null;
			for(int j=p.list.size()-1;j>=0;j--) {
				var rt=p.list.remove(j);
				if(rt==null)
					continue;
				rt.destroy();
			}
		}
	}
	public render_target_container()
	{
		super(new render_target_comparator());
		target_array=new ArrayList<render_target>();
	}
	public ArrayList<render_target>get_render_target()
	{
		return target_array;
	}
	public void register_target(render_target new_rt)
	{
		ArrayList<render_target> p;

		if((p=search(new_rt))==null){
			new_rt.target_id=target_array.size();
			target_array.add(new_rt.target_id,new_rt);
			add(new_rt,new_rt);
		}else{
			new_rt.target_id=p.get(0).target_id;
			target_array.set(new_rt.target_id,new_rt);
			p.set(0,new_rt);
		}
	}
}
