package kernel_engine;

public class create_engine_counter 
{
	public  int		engine_kernel_number;
	public  int		engine_component_number;
	
	synchronized public void update_kernel_component_number(int kernel_number,int component_number)
	{
		engine_kernel_number+=kernel_number;
		engine_component_number+=component_number;
	}
	public create_engine_counter()
	{
		engine_kernel_number=0;
		engine_component_number=0;
	}
}
