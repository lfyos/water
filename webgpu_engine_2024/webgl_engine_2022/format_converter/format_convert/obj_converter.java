package format_convert;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_transformation.point;
import kernel_transformation.box;

public class obj_converter
{
	private point v[],vn[],vt[];
	private int v_number,vn_number,vt_number;
	
	private int f_number,f_index[][][];
	private int l_number,l_index[][][];
	private int p_number,p_index[][][];
	
	private boolean map_update_flag;
	private int material_id[];
	private String material_name[][];
	private String current_group,current_usemtl,current_mtllib;

	private void caculate_material_id()
	{
		material_id=new int[0];
		for(int i=0,ni=material_name.length;i<ni;i++)
			if(material_name[i][0].compareTo(current_group)==0)
				if(material_name[i][1].compareTo(current_usemtl)==0)
					if(material_name[i][2].compareTo(current_mtllib)==0){
						int bak[]=material_id;
						material_id=new int[material_id.length+1];
						for(int j=0,nj=bak.length;j<nj;j++)
							material_id[j]=bak[j];
						material_id[bak.length]=i;
					}
		if(material_id.length>0)
			return;
		
		String bak[][]=material_name;
		material_name=new String[bak.length+1][];
		for(int i=0,ni=bak.length;i<ni;i++)
			material_name[i]=bak[i];
		material_name[material_name.length-1]=new String[]
				{
						current_group,current_usemtl,current_mtllib,"0","0","1"
				};
		material_id=new int[]{material_name.length-1};
		map_update_flag=true;
		return;
	}
	private point input_point(file_reader fr)
	{
		String str=fr.get_line().replace('\t',' ').trim();
		int index_id=str.indexOf(' ');
		if(index_id<0)
			return new point(Double.parseDouble(str),0,0);
		
		double x=Double.parseDouble(str.substring(0,index_id));
		str=str.substring(index_id+1).trim();
		if((index_id=str.indexOf(' '))<0)
			return new point(x,Double.parseDouble(str),0);
		
		double y=Double.parseDouble(str.substring(0,index_id));
		str=str.substring(index_id+1).trim();
		if((index_id=str.indexOf(' '))<0)
			return new point(x,y,Double.parseDouble(str));
		double z=Double.parseDouble(str.substring(0,index_id));
		str=str.substring(index_id+1).trim();
		
		return new point(x,y,z,Double.parseDouble(str));
	}
	private void input_v(file_reader fr)
	{
		if(v.length<=v_number){
			point bak[]=v;
			v=new point[v_number+1000];
			for(int i=0,ni=bak.length;i<ni;i++)
				v[i]=bak[i];
		}
		v[v_number++]=input_point(fr);
	}
	private void input_vn(file_reader fr)
	{
		if(vn.length<=vn_number){
			point bak[]=vn;
			vn=new point[vn_number+1000];
			for(int i=0,ni=bak.length;i<ni;i++)
				vn[i]=bak[i];
		}
		vn[vn_number++]=input_point(fr);
	}
	private void input_vt(file_reader fr)
	{
		if(vt.length<=vt_number){
			point bak[]=vt;
			vt=new point[vt_number+1000];
			for(int i=0,ni=bak.length;i<ni;i++)
				vt[i]=bak[i];
		}
		vt[vt_number++]=input_point(fr);
	}
	private void input_f(file_reader fr)
	{
		if(f_index.length<=f_number){
			int bak[][][]=f_index;
			f_index=new int[bak.length+1000][][];
			for(int i=0,ni=bak.length;i<ni;i++)
				f_index[i]=bak[i];
		}
		f_index[f_number]=new int[4][];
		f_index[f_number][0]=material_id;

		for(int i=1;i<=3;i++){
			int index_id,v_index,vn_index,vt_index;
			String str=fr.get_string();
			
			index_id=str.indexOf('/');
			v_index =Integer.decode(str.substring(0,index_id));
			str=str.substring(index_id+1);
			if((index_id=str.indexOf('/'))>0){
				vt_index=Integer.decode(str.substring(0,index_id));	
				vn_index=Integer.decode(str.substring(index_id+1));
			}else{
				vt_index=0;
				vn_index=Integer.decode(str.substring(1));
			}
			f_index[f_number][i]=new int[]
			{
				(( v_index>0)?( v_index-1):( v_index<0)?( v_number+ v_index):0)%(( v_number>0)? v_number:1),
				((vn_index>0)?(vn_index-1):(vn_index<0)?(vn_number+vn_index):0)%((vn_number>0)?vn_number:1),
				((vt_index>0)?(vt_index-1):(vt_index<0)?(vt_number+vt_index):0)%((vt_number>0)?vt_number:1)
			};
		}
		f_number++;
	}
	private void input_l(file_reader fr)
	{
		if(l_index.length<=l_number){
			int bak[][][]=l_index;
			l_index=new int[bak.length+1000][][];
			for(int i=0,ni=bak.length;i<ni;i++)
				l_index[i]=bak[i];
		}
		l_index[l_number]=new int[3][];
		l_index[l_number][0]=material_id;
		
		
		for(int i=1;i<=2;i++){
			int index_id,v_index,vn_index,vt_index;
			String str=fr.get_string();
			
			index_id=str.indexOf('/');
			v_index =Integer.decode(str.substring(0,index_id));
			str=str.substring(index_id+1);
			index_id=str.indexOf('/');
			vt_index=Integer.decode(str.substring(0,index_id));	
			vn_index=Integer.decode(str.substring(index_id+1));
			
			l_index[l_number][i]=new int[]
			{
				(( v_index>0)?( v_index-1):( v_index<0)?( v_number+ v_index):0)%(( v_number>0)? v_number:1),
				((vn_index>0)?(vn_index-1):(vn_index<0)?(vn_number+vn_index):0)%((vn_number>0)?vn_number:1),
				((vt_index>0)?(vt_index-1):(vt_index<0)?(vt_number+vt_index):0)%((vt_number>0)?vt_number:1)
			};
		}
		l_number++;
	}
	private void input_p(file_reader fr)
	{
		if(p_index.length<=p_number){
			int bak[][][]=p_index;
			p_index=new int[bak.length+1000][][];
			for(int i=0,ni=bak.length;i<ni;i++)
				p_index[i]=bak[i];
		}
		p_index[p_number]=new int[2][];
		p_index[p_number][0]=material_id;
		
		int index_id,v_index,vn_index,vt_index;
		String str=fr.get_string();
		
		index_id=str.indexOf('/');
		v_index =Integer.decode(str.substring(0,index_id));
		str=str.substring(index_id+1);
		index_id=str.indexOf('/');
		vt_index=Integer.decode(str.substring(0,index_id));	
		vn_index=Integer.decode(str.substring(index_id+1));
		
		p_index[p_number][1]=new int[]
		{
			(( v_index>0)?( v_index-1):( v_index<0)?( v_number+ v_index):0)%(( v_number>0)? v_number:1),
			((vn_index>0)?(vn_index-1):(vn_index<0)?(vn_number+vn_index):0)%((vn_number>0)?vn_number:1),
			((vt_index>0)?(vt_index-1):(vt_index<0)?(vt_number+vt_index):0)%((vt_number>0)?vt_number:1)
		};
		p_number++;
	}
	private void load_map(String file_name_only,String file_system_charset)
	{
		for(file_reader fr=new file_reader(file_name_only+".map",file_system_charset);;){
			String my_current_group		=fr.get_string();
			String my_current_usemtl	=fr.get_string();
			String my_current_mtllib	=fr.get_string();
			String material_id			=fr.get_string();
			if(material_id==null)
				if(fr.eof()){
					fr.close();
					break;
				}
			String min_texture_z		=fr.get_string();
			String max_texture_z		=fr.get_string();
			
			String bak[][]=material_name;
			material_name=new String[bak.length+1][];
			for(int i=0,ni=bak.length;i<ni;i++)
				material_name[i]=bak[i];
		
			material_name[material_name.length-1]=new String[]{
					my_current_group,my_current_usemtl,my_current_mtllib,
					material_id,min_texture_z,max_texture_z};
		}
	}
	private void load_mesh(String file_name_only,String file_system_charset)
	{
		for(file_reader fr=new file_reader(file_name_only+".obj",file_system_charset);;){
			String str=fr.get_string();
			if(fr.eof()){
				fr.close();
				return;
			}
			switch(str.toLowerCase().trim()){
			default:
				fr.get_line();break;
			case "v":
				input_v(fr);break;
			case "vn":
				input_vn(fr);break;
			case "vt":
				input_vt(fr);break;
			case "f":
				input_f(fr);
				break;
			case "l":
				input_l(fr);
				break;
			case "p":
				input_p(fr);
				break;
			case "g":
				current_group=fr.get_line().trim().
					replace(' ','_'). replace('\t','_').
					replace('\\','/').replace("\"","");
				caculate_material_id();
				break;
			case "usemtl":
				current_usemtl=fr.get_string();
				caculate_material_id();
				break;
			case "mtllib":
				current_mtllib=fr.get_string();
				caculate_material_id();
				break;
			}
		}
	}
	private void write_out_material(String file_name_only,String file_system_charset)
	{
		file_writer fw;
		
		if(map_update_flag){
			fw=new file_writer(file_name_only+".map",file_system_charset);
			for(int i=0,ni=material_name.length;i<ni;i++){
				for(int j=0,nj=material_name[i].length;j<nj;j++)
					fw.print((j==0)?"":"\t\t\t",material_name[i][j]);
				fw.println();
			}
			fw.close();
		}
	}
	
