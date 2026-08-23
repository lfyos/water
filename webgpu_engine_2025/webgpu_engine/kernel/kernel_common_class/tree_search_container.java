package kernel_common_class;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;

public class tree_search_container<KEY_TYPE,VALUE_TYPE>
{
	private TreeMap<KEY_TYPE,tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE>> tree;
	private tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> first,last;
	
	private ArrayList<VALUE_TYPE>tree_value_list;
	private ArrayList<tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>>tree_node_list;

	private void dismount_from_list(tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> p)
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
		if(my_comparator==null)
			tree=new TreeMap<KEY_TYPE,tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE>>();
		else
			tree=new TreeMap<KEY_TYPE,tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE>>(my_comparator);
		first=null;
		last=null;
		
		tree_node_list	=null;
		tree_value_list	=null;
	}
	public void destroy()
	{
		for(var p=first;p!=null;) {
			var pp=p;
			p=p.back;
			
			pp.destroy();
		}
		first		=null;
		last		=null;
		
		tree.clear();
		
		tree_node_list		=null;
		tree_value_list		=null;
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
	public tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> add(KEY_TYPE my_key,VALUE_TYPE my_value)
	{
		tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> old_tree_node,new_tree_node;
		new_tree_node=new tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>(my_key);
		if((old_tree_node=tree.put(my_key,new_tree_node))!=null){
			dismount_from_list(old_tree_node);
			new_tree_node.list=old_tree_node.list;
		}
		mount_to_last(new_tree_node);	
		new_tree_node.list.add(my_value);
		new_tree_node.touch_time=nanosecond_timer.absolute_nanoseconds();
		
		tree_node_list	=null;
		tree_value_list	=null;

		return new_tree_node;
	}
	public tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> search(KEY_TYPE my_key)
	{
		tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> tree_node;
		if((tree_node=tree.get(my_key))!=null){
			dismount_from_list(tree_node);
			mount_to_last(tree_node);
			tree_node.touch_time=nanosecond_timer.absolute_nanoseconds();
		}
		return tree_node;
	}
	public tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> remove(KEY_TYPE my_key)
	{
		tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> tree_node;
		if((tree_node=tree.remove(my_key))!=null){
			dismount_from_list(tree_node);
			tree_node_list		=null;
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
	public ArrayList<tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>>tree_get_node_list()
	{
		if(tree_node_list==null){
			tree_node_list=new ArrayList<tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>>();
			for(var my_tree_node:tree.values()) 
				tree_node_list.add(my_tree_node);
		}
		return tree_node_list;
	}
	public ArrayList<VALUE_TYPE>tree_get_value_list()
	{
		if(tree_value_list==null){
			tree_value_list=new ArrayList<VALUE_TYPE>();
			tree_get_node_list();
			for(int i=0,ni=tree_node_list.size();i<ni;i++){
				ArrayList<VALUE_TYPE>item_list=tree_node_list.get(i).list;
				for(int j=0,nj=item_list.size();j<nj;j++)
					tree_value_list.add(item_list.get(j));
			}
		}
		return tree_value_list;
	}
}
