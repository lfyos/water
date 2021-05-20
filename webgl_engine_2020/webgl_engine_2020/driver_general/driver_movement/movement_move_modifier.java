package driver_movement;

import kernel_component.component;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_network.network_parameter;
import kernel_transformation.location;


public class movement_move_modifier  extends driver_location_modifier.location_modifier_modifier
{
	private network_parameter parameter[];
	private int modify_id,clear_parameter_channel_id[],set_parameter_channel_id[],modify_parameter_channel_id[];
	
	public void destroy()
	{
		super.destroy();
		parameter=null;
		clear_parameter_channel_id=null;
		set_parameter_channel_id=null;
		modify_parameter_channel_id=null;
	}
	
	public movement_move_modifier(component my_comp,
			int my_movement_modifier_container_id,int my_location_component_id,
				
			int my_clear_parameter_channel_id[],
			int my_set_parameter_channel_id[],
			int my_modify_parameter_channel_id[],
			
			long my_start_time,		String my_start_parameter[],		location my_start_location,
			long my_terminate_time,	String my_terminate_parameter[],	location my_terminate_location,
			int my_follow_component_id[],location my_follow_component_location[])
	{
		super(	my_comp.component_id,
				my_location_component_id,my_movement_modifier_container_id,
				my_start_time,my_start_location,
				my_terminate_time,my_terminate_location,
				my_follow_component_id,my_follow_component_location);

		clear_parameter_channel_id	=my_clear_parameter_channel_id;
		set_parameter_channel_id	=my_set_parameter_channel_id;
		modify_parameter_channel_id	=my_modify_parameter_channel_id;
		
		int start_length	=(my_start_parameter==null)		?0:my_start_parameter.length;
		int terminate_length=(my_terminate_parameter==null)	?0:my_terminate_parameter.length;
		
		if((start_length+terminate_length)<=0)
			parameter=null;
		else{
			parameter=new network_parameter[start_length+terminate_length+4];
			parameter[2]=new network_parameter("start_time",						Long.toString(my_start_time));
			parameter[3]=new network_parameter("terminate_time",					Long.toString(my_terminate_time));
			for(int i=0;i<start_length;i++)
				parameter[i+4]=new network_parameter("par_s_"+Integer.toString(i),my_start_parameter[i]);
			for(int i=0;i<terminate_length;i++)
				parameter[i+4+start_length]=new network_parameter("par_t_"+Integer.toString(i),my_terminate_parameter[i]);
		}
		modify_id=2;
	}
	private void do_component_driver(component comp,engine_kernel ek,client_information ci)
	{
		for(int i=0,n=comp.driver_number();i<n;i++)
			instance_driver.execute_component_function(comp.component_id,i,parameter,ek,ci);
		for(int i=0,n=comp.children_number();i<n;i++)
			do_component_driver(comp.children[i],ek,ci);
	}
	private void call_component_driver(
			engine_kernel ek,client_information ci,
			long my_current_time,int operation_id)
	{
		component comp;
		if(parameter!=null){
			parameter[0]=new network_parameter("operation_id",	Integer.toString(operation_id));
			parameter[1]=new network_parameter("current_time",	Long.toString(my_current_time));
			if((comp=ek.component_cont.get_component(component_id))!=null)
				do_component_driver(comp,ek,ci);
			if(follow_component_id!=null)
				for(int i=0,ni=follow_component_id.length;i<ni;i++)
					if((comp=ek.component_cont.get_component(follow_component_id[i]))!=null)
						do_component_driver(comp,ek,ci);
		}
	}
	public void modify(long my_current_time,engine_kernel ek,client_information ci)
	{
		super.modify(my_current_time, ek, ci);
		component comp;
		if((comp=ek.component_cont.get_component(component_id))!=null){
			comp.modify_display_flag(modify_parameter_channel_id,true,ek.component_cont);

			if(follow_component_id!=null)
				for(int i=0,ni=follow_component_id.length;i<ni;i++)
					if((comp=ek.component_cont.get_component(follow_component_id[i]))!=null)
						comp.modify_display_flag(
									modify_parameter_channel_id,true,ek.component_cont);
			call_component_driver(ek,ci,my_current_time,modify_id++);
		}
	}
	
	private void reset_modifier(long my_current_time,
			engine_kernel ek,client_information ci,int operation_id)
	{
		component comp;
		if((comp=ek.component_cont.get_component(component_id))==null)
			return;
		comp.modify_display_flag(clear_parameter_channel_id,false,ek.component_cont);
		comp.modify_display_flag(set_parameter_channel_id,true,ek.component_cont);

		if(follow_component_id!=null)
			for(int i=0,ni=follow_component_id.length;i<ni;i++)
				if((comp=ek.component_cont.get_component(follow_component_id[i]))!=null) {
					comp.modify_display_flag(clear_parameter_channel_id,false,ek.component_cont);
					comp.modify_display_flag(set_parameter_channel_id,true,ek.component_cont);
				}
		call_component_driver(ek,ci,my_current_time,operation_id);	
	}
	public void last_modify(long my_current_time,engine_kernel ek,client_information ci,boolean terminated_flag)
	{
		super.last_modify(my_current_time,ek,ci,terminated_flag);
		if(terminated_flag)
			reset_modifier(my_current_time,ek,ci,1);
	}
	public boolean can_start(long my_current_time,engine_kernel ek,client_information ci)
	{
		return super.can_start(my_current_time, ek, ci);
	}
}
