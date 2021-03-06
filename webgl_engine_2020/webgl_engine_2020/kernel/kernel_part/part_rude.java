package kernel_part;

import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.location;

public class part_rude 
{
	public void destroy()
	{
		origin_material=null;
		default_material=null;
		default_attribute_double=null;
		default_attribute_string=null;
		for(int i=0,ni=body_number();i<ni;i++)
			if(body_array[i]!=null){
				body_array[i].destroy();
				body_array[i]=null;
			}
		body_array=null;
		part_box=null;
	}
	
	public String origin_material[];
	public String default_material[];
	public double default_attribute_double[];
	public String default_attribute_string[];
	
	public body body_array[];
	public box part_box;
	public int total_face_primitive_number,total_edge_primitive_number,total_point_primitive_number;

	public int body_number()
	{
		return (body_array==null)?0:body_array.length;
	}
	public int max_attribute_number()
	{
		return (default_attribute_string==null)?0:(default_attribute_string.length);
	}
	private void caculate_rp_box_and_primitive_number()
	{
		part_box=null;
		total_face_primitive_number=0;
		for(int i=0,ni=body_number();i<ni;i++)
			if(body_array[i]!=null){
				if(body_array[i].body_box!=null) {
					if(part_box==null) {
						part_box=new box(body_array[i].body_box);
					}else
						part_box=part_box.add(body_array[i].body_box);
				}
				total_face_primitive_number+=body_array[i].total_face_primitive_number;
				total_edge_primitive_number+=body_array[i].total_edge_primitive_number;
				total_point_primitive_number+=body_array[i].total_point_primitive_number;
			}
	}
	public part_rude(part_rude s)
	{
		origin_material=s.origin_material;
		
		default_material=s.default_material;
		default_attribute_double=s.default_attribute_double;
		default_attribute_string=s.default_attribute_string;
		
		int body_number;
		if((body_number=s.body_number())<=0)
			body_array=null;
		else{
			body_array=new body[body_number];
			for(int i=0;i<body_number;i++)
				body_array[i]=new body(s.body_array[i]);
		}
		
		if((part_box=s.part_box)!=null)
			part_box=new box(s.part_box);
		
		total_face_primitive_number =s.total_face_primitive_number;
		total_edge_primitive_number =s.total_edge_primitive_number;
		total_point_primitive_number=s.total_point_primitive_number;
	}
	public part_rude(file_reader fr,double vertex_scale_value)
	{
		fr.get_string();	//version code 
		
		origin_material=new String[4];
		for(int i=0,ni=origin_material.length;i<ni;i++)
			origin_material[i]=fr.get_string();
		
		default_material=new String[]
			{fr.get_string(),fr.get_string(),fr.get_string(),fr.get_string()};
		int max_attribute_number=fr.get_int();
		max_attribute_number=(max_attribute_number<=0)?0:max_attribute_number;
		
		default_attribute_double=new double[3*max_attribute_number];
		default_attribute_string=new String[1*max_attribute_number];
		for(int i=0,j=0;j<max_attribute_number;) {
			default_attribute_double[i++]=fr.get_double();
			default_attribute_double[i++]=fr.get_double();
			default_attribute_double[i++]=fr.get_double();
			default_attribute_string[j++]=fr.get_string();
		}
		int my_body_number;
		if((my_body_number=fr.get_int())<=0)
			body_array=null;
		else{
			body_array=new body[my_body_number];
			for(int i=0;i<my_body_number;i++)
				body_array[i]=new body(fr,vertex_scale_value,
					default_material,default_attribute_double,default_attribute_string);
		}
		caculate_rp_box_and_primitive_number();
		return;
	}
	public part_rude(String my_origin_material[],String my_default_material[][],
		double my_default_attribute_double[][],String my_default_attribute_string[][],
		int my_box_number,location my_box_loca[],box my_box_array[],String my_extra_data[])
	{
		origin_material			=my_origin_material;
		default_material		=my_default_material[0];
		default_attribute_double=my_default_attribute_double[0];
		default_attribute_string=my_default_attribute_string[0];
		for(int i=0;i<my_box_number;i++)
			if(default_attribute_string.length<my_default_attribute_string[i].length) {
				default_material=my_default_material[i];
				default_attribute_double=my_default_attribute_double[i];
				default_attribute_string=my_default_attribute_string[i];
			}
		for(int i=0;i<my_box_number;i++)
			if(my_default_attribute_string[i].length<default_attribute_string.length){
				double bak_default_attribute_double[]=my_default_attribute_double[i];
				String bak_default_attribute_string[]=my_default_attribute_string[i];
				my_default_attribute_double[i]=new double[default_attribute_double.length];
				my_default_attribute_string[i]=new String[default_attribute_string.length];
				for(int j=0,nj=bak_default_attribute_double.length;j<nj;j++)
					my_default_attribute_double[i][j]=bak_default_attribute_double[j];
				for(int j=bak_default_attribute_double.length,nj=default_attribute_double.length;j<nj;j++)
					my_default_attribute_double[i][j]=default_attribute_double[j];
				for(int j=0,nj=bak_default_attribute_string.length;j<nj;j++)
					my_default_attribute_string[i][j]=bak_default_attribute_string[j];
				for(int j=bak_default_attribute_string.length,nj=default_attribute_string.length;j<nj;j++)
					my_default_attribute_string[i][j]=default_attribute_string[j];
			}
		double max_distance_2=my_box_array[0].distance2();
		int max_index_id=0;
		for(int i=0;i<my_box_number;i++){
			double my_max_distance_2=my_box_array[i].distance2();
			if(my_max_distance_2>max_distance_2) {
				max_distance_2=my_max_distance_2;
				max_index_id=i;
			}
		}

		default_material		=my_default_material		[max_index_id];
		default_attribute_double=my_default_attribute_double[max_index_id];
		default_attribute_string=my_default_attribute_string[max_index_id];
		
		body_array=new body[]
		{
			new body(my_box_number,my_box_loca,my_box_array,my_extra_data,my_default_material,
						my_default_attribute_double,my_default_attribute_string)
		};
		caculate_rp_box_and_primitive_number();
		return;
	}
}
