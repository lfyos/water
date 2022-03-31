package driver_movement;

public class movement_searcher
{
	public movement_tree result,result_parent,result_children[];
	public int result_id;
	private boolean search(movement_tree my_current,movement_tree my_current_parent,long movement_tree_id,int my_result_id)
	{
		if(my_current!=null){
			if(my_current.movement_tree_id==movement_tree_id){
				result			=my_current;
				result_parent	=my_current_parent;
				result_children	=my_current.children;
				result_id		=my_result_id;
				return true;
			}
			if(my_current.children!=null)
				for(int i=0,ni=my_current.children.length;i<ni;i++)
					if(search(my_current.children[i],my_current,movement_tree_id,i))
						return true;
		}
		return false;
	}
	public movement_searcher(movement_tree root_movement,long movement_tree_id)
	{
		result=null;
		result_parent=null;
		result_children=null;
		search(root_movement,null,movement_tree_id,0);
	}
}
