package old_convert;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_transformation.point;

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
	private void write_out_mesh(file_writer f)
	{
		final String str[]={
			"/* version          */     2019.08.12",
			"/* Origin material  */     0  0  0  1",
			"/* body_number      */     1",
			"/*    body  0  name   */  obj_object_body_0",
			"/*     face_number          */  1",
			"/*        face  0  name   */  no_face_name",
			"/*          face_type   */  unknown  /*  parameter_number  */  0  /*  parameter */ ",
			"/*          face_attribute_number   */ 1",
			""
		};
		
		for(int i=0,ni=str.length;i<ni;i++)
			f.println(str[i]);	
		
		f.print("/*\t\t\tface_vertex_number\t*/\t");
		f.println(v_number);
		for(int i=0;i<v_number;i++){
			f.print("/*\t\t\t\tface_vertex\t");
			f.print(i);	
			f.print("\tis\t*/\t");	f.print(v[i].x);
			f.print("\t");			f.print(v[i].y);
			f.print("\t");			f.print(v[i].z);
			f.println("\t1");
		}
		f.println();

		f.print("/*\t\t\tface_normal_number\t*/\t");
		f.println(vn_number);
		for(int i=0;i<vn_number;i++){
			f.print("/*\t\t\t\tface_normal\t");
			f.print(i);	
			f.print("\tis\t*/\t");	f.print(vn[i].x);
			f.print("\t");			f.print(vn[i].y);
			f.print("\t");			f.print(vn[i].z);
			f.println("\t1");
		}
		f.println();

		f.print("/*\t\t\tface_texture_number\t*/\t");
		f.println(vt_number);
		for(int i=0;i<vt_number;i++){
			f.print("/*\t\t\t\tface_texture_vertex\t");
			f.print(i);	
			f.print("\tis\t*/\t");	f.print(vt[i].x);
			f.print("\t");			f.print(vt[i].y);
			f.print("\t");			f.print(vt[i].z);
			f.println("\t1");
		}
		f.println();

		f.println("/*\t\t\tface_primitive_number\t*/\t",f_number);
		
		for(int i=0;i<f_number;i++){
			double z1=vt[f_index[i][1][2]].z;
			double z2=vt[f_index[i][2][2]].z;
			double z3=vt[f_index[i][3][2]].z;
			
			String my_material_str="0";
			for(int j=0,nj=f_index[i][0].length;j<nj;j++){
				int my_material_id=f_index[i][0][j];
				my_material_str=material_name[my_material_id][3];
				double min_z=Double.parseDouble(material_name[my_material_id][4]);
				double max_z=Double.parseDouble(material_name[my_material_id][5]);
				if((z1>=min_z)&&(z1<max_z))
					if((z2>=min_z)&&(z2<max_z))
						if((z3>=min_z)&&(z3<max_z))
							break;
			}

			f.print  ("/*\t\t\t\tface_primitive\t",i);
			f.print  ("\tmaterial\t\t*/\t");
			f.print  ("\t",0);
			f.print  ("\t",0);
			f.print  ("\t",0);
			f.println("\t",my_material_str);

			f.print  ("/*\t\t\t\tface_primitive\t",i);
			f.print  ("\tvertex_index\t*/\t");
			f.print  ("\t",f_index[i][1][0]);
			f.print  ("\t",f_index[i][2][0]);
			f.print  ("\t",f_index[i][3][0]);
			f.println("\t-1");
		
			f.print  ("/*\t\t\t\tface_primitive\t",i);
			f.print  ("\tnormal_index\t*/\t");
			f.print  ("\t",f_index[i][1][1]);
			f.print  ("\t",f_index[i][2][1]);
			f.print  ("\t",f_index[i][3][1]);
			f.println("\t-1");
			
			f.print  ("/*\t\t\t\tface_primitive\t",i);
			f.print  ("\ttexture_index\t*/\t");
			
			f.print  ("\t",f_index[i][1][2]);
			f.print  ("\t",f_index[i][2][2]);
			f.print  ("\t",f_index[i][3][2]);
			f.println("\t-1");
			
			f.println();
		}
		f.println();
	}
	private void write_out_line_segment(file_writer f)
	{
		f.println("/*            face_loop_edge  data:segment   */");   
		f.println("/*                curve_type    */    segment");
		f.println("/*                parameter_number  */  0 /* parameter */");
		f.println("/*                material */\t0\t0\t0\t1");
		f.println("                  start_not_effective");
		f.println("                  end_not_effective");

		f.println("/*                edge_vertex_number  */  ",l_number+l_number);
		
		for(int i=0,id=0;i<l_number;i++){
			int my_material_id=l_index[i][0][0];
			
			String material_str[]=material_name[my_material_id];
			
			for(int j=1;j<=2;i++){
				point p=v[l_index[i][j][0]];
				f.print  ("/*\t\t\t\tedge_vertex\t",id++);
				f.print  ("\tlocation\t*/\t",		p.x);
				f.print  ("\t",						p.y);
				f.print  ("\t",						p.z);
				f.print  ("\t",						1);
				f.println("\t/*\tmaterial\t*/\t0\t0\t0\t",material_str[3]);
			}
		}
	}
	private void write_out_point(file_writer f)
	{
		f.println("/*            face_loop_edge  data:point   */");   
		f.println("/*                curve_type    */    point");
		f.println("/*                parameter_number  */  0 /* parameter */");
		f.println("/*                material */\t0\t0\t0\t1");
		f.println("                  start_not_effective");
		f.println("                  end_not_effective");

		f.println("/*                edge_vertex_number  */  ",p_number);
		
		for(int i=0;i<p_number;i++){
			int my_material_id=p_index[i][0][0];
			String material_str[]=material_name[my_material_id];
			point p=v[l_index[i][1][0]];
			
			f.print  ("/*\t\t\t\tedge_vertex\t",i);
			f.print  ("\tlocation\t*/\t",		p.x);
			f.print  ("\t",						p.y);
			f.print  ("\t",						p.z);
			f.print  ("\t",						1);
			f.println("\t/*\tmaterial\t*/\t0\t0\t0\t",	material_str[3]);
		}
	}
	public obj_converter(String mesh_file_name,String mesh_charset,String target_file_name,String target_charset)
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
		
		file_writer fw=new file_writer(target_file_name,target_charset);
		write_out_mesh(fw);
		
		fw.println("/*        face_loop_number    */    1");
		fw.println("/*        	face_loop_edge_number  */    2");
		write_out_line_segment(fw);
		write_out_point(fw);
		
		fw.close();
	}
}
