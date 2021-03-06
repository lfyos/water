package convert_old_part_rude_2021_07_01;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_transformation.box;

public class part_rude 
{
	static public final  String default_material[]=new String[] {"0","0","0","0"};
	public String origin_material[];
	
	public face_face max_attribute_face_face()
	{
		face f;
		face_face f_f=null;
		int max_attribute_number=0;
		for(int i=0,ni=body_number();i<ni;i++)
			if(body_array[i]!=null)
				for(int j=0,nj=body_array[i].face_number();j<nj;j++)	
					if((f=body_array[i].face_array[j])!=null) {
						int my_attribute_number=f.fa_face.attribute_number;
						if(f_f!=null)
							if(my_attribute_number<=max_attribute_number)
								continue;
						max_attribute_number=my_attribute_number;
						f_f=f.fa_face;
					}
		return f_f;
	}
	public void destroy()
	{
		origin_material=null;
		
		for(int i=0,ni=body_number();i<ni;i++)
			if(body_array[i]!=null){
				body_array[i].destroy();
				body_array[i]=null;
			}
		body_array=null;
		rp_box=null;
		box_material=null;
		
		box_attribute_double=null;
		box_attribute_string=null;
	}
	public body body_array[];
	public int body_number()
	{
		if(body_array==null)
			return 0;
		else
			return body_array.length;
	}
	public box rp_box;
	public String box_material[];
	public int total_primitive_number;
	
	public double box_attribute_double[];
	public String box_attribute_string[];

	private void caculate_box()
	{
		rp_box=null;
		box_material=new String[]{"0","0","0","0"};
		
		int my_body_number;
		if((my_body_number=body_number())<=0)
			return;
		
		body max_body=null;
		for(int i=0;i<my_body_number;i++)
			if(body_array[i].body_box!=null){
				if(rp_box==null)
					rp_box=new box(body_array[i].body_box);
				else
					rp_box=rp_box.add(body_array[i].body_box);
				if(max_body==null)
					max_body=body_array[i];
				else if(body_array[i].body_box.distance2()>max_body.body_box.distance2())
					max_body=body_array[i];
			}
		face max_fa=null,fa;
		if(max_body!=null)
			for(int i=0,ni=max_body.face_number();i<ni;i++)
				if((fa=max_body.face_array[i]).face_box!=null) {
					if(max_fa==null)
						max_fa=fa;
					else if(fa.face_box.distance2()>max_fa.face_box.distance2())
						max_fa=fa;
				}
		if(max_fa!=null)
			box_material=max_fa.fa_face.box_material;
	}
	private int caculate_triangle_number()
	{
		int my_body_number;
		
		if((my_body_number=body_number())<=0)
			return total_primitive_number;
		
		total_primitive_number=0;
		for(int i=0;i<my_body_number;i++)
			for(int j=0,nj=body_array[i].face_number();j<nj;j++)
				total_primitive_number+=body_array[i].face_array[j].fa_face.total_primitive_number;
		return total_primitive_number;
	}
	private void caculate_box_attribute()
	{
		body max_body=null;
		for(int i=0,ni=body_number();i<ni;i++)
			if(body_array[i].body_box!=null){
				if(max_body==null)
					max_body=body_array[i];
				else if(body_array[i].body_box.distance2()>max_body.body_box.distance2())
					max_body=body_array[i];
			}
		if(max_body==null)
			return;
		face max_fa=null,fa;
		for(int i=0,ni=max_body.face_number();i<ni;i++)
			if((fa=max_body.face_array[i]).face_box!=null) {
				if(max_fa==null)
					max_fa=fa;
				else if(fa.face_box.distance2()>max_fa.face_box.distance2())
					max_fa=fa;
			}
		if(max_fa!=null){
			box_attribute_string=max_fa.fa_face.box_attribute_string;
			box_attribute_double=max_fa.fa_face.box_attribute_double;
		}
	}
	
