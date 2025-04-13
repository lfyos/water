package kernel_common_class;

import java.util.ArrayList;

public class tree_search_container_tree_node <KEY_TYPE,VALUE_TYPE>
{
	public KEY_TYPE key;
	public ArrayList<VALUE_TYPE> list;
	public long touch_time;
	public tree_search_container_tree_node<KEY_TYPE,VALUE_TYPE> front,back;
	
	public tree_search_container_tree_node(KEY_TYPE my_key)
	{
		key			=my_key;
		list		=new ArrayList<VALUE_TYPE>();
		touch_time	=nanosecond_timer.absolute_nanoseconds();
		front		=null;
		back		=null;
	}
}
