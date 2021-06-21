package kernel_part;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.location;

public class body
{
	public String name;	
	public face face_array[];
	public box body_box;
	public int total_face_primitive_number,total_edge_primitive_number,total_point_primitive_number;
	
	public int face_number()
	{
		return (face_array==null)?0:face_array.length;
	}
	public void destroy()
	{
		for(int i=0,ni=face_number();i<ni;i++)
			if(face_array[i]!=null){
				face_array[i].destroy();
				face_array[i]=null;
			}
		face_array=null;
		name=null;
		body_box=null;
	}
	public body(body s)
	{
		int face_number;
		name=new String(s.name);
		if((face_number=s.face_number())<=0)
			face_array=null;
		else{
			face_array=new face[face_number];
			for(int i=0;i<face_number;i++)
				face_array[i]=new face(s.face_array[i]);
		}
		body_box=(s.body_box==null)?null:(new box(s.body_box));
		total_face_primitive_number=s.total_face_primitive_number;
		total_edge_primitive_number=s.total_edge_primitive_number;
		total_point_primitive_number=s.total_point_primitive_number;
	}
	private void caculate_box_and_primitive_number()
	{
		body_box=null;
		total_face_primitive_number=0;
		for(int i=0,ni=face_number();i<ni;i++){
			if(face_array[i].face_box!=null){
				if(body_box==null)
					body_box=new box(face_array[i].face_box);
				else
					body_box=body_box.add(face_array[i].face_box);
			}
			total_face_primitive_number+=face_array[i].fa_face.total_face_primitive_number;
			total_edge_primitive_number+=face_array[i].fa_curve.total_edge_primitive_number;
			total_point_primitive_number+=face_array[i].fa_curve.total_point_primitive_number;
		}
	}
	public body(file_reader fr,double vertex_scale_value,String my_default_material[],
			double my_default_attribute_double[],String my_default_attribute_string[])
	
	{
		name=fr.get_string();
		name=(name==null)?"":name;
		
		int my_face_number=fr.get_int();
		
		if(my_face_number<=0){
			my_face_number=0;
			face_array=null;
		}else{
			face_array=new face[my_face_number];
			for(int i=0;i<my_face_number;i++)
				face_array[i]=new face(fr,vertex_scale_value,
					my_default_material,my_default_attribute_double,my_default_attribute_string);
		}
		caculate_box_and_primitive_number();
	}
	public body(
			int my_box_number,location my_box_loca[],box my_box_array[],
			String extra_data[],String my_default_material[][],
			double my_default_attribute_double[][],String my_default_attribute_string[][])
	{
		name="box_body_with_"+my_box_number+"_faces";	
		face_array=new face[my_box_number];
		for(int i=0;i<my_box_number;i++)
			face_array[i]=new face(my_box_loca[i],my_box_array[i],extra_data[i],
				my_default_material[i],my_default_attribute_double[i],my_default_attribute_string[i]);
		caculate_box_and_primitive_number();
	}
}
