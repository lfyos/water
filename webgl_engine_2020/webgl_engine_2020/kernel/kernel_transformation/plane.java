package kernel_transformation;
import kernel_common_class.const_value;
import kernel_component.component;


public class plane 
{
	public boolean error_flag,close_clip_plane_flag;	
	public double A,B,C,D;

	public boolean test_same_plane(plane pl)
	{
		double d_a=pl.A-A,d_b=pl.B-B,d_c=pl.C-C,d_d=pl.D-D;
		return ((d_a*d_a+d_b*d_b+d_c*d_c+d_d*d_d)<const_value.min_value2)?true:false;
	}
	private location project_to_plane_location(double scale)
	{
		scale/=A*A+B*B+C*C;
		double a_scale=A*scale,b_scale=B*scale,c_scale=C*scale,d_scale=D*scale;
		return new location(new double[]
			{
				1-A*a_scale,	 -B*a_scale,	 -C*a_scale,	0,
				 -A*b_scale,	1-B*b_scale,	 -C*b_scale,	0,
				 -A*c_scale,	 -B*c_scale,	1-C*c_scale,	0,
				 -A*d_scale,	 -B*d_scale,	 -C*d_scale,	1
			});
	}
	public location project_to_plane_location()
	{
		return project_to_plane_location(1.0);
	}
	public location reflect_by_plane_location()
	{
		return project_to_plane_location(2.0);
	}
	public location shoot_to_plane_location(point p0,double t)
	{
		double p=p0.x*A+p0.y*B+p0.z*C+D;
		double q=D-(1.0-t)*p,tp=t*p;
		double shoot_plane_data[]={
			p0.x*A-tp,	p0.y*A,		p0.z*A,		A,	
			p0.x*B,		p0.y*B-tp,	p0.z*B,		B,	
			p0.x*C,		p0.y*C,		p0.z*C-tp,	C,	
			p0.x*q,		p0.y*q,		p0.z*q,		D-p
		};
		return new location(shoot_plane_data);
	}
	public location shoot_to_plane_location(point p0)
	{
		return shoot_to_plane_location(p0,1.0);
	}
	public void copy_from(plane p)
	{
		A=p.A;
		B=p.B;
		C=p.C;
		D=p.D;
		error_flag=p.error_flag;
		close_clip_plane_flag=p.close_clip_plane_flag;
	}
	public location insect_location(point s)
	{
		double P=(-s.x*A-s.y*B-s.z*C),Q=P-D;
		double location_data[]={
				s.x*A+Q, 	s.y*A,		s.z*A,		A,
				s.x*B, 		s.y*B+Q,	s.z*B,		B,
				s.x*C, 		s.y*C,		s.z*C+Q,	C,
				s.x*D,		s.y*D,		s.z*D,		P
		};
		return new location(location_data);
	}
	public point insection_point(point s,point t)
	{
		point dir=t.sub(s);
		if(dir.distance2()<const_value.min_value2)
			return null;
		if(Math.abs(dir.expand(1.0).dot(new point(A,B,C)))<const_value.min_value)
			return null;
		if(Math.abs(test(s))>=Math.abs(test(t)))
			return insect_location(s).multiply(t);
		else
			return insect_location(t).multiply(s);
	}
	public double test(double x,double y,double z)
	{
		return A*x+B*y+C*z+D;
	}
	public double test(point p)
	{
		return test(p.x,p.y,p.z);
	}
	private int clip_compare_center(box b,point center)
	{
		double max_box_distance2=b.distance2();
		double distance_to_plane=test(center);
		
		if((distance_to_plane*distance_to_plane)>(max_box_distance2/4.0))
			return (distance_to_plane<=0)?8:0;
		else
			return 4;
	}
	
	public int clip_component_test(component comp,int parameter_channel_id)
	{
		box b=null;
		int return_value,positive_number,negative_number,i,n;
		double distance_to_plane;
		
		final int test_map[][]={
			{0,0,0},{1,1,1},
			{0,0,1},{1,1,0},
			{0,1,0},{1,0,1},
			{0,1,1},{1,0,0}
		};

		if(comp.children_number()<=0){
			if((b=comp.model_box)==null)
				return 4;
			if((return_value=clip_compare_center(b,comp.absolute_location.multiply(b.center())))!=4)
				return return_value;
			for(i=0,n=test_map.length,positive_number=0,negative_number=0;i<n;i++){
				distance_to_plane=test(comp.absolute_location.multiply(
						new point(b.p[test_map[i][0]].x,b.p[test_map[i][1]].y,b.p[test_map[i][2]].z)));
				if(distance_to_plane<=0){
					if(positive_number>0)
						return 4;
					negative_number++;
				}else{
					if(negative_number>0)
						return 4;
					positive_number++;
				}
			}
			return (negative_number>0)?8:0;
		}
		if((b=comp.get_component_box(false))!=null){
			if((return_value=clip_compare_center(b,b.center()))!=4)
				return return_value;
			for(i=0,n=test_map.length,positive_number=0,negative_number=0;i<n;i++){
				distance_to_plane=test(new point(b.p[test_map[i][0]].x,b.p[test_map[i][1]].y,b.p[test_map[i][2]].z));
				if(distance_to_plane<=0){
					if(positive_number>0)
						return 4;
					negative_number++;
				}else{
					if(negative_number>0)
						return 4;
					positive_number++;
				}
			}
			return (negative_number>0)?8:0;
		}
		if(!(comp.get_can_display_assembly_flag(parameter_channel_id)))
			return 4;
		if((b=comp.model_box)==null)
			return 4;	
		if((return_value=clip_compare_center(b,comp.absolute_location.multiply(b.center())))!=4)
			return return_value;
		for(i=0,n=test_map.length,positive_number=0,negative_number=0;i<n;i++){
			distance_to_plane=test(comp.absolute_location.multiply(
					new point(b.p[test_map[i][0]].x,b.p[test_map[i][1]].y,b.p[test_map[i][2]].z)));
			if(distance_to_plane<=0){
				if(positive_number>0)
					return 4;
				negative_number++;
			}else{
				if(negative_number>0)
					return 4;
				positive_number++;
			}
		}
		return (negative_number>0)?8:0;
	}	
	public plane(double my_A,double my_B,double my_C,double my_D)
	{
		double d=Math.sqrt(my_A*my_A+my_B*my_B+my_C*my_C);
		if(Math.abs(d)>const_value.min_value)
			error_flag=false;
		else{		
			my_A=0;
			my_B=1.0;
			my_C=0;
			my_D=0;
			d=1.0;
			error_flag=true;
		}
		A=my_A/d;
		B=my_B/d;
		C=my_C/d;
		D=my_D/d;	
		close_clip_plane_flag=false;
	}
	public plane(point p0,point p1)
	{
		point d=p1.sub(p0);
		double my_A=d.x,my_B=d.y,my_C=d.z;
		double my_D=p0.x*my_A+p0.y*my_B+p0.z*my_C;
		copy_from(new plane(my_A,my_B,my_C,-my_D));
	}
	public plane(point p0,point p1,point p2)
	{
		point pp=(p1.sub(p0)).cross(p2.sub(p1)).add(p0);
		copy_from(new plane(p0,pp));
	}
	public plane(plane p)
	{
		copy_from(p);
	}
};