	private void write_out_mesh(String target_file_name,String target_charset)
	{
		String str[]= {
				"/*	version					*/	2021.07.15",
				"/*	origin material			*/	0	0	0	"+face_material,
				"/*	default material		*/	0	0	0	"+face_material,
				"/*	origin  vertex_location_extra_data	*/	1",
				"/*	default vertex_location_extra_data	*/	1",
				"/*	default vertex_normal_extra_data	*/	1",
				"/*	max_attribute_number	*/	1",
				"	"+face_attribute.x+"	"+face_attribute.y+"	"+face_attribute.z+"	1",

				"/*	body_number	*/	1",
				"	/* body  0  name   */  obj_object_body		/*   face_number   */  1",
				"		/* face  0  name   */	obj_object_face",
				"			/* face_type   */	unknown  		/*   parameter_number   */  0 /*   parameter  */",
				"			/* total_face_primitive_number		*/	"+f_number,
				"			/* face_attribute_number 			*/	1",
				"			/* face_face_box					*/	"+((face_box==null)?"nobox":(
								face_box.p[0].x+"	"+face_box.p[0].y+"	"+face_box.p[0].z+"	"+
								face_box.p[1].x+"	"+face_box.p[1].y+"	"+face_box.p[1].z)),
				"",
				
				"			/* face_loop_number						*/	1",
				"			/* face_loop  0  loop_edge_number		*/	2",
				"",
				"				/*	face_loop_edge  0  data				*/",
				"				/*	curve_type		*/	segment		/*	parameter_number	*/	0	/*	parameter	*/",
				"				start_not_effective",
				"				end_not_effective",
				
				"				/*  parameter_point_extra_data			*/		1",
				"				/*	parameter_point_material			*/  	0    0    0    "+edge_material,

				"				/*	box definition						*/		"+((edge_box==null)?"nobox":(
									edge_box.p[0].x+"	"+edge_box.p[0].y+"	"+edge_box.p[0].z+"	"+
									edge_box.p[1].x+"	"+edge_box.p[1].y+"	"+edge_box.p[1].z)),
				"				/*	total_edge_primitive_number			*/		"+(2*l_number),
				"				/*	total_point_primitive_number		*/		0",
				
				"",
				"				/*	face_loop_edge  0  data				*/",
				"				/*	curve_type	*/  render_point_set	/* parameter_number  */  0 /* parameter */",
				"				start_not_effective",
				"				end_not_effective",
				
				"				/*  curve_point_extra_data				*/		1",
				"				/*	curve point material				*/      0    0    0    "+point_material,
				"				/*	box definition						*/		"+((point_box==null)?"nobox":(
										point_box.p[0].x+"	"+point_box.p[0].y+"	"+point_box.p[0].z+"	"+
										point_box.p[1].x+"	"+point_box.p[1].y+"	"+point_box.p[1].z)),
				"				/*	total_edge_primitive_number			*/		0",
				"				/*	total_point_primitive_number		*/		"+p_number,
				"",
				""
		};
		
		file_writer f=new file_writer(target_file_name,target_charset);
		for(int i=0,ni=str.length;i<ni;i++)
			f.println(str[i]);
		f.close();
	}
	
