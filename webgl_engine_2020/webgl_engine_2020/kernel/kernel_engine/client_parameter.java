package kernel_engine;

import kernel_part.part;
import kernel_component.component;

public class client_parameter 
{
	public int body_id,face_id,loop_id,edge_id,vertex_id,point_id;
	public double x,y,aspect,depth,value;
	public boolean high_or_low_precision_flag,mouse_inside_canvas_flag;
	
	public int request_length,max_client_loading_number;
	
	public component comp;
	
	public void destroy()
	{
		comp=null;
	}
	public client_parameter(int my_max_client_loading_number)
	{
		max_client_loading_number	=my_max_client_loading_number;
		
		body_id		=-1;
		face_id		=-1;
		loop_id		=-1;
		edge_id		=-1;
		vertex_id	=-1;
		point_id	=-1;
		
		x			=0;
		y			=0;
		aspect		=1.0;
		
		depth=1.0;
		value=-1.0;
		
		high_or_low_precision_flag=true;
		mouse_inside_canvas_flag=false;
		
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
			if((str=ci.request_response.get_parameter("body"))!=null)
				body_id=Integer.decode(str);
			if((str=ci.request_response.get_parameter("face"))!=null)
				face_id=Integer.decode(str);
			if((str=ci.request_response.get_parameter("loop"))!=null)
				loop_id=Integer.decode(str);
			if((str=ci.request_response.get_parameter("edge"))!=null)
				edge_id=Integer.decode(str);
			if((str=ci.request_response.get_parameter("vertex"))!=null)
				vertex_id=Integer.decode(str);
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
		if((str=ci.request_response.get_parameter("in_canvas"))!=null)
			mouse_inside_canvas_flag=(str.compareTo("yes")==0);
		
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
		
		switch(((str=ci.request_response.get_parameter("precision"))==null)?"":(str.toLowerCase())) {
		default:
		case "true":
		case "yes":
			high_or_low_precision_flag=true;
			break;
		case "false":
		case "no":
			high_or_low_precision_flag=false;
			break;
		}

		part my_part;
		if(comp!=null)
			if(comp.driver_number()>0)
				if((my_part=comp.driver_array[0].component_part)!=null)
					if(ek.part_lru.touch(my_part.render_id,my_part.part_id))
						ek.part_loader_cont.load_part_mesh_head_only(
								my_part,ek.part_cont,ek.system_par,ek.scene_par);
		return;
	}
}