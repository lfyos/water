package driver_movement;

public class movement_tree_link_list
{
	public movement_tree tree_node;
	public movement_tree_link_list next;
	public int index_id;
	
	public movement_tree_link_list(	movement_tree my_tree_node,
			movement_tree_link_list my_next,int my_index_id)
	{
		tree_node=my_tree_node;
		next=my_next;
		index_id=my_index_id;
	}
}