	public part_rude(file_reader fr,double vertex_scale_value,double normal_scale_value,
			boolean delete_redundant_data_flag,boolean combine_flag,boolean create_normal_flag)
	{
		String str;
		
		body_array=null;

		rp_box=null;
		box_material=new String[]{"0","0","0","0"};
		
		total_primitive_number=0;
		
		box_attribute_double=new double[] {};
		box_attribute_string=new String[] {};
		
		fr.get_string();	//version code 
		
		origin_material=new String[4];
		for(int i=0,ni=origin_material.length;i<ni;i++)
			origin_material[i]=fr.get_string();
		
		if((str=fr.get_string())==null)
			str="nobox";

		switch(str) {
		case "nobox":
			rp_box=null;
			box_material=new String[]{fr.get_string(),fr.get_string(),fr.get_string(),fr.get_string()};
			total_primitive_number=fr.get_int();
			break;
		case "box":
			rp_box=new box(fr);
			box_material=new String[] {fr.get_string(),fr.get_string(),fr.get_string(),fr.get_string()};
			total_primitive_number=fr.get_int();
			break;
		default:
			int my_body_number;
			if((my_body_number=Integer.decode(str))<=0)
				body_array=null;
			else{
				body_array=new body[my_body_number];
				for(int i=0;i<my_body_number;i++)
					body_array[i]=new body(fr,vertex_scale_value,normal_scale_value,
						delete_redundant_data_flag,combine_flag,create_normal_flag);
			}
			caculate_box();
			caculate_triangle_number();
			break;
		}
		
		if((str=fr.get_string())!=null)
			if(str.compareTo("simple")==0){
				box_attribute_string=new String[fr.get_int()];
				box_attribute_double=new double[box_attribute_string.length*3];
				for(int i=0,j=0,ni=box_attribute_string.length;i<ni;i++) {
					box_attribute_double[j++]=fr.get_double();
					box_attribute_double[j++]=fr.get_double();
					box_attribute_double[j++]=fr.get_double();
					box_attribute_string[i  ]=fr.get_string();
				}
				return;
			}
		caculate_box_attribute();
		return;
	}
	private void write_out_head(file_writer f)
	{
		f.println("/*	version					*/	2021.07.01");
		f.print  ("/*	origin material			*/");
		for(int i=0;i<4;i++)
			f.print("	",origin_material[i]);
		f.println();
		
		f.print  ("/*	default material		*/");
		for(int i=0;i<4;i++)
			f.print("	",box_material[i]);
		f.println();
		
		f.println("/*	max_attribute_number	*/	",box_attribute_string.length);
		for(int i=0,j=0,ni=box_attribute_string.length;i<ni;) {
			f.print  ("	",box_attribute_double[j++]);
			f.print  ("	",box_attribute_double[j++]);
			f.print  ("	",box_attribute_double[j++]);
			
			f.println("	",box_attribute_string[i++]);
		}
		f.println();
		
		f.print  ("/*	body_number	*/	",body_number());
		for(int i=0,ni=body_number();i<ni;i++){
			body b=body_array[i];
			f.set_pace(2);
			f.print(" /* body  ");
			f.print(i);
			f.print("  name   */  ");
			
			f.print(b.name);	
			f.print("   /*   face_number   */  ");		
			f.print(b.face_number());
			
			for(int j=0,nj=b.face_number();j<nj;j++){
				face fa=b.face_array[j];
				f.set_pace(4);
				f.print(" /* face  ");
				f.print(j);	
				f.print("  name   */  ");
				f.print(fa.name);
				
				new face_write_out(fa,f,i,j);
			}
		}
		f.println();
	}
	private void write_out_face(file_writer f,auxiliary_file_handler afh)
	{
		for(int body_id=0;body_id<body_number();body_id++)
			for(int face_id=0;face_id<body_array[body_id].face_number();face_id++){
				face_face ff=body_array[body_id].face_array[face_id].fa_face;
				face_mesh fm=ff.mesh;
				f.println("/*	body:"+body_id+"	face:"+face_id+"	*/");
				for(int primitive_id=0;primitive_id<ff.total_primitive_number;primitive_id++) {
					String material[]=fm.get_primitive_material(primitive_id,afh);
					int vertex_number=fm.get_primitive_vertex_number(primitive_id,afh);
					int attribute_number=fm.get_attribute_number();
					f.println("/*	material			*/	",material[0]+"	"+material[1]+"	"+material[2]+"	"+material[3]);
					f.println("/*	vertex number		*/	",vertex_number);
					for(int i=0;i<vertex_number;i++) {
						int index_id=fm.get_primitive_vertex_index(primitive_id, i, afh);
						double p[]=fm.get_vertex(index_id,afh);
						f.println("/*		"+i+".location		*/	",
								p[0]+"	"+p[1]+"	"+p[2]+"	"+fm.get_vertex_extra_data(index_id, afh));
						p=fm.get_normal(index_id,afh);
						f.println("/*		"+i+".normal		*/	",
								p[0]+"	"+p[1]+"	"+p[2]+"	"+fm.get_normal_extra_data(index_id, afh));
						for(int j=0;j<attribute_number;j++) {
							index_id=fm.get_primitive_attribute_index(primitive_id, i, j,afh);
							p=fm.get_attribute(index_id, j, afh);
							f.println("/*		"+i+".attribute:"+j+"	*/	",
								p[0]+"	"+p[1]+"	"+p[2]+"	"+fm.get_attribute_extra_data(index_id, j, afh));
						}
					}
				}
				
				f.println();
			}
		f.println();
	}
	private void write_out_edge(file_writer f,auxiliary_file_handler afh)
	{
		for(int body_id=0;body_id<body_number();body_id++)
			for(int face_id=0;face_id<body_array[body_id].face_number();face_id++)
				for(int loop_id=0;loop_id<body_array[body_id].face_array[face_id].fa_curve.face_loop_number();loop_id++)
					for(int edge_id=0;edge_id<body_array[body_id].face_array[face_id].fa_curve.f_loop[loop_id].edge_number();edge_id++){
						f.println("/*	body:"+body_id+"	face:"+face_id+"	loop_id:"+loop_id+"	edge_id:"+edge_id+"	*/");
						edge_tessellation e=body_array[body_id].face_array[face_id].fa_curve.f_loop[loop_id].edge[edge_id].edge;
						
						int tessellation_point_number=0;
						if(e!=null)
							tessellation_point_number=e.tessellation_point_number();
						for(int index_id=0;index_id<tessellation_point_number;index_id++) {
							kernel_transformation.point p=e.get_tessellation_point(index_id,afh);
							f.print  ("/*		"+index_id+".location	*/	",
									p.x+"	"+p.y+"	"+p.z+"	"+e.get_tessellation_extra_data(index_id,afh));
							String material[]=e.get_tessellation_material(index_id,afh);
							f.println("	/*	material	*/	",material[0]+"	"+material[1]+"	"+material[2]+"	"+material[3]);
						}
					}
		f.println();
	}
	
	public static void convert(String source_file_name,String destination_file_name,String file_charset)
	{
		file_reader fr=new file_reader(source_file_name,file_charset);
		auxiliary_file_handler afh=new auxiliary_file_handler(source_file_name,file_charset);
		file_writer head_fw=new file_writer(destination_file_name,file_charset);
		file_writer face_fw=new file_writer(destination_file_name+".face",file_charset);
		file_writer edge_fw=new file_writer(destination_file_name+".edge",file_charset);
		
		part_rude pr=new part_rude(fr,1,1,false,false,false);
		pr.write_out_head(head_fw);
		pr.write_out_face(face_fw,afh);
		pr.write_out_edge(edge_fw,afh);
		pr.destroy();
		
		afh.destroy();
		fr.close();
		head_fw.close();
		face_fw.close();
		edge_fw.close();
	}
}
