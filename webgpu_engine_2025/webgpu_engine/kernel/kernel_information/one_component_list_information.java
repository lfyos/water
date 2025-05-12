package kernel_information;

import kernel_component.component_link_list;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class one_component_list_information extends jason_creator
{
	private component_link_list cll;
	private scene_kernel sk;
	private client_information ci;
	
	public void print()
	{
		print("driver_id",cll.driver_id);
		print("component",new component_information(cll.comp,sk,ci));
	}
	public one_component_list_information(component_link_list my_cll,
			scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		cll=my_cll;
		sk=my_sk;
		ci=my_ci;
	}
}