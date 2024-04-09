package kernel_information;

import kernel_engine.engine_kernel;
import kernel_part.part;
import kernel_engine.client_information;
import kernel_component.component;

public class part_with_component_information extends jason_creator
{
	private engine_kernel ek;
	private client_information ci;
	
	private part p;
	
	public void print()
	{
		int ids[][]=ek.component_cont.part_component_id_and_driver_id[p.render_id][p.part_id];
		print("part",new part_information(p,ci));
		print("component_number",ids.length);
		
		jason_creator jc[]=new jason_creator[ids.length];
		for(int i=0,ni=jc.length;i<ni;i++){
			int component_id=ids[i][0],driver_id=ids[i][1];
			component comp=ek.component_cont.get_component(component_id);
			if(comp==null)
				jc[i]=new component_name_and_id("not_found_component",-1,-1,ci);
			else
				jc[i]=new component_name_and_id(comp.component_name,component_id,driver_id,ci);
		}
		print("component_array",jc);
	}
	public part_with_component_information(part my_p,engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
		p=my_p;
	}
}
