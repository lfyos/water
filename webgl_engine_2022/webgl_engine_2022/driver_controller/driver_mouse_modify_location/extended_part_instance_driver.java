package driver_mouse_modify_location;

import kernel_part.part;
import kernel_engine.engine_kernel;
import kernel_common_class.jason_string;
import kernel_engine.client_information;
import kernel_driver.part_instance_driver;

public class extended_part_instance_driver extends part_instance_driver
{
	private String mark_component_name,movement_component_name,movement_abstract_menu_component_name;
	
	public extended_part_instance_driver(String my_mark_component_name,
			String my_movement_component_name,String my_movement_abstract_menu_component_name)
	{
		super();
		mark_component_name=my_mark_component_name;
		movement_component_name=my_movement_component_name;
		movement_abstract_menu_component_name=my_movement_abstract_menu_component_name;
	}
	public void destroy()
	{
		super.destroy();
		mark_component_name=null;
		movement_component_name=null;
		movement_abstract_menu_component_name=null;
	}
	public void response_init_part_data(part p,engine_kernel ek,client_information ci)
	{
		ci.request_response.println("{");
		ci.request_response.print("\"mark_component_name\":		",
				jason_string.change_string(mark_component_name)).println(",");
		ci.request_response.print("\"movement_component_name\":	",
				jason_string.change_string(movement_component_name)).println(",");
		ci.request_response.print("\"movement_abstract_menu_component_name\":	",
				jason_string.change_string(movement_abstract_menu_component_name));
		ci.request_response.println("}");
	}
	public String[] response_part_event(part p,engine_kernel ek,client_information ci)
	{			
		return super.response_part_event(p,ek,ci);
	}
}