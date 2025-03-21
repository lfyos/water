package format_convert;

import java.io.File;

import kernel_common_class.const_value;
import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_transformation.point;
import kernel_transformation.plane;
import kernel_transformation.box;

class vertex
{
	static private long system_version=100;
	
	public double x,y,z;
	public long type_id;
	public long version;
	public int sequence;

	public vertex(double my_x,double my_y,double my_z,long my_type_id)
	{
		x=my_x;
		y=my_y;
		z=my_z;
		type_id=my_type_id;

		version=system_version++;
		sequence=-1;
	}
}

class vertex_container
{
	public double min_x,min_y,min_z,max_x,max_y,max_z;
	public double real_min_x,real_min_y,real_min_z,real_max_x,real_max_y,real_max_z;
	
	public vertex vertex_array[];
	public int vertex_number;
	
	public vertex_container()
	{
		min_x=0;
		min_y=0;
		min_z=0;
		max_x=1;
		max_y=1;
		max_z=1;
		
		real_min_x=0;
		real_min_y=0;
		real_min_z=0;
		real_max_x=1;
		real_max_y=1;
		real_max_z=1;
		
		vertex_number=4;
		vertex_array=new vertex[10000];

		double sequare_length=2.0;
		vertex_array[0]=new vertex(-sequare_length,0,-sequare_length,-1);
		vertex_array[1]=new vertex(-sequare_length,0, sequare_length,-1);
		vertex_array[2]=new vertex( sequare_length,0,-sequare_length,-1);
		vertex_array[3]=new vertex( sequare_length,0, sequare_length,-1);
	}
	public int add_vertex(double x,double y,double z,long type_id)
	{
		if(vertex_array.length<=vertex_number){
			vertex bak[]=vertex_array;
			vertex_array=new vertex[vertex_number+10000];
			for(int i=0;i<vertex_number;i++)
				vertex_array[i]=bak[i];
		}
		vertex_array[vertex_number]=new vertex(x,y,z,type_id);
		return vertex_number++;
	}
	
	public int[] add_vertex(int type_id,String file_name,String file_system_charset,double scale[])
	{
		debug_information.println("Begin add_vertex:	",file_name);

		int start_vertex_id=vertex_number;
		
		for(File  f=new File(file_name);;) {
			if(!(f.exists())){
				debug_information.println("Not exist in add_vertex:		",file_name);
				debug_information.println();
				break;
			}
			if(!(f.isFile())){
				debug_information.println("Not regular file in add_vertex:		",file_name);
				debug_information.println();
				break;
			}
			for(file_reader fr=new file_reader(file_name,file_system_charset);;){
				double z=fr.get_double(),x=fr.get_double();
				if(fr.eof()){
					fr.close();
					break;
				}
				add_vertex(scale[0]*x,scale[1]*(fr.get_double()),scale[2]*z,type_id);
			}
			break;
		}
		debug_information.println("End add_vertex:		",vertex_number);
		debug_information.println();

		return new int[]{start_vertex_id,vertex_number-1};
	}
	public void normalize_vertex()
	{
		debug_information.println("Begin normalize_vertex");
		
		min_x=vertex_array[4].x;max_x=min_x;
		min_y=vertex_array[4].y;max_y=min_y;
		min_z=vertex_array[4].z;max_z=min_z;
		
		for(int i=5;i<vertex_number;i++){
			min_x=(min_x<=vertex_array[i].x)?min_x:vertex_array[i].x;
			min_y=(min_y<=vertex_array[i].y)?min_y:vertex_array[i].y;
			min_z=(min_z<=vertex_array[i].z)?min_z:vertex_array[i].z;
			
			max_x=(max_x>=vertex_array[i].x)?max_x:vertex_array[i].x;
			max_y=(max_y>=vertex_array[i].y)?max_y:vertex_array[i].y;
			max_z=(max_z>=vertex_array[i].z)?max_z:vertex_array[i].z;
		}
		max_x=((max_x-min_x)<const_value.min_value)?(min_x+1.0):max_x;
		max_y=((max_y-min_y)<const_value.min_value)?(min_y+1.0):max_y;
		max_z=((max_z-min_z)<const_value.min_value)?(min_z+1.0):max_z;
		
		
		real_min_x=min_x;
		real_min_y=min_y;
		real_min_z=min_z;
		real_max_x=max_x;
		real_max_y=max_y;
		real_max_z=max_z;		
		
		double center_x=(max_x+min_x)/2.0,length_x=(max_x-min_x)/2.0;
		double center_z=(max_z+min_z)/2.0,length_z=(max_z-min_z)/2.0;
		
		if(length_x>length_z) {
			length_z=length_x;
			min_z=center_z-length_z;
			max_z=center_z+length_z;
		}else{
			length_x=length_z;
			min_x=center_x-length_x;
			max_x=center_x+length_x;
		}
		
		for(int i=4;i<vertex_number;i++){
			vertex_array[i].x=(vertex_array[i].x-center_x)/length_x;
			vertex_array[i].z=(vertex_array[i].z-center_z)/length_z;
		}
		
		debug_information.println("End normalize_vertex");
		debug_information.println();
	}
}

class vertex_sequence
{
	public int search_triangle_id,center_vertex_id;
	
	public int		ll_vertex_index[],	lr_vertex_index[],	rl_vertex_index[],	rr_vertex_index[];
	public int		ll_vertex_number,	lr_vertex_number,	rl_vertex_number,	rr_vertex_number;
	
	public vertex_sequence next;
	
	public vertex_sequence(int my_search_triangle_id,vertex	vertex_array[],int vertex_index[],int vertex_number)
	{
		search_triangle_id=my_search_triangle_id;
		
		next=null;
		
		ll_vertex_index=new int[vertex_number];
		lr_vertex_index=new int[vertex_number];
		rl_vertex_index=new int[vertex_number];
		rr_vertex_index=new int[vertex_number];
		
		ll_vertex_number=0;
		lr_vertex_number=0;
		rl_vertex_number=0;
		rr_vertex_number=0;
		
		double min_x=vertex_array[vertex_index[0]].x,max_x=min_x;
		double min_z=vertex_array[vertex_index[0]].z,max_z=min_z;
		
		for(int i=1;i<vertex_number;i++){
			double x=vertex_array[vertex_index[i]].x;
			double z=vertex_array[vertex_index[i]].z;
			
			min_x=(x<min_x)?x:min_x;
			max_x=(x>max_x)?x:max_x;
			min_z=(z<min_z)?z:min_z;
			max_z=(z>max_z)?z:max_z;
		}
		double center_x=(min_x+max_x)/2.0;
		double center_z=(min_z+max_z)/2.0;
		
		center_vertex_id=0;
		double dx=vertex_array[vertex_index[center_vertex_id]].x-center_x;
		double dz=vertex_array[vertex_index[center_vertex_id]].z-center_z;
		double distance2=dx*dx+dz*dz;
		
		for(int i=1;i<vertex_number;i++){
			dx=vertex_array[vertex_index[i]].x-center_x;
			dz=vertex_array[vertex_index[i]].z-center_z;
			double new_distance2=dx*dx+dz*dz;
			if(new_distance2<distance2){
				center_vertex_id=i;
				distance2=new_distance2;
			}
		}
		center_x=vertex_array[vertex_index[center_vertex_id]].x;
		center_z=vertex_array[vertex_index[center_vertex_id]].z;
		
		for(int i=0;i<vertex_number;i++){
			if(i!=center_vertex_id){
				int vertex_sequence_type_id=0;
				vertex_sequence_type_id+=(vertex_array[vertex_index[i]].x<center_x)?0:1;
				vertex_sequence_type_id+=(vertex_array[vertex_index[i]].z<center_z)?0:2;
				switch(vertex_sequence_type_id){
				case 0:
					ll_vertex_index[ll_vertex_number++]=vertex_index[i];
					break;
				case 1:
					lr_vertex_index[lr_vertex_number++]=vertex_index[i];
					break;
				case 2:
					rl_vertex_index[rl_vertex_number++]=vertex_index[i];
					break;
				case 3:
					rr_vertex_index[rr_vertex_number++]=vertex_index[i];
					break;
				}
			}
		}
		center_vertex_id=vertex_index[center_vertex_id];
	}
}

class triangle 
{
	public int vertex_a,vertex_b,vertex_c;				//basic triangle data
	
	public int triangle_ab,triangle_bc,triangle_ca;		//neighbor triangles,caculate from  vertex_a,vertex_b,vertex_c by set_triangle_id
	
