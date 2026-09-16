package kernel_part;

import kernel_transformation.box;
import kernel_transformation.location;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class part_rude 
{
	public boolean test_loaded_flag()
	{
		return (body_array!=null);
	}
	public boolean free_memory()
	{
		if(body_array!=null) {
			for(int i=0,ni=body_number();i<ni;i++)
				if(body_array[i]!=null){
					body_array[i].destroy();
					body_array[i]=null;
				}
			body_array=null;
			return true;
		}
		return false;
	}
	public void destroy()
	{
		point_default_material=null;
		face_default_material=null;
		edge_default_material=null;
		point_default_vertex_extra_String=null;
		face_default_vertex_extra_string=null;
		face_default_normal_extra_string=null;
		edge_default_vertex_extra_string=null;
		face_default_attribute_double=null;
		face_default_attribute_string=null;
		part_box=null;
		
		free_memory();
	}
	
	public String point_default_material[];
	public String face_default_material[];
	public String edge_default_material[];
	
	public String point_default_vertex_extra_String;
	public String face_default_vertex_extra_string;
	public String face_default_normal_extra_string;
	public String edge_default_vertex_extra_string;
	
	public double face_default_attribute_double[];
	public String face_default_attribute_string[];

	public body body_array[];
	public box part_box;
	public int total_face_primitive_number,total_edge_primitive_number,total_point_primitive_number;
	
	public int body_number()
	{
		return (body_array==null)?0:body_array.length;
	}
	public int max_attribute_number()
	{
		return (face_default_attribute_string==null)?0:(face_default_attribute_string.length);
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
		point_default_material			=s.point_default_material;
		face_default_material			=s.face_default_material;
		edge_default_material			=s.edge_default_material;
		
		point_default_vertex_extra_String=s.point_default_vertex_extra_String;
		face_default_vertex_extra_string=s.face_default_vertex_extra_string;
		face_default_normal_extra_string=s.face_default_normal_extra_string;
		edge_default_vertex_extra_string=s.edge_default_vertex_extra_string;
		
		face_default_attribute_double	=s.face_default_attribute_double;
		face_default_attribute_string	=s.face_default_attribute_string;
		
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

		point_default_material=new String[4];
		for(int i=0,ni=point_default_material.length;i<ni;i++)
			if((point_default_material[i]=fr.get_string())==null)
				point_default_material[i]=default_value;
		
		face_default_material=new String[4];
		for(int i=0,ni=face_default_material.length;i<ni;i++)
			if((face_default_material[i]=fr.get_string())==null)
				face_default_material[i]=default_value;
		
		edge_default_material=new String[4];
		for(int i=0,ni=edge_default_material.length;i<ni;i++)
			if((edge_default_material[i]=fr.get_string())==null)
				edge_default_material[i]=default_value;
		
		if((point_default_vertex_extra_String=fr.get_string())==null)
			point_default_vertex_extra_String="1";
		if((face_default_vertex_extra_string=fr.get_string())==null)
			face_default_vertex_extra_string="1";
		if((face_default_normal_extra_string=fr.get_string())==null)
			face_default_normal_extra_string="1";
		if((edge_default_vertex_extra_string=fr.get_string())==null)
			edge_default_vertex_extra_string="1";
		
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
		
		switch(version_string) {
		case "simple":
			{
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
			}
			break;
		default:
			{
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
			break;
		}
		
		return;
	}
	public part_rude(part_rude pr,int my_box_number,part my_reference_part[],location my_box_loca[],box my_box_array[])
	{
		point_default_material			=pr.point_default_material;
		face_default_material			=pr.face_default_material;
		edge_default_material			=pr.edge_default_material;
		
		point_default_vertex_extra_String=pr.point_default_vertex_extra_String;
		face_default_vertex_extra_string =pr.face_default_vertex_extra_string;
		face_default_normal_extra_string =pr.face_default_normal_extra_string;
		edge_default_vertex_extra_string =pr.edge_default_vertex_extra_string;
		
		face_default_attribute_double	=pr.face_default_attribute_double;
		face_default_attribute_string	=pr.face_default_attribute_string;
		
		for(int i=0;i<my_box_number;i++) 
			if((pr=my_reference_part[i].part_mesh)!=null){
				if(face_default_attribute_string.length<pr.face_default_attribute_string.length) {
					String bak[]=face_default_attribute_string;
					face_default_attribute_string=new String[pr.face_default_attribute_string.length];
					for(int j=0,nj=bak.length;j<nj;j++)
						face_default_attribute_string[j]=bak[j];
					for(int j=bak.length,nj=pr.face_default_attribute_string.length;j<nj;j++)
						face_default_attribute_string[j]=pr.face_default_attribute_string[j];
				}
				if(face_default_attribute_double.length<pr.face_default_attribute_double.length) {
					double bak[]=face_default_attribute_double;
					face_default_attribute_double=new double[pr.face_default_attribute_double.length];
					for(int j=0,nj=bak.length;j<nj;j++)
						face_default_attribute_double[j]=bak[j];
					for(int j=bak.length,nj=pr.face_default_attribute_double.length;j<nj;j++)
						face_default_attribute_double[j]=pr.face_default_attribute_double[j];
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
						+point_default_material[0]+"	"+point_default_material[1]+"	"
						+point_default_material[2]+"	"+point_default_material[3],
				"/*	face_default material					*/	"
						+face_default_material[0]+"	"+face_default_material[1]+"	"
						+face_default_material[2]+"	"+face_default_material[3],
				"/*	edge_default material					*/	"
						+edge_default_material[0]+"	"+edge_default_material[1]+"	"
						+edge_default_material[2]+"	"+edge_default_material[3],
				"/*	point  vertex_location_extra_data	*/	"	 +point_default_vertex_extra_String,
				"/*	face_default vertex_location_extra_data	*/	"+face_default_vertex_extra_string,
				"/*	face_default vertex_normal_extra_data	*/	"+face_default_normal_extra_string,
				"/*	edge_default vertex_location_extra_data	*/	"+edge_default_vertex_extra_string,
				"",
				"/*	face_max_attribute_number				*/	"+face_default_attribute_string.length
		};
		
		for(int i=0,ni=str.length;i<ni;i++)
			fw.println(str[i]);
		for(int i=0,j=0,ni=face_default_attribute_string.length;i<ni;i++) {
			fw.print  ("/*		"+i+".attribute:					*/	",
								face_default_attribute_double[j++]);
			fw.print  ("	",	face_default_attribute_double[j++]);
			fw.print  ("	",	face_default_attribute_double[j++]);
			fw.println("	",	face_default_attribute_double[i]);
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
