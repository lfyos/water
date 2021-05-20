package driver_cad;

public class component_parameter 
{
	public double transparency_value;
	public boolean effective_selected_flag;
	public double x_scale,y_scale,z_scale;
	
	public component_parameter()
	{
		transparency_value=1.0;
		effective_selected_flag=false;
		x_scale=1.0;
		y_scale=1.0;
		z_scale=1.0;
	}
}