	public long version;								//points and planes are caculate by test_version
	public plane plane_ab,	plane_bc,	plane_ca;
	
	public double test_ab_value,test_bc_value,test_ca_value;//test result of test_vertex_in_triangle
	
	public long state;
	
	public triangle(int my_vertex_a,int my_vertex_b,int my_vertex_c)
	{
		vertex_a=my_vertex_a;
		vertex_b=my_vertex_b;
		vertex_c=my_vertex_c;
		
		triangle_ab=-1;
		triangle_bc=-1;
		triangle_ca=-1;
		
		version=0;
		
		plane_ab=null;
		plane_bc=null;
		plane_ca=null;
		
		test_ab_value=0;
		test_bc_value=0;
		test_ca_value=0;
		
		state=0;
	}
	
	public void turn(boolean direction_flag)
	{
		int bak_int;
		plane bak_plane;
		
		if(direction_flag){
			bak_int		=vertex_a;		vertex_a	=vertex_b;		vertex_b	=vertex_c;		vertex_c	=bak_int;
			bak_int		=triangle_ab;	triangle_ab	=triangle_bc;	triangle_bc	=triangle_ca;	triangle_ca	=bak_int;
			bak_plane	=plane_ab;		plane_ab	=plane_bc;		plane_bc	=plane_ca;		plane_ca	=bak_plane;
		}else{
			bak_int		=vertex_a;		vertex_a	=vertex_c;		vertex_c	=vertex_b;		vertex_b	=bak_int;
			bak_int		=triangle_ab;	triangle_ab	=triangle_ca;	triangle_ca	=triangle_bc;	triangle_bc	=bak_int;
			bak_plane	=plane_ab;		plane_ab	=plane_ca;		plane_ca	=plane_bc;		plane_bc	=bak_plane;
		}
	}
	
	static public void set_triangle_id(
		int triangle_id_array[],int triangle_id_number,
		triangle triangle_array[],int triangle_number)
	{
		int s_id,t_id;
		triangle s,t;
		
		for(int i=0;i<triangle_id_number;i++) {
			if((s_id=triangle_id_array[i])<0)
				continue;
			if(s_id>=triangle_number)
				continue;
			if((s=triangle_array[s_id])==null)
				continue;
			for(int j=0;j<triangle_id_number;j++) {
				if((t_id=triangle_id_array[j])<0)
					continue;
				if(t_id>=triangle_number)
					continue;
				if((t=triangle_array[t_id])==null)
					continue;

				if(s_id==t_id)
					continue;

					 if((s.vertex_a==t.vertex_b)&&(s.vertex_b==t.vertex_a))
						 	s.triangle_ab=t_id;
				else if((s.vertex_a==t.vertex_c)&&(s.vertex_b==t.vertex_b))
							s.triangle_ab=t_id;
				else if((s.vertex_a==t.vertex_a)&&(s.vertex_b==t.vertex_c))
							s.triangle_ab=t_id;
				
				     if((s.vertex_b==t.vertex_b)&&(s.vertex_c==t.vertex_a))
				    	 	s.triangle_bc=t_id;
				else if((s.vertex_b==t.vertex_c)&&(s.vertex_c==t.vertex_b))
							s.triangle_bc=t_id;
				else if((s.vertex_b==t.vertex_a)&&(s.vertex_c==t.vertex_c))
							s.triangle_bc=t_id;
				
				     if((s.vertex_c==t.vertex_b)&&(s.vertex_a==t.vertex_a))
				    	 	s.triangle_ca=t_id;
				else if((s.vertex_c==t.vertex_c)&&(s.vertex_a==t.vertex_b))
							s.triangle_ca=t_id;
				else if((s.vertex_c==t.vertex_a)&&(s.vertex_a==t.vertex_c))
							s.triangle_ca=t_id;
			}
		}
	}

	public int test_vertex_in_triangle(vertex vertex_array[],double vertex_x,double vertex_z)
	{
		long version_a=vertex_array[vertex_a].version;
		long version_b=vertex_array[vertex_b].version;
		long version_c=vertex_array[vertex_c].version;
		
		if((version_a>version)||(version_b>version)||(version_c>version)){
			version=(version_a>version_b)?version_a:version_b;
			version=(version  >version_c)?version:version_c;
			
			point point_a=new point(vertex_array[vertex_a].x,0,vertex_array[vertex_a].z);
			point point_b=new point(vertex_array[vertex_b].x,0,vertex_array[vertex_b].z);
			point point_c=new point(vertex_array[vertex_c].x,0,vertex_array[vertex_c].z);
		
			point dir	=new point(0,1,0);
			plane_ab	=new plane(point_a,point_b,point_b.add(dir));
			plane_bc	=new plane(point_b,point_c,point_c.add(dir));
			plane_ca	=new plane(point_c,point_a,point_a.add(dir));
		}
		
		test_ab_value=plane_ab.test(vertex_x,0,vertex_z);
		test_bc_value=plane_bc.test(vertex_x,0,vertex_z);
		test_ca_value=plane_ca.test(vertex_x,0,vertex_z);
		
		if((test_ab_value>=test_bc_value)&&(test_ab_value>=test_ca_value)) {		//test_ab_value is max,outside ab edge
			if(test_ab_value>const_value.min_value)
				return 1;
		}else if((test_bc_value>=test_ab_value)&&(test_bc_value>=test_ca_value)) {	//test_bc_value is max,outside bc edge
			if(test_bc_value>const_value.min_value)
				return 2;
		}else{																		//test_ca_value is max,,outside ca edge
			if(test_ca_value>const_value.min_value)
				return 3;
		}
		
		if(	  (test_ab_value<(-const_value.min_value))
			&&(test_bc_value<(-const_value.min_value))								//all less than ZERO,inside triangle
			&&(test_ca_value<(-const_value.min_value)))
				return 0;
		
		return -1;																	//on one of the edge,even on one of the vertex
	}
	
	public int test_vertex_in_triangle(vertex vertex_array[],int vertex_id)
	{
		return test_vertex_in_triangle(vertex_array,vertex_array[vertex_id].x,vertex_array[vertex_id].z);
	}
}

class triangle_container
{
	public triangle triangle_array[];
	public int triangle_number;
	
	private int add_triangle(triangle t)
	{
		if(triangle_array.length<=triangle_number){
			triangle bak[]=triangle_array;
			triangle_array=new triangle[triangle_number+10000];
			for(int i=0;i<triangle_number;i++)
				triangle_array[i]=bak[i];
		}
		triangle_array[triangle_number]=t;
		return triangle_number++;
	}
	private void equal_modify_triangle(vertex_container vertex_cont,
			int equal_modify_triangle_id_number,int equal_modify_triangle_id_array[])
	{
		class id_link_list
		{
			public int id;
			public id_link_list next;
			public id_link_list(int my_id,id_link_list my_next)
			{
				id=my_id;
				next=my_next;
			}
		};
		
		id_link_list first=null;
		for(int i=equal_modify_triangle_id_number-1;i>=0;i--)
			first=new id_link_list(equal_modify_triangle_id_array[i],first);
		
		for(triangle my_triangle;first!=null;){
			int triangle_id=first.id;
			first=first.next;
			
			if((triangle_id<0)||(triangle_id>=triangle_number))
				continue;
			if((my_triangle=triangle_array[triangle_id])==null)
				continue;
			
			for(int i=0;i<3;i++,my_triangle.turn(true)){
				if(my_triangle.triangle_ab<0)		
					continue;

				triangle neighbor_triangle=triangle_array[my_triangle.triangle_ab];
				if(neighbor_triangle.triangle_bc==triangle_id)
					neighbor_triangle.turn(true);
				else if(neighbor_triangle.triangle_ca==triangle_id)
					neighbor_triangle.turn(false);

				my_triangle.test_vertex_in_triangle(
					vertex_cont.vertex_array,neighbor_triangle.vertex_c);

				if(my_triangle.test_bc_value>=(-const_value.min_value))				
					continue;

				if(my_triangle.test_ca_value>=(-const_value.min_value))				
					continue;
				
				vertex va=vertex_cont.vertex_array[neighbor_triangle.vertex_a];
				vertex vb=vertex_cont.vertex_array[neighbor_triangle.vertex_b];
				vertex vc=vertex_cont.vertex_array[neighbor_triangle.vertex_c];
				vertex vd=vertex_cont.vertex_array[my_triangle.vertex_c];
				double distance2_cd_zx=(vd.x-vc.x)*(vd.x-vc.x)+(vd.z-vc.z)*(vd.z-vc.z);
				double distance2_ab_zx=(vb.x-va.x)*(vb.x-va.x)+(vb.z-va.z)*(vb.z-va.z);
				if(distance2_ab_zx<(distance2_cd_zx+const_value.min_value2))
					continue;
				
				triangle_array[triangle_id			  ]=new triangle(
					neighbor_triangle.vertex_c,my_triangle.vertex_b,my_triangle.vertex_c);
				triangle_array[my_triangle.triangle_ab]=new triangle(
					neighbor_triangle.vertex_c,my_triangle.vertex_c,my_triangle.vertex_a);
				
				triangle.set_triangle_id(new int[]
				{
					triangle_id	,					my_triangle.triangle_ab,
					my_triangle.triangle_bc,		my_triangle.triangle_ca,
					neighbor_triangle.triangle_bc,	neighbor_triangle.triangle_ca,
				},6,triangle_array,triangle_number);
				
				first=new id_link_list(triangle_id,new id_link_list(my_triangle.triangle_ab,first));
				
				break;
			}
		}
	}

