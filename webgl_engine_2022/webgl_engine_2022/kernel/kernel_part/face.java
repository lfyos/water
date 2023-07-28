package kernel_part;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.location;

public class face
{
	public String name;
	
	public face_face fa_face;
	public face_curve fa_curve;
	public part reference_part;
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
		if(reference_part!=null)
			reference_part=null;
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
		reference_part=s.reference_part;
		face_box=(s.face_box==null)?null:new box(s.face_box);
	}
	public face(file_reader fr)
	{
		name=fr.get_string();
		name=(name==null)?"":name;
		fa_face=new face_face(fr);
		fa_curve=new face_curve(fr);
		reference_part=null;
		caculate_box();
	}
	public face(part my_reference_part,location my_face_loca,box my_face_box)
	{
		name="no_name";
		int double_attribute_number=0,string_attribute_number=0;
		String my_extra_data=null,my_material[]=null;
		if(my_reference_part!=null)
			if(my_reference_part.part_mesh!=null) {
				if(my_reference_part.part_mesh.default_vertex_extra_string!=null)
					my_extra_data=my_reference_part.part_mesh.default_vertex_extra_string;
				if(my_reference_part.part_mesh.default_material!=null)
					if(my_reference_part.part_mesh.default_material.length>=4)
						my_material=my_reference_part.part_mesh.default_material;
				if(my_reference_part.part_mesh.default_attribute_double!=null)
					double_attribute_number=my_reference_part.part_mesh.default_attribute_double.length;
				if(my_reference_part.part_mesh.default_attribute_string!=null)
					string_attribute_number=my_reference_part.part_mesh.default_attribute_string.length;
			}
		if(my_extra_data==null)
			my_extra_data="1";
		if(my_material==null)
			my_material=new String[] {"0","0","0","0"};
		fa_curve=new face_curve(my_face_loca,my_face_box,my_extra_data,my_material);
		fa_face=new face_face(fa_curve.curve_box,
				(double_attribute_number<string_attribute_number)
				?double_attribute_number:string_attribute_number);		

		reference_part=my_reference_part;
		caculate_box();
	}
}