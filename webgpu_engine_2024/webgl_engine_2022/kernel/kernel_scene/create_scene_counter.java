package kernel_scene;

public class create_scene_counter 
{
	public  int		scene_kernel_number;
	public  int		scene_component_number;
	
	synchronized public void update_kernel_component_number(int scene_number,int component_number)
	{
		scene_kernel_number+=scene_number;
		scene_component_number+=component_number;
	}
	public create_scene_counter()
	{
		scene_kernel_number=0;
		scene_component_number=0;
	}
}
