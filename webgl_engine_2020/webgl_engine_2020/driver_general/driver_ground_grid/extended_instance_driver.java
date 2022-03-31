package driver_ground_grid;

import kernel_camera.camera_result;
import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.box;

public class extended_instance_driver extends instance_driver
{
	private String grid_parameter,root_component_name;
	private double scale,color[];
	public void destroy()
	{
		super.destroy();
		grid_parameter=null;
		root_component_name=null;
		color=null;
	}
	public extended_instance_driver(component my_comp,int my_driver_id,
			String my_root_component_name,double my_scale,double my_color[])
	{
		super(my_comp,my_driver_id);
		grid_parameter="[[1,1,1,1]]";
		root_component_name=my_root_component_name;
		scale=my_scale;
		color=my_color;
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
		component root_comp;
		if((root_comp=ek.component_cont.search_component(root_component_name))==null)
			return;
		box root_box;
		if((root_box=root_comp.get_component_box(true))==null)
			return;
		double root_box_distance,my_root_box_distance=1;
		if((root_box_distance=root_box.distance())<const_value.min_value)
			return;
		if(root_box_distance<=1.0)
			while((my_root_box_distance/10)>root_box_distance)
				my_root_box_distance/=10;
		else
			while(my_root_box_distance<=root_box_distance)
				my_root_box_distance*=10;
		my_root_box_distance*=scale;
		grid_parameter ="[["+color[0]+","+color[1]+","+color[2]+","+my_root_box_distance*  1+"],";
		grid_parameter+=" ["+color[3]+","+color[4]+","+color[5]+","+my_root_box_distance* 10+"],";
		grid_parameter+=" ["+color[6]+","+color[7]+","+color[8]+","+my_root_box_distance*100+"]]";
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		return cr.target.main_display_target_flag?false:false;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(grid_parameter);
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("value"))==null)
			return null;
		try{
			str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
			str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
		}catch(Exception e){
			return null;
		}
		grid_parameter=str;
		update_component_parameter_version(0);
		return null;
	}
}
