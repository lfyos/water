package kernel_information;

import kernel_component.component;
import kernel_component.component_array;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class component_selected_information extends jason_creator
{
	private scene_kernel sk;
	private client_information ci;
	private boolean do_one_child_flag;
	public void print()
	{
		component comp=sk.component_cont.root_component;
		component_array a=new component_array();
		a.add_selected_component(comp,do_one_child_flag);
		
		jason_creator jc[]=new jason_creator[a.comp_list.size()];
		for(int i=0,ni=jc.length;i<ni;i++)
			jc[i]=new component_information(a.comp_list.get(i),ci);
		print("component_number",jc.length);
		print("component_array",jc);
	}
	public component_selected_information(scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		String str;
		sk=my_sk;
		ci=my_ci;
		do_one_child_flag=false;
		if((str=ci.request_response.get_parameter("child_flag"))!=null)
			switch(str.trim().toLowerCase()) {
			case "true":
			case "yes":
				do_one_child_flag=true;
				break;
			};
	}
}
