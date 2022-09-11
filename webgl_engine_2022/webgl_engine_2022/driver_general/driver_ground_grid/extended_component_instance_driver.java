package driver_ground_grid;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_driver.component_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class extended_component_instance_driver extends component_instance_driver
{
	private double root_box_distance;
	private boolean on_off_flag;
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,
			double my_root_box_distance,boolean init_on_off_flag)
	{
		super(my_comp,my_driver_id);
		root_box_distance=my_root_box_distance;
		on_off_flag=init_on_off_flag;
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		return (cr.target.main_display_target_flag&&on_off_flag&&(root_box_distance>0.0))?false:true;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(root_box_distance);
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("operation"))!=null)
			switch(str.toLowerCase()) {
			case "turn_on":
				on_off_flag=true;
				break;
			case "turn_off":
				on_off_flag=false;
				break;
			}
		return null;
	}
}
