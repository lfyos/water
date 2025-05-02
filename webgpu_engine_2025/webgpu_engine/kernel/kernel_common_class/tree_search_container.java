package kernel_common_class;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Comparator;

public class tree_search_container<KEY_TYPE,VALUE_TYPE>
{
	private Comparator<KEY_TYPE> comparator;
	private TreeMap<KEY_TYPE,tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE>> tree;
	private tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> first,last;
	
	private void dismount_from_list(tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> p)
	{
		var my_front=p.front;
		var my_back	=p.back;
		
		if(my_front==null){
			if(my_back==null) {
				first=null;
				last=null;
			}else{
				first=first.back;
				first.front=null;
			}
		}else if(my_back==null) {
			last=last.front;
			last.back=null;
		}else{
			my_front.back=my_back;
			my_back.front=my_front;
		}
		p.front=null;
		p.back=null;
	}
	private void mount_to_first(tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> p)
	{
		if(first==null) {
			p.front=null;
			p.back=null;
			first=p;
			last=p;
		}else{
			p.front=null;
			p.back=first;
			first.front=p;
			first=p;
		}
	}
	private void mount_to_last(tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> p)
	{
		if(last==null){
			p.front=null;
			p.back=null;
			first=p;
			last=p;
		}else {
			p.front=last;
			p.back=null;
			last.back=p;
			last=p;
		}
	}
	public tree_search_container(Comparator<KEY_TYPE> my_comparator)
	{
		comparator=my_comparator;
		tree=new TreeMap<KEY_TYPE,tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE>>(my_comparator);
		first=null;
		last=null;
	}

	public long first_touch_time()
	{
		if(first==null)
			return -1;
		return first.touch_time;
	}
	public KEY_TYPE get_first_key()
	{
		if(first==null)
			return null;
		else
			return first.key;
	}
	public ArrayList<VALUE_TYPE> get_first_value()
	{
		if(first==null)
			return null;
		else
			return first.list;
	}
	public int size()
	{
		return tree.size();
	}
	public ArrayList<VALUE_TYPE> add(KEY_TYPE my_key,VALUE_TYPE my_value)
	{
		var p=new tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>(my_key);
		var old_p=tree.put(my_key,p);
		if(old_p!=null){
			dismount_from_list(old_p);
			p.list=old_p.list;
		}
		mount_to_last(p);
		p.list.add(my_value);
		p.touch_time=nanosecond_timer.absolute_nanoseconds();
		return p.list;
	}
	public ArrayList<VALUE_TYPE> search(KEY_TYPE my_key)
	{
		var p=tree.get(my_key);
		if(p==null)
			return null;
		dismount_from_list(p);
		mount_to_last(p);
		p.touch_time=nanosecond_timer.absolute_nanoseconds();
		return p.list;
	}
	public ArrayList<VALUE_TYPE> remove(KEY_TYPE my_key)
	{
		var p=tree.remove(my_key);
		if(p==null)
			return null;
		dismount_from_list(p);
		return p.list;
	}
	public ArrayList<VALUE_TYPE> move_to_first(KEY_TYPE my_key)
	{
		var p=tree.get(my_key);
		if(p==null)
			return null;
		dismount_from_list(p);
		mount_to_first(p);
		p.touch_time=0;
		return p.list;
	}
	public ArrayList<tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>>
				get_tree_node_list(boolean do_sort_flag,boolean clear_all_flag)
	{
		class tree_node_sorter extends sorter <tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>,KEY_TYPE>
		{
			public int compare_data(
					tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE> s,
					tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE> t)
			{
				return comparator.compare(s.key,t.key);
			}
			public int compare_key(
					tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE> s,KEY_TYPE t)
			{
				return comparator.compare(s.key,t);
			}
			public tree_node_sorter(boolean do_sort_flag,boolean clear_all_flag)
			{
				for(var p=first;p!=null;p=p.back)
					data_list.add(p);
				if(clear_all_flag) {
					for(var p=first;p!=null;) {
						var q=p;
						p=p.back;
						q.front=null;
						q.back=null;
					}
					tree.clear();
					
					comparator	=null;
					tree		=null;
					first		=null;
					last		=null;
				}
				if(do_sort_flag)
					do_sort();
			}
		};
		return new tree_node_sorter(do_sort_flag,clear_all_flag).data_list;
	}
}
