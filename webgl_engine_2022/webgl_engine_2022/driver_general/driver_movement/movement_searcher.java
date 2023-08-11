package driver_movement;

public class movement_searcher
{
	public movement_tree_link_list search_link_list,can_delete_list;
	
	private boolean search(movement_tree my_current,long movement_tree_id,int my_index_id)
	{
		if(my_current!=null){
			search_link_list=new movement_tree_link_list(my_current,search_link_list,my_index_id);
			if(my_current.movement_tree_id==movement_tree_id)
				return true;
			if(my_current.children!=null)
				for(int i=0,ni=my_current.children.length;i<ni;i++)
					if(search(my_current.children[i],movement_tree_id,i))
						return true;
			search_link_list=search_link_list.next;
		}
		return false;
	}
	public movement_searcher(movement_tree root_movement,long movement_tree_id)
	{
		search_link_list=null;
		can_delete_list=null;
		search(root_movement,movement_tree_id,-1);
		for(can_delete_list=search_link_list;can_delete_list!=null;can_delete_list=can_delete_list.next)
			if(can_delete_list.next!=null)
				if(can_delete_list.next.tree_node.children!=null)
					if(can_delete_list.next.tree_node.children.length>1)
						return;
	}
}