package kernel_driver;

import kernel_component.component;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.location;


public class location_modifier extends modifier_driver
{
	public long touch_time_length;
	
	public int component_id;
	public location start_location,terminate_location;
	public double p;
	
	private boolean do_response_location_flag;
	private boolean last_do_response_location_flag;
	
	private boolean location_lock_result;
	private long modify_counter;
	
	public location_modifier(component my_comp,
			long my_start_time,location my_start_location,
			long my_terminate_time,location my_terminate_location,
			boolean my_do_response_location_flag,
			boolean my_last_do_response_location_flag)
	{
		super(my_start_time,my_terminate_time);
		
		touch_time_length				=0;
		
		component_id					=my_comp.component_id;
		start_location					=new location(my_start_location);
		terminate_location				=new location(my_terminate_location);
		p=0.0;

		do_response_location_flag		=my_do_response_location_flag;
		last_do_response_location_flag	=my_last_do_response_location_flag;
		
		location_lock_result			=false;
		modify_counter					=0;
	}
	public void modify(long my_current_time,engine_kernel ek,client_information ci)
	{
		component comp;
		super.modify(my_current_time,ek,ci);
		if((comp=ek.component_cont.get_component(component_id))==null)
			return;
		location_lock_result|=comp.lock_location_modification();
		if(!location_lock_result)
			return;
		if((modify_counter++)>0)
			if(touch_time_length>0)
				if((ek.current_time.nanoseconds()-comp.render_touch_time)>touch_time_length)
					return;
		if(terminate_time==start_time)
			p=1.0;
		else{
			p=((double)(my_current_time-start_time))/((double)(terminate_time-start_time));
			p=(p<0.0)?0.0:(p>1.0)?1.0:p;
		}
		location loca=location.mix_location(start_location,terminate_location,p);
		comp.modify_location(loca,ek.component_cont);
		comp.uniparameter.touch_time=ek.current_time.nanoseconds();
		comp.uniparameter.do_response_location_flag=do_response_location_flag;
	}
	public void destroy()
	{
		super.destroy();
		start_location=null;
		terminate_location=null;
	}
	public void last_modify(long my_current_time,engine_kernel ek,client_information ci,boolean terminated_flag)
	{
		super.last_modify(my_current_time,ek,ci,terminated_flag);
		
		component comp;
		if((comp=ek.component_cont.get_component(component_id))==null)
			return;
		if(location_lock_result){
			comp.unlock_location_modification();
			location_lock_result=false;
		}
		comp.uniparameter.do_response_location_flag=last_do_response_location_flag;
		comp.uniparameter.touch_time=ek.current_time.nanoseconds();
		if(terminated_flag)
			comp.modify_location(terminate_location,ek.component_cont);
	}
	public boolean can_start(long my_current_time,engine_kernel ek,client_information ci)
	{
		return super.can_start(my_current_time,ek,ci);
	}
}
