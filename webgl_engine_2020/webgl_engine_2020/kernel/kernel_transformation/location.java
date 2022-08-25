package kernel_transformation;

import kernel_common_class.const_value;
import kernel_common_class.common_reader;
import kernel_file_manager.file_writer;

public class location
{
	public static final point standard_point[]=new point[]{
			new point(0,0,0),new point(1,0,0),new point(0,1,0),new point(0,0,1)};
	public static final location standard_negative=new location(
			new double[]{-1,1,	0,	0,	-1,	0,	1,	0,	-1,	0,	0,	1,	1,	0,	0,	0});
	
	private double a[][];
	
	public boolean is_not_identity_matrix()
	{
		double difference2=0;
		for(int i=0,n=standard_point.length;i<n;i++)
			difference2+=multiply(standard_point[i]).sub(standard_point[i]).distance2();
		return (difference2>=const_value.min_value2)?true:false;
	}
	public static boolean is_not_same_location(location s,location d)
	{
		if(s==null)
			return (d!=null)?true:false; 
		if(d==null)
			return true;
		double difference2=0;
		for(int i=0,ni=standard_point.length;i<ni;i++)
			difference2+=d.multiply(standard_point[i]).sub(s.multiply(standard_point[i])).distance2();
		return (difference2>=const_value.min_value2)?true:false;
	}
	public location()
	{
		a=new double [][]{
				{ 1, 0, 0, 0},
				{ 0, 1, 0, 0},
				{ 0, 0, 1, 0},
				{ 0, 0, 0, 1}
			};
	}
	public location(double b[])
	{
		a=new double [][]{
				{b[ 0], b[ 4], b[ 8], b[12]},
				{b[ 1], b[ 5], b[ 9], b[13]},
				{b[ 2], b[ 6], b[10], b[14]},
				{b[ 3], b[ 7], b[11], b[15]}
			};
	}
	public location(String str,String ch)
	{
		int ch_length;
		a=new double [][]
		{
			{ 1, 0, 0, 0},
			{ 0, 1, 0, 0},
			{ 0, 0, 1, 0},
			{ 0, 0, 0, 1}
		};
		if(ch==null)
			return;
		if((ch_length=ch.length())<=0)
			return;
		for(int i=0;i<4;i++)
			for(int index_id,j=0;j<4;j++){
				if((index_id=str.indexOf(ch))<0) {
					a[j][i]=Double.parseDouble(str);
					return;
				}
				a[j][i]=Double.parseDouble(str.substring(0,index_id));
				str=str.substring(index_id+ch_length);
			}
	}
	public location(
			double d00,double d01,double d02,double d03,
			double d04,double d05,double d06,double d07,
			double d08,double d09,double d10,double d11,
			double d12,double d13,double d14,double d15)
	{
		a=new double [][]{
			{d00, d04, d08, d12},
			{d01, d05, d09, d13},
			{d02, d06, d10, d14},
			{d03, d07, d11, d15}
		};
	}
	public double []get_location_data()
	{
		return new double []{
				a[0][0],a[1][0],a[2][0],a[3][0],
				a[0][1],a[1][1],a[2][1],a[3][1],
				a[0][2],a[1][2],a[2][2],a[3][2],
				a[0][3],a[1][3],a[2][3],a[3][3]
			};
	}
	public  location(location b)
	{
		a=new double [][]{
				{b.a[0][0],b.a[0][1],b.a[0][2],b.a[0][3]},
				{b.a[1][0],b.a[1][1],b.a[1][2],b.a[1][3]},
				{b.a[2][0],b.a[2][1],b.a[2][2],b.a[2][3]},
				{b.a[3][0],b.a[3][1],b.a[3][2],b.a[3][3]}
		};
	}
	public location(point p0, point p1, point p2, point p3)
	{
		a=new double [][]{
				{p0.x,		p1.x,		p2.x,		p3.x},	
				{p0.y,		p1.y,		p2.y,		p3.y},
				{p0.z,		p1.z,		p2.z,		p3.z},
				{1.0 ,		1.0 ,		1.0 ,		1.0 }
		};
	}
	public location(common_reader reader)
	{
		a=new double[4][4];
		for(int i=0;i<4;i++)
			for(int j=0;j<4;j++)
				a[j][i]=reader.get_double();
	}
	public double []get_location_data(file_writer out)
	{
		double p[]=get_location_data();
		for(int i=0,ni=p.length;i<ni;i++)
			out.print(" ",p[i]);
		out.println(" ");
		return p;
	}
	public location multiply(location b)
	{
		double x[]=new double[16];
		
		for(int i=0;i<4;i++)
			for(int j=0;j<4;j++){
				x[i+4*j]=0;
				for(int k=0;k<4;k++)
					x[i+4*j]+=a[i][k]*(b.a[k][j]);
			}
		return new location(x);
	}
	public point multiply(point b)
	{
		return new point(
						a[0][0]*(b.x)+a[0][1]*(b.y)+a[0][2]*(b.z)+a[0][3],
						a[1][0]*(b.x)+a[1][1]*(b.y)+a[1][2]*(b.z)+a[1][3],
						a[2][0]*(b.x)+a[2][1]*(b.y)+a[2][2]*(b.z)+a[2][3],
						a[3][0]*(b.x)+a[3][1]*(b.y)+a[3][2]*(b.z)+a[3][3]
					);
	}
	public point multiply(double x,double y,double z)
	{
		return multiply(new point(x,y,z));
	}
	public box multiply(box b)
	{
		box ret_val=null;
		
		for(int i=0;i<2;i++)
			for(int j=0;j<2;j++)
				for(int k=0;k<2;k++){
					point p=multiply(new point(b.p[i].x,b.p[j].y,b.p[k].z));
					if((i+j+k)==0)
						ret_val=new box(p);
					else
						ret_val=ret_val.add(new box(p));
				}
		return ret_val;
	}
	public location normalize()
	{
		point p0=multiply(standard_point[0]);
		point dx=multiply(standard_point[1]).sub(p0);
		point dy=multiply(standard_point[2]).sub(p0);
		point dz=multiply(standard_point[3]).sub(p0);
		
		point cross_dxdy=dx.cross(dy);
		point cross_dydz=dy.cross(dz);
		point cross_dzdx=dz.cross(dx);
		
		double cross_dxdy_length2=cross_dxdy.distance2();
		double cross_dydz_length2=cross_dydz.distance2();
		double cross_dzdx_length2=cross_dzdx.distance2();
		
		if((cross_dxdy_length2>cross_dydz_length2)&&(cross_dxdy_length2>cross_dzdx_length2)){
			dz=cross_dxdy;
			if(dx.distance2()<dy.distance2())
				dx=dy.cross(dz);
			else
				dy=dz.cross(dx);
		}else if((cross_dydz_length2>cross_dxdy_length2)&&(cross_dydz_length2>cross_dzdx_length2)) {
			dx=cross_dydz;
			if(dy.distance2()<dz.distance2())
				dy=dz.cross(dx);
			else
				dz=dx.cross(dy);
		}else{
			dy=cross_dzdx;
			if(dx.distance2()<dz.distance2())
				dx=dy.cross(dz);
			else
				dz=dx.cross(dy);
		}
		if(dx.distance2()>const_value.min_value2)
			if(dy.distance2()>const_value.min_value2)
				if(dz.distance2()>const_value.min_value2)
					return new location(p0,
							p0.add(dx.expand(1.0)),
							p0.add(dy.expand(1.0)),
							p0.add(dz.expand(1.0))).multiply(standard_negative);
		
		return move_rotate(p0.x,p0.y,p0.z,0,0,0);
	}
	public location negative()
	{
		double b[][]={
				{a[0][0],a[0][1],a[0][2],a[0][3]},
				{a[1][0],a[1][1],a[1][2],a[1][3]},
				{a[2][0],a[2][1],a[2][2],a[2][3]},
				{a[3][0],a[3][1],a[3][2],a[3][3]}
		};
		double c[][]={
				{ 1, 0, 0, 0},
				{ 0, 1, 0, 0},
				{ 0, 0, 1, 0},
				{ 0, 0, 0, 1}
		};
		for(int i=0;i<3;i++){
			int k=i;
			for(int j=i+1;j<4;j++)
				if(Math.abs(b[j][i])>Math.abs(b[k][i]))
					k=j;
			if(i!=k){
				for(int j=i;j<4;j++){
					double p=b[i][j];
					b[i][j]=b[k][j];
					b[k][j]=p;
				}
				for(int j=0;j<4;j++){
					double p=c[i][j];
					c[i][j]=c[k][j];
					c[k][j]=p;
				}
			}
			for(int j=i+1;j<4;j++){
				double p=b[j][i]/b[i][i];
				for(k=i+1;k<4;k++)
					b[j][k]-=p*b[i][k];
				for(k=0;k<4;k++)
					c[j][k]-=p*c[i][k];
			}
		}
		for(int i=3;i>0;i--)
			for(int j=0;j<i;j++){
				double p=b[j][i]/b[i][i];
				for(int k=0;k<4;k++)
					c[j][k]-=p*c[i][k];
			}
		for(int i=0;i<4;i++){
			double p=b[i][i];
			for(int j=0;j<4;j++)
				c[i][j]/=p;
		}
		return new location(
			new double []
					{
						c[0][0],c[1][0],c[2][0],c[3][0],
						c[0][1],c[1][1],c[2][1],c[3][1],
						c[0][2],c[1][2],c[2][2],c[3][2],
						c[0][3],c[1][3],c[2][3],c[3][3]
					});
	}
	public static location caculate_location_from_three_point(point p0,point dx,point dy,boolean modify_flag)
	{
		point dz;
		if(dx.distance2()<const_value.min_value2)
			return null;
		if(dy.distance2()<const_value.min_value2)
			return null;
		if((dz=dx.cross(dy)).distance2()<const_value.min_value2)
			return null;
		if(modify_flag){
			if((dy=dz.cross(dx)).distance2()<const_value.min_value2)
				return null;
		}else{
			if((dx=dy.cross(dz)).distance2()<const_value.min_value2)
				return null;
		}
		point px=p0.add(dx.expand(1.0));
		point py=p0.add(dy.expand(1.0));
		point pz=p0.add(dz.expand(1.0));
		location loca=new location(p0,px,py,pz);
		loca=loca.multiply(location.standard_negative);
		return loca;
	}
	
