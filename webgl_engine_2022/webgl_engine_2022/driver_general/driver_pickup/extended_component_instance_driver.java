package driver_pickup;

import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_render.render_target;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private int width,height;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		width=1;
		height=1;
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		if(cr.target.main_display_target_flag)
			if(ci.display_camera_result!=null){
				box view_volume_box=new box(
						ci.parameter.x*(double)width/(double)height+0.0/(double)height,
						ci.parameter.y+0.0/(double)height,
						-1,

						ci.parameter.x*(double)width/(double)height+1.0/(double)height,
						ci.parameter.y+1.0/(double)height,
						1);
				render_target rt=new render_target(true,
					comp.component_id,	driver_id,	0,					//target IDS
					new component[] {ek.component_cont.root_component},	//components
					null,												//driver_id
					
					cr.target.camera_id,cr.target.parameter_channel_id,	//camera_id,parameter_channel_id
					view_volume_box,					//view box
					cr.target.clip_plane,null,			//clip_plane,mirror_plane
					false,								//main_display_target_flag
					false,								//canvas_display_target_flag
					true,								//do_discard_lod_flag
					false);								//do_selection_lod_flag
				ci.target_container.register_target(rt);
			}
		return false;
	}
	public void create_render_parameter(int render_buffer_id,
			int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print("0");
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{
		ci.request_response.print("0");
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str;
		
		if((str=ci.request_response.get_parameter("width"))!=null) 
			width=Integer.parseInt(str);
		
		if((str=ci.request_response.get_parameter("height"))!=null) 
			height=Integer.parseInt(str);
		
		return null;
	}
}
