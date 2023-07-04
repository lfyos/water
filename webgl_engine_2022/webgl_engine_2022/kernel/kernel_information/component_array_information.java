package kernel_information;

import kernel_component.component;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class component_array_information extends jason_creator
{
	private engine_kernel ek;
	private client_information ci;
	public void print()
	{
		component comp[]=ek.component_cont.get_sort_component_array();
		jason_creator jc[]=new jason_creator[comp.length];
		for(int i=0,ni=jc.length;i<ni;i++)
			jc[i]=new component_information(comp[i],ci);
		print("component_number",jc.length);
		print("component_array",jc);
	}
	public component_array_information(engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
	}
}