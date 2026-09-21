package kernel_create_top_assemble_part;

import java.util.Comparator;

import kernel_component.component;
import kernel_common_class.heap_list;

public class assemble_component_heap
{
	class component_comparator implements Comparator<component>
	{
		public int compare(component s,component t)
		{
			int s_part_number=part_number[s.component_id];
			int t_part_number=part_number[t.component_id];
			return (s_part_number!=t_part_number)
						?(s_part_number-t_part_number)
						:s.part_name.compareTo(t.part_name);
		}
	};
	
	private heap_list<component> component_heap;
	private String assemble_part_name[];
	private int part_number[];

	public component get_heap_component()
	{
		return component_heap.extract_data(false);
	}
	public void register_component(component comp)
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

		if(assemble_part_name[comp.component_id]!=null)
			component_heap.insert_data(comp);
		else
			for(int i=0;i<children_number;i++)
				register_component(comp.children.get(i));
	}
	public void split_large_assemble(int min_expand_part_number)
	{
		for(component comp;(comp=component_heap.extract_data(true))!=null;){
			if(part_number[comp.component_id]<=min_expand_part_number)
				break;
			comp=component_heap.extract_data(false);
			for(int i=0,child_number=comp.children.size();i<child_number;i++)
				register_component(comp.children.get(i));
		};
	}
	public assemble_component_heap(String my_assemble_part_name[],int my_part_number[])
	{
		part_number=my_part_number;
		assemble_part_name=my_assemble_part_name;
		component_heap=new heap_list<component>(new component_comparator());
	}
}