	private int[] split_edge(boolean do_equal_modify_triangle_flag,
			vertex_container vertex_cont,int triangle_id,int vertex_id)
	{
		triangle my_triangle=triangle_array[triangle_id];

		double x=vertex_cont.vertex_array[vertex_id].x;
		double z=vertex_cont.vertex_array[vertex_id].z;
		double dx=vertex_cont.vertex_array[my_triangle.vertex_a].x-x;
		double dz=vertex_cont.vertex_array[my_triangle.vertex_a].z-z;
		
		if((dx*dx+dz*dz)<const_value.min_value2) {
			vertex_cont.vertex_array[my_triangle.vertex_a].type_id|=vertex_cont.vertex_array[vertex_id			 ].type_id;
			vertex_cont.vertex_array[vertex_id			 ].type_id|=vertex_cont.vertex_array[my_triangle.vertex_a].type_id;
			return new int[] {triangle_id};
		}
		dx=vertex_cont.vertex_array[my_triangle.vertex_b].x-x;
		dz=vertex_cont.vertex_array[my_triangle.vertex_b].z-z;
		if((dx*dx+dz*dz)<const_value.min_value2){
			vertex_cont.vertex_array[my_triangle.vertex_b].type_id|=vertex_cont.vertex_array[vertex_id			 ].type_id;
			vertex_cont.vertex_array[vertex_id			 ].type_id|=vertex_cont.vertex_array[my_triangle.vertex_b].type_id;
			return new int[] {triangle_id};
		}
		dx=vertex_cont.vertex_array[my_triangle.vertex_c].x-x;
		dz=vertex_cont.vertex_array[my_triangle.vertex_c].z-z;
		if((dx*dx+dz*dz)<const_value.min_value2) {
			vertex_cont.vertex_array[my_triangle.vertex_c].type_id|=vertex_cont.vertex_array[vertex_id			 ].type_id;
			vertex_cont.vertex_array[vertex_id			 ].type_id|=vertex_cont.vertex_array[my_triangle.vertex_c].type_id;
			return new int[] {triangle_id};
		}
		
		double abs_test_ab_value=Math.abs(my_triangle.test_ab_value);
		double abs_test_bc_value=Math.abs(my_triangle.test_bc_value);
		double abs_test_ca_value=Math.abs(my_triangle.test_ca_value);
		
		if((abs_test_bc_value<=abs_test_ab_value)&&(abs_test_bc_value<=abs_test_ca_value))
			my_triangle.turn(true);
		else if((abs_test_ca_value<=abs_test_ab_value)&&(abs_test_ca_value<=abs_test_bc_value))
			my_triangle.turn(false);
		
		int create_triangle_number=0;
		triangle create_triangle_array[]=new triangle[4];
		
		int need_set_triangle_id_number=0;
		int need_set_triangle_id_array[]=new int[8];

		if(my_triangle.triangle_ab>=0){
			triangle neighbor_triangle=triangle_array[my_triangle.triangle_ab];
			if(neighbor_triangle.triangle_bc==triangle_id)
				neighbor_triangle.turn(true);
			else if(neighbor_triangle.triangle_ca==triangle_id)
				neighbor_triangle.turn(false);
			
			need_set_triangle_id_array[need_set_triangle_id_number++]=neighbor_triangle.triangle_bc;
			need_set_triangle_id_array[need_set_triangle_id_number++]=neighbor_triangle.triangle_ca;
			create_triangle_array[create_triangle_number++]=new triangle(vertex_id,neighbor_triangle.vertex_b,neighbor_triangle.vertex_c);
			create_triangle_array[create_triangle_number++]=new triangle(vertex_id,neighbor_triangle.vertex_c,neighbor_triangle.vertex_a);
		}

		need_set_triangle_id_array[need_set_triangle_id_number++]=my_triangle.triangle_bc;
		need_set_triangle_id_array[need_set_triangle_id_number++]=my_triangle.triangle_ca;
		create_triangle_array[create_triangle_number++]=new triangle(vertex_id,my_triangle.vertex_b,my_triangle.vertex_c);
		create_triangle_array[create_triangle_number++]=new triangle(vertex_id,my_triangle.vertex_c,my_triangle.vertex_a);
		
		triangle_array[triangle_id]=create_triangle_array[--create_triangle_number];
		need_set_triangle_id_array[need_set_triangle_id_number++]=triangle_id;
		
		if(my_triangle.triangle_ab>=0){
			triangle_array[my_triangle.triangle_ab]=create_triangle_array[--create_triangle_number];
			need_set_triangle_id_array[need_set_triangle_id_number++]=my_triangle.triangle_ab;
		}
		for(int i=0;i<create_triangle_number;i++)
			need_set_triangle_id_array[need_set_triangle_id_number++]=add_triangle(create_triangle_array[i]);
		
		triangle.set_triangle_id(
			need_set_triangle_id_array,need_set_triangle_id_number,triangle_array,triangle_number);
		
		if(do_equal_modify_triangle_flag)
			equal_modify_triangle(vertex_cont,need_set_triangle_id_number,need_set_triangle_id_array);
		
		return need_set_triangle_id_array;
	}
	
	private int [] split_triangle(boolean do_equal_modify_triangle_flag,
			int triangle_id,int vertex_id,vertex_container vertex_cont)
	{
		triangle my_triangle=triangle_array[triangle_id];
		
		int need_set_triangle_id_number=0,need_set_triangle_id_array[]=new int[6];
		
		need_set_triangle_id_array[need_set_triangle_id_number++]=my_triangle.triangle_ab;
		need_set_triangle_id_array[need_set_triangle_id_number++]=my_triangle.triangle_bc;
		need_set_triangle_id_array[need_set_triangle_id_number++]=my_triangle.triangle_ca;
		
		triangle_array[triangle_id]=new triangle(my_triangle.vertex_a,my_triangle.vertex_b,vertex_id);
		need_set_triangle_id_array[need_set_triangle_id_number++]=triangle_id;
		need_set_triangle_id_array[need_set_triangle_id_number++]=add_triangle(
				new triangle(my_triangle.vertex_b,my_triangle.vertex_c,vertex_id));
		need_set_triangle_id_array[need_set_triangle_id_number++]=add_triangle(
				new triangle(my_triangle.vertex_c,my_triangle.vertex_a,vertex_id));
		
		triangle.set_triangle_id(need_set_triangle_id_array,need_set_triangle_id_number,triangle_array,triangle_number);
		
		if(do_equal_modify_triangle_flag)
			equal_modify_triangle(vertex_cont,need_set_triangle_id_number,need_set_triangle_id_array);
		
		return need_set_triangle_id_array;
	}

