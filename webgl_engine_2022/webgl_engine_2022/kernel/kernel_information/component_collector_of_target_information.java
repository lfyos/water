package kernel_information;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class component_collector_of_target_information extends jason_creator
{
	private engine_kernel ek;
	private client_information ci;
	
	public void print()
	{
		jason_creator creator_array[];
		if(ci.target_component_collector_array==null)
			creator_array=new jason_creator[0];
		else
			creator_array=new jason_creator[ci.target_component_collector_array.length];
		for(int i=0,ni=ci.target_component_collector_array.length;i<ni;i++)
			creator_array[i]=new component_collector_with_component_information(
										ci.target_component_collector_array[i],ek,ci);
		print("target_collector",creator_array);
	}
	public component_collector_of_target_information(engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
	}
}
