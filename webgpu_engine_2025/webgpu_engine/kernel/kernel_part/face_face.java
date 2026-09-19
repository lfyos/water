package kernel_part;

import kernel_transformation.box;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class face_face
{
	public void destroy()
	{
		face_type=null;
		face_parameter=null;
		face_face_box=null;
	}
	public int face_parameter_number()
	{
		return (face_parameter==null)?0:face_parameter.length;
	}
	public String face_type;
	public double face_parameter[];

	public int total_face_primitive_number,attribute_number;
	
	public box face_face_box;
	
	public face_face(face_face s)
	{
		face_type=s.face_type;
		face_parameter=s.face_parameter;
		
		total_face_primitive_number	=s.total_face_primitive_number;
		attribute_number			=s.attribute_number;
		
		face_face_box=(s.face_face_box==null)?null:new box(s.face_face_box);
	}
	public face_face(box my_face_box,int my_attribute_number)
	{
		face_type="unknown";
		face_parameter=null;
		
		total_face_primitive_number	=2;
		attribute_number			=my_attribute_number;

		face_face_box=(my_face_box==null)?null:new box(my_face_box);
	}
	public face_face(file_reader fr)
	{
		String str;
		
		if((face_type=fr.get_string())==null)
			face_type="";
		int my_face_parameter_number;
		if((my_face_parameter_number=fr.get_int())<=0)
			face_parameter=null;
		else{
			face_parameter=new double[my_face_parameter_number];
			for(int i=0;i<my_face_parameter_number;i++)
				face_parameter[i]=fr.get_double();
		}
		if((total_face_primitive_number=fr.get_int())<0)
			total_face_primitive_number=0;
		if((attribute_number=fr.get_int())<0)
			attribute_number=0;

		fr.mark_start();
		if((str=fr.get_string())==null)
			str="nobox";
		
		switch(str.toLowerCase()) {
		case "nobox":
			fr.mark_terminate(false);
			face_face_box=null;
			break;
		default:
			fr.mark_terminate(true);
			face_face_box=new box(fr);
			break;
		}
		return;
	}
	public void write_out(file_writer fw)
	{
		fw.println();

		int my_face_parameter_number=(face_parameter==null)?0:face_parameter.length;
		fw.println("/*	face_type:						*/	 ",face_type);
		fw.println("/*	face_parameter_number:			*/	 ",my_face_parameter_number);
		fw.print  ("/*	face_parameter:					*/	 ");
		for(int i=0;i<my_face_parameter_number;i++)
			fw.print(face_parameter[i]+" ");
		fw.println();
		fw.println("/*	total_face_primitive_number:	*/	 ",total_face_primitive_number);
		fw.println("/*	attribute_number:				*/	 ",attribute_number);
		fw.print  ("/*	face_face_box:					*/	");
		if(face_face_box==null)
			fw.print(" nobox");
		else 
			face_face_box.write_out(fw);
		fw.println();
		fw.println();
	}
}