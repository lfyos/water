package kernel_component;


import kernel_engine.component_container;
import kernel_component.component_collector;
import kernel_transformation.box;
import kernel_part.part;

public class component_array 
{
	public component []comp;
	public int component_number;

	private box exist_container_box,all_container_box;
	
	private void init(int max_component_number)
	{
		if(max_component_number<=0)
			max_component_number=1;
		comp=new component[max_component_number];
		component_number=0;
		exist_container_box=null;
		all_container_box=null;
		for(int i=0;i<comp.length;i++)
			comp[i]=null;
	}	
	public box get_box()
	{
		return (exist_container_box!=null)?exist_container_box:all_container_box;
	}
	public void clear_compoment()
	{
		component_number=0;
		exist_container_box=null;
		all_container_box=null;
	}
	public component []get_component()
	{
		if(component_number<=0)
			return null;
		component ret_val[]=new component[component_number];
		for(int i=0;i<component_number;i++)
			ret_val[i]=comp[i];
		return ret_val;
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
		return component_number;
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
		return component_number;
	}
	public boolean add_component(component new_comp)
	{
		box my_box;
		if(component_number<comp.length)
			if(new_comp!=null){
				if((my_box=new_comp.get_component_box(false))==null)
					my_box=new_comp.get_component_box(true);
				else if(exist_container_box==null)
					exist_container_box=my_box;
				else
					exist_container_box=exist_container_box.add(my_box);
				if(all_container_box==null)
					all_container_box=my_box;
				else
					all_container_box=all_container_box.add(my_box);
				comp[component_number++]=new_comp;
				return true;
			}
		return false;
	}
	public int add_selected_component(component new_comp,boolean do_one_child_flag)
	{
		if(new_comp==null)
			return component_number;
		int child_number=new_comp.children_number();
		if(new_comp.uniparameter.effective_selected_flag)
			if((child_number!=1)||do_one_child_flag) {
				add_component(new_comp);
				return component_number;
			}
		for(int i=0;i<child_number;i++)
			add_selected_component(new_comp.children[i],do_one_child_flag);
		return component_number;
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
		return component_number;
	}
	public int make_to_children()
	{
		for(int i=0;i<component_number;i++)
			while(comp[i].children_number()>0){
				for(int j=1,nj=comp[i].children_number();j<nj;j++)
					add_component(comp[i].children[j]);
				comp[i]=comp[i].children[0];
			}
		return component_number;
	}
	public void delete_same_component(component_container component_cont)
	{
		int same_flag[]		=new int[component_cont.root_component.component_id+1];
		for(int i=0,ni=same_flag.length;i<ni;i++)
			same_flag[i]=0;
		for(int i=0;i<component_number;i++)
			if((same_flag[comp[i].component_id]++)>0)
				delete(i--);
	}
	public int make_to_ancestor(component_container component_cont)
	{
		delete_same_component(component_cont);
		
		int mark_flag[]		=new int[component_cont.root_component.component_id+1];
		int collect_flag[]	=new int[component_cont.root_component.component_id+1];

		for(boolean exit_flag=true;exit_flag;){
			component parent;
			for(int i=0;i<component_number;i++)
				if((parent=component_cont.get_component(comp[i].parent_component_id))!=null){
					mark_flag	[parent.component_id]=0;
					collect_flag[parent.component_id]=0;
				}
			for(int i=0;i<component_number;i++)
				if((parent=component_cont.get_component(comp[i].parent_component_id))!=null)
					mark_flag[parent.component_id]++;
			exit_flag=false;
			for(int i=0;i<component_number;i++)
				if((parent=component_cont.get_component(comp[i].parent_component_id))!=null)
					if((mark_flag[parent.component_id])==(parent.children_number())){
						exit_flag=true;
						if((collect_flag[parent.component_id]++)==0)
							comp[i]=parent;
						else
							comp[i--]=comp[--component_number];
					}
		}
		return component_number;
	}
	public int remove_not_in_part_list_component(boolean part_list_flag_effective_flag)
	{
		int execute_number=0;
		for(int i=0;i<component_number;){
			int driver_number=comp[i].driver_number();
			boolean part_list_flag=true;
			if(part_list_flag_effective_flag)
				part_list_flag=comp[i].uniparameter.part_list_flag;
			boolean skip_flag=(part_list_flag&&(driver_number>0));
			if(skip_flag){
				int top_number=0;
				for(int j=0;j<driver_number;j++){
					part my_part=comp[i].driver_array[j].component_part;
					if(my_part.is_top_box_part())
						top_number++;
				}
				if(top_number==driver_number)
					skip_flag=false;
			}
			if(skip_flag)
				i++;
			else{
				component p=comp[i];
				comp[i]=comp[--component_number];
				comp[component_number]=null;
				
				for(int j=0,nj=p.children_number();j<nj;j++)
					add_component(p.children[j]);
				execute_number++;
			}
		}
		return execute_number;
	}
	public component delete(int array_id)
	{
		component delete_comp=null;
		if((array_id>=0)&&(array_id<component_number)){
			delete_comp=comp[array_id];
			component_number--;
			comp[array_id]=comp[component_number];
			comp[component_number]=null;
		}
		return delete_comp;
	}
	public component expand(int array_id)
	{
		component expand_comp;
		if((expand_comp=delete(array_id))!=null)
			for(int i=0,ni=expand_comp.children_number();i<ni;i++)
				add_component(expand_comp.children[i]);
		return expand_comp;
	}
	public component_array(int max_component_number)
	{
		init(max_component_number);
	}
	public component_array(component_array my_component_array)
	{
		init(my_component_array.comp.length);
		for(int i=0,ni=my_component_array.component_number;i<ni;i++)
			add_component(my_component_array.comp[i]);
	}
}