package kernel_transformation;

public class projection_matrix
{
	public location			frustem_matrix,			negative_frustem_matrix;
	public location			orthographic_matrix,	negative_orthographic_matrix;
	
	public projection_matrix(
			double near_value_ratio,double far_value_ratio,
			double half_fovy_tanl,	double distance)
	{
		
		double near_value=near_value_ratio*distance;
		double far_value=far_value_ratio*distance;	
		double top_value,bottom_value,right_value,left_value;
	
		top_value=distance*half_fovy_tanl;
		bottom_value=(-top_value);
		right_value=top_value;
		left_value=(-right_value);
		
		
		frustem_matrix=new location(new double[]
		{
				2.0*distance/(right_value-left_value),				0.0,													0.0,												0.0,
				0.0,												2.0*distance/(top_value-bottom_value),					0.0,												0.0,
				(right_value+left_value)/(right_value-left_value),	(top_value+bottom_value)/(top_value-bottom_value),		(near_value+far_value)/(near_value-far_value),		-1.0,
				0.0,												0.0,													2.0*near_value*far_value/(near_value-far_value),	0.0
		});
		negative_frustem_matrix=new location(new double[]
		{
				(right_value-left_value)/(2.0*distance),			0.0,										0.0,													0.0,
				0.0,												(top_value-bottom_value)/(2.0*distance),	0.0,													0.0,
				0.0,												0.0,										0.0,		(near_value-far_value)/(2.0*near_value*far_value),
				(right_value+left_value)/(2.0*distance),			(top_value+bottom_value)/(2.0*distance),	-1.0,		(near_value+far_value)/(2.0*near_value*far_value)
		});

		orthographic_matrix=new location(new double[]
		{
				2.0/(right_value-left_value),						0.0,												0.0,												0.0,
				0.0,												2.0/(top_value-bottom_value),						0.0,												0.0,
				0.0,												0.0,												2.0/(near_value-far_value),							0.0,
				(right_value+left_value)/(left_value-right_value),	(top_value+bottom_value)/(bottom_value-top_value),	(near_value+far_value)/(near_value-far_value),		1.0
		});
		negative_orthographic_matrix=new location(new double[]
		{
				(right_value-left_value)/2.0,		0.0,									0.0,								0.0,
				0.0,								(top_value-bottom_value)/2.0,			0.0,								0.0,
				0.0,								0.0,									(near_value-far_value)/2.0,			0.0,
				(right_value+left_value)/2.0,		(top_value+bottom_value)/2.0,			(near_value+far_value)/(-2.0),		1.0
		});
	}
}
