package driver_location_modifier;

import kernel_component.component;
import kernel_driver.modifier_driver;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_driver.component_driver;
import kernel_transformation.location;


public class location_modification_modifier extends modifier_driver
{
	public int component_id;
	public int location_component_id;
	private location start_location,terminate_location;
	public double p;
	
	public int follow_component_id[];
	public location follow_component_location[];
	
	private long do_modify_number;
	
	public void destroy()
	{
		super.destroy();
		
		start_location=null;
		terminate_location=null;
		follow_component_id=null;
		follow_component_location=null;
	}
	
	public location_modification_modifier(
			int my_component_id,			int my_location_component_id,
			long my_start_time,				location my_start_location,
			long my_terminate_time,			location my_terminate_location,
			int my_follow_component_id[],	location my_follow_component_location[])
	{
		super(my_start_time,my_terminate_time);

		component_id					=my_component_id;
		location_component_id			=my_location_component_id;
		start_location					=new location(my_start_location);
		terminate_location				=new location(my_terminate_location);
		follow_component_id				=my_follow_component_id;
		follow_component_location		=my_follow_component_location;
		
		p=0.0;
		
		do_modify_number=0;
	}
	private boolean set_location(location my_loca,boolean my_do_response_location_flag,scene_kernel sk)
	{
		component comp,follow_comp;
		
		if((comp=sk.component_cont.get_component(component_id))==null)
			return false;
		
		comp.set_component_move_location(my_loca,sk.component_cont);
		comp.uniparameter.touch_time=sk.current_time.nanoseconds();
		comp.uniparameter.do_response_location_flag=my_do_response_location_flag;
		
		location parent_and_relative_location;
		if(comp.uniparameter.cacaulate_location_flag)
			parent_and_relative_location=comp.move_location;
		else
			parent_and_relative_location=comp.parent_and_relative_location.multiply(comp.move_location);
		
		if(follow_component_id!=null)
			for(int i=0,ni=follow_component_id.length;i<ni;i++)
				if((follow_comp=sk.component_cont.get_component(follow_component_id[i]))!=null){
					location loca=parent_and_relative_location.multiply(follow_component_location[i]);
					follow_comp.set_component_move_location(loca,sk.component_cont);
					follow_comp.uniparameter.touch_time=sk.current_time.nanoseconds();
					follow_comp.uniparameter.do_response_location_flag=my_do_response_location_flag;
					follow_comp.uniparameter.cacaulate_location_flag=true;
				}	
		return true;
	}
	
	public void modify(long my_current_time,scene_kernel sk,client_information ci)
	{
		super.modify(my_current_time,sk,ci);

		if(terminate_time==start_time)
			p=1.0;
		else{
			p=((double)(my_current_time-start_time))/((double)(terminate_time-start_time));
			p=(p<0.0)?0.0:(p>1.0)?1.0:p;
		}
		component location_comp=sk.component_cont.get_component(location_component_id);
		if(!(set_location(location.mix_location(
				start_location,terminate_location,p),(location_comp==null)?true:false,sk)))
			return;
		if(location_comp==null)
			return;
		if((do_modify_number++)!=0)
			return;
		for(int i=0,ni=location_comp.driver_number();i<ni;i++) {
			component_driver c_d=location_comp.driver_array.get(i);
			if(!(c_d instanceof extended_component_driver))
				continue;
			extended_component_driver ecd=(extended_component_driver)c_d;
			ecd.register_location_modifier(sk,component_id,terminate_time-start_time,
				start_location,terminate_location,follow_component_id,follow_component_location);
			return;
		}
	}
	public void last_modify(long my_current_time,scene_kernel sk,client_information ci,boolean terminated_flag)
	{
		super.last_modify(my_current_time,sk,ci,terminated_flag);
		if(terminated_flag)
			set_location(terminate_location,true,sk);
	}
	public boolean can_start(long my_current_time,scene_kernel sk,client_information ci)
	{
		return super.can_start(my_current_time,sk,ci);
	}
}
