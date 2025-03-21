package driver_location_modifier;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_transformation.location;
import kernel_common_class.const_value;
import kernel_driver.component_instance_driver;
import kernel_network.client_request_response;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class extended_component_instance_driver extends component_instance_driver
{
	private long last_parameter_version;
	private int modifier_container_id;

	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,int my_modifier_container_id)
	{
		super(my_comp,my_driver_id);
		modifier_container_id=my_modifier_container_id;
		last_parameter_version=0;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
		
	}
	public boolean check(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		if(cr.target.main_display_target_flag){
			((extended_component_driver)(comp.driver_array.get(driver_id))).delete_timeout_location_modifier(sk);
			return false;
		}
		return true;
	}
	public void create_render_parameter(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(modifier_container_id);
	}
	private void response_location_data(location loca,client_request_response request_response)
	{
		double parameter[]=loca.caculate_translation_rotation(true);
		int code=0,number=0;
		for(int i=0;i<6;i++){
			code+=code;
			if(Math.abs(parameter[i])>const_value.min_value){
				request_response.print(((number++)<=0)?"[":",",parameter[i]);
				code++;
			}
		}
		request_response.print((number<=0)?"[]":(","+code+"]"));
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		int print_number=0;
		ci.request_response.print("[");
		extended_component_driver ecd=(extended_component_driver)(comp.driver_array.get(driver_id));
		for(location_modification_data	p=ecd.first;p!=null;p=p.next) {
			if(last_parameter_version>p.parameter_version)
				continue;
			if(p.clear_flag) {
				ci.request_response.print(((print_number++)<=0)?"0":",0");
				continue;
			}
			ci.request_response.print(((print_number++)<=0)?"[":",[",p.component_id);
			ci.request_response.print(",",	p.start_time);
			ci.request_response.print(",");
			response_location_data(p.start_location,ci.request_response);
	
			ci.request_response.print(",",	p.terminate_time);
			ci.request_response.print(",");
			response_location_data(p.terminate_location,ci.request_response);
	
			if(p.follow_component_id!=null)
				for(int i=0,ni=p.follow_component_id.length;i<ni;i++){
					ci.request_response.print(",",p.follow_component_id[i]);
					ci.request_response.print(",");
					response_location_data(p.follow_component_location[i],ci.request_response);
				}
			ci.request_response.print("]");
		}
		ci.request_response.print("]");
		
		last_parameter_version=ecd.get_component_parameter_version();
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		return null;
	}
}
