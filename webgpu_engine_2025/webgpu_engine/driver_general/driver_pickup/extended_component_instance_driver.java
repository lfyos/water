package driver_pickup;

import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_render.render_target;
import kernel_render.render_target_parameter;
import kernel_render.render_target_view;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private String pickup_target_name;
	public void destroy()
	{
		super.destroy();
		pickup_target_name=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,String my_pickup_target_name)
	{
		super(my_comp,my_driver_id);
		pickup_target_name=my_pickup_target_name;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		if(!(cr.target.main_display_target_flag))
			return false;

		double local_xy[]=cr.target.target_view.caculate_view_local_xy(ci.parameter.x,ci.parameter.y);
		
		render_target rt=new render_target(
				render_target_parameter.create_pickup_parameter(),				//render_target_parameter
				
				pickup_target_name,comp.component_id,	driver_id,	0,			//target IDS
				new component[] {sk.component_cont.root_component},				//components
					
				cr.target.camera_id,cr.target.parameter_channel_id,				//camera_id,parameter_channel_id
					
				new render_target_view(0,0,1,1,1,1),							//render_target_view
				new box(local_xy[4],local_xy[5],-1,local_xy[6],local_xy[7],1),	//view_volume_box
					
				cr.target.clip_plane,null);										//clip_plane,mirror_plane
														
		ci.target_container.register_target(rt);

		return false;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print("0");
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print("0");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		return null;
	}
}
