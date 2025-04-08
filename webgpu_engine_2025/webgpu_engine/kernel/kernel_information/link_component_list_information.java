package kernel_information;

import kernel_component.component_link_list;
import kernel_part.part;
import kernel_scene.client_information;

public class link_component_list_information extends jason_creator
{
	private part pa;
	private component_link_list cll;
	private client_information ci;
	private int component_number;
	public void print()
	{
		jason_creator jc[]=new jason_creator[component_number];
		component_link_list pp=cll;
		for(int i=0;i<component_number;i++,pp=pp.next_list_item)
			jc[i]=new one_component_list_information(pp,ci);
		print("component_part",new part_information(pa,ci));
		print("component_number",component_number);
		print("component_array",jc);
	}
	public link_component_list_information(part my_pa,int my_component_number,
			component_link_list my_cll,client_information my_ci)
	{
		super(my_ci.request_response);
		pa=my_pa;
		component_number=my_component_number;
		cll=my_cll;
		ci=my_ci;
	}
}
