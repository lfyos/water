package driver_component_marker;

import kernel_component.component;

public class component_marker 
{
	private static long system_marker_id=0;
	
	public long marker_id;
	public int marker_component_id;
	public String marker_text;
	public double marker_x,marker_y,marker_z;
	
	public component_marker(component my_mark_comp,String my_marker_text,
			double my_marker_x,double my_marker_y,double my_marker_z)
	{
		marker_id			=system_marker_id++;
		marker_component_id	=my_mark_comp.component_id;
		marker_text			=my_marker_text;
		marker_x			=my_marker_x;
		marker_y			=my_marker_y;
		marker_z			=my_marker_z;
	}
}
