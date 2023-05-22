package kernel_information;

import kernel_render.render;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_component.component_collector;

public class component_collector_with_component_information extends jason_creator
{
	private component_collector collector;
	private engine_kernel ek;
	private client_information ci;
	public void print()
	{
		render r;
		int number=0;
		jason_creator jc[]=new jason_creator[collector.part_number];
		
		if(collector.component_collector!=null)
			for(int i=0,ni=collector.component_collector.length;i<ni;i++)
				if(((r=ek.render_cont.renders.get(i))!=null)&&(collector.component_collector[i]!=null))
					for(int j=0,nj=collector.component_collector[i].length;j<nj;j++)
						if(collector.component_collector[i][j]!=null)
							jc[number++]=new link_component_list_information(r.parts.get(j),
								collector.part_component_number[i][j],collector.component_collector[i][j],ci);
			
		print("collector",new component_collector_information(collector,ci));
		print("component",jc);
	}
	
	public component_collector_with_component_information(
			component_collector my_collector,engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		
		collector=my_collector;
		ek=my_ek;
		ci=my_ci;
	}
}
