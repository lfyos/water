package kernel_part;

import kernel_transformation.box;
import kernel_transformation.point;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class face
{
	public String name;
	
	public face_face fa_face;
	public face_curve fa_curve;
	public box face_box;

	public void destroy()
	{
		if(name!=null)
			name=null;
		if(fa_face!=null) {
			fa_face.destroy();
			fa_face=null;
		}
		if(fa_curve!=null) {
			fa_curve.destroy();
			fa_curve=null;
		}
		if(face_box!=null)
			face_box=null;
	}
	private void caculate_face_box()
	{
		face_box=null;	
		if(fa_face!=null)
			if(fa_face.face_face_box!=null)
				face_box=new box(fa_face.face_face_box);
		if(fa_curve!=null)
			if(fa_curve.curve_box!=null)
				if(face_box==null)
					face_box=new box(fa_curve.curve_box);
				else
					face_box=face_box.add(fa_curve.curve_box);
	}
	public face(face s)
	{
		name	=new String(s.name);
		fa_face	=(s.fa_face ==null)?null:new face_face (s.fa_face);
		fa_curve=(s.fa_curve==null)?null:new face_curve(s.fa_curve);
		face_box=(s.face_box==null)?null:new box(s.face_box);
	}
	public face(point p0,point p1,point p2,point p3,String face_name,
			String my_edge_extra_data,String my_edge_material[],int attribute_number)
	{
		name=face_name;

		fa_curve=new face_curve	(p0,p1,p2,p3,my_edge_extra_data,my_edge_material);
		fa_face	=new face_face	(fa_curve.curve_box,attribute_number);
		caculate_face_box();
	}
	public face(file_reader fr)
	{
		name=fr.get_string();
		name=(name==null)?"":name;
		fa_face=new face_face(fr);
		fa_curve=new face_curve(fr);
		caculate_face_box();
	}
	public void write_out(file_writer fw)
	{
		fw.println().println("/*	face name:	*/	",name);
		fa_face.write_out(fw);
		fa_curve.write_out(fw);
		fw.println();
	}
}