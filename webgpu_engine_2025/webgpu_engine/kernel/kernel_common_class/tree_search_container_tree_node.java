package kernel_common_class;

import java.util.ArrayList;

public class tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE>
{
	public KEY_TYPE key;
	public long touch_time;
	public ArrayList<VALUE_TYPE> list;
	
	public tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE> front,back;
	
	public tree_search_container_tree_node(KEY_TYPE my_key)
	{
		front		=null;
		back		=null;
		
		key			=my_key;
		list		=new ArrayList<VALUE_TYPE>();
		
		touch_time	=nanosecond_timer.absolute_nanoseconds();
	}
}
