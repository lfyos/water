package kernel_common_class;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;

public class tree_search_container<KEY_TYPE,VALUE_TYPE>
{
	private TreeMap<KEY_TYPE,tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE>> tree;
	private tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> first,last;
	private Comparator<VALUE_TYPE> value_comparator;
	
	private ArrayList<VALUE_TYPE>tree_value_list;

	private tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> 
		dismount_from_list(tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> p)
	{
		var my_front=p.front;
		var my_back	=p.back;
		
		if(my_front==null){
			if(my_back==null){
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
		
		return p;
	}
	private tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> 
		mount_to_first(tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> p)
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
		return p;
	}
	private tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> 
		mount_to_last(tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> p)
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
		return p;
	}
	public tree_search_container(
			Comparator<KEY_TYPE>my_key_comparator,
			Comparator<VALUE_TYPE>my_value_comparator)
	{
		if(my_key_comparator==null)
			tree=new TreeMap<KEY_TYPE,tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE>>();
		else
			tree=new TreeMap<KEY_TYPE,tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE>>(my_key_comparator);
		first=null;
		last=null;
		value_comparator=my_value_comparator;
		
		tree_value_list	=null;
	}
	public void destroy()
	{
		while(first!=null) 
			dismount_from_list(first).destroy();
		
		tree.clear();
		first=null;
		last=null;
		value_comparator=null;
		
		tree_value_list=null;
	}
	
	public int size()
	{
		return tree.size();
	}
	public tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> get_first_tree_node()
	{
		return first;
	}
	public tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> get_last_tree_node()
	{
		return last;
	}
	public tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> search_tree_node(KEY_TYPE my_key)
	{
		tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> tree_node;
		if((tree_node=tree.get(my_key))!=null){
			dismount_from_list(tree_node);
			mount_to_last(tree_node);
			tree_node.touch_time=nanosecond_timer.absolute_nanoseconds();
		}
		return tree_node;
	}
	public ArrayList<VALUE_TYPE>search_value_list(KEY_TYPE my_key)
	{
		tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> tree_node;
		if((tree_node=search_tree_node(my_key))!=null)
			if(tree_node.list!=null)
				if(tree_node.list.size()>0)
					return tree_node.list;
		return null;
	}
	public tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> add(KEY_TYPE my_key,VALUE_TYPE my_value)
	{
		tree_value_list	=null;
		
		tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> tree_node;
		if((tree_node=search_tree_node(my_key))==null){
			tree_node=new tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>(my_key);
			tree.put(my_key,tree_node);
			mount_to_last(tree_node);
			tree_node.touch_time=nanosecond_timer.absolute_nanoseconds();
			tree_node.list.add(my_value);
			return tree_node;
		}
		if(value_comparator==null)
			tree_node.list.add(tree_node.list.size(),my_value);
		else {
			ArrayList<VALUE_TYPE> my_value_list=tree_node.list;
			for(int begin_pointer=0,end_pointer=my_value_list.size()-1;;) {
				int middle_pointer=(begin_pointer+end_pointer)/2;
				var middle_value=my_value_list.get(middle_pointer);
				int compare_result=value_comparator.compare(my_value,middle_value);
				if(compare_result<0){
					if((end_pointer=middle_pointer-1)<begin_pointer) {
						my_value_list.add(middle_pointer+0,my_value);
						break;
					};
				}else{
					if((begin_pointer=middle_pointer+1)>end_pointer) {
						my_value_list.add(middle_pointer+1,my_value);
						break;
					};
				}
			}
		}
		return tree_node;
	}
	
	public tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> remove(KEY_TYPE my_key)
	{
		tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> tree_node;
		if((tree_node=tree.remove(my_key))!=null){
			dismount_from_list(tree_node);
			tree_value_list		=null;
		}
		return tree_node;
	}
	public tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> move_to_first(KEY_TYPE my_key)
	{
		tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> tree_node;
		if((tree_node=tree.get(my_key))!=null){
			dismount_from_list(tree_node);
			mount_to_first(tree_node);
			tree_node.touch_time=0;
		}
		return tree_node;
	}
	public Collection<tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>>tree_get_node_collection()
	{
		return tree.values();
	}
	public ArrayList<VALUE_TYPE>tree_get_value_list()
	{
		if(tree_value_list==null){
			tree_value_list=new ArrayList<VALUE_TYPE>();
			for(var my_tree_node:tree.values())
				for(var my_item:my_tree_node.list)
					tree_value_list.add(my_item);
		}
		return tree_value_list;
	}
}
