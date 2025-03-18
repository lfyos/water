package kernel_information;

import kernel_render.render;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_component.component_collector;

public class component_collector_with_component_information extends jason_creator
{
	private component_collector collector;
	private scene_kernel sk;
	private client_information ci;
	public void print()
	{
		render r;
		int number=0;
		jason_creator jc[]=new jason_creator[collector.part_number];
		
		if(collector.component_collector!=null)
			for(int i=0,ni=collector.component_collector.length;i<ni;i++)
				if(((r=sk.render_cont.renders.get(i))!=null)&&(collector.component_collector[i]!=null))
					for(int j=0,nj=collector.component_collector[i].length;j<nj;j++)
						if(collector.component_collector[i][j]!=null)
							jc[number++]=new link_component_list_information(r.parts.get(j),
								collector.part_component_number[i][j],collector.component_collector[i][j],ci);
			
		print("collector",new component_collector_information(collector,ci));
		print("component",jc);
	}
	
	public component_collector_with_component_information(
			component_collector my_collector,scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		
		collector=my_collector;
		sk=my_sk;
		ci=my_ci;
	}
}
