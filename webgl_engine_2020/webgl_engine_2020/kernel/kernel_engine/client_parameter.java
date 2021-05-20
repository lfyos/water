package kernel_engine;

import kernel_component.component;

public class client_parameter 
{
	public boolean 	do_discard_lod_flag,do_selection_lod_flag;
	
	public int body_id,face_id,loop_id,edge_id,vertex_id,point_id;
	public double body_value,face_value,loop_value,edge_value,vertex_value;
	public double x,y,aspect,depth,value;
	public boolean high_or_low_precision_flag;
	
	public int request_length;
	
	public component comp;
	
	public void destroy()
	{
		comp=null;
	}
	public client_parameter(boolean my_do_discard_lod_flag,boolean my_do_selection_lod_flag)
	{
		do_discard_lod_flag		=my_do_discard_lod_flag;
		do_selection_lod_flag	=my_do_selection_lod_flag;
		
		body_id		=-1;
		face_id		=-1;
		loop_id		=-1;
		edge_id		=-1;
		vertex_id	=-1;
		
		body_value=0;
		face_value=0;
		loop_value=0;
		edge_value=0;
		vertex_value=0;
		
		point_id	=-1;
		
		x			=-1.0;
		y			=-1.0;
		aspect		=1.0;
		

		depth=1.0;
		value=-1.0;
		
		high_or_low_precision_flag=true;
		
		request_length=0;

		comp=null;
	}
	public void get_call_parameter(engine_kernel ek,client_information ci)
	{		
		String str;

		if((str=ci.request_response.get_parameter("component"))!=null)
			comp=ek.component_cont.get_component(Integer.decode(str));

		if(comp==null){
			body_id				=-1;
			face_id				=-1;
			vertex_id			=-1;
			loop_id				=-1;
			edge_id				=-1;
			point_id			=-1;
		}else{
			if((str=ci.request_response.get_parameter("body"))!=null){
				if((body_id=str.indexOf(';'))<0) {
					body_value=0;
					body_id=Integer.decode(str);
				}else if(body_id==0){
					body_value=0;
					body_id=Integer.decode(str.substring(1));
				}else{
					body_value=Double.parseDouble(str.substring(0,body_id));
					body_id=Integer.decode(str.substring(body_id+1));
				}
			}
			if((str=ci.request_response.get_parameter("face"))!=null){
				if((face_id=str.indexOf(';'))<0) {
					face_value=0;
					face_id=Integer.decode(str);
				}else if(face_id==0){
					face_value=0;
					face_id=Integer.decode(str.substring(1));
				}else {
					face_value=Double.parseDouble(str.substring(0,face_id));
					face_id=Integer.decode(str.substring(face_id+1));
				}
			}
			if((str=ci.request_response.get_parameter("loop"))!=null){
				if((loop_id=str.indexOf(';'))<0) {
					loop_value=0;
					loop_id=Integer.decode(str);
				}else if(loop_id==0){
					loop_value=0;
					loop_id=Integer.decode(str.substring(1));
				}else {
					loop_value=Double.parseDouble(str.substring(0,loop_id));
					loop_id=Integer.decode(str.substring(loop_id+1));
				}
			}
			if((str=ci.request_response.get_parameter("edge"))!=null){
				if((edge_id=str.indexOf(';'))<0) {
					edge_value=0;
					edge_id=Integer.decode(str);
				}else if(edge_id==0){
					edge_value=0;
					edge_id=Integer.decode(str.substring(1));
				}else {
					edge_value=Double.parseDouble(str.substring(0,edge_id));
					edge_id=Integer.decode(str.substring(edge_id+1));
				}
			}
			if((str=ci.request_response.get_parameter("vertex"))!=null){
				if((vertex_id=str.indexOf(';'))<0) {
					vertex_value=0;
					vertex_id=Integer.decode(str);
				}else if(vertex_id==0){
					vertex_value=0;
					vertex_id=Integer.decode(str.substring(1));
				}else {
					vertex_value=Double.parseDouble(str.substring(0,vertex_id));
					vertex_id=Integer.decode(str.substring(vertex_id+1));
				}
			}
			if((str=ci.request_response.get_parameter("point"))!=null)
				point_id=Integer.decode(str);
		}
		
		if((str=ci.request_response.get_parameter("x"))!=null)
			x=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("y"))!=null)
			y=Double.parseDouble(str);	
		if((str=ci.request_response.get_parameter("aspect"))!=null)
			if((aspect=Double.parseDouble(str))<=0)
				aspect=1.0;
		if((str=ci.request_response.get_parameter("depth"))!=null)
			depth=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("value"))!=null)
			value=Double.parseDouble(str);
		
		
		if((str=ci.request_response.get_parameter("length"))!=null)
			request_length=Integer.decode(str);

		if((str=ci.request_response.get_parameter("data_time"))==null)
			ci.statistics_client.data_time_length=1;
		else if((ci.statistics_client.data_time_length=1000000*Long.decode(str))<=0)
			ci.statistics_client.data_time_length=1;
		
		if((str=ci.request_response.get_parameter("render_time"))==null)
			ci.statistics_client.render_time_length=1;
		else if((ci.statistics_client.render_time_length=1000000*Long.decode(str))<=0)
			ci.statistics_client.render_time_length=1;

		if((str=ci.request_response.get_parameter("read_time"))==null)
			ci.statistics_client.read_time_length=1;
		else if((ci.statistics_client.read_time_length=1000000*Long.decode(str))<=0)
			ci.statistics_client.read_time_length=1;
		
		if((str=ci.request_response.get_parameter("render_interval"))==null)
			ci.statistics_client.render_interval_length=1;
		else if((ci.statistics_client.render_interval_length=1000000*Long.decode(str))<=0)
			ci.statistics_client.render_interval_length=1;

		return;
	}
}