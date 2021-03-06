package kernel_driver;

import kernel_driver.modifier_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;


public class modifier_container
{
	private modifier_container_timer timer;
	private modifier_driver_holder modifier_driver_array[];
	private modifier_driver_holder insert_modifier_driver_array[];
	private modifier_driver_holder temp_modifier_driver_array[];
	private int modifier_number,insert_modifier_number;
	private long modifier_driver_sequence_id;

	public void destroy()
	{
		if(timer!=null)
			timer=null;

		if(modifier_driver_array!=null) {
			for(int i=0, ni=modifier_driver_array.length;i<ni;i++)
				if(modifier_driver_array[i]!=null){
					if(modifier_driver_array[i].md!=null){
						modifier_driver_array[i].md.destroy();
						modifier_driver_array[i].md=null;
					}
					modifier_driver_array[i]=null;
				}
			modifier_driver_array=null;
		}
		if(insert_modifier_driver_array!=null) {
			for(int i=0, ni=insert_modifier_driver_array.length;i<ni;i++)
				if(insert_modifier_driver_array[i]!=null){
					if(insert_modifier_driver_array[i].md!=null){
						insert_modifier_driver_array[i].md.destroy();
						insert_modifier_driver_array[i].md=null;
					}
					insert_modifier_driver_array[i]=null;
				}
			insert_modifier_driver_array=null;
		}
		if(temp_modifier_driver_array!=null)
			for(int i=0, ni=temp_modifier_driver_array.length;i<ni;i++)
				if(temp_modifier_driver_array[i]!=null){
					temp_modifier_driver_array[i].md=null;
					temp_modifier_driver_array[i]=null;
				};
		temp_modifier_driver_array=null;
	}
	private void adjust_heap(int modifier_id)
	{
		if((modifier_number<=0)||(modifier_id<0)||(modifier_id>=modifier_number))
			return;

		long this_time=modifier_driver_array[modifier_id].md.start_time;
		
		while(modifier_id>0){
			int parent=(modifier_id-1)/2;
			if(modifier_driver_array[parent].md.start_time<=this_time)
				break;	
			modifier_driver_holder p=modifier_driver_array[modifier_id];
			modifier_driver_array[modifier_id]=modifier_driver_array[parent];
			modifier_driver_array[parent]=p;
			modifier_id=parent;
		}
		while(true){
			int left_child=modifier_id+modifier_id+1;
			int right_child=left_child+1;
			if(left_child>=modifier_number)
				break;
			long left_time=modifier_driver_array[left_child].md.start_time;
			if(right_child<modifier_number){
				long right_time=modifier_driver_array[right_child].md.start_time;
				if(right_time<left_time){
					left_child=right_child;
					left_time=right_time;
				}
			}
			if(this_time<=left_time)
				break;
			modifier_driver_holder p=modifier_driver_array[modifier_id];
			modifier_driver_array[modifier_id]=modifier_driver_array[left_child];
			modifier_driver_array[left_child]=p;
			modifier_id=left_child;
		}
	}
	private int recurse_process(int modifier_id,int delete_number,
			modifier_driver_holder my_delete_modifier_driver_array[],long my_current_time,
			engine_kernel ek,client_information ci)
	{
		while(true){
			if((modifier_number<=0)||(modifier_id<0)||(modifier_id>=modifier_number))
				return delete_number;
			if(my_current_time<modifier_driver_array[modifier_id].md.start_time)
				return delete_number;
			if(my_current_time<modifier_driver_array[modifier_id].md.terminate_time){
				modifier_driver_array[modifier_id].md.modify(my_current_time,ek,ci);
				delete_number=recurse_process(modifier_id+modifier_id+1,
					delete_number,my_delete_modifier_driver_array,my_current_time,ek,ci);
				delete_number=recurse_process(modifier_id+modifier_id+2,
					delete_number,my_delete_modifier_driver_array,my_current_time,ek,ci);
				return delete_number;
			}
			my_delete_modifier_driver_array[delete_number++]=modifier_driver_array[modifier_id];
			modifier_driver_array[modifier_id]=modifier_driver_array[--modifier_number];
			modifier_driver_array[modifier_number]=null;
			adjust_heap(modifier_id);
		}
	}
	private boolean test_can_not_start(
			int modifier_id,long my_current_time,engine_kernel ek,client_information ci)
	{
		if(modifier_number<=0)
			return false;
		if(modifier_id>=modifier_number)
			return false;
		if(my_current_time<modifier_driver_array[modifier_id].md.start_time)
			return false;
		if(modifier_driver_array[modifier_id].md.can_start(my_current_time,ek,ci)){
			if(test_can_not_start(modifier_id+modifier_id+1,my_current_time,ek,ci))
				return true;
			if(test_can_not_start(modifier_id+modifier_id+2,my_current_time,ek,ci))
				return true;
			return false;
		}
		return true;
	}
	private void sort_modifiers(int begin_modifier_id,int end_modifier_id,
			modifier_driver_holder sort_modifier_driver_array[],
			modifier_driver_holder temp_modifier_driver_array[])
	{
		if(begin_modifier_id<end_modifier_id){
			int mid_modifier_id	=(begin_modifier_id+end_modifier_id)/2;
			int p_left_begin	=begin_modifier_id,p_left_end	=mid_modifier_id;
			int p_right_begin	=mid_modifier_id+1,p_right_end	=end_modifier_id;
			
			sort_modifiers(p_left_begin,p_left_end,sort_modifier_driver_array,temp_modifier_driver_array);
			sort_modifiers(p_right_begin,p_right_end,sort_modifier_driver_array,temp_modifier_driver_array);
			
			for(int p_result_pointer=0,p_left_pointer=p_left_begin,p_right_pointer=p_right_begin;(p_left_pointer<=p_left_end)||(p_right_pointer<=p_right_end);p_result_pointer++){
				if(p_left_pointer>p_left_end)
					temp_modifier_driver_array[p_result_pointer]=sort_modifier_driver_array[p_right_pointer++];
				else if(p_right_pointer>p_right_end)
					temp_modifier_driver_array[p_result_pointer]=sort_modifier_driver_array[p_left_pointer++];
				else{
					long left_time	=sort_modifier_driver_array[p_left_pointer].md.terminate_time;
					long right_time	=sort_modifier_driver_array[p_right_pointer].md.terminate_time;
					if(left_time==right_time){
						left_time	=sort_modifier_driver_array[p_left_pointer].sequence_id;
						right_time	=sort_modifier_driver_array[p_right_pointer].sequence_id;
					}
					if(left_time<=right_time)
						temp_modifier_driver_array[p_result_pointer]=sort_modifier_driver_array[p_left_pointer++];
					else
						temp_modifier_driver_array[p_result_pointer]=sort_modifier_driver_array[p_right_pointer++];
				}
			}
			for(int i=begin_modifier_id,j=0;i<=end_modifier_id;i++,j++)
				sort_modifier_driver_array[i]=temp_modifier_driver_array[j];
		}
	}
	public void process(engine_kernel ek,client_information ci)
	{
		if(modifier_number<=0){
			modifier_driver_sequence_id=0;
			modifier_number=0;
		}
		if((modifier_number+insert_modifier_number)>=modifier_driver_array.length){
			modifier_driver_holder bak[]=modifier_driver_array;
			modifier_driver_array=new modifier_driver_holder[modifier_number+insert_modifier_number];
			for(int j=0,n=bak.length;j<n;j++)
				modifier_driver_array[j]=bak[j];
			for(int j=bak.length,n=modifier_driver_array.length;j<n;j++)
				modifier_driver_array[j]=null;
		}
		for(int i=0;i<insert_modifier_number;i++)
			if(insert_modifier_driver_array[i]!=null){
				modifier_driver_array[modifier_number]=insert_modifier_driver_array[i];
				insert_modifier_driver_array[i]=null;
				adjust_heap(modifier_number++);
			}
		insert_modifier_number=0;
		
		long my_current_time=timer.caculate_current_time(ek.current_time);
		
		if(test_can_not_start(0,my_current_time,ek,ci))
			timer.modify_speed(timer.get_speed(),ek.current_time);
		else{
			if(insert_modifier_driver_array.length<modifier_number){
				insert_modifier_driver_array=new modifier_driver_holder[modifier_number];
				for(int i=0,ni=insert_modifier_driver_array.length;i<ni;i++)
					insert_modifier_driver_array[i]=null;
			}
			int delete_number=recurse_process(0,0,
					insert_modifier_driver_array,my_current_time,ek,ci);
			if(delete_number>0){
				if(temp_modifier_driver_array.length<delete_number){
					temp_modifier_driver_array=new modifier_driver_holder[delete_number];
					for(int i=0;i<delete_number;i++)
						temp_modifier_driver_array[i]=null;
				}
				sort_modifiers(0,delete_number-1,insert_modifier_driver_array,temp_modifier_driver_array);
				for(int i=0;i<delete_number;i++)
					insert_modifier_driver_array[i].md.last_modify(my_current_time,ek,ci,true);
				for(int i=0;i<delete_number;i++){
					insert_modifier_driver_array[i].md.destroy();
					insert_modifier_driver_array[i].md=null;
					insert_modifier_driver_array[i]=null;
				}
				for(int i=0;i<delete_number;i++)
					temp_modifier_driver_array[i]=null;
			}
			timer.mark_current_time(ek.current_time);
		}
	}
	public void clear_modifier(engine_kernel ek,client_information ci)
	{
		process(ek,ci);
		
		if(temp_modifier_driver_array.length<modifier_number){
			temp_modifier_driver_array=new modifier_driver_holder[modifier_number];
			for(int i=0;i<modifier_number;i++)
				temp_modifier_driver_array[i]=null;
		}
		sort_modifiers(0,modifier_number-1,modifier_driver_array,temp_modifier_driver_array);
		
		long my_current_time=timer.caculate_current_time(ek.current_time);
		
		for(int i=0;i<modifier_number;i++)
			modifier_driver_array[i].md.last_modify(my_current_time,ek,ci,false);
		
		for(int i=0;i<modifier_number;i++){
			modifier_driver_array[i].md.destroy();
			modifier_driver_array[i].md=null;
			modifier_driver_array[i]=null;
		}
		for(int i=0;i<modifier_number;i++)
			temp_modifier_driver_array[i]=null;
		modifier_number=0;
	}
	public void add_modifier(modifier_driver new_modifier)
	{
		if(insert_modifier_number>=insert_modifier_driver_array.length){
			modifier_driver_holder bak[]=insert_modifier_driver_array;
			insert_modifier_driver_array=new modifier_driver_holder[bak.length+100];
			for(int i=0,n=bak.length;i<n;i++)
				insert_modifier_driver_array[i]=bak[i];
			for(int i=bak.length,n=insert_modifier_driver_array.length;i<n;i++)
				insert_modifier_driver_array[i]=null;
		}
		insert_modifier_driver_array[insert_modifier_number++]
				=new modifier_driver_holder(new_modifier,modifier_driver_sequence_id++);
	}
	public modifier_container_timer get_timer()
	{
		return timer;
	};
	public int get_modifier_number()
	{
		return modifier_number+insert_modifier_number;
	}
	public modifier_container(long my_timer_adjust_value)
	{
		timer=new modifier_container_timer(my_timer_adjust_value);
		
		modifier_driver_array		=new modifier_driver_holder[100];
		insert_modifier_driver_array=new modifier_driver_holder[100];
		temp_modifier_driver_array	=new modifier_driver_holder[100];
		
		for(int i=0,n=modifier_driver_array.length;i<n;i++)
			modifier_driver_array[i]=null;
		for(int i=0,n=insert_modifier_driver_array.length;i<n;i++)
			insert_modifier_driver_array[i]=null;
		for(int i=0,n=temp_modifier_driver_array.length;i<n;i++)
			temp_modifier_driver_array[i]=null;
		
		insert_modifier_number=0;
		modifier_number=0;
		modifier_driver_sequence_id=0;
	}
}
