package kernel_part;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.location;

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
	private void caculate_box()
	{
		face_box=null;
		
		if(fa_face!=null)
			if(fa_face.face_face_box!=null)
				face_box=new box(fa_face.face_face_box);
		if(fa_curve!=null)
			if(fa_curve.curve_box!=null){
				if(face_box==null)
					face_box=fa_curve.curve_box;
				else
					face_box=face_box.add(fa_curve.curve_box);
			}		
	}
	public face(face s)
	{
		name	=new String(s.name);
		fa_face	=(s.fa_face ==null)?null:new face_face (s.fa_face);
		fa_curve=(s.fa_curve==null)?null:new face_curve(s.fa_curve);
		face_box=(s.face_box==null)?null:new box(s.face_box);
	}
	public face(file_reader fr,double vertex_scale_value,String my_default_material[],
			double my_default_attribute_double[],String my_default_attribute_string[])
	{
		name=fr.get_string();
		name=(name==null)?"":name;
		fa_face=new face_face(fr,my_default_material,my_default_attribute_double,my_default_attribute_string);
		fa_curve=new face_curve(fr,vertex_scale_value);
		caculate_box();
	}
	public face(
		location my_face_loca,box my_face_box,String extra_data,String my_default_material[],
		double my_default_attribute_double[],String my_default_attribute_string[])
	{
		name="no_name";
		fa_curve=new face_curve(my_face_loca,my_face_box,extra_data,my_default_material);
		fa_face=new face_face(fa_curve.curve_box,my_default_material,
			my_default_attribute_double,my_default_attribute_string);
		caculate_box();
	}
};