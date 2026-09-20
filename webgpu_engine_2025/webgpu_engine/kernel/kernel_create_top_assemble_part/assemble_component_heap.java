package kernel_create_top_assemble_part;

import java.util.ArrayList;

import kernel_component.component;

public class assemble_component_heap
{
	private ArrayList<component> component_heap;
	private int part_number[];
	private String can_create_assemble_part_name[];
	
	private int compare_component(component s,component t)
	{
		int s_part_number	=part_number[s.component_id];
		int t_part_number	=part_number[t.component_id];
		return (s_part_number!=t_part_number)
				?(s_part_number-t_part_number)
				:s.part_name.compareTo(t.part_name);
	}
	public component get_heap_component()
	{
		int component_number;
		if((component_number=component_heap.size())<=0)
			return null;
		component ret_val=component_heap.get(0);
		component last_comp=component_heap.remove(--component_number);
		if(component_number<=0)
			return ret_val;
		component_heap.set(0,last_comp);
		
		for(int comp_id=0,child_id;comp_id<component_number;comp_id=child_id){
			int left_child_id	=comp_id+comp_id+1;
			int right_child_id	=comp_id+comp_id+2;
			if(left_child_id>=component_number)
				break;
			if(right_child_id>=component_number)
				child_id=left_child_id;
			else{
				component left_comp=component_heap.get(left_child_id);
				component right_comp=component_heap.get(right_child_id);
				child_id=(compare_component(left_comp,right_comp)>=0)
							?left_child_id:right_child_id;
			}
			component this_comp=component_heap.get(comp_id);
			component child_comp=component_heap.get(child_id);
			
			if(compare_component(this_comp,child_comp)>=0)
				break;
			component_heap.set(comp_id, child_comp);
			component_heap.set(child_id,this_comp);
		}
		return ret_val;
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

		if(can_create_assemble_part_name[comp.component_id]==null) {
			for(int i=0;i<children_number;i++)
				register_component(comp.children.get(i));
			return;
		}
		component_heap.add(comp);
		for(int parent_id,comp_id=component_heap.size()-1;comp_id>0;comp_id=parent_id){
			component parent_comp=component_heap.get(parent_id=(comp_id-1)/2);
			component this_comp=component_heap.get(comp_id);
			if(compare_component(parent_comp,this_comp)>=0)
				break;
			component_heap.set(comp_id,parent_comp);
			component_heap.set(parent_id,this_comp);
		}
	}
	public void split_large_assemble(int min_expand_part_number)
	{
		while(component_heap.size()>0){
			if(part_number[component_heap.get(0).component_id]<=min_expand_part_number)
				break;
			component expand_p=get_heap_component();
			for(int i=0,child_number=expand_p.children.size();i<child_number;i++)
				register_component(expand_p.children.get(i));
		}
	}
	public assemble_component_heap(int my_part_number[],String my_can_create_assemble_part_name[])
	{
		component_heap=new ArrayList<component>();
		part_number=my_part_number;
		can_create_assemble_part_name=my_can_create_assemble_part_name;
	}
}
