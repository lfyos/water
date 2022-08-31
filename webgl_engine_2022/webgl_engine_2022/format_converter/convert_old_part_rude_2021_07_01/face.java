package convert_old_part_rude_2021_07_01;

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
		if(fa_face!=null)
			fa_face.destroy();
		fa_face=null;
		
		if(fa_curve!=null)
			fa_curve.destroy();
		fa_curve=null;
		
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
	public face(file_reader fr,double vertex_scale_value,double normal_scale_value,
			boolean delete_redundant_data_flag,boolean combine_flag,boolean create_normal_flag)
	{
		name=fr.get_string();
		name=(name==null)?"":name;
		fa_face=new face_face(fr,vertex_scale_value,normal_scale_value,
				delete_redundant_data_flag,combine_flag,create_normal_flag);
		fa_curve=new face_curve(fr,vertex_scale_value);
		caculate_box();
	}
	public face(location loca,box b,String material[])
	{
		double vertex_location[]=new double[24];
		
		name="no_name";
		fa_face=new face_face(loca,b,material);
		
		for(int i=0;i<8;i++) {
			double p[]=fa_face.mesh.get_vertex(i,null);
			vertex_location[3*i+0]=p[0];
			vertex_location[3*i+1]=p[1];
			vertex_location[3*i+2]=p[2];
		}
		fa_curve=new face_curve(vertex_location);
		caculate_box();
	}
	public face(face s)
	{
		name=new String(s.name);
		
		fa_face=new face_face(s.fa_face);
		
		if(s.fa_curve==null)
			fa_curve=null;
		else
			fa_curve=new face_curve(s.fa_curve);

		face_box=null;
		if(s.face_box!=null)
			face_box=new box(s.face_box);
	}
	public void free_memory()
	{
		fa_face.free_memory();
		fa_curve.free_memory();
	}
};