package kernel_part;

import kernel_common_class.system_id;
import kernel_common_class.system_id_manager;
import kernel_file_manager.file_reader;
import kernel_transformation.box;

public class face_face  extends system_id
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

	public face_face(file_reader fr,system_id_manager id_manager,int id_array[])
	{
		super(id_manager,id_array);
		
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
		if(str.toLowerCase().compareTo("nobox")==0) {
			fr.mark_terminate(false);
			face_face_box=null;
		}else{
			fr.mark_terminate(true);
			face_face_box=new box(fr);
		}
		return;
	}
	
	public face_face(face_face s,system_id_manager id_manager,int id_array[])
	{
		super(id_manager,id_array);

		face_type=s.face_type;
		face_parameter=s.face_parameter;
		
		total_face_primitive_number	=s.total_face_primitive_number;
		attribute_number			=s.attribute_number;
		
		face_face_box=s.face_face_box;
	}
	public face_face(box b,int my_attribute_number,system_id_manager id_manager,int id_array[])
	{
		super(id_manager,id_array);
	
		face_type="unknown";
		face_parameter=null;
		
		total_face_primitive_number=12;
		attribute_number=my_attribute_number;
		
		face_face_box=new box(b);
	}
}