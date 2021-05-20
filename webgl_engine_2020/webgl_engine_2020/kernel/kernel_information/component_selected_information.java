package kernel_information;

import kernel_component.component;
import kernel_component.component_array;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class component_selected_information extends jason_creator
{
	private engine_kernel ek;
	private client_information ci;
	public void print()
	{
		component comp=ek.component_cont.root_component;
		component_array a=new component_array(comp.component_id+1);
		a.add_selected_component(comp);
		
		jason_creator jc[]=new jason_creator[a.component_number];
		for(int i=0,ni=jc.length;i<ni;i++)
			jc[i]=new component_information(a.comp[i],ci);
		print("component_number",jc.length);
		print("component_array",jc);
	}
	public component_selected_information(engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
	}
}
