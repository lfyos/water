package kernel_component;

import java.util.ArrayList;

import kernel_transformation.box;
import kernel_part.part;

public class component_array 
{
	public ArrayList<component>comp_list;
	private box exist_container_box,all_container_box;
	
	public box get_box()
	{
		return (exist_container_box!=null)?exist_container_box:all_container_box;
	}
	public void clear_compoment()
	{
		comp_list.clear();
		exist_container_box=null;
		all_container_box=null;
	}
	public component []get_component()
	{
		int component_number;
		return ((component_number=comp_list.size())<=0)?null:(comp_list.toArray(new component[component_number]));
	}
	public int add_part_list_component(component comp)
	{
		if(comp.uniparameter.part_list_flag){
			int child_number;
			if((child_number=comp.children_number())<=0)
				add_component(comp);
			else
				for(int i=0;i<child_number;i++)
					add_part_list_component(comp.children[i]);
		}
		return comp_list.size();
	}
	public int add_visible_component(component comp,int parameter_channel_id,boolean part_list_flag)
	{
		if(comp.get_effective_display_flag(parameter_channel_id)){
			int child_number;
			if((child_number=comp.children_number())<=0){
				if(comp.uniparameter.part_list_flag|part_list_flag)
					add_component(comp);
			}else
				for(int i=0;i<child_number;i++)
					add_visible_component(comp.children[i],parameter_channel_id,part_list_flag);
		}
		return comp_list.size();
	}
	public boolean add_component(component new_comp)
	{
		if(new_comp==null)
			return false;
		box my_box;
		if((my_box=new_comp.get_component_box(false))==null)
			my_box=new_comp.get_component_box(true);
		else if(exist_container_box==null)
			exist_container_box=my_box;
		else
			exist_container_box=exist_container_box.add(my_box);
		
		if(my_box!=null) {
			if(all_container_box==null)
				all_container_box=my_box;
			else
				all_container_box=all_container_box.add(my_box);
		}
		comp_list.add(comp_list.size(),new_comp);
		return true;
	}
	public int add_selected_component(component new_comp,boolean do_one_child_flag)
	{
		if(new_comp==null)
			return comp_list.size();
		int child_number=new_comp.children_number();
		if(new_comp.uniparameter.effective_selected_flag)
			if((child_number!=1)||do_one_child_flag) {
				add_component(new_comp);
				return comp_list.size();
			}
		for(int i=0;i<child_number;i++)
			add_selected_component(new_comp.children[i],do_one_child_flag);
		return comp_list.size();
	}
	public int add_collector(component_collector my_collector)
	{
		if(my_collector!=null)
			for(int i=0,ni=my_collector.component_collector.length;i<ni;i++)
				if(my_collector.component_collector[i]!=null)
					for(int j=0,nj=my_collector.component_collector[i].length;j<nj;j++) {
						component_link_list p=my_collector.component_collector[i][j];
						while(p!=null){
							add_component(p.comp);
							p=p.next_list_item;
						}
					}
		return comp_list.size();
	}
	public int make_to_children()
	{
		component comp;
		for(int i=0;i<comp_list.size();i++) {
			for(comp=comp_list.get(i);comp.children_number()>0;comp=comp.children[0])
				for(int j=1,nj=comp.children_number();j<nj;j++)
					add_component(comp.children[j]);
			comp_list.set(i,comp);
		}
		return comp_list.size();
	}
	public void delete_same_component(component_container component_cont)
	{
		component my_comp;
		ArrayList<component>new_comp_list=new ArrayList<component>();
		boolean exist_flag[]=new boolean[component_cont.root_component.component_id+1];
		for(int i=0,ni=exist_flag.length;i<ni;i++)
			exist_flag[i]=true;
		for(int i=0,ni=comp_list.size();i<ni;i++)
			if((my_comp=comp_list.get(i))!=null)
				if(exist_flag[my_comp.component_id]) {
					exist_flag[my_comp.component_id]=false;
					new_comp_list.add(new_comp_list.size(),my_comp);
				}
		comp_list=new_comp_list;
	}
	public int make_to_ancestor(component_container component_cont)
	{
		make_to_children();
		delete_same_component(component_cont);
		
		int mark_flag[]		=new int[component_cont.root_component.component_id+1];
		int collect_flag[]	=new int[component_cont.root_component.component_id+1];

		for(boolean exit_flag=true;exit_flag;){
			component comp,parent;
			for(int i=0,ni=comp_list.size();i<ni;i++) {
				comp=comp_list.get(i);
				if((parent=component_cont.get_component(comp.parent_component_id))!=null){
					mark_flag	[parent.component_id]=0;
					collect_flag[parent.component_id]=0;
				}
			}
			for(int i=0,ni=comp_list.size();i<ni;i++) {
				comp=comp_list.get(i);
				if((parent=component_cont.get_component(comp.parent_component_id))!=null)
					mark_flag[parent.component_id]++;
			}
			exit_flag=false;
			for(int i=0;i<comp_list.size();i++){
				comp=comp_list.get(i);
				if((parent=component_cont.get_component(comp.parent_component_id))!=null)
					if((mark_flag[parent.component_id])==(parent.children_number())){
						exit_flag=true;
						if((collect_flag[parent.component_id]++)==0)
							comp_list.set(i,parent);
						else{
							int last_id=comp_list.size()-1;
							comp_list.set(i,comp_list.get(last_id));
							comp_list.remove(last_id);
						}
					}
			}
		}
		return comp_list.size();
	}
	public int remove_not_in_part_list_component(boolean part_list_flag_effective_flag)
	{
		int execute_number=0;
		for(int i=0;i<comp_list.size();){
			component comp=comp_list.get(i);
			int driver_number=comp.driver_number();
			boolean part_list_flag=true;
			if(part_list_flag_effective_flag)
				part_list_flag=comp.uniparameter.part_list_flag;
			boolean skip_flag=(part_list_flag&&(driver_number>0));
			if(skip_flag){
				int top_number=0;
				for(int j=0;j<driver_number;j++){
					part my_part=comp.driver_array.get(j).component_part;
					if(my_part.is_top_box_part())
						top_number++;
				}
				if(top_number==driver_number)
					skip_flag=false;
			}
			if(skip_flag)
				i++;
			else{
				int last_id=comp_list.size()-1;
				comp_list.set(i,comp_list.get(last_id));
				comp_list.remove(last_id);
				
				for(int j=0,nj=comp.children_number();j<nj;j++)
					add_component(comp.children[j]);
				execute_number++;
			}
		}
		return execute_number;
	}
	public component delete(int array_id)
	{
		return ((array_id<0)||(array_id>=comp_list.size()))?null:(comp_list.remove(array_id));
	}
	public component expand(int array_id)
	{
		component expand_comp;
		if((expand_comp=delete(array_id))!=null)
			for(int i=0,ni=expand_comp.children_number();i<ni;i++)
				add_component(expand_comp.children[i]);
		return expand_comp;
	}
	public component_array()
	{
		comp_list=new ArrayList<component>();
		
		exist_container_box=null;
		all_container_box=null;
	}
	public component_array(component_array my_component_array)
	{
		comp_list=new ArrayList<component>();
		exist_container_box=null;
		all_container_box=null;
		for(int i=0,ni=my_component_array.comp_list.size();i<ni;i++)
			add_component(my_component_array.comp_list.get(i));
	}
}