package kernel_part;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.point;

public class face_loop
{	
	public face_edge edge[];
	public box loop_box;
	public int total_edge_primitive_number,total_point_primitive_number;
	
	public void destroy()
	{
		for(int i=0,ni=edge_number();i<ni;i++)
			if(edge[i]!=null){
				edge[i].destroy();
				edge[i]=null;
			}
		edge=null;
		loop_box=null;
	}
	public int edge_number()
	{
		if(edge==null)
			return 0;
		else
			return edge.length;
	}
	private void caculate_box_and_primitive_number()
	{
		loop_box=null;
		total_edge_primitive_number=0;
		total_point_primitive_number=0;
		for(int i=0,ni=edge_number();i<ni;i++) {
			if(edge[i].edge_box!=null){
				if(loop_box==null)
					loop_box=new box(edge[i].edge_box);
				else
					loop_box=loop_box.add(edge[i].edge_box);
			}
			if(edge[i].start_point!=null)
				total_point_primitive_number++;
			if(edge[i].end_point!=null)
				total_point_primitive_number++;
			
			//"line",	"circle",	"ellipse",	"hyperbola",	"parabola",
			//"pickup_point_set",	"render_point_set",			"segment",		"unknown"
			switch(edge[i].curve_type){
			case "line":
				total_edge_primitive_number+=edge[i].total_edge_primitive_number-1;
				total_point_primitive_number++;
				break;
			case "circle":
				total_edge_primitive_number+=edge[i].total_edge_primitive_number-1;
				total_point_primitive_number++;
				break;
			case "ellipse":
			case "hyperbola":
			case "parabola":
				total_edge_primitive_number+=edge[i].total_edge_primitive_number-1;
				total_point_primitive_number++;
				total_point_primitive_number++;
				break;
			case "pickup_point_set":
				if(edge[i].curve_parameter!=null)
					total_point_primitive_number+=edge[i].curve_parameter.length/3;
				break;
			case "render_point_set":
				total_point_primitive_number+=edge[i].total_point_primitive_number;
				break;
			case "segment":
				total_edge_primitive_number+=edge[i].total_edge_primitive_number/2;
				break;
			case "unknown":
			default:
				total_edge_primitive_number+=edge[i].total_edge_primitive_number-1;
				break;
			}
		}
	}
	public face_loop(face_loop s)
	{
		edge=null;
		if(s.edge!=null) {
			if(s.edge.length>0){
				edge=new face_edge[s.edge.length];
				for(int i=0,ni=edge.length;i<ni;i++)
					edge[i]=new face_edge(s.edge[i]);
			}
		}
		loop_box=(s.loop_box==null)?null:new box(s.loop_box);
		total_edge_primitive_number	=s.total_edge_primitive_number;
		total_point_primitive_number=s.total_point_primitive_number;
	}
	public face_loop(file_reader fr)
	{
		int my_edge_number;

		if((my_edge_number=fr.get_int())<=0)
			edge=null;
		else {
			edge=new face_edge[my_edge_number];		
			for(int i=0;i<my_edge_number;i++)
				edge[i]=new face_edge(fr);
		}
		caculate_box_and_primitive_number();
	}
	public face_loop(point p0,point p1,point p2,point p3,
			String my_extra_data,String my_material[])
	{
		edge=new face_edge[] 
		{
			new face_edge(p0,p1,my_extra_data,my_material),
			new face_edge(p1,p2,my_extra_data,my_material),
			new face_edge(p2,p3,my_extra_data,my_material),
			new face_edge(p3,p0,my_extra_data,my_material)
		};
		caculate_box_and_primitive_number();
	}
};