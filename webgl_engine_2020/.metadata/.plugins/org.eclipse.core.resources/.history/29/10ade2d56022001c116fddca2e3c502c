package kernel_part;

import kernel_common_class.const_value;
import kernel_part.body;
import kernel_part.face;
import kernel_part.face_curve;
import kernel_part.face_edge;
import kernel_part.face_loop;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_transformation.plane;
import kernel_transformation.point;

public class caculate_part_items
{
	public static point caculate_normal_for_ellipse_hyperbola_parabola(double par[])
	{
		point center	=new point(par[0],par[1],par[2]);
		point a_point	=new point(par[3],par[4],par[5]);
		point b_point	=new point(par[6],par[7],par[8]);
		point max_dir	=a_point.sub(center);
		point min_dir	=b_point.sub(center);
		return max_dir.expand(1).cross(min_dir.expand(1)).expand(1);
	}
	public static point[] caculate_point_for_ellipse_hyperbola_parabola(double par[],String curve_type)
	{
		point center	=new point(par[0],par[1],par[2]);
		point a_point	=new point(par[3],par[4],par[5]);
		point b_point	=new point(par[6],par[7],par[8]);
		point max_dir	=a_point.sub(center);
		point min_dir	=b_point.sub(center);
		double a		=max_dir.distance();
		double b		=min_dir.distance();
		max_dir=max_dir.expand(1);
		min_dir=min_dir.expand(1);

		location loca=new location(center,center.add(max_dir),center.add(min_dir),
				center.add(max_dir.cross(min_dir).expand(1))).multiply(location.standard_negative);
		switch(curve_type) {
		default:
			return new point[] {};
		case "ellipse":
			if(a*a>=b*b) {
				double c=Math.sqrt(Math.abs(a*a-b*b));
				return new point[]
				{
					center,
					loca.multiply(a,	0,	0),	loca.multiply(-a,	0,		0),
					loca.multiply(0,	b,	0),	loca.multiply(0,	-b,		0),
					loca.multiply(c,	0,	0),	loca.multiply(-c,	0,		0)
				};
			}else{
				double c=Math.sqrt(Math.abs(b*b-a*a));
				return new point[]
				{
					center,
					loca.multiply(a,	0,	0),	loca.multiply(-a,	0,		0),
					loca.multiply(0,	b,	0),	loca.multiply(0,	b,		0),
					loca.multiply(0,	c,	0),	loca.multiply(0,	-c,		0)
				};
			}
		case "hyperbola":
			double c=Math.sqrt(a*a+b*b);
			return new point[]
			{
				center,
				loca.multiply(a,	0,		0),	loca.multiply(-a,	0,		0),
				loca.multiply(0,	b,		0),	loca.multiply(0,	-b,		0),
				loca.multiply(c,	0,		0),	loca.multiply(-c,	0,		0)
			};
		case "parabola":
			return new point[]
			{
				center,
				loca.multiply(2*a,0,0)
			};
		}
	}
	
	public box my_box;

	public part_rude my_part_rude;
	public body my_body;
	public face my_face;
	
	public face_curve my_face_curve;
	public face_loop my_face_loop;
	public face_edge my_face_edge;

	public String part_item_name;
	
