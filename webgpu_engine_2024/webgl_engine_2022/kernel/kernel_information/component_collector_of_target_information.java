package kernel_information;

import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class component_collector_of_target_information extends jason_creator
{
	private scene_kernel sk;
	private client_information ci;
	
	public void print()
	{
		jason_creator creator_array[];
		if(ci.target_component_collector_list==null)
			creator_array=new jason_creator[0];
		else
			creator_array=new jason_creator[ci.target_component_collector_list.size()];
		for(int i=0,ni=ci.target_component_collector_list.size();i<ni;i++)
			creator_array[i]=new component_collector_with_component_information(
										ci.target_component_collector_list.get(i),sk,ci);
		print("target_collector",creator_array);
	}
	public component_collector_of_target_information(scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		sk=my_sk;
		ci=my_ci;
	}
}
