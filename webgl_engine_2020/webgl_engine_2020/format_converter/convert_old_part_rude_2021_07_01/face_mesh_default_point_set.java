package convert_old_part_rude_2021_07_01;

import kernel_common_class.const_value;
import kernel_file_manager.file_reader;
import kernel_transformation.point;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_transformation.plane;

public class face_mesh_default_point_set
{
	public double vertex_location[];
	public String vertex_extra_data[];
	
	public void destroy()
	{
		vertex_location=null;
		vertex_extra_data=null;
	}

	public int point_number()
	{
		if(vertex_extra_data==null)
			return 0;
		else
			return vertex_extra_data.length;
	}
	public int touch_point(double x,double y,double z,String my_vertex_extra_data,boolean combine_flag)
	{
		int point_set_point_number;
		if((point_set_point_number=point_number())==0){
			vertex_location=new double[]{x,y,z};
			vertex_extra_data=new String[]{my_vertex_extra_data};
			return 0;
		}
		
		if(combine_flag){
			int min_index=0;
			double min_distance2=0;
			min_distance2+=(vertex_location[0]-x)*(vertex_location[0]-x);
			min_distance2+=(vertex_location[1]-y)*(vertex_location[1]-y);
			min_distance2+=(vertex_location[2]-z)*(vertex_location[2]-z);
			
			for(int i=1,j=3;i<point_set_point_number;i++,j+=3){
				double my_min_distance2=0;
				my_min_distance2+=(vertex_location[j+0]-x)*(vertex_location[j+0]-x);
				my_min_distance2+=(vertex_location[j+1]-y)*(vertex_location[j+1]-y);
				my_min_distance2+=(vertex_location[j+2]-z)*(vertex_location[j+2]-z);
				
				if(my_min_distance2<min_distance2){
					min_distance2=my_min_distance2;
					min_index=i;
				}
			}
			if(min_distance2<const_value.min_value2)
				if(vertex_extra_data[min_index].compareTo(my_vertex_extra_data)==0)
					return min_index;
		}
	
		if(vertex_extra_data.length<=point_set_point_number){
			double bak_vertex_location  []=vertex_location;
			String bak_vertex_extra_data[]=vertex_extra_data;
			
			vertex_extra_data=new String[vertex_extra_data.length+1000];
			vertex_location	 =new double[vertex_extra_data.length*3];
			
			for(int i=0,ni=bak_vertex_location.length;i<ni;i++)
				vertex_location[i]=bak_vertex_location[i];
			for(int i=bak_vertex_location.length,ni=vertex_location.length;i<ni;i++)
				vertex_location[i]=0.0;
			
			for(int i=0,ni=bak_vertex_extra_data.length;i<ni;i++)
				vertex_extra_data[i]=bak_vertex_extra_data[i];
			for(int i=bak_vertex_extra_data.length,ni=vertex_extra_data.length;i<ni;i++)
				vertex_extra_data[i]=null;
		}
		
		int pointer=point_set_point_number+point_set_point_number+point_set_point_number;
		vertex_location		[pointer++				 ]=x;
		vertex_location		[pointer++				 ]=y;
		vertex_location		[pointer++				 ]=z;
		vertex_extra_data	[point_set_point_number  ]=my_vertex_extra_data;

		return point_set_point_number;
	}
	
	public double[] get_vertex_location(int index_id)
	{
		int point_set_point_number;
		
		if((point_set_point_number=point_number())<=0)
			return new double[]{0.0,0.0,0.0};

		index_id=(index_id<0)?0:(index_id>=point_set_point_number)?(point_set_point_number-1):index_id;
		index_id=index_id+index_id+index_id;
		
		double x=vertex_location[index_id++];
		double y=vertex_location[index_id++];
		double z=vertex_location[index_id++];
		
		return new double[]{x,y,z};
	}
	
	public String get_vertex_extra_data(int index_id)
	{
		int point_set_point_number;
		if((point_set_point_number=point_number())<=0)
			return "1.0";
		index_id=(index_id<0)?0:(index_id>=point_set_point_number)?(point_set_point_number-1):index_id;
		return vertex_extra_data[index_id];
	}
	public void scale(double scale_value)
	{
		for(int i=0,ni=3*point_number();i<ni;i++)
			vertex_location[i]*=scale_value;
		return ;
	}
	
