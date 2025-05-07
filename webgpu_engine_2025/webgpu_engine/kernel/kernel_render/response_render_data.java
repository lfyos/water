package kernel_render;

import kernel_camera.camera_result;
import kernel_component.component_collector;

public class response_render_data 
{
	public component_collector collector;
	public camera_result cam_result;
	
	public response_render_data(component_collector my_collector,camera_result my_cam_result)
	{
		collector		=my_collector;
		cam_result		=my_cam_result;
	}
}
