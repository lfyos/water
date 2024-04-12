function construct_computation_object()
{
	this.standard_negative=[
			-1,	1,	0,	0,
			-1,	0,	1,	0,
			-1,	0,	0,	1,
			 1,	0,	0,	0
		];
	this.identity_matrix=[
			1,	0,	0,	0,
			0,	1,	0,	0,
			0,	0,	1,	0,
			0,	0,	0,	1
		];
	this.matrix_multiplication=function(s,t)
	{
		var i,j,k,p,result=new Array(16);
		
		for(i=0;i<4;i++)
			for(j=0;j<4;j++){
				for(p=0,k=0;k<4;k++)
					p+=s[i+4*k]*t[k+4*j];
				result[i+4*j]=p;
			}
		return result;
	};
	this.caculate_coordinate=function(s,x,y,z)
	{
		var result=[0,0,0,0],p=[x,y,z,1.0];
		for(var i=0;i<4;i++)
			for(var j=0;j<4;j++)
				result[i]+=s[i+4*j]*p[j];
		return [result[0]/result[3],result[1]/result[3],result[2]/result[3],1.0];
	};
	this.create_scale_matrix=function(sx,sy,sz)
	{
		return [
			sx,	 0,	 0,	 0,
			 0,	sy,	 0,	 0,
			 0,	 0,	sz,	 0,
			 0,	 0,	 0,	 1
		];
	};
	this.create_move_rotate_matrix=function(mx,my,mz,rx,ry,rz)
	{
		if(rx==0)
			if(ry==0)
				if(rz==0)
					return [
						1,	0,	0,	0,
						0,	1,	0,	0,
						0,	0,	1,	0,
						mx,	my,	mz,	1
					];
		
		rx*=Math.PI/180;
		ry*=Math.PI/180;
		rz*=Math.PI/180;
		
		var cos_alf	 =Math.cos(rx),	sin_alf	 =Math.sin(rx);
		var cos_belta=Math.cos(ry),	sin_belta=Math.sin(ry);
		var cos_gamma=Math.cos(rz),	sin_gamma=Math.sin(rz);
		
		return [
				 cos_gamma*cos_belta,
				 sin_gamma*cos_belta,
				-sin_belta,				0,
				
				-sin_gamma*cos_alf+cos_gamma*sin_belta*sin_alf,
				 cos_gamma*cos_alf+sin_gamma*sin_belta*sin_alf,
				 cos_belta*sin_alf,		0,
				
				 sin_gamma*sin_alf+cos_gamma*sin_belta*cos_alf,
				-cos_gamma*sin_alf+sin_gamma*sin_belta*cos_alf,
				 cos_belta*cos_alf,		0,
		
				 mx,	my,		mz,		1
			];
	};
	this.matrix_negative=function(a)
	{
		var i,j,k;
		var p,b=[
			[a[ 0],a[ 4],a[ 8],a[12],	1,0,0,0],
			[a[ 1],a[ 5],a[ 9],a[13],	0,1,0,0],
			[a[ 2],a[ 6],a[10],a[14],	0,0,1,0],
			[a[ 3],a[ 7],a[11],a[15],	0,0,0,1]
		];
			
		for(i=0;i<3;i++){
			for(k=i,j=i+1;j<4;j++)
				if(Math.abs(b[j][i])>Math.abs(b[k][i]))
					k=j;
			if(i!=k)		
				for(j=i;j<8;j++){
					p=b[i][j];
					b[i][j]=b[k][j];
					b[k][j]=p;
				}
			for(j=i+1;j<4;j++)
				for(p=b[j][i]/b[i][i],k=i+1;k<8;k++)
					b[j][k]-=p*b[i][k];
		}
		for(i=3;i>0;i--)
			for(j=0;j<i;j++)
				for(p=b[j][i]/b[i][i],k=4;k<8;k++)
					b[j][k]-=p*b[i][k];
			
		for(i=0;i<4;i++)
			for(p=b[i][i],j=4;j<8;j++)
				b[i][j]/=p;

		return [
			b[0][4],b[1][4],b[2][4],b[3][4],
			b[0][5],b[1][5],b[2][5],b[3][5],
			b[0][6],b[1][6],b[2][6],b[3][6],
			b[0][7],b[1][7],b[2][7],b[3][7]
		];
	};

	this.create_point_location=function(p0,p1,p2,p3)
	{
		return [
			p0[0],	p0[1],	p0[2],	1.0,
			p1[0],	p1[1],	p1[2],	1.0,
			p2[0],	p2[1],	p2[2],	1.0,
			p3[0],	p3[1],	p3[2],	1.0,
		];
	};
	this.min_value=function()
	{
		return 0.001*0.001*0.001;
	};
	this.min_value2=function()
	{
		return (this.min_value())*(this.min_value());
	};
	this.dot_operation=function(p0,p1)
	{
		var x0=p0[0],	y0=p0[1],	z0=p0[2];
		var x1=p1[0],	y1=p1[1],	z1=p1[2];
		return (x0*x1+y0*y1+z0*z1);	
	};
	this.cross_operation=function(p0,p1)
	{
		var x0=p0[0],	y0=p0[1],	z0=p0[2];
		var x1=p1[0],	y1=p1[1],	z1=p1[2];
		var result=[y0*z1-y1*z0,z0*x1-x0*z1,x0*y1-y0*x1,1.0];
		return result;
	};
	this.add_operation=function(p0,p1)
	{
		var result=[p0[0]+p1[0],p0[1]+p1[1],p0[2]+p1[2],1.0];
		return result;
	};
	this.sub_operation=function(p0,p1)
	{
		var result=[p0[0]-p1[0],p0[1]-p1[1],p0[2]-p1[2],1.0];
		return result;
	};
	this.mix_operation=function(p0,p1,pp)
	{
		var x0=p0[0],	y0=p0[1],	z0=p0[2];
		var x1=p1[0],	y1=p1[1],	z1=p1[2];
		var result=[pp*x0+(1-pp)*x1,pp*y0+(1-pp)*y1,pp*z0+(1-pp)*z1,1.0];
		return result;
	};
	this.distance=function(p)
	{
		return Math.sqrt(this.dot_operation(p,p));
	};
	this.scale_operation=function(p,scale_value)
	{
		var result=[p[0]*scale_value,p[1]*scale_value,p[2]*scale_value,1.0];
		return result;
	};
	this.expand_operation=function(p,new_length)
	{
		var old_length=this.distance(p);
		
		if(old_length<this.min_value())
			return new Array(p[0],p[1],p[2],1.0);
		else
			return this.scale_operation(p,new_length/old_length);
	};
	this.create_plane_from_two_point=function(p0,p1)
	{
		var x0=p0[0],	y0=p0[1],	z0=p0[2];
		var x1=p1[0],	y1=p1[1],	z1=p1[2];
		var A=x1-x0,	B=y1-y0,	C=z1-z0;
		var D=A*x0+B*y0+C*z0;
		var length=Math.sqrt(A*A+B*B+C*C);
		var result=[A/length,B/length,C/length,-D/length];
		return result;
	};
	this.create_plane_from_three_point=function(p0,p1,p2)
	{	
		var x0=p0[0],	y0=p0[1],	z0=p0[2];
		var x1=p1[0],	y1=p1[1],	z1=p1[2];
		var x2=p2[0],	y2=p2[1],	z2=p2[2];
		
		var d10=new Array(x1-x0,y1-y0,z1-z0);
		var d20=new Array(x2-x0,y2-y0,z2-z0);
		var dir=this.cross_operation(d10,d20);
	
		return this.create_plane_from_two_point(p0,this.add_operation(p0,dir));
	};
	this.project_to_plane_location=function(A,B,C,D,scale)
	{
		var d2=(A*A+B*B+C*C)/scale;
		var go_plane_data=[1-A*A/d2,		 -A*B/d2,		 -A*C/d2,		0,
							 -A*B/d2,		1-B*B/d2,		 -B*C/d2,		0,
							 -A*C/d2,		 -B*C/d2,		1-C*C/d2,		0,
							 -A*D/d2,		 -B*D/d2,		 -C*D/d2,		1
						];
		return go_plane_data;
	};
	this.shoot_to_plane_location=function(A,B,C,D,x0,y0,z0,t)
	{
		var p=x0*A+y0*B+z0*C+D;
		var q=D-(1.0-t)*p;
		var tp=t*p;
		var shoot_plane_data=[
							x0*A-tp,	y0*A,		z0*A,		A,	
							x0*B,		y0*B-tp,	z0*B,		B,	
							x0*C,		y0*C,		z0*C-tp,	C,	
							x0*q,		y0*q,		z0*q,		D-p
						];
		return shoot_plane_data
	};
	this.insect_plane=function(pl,s0,s1)
	{
		var A=pl[0],B=pl[1],C=pl[2],D=pl[3];
		var t_up=A*s0[0]+B*s0[1]+C*s0[2]+D;
		var t_down=A*(s1[0]-s0[0])+B*(s1[1]-s0[1])+C*(s1[2]-s0[2]);
		var t=t_up/t_down;
	
		var result=new Array();
		
		result[0]=s0[0]-t*(s1[0]-s0[0]);
		result[1]=s0[1]-t*(s1[1]-s0[1]);
		result[2]=s0[2]-t*(s1[2]-s0[2]);
		result[3]=[1];
		
		return result;
	};
	this.plane_test=function(pl,po)
	{
		var A=pl[0],B=pl[1],C=pl[2],D=pl[3];
		var x=po[0],y=po[1],z=po[2];
		return A*x+B*y+C*z+D;
	};
	this.view_volume_clip_test=function(project_matrix,model_matrix,box_data)
	{
		var plane_array=[
			project_matrix.left_plane,
			project_matrix.right_plane,
			project_matrix.up_plane,
			project_matrix.down_plane,
			project_matrix.near_plane,
			project_matrix.far_plane
		];
		var world_point=[
			this.caculate_coordinate(model_matrix,box_data[0][0],box_data[0][1],box_data[0][2]),
			this.caculate_coordinate(model_matrix,box_data[0][0],box_data[0][1],box_data[1][2]),
			this.caculate_coordinate(model_matrix,box_data[0][0],box_data[1][1],box_data[0][2]),
			this.caculate_coordinate(model_matrix,box_data[0][0],box_data[1][1],box_data[1][2]),
			this.caculate_coordinate(model_matrix,box_data[1][0],box_data[0][1],box_data[0][2]),
			this.caculate_coordinate(model_matrix,box_data[1][0],box_data[0][1],box_data[1][2]),
			this.caculate_coordinate(model_matrix,box_data[1][0],box_data[1][1],box_data[0][2]),
			this.caculate_coordinate(model_matrix,box_data[1][0],box_data[1][1],box_data[1][2])
		];
		for(var i=0,ni=plane_array.length;i<ni;i++){
			var test_number=0;
			for(var j=0,nj=world_point.length;j<nj;j++)
				if(this.plane_test(plane_array[i],world_point[j])>0.0)
					test_number++;
			if(world_point.length==test_number)
				return true;
		};
		return false;
	};
	this.combine_box=function(b1,b2)
	{
		var ret_val=[
			[
				(b1[0][0]<b2[0][0])?b1[0][0]:b2[0][0],
				(b1[0][1]<b2[0][1])?b1[0][1]:b2[0][1],
				(b1[0][2]<b2[0][2])?b1[0][2]:b2[0][2],
				1
			],
			[
				(b1[1][0]>b2[1][0])?b1[1][0]:b2[1][0],
				(b1[1][1]>b2[1][1])?b1[1][1]:b2[1][1],
				(b1[1][2]>b2[1][2])?b1[1][2]:b2[1][2],
				1
			]
		];
		
		ret_val=[
			[
				(ret_val[0][0]<ret_val[1][0])?ret_val[0][0]:ret_val[1][0],
				(ret_val[0][1]<ret_val[1][1])?ret_val[0][1]:ret_val[1][1],
				(ret_val[0][2]<ret_val[1][2])?ret_val[0][2]:ret_val[1][2],
				1
			],
			[
				(ret_val[0][0]>ret_val[1][0])?ret_val[0][0]:ret_val[1][0],
				(ret_val[0][1]>ret_val[1][1])?ret_val[0][1]:ret_val[1][1],
				(ret_val[0][2]>ret_val[1][2])?ret_val[0][2]:ret_val[1][2],
				1
			]
		];
		return ret_val;
	};
	
	this.destroy=function()
	{
		this.standard_negative				=null;
		this.identity_matrix				=null;
	
		this.matrix_multiplication			=null;
		this.caculate_coordinate			=null;
		this.create_scale_matrix			=null;
		this.create_move_rotate_matrix		=null;
		this.matrix_negative				=null;
	
		this.create_point_location			=null;
		this.min_value						=null;
		this.min_value2						=null;
		this.dot_operation					=null;
		this.cross_operation				=null;
		this.add_operation					=null;
		this.sub_operation					=null;
		this.mix_operation					=null;
		this.distance						=null;
		this.scale_operation				=null;
		this.expand_operation				=null;
		this.create_plane_from_two_point	=null;
		this.create_plane_from_three_point	=null;
		this.project_to_plane_location		=null;
		this.shoot_to_plane_location		=null;
		this.insect_plane					=null;
		this.plane_test						=null;
		this.view_volume_clip_test			=null;
		this.combine_box					=null;
	};
};
