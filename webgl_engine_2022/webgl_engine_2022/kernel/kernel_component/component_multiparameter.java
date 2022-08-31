package kernel_component;

public class component_multiparameter
{
	public boolean display_flag,effective_display_flag;
	public long display_bitmap;
	public component_multiparameter(long default_display_bitmap)
	{
		display_bitmap=default_display_bitmap;
		display_flag=true;
		effective_display_flag=true;
	}
}
