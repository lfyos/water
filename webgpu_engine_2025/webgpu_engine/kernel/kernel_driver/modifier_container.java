package kernel_driver;

import java.util.ArrayList;
import java.util.Comparator;

import kernel_scene.scene_kernel;
import kernel_scene.client_information;
import kernel_common_class.tree_search_container;

public class modifier_container
{
	class modifier_driver_holder 
	{
		public long sequence_id;
		public modifier_driver md;
		
		public modifier_driver_holder(modifier_driver my_md,long my_sequence_id)
		{
			md=my_md;
			sequence_id=my_sequence_id;
		}
	}
	private modifier_container_timer timer;
	private ArrayList<modifier_driver_holder>modifier_driver_execute_list;
	private ArrayList<modifier_driver_holder>modifier_driver_insert_list;
	private long modifier_driver_sequence_id;
	
	private boolean clear_modifier_flag;
	public void  set_clear_modifier_flag()
	{
		clear_modifier_flag=true;
	};

	public void destroy()
	{
		if(timer!=null)
			timer=null;

		if(modifier_driver_execute_list!=null) {
			for(var p:modifier_driver_execute_list)
				if(p!=null)
					if(p.md!=null){
						p.md.destroy();
						p.md=null;
					}
			modifier_driver_execute_list.clear();
			modifier_driver_execute_list=null;
		}
		if(modifier_driver_insert_list!=null) {
			for(var p:modifier_driver_insert_list)
				if(p!=null)
					if(p.md!=null){
						p.md.destroy();
						p.md=null;
					}
			modifier_driver_insert_list.clear();
			modifier_driver_insert_list=null;
		}
	}
	private int adjust_heap(int modifier_id)
	{
		int modifier_number=modifier_driver_execute_list.size();
		if((modifier_number<=0)||(modifier_id<0)||(modifier_id>=modifier_number))
			return modifier_id;

		modifier_driver_holder p=modifier_driver_execute_list.get(modifier_id);
		
		while(modifier_id>0){
			int parent=(modifier_id-1)/2;
			modifier_driver_holder parent_p=modifier_driver_execute_list.get(parent);
			if(parent_p.md.start_time<=p.md.start_time)
				break;
			modifier_driver_execute_list.set(modifier_id,parent_p);
			modifier_driver_execute_list.set(parent,p);
			modifier_id=parent;
		}
		
		int ret_val=modifier_id;
		
		for(int my_child,other_child;(my_child=modifier_id+modifier_id+1)<modifier_number;) {
			modifier_driver_holder my_child_p=modifier_driver_execute_list.get(my_child);
			if((other_child=my_child+1)<modifier_number){
				modifier_driver_holder other_child_p=modifier_driver_execute_list.get(other_child);
				if(other_child_p.md.start_time<my_child_p.md.start_time){
					my_child	=other_child;
					my_child_p	=other_child_p;
				}
			}
			if(p.md.start_time<=my_child_p.md.start_time)
				break;
			
			modifier_driver_execute_list.set(modifier_id,my_child_p);
			modifier_driver_execute_list.set(my_child,p);
			modifier_id=my_child;
		}
		
		return ret_val;
	}
	private int recurse_collect_delete_modifiers(int modifier_id,
			ArrayList<modifier_driver_holder> delete_modify_driver_list,
			long my_current_time,scene_kernel sk,client_information ci)
	{
		for(int modifier_number;(modifier_number=modifier_driver_execute_list.size())>0;) {
			if((modifier_id<0)||(modifier_id>=modifier_number))
				break;
			modifier_driver_holder p=modifier_driver_execute_list.get(modifier_id);
			if(my_current_time<p.md.start_time)
				break;
			if(my_current_time<p.md.terminate_time){
				int left_id =recurse_collect_delete_modifiers(modifier_id+modifier_id+1,
						delete_modify_driver_list,my_current_time,sk,ci);
				int right_id=recurse_collect_delete_modifiers(modifier_id+modifier_id+2,
						delete_modify_driver_list,my_current_time,sk,ci);
				if(left_id<=right_id) {
					if(modifier_id<left_id)
						break;
					modifier_id=left_id;
				}else{
					if(modifier_id<right_id)
						break;
					modifier_id=right_id;
				}
			}else{
				delete_modify_driver_list.add(delete_modify_driver_list.size(),p);
				p=modifier_driver_execute_list.remove(modifier_number-1);
				if(modifier_id<(modifier_number-1)){
					modifier_driver_execute_list.set(modifier_id,p);
					modifier_id=adjust_heap(modifier_id);
				}
			}
		}
		return modifier_id;
	}
	private void recurse_execute_modifier_modify(
			int modifier_id,long my_current_time,scene_kernel sk,client_information ci)
	{
		int modifier_number=modifier_driver_execute_list.size();
		if((modifier_number<=0)||(modifier_id<0)||(modifier_id>=modifier_number))
			return;
		modifier_driver_holder p=modifier_driver_execute_list.get(modifier_id);
		if(my_current_time<p.md.start_time)
			return;
		p.md.modify(my_current_time,sk,ci);
		recurse_execute_modifier_modify(modifier_id+modifier_id+1,my_current_time,sk,ci);
		recurse_execute_modifier_modify(modifier_id+modifier_id+2,my_current_time,sk,ci);
	}
	private boolean test_can_not_start(
			int modifier_id,long my_current_time,scene_kernel sk,client_information ci)
	{
		int modifier_number=modifier_driver_execute_list.size();
		if((modifier_number<=0)||(modifier_id<0)||(modifier_id>=modifier_number))
			return false;
		modifier_driver_holder p=modifier_driver_execute_list.get(modifier_id);
		if(my_current_time<p.md.start_time)
			return false;
		boolean my_ret_val	 =p.md.can_start(my_current_time,sk,ci)?false:true;
		boolean left_ret_val =test_can_not_start(modifier_id+modifier_id+1,my_current_time,sk,ci);
		boolean right_ret_val=test_can_not_start(modifier_id+modifier_id+2,my_current_time,sk,ci);
		return my_ret_val||left_ret_val||right_ret_val;
	}
	private ArrayList<modifier_driver_holder> sort_modifier_driver_list(
			ArrayList<modifier_driver_holder> modifier_driver_holder_list_1,
			ArrayList<modifier_driver_holder> modifier_driver_holder_list_2)
	{
		class modifier_driver_list_comparator implements Comparator<modifier_driver_holder>
		{
			public int compare(modifier_driver_holder s,modifier_driver_holder t)
			{
				if(s.md.terminate_time<t.md.terminate_time)
					return -1;
				if(s.md.terminate_time>t.md.terminate_time)
					return 1;
				if(s.sequence_id<t.sequence_id)
					return -1;
				if(s.sequence_id>t.sequence_id)
					return 1;
				return 0;
			}
		};
		class modifier_driver_list_sorter 
				extends tree_search_container<modifier_driver_holder,modifier_driver_holder>
		{
			public modifier_driver_list_sorter()
			{
				super(new modifier_driver_list_comparator());
				
				if(modifier_driver_holder_list_1!=null)
					for(var p:modifier_driver_holder_list_1)
						add(p,p);
				if(modifier_driver_holder_list_2!=null)
					for(var p:modifier_driver_holder_list_2)
						add(p,p);
			}
		}
		return new modifier_driver_list_sorter().tree_get_value_list();
	}
	private void process_routine(scene_kernel sk,client_information ci)
	{
		long my_current_time=timer.get_current_time();
		
		int modifier_number;
		if((modifier_number=modifier_driver_execute_list.size())<=0){
			modifier_driver_sequence_id=0;
			modifier_number=0;
		}
		for(var p:modifier_driver_insert_list)
			if(p!=null)
				if(p.md!=null){
					modifier_driver_execute_list.add(modifier_number,p);
					adjust_heap(modifier_number++);
				}
		modifier_driver_insert_list.clear();
		
		if(test_can_not_start(0,my_current_time,sk,ci)){
			timer.modify_current_time(my_current_time,sk.current_time);
			return;
		}
		var delete_modify_driver_list=new ArrayList<modifier_driver_holder>();
		recurse_collect_delete_modifiers(0,delete_modify_driver_list,my_current_time,sk,ci);
		for(var p:sort_modifier_driver_list(delete_modify_driver_list,null))
			if(p!=null)
				if(p.md!=null){
					p.md.last_modify(my_current_time,sk,ci,true);
					p.md.destroy();
					p.md=null;
				}
		recurse_execute_modifier_modify(0,my_current_time,sk,ci);
		timer.caculate_current_time(sk.current_time);
		return;	
	}
	public void process(scene_kernel sk,client_information ci,boolean my_clear_modifier_flag)
	{
		clear_modifier_flag|=my_clear_modifier_flag;
		process_routine(sk,ci);
		if(clear_modifier_flag){
			clear_modifier_flag=false;
			long my_current_time=timer.caculate_current_time(sk.current_time);
			for(var p:sort_modifier_driver_list(modifier_driver_execute_list,modifier_driver_insert_list))
				if(p!=null)
					if(p.md!=null){
						p.md.last_modify(my_current_time,sk,ci,false);
						p.md.destroy();
						p.md=null;
					}
			modifier_driver_execute_list.clear();
			modifier_driver_insert_list.clear();
		}
	}
	public void add_modifier(modifier_driver new_modifier)
	{
		modifier_driver_insert_list.add(modifier_driver_insert_list.size(),
				new modifier_driver_holder(new_modifier,modifier_driver_sequence_id++));
	}
	public modifier_container_timer get_timer()
	{
		return timer;
	};
	public int get_modifier_number()
	{
		return modifier_driver_execute_list.size()+modifier_driver_insert_list.size();
	}
	public modifier_container(long my_timer_adjust_value)
	{
		timer=new modifier_container_timer(my_timer_adjust_value);
		
		modifier_driver_execute_list	=new ArrayList<modifier_driver_holder>();
		modifier_driver_insert_list		=new ArrayList<modifier_driver_holder>();
		
		modifier_driver_sequence_id=0;
		
		clear_modifier_flag=false;
	}
}
