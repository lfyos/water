package driver_movement;

public class movement_tree_id_creator 
{
	private long movement_tree_id;
	public long create_movement_tree_id()
	{
		return movement_tree_id++;
	}
	public movement_tree_id_creator()
	{
		movement_tree_id=0;
	}
}
