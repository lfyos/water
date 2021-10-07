package driver_text;

public class text_item 
{
	public String	display_information[];
	public int 		canvas_width,		canvas_height;
	public double	text_square_width,	text_square_height;
	public boolean	view_or_model_coordinate_flag;

	public text_item()
	{
		display_information				=new String[]{};
		canvas_width					=4096;
		canvas_height					=64;
		text_square_width				=4;
		text_square_height				=0.125;
		view_or_model_coordinate_flag	=true;
	}
}
