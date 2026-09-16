package kernel_mesh;

import kernel_part.body;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class part_rude_2021_07_15 
{
	public String origin_vertex_extra_data,origin_material[];
	
	public String face_default_material[];
	public String face_default_vertex_extra_string,face_default_normal_extra_string;
	public double face_default_attribute_double[];
	public String face_default_attribute_string[];
	
	public body body_array[];
	public int total_face_primitive_number,total_edge_primitive_number,total_point_primitive_number;
	
	public part_rude_2021_07_15(String file_name,String file_charset)
	{
		file_reader fr=new file_reader(file_name,file_charset);
		read_in(fr);
		fr.close();
	}
	public void read_in(file_reader fr)
	{
		String default_value="0";
		
		String version_string=fr.get_string();	//version code
		if(version_string==null)
			version_string="simple";

		origin_material=new String[4];
		for(int i=0,ni=origin_material.length;i<ni;i++)
			if((origin_material[i]=fr.get_string())==null)
				origin_material[i]=default_value;
		
		face_default_material=new String[4];
		for(int i=0,ni=face_default_material.length;i<ni;i++)
			if((face_default_material[i]=fr.get_string())==null)
				face_default_material[i]=default_value;
		
		if((origin_vertex_extra_data=fr.get_string())==null)
			origin_vertex_extra_data="1";
		if((face_default_vertex_extra_string=fr.get_string())==null)
			face_default_vertex_extra_string="1";
		if((face_default_normal_extra_string=fr.get_string())==null)
			face_default_normal_extra_string="1";
		
		int max_attribute_number=fr.get_int();
		max_attribute_number=(max_attribute_number<=0)?0:max_attribute_number;
		
		face_default_attribute_double=new double[3*max_attribute_number];
		face_default_attribute_string=new String[1*max_attribute_number];
		for(int i=0,j=0;j<max_attribute_number;j++) {
			face_default_attribute_double[i++]=fr.get_double();
			face_default_attribute_double[i++]=fr.get_double();
			face_default_attribute_double[i++]=fr.get_double();
			if((face_default_attribute_string[j]=fr.get_string())==null)
				face_default_attribute_string[j]="1";
		}
		int my_body_number;
		if((my_body_number=fr.get_int())<=0)
			body_array=null;
		else{
			body_array=new body[my_body_number];
			for(int i=0;i<my_body_number;i++)
				body_array[i]=new body(fr);
		}
		return;
	}
	
	public void write_out(file_writer fw)
	{
		fw.println("/*	version_string						*/	2026.09.16");
		
		fw.print  ("/*	point_default_material				*/");
		for(int i=0,ni=origin_material.length;i<ni;i++)
			fw.print  ("	",origin_material[i]);
		fw.println();
		
		fw.print  ("/*	face_default_material				*/");
		for(int i=0,ni=face_default_material.length;i<ni;i++)
			fw.print  ("	",face_default_material[i]);
		fw.println();
		
		fw.print  ("/*	edge_default_material				*/");
		for(int i=0,ni=face_default_material.length;i<ni;i++)
			fw.print  ("	",face_default_material[i]);
		fw.println();
		
		fw.println("/*	point_default_vertex_extra_String	*/	",origin_vertex_extra_data);
		fw.println("/*	face_default_vertex_extra_string	*/	",face_default_vertex_extra_string);
		fw.println("/*	face_default_normal_extra_string	*/	",face_default_normal_extra_string);
		fw.println("/*	edge_default_vertex_extra_string	*/	",face_default_vertex_extra_string);
		
		fw.println("/*	max_attribute_number				*/	",face_default_attribute_string.length);
		
		for(int i=0,ni=face_default_attribute_string.length;i<ni;i++) {
			fw	.print  ("/*	face_default_attribute_double:"+i+"		*/	")
				.print	(face_default_attribute_double[3*i+0])
				.print	(" ",face_default_attribute_double[3*i+1])
				.println(" ",face_default_attribute_double[3*i+2]);
			fw	.print  ("/*	face_default_attribute_double:"+i+"		*/	")
				.println(face_default_attribute_string[i]);
		}
		
		int my_body_number=(body_array==null)?0:body_array.length;
		fw.println().println().println("/*	body_number	*/	",my_body_number);
		for(int i=0;i<my_body_number;i++)
			body_array[i].write_out(fw);
	}
}
