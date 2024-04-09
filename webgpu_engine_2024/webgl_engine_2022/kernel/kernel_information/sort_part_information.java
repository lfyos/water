package kernel_information;

import kernel_engine.engine_kernel;
import kernel_engine.client_information;

public class sort_part_information extends jason_creator
{
	private client_information ci;
	private engine_kernel ek;
	
	public void print()
	{
		jason_creator jc[]=new jason_creator[ek.part_cont.data_array.length];
		for(int i=0,ni=jc.length;i<ni;i++)
			jc[i]=new part_with_component_information(ek.part_cont.data_array[i],ek,ci);
		
		print("part_number",jc.length);
		print("part",jc);
	}
	public sort_part_information(engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
	}
}
