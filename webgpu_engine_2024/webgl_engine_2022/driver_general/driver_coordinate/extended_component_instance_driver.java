package driver_coordinate;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_common_class.const_value;
import kernel_driver.component_instance_driver;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class extended_component_instance_driver extends component_instance_driver
{
	private boolean display_main_coordinate_flag,display_selection_coordinate_flag;
	private double coordinate_length_scale;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(
			component my_comp,int my_driver_id,double my_coordinate_length_scale)
	{
		super(my_comp,my_driver_id);
		display_main_coordinate_flag		=true;
		display_selection_coordinate_flag	=true;
		coordinate_length_scale				=my_coordinate_length_scale;
	}
	private int response_selection_coordinate(int print_number,component my_comp,client_information ci)
	{
		double coordinate_length;
		
		if(my_comp.uniparameter.selected_flag)
			if(my_comp.component_box!=null)
				if((coordinate_length=my_comp.component_box.distance())>const_value.min_value){
					ci.request_response.print(((print_number++)<=0)?"[":",[",
							coordinate_length*coordinate_length_scale);
					ci.request_response.print(",",my_comp.component_id);
					ci.request_response.print("]");
					return print_number;
				}
		
		for(int i=0,ni=my_comp.children_number();i<ni;i++)
			print_number=response_selection_coordinate(print_number,my_comp.children[i],ci);
		return print_number;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
		
	}
	public boolean check(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		return false;
	}
	public void create_render_parameter(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		int print_number=0;
		ci.request_response.print("[");
		
		if(display_selection_coordinate_flag)
			for(int i=0,ni=sk.component_cont.root_component.children_number();i<ni;i++)
				print_number=response_selection_coordinate(
						print_number,sk.component_cont.root_component.children[i],ci);
		
		if(display_main_coordinate_flag)
			ci.request_response.print(((print_number++)<=0)?"[-1,-1]":",[-1,-1]");

		ci.request_response.print("]");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		String str=ci.request_response.get_parameter("operation");
		switch((str==null)?"":str) {
		case "onoff":
			str=ci.request_response.get_parameter("main");
			switch(((str==null)?"":str).toLowerCase()) {
			case "true":
				display_main_coordinate_flag=true;
				break;
			case "false":
				display_main_coordinate_flag=false;
				break;
			}
			str=ci.request_response.get_parameter("selection");
			switch(((str==null)?"":str).toLowerCase()) {
			case "true":
				display_selection_coordinate_flag=true;
				break;
			case "false":
				display_selection_coordinate_flag=false;
				break;
			}
			comp.driver_array.get(driver_id).update_component_parameter_version();
			break;
		}
		return null;
	}
}
