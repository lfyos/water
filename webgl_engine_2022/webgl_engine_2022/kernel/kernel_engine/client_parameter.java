package kernel_engine;

import kernel_part.part;
import kernel_component.component;

public class client_parameter 
{
	public int body_id,face_id,loop_id,edge_id,vertex_id,point_id;
	public double x,y,depth,value;
	public boolean high_or_low_precision_flag,mouse_inside_canvas_flag;
	
	public int canvas_width	,canvas_height,request_length,max_client_loading_number;
	
	public component comp;
	
	public void destroy()
	{
		comp=null;
	}
	public client_parameter(int my_max_client_loading_number)
	{
		body_id		=-1;
		face_id		=-1;
		loop_id		=-1;
		edge_id		=-1;
		vertex_id	=-1;
		point_id	=-1;
		
		x			=0;
		y			=0;
		
		depth=1.0;
		value=-1.0;
		
		high_or_low_precision_flag=true;
		mouse_inside_canvas_flag=false;
		
		canvas_width=1;
		canvas_height=1;
		request_length=0;
		max_client_loading_number	=my_max_client_loading_number;

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
		if((str=ci.request_response.get_parameter("width"))!=null)
			canvas_width=Integer.decode(str);
		if((str=ci.request_response.get_parameter("height"))!=null)
			canvas_height=Integer.decode(str);		
		if((str=ci.request_response.get_parameter("in_canvas"))!=null)
			mouse_inside_canvas_flag=(str.compareTo("yes")==0);
		
		if((str=ci.request_response.get_parameter("depth"))!=null)
			depth=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("value"))!=null)
			value=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("length"))!=null)
			request_length=Integer.decode(str);
		
		if((str=ci.request_response.get_parameter("acknowledge"))!=null) 
			for(int render_id,part_id,index_id;str.length()>0;) {
				if((index_id=str.indexOf('_'))<0)
					break;
				render_id=Integer.parseInt(str.substring(0,index_id));
				str=str.substring(index_id+1);				
				if((index_id=str.indexOf('_'))<0)
					break;
				part_id=Integer.parseInt(str.substring(0,index_id));
				str=str.substring(index_id+1);
				if((render_id<0)||(part_id<0))
					continue;
				if(render_id>=ci.not_acknowledge_render_part_id.length)
					continue;
				if(render_id>=ci.not_acknowledge_render_part_id[render_id].length)
					continue;
				ci.not_acknowledge_render_part_id[render_id][part_id]=false;
			}
		
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

		if(comp==null)
			return;
		if(comp.driver_number()<=0)
			return;
		part my_part=comp.driver_array.get(0).component_part;
		if(my_part==null)
			return;
		if(!(ek.part_lru.touch(my_part.render_id,my_part.part_id)))
			return;
		ek.part_loader_cont.load_part_mesh_head_only(my_part,ek.part_cont,ek.system_par,ek.scene_par);
		return;
	}
}