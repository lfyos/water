package driver_coordinate;

import kernel_camera.camera_result;
import kernel_common_class.const_value;
import kernel_component.component;
import kernel_driver.component_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.box;

public class extended_component_instance_driver extends component_instance_driver
{
	private double 	selection_length_scale;
	private boolean	abandon_camera_display_flag,abandon_selected_display_flag;
	
	private int register_coordinate(
			double coordinate_length_scale,component comp,double default_distance,
			int coordinate_number,int component_id[],double coordinate_length[])
	{
		if(comp.uniparameter.selected_flag){
			box my_box=comp.get_component_box(true);
			component_id[coordinate_number]=comp.component_id;
			coordinate_length[coordinate_number]=default_distance;
			if(my_box!=null){
				coordinate_length[coordinate_number]=my_box.distance()*coordinate_length_scale;
				if(coordinate_length[coordinate_number]<const_value.min_value)
					coordinate_length[coordinate_number]=default_distance;
			}
			coordinate_number++;
		}
		for(int i=0,ni=comp.children_number();i<ni;i++)
			coordinate_number=register_coordinate(coordinate_length_scale,comp.children[i],
						default_distance,coordinate_number,component_id,coordinate_length);
		return coordinate_number;
	}
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(
			component my_comp,int my_driver_id,		double my_selection_length_scale,
			boolean my_abandon_camera_display_flag,	boolean my_abandon_selected_display_flag)
	{
		super(my_comp,my_driver_id);
		
		selection_length_scale			=my_selection_length_scale;
		abandon_camera_display_flag		=my_abandon_camera_display_flag;
		abandon_selected_display_flag	=my_abandon_selected_display_flag;
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
		
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		if(!(cr.target.main_display_target_flag))
			return true;
		if(abandon_camera_display_flag&&abandon_selected_display_flag)
			return true;
		return false;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		int coordinate_number		=0;
		int component_id[]			=new int	[ek.component_cont.get_sort_component_array().length];
		double coordinate_length[]	=new double	[component_id.length];

		if(!abandon_camera_display_flag){
			component_id[coordinate_number]			=-1;
			coordinate_length[coordinate_number]	=cr.view_box.distance();
			coordinate_number++;
		}
		if(!abandon_selected_display_flag){
			coordinate_number=register_coordinate(selection_length_scale,
					ek.component_cont.root_component,
					cr.cam.parameter.distance*cr.cam.parameter.half_fovy_tanl/10.0,
					coordinate_number,component_id,coordinate_length);
		}
		ci.request_response.print("[");
		for(int i=0;i<coordinate_number;i++){
			ci.request_response.print((i<=0)?"[":",[");

			ci.request_response.print(coordinate_length[i]);
			ci.request_response.print(",",component_id[i]);
			
			ci.request_response.print("]");
		}
		ci.request_response.print("]");
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{
		ci.request_response.print(data_buffer_id);
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str;

		ci.request_response.print("{\"camera_display_flag\" : ",	abandon_camera_display_flag		?"true"	:"false");
		ci.request_response.print(", \"selected_display_flag\" : ",	abandon_selected_display_flag	?"true}":"false}");
		
		if((str=ci.request_response.get_parameter("camera_display_flag"))!=null){
			switch(str.toLowerCase()) {
			default:
				break;
			case "true":
				abandon_camera_display_flag=false;
				break;
			case "false":
				abandon_camera_display_flag=true;
				break;
			}
			comp.driver_array.get(driver_id).update_component_render_version();
		}
		if((str=ci.request_response.get_parameter("selected_display_flag"))!=null){
			switch(str.toLowerCase()) {
			default:
				break;
			case "true":
				abandon_selected_display_flag=false;
				break;
			case "false":
				abandon_selected_display_flag=true;
				break;
			}
			comp.driver_array.get(driver_id).update_component_render_version();
		}
		return null;
	}
}
