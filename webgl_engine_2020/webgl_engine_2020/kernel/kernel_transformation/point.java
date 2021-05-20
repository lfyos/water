package kernel_transformation;

import kernel_common_class.const_value;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;

public class point {
	public double x,y,z;

	public point()
	{
		x=0;
		y=0;
		z=0;;
	}
	public point(point b)
	{
		load(b);
	}
	public point(double my_x,double my_y,double my_z)
	{
		x=my_x;	
		y=my_y;
		z=my_z;
	}
	public point(double my_x,double my_y,double my_z,double my_w)
	{
		if(Math.abs(my_w)<const_value.min_value){
			x=0;
			y=0;
			z=0;
		}else{
			x=my_x/my_w;
			y=my_y/my_w;
			z=my_z/my_w;
		}
	}
	public point(file_reader f)
	{
		load(f);
	}
	public point load(file_reader f)
	{
		x=f.get_double();
		y=f.get_double();
		z=f.get_double();
		return this;
	}
	public void write_out(file_writer f)
	{
		f.print(" ", x);
		f.print(" ", y);
		f.print(" ", z);
		f.print(" ");
	}
	public point load(point b)
	{
		x=b.x;		
		y=b.y;
		z=b.z;
		return this;
	}
	public point add(point b)
	{
		return new point(x+b.x,	y+b.y,	z+b.z);	
	}
	public point sub(point b)
	{
		return new point(x-b.x,y-b.y,z-b.z);	
	}
	public  double distance2()
	{
		return (x*x+y*y+z*z);
	}
	public double distance()
	{
		return Math.sqrt(distance2());
	}
	public point scale(double scale_value)
	{
		return new point(x*scale_value,y*scale_value,z*scale_value);
	}
	public point expand(double length)
	{
		double d;
		if((d=distance())<const_value.min_value)
			return new point(0,0,0);
		return scale(length/d);
	}
	
	public point reverse()
	{
		return new point(-x,-y,-z);
	}
	public double dot(point b)
	{
		return (x*(b.x)+y*(b.y)+z*(b.z));
	}
	public point cross(point b)
	{
		double x1=b.x,	y1=b.y,	z1=b.z;

		return new point(y*z1-y1*z,z*x1-x*z1,x*y1-y*x1);
	}
	public point  mix(point b,double p)
	{
		double x1=b.x,	y1=b.y,	z1=b.z;

		return new point(p*x+(1-p)*x1,p*y+(1-p)*y1,p*z+(1-p)*z1);
	}
	public point project_to_line(point p0,point p1)
	{
		point dir=p1.sub(p0);
		double A=dir.x,B=dir.y,C=dir.z;
		double D=(-(A*x+B*y+C*z));
		
		return (new plane(A,B,C,D)).insect_location(p0).multiply(p1);
	}
	public void get_point_data(String lead_str,client_request_response out,String follow_str)
	{
		if(lead_str!=null)
			out.print(lead_str);
		
		out.print("[",x);
		out.print(",",y);
		out.print(",",z);	
		out.print(",1.0]");
		
		if(follow_str!=null)
			out.print(follow_str);
	}
	public void get_point_data(client_request_response out)
	{
		get_point_data(null,out,null);
	}
	public void get_point_data(String lead_str,client_request_response out)
	{
		get_point_data(lead_str,out,null);
	}
	public void get_point_data(client_request_response out,String follow_str)
	{
		get_point_data(null,out,follow_str);
	}
}
