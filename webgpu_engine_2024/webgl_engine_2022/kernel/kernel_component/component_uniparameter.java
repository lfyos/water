package kernel_component;

public class component_uniparameter 
{
	public boolean selected_flag,effective_selected_flag;
	public long selected_time,touch_time,file_last_modified_time;
	
	public boolean normalize_location_flag,part_list_flag;
	
	public double discard_precision2,transparency_value,component_driver_lod_precision_scale;
	
	public boolean do_response_location_flag,cacaulate_location_flag;
	public boolean display_part_name_or_component_name_flag;
	
	public component_uniparameter(long my_lastModified_time,
			boolean my_normalize_location_flag,boolean my_part_list_flag)
	{
		selected_flag						=false;
		effective_selected_flag				=false;
		selected_time						=0;
		
		normalize_location_flag				=my_normalize_location_flag;
		part_list_flag						=my_part_list_flag;

		discard_precision2					=-1.0;
		transparency_value					=1.0;
		component_driver_lod_precision_scale=1.0;
		
		do_response_location_flag			=true;
		cacaulate_location_flag				=false;
		display_part_name_or_component_name_flag=true;
		
		touch_time							=0;
		file_last_modified_time				=my_lastModified_time;
	}
}
