package driver_show_target;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_driver.component_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_render.render_target;

public class extended_component_instance_driver extends component_instance_driver
{
	private String target_name;
	private int target_id,texture_id;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,String my_target_name,int my_texture_id)
	{
		super(my_comp,my_driver_id);
		
		target_name	=my_target_name;
		texture_id	=my_texture_id;

		target_id=-1;
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		if(!(cr.target.main_display_target_flag))
			return true;
		render_target rt;
		if((rt=ci.target_container.get_target(target_name))==null){
			String language_str=ci.request_response.get_parameter("language");
			language_str=(language_str==null)?"english":(language_str.trim());
			String caption=ek.system_par.language_change_name.search_change_name(
					"No_shown_render_target+"+language_str, "No_shown_render_target");
			ci.message_display.set_display_message(caption,1000*1000*1000*10);
			return true;
		}
		if(rt.target_id!=target_id){
			target_id=rt.target_id;
			update_component_parameter_version(0);
		}
		return (target_id<0)?true:false;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[",target_id);
		ci.request_response.print(",",texture_id);
		ci.request_response.print("]");
	}
	public String[] response_event(engine_kernel ek,client_information ci)
	{
		return null;
	}
}
