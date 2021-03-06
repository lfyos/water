package driver_location_modifier;

import kernel_camera.camera_result;
import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_transformation.location;


public class extended_instance_driver extends instance_driver
{
	private long last_parameter_version;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		last_parameter_version=0;
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		return cr.target.main_display_target_flag?false:true;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
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
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		location_modification_data	p=((extended_component_driver)(comp.driver_array[driver_id])).first;
	
		ci.request_response.print("[");
		for(int print_number=0;p!=null;p=p.next)
			if(last_parameter_version<=p.parameter_version){
				ci.request_response.print(((print_number++)<=0)?"[":",[",p.component_id);
				
				ci.request_response.print(",",	p.modifier_container_id);
				ci.request_response.print(",",	ek.modifier_cont[p.modifier_container_id].get_timer().get_version());
	
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
		
		last_parameter_version=comp.driver_array[driver_id].get_component_parameter_version();
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		return null;
	}
}