	private void clear_all()
	{
		my_box=null;
		
		my_part_rude=null;
		my_body=null;
		my_face=null;
		my_face_curve=null;
		my_face_loop=null;
		my_face_edge=null;
		
		part_item_name="未知";
	}
	private void do_caculate_part_box(part_rude box_part_mesh,int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		if((my_part_rude=box_part_mesh)==null)
			return;
		if(body_id==my_part_rude.body_number())
			if((face_id==0)&&(loop_id==0)&&(edge_id==0)&&(point_id==0)){
				my_box=new box(new point(0,0,0));
				return;
			}
		my_box=(my_part_rude.part_box!=null)?(my_part_rude.part_box):my_box;
		if((body_id<0)||(body_id>=(my_part_rude.body_number())))
			return ;
		if((my_body=my_part_rude.body_array[body_id])==null)
			return ;
		my_box=(my_body.body_box!=null)?my_body.body_box:my_box;

		if((face_id<0)||(face_id>=(my_body.face_number())))
			return ;
		if((my_face=my_body.face_array[face_id])==null)
			return ;
		my_box=(my_face.face_box!=null)?(my_face.face_box):my_box;
		
		my_face_curve=my_face.fa_curve;
		my_box=(my_face_curve.curve_box!=null)?(my_face_curve.curve_box):my_box;
		
		if((loop_id<0)||(loop_id>=(my_face_curve.face_loop_number())))
			return ;
		if((my_face_loop=my_face.fa_curve.f_loop[loop_id])==null)
			return ;
		my_box=(my_face_loop.loop_box!=null)?(my_face_loop.loop_box):my_box;
		
		if((edge_id<0)||(edge_id>=(my_face_loop.edge_number())))
			return ;
		if((my_face_edge=my_face_loop.edge[edge_id])==null)
			return ;
		my_box=(my_face_edge.edge_box!=null)?(my_face_edge.edge_box):my_box;
		if(point_id<0)
			return;
		
		switch(point_id){
		case 0:
			my_box=new box(new point(0,0,0));
			return;
		case 1:
			if(my_face_edge.start_point!=null)
				my_box=new box(my_face_edge.start_point);
			return;
		case 2:
			if(my_face_edge.end_point!=null)
				my_box=new box(my_face_edge.end_point);
			return;
		};
		
		switch(my_face_edge.curve_type) {
		case "line":
			my_box=new box(my_face_edge.end_point.add(my_face_edge.start_point).scale(0.5));
			return;
		case "circle":
			my_box=new box(new point(my_face_edge.curve_parameter[0],
				my_face_edge.curve_parameter[1],my_face_edge.curve_parameter[2]));
			return;
		case "ellipse":
		case "hyperbola":
		case "parabola":
			point point_array[]=caculate_point_for_ellipse_hyperbola_parabola(
					my_face_edge.curve_parameter,my_face_edge.curve_type);
			if(point_id>=5)
				if((point_id-5)<point_array.length)
					my_box=new box(point_array[point_id-5]);
			return;
		case "pickup_point_set":
			if(my_face_edge.curve_parameter!=null)
				if(point_id>=1000)
					if((point_id-1000)<(my_face_edge.curve_parameter.length/3))
						my_box=new box(new point(
									my_face_edge.curve_parameter[3*point_id+0],
									my_face_edge.curve_parameter[3*point_id+1],
									my_face_edge.curve_parameter[3*point_id+2]));
			return;
		case "render_point_set":
			return;
		}
		return;
	}
	private location loca,neg_loca;
	private point local_p0,local_p1,local_normal;
	private void create_location(point pp0,point pp1,point origin,point axis)
	{
		point dx,dy,dz;
		{
			point  p0 =		origin,							p1=origin.add(axis);
			point  p[]=	{	new point(0,0,0),				new point(1,0,0),				new point(0,1,0),				new point(0,0,1)				};
			point  q[]=	{	p[0].project_to_line(p0,p1),	p[1].project_to_line(p0,p1),	p[2].project_to_line(p0,p1),	p[3].project_to_line(p0,p1)		};
			double d[]=	{	p[0].sub(q[0]).distance2(),		p[1].sub(q[1]).distance2(),		p[2].sub(q[2]).distance2(),		p[3].sub(q[3]).distance2()		};
		
			int select_id=0;
			if(d[1]>d[select_id])
				select_id=1;
			if(d[2]>d[select_id])
				select_id=2;
			if(d[3]>d[select_id])
				select_id=3;
			
			dx=p[select_id].sub(q[select_id]);
		}
		dz=axis.expand(1.0);
		dy=dz.cross(dx).expand(1.0);
		dx=dy.cross(dz).expand(1.0);
		
		point p_origin=origin,p_x=origin.add(dx),p_y=origin.add(dy),p_z=origin.add(dz);
		
		loca=(new location(p_origin,p_x,p_y,p_z)).multiply(location.standard_negative);
		neg_loca=loca.negative();
		
		local_p0=neg_loca.multiply(pp0);
		local_p1=neg_loca.multiply(pp1);
		local_normal=local_p1.sub(local_p0);
	}
	
