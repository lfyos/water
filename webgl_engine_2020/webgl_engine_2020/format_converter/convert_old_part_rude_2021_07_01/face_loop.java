package convert_old_part_rude_2021_07_01;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.point;

public class face_loop {	
	public face_edge edge[];
	
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
	public box loop_box;

	private void caculate_box()
	{
		loop_box=null;
		for(int i=0;i<edge_number();i++)
			if(edge[i].edge_box!=null){
				if(loop_box==null)
					loop_box=new box(edge[i].edge_box);
				else
					loop_box=loop_box.add(edge[i].edge_box);
			}
	}
	void free_memory()
	{
		for(int i=0,n=edge_number();i<n;i++)
			edge[i].free_memory();
	}
	public face_loop(face_loop s)
	{
		edge=null;
		if(s.edge!=null)
			if(s.edge.length>0){
				edge=new face_edge[s.edge.length];
				for(int i=0,ni=edge.length;i<ni;i++)
					edge[i]=new face_edge(s.edge[i]);
			}
		if(s.loop_box==null)
			loop_box=null;
		else
			loop_box=new box(s.loop_box);
	}
	public face_loop(file_reader fr,double scale_value)
	{
		int my_edge_number=fr.get_int();

		if(my_edge_number<=0){
			edge=null;
			return;
		}

		edge=new face_edge[my_edge_number];		
		for(int i=0;i<my_edge_number;i++)
			edge[i]=new face_edge(fr,scale_value);
		caculate_box();
	}
	
	public face_loop(point p0,point p1,point p2,point p3)
	{
		edge=new face_edge[4];
		edge[0]=new face_edge(p0,p1);
		edge[1]=new face_edge(p1,p2);
		edge[2]=new face_edge(p2,p3);
		edge[3]=new face_edge(p3,p0);
		caculate_box();
	}
};


