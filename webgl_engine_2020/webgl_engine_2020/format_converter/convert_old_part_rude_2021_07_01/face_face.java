package convert_old_part_rude_2021_07_01;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.location;

public class face_face 
{
	public face_mesh mesh;

	public void free_memory()
	{
		if(mesh!=null){
			mesh.destroy();
			mesh=null;
		}
	}
	public void destroy()
	{
		free_memory();

		face_parameter=null;
		face_face_box=null;
		box_material=null;
		face_type=null;
	}
	public void add_attribute(double x,double y,double z,String w)
	{
		if(mesh!=null)
			attribute_number=mesh.add_attribute(x,y,z,w);
	}
	
	public String face_type;
	public double face_parameter[];
	
	public int face_parameter_number()
	{
		if(face_parameter==null)
			return 0;
		else
			return face_parameter.length;
	}
	public box face_face_box;
	public String box_material[];
	public int attribute_number,total_primitive_number;
	
	public double box_attribute_double[];
	public String box_attribute_string[];
	
	public face_face(file_reader fr,double vertex_scale_value,double normal_scale_value,
			boolean delete_redundant_data_flag,boolean combine_flag,boolean create_normal_flag)
	{
		String str;
		
		if((face_type=fr.get_string())==null)
			face_type="";
		int my_face_parameter_number=fr.get_int();

		if(my_face_parameter_number<=0){
			my_face_parameter_number=0;
		}else{
			face_parameter=new double[my_face_parameter_number];
			for(int i=0;i<my_face_parameter_number;i++)
				face_parameter[i]=fr.get_double();
		}
		switch(str=fr.get_string()) {
		case "simple":
			mesh=null;
			
			attribute_number=fr.get_int();
			total_primitive_number=fr.get_int();
			box_material=new String[4];
			for(int i=0,ni=box_material.length;i<ni;i++)
				box_material[i]=fr.get_string();
			
			face_face_box=null;
			fr.mark_start();
			if((str=fr.get_string())==null)
				str="nobox";
			if(str.toLowerCase().compareTo("nobox")==0)
				fr.mark_terminate(false);
			else{
				fr.mark_terminate(true);
				face_face_box=new box(fr);
			}
			box_attribute_string=new String[] {};
			box_attribute_double=new double[] {};
			return;
		case "external":
			mesh=new face_mesh_external_triangle(fr,vertex_scale_value,normal_scale_value);
			
			attribute_number		=mesh.get_attribute_number();
			total_primitive_number	=mesh.get_primitive_number();
			box_material			=mesh.get_box_material();
			face_face_box			=mesh.get_face_box();
			
			box_attribute_string=new String[mesh.get_attribute_number()];
			box_attribute_double=new double[box_attribute_string.length*3];
			for(int i=0,j=0,ni=box_attribute_string.length;i<ni;i++) {
				box_attribute_double[j++]=fr.get_double();
				box_attribute_double[j++]=fr.get_double();
				box_attribute_double[j++]=fr.get_double();
				box_attribute_string[i  ]=fr.get_string();
			}
			return;
		default:
			mesh=new face_mesh_default(fr,Integer.decode(str),vertex_scale_value,
				normal_scale_value,delete_redundant_data_flag,combine_flag,create_normal_flag);
			
			attribute_number		=mesh.get_attribute_number();
			total_primitive_number	=mesh.get_primitive_number();
			box_material			=mesh.get_box_material();
			face_face_box			=mesh.get_face_box();
			
			box_attribute_string=new String[mesh.get_attribute_number()];
			box_attribute_double=new double[box_attribute_string.length*3];
			for(int i=0,j=0,ni=box_attribute_string.length;i<ni;i++) {
				double p[]=mesh.get_attribute(0,i,null);
				box_attribute_double[j++]=p[0];
				box_attribute_double[j++]=p[1];
				box_attribute_double[j++]=p[2];
				box_attribute_string[i  ]=mesh.get_attribute_extra_data(0,i,null);
			}
			return;
		}
	}
	public face_face(face_face s)
	{
		attribute_number=s.attribute_number;
		
		if((mesh=s.mesh)!=null)
			mesh=s.mesh.clone();
		
		face_type=s.face_type;
		face_parameter=null;
		if(s.face_parameter_number()>0){
			face_parameter=new double[s.face_parameter.length];
			for(int i=0,ni=face_parameter.length;i<ni;i++)
				face_parameter[i]=s.face_parameter[i];
		}
		 if(s.face_face_box==null)
			 face_face_box=null;
		 else
			 face_face_box=new box(s.face_face_box);
		 
		 box_material=s.box_material;
		 total_primitive_number=s.total_primitive_number;
		 
		 box_attribute_string=s.box_attribute_string;
		 box_attribute_double=s.box_attribute_double;
	}
	
	public face_face(location loca,box b,String material[])
	{
		face_type="unknown";
		face_parameter=null;
		
		mesh=new face_mesh_default(loca,b,material);
		
		total_primitive_number=mesh.get_primitive_number();
		box_material=mesh.get_box_material();
		face_face_box=mesh.get_face_box();
		
		box_attribute_string=new String[mesh.get_attribute_number()];
		box_attribute_double=new double[box_attribute_string.length*3];
		for(int i=0,j=0,ni=box_attribute_string.length;i<ni;i++) {
			double p[]=mesh.get_attribute(0,i,null);
			box_attribute_double[j++]=p[0];
			box_attribute_double[j++]=p[1];
			box_attribute_double[j++]=p[2];
			box_attribute_string[i  ]=mesh.get_attribute_extra_data(0,i,null);
		}
	}
}