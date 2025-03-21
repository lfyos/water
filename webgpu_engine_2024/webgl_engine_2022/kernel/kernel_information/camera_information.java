package kernel_information;

import kernel_camera.camera;
import kernel_camera.camera_parameter;
import kernel_scene.client_information;

public class camera_information extends jason_creator
{
	private client_information ci;
	private camera cam;
	public void print()
	{
		if(cam==null)
			return;
		
		print("eye_component",new component_information(cam.eye_component,ci));
		
		camera_parameter par=cam.parameter;
		
		print("movement_flag",			par.movement_flag);
		print("direction_flag",			par.direction_flag);
		print("change_type_flag",		par.change_type_flag);
		print("scale_value",			par.scale_value);
		
		print("switch_time_length",		par.switch_time_length);
		print("distance",				par.distance);
		print("half_fovy_tanl",			par.half_fovy_tanl);
		print("bak_half_fovy_tanl",		par.bak_half_fovy_tanl);
		print("near_ratio",				par.near_ratio);
		print("far_ratio",				par.far_ratio);
		
		print("projection",				par.projection_type_flag);
		
		print("low_precision_scale",	par.low_precision_scale);
		print("high_precision_scale",	par.high_precision_scale);

		return;
	}
	
	public camera_information(camera my_cam,client_information my_ci)
	{
		super(my_ci.request_response);
		cam=my_cam;
		ci=my_ci;
	}
}
