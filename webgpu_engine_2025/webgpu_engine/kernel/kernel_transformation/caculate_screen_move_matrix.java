package kernel_transformation;

public class caculate_screen_move_matrix 
{
	public location screen_move_matrix,negative_screen_move_matrix;
	
	public caculate_screen_move_matrix(box view_volume_box)
	{
		double x0=view_volume_box.p[0].x,y0=view_volume_box.p[0].y,z0=view_volume_box.p[0].z;
		double x1=view_volume_box.p[1].x,y1=view_volume_box.p[1].y,z1=view_volume_box.p[1].z;
		
		double dx=x1-x0,dy=y1-y0,dz=z1-z0;
		screen_move_matrix=new location(new double []
		{
				2.0/dx,			0.0,			0.0,		0.0,
				0.0,			2.0/dy,			0.0,		0.0,
				0.0,			0.0,			1.0/dz,		0.0,
				-(x1+x0)/dx,	-(y1+y0)/dy,	-z0/dz,		1.0
		});
		negative_screen_move_matrix=new location(new double []
		{
				dx/2.0,			0.0,			0.0,		0.0,
				0.0,			dy/2.0,			0.0,		0.0,
				0.0,			0.0,			dz,			0.0,
				(x1+x0)/2.0,	(y1+y0)/2.0,	z0,			1.0
		});
	}
}
