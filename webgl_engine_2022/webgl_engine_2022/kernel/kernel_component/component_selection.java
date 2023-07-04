package kernel_component;

import kernel_common_class.nanosecond_timer;
import kernel_engine.engine_kernel;

public class component_selection
{
	private nanosecond_timer current_time;

	private void do_clear_selected_flag(component my_comp)
	{
		if(my_comp==null)
			return;
		for(int i=0,children_number=my_comp.children_number();i<children_number;i++)
			do_clear_selected_flag(my_comp.children[i]);
		my_comp.uniparameter.selected_flag=false;
	}
	private void clear_selected_flag_without_brother(component my_comp,component_container component_cont)
	{
		if(my_comp!=null){
			component_link_list p=null;
			for(component comp=my_comp;comp!=null;comp=component_cont.get_component(comp.parent_component_id))
				if((p=new component_link_list(comp,0,p)).comp.uniparameter.selected_flag){
					do_clear_selected_flag(my_comp=p.comp);
					for(;p.next_list_item!=null;p=p.next_list_item)
						for(int i=0;i<(p.comp.children.length);i++)
							if(p.comp.children[i]!=(p.next_list_item.comp))
								do_set_selected_flag(p.comp.children[i],component_cont);
					
					component parent_comp=component_cont.get_component(comp.parent_component_id);
					if(parent_comp==null)
						parent_comp=my_comp;
					break;
				}
		}
	}
	private void do_set_selected_flag(component my_comp,component_container component_cont)
	{
		if(my_comp==null)
			return;
		
		long cur_time=current_time.nanoseconds();
		
		my_comp.uniparameter.selected_time=cur_time+1;
		for(component p=my_comp;p!=null;p=component_cont.get_component(p.parent_component_id))
			if(p.uniparameter.selected_flag)
				return;

		my_comp.uniparameter.selected_flag=true;
		for(component p=component_cont.get_component(my_comp.parent_component_id);
			p!=null;p=component_cont.get_component(p.parent_component_id))
		{
			int n=p.children.length;
			for(int i=0;i<n;i++)
				if(!(p.children[i].uniparameter.selected_flag))
					return;
			for(int i=0;i<n;i++)
				p.children[i].uniparameter.selected_flag=false;
			p.uniparameter.selected_flag=true;
			p.uniparameter.selected_time=cur_time;
		}
	}
	public void set_selected_flag(component my_comp,component_container component_cont)
	{
		do_clear_selected_flag(my_comp);
		do_set_selected_flag(my_comp,component_cont);
	}
	public void clear_selected_flag(component_container component_cont)
	{
		do_clear_selected_flag(component_cont.root_component);
	}
	public void clear_selected_flag(component my_comp,component_container component_cont)
	{
		if(my_comp!=null){
			for(component comp=my_comp;comp!=null;comp=component_cont.get_component(comp.parent_component_id))
				if(comp.uniparameter.selected_flag){
					clear_selected_flag_without_brother(my_comp,component_cont);
					break;
				}
			do_clear_selected_flag(my_comp);
		}
	}
	public void switch_selected_flag(component my_comp,component_container component_cont)
	{
		for(component comp=my_comp;comp!=null;comp=component_cont.get_component(comp.parent_component_id))
			if(comp.uniparameter.selected_flag){
				clear_selected_flag_without_brother(my_comp,component_cont);
				return;
			}
		set_selected_flag(my_comp,component_cont);
	}
	public void set_parent_selected(component_container component_cont)
	{
		component my_comp;
		if((my_comp=component_cont.search_component())==null)
			return;
		component parent=component_cont.get_component(my_comp.parent_component_id);
		my_comp=(parent==null)?my_comp:parent;
		set_selected_flag(my_comp,component_cont);
	}
	public void set_child_selected(component_container component_cont)
	{
		component my_comp;
		if((my_comp=component_cont.search_component())==null)
			return;
		for(component comp=my_comp;comp!=null;comp=component_cont.get_component(comp.parent_component_id))
			if(comp.uniparameter.selected_flag) {
				switch_selected_flag(my_comp,component_cont);
				break;
			}
		if(my_comp.children==null)
			set_selected_flag(my_comp,component_cont);
		else if(my_comp.children.length<=0)
			set_selected_flag(my_comp,component_cont);
		else{
			int last_id=0;
			long last_time=my_comp.children[0].uniparameter.selected_time;
			for(int i=1,ni=my_comp.children.length;i<ni;i++)
				if(my_comp.children[i].uniparameter.selected_time>last_time){
					last_id=i;
					last_time=my_comp.children[last_id].uniparameter.selected_time;
				}
			do_set_selected_flag(my_comp.children[last_id],component_cont);
		}
	}
	public void set_moved_descendant_selected(component comp,component_container component_cont)
	{
		if(comp!=null){
			if(comp.move_location.is_not_identity_matrix())
				set_selected_flag(comp,component_cont);
			else
				for(int i=0,n=comp.children_number();i<n;i++)
					set_moved_descendant_selected(comp.children[i],component_cont);
		}
	}
	public void set_collector_selected(component_collector collector,component_container component_cont)
	{
		if(collector==null)
			return;
		for(int i=0,ni=collector.component_collector.length;i<ni;i++)
			for(int j=0,nj=collector.component_collector[i].length;j<nj;j++)
				for(component_link_list p=collector.component_collector[i][j];p!=null;p=p.next_list_item)
					set_selected_flag(p.comp,component_cont);
	}
	public void set_component_container_selected(component_array comp_cont,component_container component_cont)
	{
		for(int i=0,ni=comp_cont.comp_list.size();i<ni;i++)
			set_selected_flag(comp_cont.comp_list.get(i),component_cont);
	}
	public component_selection(engine_kernel ek)
	{
		current_time=ek.current_time;
		ek.mark_reset_flag();
	}
}