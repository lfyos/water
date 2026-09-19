package kernel_part;

import kernel_transformation.box;
import kernel_transformation.location;
import kernel_transformation.point;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class body
{
	public String name;	
	public face face_array[];
	public part reference_part;
	public box body_box;
	public int total_face_primitive_number,total_edge_primitive_number,total_point_primitive_number;
	
	public int face_number()
	{
		return (face_array==null)?0:face_array.length;
	}
	public void destroy()
	{
		name=null;
		for(int i=0,ni=face_number();i<ni;i++)
			if(face_array[i]!=null){
				face_array[i].destroy();
				face_array[i]=null;
			}
		face_array=null;
		reference_part=null;
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
		reference_part=s.reference_part;
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
	public body(part my_reference_part,location loca,box b)
	{
		reference_part=my_reference_part;
		name=reference_part.user_name+"_box_part_body";
		
		String my_edge_extra_data=null,my_edge_material[]=null;
		int double_attribute_number=0,string_attribute_number=0;
		if(reference_part!=null)
			if(reference_part.part_mesh!=null) {
				if(reference_part.part_mesh.edge_default_vertex_extra_string!=null)
					my_edge_extra_data=reference_part.part_mesh.edge_default_vertex_extra_string;
				if(reference_part.part_mesh.edge_default_material!=null)
					if(reference_part.part_mesh.edge_default_material.length>=4)
						my_edge_material=reference_part.part_mesh.edge_default_material;
				if(reference_part.part_mesh.face_default_attribute_double!=null)
					double_attribute_number=reference_part.part_mesh.face_default_attribute_double.length;
				double_attribute_number/=3;
				if(reference_part.part_mesh.face_default_attribute_string!=null)
					string_attribute_number=reference_part.part_mesh.face_default_attribute_string.length;
			}
		if(my_edge_extra_data==null)
			my_edge_extra_data="1";
		if(my_edge_material==null)
			my_edge_material=new String[] {"0","0","0","0"};
		int attribute_number=(double_attribute_number<string_attribute_number)
				?double_attribute_number:string_attribute_number;
																//		 Y
		point p[]=new point[]									//		 |
		{														//       2***********************6
			new point(b.p[0].x,b.p[0].y,b.p[0].z),	//point 0	//     * |                     * *
			new point(b.p[0].x,b.p[0].y,b.p[1].z),	//point 1	//	 *   |                    *  *
			new point(b.p[0].x,b.p[1].y,b.p[0].z),	//point 2	//	3************************7   *
			new point(b.p[0].x,b.p[1].y,b.p[1].z),	//point 3	//	*    |                   *   *
			new point(b.p[1].x,b.p[0].y,b.p[0].z),	//point 4	//	*    0-------------------*---4-X
			new point(b.p[1].x,b.p[0].y,b.p[1].z),	//point 5	//	*   /                    *  *
			new point(b.p[1].x,b.p[1].y,b.p[0].z),	//point 6	//	*  /                     * *  
			new point(b.p[1].x,b.p[1].y,b.p[1].z),	//point 7	//	1************************5
		};														// 	Z
		for(int i=0,ni=p.length;i<ni;i++)
			p[i]=loca.multiply(p[i]);
		
		face_array=new face[] {
				new face(p[0],p[1],p[3],p[2],"box_part_face_0",
						my_edge_extra_data,my_edge_material,attribute_number),	//left face
				new face(p[4],p[6],p[7],p[5],"box_part_face_1",
						my_edge_extra_data,my_edge_material,attribute_number),	//right face
				new face(p[0],p[4],p[5],p[1],"box_part_face_2",
						my_edge_extra_data,my_edge_material,attribute_number),	//down
				new face(p[2],p[3],p[7],p[6],"box_part_face_3",
						my_edge_extra_data,my_edge_material,attribute_number),	//up
				new face(p[0],p[2],p[6],p[4],"box_part_face_4",
						my_edge_extra_data,my_edge_material,attribute_number),	//front
				new face(p[1],p[5],p[7],p[3],"box_part_face_5",
						my_edge_extra_data,my_edge_material,attribute_number)	//back
		};
		caculate_box_and_primitive_number();
	}
	public body(file_reader fr)
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
				face_array[i]=new face(fr);
		}
		reference_part=null;
		caculate_box_and_primitive_number();
	}
	public void write_out(file_writer fw)
	{
		int my_face_number=(face_array==null)?0:face_array.length;
		
		fw.println();
		fw.println("/*	body name:		*/	",name);
		fw.println("/*	face number:	*/	",my_face_number);
		for(int i=0;i<my_face_number;i++)
			face_array[i].write_out(fw);
		fw.println();
	}
}
