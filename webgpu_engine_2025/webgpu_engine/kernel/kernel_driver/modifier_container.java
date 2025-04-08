package kernel_driver;

import java.util.ArrayList;

import kernel_common_class.sorter;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class modifier_container
{
	private modifier_container_timer timer;
	private ArrayList<modifier_driver_holder> modifier_driver_execute_list;
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
			for(int i=0, ni=modifier_driver_execute_list.size();i<ni;i++) {
				modifier_driver_holder p=modifier_driver_execute_list.get(i);
				if(p!=null){
					if(p.md!=null){
						p.md.destroy();
						p.md=null;
					}
				}
			}
			modifier_driver_execute_list.clear();
			modifier_driver_execute_list=null;
		}
		if(modifier_driver_insert_list!=null) {
			for(int i=0, ni=modifier_driver_insert_list.size();i<ni;i++) {
				modifier_driver_holder p=modifier_driver_insert_list.get(i);
				if(p!=null){
					if(p.md!=null){
						p.md.destroy();
						p.md=null;
					}
				}
			}
			modifier_driver_insert_list.clear();
			modifier_driver_insert_list=null;
		}
	}
	private int adjust_heap(int modifier_id)
	{
		int modifier_number=modifier_driver_execute_list.size();
		if((modifier_driver_execute_list.size()<=0)||(modifier_id<0)||(modifier_id>=modifier_number))
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
		
		while(true){
			int left_child=modifier_id+modifier_id+1;
			int right_child=left_child+1;
			if(left_child>=modifier_number)
				break;
			modifier_driver_holder left_p=modifier_driver_execute_list.get(left_child);
			if(right_child<modifier_number){
				modifier_driver_holder right_p=modifier_driver_execute_list.get(right_child);
				if(right_p.md.start_time<left_p.md.start_time){
					left_child=right_child;
					left_p=right_p;
				}
			}
			if(p.md.start_time<=left_p.md.start_time)
				break;
			
			modifier_driver_execute_list.set(modifier_id,left_p);
			modifier_driver_execute_list.set(left_child,p);
			modifier_id=left_child;
		}
		
		return ret_val;
	}
	private int recurse_collect_delete_modifiers(int modifier_id,
			ArrayList<modifier_driver_holder> delete_modify_driver_list,
			long my_current_time,scene_kernel sk,client_information ci)
	{
		while(true){
			int modifier_number=modifier_driver_execute_list.size();
			if((modifier_number<=0)||(modifier_id<0)||(modifier_id>=modifier_number))
				return modifier_id;
			modifier_driver_holder p=modifier_driver_execute_list.get(modifier_id);
			if(my_current_time<p.md.start_time)
				return modifier_id;
			if(my_current_time<p.md.terminate_time){
				int left_id=recurse_collect_delete_modifiers(modifier_id+modifier_id+1,
						delete_modify_driver_list,my_current_time,sk,ci);
				int right_id=recurse_collect_delete_modifiers(modifier_id+modifier_id+2,
						delete_modify_driver_list,my_current_time,sk,ci);
				if(left_id>right_id)
					left_id=right_id;
				if(left_id>modifier_id)
					return modifier_id;
				modifier_id=left_id;
			}else {
				delete_modify_driver_list.add(delete_modify_driver_list.size(),p);
				p=modifier_driver_execute_list.remove(modifier_number-1);
				if(modifier_id<(modifier_number-1)) {
					modifier_driver_execute_list.set(modifier_id,p);
					int new_modifier_id=adjust_heap(modifier_id);
					if(new_modifier_id<modifier_id)
						modifier_id=new_modifier_id;
				}
			}
		}
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
		if(modifier_number<=0)
			return false;
		if(modifier_id>=modifier_number)
			return false;
		modifier_driver_holder p=modifier_driver_execute_list.get(modifier_id);
		if(my_current_time<p.md.start_time)
			return false;
		boolean my_ret_val	 =p.md.can_start(my_current_time,sk,ci)?false:true;
		boolean left_ret_val =test_can_not_start(modifier_id+modifier_id+1,my_current_time,sk,ci);
		boolean right_ret_val=test_can_not_start(modifier_id+modifier_id+2,my_current_time,sk,ci);
		return my_ret_val||left_ret_val||right_ret_val;
	}
	private modifier_driver_holder[] sort_modifier_driver_list(
			ArrayList<modifier_driver_holder> modifier_driver_list)
	{
		class modifier_driver_list_sorter extends sorter<modifier_driver_holder,modifier_driver_holder>
		{
			public int compare_data(modifier_driver_holder s,modifier_driver_holder t)
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
			public int compare_key(modifier_driver_holder s,modifier_driver_holder t)
			{
				return compare_data(s,t);
			}
			
			public modifier_driver_list_sorter()
			{
				data_array=modifier_driver_list.toArray(
					new modifier_driver_holder[modifier_driver_list.size()]);
				do_sort();
			}
		}
		return new modifier_driver_list_sorter().data_array;
	}
	public void process(scene_kernel sk,client_information ci,boolean my_clear_modifier_flag)
	{
		clear_modifier_flag|=my_clear_modifier_flag;
		process_routine(sk,ci);
		if(clear_modifier_flag){
			int modifier_number=modifier_driver_execute_list.size();
			
			modifier_driver_holder p_array[]=sort_modifier_driver_list(modifier_driver_execute_list);
			long my_current_time=timer.caculate_current_time(sk.current_time);
			
			for(int i=0;i<modifier_number;i++)
				p_array[i].md.last_modify(my_current_time,sk,ci,false);
			for(int i=0;i<modifier_number;i++){
				p_array[i].md.destroy();
				p_array[i].md=null;
			}
			modifier_driver_execute_list.clear();
		}
		clear_modifier_flag=false;
	}
	private void process_routine(scene_kernel sk,client_information ci)
	{
		modifier_driver_holder p;
		long my_current_time=timer.get_current_time();
		
		for(int i=0,ni=sk.system_par.max_process_modifier_number;i<ni;i++){
			int modifier_number=modifier_driver_execute_list.size();
			if(modifier_number<=0){
				modifier_driver_sequence_id=0;
				modifier_number=0;
			}
			for(int j=0,nj=modifier_driver_insert_list.size();j<nj;j++)
				if((p=modifier_driver_insert_list.get(j))!=null){
					modifier_driver_execute_list.add(modifier_number,p);
					adjust_heap(modifier_number++);
				}
			modifier_driver_insert_list.clear();
			
			if(test_can_not_start(0,my_current_time,sk,ci)) {
				timer.modify_current_time(my_current_time,sk.current_time);
				return;
			}
			ArrayList<modifier_driver_holder> delete_modify_driver_list=new ArrayList<modifier_driver_holder>();
			recurse_collect_delete_modifiers(0,delete_modify_driver_list,my_current_time,sk,ci);
			
			int delete_number;
			if((delete_number=delete_modify_driver_list.size())<=0)
				break;
			modifier_driver_holder p_array[]=sort_modifier_driver_list(delete_modify_driver_list);
			
			for(int j=0;j<delete_number;j++)
				p_array[j].md.last_modify(my_current_time,sk,ci,true);
			for(int j=0;j<delete_number;j++){
				p_array[j].md.destroy();
				p_array[j].md=null;
			}
			if(modifier_driver_insert_list.size()<=0)
				break;
		}
		recurse_execute_modifier_modify(0,my_current_time,sk,ci);
		timer.caculate_current_time(sk.current_time);
		return;	
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
