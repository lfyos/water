package kernel_part;


import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_transformation.box;
import kernel_transformation.location;

public class part_rude 
{
	public void free_memory()
	{
		if(body_array!=null) {
			for(int i=0,ni=body_number();i<ni;i++)
				if(body_array[i]!=null){
					body_array[i].destroy();
					body_array[i]=null;
				}
			body_array=null;
		}
	}
	public void destroy()
	{
		origin_vertex_extra_data=null;
		origin_material=null;
		default_material=null;
		default_vertex_extra_string=null;
		default_normal_extra_string=null;
		default_attribute_double=null;
		default_attribute_string=null;
		part_box=null;
		
		free_memory();
	}
	
	public String origin_vertex_extra_data,origin_material[];
	public String default_material[];
	public String default_vertex_extra_string,default_normal_extra_string;
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
		total_edge_primitive_number=0;
		total_point_primitive_number=0;
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
		origin_vertex_extra_data	=s.origin_vertex_extra_data;
		origin_material				=s.origin_material;
		default_material			=s.default_material;
		default_vertex_extra_string	=s.default_vertex_extra_string;
		default_normal_extra_string	=s.default_normal_extra_string;
		default_attribute_double	=s.default_attribute_double;
		default_attribute_string	=s.default_attribute_string;
		
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
	public part_rude(file_reader fr)
	{
		String default_value="0";
		
		String version_string=fr.get_string();	//version code
		if(version_string==null)
			version_string="simple";

		origin_material=new String[4];
		for(int i=0,ni=origin_material.length;i<ni;i++)
			if((origin_material[i]=fr.get_string())==null)
				origin_material[i]=default_value;
		
		default_material=new String[4];
		for(int i=0,ni=default_material.length;i<ni;i++)
			if((default_material[i]=fr.get_string())==null)
				default_material[i]=default_value;
		
		if((origin_vertex_extra_data=fr.get_string())==null)
			origin_vertex_extra_data="1";
		if((default_vertex_extra_string=fr.get_string())==null)
			default_vertex_extra_string="1";
		if((default_normal_extra_string=fr.get_string())==null)
			default_normal_extra_string="1";
		
		int max_attribute_number=fr.get_int();
		max_attribute_number=(max_attribute_number<=0)?0:max_attribute_number;
		
		default_attribute_double=new double[3*max_attribute_number];
		default_attribute_string=new String[1*max_attribute_number];
		for(int i=0,j=0;j<max_attribute_number;j++) {
			default_attribute_double[i++]=fr.get_double();
			default_attribute_double[i++]=fr.get_double();
			default_attribute_double[i++]=fr.get_double();
			if((default_attribute_string[j]=fr.get_string())==null)
				default_attribute_string[j]="1";
		}
		if(version_string.compareTo("simple")==0) {
			body_array=null;
			fr.mark_start();
			String str;
			if((str=fr.get_string())==null)
				str="nobox";
			if(str.toLowerCase().compareTo("nobox")==0) {
				fr.mark_terminate(false);
				part_box=null;
			}else{
				fr.mark_terminate(true);
				part_box=new box(fr);
			}
			total_face_primitive_number	=fr.get_int();
			total_edge_primitive_number	=fr.get_int();
			total_point_primitive_number=fr.get_int();
		}else {
			int my_body_number;
			if((my_body_number=fr.get_int())<=0)
				body_array=null;
			else{
				body_array=new body[my_body_number];
				for(int i=0;i<my_body_number;i++)
					body_array[i]=new body(fr);
			}
			caculate_rp_box_and_primitive_number();
		}
		return;
	}
	public part_rude(int my_box_number,part my_reference_part[],location my_box_loca[],box my_box_array[])
	{		
		double max_distance_2=my_box_array[0].distance2();
		int max_index_id=0;
		for(int i=0;i<my_box_number;i++){
			double my_max_distance_2=my_box_array[i].distance2();
			if(my_max_distance_2>max_distance_2) {
				max_distance_2=my_max_distance_2;
				max_index_id=i;
			}
		}
		part_rude pr=my_reference_part[max_index_id].part_mesh;
		
		origin_vertex_extra_data	=pr.origin_vertex_extra_data;
		origin_material				=pr.origin_material;
		default_material			=pr.default_material;
		default_vertex_extra_string	=pr.default_vertex_extra_string;
		default_normal_extra_string	=pr.default_normal_extra_string;
		default_attribute_double	=pr.default_attribute_double;
		default_attribute_string	=pr.default_attribute_string;
		
		for(int i=0;i<my_box_number;i++) 
			if((pr=my_reference_part[i].part_mesh)!=null){
				if(default_attribute_string.length<pr.default_attribute_string.length) {
					String bak[]=default_attribute_string;
					default_attribute_string=new String[pr.default_attribute_string.length];
					for(int j=0,nj=bak.length;j<nj;j++)
						default_attribute_string[j]=bak[j];
					for(int j=bak.length,nj=pr.default_attribute_string.length;j<nj;j++)
						default_attribute_string[j]=pr.default_attribute_string[j];
				}
				if(default_attribute_double.length<pr.default_attribute_double.length) {
					double bak[]=default_attribute_double;
					default_attribute_double=new double[pr.default_attribute_double.length];
					for(int j=0,nj=bak.length;j<nj;j++)
						default_attribute_double[j]=bak[j];
					for(int j=bak.length,nj=pr.default_attribute_double.length;j<nj;j++)
						default_attribute_double[j]=pr.default_attribute_double[j];
				}
			}
		body_array=new body[]
			{
				new body(my_box_number,my_reference_part,my_box_loca,my_box_array)
			};
		caculate_rp_box_and_primitive_number();
		return;
	}
	
	public void write_out_to_simple_file(file_writer fw)
	{
		String str[]=new String[] {
				"/*	version								*/	simple",
				"/*	origin material						*/	"
						+origin_material[0]+"	"+origin_material[1]+"	"+origin_material[2]+"	"+origin_material[3],
				"/*	default material					*/	"
						+default_material[0]+"	"+default_material[1]+"	"+default_material[2]+"	"+default_material[3],
				"/*	origin  vertex_location_extra_data	*/	"+origin_vertex_extra_data,
				"/*	default vertex_location_extra_data	*/	"+default_vertex_extra_string,
				"/*	default vertex_normal_extra_data	*/	"+default_normal_extra_string,
				"",
				"/*	max_attribute_number				*/	"+default_attribute_string.length
		};
		
		for(int i=0,ni=str.length;i<ni;i++)
			fw.println(str[i]);
		for(int i=0,j=0,ni=default_attribute_string.length;i<ni;i++) {
			fw.print  ("/*		"+i+".attribute:					*/	",
								default_attribute_double[j++]);
			fw.print  ("	",	default_attribute_double[j++]);
			fw.print  ("	",	default_attribute_double[j++]);
			fw.println("	",	default_attribute_string[i]);
		}
		
		fw.println();
		fw.print  ("/*	part_box							*/	");
		if(part_box==null)
			fw.println("nobox");
		else
			fw.println(	part_box.p[0].x+"	"+part_box.p[0].y+"	"+part_box.p[0].z+"	"+
						part_box.p[1].x+"	"+part_box.p[1].y+"	"+part_box.p[1].z);
		fw.println();
		
		fw.println("/*	total_face_primitive_number			*/	",total_face_primitive_number);
		fw.println("/*	total_edge_primitive_number			*/	",total_edge_primitive_number);
		fw.println("/*	total_point_primitive_number		*/	",total_point_primitive_number);
		fw.println();
		
		return;
	}
}
