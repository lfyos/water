package kernel_common_class;

public class system_id 
{
	public int system_id;
	
	public system_id(system_id_manager manager,int id_array[])
	{
		system_id=manager.apply_for_system_id(id_array);
	}
}
