package kernel_create_top_assemble_part;

import java.util.ArrayList;

import kernel_component.component;

public class assemble_component_heap
{
	private ArrayList<component> component_heap;
	
	public component get_component_0()
	{
		return component_heap.get(0);
	}
	public component get_heap_component(int part_number[])
	{
		int component_number;
		if((component_number=component_heap.size())<=0)
			return null;
		component ret_val=component_heap.get(0);
		component last_comp=component_heap.remove(--component_number);
		if(component_number<=0)
			return ret_val;
		component_heap.set(0,last_comp);
		
		for(int comp_id=0;comp_id<component_number;){
			int left_child_id	=comp_id+comp_id+1;
			int right_child_id	=comp_id+comp_id+2;
			if(left_child_id>=component_number)
				break;
			int child_id;
			if(right_child_id>=component_number)
				child_id=left_child_id;
			else{
				component left_child_comp=component_heap.get(left_child_id);
				component right_child_comp=component_heap.get(right_child_id);
				
				int left_child_number	=part_number[left_child_comp.component_id];
				int right_child_number	=part_number[right_child_comp.component_id];
				if(left_child_number>right_child_number)
					child_id=left_child_id;
				else if(left_child_number<right_child_number)
					child_id=right_child_id;
				else{
					String left_child_name	=left_child_comp.part_name;
					String right_child_name	=right_child_comp.part_name;
					child_id=(left_child_name.compareTo(right_child_name)<=0)?left_child_id:right_child_id;
				}
			}
			component this_comp=component_heap.get(comp_id);
			component child_comp=component_heap.get(child_id);
			
			int this_number =part_number[this_comp.component_id];
			int child_number=part_number[child_comp.component_id];
			
			if(this_number>child_number)
				break;
			if(this_number==child_number){
				String this_name	=this_comp.part_name;
				String child_name	=child_comp.part_name;
				if(this_name.compareTo(child_name)<=0)
					break;
			}
			component_heap.set(comp_id, child_comp);
			component_heap.set(child_id,this_comp);
			comp_id=child_id;
		}
		return ret_val;
	}
	public void register_component(component comp,String can_create_assemble_part_name[],int part_number[])
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
				register_component(comp.children.get(i),can_create_assemble_part_name,part_number);
			return;
		}
		component_heap.add(comp);
		for(int parent_id,comp_id=component_heap.size()-1;comp_id>0;comp_id=parent_id){
			parent_id=(comp_id-1)/2;
			component parent_comp	=component_heap.get(parent_id);
			component this_comp		=component_heap.get(comp_id);
			int parent_part_number	=part_number[parent_comp.component_id];
			int this_part_number	=part_number[this_comp.component_id];
			if(parent_part_number>this_part_number)
				break;
			if(parent_part_number==this_part_number)
				if(parent_comp.part_name.compareTo(this_comp.part_name)<=0)
					break;
			component_heap.set(comp_id,parent_comp);
			component_heap.set(parent_id,this_comp);
		}
	}
	
	public void split_large_assemble(double create_top_part_expand_ratio,
			String can_create_assemble_part_name[],int part_number[],int all_part_number)
	{
		int min_expand_part_number=(int)(((double)all_part_number)/create_top_part_expand_ratio);
		while(component_heap.size()>0){
			int max_part_number=part_number[component_heap.get(0).component_id];
			if(max_part_number<=min_expand_part_number)
				break;
			component expand_p=get_heap_component(part_number);
			for(int i=0,child_number=expand_p.children.size();i<child_number;i++)
				register_component(expand_p.children.get(i),
						can_create_assemble_part_name,part_number);
		}
	}
	public assemble_component_heap()
	{
		component_heap=new ArrayList<component>();
	}
}
