package kernel_create_top_assemble_part;

import java.util.Comparator;

import kernel_component.component;
import kernel_common_class.heap_list;

class component_comparator implements Comparator<component>
{
	private int part_number[];
	
	public int compare(component s,component t)
	{
		int ret_val;
		if((ret_val=part_number[t.component_id]-part_number[s.component_id])==0)
			ret_val=t.part_name.compareTo(s.part_name);
		return ret_val;
	}
	public component_comparator(int my_part_number[])
	{
		part_number=my_part_number;
	}
};

public class assemble_component_heap extends heap_list<component>
{
	private int part_number[];
	private String reference_part_name[];
	
	private void register_component(component comp)
	{
		int children_number;
		while((children_number=comp.children.size())==1)
			comp=comp.children.get(0);
		if(children_number<=0)
			return;
		if(part_number[comp.component_id]<=1)
			return;
		if(comp.driver_array.size()>0)
			return;

		if(reference_part_name[comp.component_id]!=null)
			insert_heap_data(comp);
		else
			for(int i=0;i<children_number;i++)
				register_component(comp.children.get(i));
	}
	public assemble_component_heap(
			int my_part_number[],String my_reference_part_name[],
			component my_root_component,int my_min_expand_part_number)
	{
		super(new component_comparator(my_part_number));
		
		part_number			=my_part_number;
		reference_part_name	=my_reference_part_name;
		
		register_component(my_root_component);
		
		for(component comp;(comp=get_heap_top_data())!=null;){
			if(part_number[comp.component_id]<=my_min_expand_part_number)
				break;
			for(component child_comp:extract_heap_data().children)
				register_component(child_comp);
		}
	}
}
