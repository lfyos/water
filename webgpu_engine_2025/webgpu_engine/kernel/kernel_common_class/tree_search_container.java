package kernel_common_class;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Comparator;

public class tree_search_container<KEY_TYPE,VALUE_TYPE>
{
	private TreeMap<KEY_TYPE,tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE>> tree;
	private tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE> first,last;
	
	private ArrayList<tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>>linklist_node_list,tree_node_list;
	private ArrayList<VALUE_TYPE>linklist_value_list,tree_value_list;
	
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
		
		linklist_node_list	=null;
		tree_node_list		=null;
		linklist_value_list	=null;
		tree_value_list		=null;
	}
	public void clear()
	{
		for(var p=first;p!=null;) {
			var pp=p;
			p=p.back;
			
			pp.destroy();
		}
		first		=null;
		last		=null;
		
		tree.clear();
		
		linklist_node_list	=null;
		tree_node_list		=null;
		linklist_value_list	=null;
		tree_value_list		=null;
	}
	public long first_touch_time()
	{
		return (first==null)?-1:first.touch_time;
	}
	public KEY_TYPE first_key()
	{
		return (first==null)?null:first.key;
	}
	public ArrayList<VALUE_TYPE> first_value()
	{
		return (first==null)?null:first.list;
	}
	public long last_touch_time()
	{
		return (last==null)?-1:last.touch_time;
	}
	public KEY_TYPE last_key()
	{
		return (last==null)?null:last.key;
	}
	public ArrayList<VALUE_TYPE> last_value()
	{
		return (last==null)?null:last.list;
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
		
		linklist_node_list	=null;
		tree_node_list		=null;
		linklist_value_list	=null;
		tree_value_list		=null;
		
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
		
		linklist_node_list	=null;
		linklist_value_list	=null;
		
		return p.list;
	}
	public ArrayList<VALUE_TYPE> remove(KEY_TYPE my_key)
	{
		var p=tree.remove(my_key);
		if(p==null)
			return null;
		dismount_from_list(p);
		
		linklist_node_list	=null;
		tree_node_list		=null;
		linklist_value_list	=null;
		tree_value_list		=null;
		
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
		
		linklist_node_list	=null;
		linklist_value_list	=null;
		
		return p.list;
	}
	public boolean operate_tree_node(tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE> current_node)
	{
		return false;
	}
	
	public int linklist_iterate_tree_node()
	{
		int ret_val=0;
		for(var p=first;p!=null;p=p.back) {
			if(operate_tree_node(p))
				break;
			ret_val++;
		}
		return ret_val;
	}
	public int tree_iterate_tree_node()
	{
		int ret_val=0;
		for(var my_tree_node:tree.values()){
			if(operate_tree_node(my_tree_node))
				break;
			ret_val++;
		}
		return ret_val;
	}
	public  ArrayList<tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>>linklist_get_node_list()
	{
		if(linklist_node_list==null){
			linklist_node_list=new ArrayList<tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>>();
			for(var p=first;p!=null;p=p.back) { 
				if(operate_tree_node(p))
					break;
				linklist_node_list.add(p);
			}
		}
		return linklist_node_list;
	}
	public ArrayList<tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>>tree_get_node_list()
	{
		if(tree_node_list==null){
			tree_node_list=new ArrayList<tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>>();
			for(var my_tree_node:tree.values()) {
				if(operate_tree_node(my_tree_node))
					break;
				tree_node_list.add(my_tree_node);
			}
		}
		return tree_node_list;
	}
	private ArrayList<VALUE_TYPE> from_node_list_to_value_list(
			ArrayList<tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE>> node_list)
	{
		var value_list=new ArrayList<VALUE_TYPE>();
		for(int i=0,ni=node_list.size();i<ni;i++){
			var item_list=node_list.get(i).list;
			for(int j=0,nj=item_list.size();j<nj;j++)
				value_list.add(item_list.get(j));
		}
		return value_list;	
	}
	public ArrayList<VALUE_TYPE>linklist_get_value_list()
	{
		if (linklist_value_list==null)
			linklist_value_list=from_node_list_to_value_list(linklist_get_node_list());
		return linklist_value_list;
	}
	public ArrayList<VALUE_TYPE>tree_get_value_list()
	{
		if(tree_value_list==null)
			tree_value_list=from_node_list_to_value_list(tree_get_node_list());
		return tree_value_list;
	}
}
