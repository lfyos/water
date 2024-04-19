package kernel_common_class;

import java.util.TreeMap;
import java.util.Comparator;

public class tree_search_container<T>
{
	class tree_node
	{
		public String key[];
		public T value;
		public long touch_time;
		public tree_node front,back;
		
		public tree_node(String my_key[],T my_value)
		{
			key			=my_key;
			value		=my_value;
			touch_time	=nanosecond_timer.absolute_nanoseconds();
			front		=null;
			back		=null;
		}
	}
	class tree_node_comparator implements Comparator<String[]>
	{
		private String empty_str[];
		
		public int compare(String[] obj1, String[] obj2)
		{
			obj1=(obj1==null)?empty_str:obj1;
			obj2=(obj2==null)?empty_str:obj2;
			
			for(int comp_result,i=0,n1=obj1.length,n2=obj2.length;;i++)
				if(i>=n1)
						return (i>=n2)?0:-1;
				else if(i>=n2)
						return 1;
				else if((comp_result=obj1[i].compareTo(obj2[i]))!=0)
						return comp_result;
		}
		public tree_node_comparator()
		{
			empty_str=new String[] {};
		}
	}
	
	private TreeMap<String[],tree_node> tree;
	private tree_node first,last;
	
	public tree_search_container()
	{
		tree=new TreeMap<String[],tree_node>(new tree_node_comparator());
		
		first=null;
		last=null;
		
		search_key=null;
		search_value=null;
	}

	public String search_key[];
	public T search_value;
	
	public long first_touch_time()
	{
		if(first==null)
			return -1;
		search_key=first.key;
		search_value=first.value;
		return first.touch_time;
	}
	
	public T add(String my_key[],T my_value)
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
	
	public T search(String my_key[])
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
	public T remove(String my_key[])
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
		
		return p.value;
	}
}
