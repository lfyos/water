package driver_location_modifier;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

import kernel_transformation.location;
import kernel_file_manager.file_reader;

public class extended_component_driver  extends component_driver
{
	public location_modification_data first;
	
	public void destroy()
	{
		super.destroy();
		
		if(first!=null) {
			first.destroy();
			first=null;
		}
	}
	public extended_component_driver(part my_component_part)
	{
		super(my_component_part);
		
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
	public String [][]assemble_file_name_and_file_charset(file_reader fr,
			engine_kernel ek,client_request_response request_response)
	{
		return null;
	}
	public instance_driver create_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_instance_driver(comp,driver_id);
	}
	
	public void register(engine_kernel ek,
			int my_modifier_container_id,		int my_component_id,
			long my_start_time,					long my_terminate_time,
			location my_start_location,			location my_terminate_location,
			int my_follow_component_id[],		location my_follow_component_location[])
	{
		location_modification_data p=new location_modification_data(
				first,get_component_parameter_version(),
				my_component_id,my_modifier_container_id,my_start_time,my_terminate_time,
				my_start_location,my_terminate_location,
				my_follow_component_id,my_follow_component_location);
		long current_time=ek.modifier_cont[my_modifier_container_id].get_timer().get_current_time();
		for(first=null;p!=null;){
			location_modification_data pp=p;
			p=p.next;
			if((current_time<=pp.start_time)||(current_time<=pp.terminate_time)){
				pp.next=first;
				first=pp;
			}
		}
		update_component_parameter_version();
	}
}