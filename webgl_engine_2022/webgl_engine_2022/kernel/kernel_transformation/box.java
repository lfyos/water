package kernel_transformation;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class box 
{
	public point p[];
	
	private void init()
	{
		p=new point[2];
		p[0]=new point();
		p[1]=new point();
	}

	public box()
	{
		init();
		
		p[0].x=0;p[0].y=0;p[0].z=0;
		p[1].x=0;p[1].y=0;p[1].z=0;
	}

	public box(point pp)
	{
		init();
		
		p[0]=new point(pp);
		p[1]=new point(pp);
	}
	
	public box(point pp[])
	{
		init();
		
		double min_x=pp[0].x,min_y=pp[0].y,min_z=pp[0].z;
		double max_x=pp[0].x,max_y=pp[0].y,max_z=pp[0].z;
		
		for(int i=1,ni=pp.length;i<ni;i++) {
			if(min_x>pp[i].x)
				min_x=pp[i].x;
			if(min_y>pp[i].y)
				min_y=pp[i].y;
			if(min_z>pp[i].z)
				min_z=pp[i].z;
			
			if(max_x<pp[i].x)
				max_x=pp[i].x;
			if(max_y<pp[i].y)
				max_y=pp[i].y;
			if(max_z<pp[i].z)
				max_z=pp[i].z;
		}
		p[0].x=min_x;
		p[0].y=min_y;
		p[0].z=min_z;

		p[1].x=max_x;
		p[1].y=max_y;
		p[1].z=max_z;
	}
	
	public box(point p0,point p1)
	{
		init();
		
		p[0].x=(p0.x<p1.x)?p0.x:p1.x;
		p[0].y=(p0.y<p1.y)?p0.y:p1.y;
		p[0].z=(p0.z<p1.z)?p0.z:p1.z;

		p[1].x=(p0.x>p1.x)?p0.x:p1.x;
		p[1].y=(p0.y>p1.y)?p0.y:p1.y;
		p[1].z=(p0.z>p1.z)?p0.z:p1.z;		
	}
	public box(double x0,double y0,double z0,double x1,double y1,double z1)
	{
		init();
		p[0]=new point((x0<x1)?x0:x1,(y0<y1)?y0:y1,(z0<z1)?z0:z1);
		p[1]=new point((x0>x1)?x0:x1,(y0>y1)?y0:y1,(z0>z1)?z0:z1);
	}
	
	public box(box b)
	{
		init();
		p[0]=new point(b.p[0]);
		p[1]=new point(b.p[1]);		
	}
	public box(file_reader f)
	{
		p=new point[2];
		p[0]=new point(f);
		p[1]=new point(f);
	}
	public void write_out(file_writer f)
	{
		p[0].write_out(f);
		p[1].write_out(f);
	}
	public box add(box b)
	{
		double x0=p[0].x<b.p[0].x?p[0].x:b.p[0].x;
		double y0=p[0].y<b.p[0].y?p[0].y:b.p[0].y;
		double z0=p[0].z<b.p[0].z?p[0].z:b.p[0].z;
		
		double x1=p[1].x>b.p[1].x?p[1].x:b.p[1].x;
		double y1=p[1].y>b.p[1].y?p[1].y:b.p[1].y;
		double z1=p[1].z>b.p[1].z?p[1].z:b.p[1].z;

		return new box(x0,y0,z0,x1,y1,z1);
	}
	
	public point center()
	{
		double x0=p[0].x,y0=p[0].y,z0=p[0].z;
		double x1=p[1].x,y1=p[1].y,z1=p[1].z;
		return new point(x0+x1,y0+y1,z0+z1,2);
	}
	
	public double distance()
	{
		return p[1].sub(p[0]).distance();
	}
	public double distance2()
	{
		return p[1].sub(p[0]).distance2();
	}
	public double volume_for_compare()
	{
		point d=p[1].sub(p[0]);
		double box_volume=Math.abs(d.x*d.y*d.z);
		double average_edge_length=(Math.abs(d.x)+Math.abs(d.y)+Math.abs(d.z))/3.0;
		double edge_volume=average_edge_length*average_edge_length*average_edge_length;
		return (box_volume<edge_volume)?edge_volume:box_volume;
	}
	
	public box add(point b)
	{
		return add(new box(b));
	}

	public boolean test_in_box(point my_p)
	{
		if(my_p.x>=p[0].x)
			if(my_p.x<=p[1].x)
				
				if(my_p.y>=p[0].y)
					if(my_p.y<=p[1].y)
				
						if(my_p.z>=p[0].z)
							if(my_p.z<=p[1].z)
						
								return true;
		return false;
				
	}
	public box scale(double scale_value)
	{
		return new box(p[0].scale(scale_value),p[1].scale(scale_value));
	}
	public box center_scale(double scale_value)
	{
		point center_point=center();
		point dir=p[1].sub(center_point).scale(scale_value);
		return new box(center_point.sub(dir),center_point.add(dir));
	}
};