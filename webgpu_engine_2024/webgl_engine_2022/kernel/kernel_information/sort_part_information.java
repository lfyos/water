package kernel_information;

import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class sort_part_information extends jason_creator
{
	private client_information ci;
	private scene_kernel sk;
	
	public void print()
	{
		jason_creator jc[]=new jason_creator[sk.part_cont.data_array.length];
		for(int i=0,ni=jc.length;i<ni;i++)
			jc[i]=new part_with_component_information(sk.part_cont.data_array[i],sk,ci);
		
		print("part_number",jc.length);
		print("part",jc);
	}
	public sort_part_information(scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		sk=my_sk;
		ci=my_ci;
	}
}