	private point caculate_focus_point_from_abc(double a,double b,double c,point p_default,String name)
	{
		double dlt=b*b-4*a*c;
		
		if(Math.abs(a)<const_value.min_value){
			if(Math.abs(b)>const_value.min_value)
				return loca.multiply(local_normal.scale(-c/b).add(local_p0));
			part_item_name="第一种无法解出的"+name;
			return p_default;
		}
		if(dlt<0){
			part_item_name="第二种无法解出的"+name;
			return p_default;
		}
		dlt=Math.sqrt(dlt);
		double t1=(-b-dlt)/(2*a),t2=(-b+dlt)/(2*a);
		
		point result_local_p1=local_normal.scale(t1).add(local_p0);
		point result_local_p2=local_normal.scale(t2).add(local_p0);

		point result_global_p1=loca.multiply(result_local_p1);
		point result_global_p2=loca.multiply(result_local_p2);
		
		if(p_default!=null){
			double d1=p_default.sub(result_global_p1).distance2();
			double d2=p_default.sub(result_global_p2).distance2();
			
			return (d1<d2)?result_global_p1:result_global_p2;
		}
		if((t1<0)||(t1>1.0))
			return result_global_p2;
		if((t2<0)||(t2>1.0))
			return result_global_p1;
		
		if(my_face.face_box!=null){
			if(my_face.face_box.test_in_box(result_global_p1)){
				if(!my_face.face_box.test_in_box(result_global_p2))
					return result_global_p1;
			}else if(my_face.face_box.test_in_box(result_global_p2))
				return result_global_p2;
			else
				return p_default;
		}
		return result_global_p1;
	}
	public point caculate_focus_point(point p0,point p1)
	{
		if(my_box!=null)
			if(my_box.distance2()<const_value.min_value2)
				return my_box.center();
		
		if(my_face==null)
			return p0;
			
		double face_parameter[]=my_face.fa_face.face_parameter;
		if(face_parameter==null)
			return p0;
		
		if(my_face.fa_face.face_type.compareTo("plane")==0){
			part_item_name="平面";
			point plane_point=new point(face_parameter[3],face_parameter[4],face_parameter[5]);
			point plane_normal=new point(face_parameter[0],face_parameter[1],face_parameter[2]);
			plane pl=new plane(plane_point,plane_point.add(plane_normal));
			return pl.error_flag?p0:pl.insect_location(p0).multiply(p1);
		}
		if(my_face.fa_face.face_type.compareTo("cylinder")==0){		
			part_item_name="柱面";
			if((my_face.fa_face.face_parameter_number()<7)||(p0==null)){
				part_item_name="柱面参数不足:"+Integer.toString(my_face.fa_face.face_parameter_number());
				return p0;
			}
				
			double radius=face_parameter[6];

			create_location(p0,p1,
					new point(face_parameter[0],face_parameter[1],face_parameter[2]),
					new point(face_parameter[3],face_parameter[4],face_parameter[5])
			);

			double a=local_normal.x*local_normal.x+local_normal.y*local_normal.y;
			double b=2*(local_p0.x*local_normal.x+local_p0.y*local_normal.y);
			double c=local_p0.x*local_p0.x+local_p0.y*local_p0.y-radius*radius;
			
			return caculate_focus_point_from_abc(a,b,c,p0,"柱面");
		}
		if(my_face.fa_face.face_type.compareTo("cone")==0){	
			part_item_name="锥面";
			if((my_face.fa_face.face_parameter_number()<8)||(p0==null)){
				part_item_name="锥面参数不足:"+Integer.toString(my_face.fa_face.face_parameter_number());
				return p0;
			}				
			double radius=face_parameter[6],half_angle=face_parameter[7];
			double tan=Math.tan(half_angle);
			double h=radius/tan;
			double tan2=tan*tan;
			create_location(p0,p1,
					new point(face_parameter[0],face_parameter[1],face_parameter[2]),
					new point(face_parameter[3],face_parameter[4],face_parameter[5])
			);
			double a=local_normal.x*local_normal.x+local_normal.y*local_normal.y-tan2*local_normal.z*local_normal.z;
			double b=2*(local_p0.x*local_normal.x+local_p0.y*local_normal.y-tan2*(local_p0.z-h)*local_normal.z);
			double c=local_p0.x*local_p0.x+local_p0.y*local_p0.y-tan2*(local_p0.z-h)*(local_p0.z-h);
			
			return caculate_focus_point_from_abc(a,b,c,p0,"锥面");
		}
		
		if(my_face.fa_face.face_type.compareTo("sphere")==0){	
			part_item_name="球面";
			if((my_face.fa_face.face_parameter_number()<4)||(p0==null)){
				part_item_name="球面参数不足:"+Integer.toString(my_face.fa_face.face_parameter_number());
				return p0;
			}	
			create_location(p0,p1,new point(face_parameter[0],face_parameter[1],face_parameter[2]),new point(0,0,1));
			double radius=face_parameter[3];
				
			double a=local_normal.x*local_normal.x+local_normal.y*local_normal.y+local_normal.z*local_normal.z;
			double b=2*(local_p0.x*local_normal.x+local_p0.y*local_normal.y+local_p0.z*local_normal.z);
			double c=local_p0.x*local_p0.x+local_p0.y*local_p0.y+local_p0.z*local_p0.z-radius*radius;
			return caculate_focus_point_from_abc(a,b,c,p0,"锥面");
		}
		
		if(my_face.fa_face.face_type.compareTo("torus")==0){
			part_item_name="环面";
			return p0;
		}

		part_item_name="未知曲面";
		
		return p0;
	}
	public caculate_part_items(part p,int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		clear_all();
		if(p==null)
			return;
		if(p.part_mesh==null)
			return;
		if((my_part_rude=p.part_mesh)==null)
			return;
		do_caculate_part_box(my_part_rude,body_id,face_id,loop_id,edge_id,point_id);
		return;
	}
}
