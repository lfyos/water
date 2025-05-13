package kernel_information;

import kernel_component.component;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class component_tree_information extends jason_creator
{
	private component comp;
	private scene_kernel sk;
	private client_information ci;
	
	public void print()
	{
		jason_creator jc=new component_information(comp,sk,ci);
		jason_creator child[]=new jason_creator[comp.children.size()];
		for(int i=0,ni=child.length;i<ni;i++)
			child[i]=new component_tree_information(comp.children.get(i),sk,ci);
		print("component",jc);
		print("child",child);
	}
	public component_tree_information(component my_comp,scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		comp=my_comp;
		sk=my_sk;
		ci=my_ci;
	}
}
