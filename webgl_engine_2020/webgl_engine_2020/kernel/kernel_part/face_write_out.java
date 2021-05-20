package kernel_part;

import kernel_file_manager.file_writer;
import kernel_transformation.point;
import kernel_mesh.auxiliary_file_handler;

public class face_write_out
{
	private void write_out_face(
			auxiliary_file_handler f,face_face fa_face,file_writer fw,
			double scale_value,boolean simple_flag,boolean write_out_face_flag)
	{
		fw.set_pace(8);
		fw.print(" /* face_type   */  ");
		fw.print(fa_face.face_type);

		fw.print("  /*   parameter_number   */  ");		
		fw.print(fa_face.face_parameter_number());		
		fw.print(" /*   parameter  */ ");
		for(int i=0;i<fa_face.face_parameter_number();i++){
			fw.print("   ");
			fw.print(fa_face.face_parameter[i]);
		}
		fw.println();
		fw.println();

		if(simple_flag||(fa_face.mesh==null)){
			fw.println(" simple ");
			fw.println(" /* face_attribute_number */ ",fa_face.attribute_number);
			fw.println(" /* total_primitive_number */ ",fa_face.total_primitive_number);
			fw.print(" /* box_material */ ");
			for(int i=0,ni=fa_face.box_material.length;i<ni;i++)
				fw.print(" ",fa_face.box_material[i]);
			fw.println();
			
			fw.print(" /* box_definition */ ");
			if(fa_face.face_face_box==null)
				fw.println("	nobox");
			else{
				fa_face.face_face_box.write_out(fw);
				fw.println();
			}
			return;
		}
		fw.println(" /* face_attribute_number */ ",fa_face.attribute_number);
		fw.println();
		
		int vertices_point_number=write_out_face_flag?fa_face.mesh.get_vertex_number(f):0;
		
		fw.print(" /* face_vertex_number   */  ");	fw.print(vertices_point_number);
		fw.set_pace(12);
		for(int i=0;i<vertices_point_number;i++){
			double data[]		=fa_face.mesh.get_vertex(i,f);
			String extra_data	=fa_face.mesh.get_vertex_extra_data(i,f);
			
			fw.print(" /* face_vertex  ");
			fw.print(i);
			fw.print("  is  */  ");
			
			fw.print  (							data[0]/scale_value);
			fw.print  ("   ",	data[1]/scale_value);	
			fw.print  ("   ",	data[2]/scale_value);
			fw.println("   ",	extra_data);
		}
		
		fw.set_pace(8);
		
		int normals_point_number=write_out_face_flag?fa_face.mesh.get_normal_number(f):0;
		
		fw.print(" /* face_normal_number      */  ");	fw.print(normals_point_number);
		fw.set_pace(12);
		for(int i=0;i<normals_point_number;i++){
			double data[]		=fa_face.mesh.get_normal(i,f);
			String extra_data	=fa_face.mesh.get_normal_extra_data(i,f);
			
			fw.print(" /* face_normal  ");
			fw.print(i);
			fw.print  ("  is  */  ");
			
			fw.print  (			data[0]);
			fw.print  ("   ",	data[1]);	
			fw.print  ("   ",	data[2]);
			fw.println("   ",	extra_data);
		}
		
		for(int i=0,ni=fa_face.attribute_number;i<ni;i++){
			fw.set_pace(8);

			fw.print(" /* face_attribute   ");
			fw.print(i);
			fw.print("  attribute_vertex_number  */  ");
			
			int attribute_point_number=write_out_face_flag
					?fa_face.mesh.get_attribute_number(i,f):0;
			fw.print(attribute_point_number);
			fw.set_pace(12);
			for(int j=0;j<attribute_point_number;j++){
				double data[]		=fa_face.mesh.get_attribute(j,i,f);
				String extra_data	=fa_face.mesh.get_attribute_extra_data(j,i,f);
				
				fw.print(" /* face_attribute_vertex  ");
				fw.print(j);
				fw.print("  is  */  ");
				
				fw.print  (							data[0]);
				fw.print  ("   ",	data[1]);	
				fw.print  ("   ",	data[2]);
				fw.println("   ",	extra_data);
			}
		}
		fw.set_pace(8);
		
		int primitive_number=write_out_face_flag?fa_face.mesh.get_primitive_number():0;
		fw.print(" /* face_primitive_number  */  ");		fw.print(primitive_number);
		fw.set_pace(12);
		
		for(int i=0;i<primitive_number;i++){
			
			fw.print(" /* face_primitive  ");
			fw.print(i);
			fw.print("  material      */ ");
			
			String material[]=fa_face.mesh.get_primitive_material(i,f);
			for(int j=0,nj=material.length;j<nj;j++)
				fw.print("   ",material[j]);
			fw.println();
			
			fw.print(" /* face_primitive  ");
			fw.print(i);
			fw.print("  vertex_index  */ ");
			
			int primitive_vertex_number=fa_face.mesh.get_primitive_vertex_number(i,f);
			for(int j=0;j<primitive_vertex_number;j++)
				fw.print("   ",fa_face.mesh.get_primitive_vertex_index(i,j,f));
			fw.println("   -1");
			
            fw.print(" /* face_primitive  ");
            fw.print(i);
            fw.print("  normal_index  */ ");
		
			for(int j=0;j<primitive_vertex_number;j++)
				fw.print  ("   ",fa_face.mesh.get_primitive_normal_index(i,j,f));
			fw.println("   -1");
			
			for(int j=0,nj=fa_face.attribute_number;j<nj;j++){
				
				fw.print(" /* face_primitive  ");
				fw.print(i);
				fw.print("  attribute  ");
				fw.print(j);	 
				fw.print(" attribute_index   */  ");				
				
				for(int k=0;k<primitive_vertex_number;k++)
					fw.print  ("   ",fa_face.mesh.get_primitive_attribute_index(i,k,j,f));
				fw.println("   -1");
			}
			fw.println();
		}
		fw.println();
	}
	private void write_out_curve(
			auxiliary_file_handler f,face_curve fa_curve,file_writer fw,
			double scale_value,boolean simple_flag,boolean write_out_curve_flag)
	{
		point p;

		fw.set_pace(8);
	
		fw.print(" /* face_loop_number   */ ");
		
		int face_loop_number=write_out_curve_flag?fa_curve.face_loop_number():0;
		
		fw.println(face_loop_number);
		
		for(int i=0;i<face_loop_number;i++){
			fw.set_pace(8);
			face_loop fl=fa_curve.f_loop[i];
			
			fw.print(" /* face_loop  ");
			fw.print(i);
			fw.print  ("  loop_edge_number    */    ");
			
			fw.println(fl.edge_number());
			for(int j=0;j<(fl.edge_number());j++){
				fw.set_pace(12);
				fw.print(" /* face_loop_edge  ");
				fw.print(j);
				fw.print("  data   */   ");
				
				fw.set_pace(16);
				face_edge fe=fl.edge[j];
				
				fw.print("/* curve_type    */  ");
				fw.println(fe.curve_type);
				
				fw.print("/* parameter_number  */  ");	
				fw.print(fe.curve_parameter_number());
				fw.print(" /* parameter */ ");	
				for(int k=0;k<(fe.curve_parameter_number());k++){
					fw.print("   ");	fw.print(fe.curve_parameter[k]);
				}
				fw.println();
				fw.print("/* curve point material */  ");
				for(int k=0,nk=fe.parameter_point_material.length;k<nk;k++)
					fw.print("    ",fe.parameter_point_material[k]);			
				fw.println();
				
				p=fe.start_point;
				fw.print(fe.start_effective_flag?"start_effective":"start_not_effective");
				
				if(fe.start_effective_flag){
					fw.print("   ");	fw.print(p.x/scale_value);		
					fw.print("   ");	fw.print(p.y/scale_value);		
					fw.print("   ");	fw.print(p.z/scale_value);
					fw.print("   ");	fw.print(fe.start_extra_data);
					fw.print("  /*  material  */  ");
					for(int k=0,nk=fe.start_point_material.length;k<nk;k++)
						fw.print("    ",fe.start_point_material[k]);
				}
				fw.println();

				p=fe.end_point;
				fw.print(fe.end_effective_flag?"end_effective":"end_not_effective");
				
				if(fe.end_effective_flag){
					fw.print("   ");	fw.print(p.x/scale_value);		
					fw.print("   ");	fw.print(p.y/scale_value);		
					fw.print("   ");	fw.print(p.z/scale_value);
					fw.print("   ");	fw.print(fe.end_extra_data);
					fw.print("  /*  material  */  ");
					for(int k=0,nk=fe.end_point_material.length;k<nk;k++)
						fw.print("    ",fe.end_point_material[k]);
				}
				fw.println();

				if(simple_flag){
					fw.print("/* box definition  */	");	
					
					if(fe.edge_box==null)
						fw.println("nobox ");
					else{
						fw.print  ("box	");
						fe.edge_box.write_out(fw);
						fw.println();
					}
				}else{
					int tessellation_point_number=0;
					if(fe.edge!=null)
						tessellation_point_number=fe.edge.tessellation_point_number();
					
					fw.println("/* edge_vertex_number  */	",tessellation_point_number);
					
					for(int k=0;k<tessellation_point_number;k++){
						point tessellation_location		=fe.edge.get_tessellation_point(k,f);
						String tessellation_extra_data	=fe.edge.get_tessellation_extra_data(k,f);
						String tessellation_material[]	=fe.edge.get_tessellation_material(k,f);
						
						fw.print("/* edge_vertex    ",k).print("    location   */");
						
						fw.print  ("   ",tessellation_location.x/scale_value);
						fw.print  ("   ",tessellation_location.y/scale_value);		
						fw.print  ("   ",tessellation_location.z/scale_value);
						fw.print  ("   ",tessellation_extra_data);
						fw.print  ("	/*	tessellation material	*/");	
						
						for(int kk=0,nkk=tessellation_material.length;kk<nkk;kk++)
							fw.print("	",tessellation_material[kk]);			
						
						fw.println();
					}
				}
			}
		}
	}
	int body_id,face_id;
	public face_write_out(auxiliary_file_handler f,
			face fa,file_writer fw,double scale_value,boolean simple_flag,
			boolean write_out_face_flag,boolean write_out_curve_flag,int my_body_id,int my_face_id)
	{
		body_id=my_body_id;
		face_id=my_face_id;
		
		write_out_face	(f,	fa.fa_face,	fw,	scale_value,simple_flag,write_out_face_flag);
		write_out_curve	(f,	fa.fa_curve,fw,	scale_value,simple_flag,write_out_curve_flag);
	}
}
