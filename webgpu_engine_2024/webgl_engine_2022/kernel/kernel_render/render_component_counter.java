package kernel_render;

public class render_component_counter 
{
	public int component_keep_number,component_delete_number,component_refresh_number,component_append_number;
	public int update_component_parameter_number,update_location_number;
	
	public render_component_counter()
	{
		component_keep_number			=0;
		component_delete_number			=0;
		component_refresh_number		=0;
		component_append_number			=0;
		update_component_parameter_number=0;
		update_location_number			=0;
	}
}