	public static location mix_location(location start_location,location terminate_location,double p)
	{
		if(p<=0.0)
			return new location(start_location);
		if(p>=1.0)
			return new location(terminate_location);
		
		double start_data[]		=start_location.get_location_data();
		double terminate_data[]	=terminate_location.get_location_data();
		
		double a[]=new double[start_data.length];
		for(int i=0,n=a.length;i<n;i++)
			a[i]=(start_data[i])*(1.0-p)+(terminate_data[i])*p;
		return (new location(a)).normalize();
	}
	public static location combine_location(location location_array[])
	{
		if(location_array==null)
			return null;
		
		double data[]=null;
		int n=0;
		for(int i=0;i<location_array.length;i++)
			if(location_array[i]!=null){
				if(data==null){
					data=location_array[i].get_location_data();
					n++;
				}else{
					double my_data[]=location_array[i].get_location_data();
					if(my_data!=null){
						for(int j=0,nj=my_data.length;j<nj;j++)
							data[j]+=my_data[j];
						n++;
					}
				}
			}
		if(data==null)
			return null;
		for(int i=0,ni=data.length;i<ni;i++)
			data[i]/=(double)n;
		return (new location(data)).normalize();
	}
	public static location move_rotate(String str,String ch)
	{
		if(ch!=null){
			double mr[]=new double[] {0,0,0,0,0,0};
			for(int index_id,ch_length=ch.length(),i=0,ni=mr.length;i<ni;i++) {
				if((index_id=str.indexOf(ch))<0) {
					mr[i]=Double.parseDouble(str);
					break;
				}
				mr[i]=Double.parseDouble(str.substring(0,index_id));
				str=str.substring(index_id+ch_length);
			}
			return move_rotate(mr);
		}
		return new location();
	}
	public static location move_rotate(double mr[])
	{
		return move_rotate(mr[0],mr[1],mr[2],mr[3],mr[4],mr[5]);
	}
	public static location move_rotate(double mx,double my,double mz,double rx,double ry,double rz)
	{
		if((rx==0)&&(ry==0)&&(rz==0))
			return new location(new double[]
				{
					1,	0,	0,	0,
					0,	1,	0,	0,
					0,	0,	1,	0,
					mx,	my,	mz,	1
				});
		double cos_alf	=Math.cos(rx*Math.PI/180),	sin_alf	 =Math.sin(rx*Math.PI/180);
		double cos_belta=Math.cos(ry*Math.PI/180),	sin_belta=Math.sin(ry*Math.PI/180);
		double cos_gamma=Math.cos(rz*Math.PI/180),	sin_gamma=Math.sin(rz*Math.PI/180);
		double p[]=new double[]
		{
			 cos_gamma*cos_belta,								//	0:	a[0][0]
			 sin_gamma*cos_belta,								//	1:	a[1][0]
			-sin_belta,											//	2:	a[2][0]
			 0,													//	3:	a[3][0]
				
			-sin_gamma*cos_alf+cos_gamma*sin_belta*sin_alf,		//	4:	a[0][1]
			 cos_gamma*cos_alf+sin_gamma*sin_belta*sin_alf,		//	5:	a[1][1]
			 cos_belta*sin_alf,									//	6:	a[2][1]
			 0,													//	7:	a[3][1]
					
			 sin_gamma*sin_alf+cos_gamma*sin_belta*cos_alf,		//	8:	a[0][2]
			-cos_gamma*sin_alf+sin_gamma*sin_belta*cos_alf,		//	9:	a[1][2]
			 cos_belta*cos_alf,									//	10:	a[2][2]
			 0,													//	11:	a[3][2]
				
			 mx,												//	12:	a[0][3]
			 my,												//	13:	a[1][3]
			 mz,												//	14:	a[2][3]
			 1													//	15:	a[3][3]
		};
		return new location(p);
	}
	public static location quaternion(double x,double y,double z,double w)
	{
		return new location(new double[] 
				{
						1-2*y*y-2*z*z,		2*x*y+2*w*z,			2*x*z-2*w*y,		0,
						2*x*y-2*w*z,		1-2*x*x-2*z*z,			2*y*z+2*w*x,		0,
						2*x*z+2*w*y,		2*y*z-2*w*x,			1-2*x*x-2*y*y,		0,
						0,					0,						0,					1
				});
	}
	public static location scale(double sx,double sy,double sz)
	{
		return new location(
				new double[] 
						{
							sx,	0,	0,	0,
							0,	sy,	0,	0,
							0,	0,	sz,	0,
							0,	0,	0,	1
						});
	}
	private double caculate_angle(double sin_value,double cos_value)
	{
		sin_value=(sin_value<-1.0)?-1.0:(sin_value>1.0)?1.0:sin_value;
		cos_value=(cos_value<-1.0)?-1.0:(cos_value>1.0)?1.0:cos_value;
		return ((sin_value>=0)?(180.0/Math.PI):(-180.0/Math.PI))*Math.acos(cos_value);
	}	
	public double []caculate_translation_rotation(boolean cos_belta_flag)
	{
		double data[]=normalize().get_location_data();
		double mx=data[12],my=data[13],mz=data[14];
		double sin_belta=(data[2]<-1.0)?1.0:(data[2]>1.0)?-1.0:(-data[2]);
		
		if(Math.abs(sin_belta)<(1.0-const_value.min_value)){
			double cos_belta=Math.sqrt(1.0-sin_belta*sin_belta)*(cos_belta_flag?1.0:-1.0);
			double sin_alf	=data[6]/cos_belta,cos_alf	=data[10]/cos_belta;
			double sin_gamma=data[1]/cos_belta,cos_gamma=data[ 0]/cos_belta;
		
			double rx= caculate_angle(sin_alf,	cos_alf);
			double ry= caculate_angle(sin_belta,cos_belta);
			double rz= caculate_angle(sin_gamma,cos_gamma);

			return new double[] {mx,my,mz,rx,ry,rz,0};
		}else if(sin_belta>0){
			double cos_gamma_minus_alf=(data[5]+data[8])/2.0;
			double sin_gamma_minus_alf=(data[9]-data[4])/2.0;
			
			double rx= 0.0;
			double ry= 90.0;
			double rz= caculate_angle(sin_gamma_minus_alf,cos_gamma_minus_alf);
			 
			return new double[] {mx,my,mz,rx,ry,rz,1};
		}else{
			double cos_gamma_plus_alf=( data[5]-data[8])/2.0;
			double sin_gamma_plus_alf=(-data[4]-data[9])/2.0;
			
			double rx= 0.0;
			double ry=-90;
			double rz= caculate_angle(sin_gamma_plus_alf,cos_gamma_plus_alf);
			
			return new double[] {mx,my,mz,rx,ry,rz,-1};
		}
	}
	public location transpose()
	{
		location ret_val=new location();
		for(int i=0;i<4;i++)
			for(int j=0;j<4;j++)
				ret_val.a[j][i]=a[i][j];
		return ret_val;
	}
}
