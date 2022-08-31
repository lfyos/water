package convert_old_part_rude_2021_07_01;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.point;

public class face_curve {
	public face_loop f_loop[];
	
	public void destroy()
	{
		for(int i=0,ni=face_loop_number();i<ni;i++)
			if(f_loop[i]!=null){
				f_loop[i].destroy();
				f_loop[i]=null;
			}
		f_loop=null;
		curve_box=null;
	}
	public int face_loop_number()
	{
		if(f_loop==null)
			return 0;
		else
			return f_loop.length;
	}
	public box curve_box;
	
	private void caculate_box()
	{
		curve_box=null;

		for(int i=0,ni=face_loop_number();i<ni;i++)
			if(f_loop[i].loop_box!=null){
				if(curve_box==null)
					curve_box=new box(f_loop[i].loop_box);
				else
					curve_box=curve_box.add(f_loop[i].loop_box);
			}
	}
	private void clear()
	{
		f_loop=null;
		
		caculate_box();
	}
	public face_curve(double vertex_data[])
	{
		point p[]=new point[]
				{
						new point(vertex_data[ 0],vertex_data[ 1],vertex_data[ 2]),
						new point(vertex_data[ 3],vertex_data[ 4],vertex_data[ 5]),
						new point(vertex_data[ 6],vertex_data[ 7],vertex_data[ 8]),
						new point(vertex_data[ 9],vertex_data[10],vertex_data[11]),
						new point(vertex_data[12],vertex_data[13],vertex_data[14]),
						new point(vertex_data[15],vertex_data[16],vertex_data[17]),
						new point(vertex_data[18],vertex_data[19],vertex_data[20]),
						new point(vertex_data[21],vertex_data[22],vertex_data[23]),
				};
		clear();
		
		f_loop=new face_loop[6];
		
		f_loop[0]=new face_loop(p[0],p[1],p[3],p[2]);		//left face
		f_loop[1]=new face_loop(p[5],p[4],p[6],p[7]);		//right face
		f_loop[2]=new face_loop(p[0],p[1],p[5],p[4]);		//down
		f_loop[3]=new face_loop(p[2],p[3],p[7],p[6]);		//up
		f_loop[4]=new face_loop(p[0],p[2],p[6],p[4]);		//front
		f_loop[5]=new face_loop(p[1],p[5],p[7],p[3]);		//back
		
		caculate_box();
	}
	public face_curve(file_reader fr,double scale_value)
	{
		clear();

		int my_face_loop_number=fr.get_int();
		
		if(my_face_loop_number<=0){
			my_face_loop_number=0;
		}else{
			f_loop=new face_loop[my_face_loop_number];
			for(int i=0;i<my_face_loop_number;i++)
				f_loop[i]=new face_loop(fr,scale_value);
		}
		caculate_box();
	}
	public face_curve(face_curve s)
	{
		f_loop=null;
		if(s.face_loop_number()>0){
			f_loop=new face_loop[s.f_loop.length];
			for(int i=0,ni=f_loop.length;i<ni;i++)
				f_loop[i]=new face_loop(s.f_loop[i]);
		}
		if(s.curve_box==null)
			curve_box=null;
		else
			curve_box=new box(s.curve_box);
	}
	public void free_memory()
	{
		for(int i=0,n=face_loop_number();i<n;i++)
			f_loop[i].free_memory();
	}
};