	public int[] process_vertex_sequence(
			boolean do_equal_modify_triangle_flag,vertex_container vertex_cont,vertex_sequence vs)
	{
		int ret_val[]=null,process_number;
		vertex_sequence first=vs,last=vs;
		last.next=null;

		for(process_number=0;(first!=null);first=first.next,process_number++){
			if((process_number%100000)==0)
				if(do_equal_modify_triangle_flag)
					debug_information.println("split triangle:",process_number);
			int this_search_triangle_id=first.search_triangle_id,child_search_triangle_id=-1;
			for(int j=0,nj=2*triangle_number;(this_search_triangle_id>=0)&&(j<nj);j++){
				if(j>=triangle_number)
					this_search_triangle_id=j-triangle_number;
				
				switch(triangle_array[this_search_triangle_id].test_vertex_in_triangle(
						vertex_cont.vertex_array,first.center_vertex_id)){
				case 0:
					ret_val=split_triangle(do_equal_modify_triangle_flag,
							this_search_triangle_id,first.center_vertex_id,vertex_cont);
					child_search_triangle_id=this_search_triangle_id;
					this_search_triangle_id=-1;
					break;
				case 1:
					this_search_triangle_id=triangle_array[this_search_triangle_id].triangle_ab;
					break;
				case 2:
					this_search_triangle_id=triangle_array[this_search_triangle_id].triangle_bc;
					break;
				case 3:
					this_search_triangle_id=triangle_array[this_search_triangle_id].triangle_ca;
					break;
				default:
					ret_val=split_edge(do_equal_modify_triangle_flag,
							vertex_cont,this_search_triangle_id,first.center_vertex_id);
					child_search_triangle_id=this_search_triangle_id;
					this_search_triangle_id=-1;
					break;
				}
			}
			if(child_search_triangle_id>=0){
				if(first.ll_vertex_number>0){
					last.next=new vertex_sequence(child_search_triangle_id,
							vertex_cont.vertex_array,first.ll_vertex_index,first.ll_vertex_number);
					last=last.next;
				}
				if(first.lr_vertex_number>0){
					last.next=new vertex_sequence(child_search_triangle_id,
							vertex_cont.vertex_array,first.lr_vertex_index,first.lr_vertex_number);
					last=last.next;
				}	
				if(first.rr_vertex_number>0){
					last.next=new vertex_sequence(child_search_triangle_id,
							vertex_cont.vertex_array,first.rr_vertex_index,first.rr_vertex_number);
					last=last.next;
				}
				if(first.rl_vertex_number>0){
					last.next=new vertex_sequence(child_search_triangle_id,
							vertex_cont.vertex_array,first.rl_vertex_index,first.rl_vertex_number);
					last=last.next;
				}
			}
		}
		
		if(do_equal_modify_triangle_flag)
			debug_information.println("Split triangle:",process_number);
		
		return ret_val;
	}

	public triangle_container(vertex_container vertex_cont)
	{
		debug_information.println("Begin create triagles");
		
		triangle_number=2;
		triangle_array=new triangle[10000];
		triangle_array[0]=new triangle(0,1,2);
		triangle_array[1]=new triangle(3,2,1);
		triangle.set_triangle_id(new int[]{0,1},2,triangle_array,triangle_number);

		int vertex_number=0,vertex_index[]=new int[vertex_cont.vertex_number];
		for(int i=0,ni=vertex_cont.vertex_number;i<ni;i++)
			if(vertex_cont.vertex_array[i].type_id>=0)
				vertex_index[vertex_number++]=i;

		process_vertex_sequence(true,vertex_cont,
			new vertex_sequence(0,vertex_cont.vertex_array,vertex_index,vertex_number));
		
		debug_information.println("End create triagles");
		debug_information.println();
	}
}

class caculate_height
{
	public caculate_height(vertex_container vertex_cont,triangle_container triangle_cont)
	{
		vertex_cont.vertex_array[0].y=vertex_cont.min_y;
		vertex_cont.vertex_array[1].y=vertex_cont.min_y;
		vertex_cont.vertex_array[2].y=vertex_cont.min_y;
		vertex_cont.vertex_array[3].y=vertex_cont.min_y;

		boolean effective_vertex_flag[]=new boolean[vertex_cont.vertex_number];
		for(int i=0,ni=vertex_cont.vertex_number;i<ni;i++)
			effective_vertex_flag[i]=(vertex_cont.vertex_array[i].type_id==0)?true:false;

		for(boolean continue_flag=true;continue_flag;){
			double height_value	[]=new double[vertex_cont.vertex_number];
			int height_number	[]=new int[vertex_cont.vertex_number];
			int triangle_number	[]=new int[vertex_cont.vertex_number];

			for(int i=0,ni=vertex_cont.vertex_number;i<ni;i++){
				height_value[i]		=0.0;
				height_number[i]	=0;
				triangle_number[i]	=0;
			}

			for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++) {
				triangle t=triangle_cont.triangle_array[i];
				if(t==null)
					continue;
				triangle_number[t.vertex_a]++;
				triangle_number[t.vertex_b]++;
				triangle_number[t.vertex_c]++;

				if(effective_vertex_flag[t.vertex_a])
					if(!(effective_vertex_flag[t.vertex_b])){
							height_value [t.vertex_b]+=vertex_cont.vertex_array[t.vertex_a].y;
							height_number[t.vertex_b]++;
						}
				if(effective_vertex_flag[t.vertex_b])
					if(!(effective_vertex_flag[t.vertex_c])){
							height_value [t.vertex_c]+=vertex_cont.vertex_array[t.vertex_b].y;
							height_number[t.vertex_c]++;
						}
				if(effective_vertex_flag[t.vertex_c])
					if(!(effective_vertex_flag[t.vertex_a])){
							height_value [t.vertex_a]+=vertex_cont.vertex_array[t.vertex_c].y;
							height_number[t.vertex_a]++;
						}
			}
			
			for(int i=0,ni=vertex_cont.vertex_number;i<ni;i++)
				if(height_number[i]>0){
					vertex_cont.vertex_array[i].y=height_value[i]/height_number[i];
					vertex_cont.vertex_array[i].version++;
					effective_vertex_flag[i]=true;
				}
			
			continue_flag=false;
			for(int i=0,ni=vertex_cont.vertex_number;i<ni;i++)
				if(triangle_number[i]>0)
					if(!(effective_vertex_flag[i])) {
						continue_flag=true;
						break;
					}
		}
	}
}

class separate_triangle
{
	private int triangle_id;
	private int vertex_number,vertex_array[];
	
	private boolean caculate_vertex_array(long separate_type_id,
			vertex_container vertex_cont,triangle_container triangle_cont)
	{
		triangle_id=-1;
		vertex_number=1;
		vertex_array=new int[vertex_cont.vertex_number+2];
		
		for(int i=0,ni=vertex_cont.vertex_number;i<ni;i++)
			if(vertex_cont.vertex_array[i].type_id>0)
				if((vertex_cont.vertex_array[i].type_id&separate_type_id)!=0)
					vertex_array[vertex_number++]=i;
		if(vertex_number<=1)
			return true;
		
		double x=vertex_cont.vertex_array[vertex_array[1]].x;
		double z=vertex_cont.vertex_array[vertex_array[1]].z;
		double bounder_vertex_distance2=0;
		
		for(int i=0;i<4;i++) {
			double dx=vertex_cont.vertex_array[i].x-x;
			double dz=vertex_cont.vertex_array[i].z-z;
			double my_distance2=dx*dx+dz*dz;
			if((i==0)||(my_distance2<bounder_vertex_distance2)){
				vertex_array[0]=i;
				bounder_vertex_distance2=my_distance2;
			}
		}
		
		x=vertex_cont.vertex_array[vertex_array[vertex_number-1]].x;
		z=vertex_cont.vertex_array[vertex_array[vertex_number-1]].z;
		bounder_vertex_distance2=0;
		for(int i=0;i<4;i++){
			double dx=vertex_cont.vertex_array[i].x-x;
			double dz=vertex_cont.vertex_array[i].z-z;
			double my_distance2=dx*dx+dz*dz;
			if((i==0)||(my_distance2<bounder_vertex_distance2)){
				vertex_array[vertex_number]=i;
				bounder_vertex_distance2=my_distance2;
			}
		}
		vertex_number++;
		
		triangle t;
		for(triangle_id=0;triangle_id<triangle_cont.triangle_number;triangle_id++)
			if((t=triangle_cont.triangle_array[triangle_id])!=null) {
				if(t.vertex_a==vertex_array[0])
					return false;
				if(t.vertex_b==vertex_array[0]) {
					t.turn(true);
					return false;
				}
				if(t.vertex_c==vertex_array[0]) {
					t.turn(false);
					return false;
				}
			}
		return true;
	}
	
