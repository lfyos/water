package driver_component_pickup;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_render.render_target;
import kernel_render.target_viewport;
import kernel_transformation.box;
import kernel_transformation.point;

public class extended_instance_driver  extends instance_driver
{
	private int target_id;
	private long do_render_number;
	private double pickup_area_length;

	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id,double my_pickup_area_length)
	{
		super(my_comp,my_driver_id);
		target_id=-1;
		do_render_number=-1;
		pickup_area_length=my_pickup_area_length;
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		render_target rt;
		
		if(ci.display_camera_result==null)
			return true;
		if(ci.display_camera_result.target.target_id!=cr.target.target_id)
			return true;
		
		double viewport[]=null;
		if(do_render_number!=0)
			viewport=ci.display_camera_result.caculate_view_coordinate(ci);
		if(viewport==null){
			if((rt=ci.target_container.get_target(comp.component_name))!=null)
				ci.target_container.register_target(rt,0,null);
			return true;
		}
		double xm=viewport[0],ym=viewport[1];
		box view_volume_box=ci.display_camera_result.target.view_volume_box;
		point center=view_volume_box.center(),diff=view_volume_box.p[1].sub(center);

		rt=new render_target(comp.component_name,
			cr.target.camera_id,cr.target.parameter_channel_id,
			new component[] {ek.component_cont.root_component},null,ci.clip_plane,2,1,4,
			new box(	center.x+diff.x*xm-pickup_area_length,
						center.y+diff.y*ym-pickup_area_length,	view_volume_box.p[0].z,
				
						center.x+diff.x*xm+pickup_area_length,
						center.y+diff.y*ym+pickup_area_length,	view_volume_box.p[1].z),
			new target_viewport[]
				{
					new target_viewport(-1,-1,1,2,	1,new double[]{0.0,0.0,0.0,0.0}),
					new target_viewport( 0,-1,1,2,	2,new double[]{0.0,0.0,0.0,0.0})
				},
			false,true,false,false);
		
		rt.selection_target_flag=true;
		
		int new_target_id=ci.target_container.register_target(rt,do_render_number,null);
		
		if(target_id!=new_target_id){
			target_id=new_target_id;
			update_component_parameter_version(0);
		}
		
		return (do_render_number==0)?true:false;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(target_id);
	}
	public String[] response_event(engine_kernel ek,client_information ci)
	{
		String str;

		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;
		
		switch(str) {
		case "turn_on":
			do_render_number=-1;
			break;
		case "turn_off":
			do_render_number=0;
			break;
		case "on_off":
			ci.request_response.print((do_render_number!=0)?"1":"-1");
			break;
		}
		return null;
	}
}
