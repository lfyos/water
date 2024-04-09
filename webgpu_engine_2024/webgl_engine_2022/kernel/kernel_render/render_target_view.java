package kernel_render;

public class render_target_view 
{
	public int view_x0,view_y0,view_width,view_height,whole_view_width,whole_view_height;
	
	public render_target_view()
	{
		view_x0				=0;
		view_y0				=0;
		view_width			=1;
		view_height			=1;
		whole_view_width	=1;
		whole_view_height	=1;
	}
	public render_target_view(
		int my_view_x0,			int my_view_y0,
		int my_view_width,		int my_view_height,
		int my_whole_view_width,int my_whole_view_height)
	{
		view_x0				=my_view_x0;
		view_y0				=my_view_y0;
		view_width			=(my_view_width<1)			?1:my_view_width;
		view_height			=(my_view_height<1)			?1:my_view_height;
		whole_view_width	=(my_whole_view_width<1)	?1:my_whole_view_width;
		whole_view_height	=(my_whole_view_height<1)	?1:my_whole_view_height;
	}
	public render_target_view(render_target_view rtv)
	{
		view_x0				=rtv.view_x0;
		view_y0				=rtv.view_y0;
		view_width			=rtv.view_width;
		view_height			=rtv.view_height;
		whole_view_width	=rtv.whole_view_width;
		whole_view_height	=rtv.whole_view_height;
	}

	public double [] caculate_view_local_xy(double x,double y)
	{
		int max_width_height=(whole_view_width>whole_view_height)?whole_view_width:whole_view_height;
		double view_x=(x+1.0)*(double)(whole_view_width )/2.0;
		double view_y=(y+1.0)*(double)(whole_view_height)/2.0;
		double center_x=(double)(view_x0)+(double)(view_width) /2.0;
		double center_y=(double)(view_y0)+(double)(view_height)/2.0;
		
		double x0=2.0*(view_x-center_x)/(double)(view_width);
		double y0=2.0*(view_y-center_y)/(double)(view_height);
		double x1=2.0*(view_x-center_x)/(double)(view_width)	+1.0/(double)(max_width_height);
		double y1=2.0*(view_y-center_y)/(double)(view_height)	+1.0/(double)(max_width_height);
		double aspect_value=(double)(view_width)/(view_height);
		
		return new double[] 
		{
			x0,					y0,	x1,					y1,
			x0*aspect_value,	y0,	x1*aspect_value,	y1
		};
	}
}