	private void do_separate(long separate_type_id,
			vertex_container vertex_cont,triangle_container triangle_cont)
	{
		triangle t;
		for(int sequence_pointer=1,vertex_pointer=0;vertex_pointer<vertex_number;){
			if((triangle_id<0)||(triangle_id>=triangle_cont.triangle_number))
				break;
			if((t=triangle_cont.triangle_array[triangle_id])==null)
				break;
			vertex_cont.vertex_array[t.vertex_a					 ].sequence=sequence_pointer;
			vertex_cont.vertex_array[t.vertex_a					 ].type_id|=separate_type_id;
			vertex_cont.vertex_array[vertex_array[vertex_pointer]].type_id|=separate_type_id;
			
			double x=vertex_cont.vertex_array[vertex_array[vertex_pointer]].x;
			double z=vertex_cont.vertex_array[vertex_array[vertex_pointer]].z;
			double dx=vertex_cont.vertex_array[t.vertex_a].x-x;
			double dz=vertex_cont.vertex_array[t.vertex_a].z-z;
			if((dx*dx+dz*dz)<const_value.min_value2){
				vertex_pointer++;
				continue;
			}

			t.test_vertex_in_triangle(vertex_cont.vertex_array,vertex_array[vertex_pointer]);
			
			if(t.test_ab_value>const_value.min_value){
				int my_vertex_id=t.vertex_a;
				if((triangle_id=t.triangle_ab)<0)
					break;
				if(triangle_id>=triangle_cont.triangle_number)
					break;
				if((t=triangle_cont.triangle_array[triangle_id])==null)
					break;
				if(t.vertex_b==my_vertex_id)
					t.turn(true);
				if(t.vertex_c==my_vertex_id)
					t.turn(false);
				continue;
			}
			if(t.test_ca_value>const_value.min_value){
				int my_vertex_id=t.vertex_a;
				if((triangle_id=t.triangle_ca)<0)
					break;
				if(triangle_id>=triangle_cont.triangle_number)
					break;
				if((t=triangle_cont.triangle_array[triangle_id])==null)
					break;
				if(t.vertex_b==my_vertex_id)
					t.turn(true);
				if(t.vertex_c==my_vertex_id)
					t.turn(false);
				continue;
			}
			if(Math.abs(t.test_ab_value)<const_value.min_value){
				dx=vertex_cont.vertex_array[t.vertex_b].x-x;
				dz=vertex_cont.vertex_array[t.vertex_b].z-z;
				if((dx*dx+dz*dz)<const_value.min_value2)
					vertex_pointer++;
				t.turn(true);
				sequence_pointer++;
				continue;
			}
			if(Math.abs(t.test_ca_value)<const_value.min_value){
				dx=vertex_cont.vertex_array[t.vertex_c].x-x;
				dz=vertex_cont.vertex_array[t.vertex_c].z-z;
				if((dx*dx+dz*dz)<const_value.min_value2)
					vertex_pointer++;
				t.turn(false);
				sequence_pointer++;
				continue;
			}
			
			sequence_pointer++;
			
			vertex va=vertex_cont.vertex_array[t.vertex_a];
			vertex vb=vertex_cont.vertex_array[t.vertex_b];
			vertex vc=vertex_cont.vertex_array[t.vertex_c];
			vertex vv=vertex_cont.vertex_array[vertex_array[vertex_pointer]];
			point pa=new point(va.x,va.y,va.z);
			point pb=new point(vb.x,vb.y,vb.z);
			point pc=new point(vc.x,vc.y,vc.z);
			point pv=new point(vv.x,vv.y,vv.z);
			point pp=new plane(pa,pv,pv.add(new point(0,1,0))).insection_point(pb,pc);
			int new_vertex_id=vertex_cont.add_vertex(pp.x,pp.y,pp.z,separate_type_id);

			int triangle_id_array[]=triangle_cont.process_vertex_sequence(false,vertex_cont,
				new vertex_sequence(triangle_id,vertex_cont.vertex_array,new int[] {new_vertex_id},1));

			triangle_id=-1;
			for(int i=0,ni=triangle_id_array.length;i<ni;i++) {
				if(triangle_id_array[i]<0)
					continue;
				if(triangle_id_array[i]>=triangle_cont.triangle_number)
					continue;
				if((t=triangle_cont.triangle_array[triangle_id_array[i]])==null)
					continue;
				if((t.vertex_a==new_vertex_id)){
					triangle_id=triangle_id_array[i];
					break;
				}
				if((t.vertex_b==new_vertex_id)){
					triangle_id=triangle_id_array[i];
					t.turn(true);
					break;
				}
				if((t.vertex_c==new_vertex_id)){
					triangle_id=triangle_id_array[i];
					t.turn(false);
					break;
				}
			}
		}
	}

	private int test_triangle_state(int triangle_id,long separate_type_id,
					vertex_container vertex_cont,triangle_container triangle_cont)
	{
		triangle t;
		if((triangle_id<0)||(triangle_id>=triangle_cont.triangle_number))
			return 0;
		if((t=triangle_cont.triangle_array[triangle_id])==null)
			return 0;
		if((vertex_cont.vertex_array[t.vertex_a].type_id&separate_type_id)!=0)
			if((vertex_cont.vertex_array[t.vertex_b].type_id&separate_type_id)!=0)
				if(vertex_cont.vertex_array[t.vertex_a].sequence>0)
					if(vertex_cont.vertex_array[t.vertex_b].sequence>0){
						if(vertex_cont.vertex_array[t.vertex_a].sequence<vertex_cont.vertex_array[t.vertex_b].sequence)
							return 1;
						if(vertex_cont.vertex_array[t.vertex_a].sequence>vertex_cont.vertex_array[t.vertex_b].sequence)
							return -1;
					}
		if((vertex_cont.vertex_array[t.vertex_b].type_id&separate_type_id)!=0)
			if((vertex_cont.vertex_array[t.vertex_c].type_id&separate_type_id)!=0)
				if(vertex_cont.vertex_array[t.vertex_b].sequence>0)
					if(vertex_cont.vertex_array[t.vertex_c].sequence>0) {
						if(vertex_cont.vertex_array[t.vertex_b].sequence<vertex_cont.vertex_array[t.vertex_c].sequence)
							return 1;
						if(vertex_cont.vertex_array[t.vertex_b].sequence>vertex_cont.vertex_array[t.vertex_c].sequence)
							return -1;
					}
		if((vertex_cont.vertex_array[t.vertex_c].type_id&separate_type_id)!=0)
			if((vertex_cont.vertex_array[t.vertex_a].type_id&separate_type_id)!=0)
				if(vertex_cont.vertex_array[t.vertex_c].sequence>0)
					if(vertex_cont.vertex_array[t.vertex_a].sequence>0){
						if(vertex_cont.vertex_array[t.vertex_c].sequence<vertex_cont.vertex_array[t.vertex_a].sequence)
							return 1;
						if(vertex_cont.vertex_array[t.vertex_c].sequence>vertex_cont.vertex_array[t.vertex_a].sequence)
							return -1;
					}
		return 0;
	}
	
	private void caculate_triangle_state(long separate_type_id,
			vertex_container vertex_cont,triangle_container triangle_cont)
	{
		triangle t;
		boolean has_processed_flag[]=new boolean[triangle_cont.triangle_number];
		int triangle_state_array[]=new int[triangle_cont.triangle_number];
		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++){
			if((triangle_state_array[i]=test_triangle_state(i,separate_type_id,vertex_cont,triangle_cont))==0)
				has_processed_flag[i]=false;
			else
				has_processed_flag[i]=true;
		}
		
		for(boolean continue_flag=true;continue_flag;){
			continue_flag=false;
			for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++) {
				if(has_processed_flag[i])
					continue;
				if((t=triangle_cont.triangle_array[i])==null){
					has_processed_flag[i]=true;
					continue;
				}
				for(int j=0;j<3;j++,t.turn(true)) {
					if(t.triangle_ab<0)
						continue;
					if(!(has_processed_flag[t.triangle_ab]))
						continue;
					if((vertex_cont.vertex_array[t.vertex_a].type_id&separate_type_id)!=0)
						if((vertex_cont.vertex_array[t.vertex_b].type_id&separate_type_id)!=0)
							continue;
					triangle_state_array[i]=triangle_state_array[t.triangle_ab];
					has_processed_flag[i]=true;
					continue_flag=true;
					break;
				}
			}
		}

		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++)
			if((t=triangle_cont.triangle_array[i])!=null)
				if(triangle_state_array[i]>0)
					t.state|= separate_type_id;
				else
					t.state&=~separate_type_id;
	}
	
	public separate_triangle(long separate_type_id,
			vertex_container vertex_cont,triangle_container triangle_cont)
	{
		for(int i=0,ni=vertex_cont.vertex_number;i<ni;i++)
			vertex_cont.vertex_array[i].sequence=-1;
		
		if(caculate_vertex_array(separate_type_id,vertex_cont,triangle_cont))
			return;
		
		do_separate(separate_type_id,vertex_cont,triangle_cont);
		
		caculate_triangle_state(separate_type_id,vertex_cont,triangle_cont);
	}
}

