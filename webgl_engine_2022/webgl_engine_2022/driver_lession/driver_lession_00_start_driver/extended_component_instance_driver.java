package driver_lession_00_start_driver;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_common_class.debug_information;
import kernel_engine.engine_kernel;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		debug_information.print((ci.parameter.comp==null)?"NO component":ci.parameter.comp.component_name);
		debug_information.print("	body:",		ci.parameter.body_id);
		debug_information.print("	face:",		ci.parameter.face_id);
		debug_information.print("	loop:",		ci.parameter.loop_id);
		debug_information.print("	edge:",		ci.parameter.edge_id);
		debug_information.print("	primitive:",ci.parameter.primitive_id);
		debug_information.print("	vertex_id:",ci.parameter.vertex_id);
		
		debug_information.print("	x:",ci.parameter.x);
		debug_information.print("	y:",ci.parameter.y);
		debug_information.print("	depth:",ci.parameter.depth);
		
		debug_information.print("	value:",ci.parameter.value[0]+","+ci.parameter.value[1]+","+ci.parameter.value[2]);

		debug_information.println();
		return false;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{
		ci.request_response.print(0);
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		return null;
	}
}