	private box face_box;
	private point face_attribute;
	private String face_material;
	private void write_out_mesh_face(String target_file_name,String target_charset)
	{
		face_box=null;
		face_attribute=null;
		face_material="1";
		
		file_writer.file_delete(target_file_name+".face");
		file_writer.file_delete(target_file_name+".face.gzip");
		
		if(f_number<=0) {
			face_attribute=new point();
			return;
		}

		int material_reference_number[]=new int[(material_name==null)?0:material_name.length];
		for(int i=0,ni=material_reference_number.length;i<ni;i++)
			material_reference_number[i]=0;
		
		file_writer f=new file_writer(target_file_name+".face",target_charset);
		for(int i=0;i<f_number;i++){
			double z1=vt[f_index[i][1][2]].z;
			double z2=vt[f_index[i][2][2]].z;
			double z3=vt[f_index[i][3][2]].z;
			
			int my_material_id=-1;
			for(int j=0,nj=f_index[i][0].length;j<nj;j++){
				my_material_id=f_index[i][0][j];
				double min_z=Double.parseDouble(material_name[my_material_id][4]);
				double max_z=Double.parseDouble(material_name[my_material_id][5]);
				if((z1>=min_z)&&(z1<max_z))
					if((z2>=min_z)&&(z2<max_z))
						if((z3>=min_z)&&(z3<max_z))
							break;
			}
			
			String my_material_str;
			
			if(my_material_id<0)
				my_material_str="0";
			else {
				my_material_str=material_name[my_material_id][3];
				material_reference_number[my_material_id]++;
			}
			
			f.println("/*	triangle "+i+" material	*/	0	0	0	",my_material_str);
			f.println("/*	vertex number			*/	3");
			
			for(int j=0;j<3;j++) {
				int len=f_index[i][j+1].length;
				point pp=(len>=1)?(v [f_index[i][j+1][0]]):(new point());
				point pn=(len>=2)?(vn[f_index[i][j+1][1]]):(new point());
				point pt=(len>=3)?(vt[f_index[i][j+1][2]]):(new point());
				
				f.println("	/*	"+j+".location	*/","	"+pp.x+"	"+pp.y+"	"+pp.z+"	1");
				f.println("	/*	"+j+".normal	*/","	"+pn.x+"	"+pn.y+"	"+pn.z+"	1");
				f.println("	/*	"+j+".texture	*/","	"+pt.x+"	"+pt.y+"	"+pt.z+"	1");
				
				if(face_box==null) {
					face_box=new box(pp);
					face_attribute=pt;
				}else {
					face_box=face_box.add(pp);
					face_attribute=face_attribute.add(pt);
				}
			}
			f.println();
		}
		f.println();
		f.close();
		
		if(face_attribute==null)
			face_attribute=new point();
		if(f_number>0)
			face_attribute=face_attribute.scale((1.0/3*f_number));
		
		if(material_reference_number.length>0){
			int max_index_id=0;
			for(int i=1,ni=material_reference_number.length;i<ni;i++)
				if(material_reference_number[i]>material_reference_number[max_index_id])
					max_index_id=i;
			face_material=material_name[max_index_id][3];
		}
	}
	private box edge_box;
	private String edge_material;
	private void write_out_edge(String target_file_name,String target_charset)
	{
		edge_box=null;
		edge_material="1";
		
		file_writer.file_delete(target_file_name+".edge");
		file_writer.file_delete(target_file_name+".edge.gzip");
		
		if(l_number<=0)
			return;

		int material_reference_number[]=new int[(material_name==null)?0:material_name.length];
		for(int i=0,ni=material_reference_number.length;i<ni;i++)
			material_reference_number[i]=0;
		
		file_writer f=new file_writer(target_file_name+".edge",target_charset);

		for(int i=0;i<l_number;i++){
			double z1=vt[l_index[i][1][2]].z;
			double z2=vt[l_index[i][2][2]].z;
			
			int my_material_id=-1;
			for(int j=0,nj=l_index[i][0].length;j<nj;j++){
				my_material_id=l_index[i][0][j];
				double min_z=Double.parseDouble(material_name[my_material_id][4]);
				double max_z=Double.parseDouble(material_name[my_material_id][5]);
				if((z1>=min_z)&&(z1<max_z))
					if((z2>=min_z)&&(z2<max_z))
						break;
			}
			String my_material_str;
			
			if(my_material_id<0)
				my_material_str="0";
			else {
				my_material_str=material_name[my_material_id][3];
				material_reference_number[my_material_id]++;
			}
			for(int j=0;j<2;j++) {
				int len=l_index[i][j+1].length;
				point pp=(len>=1)?(v [l_index[i][j+1][0]]):(new point());
				
				f.print  ("/*	edge "+i+"."+j+".location	*/","	"+pp.x+"	"+pp.y+"	"+pp.z+"	1	");
				f.println("/*	material	*/	0	0	0	",my_material_str);
				
				if(edge_box==null)
					edge_box=new box(pp);
				else
					edge_box=edge_box.add(pp);
			}
			f.println();
		}
		f.println();
		f.close();
		
		if(material_reference_number.length>0){
			int max_index_id=0;
			for(int i=1,ni=material_reference_number.length;i<ni;i++)
				if(material_reference_number[i]>material_reference_number[max_index_id])
					max_index_id=i;
			edge_material=material_name[max_index_id][3];
		}
	}
	private box point_box;
	private String point_material;
	