class delete_triangle
{
	private vertex_container	vertex_cont;
	private triangle_container 	triangle_cont;
	
	private double x_scale,z_scale,triangle_max_length[];
	private int vertex_triangle_number[];
	private int in_heap_number,triangle_heap[];
	private boolean triangle_in_heap_flag[];

	private void init_delete()
	{
		triangle t;
		
		x_scale=(vertex_cont.max_x-vertex_cont.min_x)/2.0;
		z_scale=(vertex_cont.max_z-vertex_cont.min_z)/2.0;
		triangle_max_length=new double[triangle_cont.triangle_number];
		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++)
			triangle_max_length[i]=0;
		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++)
			if((t=triangle_cont.triangle_array[i])!=null){
				vertex va=vertex_cont.vertex_array[t.vertex_a];
				vertex vb=vertex_cont.vertex_array[t.vertex_b];
				vertex vc=vertex_cont.vertex_array[t.vertex_c];
				point pa=new point(va.x*x_scale,0,va.z*z_scale);
				point pb=new point(vb.x*x_scale,0,vb.z*z_scale);
				point pc=new point(vc.x*x_scale,0,vc.z*z_scale);
				
				double pa_pb_diatance2=pa.sub(pb).distance2();
				double pa_pc_diatance2=pa.sub(pc).distance2();
				double pb_pc_diatance2=pb.sub(pc).distance2();
				
				if(pa_pb_diatance2>pa_pc_diatance2)
					triangle_max_length[i]=pa_pb_diatance2;
				else
					triangle_max_length[i]=pa_pc_diatance2;
				
				if(triangle_max_length[i]<pb_pc_diatance2)
					triangle_max_length[i]=pb_pc_diatance2;
			}
		
		vertex_triangle_number=new int[vertex_cont.vertex_number];
		for(int i=0,ni=vertex_triangle_number.length;i<ni;i++)
			vertex_triangle_number[i]=0;
		
		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++)
			if((t=triangle_cont.triangle_array[i])!=null){
				(vertex_triangle_number[t.vertex_a])++;
				(vertex_triangle_number[t.vertex_b])++;
				(vertex_triangle_number[t.vertex_c])++;
			}
	
		in_heap_number=0;
		triangle_heap=new int[triangle_cont.triangle_number];
		triangle_in_heap_flag=new boolean[triangle_cont.triangle_number];
		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++) {
			triangle_heap[i]=-1;
			triangle_in_heap_flag[i]=false;
		}
		
		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++) {
			if((t=triangle_cont.triangle_array[i])!=null){
				if(vertex_cont.vertex_array[t.vertex_a].type_id>=0)
					if(vertex_cont.vertex_array[t.vertex_b].type_id>=0)
						if(vertex_cont.vertex_array[t.vertex_c].type_id>=0) {
							add_triangle_to_heap(i);
							continue;
						}
				delete_one_triangle(i,vertex_triangle_number);
			}
		}
		
		return;
	}
	
	private void add_triangle_to_heap(int triangle_id)
	{
		if(triangle_id<0)
			return;
		if(triangle_in_heap_flag[triangle_id])
			return;
		triangle_in_heap_flag[triangle_id]=true;
		triangle_heap[in_heap_number++]=triangle_id;
		for(int i=in_heap_number-1,j;i>0;i=j) {
			int my_triangle_id=triangle_heap[i];
			int parent_triangle_id=triangle_heap[j=(i-1)/2];
			if(triangle_max_length[parent_triangle_id]>=triangle_max_length[my_triangle_id])
				break;
			triangle_heap[j]=my_triangle_id;
			triangle_heap[i]=parent_triangle_id;
		}
		
		return;
	}
	
	private int delete_triangle_from_heap()
	{
		if(in_heap_number<=0)
			return -1;
		int ret_val=triangle_heap[0];
		triangle_heap[0]=triangle_heap[--in_heap_number];
		for(int i=0,j;;i=j) {
			if((j=2*i+1)>=in_heap_number)
				break;
			int my_triangle_id=triangle_heap[i];
			int child_triangle_id=triangle_heap[j];
			if((j+1)<in_heap_number)
				if(triangle_max_length[child_triangle_id]<triangle_max_length[triangle_heap[j+1]])
					child_triangle_id=triangle_heap[++j];
			if(triangle_max_length[my_triangle_id]>=triangle_max_length[child_triangle_id])
				break;
			triangle_heap[j]=my_triangle_id;
			triangle_heap[i]=child_triangle_id;
		}
		triangle_in_heap_flag[ret_val]=false;
		
		return ret_val;
	}
	
	private void delete_one_triangle(int triangle_id,int vertex_triangle_number[])
	{
		triangle t,tt;
		if((t=triangle_cont.triangle_array[triangle_id])==null)
			return;

		triangle_cont.triangle_array[triangle_id]=null;
		
		(vertex_triangle_number[t.vertex_a])--;
		(vertex_triangle_number[t.vertex_b])--;
		(vertex_triangle_number[t.vertex_c])--;
		
		int triangle_id_array[]=new int[] {t.triangle_ab,t.triangle_bc,t.triangle_ca};
		for(int i=0,ni=triangle_id_array.length;i<ni;i++) {
			if(triangle_id_array[i]<0)
				continue;
			if((tt=triangle_cont.triangle_array[triangle_id_array[i]])==null)
				continue;
			if(tt.triangle_ab==triangle_id)
				tt.triangle_ab=-1;
			if(tt.triangle_bc==triangle_id)
				tt.triangle_bc=-1;
			if(tt.triangle_ca==triangle_id)
				tt.triangle_ca=-1;
			add_triangle_to_heap(triangle_id_array[i]);
		}
		return;
	}
	
	private void do_delete(double height_width_proportion2,double inside_delete_distance2,double outside_delete_distance2)
	{
		triangle t;
		for(int triangle_id;(triangle_id=delete_triangle_from_heap())>=0;){
			if(triangle_id>=triangle_cont.triangle_number)
				continue;
			if((t=triangle_cont.triangle_array[triangle_id])==null)
				continue;
			if(	  (vertex_triangle_number[t.vertex_a]<=1)
				||(vertex_triangle_number[t.vertex_b]<=1)
				||(vertex_triangle_number[t.vertex_c]<=1))
					continue;
			
			for(int i=0;i<3;i++,t.turn(true)){
				if((t.triangle_bc<0)||(t.triangle_ca<0))
					continue;
				vertex va=vertex_cont.vertex_array[t.vertex_a];
				vertex vb=vertex_cont.vertex_array[t.vertex_b];
				vertex vc=vertex_cont.vertex_array[t.vertex_c];
				point pa=new point(va.x*x_scale,0,va.z*z_scale);
				point pb=new point(vb.x*x_scale,0,vb.z*z_scale);
				point pc=new point(vc.x*x_scale,0,vc.z*z_scale);
				double pb_pa_diatance2=pb.sub(pa).distance2();
				if(t.triangle_ab>=0){
					if(pb_pa_diatance2<inside_delete_distance2)
						continue;
					delete_one_triangle(triangle_id,vertex_triangle_number);
					break;
				}
				if(pb_pa_diatance2>outside_delete_distance2){
					delete_one_triangle(triangle_id,vertex_triangle_number);
					break;
				}
				plane pl=new plane(pa,pb,pb.add(new point(0,1,0)));
				point pd=pl.project_to_plane_location().multiply(pc);
				double pd_pc_diatance2=pd.sub(pc).distance2();
				if(pd_pc_diatance2>(pb_pa_diatance2*height_width_proportion2))
					continue;
				delete_one_triangle(triangle_id,vertex_triangle_number);
				break;
			}
		}
		return;
	}
	
	public delete_triangle(double height_width_proportion,double inside_delete_distance,double outside_delete_distance,
						vertex_container my_vertex_cont,triangle_container my_triangle_cont)
	{
		debug_information.println("Begin delete triagles");
		
		vertex_cont=my_vertex_cont;
		triangle_cont=my_triangle_cont;

		init_delete();
		do_delete(height_width_proportion*height_width_proportion,
				inside_delete_distance*inside_delete_distance,
				outside_delete_distance*outside_delete_distance);
		
		debug_information.println("End delete triagles");
		debug_information.println();
		
		return;
	}
}

