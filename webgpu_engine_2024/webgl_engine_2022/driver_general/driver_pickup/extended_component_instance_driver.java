package driver_pickup;

import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_render.render_target;
import kernel_render.render_target_view;
import kernel_engine.client_information;
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
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		if(!(cr.target.main_display_target_flag))
			return false;
		if(ci.display_camera_result==null)
			return false;

		double local_xy[]=cr.target.target_view.caculate_view_local_xy(ci.parameter.x,ci.parameter.y);
		
		render_target rt=new render_target(true,pickup_target_name,
				comp.component_id,	driver_id,	0,								//target IDS
				new component[] {ek.component_cont.root_component},				//components
				null,															//driver_id
					
				cr.target.camera_id,cr.target.parameter_channel_id,				//camera_id,parameter_channel_id
					
				new render_target_view(0,0,1,1,1,1),							//render_target_view
				new box(local_xy[4],local_xy[5],-1,local_xy[6],local_xy[7],1),	//view_volume_box
					
				cr.target.clip_plane,null,										//clip_plane,mirror_plane
				true,															//do_discard_lod_flag
				false);															//do_selection_lod_flag
		ci.target_container.register_target(rt);

		return false;
	}
	public void create_render_parameter(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print("0");
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("0");
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		return null;
	}
}
