package driver_movement;

import kernel_component.component;
import kernel_driver.component_instance_driver;
import kernel_network.network_parameter;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_transformation.location;

public class movement_move_modifier  extends driver_location_modifier.location_modification_modifier
{
	private movement_suspend suspend;
	private network_parameter network_par[];
	private int modify_id,clear_parameter_channel_id[],set_parameter_channel_id[];
	private boolean  start_state_flag,terminate_state_flag;
	
	public void destroy()
	{
		super.destroy();
		suspend=null;
		network_par=null;
		clear_parameter_channel_id=null;
		set_parameter_channel_id=null;
	}
	public movement_move_modifier(					boolean my_start_state_flag,			boolean my_terminate_state_flag,
			movement_suspend my_suspend,			component my_comp,						int my_location_component_id,
			int my_clear_parameter_channel_id[],	int my_set_parameter_channel_id[],
			long my_start_time,						String my_start_parameter[],			location my_start_location,
			long my_terminate_time,					String my_terminate_parameter[],		location my_terminate_location,
			int my_follow_component_id[],			location my_follow_component_location[])
	{
		super(	my_comp.component_id,
				my_location_component_id,
				my_start_time,my_start_location,
				my_terminate_time,my_terminate_location,
				my_follow_component_id,my_follow_component_location);
		
		start_state_flag=my_start_state_flag;
		terminate_state_flag=my_terminate_state_flag;
		
		suspend=my_suspend;
		
		clear_parameter_channel_id	=my_clear_parameter_channel_id;
		set_parameter_channel_id	=my_set_parameter_channel_id;

		int start_length	=(my_start_parameter==null)		?0:my_start_parameter.length;
		int terminate_length=(my_terminate_parameter==null)	?0:my_terminate_parameter.length;
		
		if((start_length+terminate_length)<=0)
			network_par=null;
		else{
			network_par=new network_parameter[start_length+terminate_length+4];
			network_par[2]=new network_parameter("start_time",						Long.toString(my_start_time));
			network_par[3]=new network_parameter("terminate_time",					Long.toString(my_terminate_time));
			for(int i=0;i<start_length;i++)
				network_par[i+4]=new network_parameter("par_s_"+Integer.toString(i),my_start_parameter[i]);
			for(int i=0;i<terminate_length;i++)
				network_par[i+4+start_length]=new network_parameter("par_t_"+Integer.toString(i),my_terminate_parameter[i]);
		}
		modify_id=0;
	}
	private void do_component_driver(component comp,scene_kernel sk,client_information ci)
	{
		for(int i=0,n=comp.driver_number();i<n;i++)
			component_instance_driver.execute_component_function(comp.component_id,i,network_par,sk,ci);
		for(int i=0,n=comp.children_number();i<n;i++)
			do_component_driver(comp.children[i],sk,ci);
	}
	private void call_component_driver(scene_kernel sk,client_information ci,long my_current_time,int operation_id)
	{
		component comp;
		if(network_par!=null){
			network_par[0]=new network_parameter("operation_id",	Integer.toString(operation_id));
			network_par[1]=new network_parameter("current_time",	Long.toString(my_current_time));
			if((comp=sk.component_cont.get_component(component_id))!=null)
				do_component_driver(comp,sk,ci);
			if(follow_component_id!=null)
				for(int i=0,ni=follow_component_id.length;i<ni;i++)
					if((comp=sk.component_cont.get_component(follow_component_id[i]))!=null)
						do_component_driver(comp,sk,ci);
		}
	}
	public boolean can_start(long my_current_time,scene_kernel sk,client_information ci)
	{
		if(super.can_start(my_current_time,sk,ci))
			if(suspend.get_suspend_component_number()<=0)
				if(suspend.get_suspend_match_number()<=0)
					return true;
		return false;
	}
	public void modify(long my_current_time,scene_kernel sk,client_information ci)
	{
		component comp;
		super.modify(my_current_time, sk, ci);
		if((comp=sk.component_cont.get_component(component_id))!=null){
			comp.modify_display_flag(clear_parameter_channel_id,true,sk.component_cont);
			comp.modify_display_flag(set_parameter_channel_id,true,sk.component_cont);
			if(follow_component_id!=null)
				for(int i=0,ni=follow_component_id.length;i<ni;i++)
					if((comp=sk.component_cont.get_component(follow_component_id[i]))!=null) {
						comp.modify_display_flag(clear_parameter_channel_id,true,sk.component_cont);
						comp.modify_display_flag(set_parameter_channel_id,true,sk.component_cont);
						comp.uniparameter.cacaulate_location_flag=true;
					}
		}
		call_component_driver(sk,ci,my_current_time,modify_id++);
	}
	public void last_modify(long my_current_time,scene_kernel sk,client_information ci,boolean terminated_flag)
	{
		component comp;
		if(!terminated_flag)
			return;
		super.last_modify(my_current_time,sk,ci,true);
		if((comp=sk.component_cont.get_component(component_id))==null)
			return;
		call_component_driver(sk,ci,my_current_time,-1);
		
		comp.modify_display_flag(clear_parameter_channel_id,!start_state_flag,sk.component_cont);
		comp.modify_display_flag(set_parameter_channel_id,!terminate_state_flag,sk.component_cont);
		
		if(terminate_state_flag)
			comp.set_component_move_location(new location(), sk.component_cont);
		
		if(follow_component_id!=null)
			for(int i=0,ni=follow_component_id.length;i<ni;i++)
				if((comp=sk.component_cont.get_component(follow_component_id[i]))!=null) {
					comp.modify_display_flag(clear_parameter_channel_id,!start_state_flag,sk.component_cont);
					comp.modify_display_flag(set_parameter_channel_id,!terminate_state_flag,sk.component_cont);
					
					if(terminate_state_flag)
						comp.set_component_move_location(new location(),sk.component_cont);
				}
	}
}
