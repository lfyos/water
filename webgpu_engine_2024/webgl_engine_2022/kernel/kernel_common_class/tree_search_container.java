package kernel_common_class;

import java.util.TreeMap;
import java.util.Comparator;

public class tree_search_container<KEY_TYPE,VALUE_TYPE>
{
	class tree_node
	{
		public KEY_TYPE key;
		public VALUE_TYPE value;
		public long touch_time;
		public tree_node front,back;
		
		public tree_node(KEY_TYPE my_key,VALUE_TYPE my_value)
		{
			key			=my_key;
			value		=my_value;
			touch_time	=nanosecond_timer.absolute_nanoseconds();
			front		=null;
			back		=null;
		}
	}
	
	private TreeMap<KEY_TYPE,tree_node> tree;
	private tree_node first,last;
	
	public tree_search_container(Comparator<KEY_TYPE> my_comparator)
	{
		tree=new TreeMap<KEY_TYPE,tree_node>(my_comparator);
		
		first=null;
		last=null;
		
		search_key=null;
		search_value=null;
	}

	public KEY_TYPE search_key;
	public VALUE_TYPE search_value;
	
	public long first_touch_time()
	{
		if(first==null)
			return -1;
		search_key=first.key;
		search_value=first.value;
		return first.touch_time;
	}
	public int size()
	{
		return tree.size();
	}
	public VALUE_TYPE add(KEY_TYPE my_key,VALUE_TYPE my_value)
	{
		tree_node p=new tree_node(my_key,my_value);
		tree.put(my_key,p);
		
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
		
		search_key=p.key;
		search_value=p.value;
		
		p.touch_time=nanosecond_timer.absolute_nanoseconds();
		
		return p.value;
	}
	public VALUE_TYPE move_to_first(KEY_TYPE my_key)
	{
		tree_node p;
	
		if((p=tree.get(my_key))==null)
			return null;
		
		tree_node my_front=p.front,my_back=p.back;
		
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
		
		search_key=p.key;
		search_value=p.value;
		p.touch_time=0;
		
		return p.value;
	}
	public VALUE_TYPE search(KEY_TYPE my_key)
	{
		tree_node p;
	
		if((p=tree.get(my_key))==null)
			return null;
		
		tree_node my_front=p.front,my_back=p.back;
		
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
		
		if(last==null) {
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

		search_key=p.key;
		search_value=p.value;
		
		p.touch_time=nanosecond_timer.absolute_nanoseconds();
		
		return p.value;
	}
	public VALUE_TYPE remove(KEY_TYPE my_key)
	{
		tree_node p;
		
		if((p=tree.remove(my_key))==null)
			return null;
		
		tree_node my_front=p.front,my_back=p.back;
		
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
		
		search_key=p.key;
		search_value=p.value;
		
		return p.value;
	}
}
