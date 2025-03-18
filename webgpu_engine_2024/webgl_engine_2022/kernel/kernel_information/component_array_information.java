package kernel_information;

import kernel_component.component;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class component_array_information extends jason_creator
{
	private scene_kernel sk;
	private client_information ci;
	public void print()
	{
		component comp[]=sk.component_cont.get_sort_component_array();
		jason_creator jc[]=new jason_creator[comp.length];
		for(int i=0,ni=jc.length;i<ni;i++)
			jc[i]=new component_information(comp[i],ci);
		print("component_number",jc.length);
		print("component_array",jc);
	}
	public component_array_information(scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		sk=my_sk;
		ci=my_ci;
	}
}