class compress_vertex_and_triangle
{
	private vertex_container	vertex_cont;
	private triangle_container 	triangle_cont;

	private void compress_vertex()
	{
		triangle t;
		int vertex_reference_number[]=new int[vertex_cont.vertex_number];
		for(int i=0,ni=vertex_reference_number.length;i<ni;i++)
			vertex_reference_number[i]=0;
		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++)
			if((t=triangle_cont.triangle_array[i])!=null) {
				(vertex_reference_number[t.vertex_a])++;
				(vertex_reference_number[t.vertex_b])++;
				(vertex_reference_number[t.vertex_c])++;
			}
		int new_vertex_number=0;
		int vertex_new_id[]=new int[vertex_cont.vertex_number];
		for(int i=0,ni=vertex_new_id.length;i<ni;i++)
			if((vertex_reference_number[i]>0)&&(vertex_cont.vertex_array[i]!=null))
				vertex_new_id[i]=new_vertex_number++;
			else
				vertex_new_id[i]=-1;
		
		vertex bak[]=vertex_cont.vertex_array;
		vertex_cont.vertex_array=new vertex[new_vertex_number];
		for(int i=0,ni=vertex_cont.vertex_number;i<ni;i++)
			if(vertex_new_id[i]>=0)
				vertex_cont.vertex_array[vertex_new_id[i]]=bak[i];
		vertex_cont.vertex_number=new_vertex_number;
		
		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++)
			if((t=triangle_cont.triangle_array[i])!=null) {
				t.vertex_a=vertex_new_id[t.vertex_a];
				t.vertex_b=vertex_new_id[t.vertex_b];
				t.vertex_c=vertex_new_id[t.vertex_c];
			}
		return;
	}
	private void compress_triangle()
	{
		triangle t;
		int new_triangle_number=0;
		int triangle_new_id[]=new int[triangle_cont.triangle_number];
		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++)
			if((t=triangle_cont.triangle_array[i])!=null)
				triangle_new_id[i]=new_triangle_number++;
			else
				triangle_new_id[i]=-1;

		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++)
			if((t=triangle_cont.triangle_array[i])!=null) {
				if(t.triangle_ab>=0)
					t.triangle_ab=triangle_new_id[t.triangle_ab];
				if(t.triangle_bc>=0)
					t.triangle_bc=triangle_new_id[t.triangle_bc];
				if(t.triangle_ca>=0)
					t.triangle_ca=triangle_new_id[t.triangle_ca];
			}
		triangle bak[]=triangle_cont.triangle_array;
		triangle_cont.triangle_array=new triangle[new_triangle_number];
		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++)
			if(bak[i]!=null) 
				triangle_cont.triangle_array[triangle_new_id[i]]=bak[i];
		triangle_cont.triangle_number=new_triangle_number;
		return;
	}
	public compress_vertex_and_triangle(
			vertex_container my_vertex_cont,triangle_container my_triangle_cont)
	{
		debug_information.println("Begin compress_vertex_and_triangle");
		
		vertex_cont=my_vertex_cont;
		triangle_cont=my_triangle_cont;
		
		compress_vertex();
		compress_triangle();
		
		debug_information.println("End compress_vertex_and_triangle");
		debug_information.println();
	}
}

class write_out_mesh
{
	private vertex_container	vertex_cont;
	private triangle_container 	triangle_cont;
	
	private vertex pa,pb,pc;
	private point ppa,ppb,ppc;
	
	private int triangle_collect_number,line_section_collect_number,triangle_collector[];
	private box part_box,edge_box;

	private point normal[];
	private boolean has_collected_flag[];
	
	private void set_point(triangle t)
	{
		double center_x=(vertex_cont.max_x+vertex_cont.min_x)/2.0;
		double center_z=(vertex_cont.max_z+vertex_cont.min_z)/2.0;
		double length_x=(vertex_cont.max_x-vertex_cont.min_x)/2.0;
		double length_z=(vertex_cont.max_z-vertex_cont.min_z)/2.0;
		
		pa=vertex_cont.vertex_array[t.vertex_a];
		pb=vertex_cont.vertex_array[t.vertex_b];
		pc=vertex_cont.vertex_array[t.vertex_c];
		
		ppa=new point(center_x+pa.x*length_x,	pa.y,	center_z+pa.z*length_z);
		ppb=new point(center_x+pb.x*length_x,	pb.y,	center_z+pb.z*length_z);
		ppc=new point(center_x+pc.x*length_x,	pc.y,	center_z+pc.z*length_z);
	}

	private void init()
	{
		triangle t;
		plane pl;
		
		has_collected_flag=new boolean [triangle_cont.triangle_number];
		for(int i=0,ni=has_collected_flag.length;i<ni;i++)
			has_collected_flag[i]=(triangle_cont.triangle_array[i]==null);

		normal=new point[vertex_cont.vertex_number];
		for(int i=0,ni=vertex_cont.vertex_number;i<ni;i++)
			normal[i]=new point(0,0,0);

		for(int i=0,ni=triangle_cont.triangle_number;i<ni;i++)
			if((t=triangle_cont.triangle_array[i])!=null){
				set_point(t);
				if((pl=new plane(ppa,ppb,ppc)).error_flag)
					continue;
				point dir=new point(pl.A,pl.B,pl.C);
				if(dir.dot(new point(0,1,0))<0)
					dir=new point(-pl.A,-pl.B,-pl.C);
				normal[t.vertex_a]=normal[t.vertex_a].add(dir);
				normal[t.vertex_b]=normal[t.vertex_b].add(dir);
				normal[t.vertex_c]=normal[t.vertex_c].add(dir);
			}
		for(int i=0,ni=vertex_cont.vertex_number;i<ni;i++)
			if(normal[i].distance2()<const_value.min_value2)
				normal[i]=new point(0,1,0);
			else
				normal[i]=normal[i].expand(1.0);
		return;
	}
	
