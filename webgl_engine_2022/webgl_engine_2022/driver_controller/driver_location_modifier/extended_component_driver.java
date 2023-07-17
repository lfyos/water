package driver_location_modifier;

import kernel_part.part;
import kernel_component.component;
import kernel_engine.engine_kernel;
import kernel_driver.component_driver;
import kernel_transformation.location;
import kernel_network.client_request_response;
import kernel_driver.component_instance_driver;

public class extended_component_driver  extends component_driver
{
	public location_modification_data first;
	private int modifier_container_id;
	
	public void destroy()
	{
		super.destroy();
		
		if(first!=null) {
			first.destroy();
			first=null;
		}
	}
	public extended_component_driver(part my_component_part,int my_modifier_container_id)
	{
		super(my_component_part);
		modifier_container_id=my_modifier_container_id;
		first=null;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;
		
		return;
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_component_instance_driver(comp,driver_id,modifier_container_id);
	}
	
	public void clear_location_modifier()
	{
		first=new location_modification_data(get_component_parameter_version());
		update_component_parameter_version();
	}
	public void delete_timeout_location_modifier(engine_kernel ek)
	{
		long current_time=ek.modifier_cont[modifier_container_id].get_timer().get_current_time();
		location_modification_data p,pp,last;
		
		for(p=first,first=null,last=null;p!=null;){
			pp=p;
			p=p.next;
			pp.next=null;
			if((current_time<=pp.start_time)||(current_time<=pp.terminate_time)||pp.clear_flag){
				if(last==null)
					first=pp;
				else
					last.next=pp;
				last=pp;
			}
		}
	}
	public void register_location_modifier(engine_kernel ek,	int my_component_id,long time_length,
			location my_start_location,		location my_terminate_location,
			int my_follow_component_id[],	location my_follow_component_location[])
	{
		long current_time=ek.modifier_cont[modifier_container_id].get_timer().get_current_time();
		first=new location_modification_data(
				first,get_component_parameter_version(),
				my_component_id,current_time,current_time+time_length,
				my_start_location,my_terminate_location,
				my_follow_component_id,my_follow_component_location);
		update_component_parameter_version();
	}
}