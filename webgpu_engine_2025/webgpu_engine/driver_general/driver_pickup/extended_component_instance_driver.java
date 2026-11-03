package driver_pickup;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_render.render_target;
import kernel_scene.client_information;
import kernel_render.render_target_flag;
import kernel_render.render_target_viewport;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private String 	pickup_target_name;
	
	public void destroy()
	{
		super.destroy();
		
		pickup_target_name=null;
	}
	public extended_component_instance_driver(
			component my_comp,int my_driver_id,String my_pickup_target_name)
	{
		super(my_comp,my_driver_id);

		pickup_target_name=my_pickup_target_name;
	}
	public void response_initialization_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		if(cr.target.main_display_target_flag){
			ci.target_container.register_target(
				new render_target(
						cr.target.target_id,render_target_flag.create_pickup_flag(),
						pickup_target_name,comp.component_id,driver_id,0,
						cr.target.target_component_array,cr.target.camera_id,cr.target.parameter_channel_id,
						new render_target_viewport(0,0,1,1,1,1),
						cr.target.target_viewport.caculate_view_box(ci.parameter.x,ci.parameter.y,1),
						cr.target.clip_plane,cr.target.adjust_eye_position_parameter));
			return false;
		}
		return true;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		return null;
	}
}
