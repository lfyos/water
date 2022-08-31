package kernel_information;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_part.part;

public class one_array_part_information extends jason_creator
{
	private client_information ci;
	private engine_kernel ek;
	private int render_id;
	private part p[];
	
	public void print()
	{
		jason_creator jc[]=new jason_creator[(p==null)?0:(p.length)];
		for(int i=0,ni=jc.length;i<ni;i++)
			jc[i]=new part_with_component_information(p[i],ek,ci);
		
		print("render_id",	render_id);
		print("part_number",jc.length);
		print("part_array",jc);
	}
	public one_array_part_information(int my_render_id,
			part my_p[],engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		render_id=my_render_id;
		p=my_p;
		ek=my_ek;
		ci=my_ci;
	}
}