	private int collect_triangle()
	{
		triangle t;
		
		triangle_collector=new int[triangle_cont.triangle_number];
		triangle_collect_number=0;
		line_section_collect_number=0;
		part_box=null;
		edge_box=null;
		
		for(int i=0,n=triangle_cont.triangle_number;i<n;i++) {
			if((t=triangle_cont.triangle_array[i])==null)
				has_collected_flag[i]=true;
			if(has_collected_flag[i])
				continue;
			
			long triangle_state=t.state;
			for(int j=i;j<n;j++) {
				if((t=triangle_cont.triangle_array[j])==null)
					has_collected_flag[j]=true;
				if(has_collected_flag[j])
					continue;
				if(t.state!=triangle_state)
					continue;
				has_collected_flag[j]=true;
					
				set_point(t);
			
				triangle_collector[triangle_collect_number++]=j;
				part_box=((part_box==null)?new box(ppa):(part_box.add(ppa))).add(ppb).add(ppc);
					
				do{
					if(t.triangle_ab>=0)
						if(triangle_cont.triangle_array[t.triangle_ab].state==t.state)
							break;
					line_section_collect_number++;
					edge_box=((edge_box==null)?new box(ppa):edge_box.add(ppa)).add(ppb);
				}while(false);

				do{
					if(t.triangle_bc>=0)
						if(triangle_cont.triangle_array[t.triangle_bc].state==t.state)
							break;
					line_section_collect_number++;
					edge_box=((edge_box==null)?new box(ppb):edge_box.add(ppb)).add(ppc);
				}while(false);
				
				do {
					if(t.triangle_ca>=0)
						if(triangle_cont.triangle_array[t.triangle_ca].state==t.state)
							break;
					line_section_collect_number++;
					edge_box=((edge_box==null)?new box(ppc):edge_box.add(ppc)).add(ppa);
				}while(false);	
			}
			break;
		}
		return triangle_collect_number;
	}
	private void create_mesh(file_writer fw)
	{
		fw.println("/*	version					*/	2021.07.15");
		fw.println("/*	origin material			*/	0	0	0	0");
		fw.println("/*	default material		*/	0	0	0	0");
		fw.println("/*	origin  vertex_location_extra_data	*/	1");
		fw.println("/*	default vertex_location_extra_data	*/	1");
		fw.println("/*	default vertex_normal_extra_data	*/	1");
		fw.println("/*	max_attribute_number	*/	0");
		fw.println();

		fw.println("/*	body_number	*/	1");
		fw.println("	/* body  0  name   */  body_0   /*   face_number   */  1");
		fw.println("		/* face  0  name   */  body_0_face_0");
		fw.println();
		
		fw.println("			/*	face_type   */  unknown  /*   parameter_number   */  0 /*   parameter  */"); 
		fw.println("			/*	total_face_primitive_number	*/	",triangle_collect_number);
		fw.println("			/*	face_attribute_number		*/	0");
		fw.println("			/*	face_face_box				*/	",
										part_box.p[0].x+"	"+part_box.p[0].y+"	"+part_box.p[0].z+"	"+
										part_box.p[1].x+"	"+part_box.p[1].y+"	"+part_box.p[1].z);
		fw.println();
		
		fw.println("			/*	face_loop_number   */ 1");
		fw.println("			/*	face_loop  0  loop_edge_number		*/	1");
		fw.println("				/*	curve_type	*/	segment			/*	parameter_number	*/	0	/*	parameter	*/");
		fw.println("				start_not_effective");
		fw.println("				end_not_effective");
		
		fw.println("				/*  parameter_point_extra_data  */	1");
		fw.println("				/*  parameter_point_material    */	0	0	0	1");

		fw.println("				/*	edge_box						*/	",
											edge_box.p[0].x+"	"+edge_box.p[0].y+"	"+edge_box.p[0].z+"	"+
											edge_box.p[1].x+"	"+edge_box.p[1].y+"	"+edge_box.p[1].z);
		fw.println("				/*	total_edge_primitive_number		*/	",2*line_section_collect_number);
		fw.println("				/*	total_point_primitive_number	*/	0");
		fw.println();
		
		return;
	}
	private void create_face_edge(file_writer part_face_fw,file_writer part_edge_fw)
	{
		int line_section_number=0;
		
		for(int i=0;i<triangle_collect_number;i++) {
			triangle t=triangle_cont.triangle_array[triangle_collector[i]];
			
			set_point(t);
			
			point pp[]=new point[] {ppa,ppb,ppc};
			point pn[]=new point[] {normal[t.vertex_a],normal[t.vertex_b],normal[t.vertex_c]};

			part_face_fw.println("/*	triangle:"+i+"/"+triangle_collect_number+"	*/");
			part_face_fw.println("/*	material			*/	0	0	0	0");
			part_face_fw.println("/*	vertex number		*/	3");
			for(int j=0;j<3;j++) {
				part_face_fw.println("	/*	"+j+".location		*/	",	pp[j].x+"	"+pp[j].y+"	"+pp[j].z+"	1.0");
				part_face_fw.println("	/*	"+j+".normal		*/	",	pn[j].x+"	"+pn[j].y+"	"+pn[j].z+"	1.0");
			}
			part_face_fw.println();

			do{
				if(t.triangle_ab>=0)
					if(triangle_cont.triangle_array[t.triangle_ab].state==t.state)
						break;
				part_edge_fw.println("/*	"+line_section_number+".location:0	*/	",	
						ppa.x+"	"+ppa.y+"	"+ppa.z+"	1.0		/*	material	*/	0	0	0	0");
				part_edge_fw.println("/*	"+line_section_number+".location:1	*/	",	
						ppb.x+"	"+ppb.y+"	"+ppb.z+"	1.0		/*	material	*/	0	0	0	0");
				line_section_number++;
			}while(false);
			
			do{
				if(t.triangle_bc>=0)
					if(triangle_cont.triangle_array[t.triangle_bc].state==t.state)
						break;
				part_edge_fw.println("/*	"+line_section_number+".location:0	*/	",	
						ppb.x+"	"+ppb.y+"	"+ppb.z+"	1.0		/*	material	*/	0	0	0	0");
				part_edge_fw.println("/*	"+line_section_number+".location:1	*/	",	
						ppc.x+"	"+ppc.y+"	"+ppc.z+"	1.0		/*	material	*/	0	0	0	0");
				line_section_number++;
			}while(false);
			
			do {
				if(t.triangle_ca>=0)
					if(triangle_cont.triangle_array[t.triangle_ca].state==t.state)
						break;
				part_edge_fw.println("/*	"+line_section_number+".location:0	*/	",	
						ppc.x+"	"+ppc.y+"	"+ppc.z+"	1.0		/*	material	*/	0	0	0	0");
				part_edge_fw.println("/*	"+line_section_number+".location:1	*/	",	
						ppa.x+"	"+ppa.y+"	"+ppa.z+"	1.0		/*	material	*/	0	0	0	0");
				line_section_number++;
			}while(false);
		}
		return;
	}
	private void create_assemble(file_writer fw,int part_number,String pre_name)
	{	
		fw.println(pre_name+"root");
		fw.println(pre_name+"root");
		fw.println("1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0");
		fw.println(part_number);
		
		for(int i=0;i<part_number;i++) {
			fw.println("	"+pre_name,i);
			fw.println("	"+pre_name,i);
			fw.println("	1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0");
			fw.println("	",0);
			fw.println();
		}
		return;
	}
	public write_out_mesh(String target_directory_name,String pre_name,String file_charset,
			vertex_container my_vertex_cont,triangle_container my_triangle_cont)
	{
		debug_information.println("Begin write_out_mesh");
		
		vertex_cont=my_vertex_cont;
		triangle_cont=my_triangle_cont;

		init();

		int part_number=0;
		file_writer part_list_fw=new file_writer(target_directory_name+"part.list",file_charset);
		while(collect_triangle()>0){
			part_list_fw.println(pre_name,part_number);
			part_list_fw.println(pre_name,part_number);
			part_list_fw.println("part_",part_number+".mesh");
			part_list_fw.println("part_",part_number+".material");
			part_list_fw.println("part_",part_number+".decription");
			part_list_fw.println("part_",part_number+".mp3");
			part_list_fw.println();
			
			file_writer part_fw		=new file_writer(target_directory_name+"part_"+part_number+".mesh",		file_charset);
			file_writer part_face_fw=new file_writer(target_directory_name+"part_"+part_number+".mesh.face",file_charset);
			file_writer part_edge_fw=new file_writer(target_directory_name+"part_"+part_number+".mesh.edge",file_charset);

			create_mesh(part_fw);
			create_face_edge(part_face_fw,part_edge_fw);

			part_fw.close();
			part_face_fw.close();
			part_edge_fw.close();
			
			part_number++;
		}
		part_list_fw.close();
		
		file_writer assemble_fw=new file_writer(target_directory_name+"assemble.assemble",file_charset);
		create_assemble(assemble_fw,part_number,pre_name);
		assemble_fw.close();
	
		debug_information.println("End write_out_mesh");
		debug_information.println();
		
		return;
	}
}

public class pointset_converter 
{
	private vertex_container	vertex_cont;
	private triangle_container 	triangle_cont;
	
	public pointset_converter(String target_directory_name,String pre_name,
			String source_file_name,String separate_file_name[],String file_charset,double scale[],
			double height_width_proportion,double inside_delete_distance,double outside_delete_distance)
	{
		vertex_cont=new vertex_container();
		vertex_cont.add_vertex(0,source_file_name,file_charset,scale);
		if(separate_file_name!=null)
			for(int i=0,j=1,ni=separate_file_name.length;i<ni;i++,j+=j)
				vertex_cont.add_vertex(j,separate_file_name[i],file_charset,scale);
		vertex_cont.normalize_vertex();

		triangle_cont=new triangle_container(vertex_cont);
		
		if(separate_file_name!=null) {
			new caculate_height(vertex_cont,triangle_cont);
			for(int i=0,j=1,ni=separate_file_name.length;i<ni;i++,j+=j) {
				debug_information.println("Begin separate triagles:",i);
				new separate_triangle(j,vertex_cont,triangle_cont);
				debug_information.println("End separate triagles:",i);
			}
			debug_information.println();
		}
		
		new delete_triangle(height_width_proportion,
				inside_delete_distance,outside_delete_distance,
				vertex_cont,triangle_cont);
		new compress_vertex_and_triangle(vertex_cont,triangle_cont);
		
		target_directory_name=file_reader.separator(target_directory_name.trim());
		if(target_directory_name.length()<=0)
			target_directory_name=".";
		if(target_directory_name.charAt(target_directory_name.length()-1)!=File.separatorChar)
			target_directory_name+=File.separatorChar;
		
		new write_out_mesh(target_directory_name,pre_name,file_charset,vertex_cont,triangle_cont);
	}
	
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		new pointset_converter("E:\\cad\\pointset\\","cad_part_",
				"e:\\part.mesh.point",
				new String[] {"e:\\part.mesh.pipe"},
				"GBK",new double[] {1,	-1,		1},
				0.25,125,125);
		
		debug_information.println("End");
	}
}
