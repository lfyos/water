package kernel_information;

import kernel_component.component;
import kernel_engine.client_information;

public class component_tree_information extends jason_creator
{
	private component comp;
	private client_information ci;
	public void print()
	{
		jason_creator jc=new component_information(comp,ci);
		jason_creator child[]=new jason_creator[comp.children_number()];
		for(int i=0,ni=child.length;i<ni;i++)
			child[i]=new component_tree_information(comp.children[i],ci);
		print("component",jc);
		print("child",child);
	}
	public component_tree_information(component my_comp,client_information my_ci)
	{
		super(my_ci.request_response);
		comp=my_comp;
		ci=my_ci;
	}
}
