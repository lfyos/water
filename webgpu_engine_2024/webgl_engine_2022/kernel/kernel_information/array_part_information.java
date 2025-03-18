package kernel_information;

import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class array_part_information extends jason_creator
{
	private client_information ci;
	private scene_kernel sk;
	
	public void print()
	{
		int n=0;
		if(sk.render_cont.renders!=null)
			n=sk.render_cont.renders.size();
		jason_creator jc[]=new jason_creator[n];
		for(int i=0,ni=jc.length;i<ni;i++)
			jc[i]=new one_array_part_information(i,
					sk.render_cont.renders.get(i).parts,sk,ci);
		
		print("render_number",sk.render_cont.renders.size());
		print("render_array",jc);
	}
	public array_part_information(scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		sk=my_sk;
		ci=my_ci;
	}
}