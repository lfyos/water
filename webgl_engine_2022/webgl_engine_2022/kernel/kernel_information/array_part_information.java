package kernel_information;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class array_part_information extends jason_creator
{
	private client_information ci;
	private engine_kernel ek;
	
	public void print()
	{
		int n=0;
		if(ek.render_cont.renders!=null)
			n=ek.render_cont.renders.length;
		jason_creator jc[]=new jason_creator[n];
		for(int i=0,ni=jc.length;i<ni;i++)
			jc[i]=new one_array_part_information(i,
					ek.render_cont.renders[i].parts,ek,ci);
		
		print("render_number",ek.render_cont.renders.length);
		print("render_array",jc);
	}
	public array_part_information(engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
	}
}