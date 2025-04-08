package kernel_information;

import java.util.ArrayList;

import kernel_part.part;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class one_array_part_information extends jason_creator
{
	private client_information ci;
	private scene_kernel sk;
	private int render_id;
	private ArrayList<part> p;
	
	public void print()
	{
		jason_creator jc[]=new jason_creator[(p==null)?0:(p.size())];
		for(int i=0,ni=jc.length;i<ni;i++)
			jc[i]=new part_with_component_information(p.get(i),sk,ci);
		
		print("render_id",	render_id);
		print("part_number",jc.length);
		print("part_array",jc);
	}
	public one_array_part_information(int my_render_id,
			ArrayList<part> my_p,scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		render_id=my_render_id;
		p=my_p;
		sk=my_sk;
		ci=my_ci;
	}
}