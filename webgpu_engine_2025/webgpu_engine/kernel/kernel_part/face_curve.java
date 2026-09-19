package kernel_part;

import kernel_transformation.box;
import kernel_transformation.point;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class face_curve
{
	public face_loop f_loop[];
	public box curve_box;
	public int total_edge_primitive_number,total_point_primitive_number;
	
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
		return (f_loop==null)?0:f_loop.length;
	}
	private void caculate_box_and_primitive_number()
	{
		curve_box=null;
		total_edge_primitive_number=0;
		total_point_primitive_number=0;
		for(int i=0,ni=face_loop_number();i<ni;i++)
			if(f_loop[i].loop_box!=null){
				if(curve_box==null)
					curve_box=new box(f_loop[i].loop_box);
				else
					curve_box=curve_box.add(f_loop[i].loop_box);
				total_edge_primitive_number +=f_loop[i].total_edge_primitive_number;
				total_point_primitive_number+=f_loop[i].total_point_primitive_number;
			}
	}
	public face_curve(point p0,point p1,point p2,point p3,
			String my_edge_extra_data,String my_edge_material[])
	{
		f_loop=new face_loop[]
		{
			new face_loop(p0,p1,p2,p3,my_edge_extra_data,my_edge_material)
		};	
		caculate_box_and_primitive_number();
	}
	public face_curve(face_curve s)
	{
		if(s.face_loop_number()<=0)
			f_loop=null;
		else{
			f_loop=new face_loop[s.f_loop.length];
			for(int i=0,ni=f_loop.length;i<ni;i++)
				f_loop[i]=new face_loop(s.f_loop[i]);
		}
		curve_box=(s.curve_box==null)?null:new box(s.curve_box);
		total_edge_primitive_number	=s.total_edge_primitive_number;
		total_point_primitive_number=s.total_point_primitive_number;
	}
	public face_curve(file_reader fr)
	{
		int my_face_loop_number;
		if((my_face_loop_number=fr.get_int())<=0)
			f_loop=null;
		else{
			f_loop=new face_loop[my_face_loop_number];
			for(int i=0;i<my_face_loop_number;i++)
				f_loop[i]=new face_loop(fr);
		}
		caculate_box_and_primitive_number();
	}
	public void write_out(file_writer fw)
	{
		int my_face_loop_number=(f_loop==null)?0:f_loop.length;
		fw.println().println("/*	loop_number	*/	",my_face_loop_number);
		for(int i=0;i<my_face_loop_number;i++)
			f_loop[i].write_out(fw);
		fw.println();
	}
};