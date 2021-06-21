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
		for(int i=0,ni=edge_number();i<ni;i++)
			if(edge[i].edge_box!=null){
				if(loop_box==null)
					loop_box=new box(edge[i].edge_box);
				else
					loop_box=loop_box.add(edge[i].edge_box);
				total_edge_primitive_number+=edge[i].total_edge_primitive_number;
				total_point_primitive_number+=edge[i].total_point_primitive_number;
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
	public face_loop(file_reader fr,double scale_value)
	{
		int my_edge_number;

		if((my_edge_number=fr.get_int())<=0)
			edge=null;
		else {
			edge=new face_edge[my_edge_number];		
			for(int i=0;i<my_edge_number;i++)
				edge[i]=new face_edge(fr,scale_value);
		}
		caculate_box_and_primitive_number();
	}
	public face_loop(point p0,point p1,point p2,point p3,String extra_data,String parameter_material[])
	{
		edge=new face_edge[4];
		edge[0]=new face_edge(p0,p1,extra_data,parameter_material);
		edge[1]=new face_edge(p1,p2,extra_data,parameter_material);
		edge[2]=new face_edge(p2,p3,extra_data,parameter_material);
		edge[3]=new face_edge(p3,p0,extra_data,parameter_material);
		caculate_box_and_primitive_number();
	}
};