package driver_pickup;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_render.render_target;
import kernel_scene.client_information;
import kernel_common_class.const_value;
import kernel_render.render_target_view;
import kernel_render.render_target_parameter;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private String 	pickup_target_name;
	private int 	main_target_id,pickup_target_width,whole_view_width,whole_view_height;
	private double	parameter_x,parameter_y;
	public void destroy()
	{
		super.destroy();
		pickup_target_name=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,
			String my_pickup_target_name,int my_pickup_target_width)
	{
		super(my_comp,my_driver_id);
		main_target_id		=-1;
		pickup_target_name	=my_pickup_target_name;
		pickup_target_width	=my_pickup_target_width;
		whole_view_width	=1;
		whole_view_height	=1;
		parameter_x			=0;
		parameter_y			=0;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
		ci.request_response.print(pickup_target_width);
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		if(!(cr.target.main_display_target_flag))
			return false;
		
		whole_view_width =cr.target.target_view.whole_view_width;
		whole_view_height=cr.target.target_view.whole_view_height;
		
		double diff_value=(cr.target.target_id==main_target_id)?0.0:1.0;
		diff_value+=(ci.parameter.x-parameter_x)*(ci.parameter.x-parameter_x);
		diff_value+=(ci.parameter.y-parameter_y)*(ci.parameter.y-parameter_y);
		if(diff_value>=const_value.min_value2) {
			parameter_x		=ci.parameter.x;
			parameter_y		=ci.parameter.y;
			main_target_id	=cr.target.target_id;
			update_component_parameter_version(0);
		}
		

		render_target rt=new render_target(
			render_target_parameter.create_pickup_parameter(),					//render_target_parameter
			pickup_target_name,comp.component_id,driver_id,0,					//target IDS,components
			cr.target.comp,cr.target.camera_id,cr.target.parameter_channel_id,	//camera_id,parameter_channel_id
			new render_target_view(0,0,pickup_target_width,pickup_target_width,
						pickup_target_width,pickup_target_width),				//render_target_view
			cr.target.target_view.caculate_view_box(
						ci.parameter.x,ci.parameter.y,pickup_target_width),		//view_volume_box
			cr.target.clip_plane,null);											//clip_plane,mirror_plane									
		ci.target_container.register_target(rt);
		return false;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print("0");
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print("[",parameter_x).
							print(",",parameter_y).
							print(",",whole_view_width ).
							print(",",whole_view_height).
							print(",",main_target_id).
							print("]");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		return null;
	}
}
