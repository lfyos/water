package kernel_information;

import kernel_component.component;
import kernel_engine.client_information;

public class component_information extends jason_creator
{
	private component comp;
	private client_information ci;
	public void print()
	{
		part_information pi[]=new part_information[0];
		String component_name="unknown";
		int component_id=-1,child_number=0,driver_number=0;
		boolean dynamic_location_flag=false;

		if(comp!=null){
			dynamic_location_flag=comp.get_dynamic_location_flag();
			component_name=comp.component_name;
			component_id=comp.component_id;
			child_number=comp.children_number();
			driver_number=comp.driver_number();
			pi=new part_information[driver_number];
			for(int i=0;i<driver_number;i++)
				pi[i]=new part_information(comp.driver_array.get(i).component_part,ci);
			
		}
		print("component_name",			component_name);
		print("component_id",			component_id);
		print("relative_location",		comp.relative_location.get_location_data());
		print("move_location",			comp.move_location.get_location_data());
		print("child_number",			child_number);
		print("driver_number",			driver_number);
		print("dynamic_location_flag",	dynamic_location_flag);
		print("part",					pi);
	}
	public component_information(component my_comp,client_information my_ci)
	{
		super(my_ci.request_response);
		comp=my_comp;
		ci=my_ci;
	}
}
