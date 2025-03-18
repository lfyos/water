package kernel_information;

import kernel_scene.client_information;

public class component_name_and_id  extends jason_creator
{
	private String component_name;
	private int component_id,driver_id;
	public void print()
	{
		print("component_name",	component_name);
		print("component_id",	component_id);
		print("driver_id",		driver_id);
	}
	public component_name_and_id(String my_component_name,
			int my_component_id,int my_driver_id,client_information ci)
	{
		super(ci.request_response);
		component_name=my_component_name;
		component_id=my_component_id;
		driver_id=my_driver_id;
	}
}