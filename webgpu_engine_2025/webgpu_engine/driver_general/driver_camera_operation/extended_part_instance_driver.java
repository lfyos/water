package driver_camera_operation;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_scene.client_information;
import kernel_common_class.const_value;
import kernel_driver.part_instance_driver;

public class extended_part_instance_driver extends part_instance_driver
{
	private double x0,y0,size,depth_start,depth_end;
	
	public extended_part_instance_driver(double my_x0,double my_y0,double my_size,
			double my_depth_start,double my_depth_end)
	{
		super();
		x0						=my_x0;
		y0						=my_y0;
		size					=my_size;
		depth_start				=my_depth_start;
		depth_end				=my_depth_end;
	}
	public void destroy()
	{
		super.destroy();
	}
	public void response_init_part_data(part p,scene_kernel sk,client_information ci)
	{
		double my_box_distance,box_distance=1.0;
		if(p.part_mesh.part_box!=null)
			if((my_box_distance=p.part_mesh.part_box.distance())>const_value.min_value)
				box_distance=my_box_distance;
		ci.request_response.print("[",x0).
							print(",",y0).
							print(",",size).
							print(",",depth_start).
							print(",",depth_end).
							print(",",box_distance).
							print("]");
	}
	public String[] response_part_event(part p,scene_kernel sk,client_information ci)
	{			
		return super.response_part_event(p,sk,ci);
	}
}