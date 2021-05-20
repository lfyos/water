package kernel_part;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_mesh.auxiliary_file_handler;
import kernel_transformation.box;
import kernel_transformation.location;

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
	
	private void part_clear()
	{
		body_array=null;

		rp_box=null;
		box_material=new String[]{"0","0","0","0"};
		
		total_primitive_number=0;
		
		box_attribute_double=new double[] {};
		box_attribute_string=new String[] {};
	}
	public part_rude(file_reader fr,double vertex_scale_value,double normal_scale_value,
			boolean delete_redundant_data_flag,boolean combine_flag,boolean create_normal_flag)
	{
		String str;
		
		part_clear();
		
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
	
	public part_rude(int box_number,location loca[],box b[],String material_id[][],
			String my_default_origin_material[],
			double parent_box_attribute_double[],String parent_box_attribute_string[])
	{	
		part_clear();
		
		origin_material=my_default_origin_material;
		body_array=new body[] {new body(box_number,loca,b,material_id)};
			
		caculate_box();
		caculate_triangle_number();

		for(int i=0,ni=body_number();i<ni;i++)
			for(int j=0,nj=body_array[i].face_number();j<nj;j++) {
				face_face ff=body_array[i].face_array[j].fa_face;
				for(int k=ff.attribute_number,nk=parent_box_attribute_string.length;k<nk;k++) {
					ff.add_attribute(
							parent_box_attribute_double[3*k+0],
							parent_box_attribute_double[3*k+1],
							parent_box_attribute_double[3*k+2],
							parent_box_attribute_string[  k  ]);
				}
			}
		caculate_box_attribute();
	}
	public part_rude(part_rude s)
	{
		origin_material			=s.origin_material;
		
		body_array=null;
		if(s.body_number()>0){
			body_array=new body[s.body_array.length];
			for(int i=0,ni=body_array.length;i<ni;i++)
				body_array[i]=new body(s.body_array[i]);
		}
		rp_box=null;
		if(s.rp_box!=null)
			rp_box=new box(s.rp_box);
		
		box_material=new String[s.box_material.length];
		for(int i=0,ni=box_material.length;i<ni;i++)
			box_material[i]=s.box_material[i];
		
		total_primitive_number=s.total_primitive_number;

		box_attribute_double=s.box_attribute_double;
		box_attribute_string=s.box_attribute_string;
	}
	public void free_memory(boolean only_free_vertex_memory_flag)
	{
		if(only_free_vertex_memory_flag)
			for(int body_id=0,my_body_number=body_number();body_id<my_body_number;body_id++)
				body_array[body_id].free_memory();
		else{
			for(int body_id=0,my_body_number=body_number();body_id<my_body_number;body_id++)
				body_array[body_id].destroy();
			body_array=null;
		}
	}
	public void write_out(
			auxiliary_file_handler auxiliary_f,file_writer f,double scale_value,
			boolean only_free_vertex_memory_flag,boolean simple_flag,
			boolean write_out_face_flag,boolean write_out_curve_flag)
	{
		f.println("/*	version	*/	2019.08.05");
		f.print  ("/*	origin material	*/");
		for(int i=0,ni=origin_material.length;i<ni;i++)
			f.print("	",origin_material[i]);
		f.println();

		if(only_free_vertex_memory_flag){
			f.println("/*	body_number	*/	",body_number());
			for(int i=0,ni=body_number();i<ni;i++){
				body b=body_array[i];
				f.set_pace(2);
				{
					f.print(" /* body  ");		f.print(i);					f.print("  name   */  ");
				}
				f.print(b.name);	
				f.print("   /*   face_number   */  ");		
				f.println(b.face_number());		f.println();
				for(int j=0,nj=b.face_number();j<nj;j++){
					face fa=b.face_array[j];
					f.set_pace(4);
					{
						f.print(" /* face  ");		f.print(j);				f.print("  name   */  ");
					}
					f.println(fa.name);
					new face_write_out(auxiliary_f,fa,f,scale_value,
							simple_flag,write_out_face_flag,write_out_curve_flag,i,j);
				}
			}
			f.println();
		}else {
			if(rp_box==null)
				f.println("/*	simplest part box	*/	nobox");
			else{
				f.print  ("/*	simplest part box	*/	box	");
				rp_box.write_out(f);
				f.println();
			}
			f.print  ("/*	box_material	*/	",box_material[0]);
			f.print  ("	",box_material[1]);
			f.print  ("	",box_material[2]);
			f.println("	",box_material[3]);
			f.println("/*	total_primitive_number	*/	",total_primitive_number);
		}
		
		if(simple_flag) {
			f.println("simple	",box_attribute_string.length);
			for(int i=0,j=0,ni=box_attribute_string.length;i<ni;i++){
				f.print  ("	",box_attribute_double[j++]);
				f.print  ("	",box_attribute_double[j++]);
				f.print  ("	",box_attribute_double[j++]);
				f.println("	",box_attribute_string[i  ]);
			}
			f.println();
		}
	}
}
