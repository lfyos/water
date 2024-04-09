package kernel_common_class;

import kernel_component.component;
import kernel_component.component_collector;
import kernel_component.component_array;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class component_collector_jason_component
{
	private void output(common_writer cw,component_array comp_array)
	{
		cw.println("[");
		for(int i=0,ni=comp_array.comp_list.size();i<ni;i++) {
			component comp=comp_array.comp_list.get(i);
			cw.println("	{");
			
			cw.println("		\"part_name\"		:	",		jason_string.change_string(comp.part_name)+",");
			cw.println("		\"component_name\"	:	",		jason_string.change_string(comp.component_name)+",");
			cw.println("		\"component_id\"		:	",	comp.component_id+",");

			cw.print("		\"relative_location\"	:	[");
			double p[]=comp.relative_location.get_location_data();
			for(int j=0,nj=p.length;j<nj;j++)
				cw.print((j==0)?"":",",p[j]);
			cw.println("],");
			
			cw.print("		\"move_location\"		:	[");
			p=comp.move_location.get_location_data();
			for(int j=0,nj=p.length;j<nj;j++)
				cw.print((j==0)?"":",",p[j]);
			cw.println("]");
	
			cw.println((i==(ni-1))?"	}":"	},");
		}
		cw.println("]");
	}
	
	public component_collector_jason_component(boolean flag,
		component_collector collector,client_information ci,engine_kernel ek)
	{
		component_array comp_array=new component_array();
		comp_array.add_collector(collector);
		if(flag)
			comp_array.make_to_ancestor(ek.component_cont);
		else
			comp_array.make_to_children();
		output(ci.request_response,comp_array);
	}
}