	private void write_out_point(String target_file_name,String target_charset)
	{
		point_box=null;
		point_material="1";
		
		file_writer.file_delete(target_file_name+".point");
		file_writer.file_delete(target_file_name+".point.gzip");
		
		if(p_number<=0)
			return;
		
		int material_reference_number[]=new int[(material_name==null)?0:material_name.length];
		for(int i=0,ni=material_reference_number.length;i<ni;i++)
			material_reference_number[i]=0;
		
		file_writer f=new file_writer(target_file_name+".point",target_charset);
		
		for(int i=0;i<p_number;i++){
			double z1=vt[p_index[i][1][2]].z;
			double z2=vt[p_index[i][2][2]].z;
			
			int my_material_id=-1;
			for(int j=0,nj=p_index[i][0].length;j<nj;j++){
				my_material_id=p_index[i][0][j];
				double min_z=Double.parseDouble(material_name[my_material_id][4]);
				double max_z=Double.parseDouble(material_name[my_material_id][5]);
				if((z1>=min_z)&&(z1<max_z))
					if((z2>=min_z)&&(z2<max_z))
						break;
			}
			String my_material_str;
			if(my_material_id<0)
				my_material_str="0";
			else {
				my_material_str=material_name[my_material_id][3];
				material_reference_number[my_material_id]++;
			}
			
			for(int j=0;j<1;j++) {
				int len=p_index[i][j+1].length;
				point pp=(len>=1)?(v [p_index[i][j+1][0]]):(new point());
				
				f.print  ("/*	point "+i+"."+j+".location	*/","	"+pp.x+"	"+pp.y+"	"+pp.z+"	1	");
				f.println("/*	material	*/	0	0	0	",my_material_str);
				
				if(point_box==null)
					point_box=new box(pp);
				else
					point_box=point_box.add(pp);
			}
			f.println();
		}
		f.println();
		f.close();
		
		if(material_reference_number.length>0){
			int max_index_id=0;
			for(int i=1,ni=material_reference_number.length;i<ni;i++)
				if(material_reference_number[i]>material_reference_number[max_index_id])
					max_index_id=i;
			point_material=material_name[max_index_id][3];
		}
	}
	
