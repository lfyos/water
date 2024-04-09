package driver_movement;

import kernel_transformation.location;
import kernel_transformation.point;

public class movement_location_to_move_rotate
{
	public double m_x,m_y,m_z,r_x,r_y,r_z;

	private void init(location loca)
	{
		double parameter[]=loca.caculate_translation_rotation(true);
		m_x=parameter[0];
		m_y=parameter[1];
		m_z=parameter[2];
		r_x=parameter[3];
		r_y=parameter[4];
		r_z=parameter[5];
	}
	private void init(location comp_abaulate_loca,location comp_move_loca,location rotate_absolute_loca)
	{
		location comp_loca=comp_abaulate_loca.multiply(comp_move_loca.negative());
		location negative_rotate_absolute_loca=rotate_absolute_loca.negative();
		
		point p0=comp_abaulate_loca	.multiply(new point(0,0,0));		p0=negative_rotate_absolute_loca.multiply(p0);
		point px=comp_abaulate_loca	.multiply(new point(1,0,0));		px=negative_rotate_absolute_loca.multiply(px);
		point py=comp_abaulate_loca	.multiply(new point(0,1,0));		py=negative_rotate_absolute_loca.multiply(py);
		point pz=comp_abaulate_loca	.multiply(new point(0,0,1));		pz=negative_rotate_absolute_loca.multiply(pz);

		point q0=comp_loca			.multiply(new point(0,0,0));		q0=negative_rotate_absolute_loca.multiply(q0);
		point qx=comp_loca			.multiply(new point(1,0,0));		qx=negative_rotate_absolute_loca.multiply(qx);
		point qy=comp_loca			.multiply(new point(0,1,0));		qy=negative_rotate_absolute_loca.multiply(qy);
		point qz=comp_loca			.multiply(new point(0,0,1));		qz=negative_rotate_absolute_loca.multiply(qz);
			
		location p_loca=new location(p0,px,py,pz),q_loca=new location(q0,qx,qy,qz);
		
		init(p_loca.multiply(q_loca.negative()));
	}
	public movement_location_to_move_rotate(location matrix)
	{
		init(new location(matrix));
	}
	public movement_location_to_move_rotate(location comp_abaulate_loca,location comp_move_loca,location rotate_absolute_loca)
	{
		init(comp_abaulate_loca,comp_move_loca,rotate_absolute_loca);
	}
	public movement_location_to_move_rotate(location comp_abaulate_loca,location comp_move_loca,point rotate_center)
	{
		location rotate_comp_loca=new location(
				rotate_center,
				rotate_center.add(new point(1,0,0)),
				rotate_center.add(new point(0,1,0)),
				rotate_center.add(new point(0,0,1)));
		init(comp_abaulate_loca,comp_move_loca,rotate_comp_loca.multiply(location.standard_negative));
	}	
}
