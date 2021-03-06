package driver_coordinate;

import kernel_camera.camera_result;
import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.box;
import kernel_part.part;


public class extended_instance_driver extends instance_driver
{
	private long 	do_selection_version;
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
		if(comp.children!=null)
			for(int i=0;i<(comp.children.length);i++)
				coordinate_number=register_coordinate(coordinate_length_scale,comp.children[i],
						default_distance,coordinate_number,component_id,coordinate_length);
		return coordinate_number;
	}
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id,
			boolean my_abandon_camera_display_flag,boolean my_abandon_selected_display_flag)
	{
		super(my_comp,my_driver_id);
		
		do_selection_version=0;
		abandon_camera_display_flag=my_abandon_camera_display_flag;
		abandon_selected_display_flag=my_abandon_selected_display_flag;
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{

	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if(ek.do_selection_version!=do_selection_version){
			do_selection_version=ek.do_selection_version;
			comp.driver_array[driver_id].update_component_render_version();
		}
		return cr.target.main_display_target_flag?false:true;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		int coordinate_number		=0;
		int component_id[]			=new int	[ek.component_cont.root_component.component_id+1];
		double coordinate_length[]	=new double	[ek.component_cont.root_component.component_id+1];

		part my_part=comp.driver_array[driver_id].component_part;
		
		if(!abandon_camera_display_flag){
			component_id[coordinate_number]			=-1;//cam_result.cam.eye_component.component_id;
			coordinate_length[coordinate_number]	=((extended_part_driver)(my_part.driver)).camera_length_scale;
			coordinate_number++;
		}
		if(!abandon_selected_display_flag){
			coordinate_number=register_coordinate(
					((extended_part_driver)(my_part.driver)).selection_length_scale,
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
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(comp.component_id);
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
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
			comp.driver_array[driver_id].update_component_render_version();
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
			comp.driver_array[driver_id].update_component_render_version();
		}
		return null;
	}
}