	public face_mesh_default_point_set creator_normal(int vertex[][])
	{
		point	normal_point[]=new point[point_number()];
		int		normal_point_number[]=new int[normal_point.length];
		
		for(int i=0,ni=normal_point_number.length;i<ni;i++){
			normal_point[i]=new point(0,0,0);
			normal_point_number[i]=0;
		}
		for(int i=0,ni=vertex.length;i<ni;i++)
			for(int j=0,nj=vertex[i].length;j<nj;j++){
				int id0=vertex[i][(j+0)%nj];
				int id1=vertex[i][(j+1)%nj];
				int id2=vertex[i][(j+2)%nj];
				
				double p[];
				
				p=get_vertex_location(id0);
				point p0=new point(p[0],p[1],p[2]);
				
				p=get_vertex_location(id1);
				point p1=new point(p[0],p[1],p[2]);
				
				p=get_vertex_location(id2);
				point p2=new point(p[0],p[1],p[2]);
				
				plane pl=new plane(p0,p1,p2);
				
				if(pl.error_flag)
					continue;
				
				point normal=new point(pl.A,pl.B,pl.C);
				
				normal_point[id1]=normal_point[id1].add(normal);
				normal_point_number[id1]++;
			}
		for(int i=0,ni=normal_point.length;i<ni;i++)
			if(normal_point_number[i]>0){
				if(normal_point[i].distance2()<=const_value.min_value2)
					normal_point[i]=new point(0,0,0);
				else
					normal_point[i]=normal_point[i].expand(1.0);
			}
		return new face_mesh_default_point_set(normal_point);
	}
	public face_mesh_default_point_set(file_reader f)
	{
		int point_set_point_number;
		if((point_set_point_number=f.get_int())<=0){
			vertex_location=new double[0];
			vertex_extra_data=new String[0];
		}else{
			vertex_location  =new double[3*point_set_point_number];
			vertex_extra_data=new String[point_set_point_number];
			for(int i=0,j=0,ni=point_set_point_number;i<ni;){
				vertex_location	 [j++]=f.get_double();
				vertex_location	 [j++]=f.get_double();
				vertex_location	 [j++]=f.get_double();
				vertex_extra_data[i++]=f.get_string();
			}
		}
	}
	public face_mesh_default_point_set(point p[])
	{
		vertex_location			=new double[p.length*3];
		vertex_extra_data		=new String[p.length];

		for(int i=0,j=0,point_set_point_number=p.length;i<point_set_point_number;i++){
			vertex_location[j++]=p[i].x;
			vertex_location[j++]=p[i].y;
			vertex_location[j++]=p[i].z;
			vertex_extra_data[i]="1";
		}
	}
	public face_mesh_default_point_set(location loca,box b,boolean vertices_normals_flag)
	{
		if(vertices_normals_flag){
			vertex_location=new double[8*3];
			vertex_extra_data=new String[]{"1","1","1","1","1","1","1","1"};
			for(int i=0,m=0;i<2;i++)
				for(int j=0;j<2;j++)
					for(int k=0;k<2;k++){
						point p=loca.multiply(new point(b.p[i].x,b.p[j].y,b.p[k].z));
						vertex_location[m++]=p.x;
						vertex_location[m++]=p.y;
						vertex_location[m++]=p.z;
					}
		}else{
			vertex_location	 =new double[6*3];
			vertex_extra_data=new String[]{"1","1","1","1","1","1"};
			
			point p,p0,px,py;
			int i=0;
			
			//right:0
			p0=loca.multiply(new point(b.p[1].x,b.p[1].y,b.p[1].z));
			px=loca.multiply(new point(b.p[1].x,b.p[0].y,b.p[1].z)).sub(p0);
			py=loca.multiply(new point(b.p[1].x,b.p[1].y,b.p[0].z)).sub(p0);
			if(((p=px.cross(py)).distance2())<const_value.min_value2)
				p=new point(1,0,0);
			else
				p=p.expand(1.0);
			vertex_location[i++]=p.x;		
			vertex_location[i++]=p.y;
			vertex_location[i++]=p.z;
			
			//left:1
			p0=loca.multiply(new point(b.p[0].x,b.p[0].y,b.p[0].z));
			px=loca.multiply(new point(b.p[0].x,b.p[0].y,b.p[1].z)).sub(p0);
			py=loca.multiply(new point(b.p[0].x,b.p[1].y,b.p[0].z)).sub(p0);
			if(((p=px.cross(py)).distance2())<const_value.min_value2)
				p=new point(-1,0,0);
			else
				p=p.expand(1.0);
			vertex_location[i++]=p.x;		
			vertex_location[i++]=p.y;
			vertex_location[i++]=p.z;
			
			
			//up:2
			p0=loca.multiply(new point(b.p[1].x,b.p[1].y,b.p[0].z));
			px=loca.multiply(new point(b.p[0].x,b.p[1].y,b.p[0].z)).sub(p0);
			py=loca.multiply(new point(b.p[1].x,b.p[1].y,b.p[1].z)).sub(p0);
			if(((p=px.cross(py)).distance2())<const_value.min_value2)
				p=new point(0,1,0);
			else
				p=p.expand(1.0);
			vertex_location[i++]=p.x;		
			vertex_location[i++]=p.y;
			vertex_location[i++]=p.z;
			
			
			//down:3
			p0=loca.multiply(new point(b.p[0].x,b.p[0].y,b.p[1].z));
			px=loca.multiply(new point(b.p[0].x,b.p[0].y,b.p[0].z)).sub(p0);
			py=loca.multiply(new point(b.p[1].x,b.p[0].y,b.p[1].z)).sub(p0);
			if(((p=px.cross(py)).distance2())<const_value.min_value2)
				p=new point(0,-1,0);
			else
				p=p.expand(1.0);
			vertex_location[i++]=p.x;		
			vertex_location[i++]=p.y;
			vertex_location[i++]=p.z;
			
			//front:4
			p0=loca.multiply(new point(b.p[0].x,b.p[1].y,b.p[1].z));
			px=loca.multiply(new point(b.p[0].x,b.p[0].y,b.p[1].z)).sub(p0);
			py=loca.multiply(new point(b.p[1].x,b.p[1].y,b.p[1].z)).sub(p0);
			if(((p=px.cross(py)).distance2())<const_value.min_value2)
				p=new point(0,0,1);
			else
				p=p.expand(1.0);
			vertex_location[i++]=p.x;		
			vertex_location[i++]=p.y;
			vertex_location[i++]=p.z;
			
			//back:5
			p0=loca.multiply(new point(b.p[1].x,b.p[0].y,b.p[0].z));
			px=loca.multiply(new point(b.p[0].x,b.p[0].y,b.p[0].z)).sub(p0);
			py=loca.multiply(new point(b.p[1].x,b.p[1].y,b.p[0].z)).sub(p0);
			if(((p=px.cross(py)).distance2())<const_value.min_value2)
				p=new point(0,0,-1);
			else
				p=p.expand(1.0);
			vertex_location[i++]=p.x;		
			vertex_location[i++]=p.y;
			vertex_location[i++]=p.z;
		}
	}
	public face_mesh_default_point_set(face_mesh_default_point_set s)
	{
		vertex_location=new double[s.vertex_location.length];
		for(int i=0,ni=vertex_location.length;i<ni;i++)
			vertex_location[i]=s.vertex_location[i];
		
		vertex_extra_data=new String[s.vertex_extra_data.length];
		for(int i=0,ni=vertex_extra_data.length;i<ni;i++)
			vertex_extra_data[i]=s.vertex_extra_data[i];
	}
	public face_mesh_default_point_set(double x,double y,double z,String w)
	{
		vertex_location		=new double[]{x,y,z};
		vertex_extra_data	=new String[]{w};
	}
	public face_mesh_default_point_set()
	{
		vertex_location=new double[0];
		vertex_extra_data=new String[0];
	}
}