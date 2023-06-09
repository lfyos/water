package kernel_render;

import kernel_camera.camera_result;
import kernel_component.component_collector;

public class response_render_data 
{
	public int render_buffer_id;
	public component_collector collector;
	public camera_result cam_result;
	
	public response_render_data(int my_render_buffer_id,
			component_collector my_collector,camera_result my_cam_result)
	{
		render_buffer_id=my_render_buffer_id;
		collector		=my_collector;
		cam_result		=my_cam_result;
	}
}
