package kernel_common_class;

import kernel_transformation.point;
import kernel_transformation.location;

public class gps_caculater 
{
	private point p0;
	private location loca;
	
	public double latitude,longitude,altitude,delta_latitude,delta_longitude;
	public point p;
	
	private point caculate_routine(double latitude,double longitude,double altitude)
	{
		latitude =Math.PI*latitude/180.0;
		longitude=Math.PI*longitude/180.0;
		double sin_latitude =Math.sin(latitude);
		double cos_latitude =Math.cos(latitude);
		double sin_longitude=Math.sin(longitude);
		double cos_longitude=Math.cos(longitude);
			
		double a =6378137.0;
		double b =6356752.314;
		double e2=(a*a-b*b)/(a*a);
		double n =a/Math.sqrt(1-e2*sin_latitude*sin_latitude);

		double x=(n+altitude)*cos_latitude*cos_longitude;
		double y=(n+altitude)*cos_latitude*sin_longitude;
		double z=(n*(1-e2)+altitude)*sin_latitude;
		
		return new point(x,y,z);
	}
	public point caculate(double latitude,double longitude,double altitude)
	{
		p=loca.multiply(caculate_routine(latitude,longitude,altitude).sub(p0));
		p=p.scale(1000);
		return p;
	}
	public gps_caculater(
			double my_latitude,			double my_longitude,		double my_altitude,
			double my_delta_latitude,	double my_delta_longitude)
	{
		point dx,dy,dz;

		latitude		= my_latitude;
		longitude		= my_longitude;
		altitude		= my_altitude;
		delta_latitude	=(my_delta_latitude <=const_value.min_value)?(1.0/360.0):my_delta_latitude;
		delta_longitude	=(my_delta_longitude<=const_value.min_value)?(1.0/360.0):my_delta_longitude;
		
		p0=caculate_routine(latitude,				longitude,					altitude);
		dx=caculate_routine(latitude,				longitude+delta_longitude,	altitude);
		dz=caculate_routine(latitude-delta_latitude,longitude,					altitude);
		dx=dx.sub(p0).expand(1.0);
		dz=dz.sub(p0).expand(1.0);
		
		dy=dz.cross(dx).expand(1.0);
		dx=dy.cross(dz).expand(1.0);
		
		location x=new location(new point(0,0,0),dx,dy,dz);
		location y=new location(new point(0,0,0),new point(1,0,0),new point(0,1,0),new point(0,0,1));
		
		loca=y.multiply(x.negative());
		p=new point(0,0,0);
	}
}