	public obj_converter(
			String mesh_file_name,	String mesh_charset,
			String target_file_name,String target_charset)
	{
		String file_name_only=mesh_file_name.substring(0,mesh_file_name.lastIndexOf('.'));
		
		v_number=0;
		v=new point[1000];
		
		vn_number=0;
		vn=new point[1000];
		
		vt_number=0;
		vt=new point[1000];
		
		f_number=0;
		f_index=new int[1000][][];
		
		l_number=0;
		l_index=new int[1000][][];
		
		p_number=0;
		p_index=new int[1000][][];
		
		map_update_flag=false;
		material_id=new int[]{0};
		material_name=new String[0][];
		
		load_map(file_name_only,mesh_charset);
		
		current_group="no_group";
		current_usemtl="no_usemtl";
		current_mtllib="no_mtllib";
		
		load_mesh(file_name_only,mesh_charset);
		write_out_material(file_name_only,target_charset);
		
		write_out_mesh_face	(target_file_name,target_charset);
		write_out_edge		(target_file_name,target_charset);
		write_out_point		(target_file_name,target_charset);
		write_out_mesh		(target_file_name,target_charset);
	}
	
	public static void main(String args[])
	{
		class traveller extends kernel_file_manager.travel_through_directory
		{
			private int number;
			
			public void operate_file(String file_name)
			{
				int index_id=file_name.lastIndexOf(".obj");
				if(index_id<0)
					return;
				if(file_name.substring(index_id).compareTo(".obj")!=0)
					return;

				file_writer.file_delete(file_name+".mesh");
				file_writer.file_delete(file_name+".mesh.face");
				file_writer.file_delete(file_name+".mesh.edge");
				file_writer.file_delete(file_name+".mesh.point");
				file_writer.file_delete(file_name+".mesh.face.gzip");
				file_writer.file_delete(file_name+".mesh.edge.gzip");
				file_writer.file_delete(file_name+".mesh.point.gzip");
				file_writer.file_delete(file_name+".mesh.lock");
				
				new obj_converter(file_name,"GBK",file_name+".mesh","GBK");
				
				debug_information.println((number++)+".finish: ",file_name);
			}
			
			public traveller()
			{
				number=0;
			}
		}
		
		debug_information.println("Begin");

		new traveller().do_travel("E:\\water_all\\data\\project\\part\\other_part\\part_obj\\", true);
		
		debug_information.println("End");
	}
}
