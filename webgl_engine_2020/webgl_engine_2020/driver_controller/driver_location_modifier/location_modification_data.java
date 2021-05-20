package driver_location_modifier;

import kernel_transformation.location;

public class location_modification_data
{
	public void destroy()
	{
		if(next!=null) {
			next.destroy();
			next=null;
		}
		
		start_location=null;
		terminate_location=null;
		
		follow_component_id=null;
		if(follow_component_location!=null) {
			for(int i=0,ni=follow_component_location.length;i<ni;i++)
				follow_component_location[i]=null;
			follow_component_location=null;
		}
	}
	public		location_modification_data	next;
	
	public		long					parameter_version;
	
	public 		int 					component_id,modifier_container_id;
	public 		long 					start_time,terminate_time;
	
	public		location				start_location,terminate_location;
	
	public		int 					follow_component_id[];
	public		location				follow_component_location[];
	
	public location_modification_data(
			location_modification_data my_next,	long my_parameter_version,
			int my_component_id,				int my_modifier_container_id,
			long my_start_time,					long my_terminate_time,
			location my_start_location,			location my_terminate_location,
			int my_follow_component_id[],		location my_follow_component_location[])
	{
		next						=my_next;
		parameter_version			=my_parameter_version;
		
		component_id				=my_component_id;
		modifier_container_id		=my_modifier_container_id;
		start_time					=my_start_time;
		terminate_time				=my_terminate_time;
		start_location				=my_start_location;
		terminate_location			=my_terminate_location;
		follow_component_id			=my_follow_component_id;
		follow_component_location	=my_follow_component_location;
	};
}
