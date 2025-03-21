package kernel_camera;

import kernel_driver.location_modifier;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_transformation.location;

public class camera_modifier extends location_modifier
{
	private camera cam;
	private camera_parameter start_parameter,terminate_parameter;
	
	public void destroy()
	{
		super.destroy();
		cam=null;
		start_parameter=null;
		terminate_parameter=null;
	}

	public camera_modifier(camera my_cam,
			location my_terminate_location,camera_parameter my_terminate_parameter,long start_time)
	{
		super(	my_cam.eye_component,
				start_time,												my_cam.eye_component.move_location,
				start_time+my_terminate_parameter.switch_time_length,	my_terminate_location,
				true,													true);
		cam=my_cam;
		start_parameter=new camera_parameter(my_cam.parameter);
		terminate_parameter=new camera_parameter(my_terminate_parameter);
	}
	public void modify(long my_current_time,scene_kernel sk,client_information ci)
	{
		super.modify(my_current_time,sk,ci);
		cam.parameter=start_parameter.mix(terminate_parameter,p);
	}
	public void last_modify(long my_current_time,scene_kernel sk,client_information ci,boolean terminated_flag)
	{
		super.last_modify(my_current_time,sk,ci,terminated_flag);
		if(terminated_flag)
			cam.parameter=terminate_parameter;
	}
	public boolean can_start(long my_current_time,scene_kernel sk,client_information ci)
	{
		return super.can_start(my_current_time,sk,ci);
	}
}