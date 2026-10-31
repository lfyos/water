package driver_location_modifier;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_scene.client_information;
import kernel_driver.component_instance_driver;
import kernel_buffer.component_location_buffer;

public class extended_component_instance_driver extends component_instance_driver
{
	private long last_parameter_version;

	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		
		last_parameter_version=0;
	}
	public void response_initialization_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		if(!(cr.target.main_display_target_flag))
			return true;
		var my_component_driver=(extended_component_driver)(comp.driver_array.get(driver_id));
		my_component_driver.delete_timeout_location_modifier(sk);
		return false;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		int print_number=0;
		ci.request_response.print("[");
		var my_component_driver=(extended_component_driver)(comp.driver_array.get(driver_id));
		for(location_modification_data	p=my_component_driver.first;p!=null;p=p.next) {
			if(last_parameter_version>p.parameter_version)
				continue;
			if(p.clear_flag) {
				ci.request_response.print(((print_number++)<=0)?"0":",0");
				continue;
			}
			ci.request_response.print(((print_number++)<=0)?"[":",[",p.component_id);
			ci.request_response.print(",",	p.start_time);
			ci.request_response.print(",");
			component_location_buffer.response_location_data(
					p.start_location,ci.request_response);
	
			ci.request_response.print(",",	p.terminate_time);
			ci.request_response.print(",");
			component_location_buffer.response_location_data(
					p.terminate_location,ci.request_response);
	
			if(p.follow_component_id!=null)
				for(int i=0,ni=p.follow_component_id.length;i<ni;i++){
					ci.request_response.print(",",p.follow_component_id[i]);
					ci.request_response.print(",");
					component_location_buffer.response_location_data(
							p.follow_component_location[i],ci.request_response);
				}
			ci.request_response.print("]");
		}
		ci.request_response.print("]");
		
		last_parameter_version=my_component_driver.get_component_parameter_version();
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		return null;
	}
}
