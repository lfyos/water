package kernel_create_top_assemble_part;

import kernel_component.component;

public class create_part_number 
{
	public int part_number[],all_part_number,give_up_number;
	
	private int caculate_part_number(component comp)
	{
		int children_number;
		if((children_number=comp.children.size())<=0){
			if(comp.driver_array.size()>0)
				if(comp.get_component_box(false)!=null){
					part_number[comp.component_id]=1;
					all_part_number++;
					return 1;
				}
			part_number[comp.component_id]=0;
			give_up_number++;
			return 0;
		}else {
			part_number[comp.component_id]=0;
			for(int i=0;i<children_number;i++)
				part_number[comp.component_id]+=caculate_part_number(comp.children.get(i));
			return part_number[comp.component_id];
		}
	}
	public create_part_number(component comp,int component_number)
	{
		part_number=new int[component_number];
		for(int i=0;i<component_number;i++)
			part_number[i]=0;
		caculate_part_number(comp);
	}
}
