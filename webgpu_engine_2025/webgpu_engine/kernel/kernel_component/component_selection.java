package kernel_component;

import kernel_scene.scene_kernel;
import kernel_common_class.nanosecond_timer;

public class component_selection
{
	private nanosecond_timer 	current_time;
	private component_container component_cont;

	private void do_clear_selected_flag(component my_comp)
	{
		if(my_comp!=null){
			my_comp.uniparameter.selected_flag=false;
			for(int i=0,children_number=my_comp.children.size();i<children_number;i++)
				do_clear_selected_flag(my_comp.children.get(i));
		}
	}
	private void do_set_selected_flag(component my_comp)
	{
		if(my_comp!=null){
			long cur_time=current_time.nanoseconds();
			
			my_comp.uniparameter.selected_time=cur_time;
			for(component comp=my_comp;comp!=null;
					comp=component_cont.get_component(comp.parent_component_id))
				if(comp.uniparameter.selected_flag)
					return;

			my_comp.uniparameter.selected_flag=true;
			for(component comp=component_cont.get_component(my_comp.parent_component_id);
				comp!=null;comp=component_cont.get_component(comp.parent_component_id))
			{
				int ni=comp.children.size();
				for(int i=0;i<ni;i++)
					if(!(comp.children.get(i).uniparameter.selected_flag))
						return;
				for(int i=0;i<ni;i++)
					comp.children.get(i).uniparameter.selected_flag=false;
				comp.uniparameter.selected_flag=true;
			}
		}
	}
	private void clear_selected_flag_without_brother(component my_comp)
	{
		component_link_list component_link_list_head=null;
		for(component my_parent=my_comp;my_parent!=null;
				my_parent=component_cont.get_component(my_parent.parent_component_id))
		{
			component_link_list_head=new component_link_list(my_parent,0,component_link_list_head);
			if(my_parent.uniparameter.selected_flag){
				my_parent.uniparameter.selected_flag=false;
				for(;component_link_list_head.next_list_item!=null;
						component_link_list_head=component_link_list_head.next_list_item)
					for(int i=0,ni=component_link_list_head.comp.children.size();i<ni;i++) {
						component my_child=component_link_list_head.comp.children.get(i);
						if(component_link_list_head.next_list_item.comp!=my_child)
							my_child.uniparameter.selected_flag=true;
					}
				return;
			}
		}
	}
	public void set_selected_flag(component my_comp)
	{
		do_clear_selected_flag(my_comp);
		do_set_selected_flag(my_comp);
	}
	public void clear_selected_flag(component my_comp)
	{
		if(my_comp!=null){
			for(component my_parent=my_comp;my_parent!=null;
					my_parent=component_cont.get_component(my_parent.parent_component_id))
				if(my_parent.uniparameter.selected_flag){
					clear_selected_flag_without_brother(my_comp);
					return;
				}
			do_clear_selected_flag(my_comp);
		}
	}
	public void switch_selected_flag(component my_comp)
	{
		for(component my_parent=my_comp;my_parent!=null;
				my_parent=component_cont.get_component(my_parent.parent_component_id))
			if(my_parent.uniparameter.selected_flag){
				clear_selected_flag_without_brother(my_comp);
				return;
			}
		do_clear_selected_flag(my_comp);
		do_set_selected_flag(my_comp);
	}
	public void set_parent_selected(component my_comp)
	{
		component my_parent=component_cont.get_component(my_comp.parent_component_id);
		my_parent=(my_parent==null)?my_comp:my_parent;
		do_clear_selected_flag(my_parent);
		do_set_selected_flag(my_parent);
	}
	public void set_child_selected(component my_comp)
	{
		for(component my_parent=my_comp;my_parent!=null;
				my_parent=component_cont.get_component(my_parent.parent_component_id))
		{
			if(my_parent.uniparameter.selected_flag) {
				clear_selected_flag_without_brother(my_comp);
				break;
			}
		}
		if(my_comp.children.size()<=0){
			do_set_selected_flag(my_comp);
			return;
		}
		component last_comp=my_comp.children.get(0);
		for(int i=1,ni=my_comp.children.size();i<ni;i++) {
			component my_child_comp=my_comp.children.get(i);
			if(my_child_comp.uniparameter.selected_time>last_comp.uniparameter.selected_time)
				last_comp=my_child_comp;
		}
		do_clear_selected_flag(my_comp);
		do_set_selected_flag(last_comp);
	}
	public void set_moved_descendant_selected(component my_comp)
	{
		if(my_comp!=null){
			if(my_comp.move_location.is_not_identity_matrix()) {
				do_clear_selected_flag(my_comp);
				do_set_selected_flag(my_comp);
			}else
				for(int i=0,n=my_comp.children.size();i<n;i++)
					set_moved_descendant_selected(my_comp.children.get(i));
		}
	}
	public void set_collector_selected(component_collector collector)
	{
		if(collector!=null)
			for(int i=0,ni=collector.component_collector.length;i<ni;i++)
				for(int j=0,nj=collector.component_collector[i].length;j<nj;j++)
					for(component_link_list p=collector.component_collector[i][j];p!=null;p=p.next_list_item) {
						do_clear_selected_flag(p.comp);
						do_set_selected_flag(p.comp);
					}
	}
	public void set_component_container_selected(component_array comp_cont)
	{
		if(comp_cont!=null)
			for(int i=0,ni=comp_cont.comp_list.size();i<ni;i++) {
				component my_comp=comp_cont.comp_list.get(i);
				do_clear_selected_flag(my_comp);
				do_set_selected_flag(my_comp);
			}
	}
	public component_selection(scene_kernel sk)
	{
		current_time	=sk.current_time;
		component_cont	=sk.component_cont;
		sk.mark_reset_flag();
	}
}