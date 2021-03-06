package convert_old_part_rude_2021_07_01;

import kernel_file_manager.file_writer;
import kernel_transformation.point;

public class face_write_out
{
	private void write_out_face(face_face fa_face,file_writer fw)
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
		
		fw.println(" /* total_face_primitive_number */ ",fa_face.total_primitive_number);
		fw.println(" /* face_attribute_number 		*/ ",fa_face.attribute_number);
		fw.print  (" /* face_face_box				*/ ");
		
		if(fa_face.face_face_box==null)
			fw.println("nobox");
		else {
			fa_face.face_face_box.write_out(fw);
			fw.println();
		}
		
		return;
	}
	private void write_out_curve(face_curve fa_curve,file_writer fw)
	{
		point p;

		fw.set_pace(8);
	
		fw.print(" /* face_loop_number   */ ");
		
		int face_loop_number=fa_curve.face_loop_number();
		
		fw.print(face_loop_number);
		
		for(int i=0;i<face_loop_number;i++){
			fw.set_pace(8);
			face_loop fl=fa_curve.f_loop[i];
			
			fw.print(" /* face_loop  ");
			fw.print(i);
			fw.print("  loop_edge_number    */    ");
			
			fw.print(fl.edge_number());
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
				for(int k=0;k<(fe.curve_parameter_number());k++)
					fw.print("   ",fe.curve_parameter[k]);
				fw.println();

				p=fe.start_point;
				fw.print(fe.start_effective_flag?"start_effective":"start_not_effective");
				
				if(fe.start_effective_flag){
					fw.print("   ");	fw.print(p.x);		
					fw.print("   ");	fw.print(p.y);		
					fw.print("   ");	fw.print(p.z);
					fw.print("   ");	fw.print(fe.start_extra_data);
					fw.print("  /*  material  */  ");
					for(int k=0;k<4;k++)
						fw.print("    ",fe.start_point_material[k]);
				}
				fw.println();

				p=fe.end_point;
				fw.print(fe.end_effective_flag?"end_effective":"end_not_effective");
				
				if(fe.end_effective_flag){
					fw.print("   ");	fw.print(p.x);		
					fw.print("   ");	fw.print(p.y);		
					fw.print("   ");	fw.print(p.z);
					fw.print("   ");	fw.print(fe.end_extra_data);
					fw.print("  /*  material  */  ");
					for(int k=0;k<4;k++)
						fw.print("    ",fe.end_point_material[k]);
				}
				fw.println();
				
				fw.print("/* curve point material */  ");
				for(int k=0;k<4;k++)
					fw.print("    ",fe.parameter_material[k]);			
				fw.println();
				
				fw.print("/* box definition  */	");	
				if(fe.edge_box==null)
					fw.println("nobox ");
				else{
					fe.edge_box.write_out(fw);
					fw.println();
				}
				
				int tessellation_point_number=0;
				if(fe.edge!=null)
					tessellation_point_number=fe.edge.tessellation_point_number();
				fw.println("/* total_edge_primitive_number */  ",tessellation_point_number);
				
				fw.print  ("/* total_point_primitive_number */  ");
				switch(fe.curve_type){
				case "line":
					fw.println(3);
					break;
				case "circle":
					fw.println(1);
					break;
				case "ellipse":
					fw.println(7);
					break;
				case "hyperbola":
					fw.println(7);
					break;
				case "parabola":
					fw.println(2);
					break;
				case "point_set":
					fw.println(tessellation_point_number);
					break;
				case "segment":
				default:
					fw.println(0);break;
				}
			}
		}
	}

	public face_write_out(face fa,file_writer fw,int my_body_id,int my_face_id)
	{
		write_out_face	(fa.fa_face,fw);
		write_out_curve	(fa.fa_curve,fw);
		fw.println();
	}
}
