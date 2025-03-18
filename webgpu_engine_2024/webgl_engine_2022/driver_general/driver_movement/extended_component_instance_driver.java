package driver_movement;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_transformation.point;
import kernel_transformation.location;
import kernel_driver.component_instance_driver;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class extended_component_instance_driver extends component_instance_driver
{
	private boolean suspend_status;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		suspend_status=true;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		component follow_mouse_comp;
		movement_suspend suspend=((extended_component_driver)(comp.driver_array.get(driver_id))).m.suspend;
		if(cr.target.main_display_target_flag) {
			if(suspend.follow_mouse_component_id>=0)
				if((follow_mouse_comp=sk.component_cont.get_component(suspend.follow_mouse_component_id))!=null)
					if(follow_mouse_comp.component_box!=null) {
						double local_xy[]=ci.display_camera_result.target.target_view.
								caculate_view_local_xy(ci.parameter.x,ci.parameter.y);
					
						location loca=follow_mouse_comp.caculate_negative_absolute_location();
						point p0=follow_mouse_comp.component_box.center();
						point p1=cr.matrix.multiply(p0);
						p0=loca.multiply(p0);
						
						p1=new point(local_xy[0],local_xy[1],p1.z);
						p1=cr.negative_matrix.multiply(p1);
						p1=loca.multiply(p1).sub(p0);

						loca=location.move_rotate(p1.x,p1.y,p1.z,0,0,0);
						loca=follow_mouse_comp.move_location.multiply(loca);
						follow_mouse_comp.set_component_move_location(loca,sk.component_cont);
					}
		}
		if(cr.target.main_display_target_flag){
			boolean new_suspend_status=(suspend.get_suspend_match_number()>0)||(suspend.get_suspend_component_number()>0);
			if(new_suspend_status^suspend_status){
				update_component_parameter_version(0);
				suspend_status=new_suspend_status;
			}
		}
		return cr.target.main_display_target_flag?false:true;
	}
	public void create_render_parameter(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print(suspend_status?"true":"false");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		extended_component_driver ecd=(extended_component_driver)(comp.driver_array.get(driver_id));
		return new movement_function_switch(sk,ci,ecd.m).get_result(comp.component_id,driver_id);
	}
}
