package format_convert;

import java.util.Date;

import kernel_common_class.const_value;
import kernel_common_class.debug_information;
import kernel_common_class.format_change;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_transformation.plane;
import kernel_transformation.point;
import kernel_transformation.box;

public class inp_converter
{
	private int vertex_number,unit_number,attribute_number;
	
	private double vertex_location_data[][],vertex_normal_data[][];
	private int vertex_id_map[],unit_data[][];
	
	private double vertex_attribute_data[][];
	private String attribute_name[],attribute_unit[];
	private double attribute_min_value[],attribute_max_value[];
	
	private void input_vertex_location(file_reader fr)
	{
		vertex_id_map=new int[vertex_number];
		for(int i=0,ni=vertex_id_map.length;i<ni;i++)
			vertex_id_map[i]=-1;

		vertex_location_data=new double[vertex_number][];
		for(int i=0;i<vertex_number;i++){
			int vertex_id=fr.get_int();
			if(vertex_id_map.length<=vertex_id){
				int bak[]=vertex_id_map;
				vertex_id_map=new int[vertex_id+1000];
				for(int j=0,nj=bak.length;j<nj;j++)
					vertex_id_map[j]=bak[j];
				for(int j=bak.length,nj=vertex_id_map.length;j<nj;j++)
					vertex_id_map[j]=-1;
			}
			vertex_id_map[vertex_id]=i;
			vertex_location_data[i]=new double[]{fr.get_double(),fr.get_double(),fr.get_double(),vertex_id};
		}
	}
	private void input_unit(file_reader fr)
	{
		unit_data=new int[unit_number][];
		for(int i=0;i<unit_number;i++){
			int unit_id=fr.get_int(),material_id=fr.get_int(),unit_type=-1;
			String type_str;

			switch(type_str=fr.get_string()){
			case "hex":		//六面体 hexahedron
				unit_type=0;unit_data[i]=new int[3+8];
				break;
			case "tri":		//三角形
				unit_type=1;unit_data[i]=new int[3+3];
				break;
			case "prism":	//三棱柱 prism
				unit_type=2;unit_data[i]=new int[3+6];
				break;
			case "tet":		//四面体 tetrahedron
				unit_type=3;unit_data[i]=new int[3+4];
				break;
			case "pyr":		//金字塔 Pyramid
				unit_type=4;unit_data[i]=new int[3+5];
				break;
			case "quad":	//四边形
				unit_type=5;unit_data[i]=new int[3+4];
				break;
			case "line":	//线段
			case "pt":		//点
			default:
				fr.get_line();
				unit_type=-1;unit_data[i]=new int[3+0];
				
				debug_information.println("Find unsupport unt type:	",type_str);
				
				break;
			};
			unit_data[i][0]=unit_id;
			unit_data[i][1]=unit_type;
			unit_data[i][2]=material_id;
			
			for(int j=3,nj=unit_data[i].length;j<nj;j++)
				unit_data[i][j]=fr.get_int();
		}
		unit_number=0;
		for(int i=0,ni=unit_data.length;i<ni;i++){
			if(unit_data[i][1]<0)
				unit_data[i]=null;
			else
				for(int vertex_id,j=3,nj=unit_data[i].length;j<nj;j++){
					if((vertex_id=unit_data[i][j])>=0)
						if(vertex_id<vertex_id_map.length)
							if((vertex_id=vertex_id_map[vertex_id])>=0)
								if(vertex_id<vertex_location_data.length)
									continue;
					unit_data[i]=null;
					break;
				}
			if(unit_data[i]!=null)
				unit_data[unit_number++]=unit_data[i];
		}
		int bak[][]=unit_data;
		unit_data=new int[unit_number][];
		for(int i=0;i<unit_number;i++)
			unit_data[i]=bak[i];
	}
	private void input_vertex_attribute(file_reader fr)
	{
		fr.get_no_empty_line();
		
		attribute_name=new String[attribute_number];
		attribute_unit=new String[attribute_number];
		
		for(int i=0;i<attribute_number;i++){
			String str=fr.get_no_empty_line();
			int index_id=str.indexOf(',');
			if(index_id<0){
				attribute_name[i]=str.trim();
				attribute_unit[i]="unknown";
			}else{
				attribute_name[i]=str.substring(0,index_id).trim();
				attribute_unit[i]=str.substring(index_id+1).trim();
			}
		}
		vertex_attribute_data=new double[vertex_location_data.length][];
		for(int i=0,ni=vertex_attribute_data.length;i<ni;i++)
			vertex_attribute_data[i]=null;
		
		while(true){
			int vertex_id=fr.get_int();
			if((fr.eof())||(fr.error_flag()))
				break;
			if((vertex_id>=0)&&(vertex_id<vertex_id_map.length))
				if((vertex_id=vertex_id_map[vertex_id])>=0)
					if(vertex_id<vertex_location_data.length){
						vertex_attribute_data[vertex_id]=new double[attribute_number];
						for(int i=0;i<attribute_number;i++)
							vertex_attribute_data[vertex_id][i]=fr.get_double();
						continue;
					}
			fr.get_line();
		}
	
		attribute_min_value=new double[attribute_number];
		attribute_max_value=new double[attribute_number];
		
		boolean first_value_flag=true;
		for(int i=0,ni=vertex_attribute_data.length;i<ni;i++){
			if(vertex_attribute_data[i]!=null){
				if(first_value_flag)
					for(int j=0;j<attribute_number;j++){
						attribute_min_value[j]=vertex_attribute_data[i][j];
						attribute_max_value[j]=vertex_attribute_data[i][j];
					}
				else
					for(int j=0;j<attribute_number;j++){
						attribute_min_value[j]=(attribute_min_value[j]<=vertex_attribute_data[i][j])?attribute_min_value[j]:vertex_attribute_data[i][j];
						attribute_max_value[j]=(attribute_max_value[j]>=vertex_attribute_data[i][j])?attribute_max_value[j]:vertex_attribute_data[i][j];
					}
				first_value_flag=false;
			}
		}
		if(first_value_flag)
			for(int i=0;i<attribute_number;i++){
				attribute_min_value[i]=0;
				attribute_max_value[i]=1;
			}
	}
	private void add_normal(int unit_id,int vertex_id_0,int vertex_id_1,int vertex_id_2)
	{
		vertex_id_0=vertex_id_map[unit_data[unit_id][vertex_id_0+3]];
		vertex_id_1=vertex_id_map[unit_data[unit_id][vertex_id_1+3]];
		vertex_id_2=vertex_id_map[unit_data[unit_id][vertex_id_2+3]];
		double p0[]=vertex_location_data[vertex_id_0];
		double p1[]=vertex_location_data[vertex_id_1];
		double p2[]=vertex_location_data[vertex_id_2];
		
		plane pl=new plane(
				new point(p0[0],p0[1],p0[2]),
				new point(p1[0],p1[1],p1[2]),
				new point(p2[0],p2[1],p2[2]));
		if(pl.error_flag)
			return;
		vertex_normal_data[vertex_id_0][0]+=pl.A;
		vertex_normal_data[vertex_id_0][1]+=pl.B;
		vertex_normal_data[vertex_id_0][2]+=pl.C;
		return;
	}
	private void caculate_normal_data()
	{
		vertex_normal_data=new double[vertex_location_data.length][];
		for(int i=0,ni=vertex_normal_data.length;i<ni;i++)
			vertex_normal_data[i]=new double[]{0,0,0,1};		
		for(int i=0,ni=unit_number;i<ni;i++){
			switch(unit_data[i][1]){
			case 0:		//六面体 hexahedron
				add_normal(i,0,3,1);
				add_normal(i,0,1,4);
				add_normal(i,0,4,3);
				
				add_normal(i,1,2,5);
				add_normal(i,1,5,0);
				add_normal(i,1,0,2);
				
				add_normal(i,2,1,3);
				add_normal(i,2,3,6);
				add_normal(i,2,6,1);
				
				add_normal(i,3,0,7);
				add_normal(i,3,7,2);
				add_normal(i,3,2,0);
				
				
				add_normal(i,4,0,5);
				add_normal(i,4,5,7);
				add_normal(i,4,7,0);
				
				add_normal(i,5,1,6);
				add_normal(i,5,6,4);
				add_normal(i,5,4,1);
				
				add_normal(i,6,5,2);
				add_normal(i,6,2,7);
				add_normal(i,6,7,5);
				
				add_normal(i,7,4,6);
				add_normal(i,7,6,3);
				add_normal(i,7,3,4);
				
				break;
			case 1:		//三角形
				add_normal(i,0,1,2);
				add_normal(i,1,2,0);
				add_normal(i,2,0,1);
				break;
			case 2:		//三棱柱 prism
				add_normal(i,0,2,1);
				add_normal(i,0,1,3);
				add_normal(i,0,3,2);
				
				add_normal(i,1,0,2);
				add_normal(i,1,4,0);
				add_normal(i,1,2,4);
				
				add_normal(i,2,1,0);
				add_normal(i,2,5,1);
				add_normal(i,2,0,5);
				
				add_normal(i,3,4,5);
				add_normal(i,3,0,4);
				add_normal(i,3,5,0);
				
				add_normal(i,4,5,3);
				add_normal(i,4,1,5);
				add_normal(i,4,3,1);
				
				add_normal(i,5,3,4);
				add_normal(i,5,4,2);
				add_normal(i,5,2,3);
				break;
			case 3:		//四面体 tetrahedron
				add_normal(i,0,1,3);
				add_normal(i,0,3,2);
				add_normal(i,0,2,1);
				
				add_normal(i,1,0,2);
				add_normal(i,1,2,3);
				add_normal(i,1,3,0);
				
				add_normal(i,2,3,1);
				add_normal(i,2,1,0);
				add_normal(i,2,0,3);
				
				add_normal(i,3,0,1);
				add_normal(i,3,1,2);
				add_normal(i,3,2,0);
				
				break;
			case 4:		//金字塔 Pyramid
				add_normal(i,0,1,4);
				add_normal(i,0,4,3);
				add_normal(i,0,3,1);
				
				add_normal(i,1,4,0);
				add_normal(i,1,0,2);
				add_normal(i,1,2,4);
				
				add_normal(i,2,4,1);
				add_normal(i,2,1,3);
				add_normal(i,2,3,4);
				
				add_normal(i,3,0,4);
				add_normal(i,3,4,2);
				add_normal(i,3,2,0);
				
				add_normal(i,4,0,1);
				add_normal(i,4,1,2);
				add_normal(i,4,2,3);
				add_normal(i,4,3,0);
				
				break;
			case 5:		//四边形
				add_normal(i,0,1,3);
				add_normal(i,1,2,0);
				add_normal(i,2,3,1);
				add_normal(i,3,0,2);
				break;
			case 6:		//线段
			case 7:		//点
			default:
				break;
			};
		}
		for(int i=0,ni=vertex_normal_data.length;i<ni;i++){
			double a=vertex_normal_data[i][0];
			double b=vertex_normal_data[i][1];
			double c=vertex_normal_data[i][2];
			double distance=Math.sqrt(a*a+b*b+c*c);
			if(distance<const_value.min_value){
				for(int j=0,nj=vertex_normal_data[i].length-1;j<nj;j++)
					vertex_normal_data[i][j]=0;
			}else{
				for(int j=0,nj=vertex_normal_data[i].length-1;j<nj;j++)
					vertex_normal_data[i][j]/=distance;
			}
			vertex_normal_data[i][3]=distance;
		}
	}
	private void write_mesh_file(String file_name,String charset)
	{
		box face_box=null;
		String default_attr_0="0	0	0	0";
		String default_attr_1="0	0	0	0";
		
		file_writer.file_delete(file_name+".lock");
		file_writer.file_delete(file_name+".face");
		file_writer.file_delete(file_name+".edge");
		file_writer.file_delete(file_name+".point");
		file_writer.file_delete(file_name+".face.gzip");
		file_writer.file_delete(file_name+".edge.gzip");
		file_writer.file_delete(file_name+".point.gzip");
		
		file_writer fw=new file_writer(file_name+".face",charset);
		
		fw.println("/*	face_primitive_number:	",unit_data.length+"	*/");
	
		for(int i=0,ni=unit_data.length;i<ni;i++){
			fw.println();
			fw.print  ("/*	face_primitive	",i);
			fw.println("	material	*/	0	0	0	",	unit_data[i][2]);
			fw.println("/*	vertex number	*/	",			unit_data[i].length-3);
			fw.println();
			
			double sum_x=0,sum_y=0,sum_z=0;
			for(int j=3,nj=unit_data[i].length;j<nj;j++){
				int vertex_id=vertex_id_map[unit_data[i][j]];
				sum_x+=vertex_location_data[vertex_id][0];
				sum_y+=vertex_location_data[vertex_id][1];
				sum_z+=vertex_location_data[vertex_id][2];
			}
			double	attr0_x =sum_x/(double)(unit_data[i].length-3);	
			double	attr0_y =sum_y/(double)(unit_data[i].length-3);
			double	attr0_z =sum_z/(double)(unit_data[i].length-3);
			String	attr0_w =Integer.toString((int)(Math.round(unit_data[i][1])));
					attr0_w+=","+Integer.toString(unit_data[i][0]);
			
			for(int j=3,nj=unit_data[i].length;j<nj;j++) {
				int vertex_id=vertex_id_map[unit_data[i][j]];
				
				double	px=vertex_location_data[vertex_id][0];
				double	py=vertex_location_data[vertex_id][1];
				double	pz=vertex_location_data[vertex_id][2];
				int		pw=(int)(vertex_location_data[vertex_id][3]);
				
				double	nx=vertex_normal_data[vertex_id][0];
				double	ny=vertex_normal_data[vertex_id][1];
				double	nz=vertex_normal_data[vertex_id][2];
				double	nw=vertex_normal_data[vertex_id][3];	//NORMAL_DISTANCE
				
				double	attr1[]=new double[attribute_number];
				for(int k=0;k<attribute_number;k++) {
					attr1[k]=attribute_max_value[k]-attribute_min_value[k];
					if(attr1[k]<const_value.min_value)
						attr1[k]=0;
					else
						attr1[k]=(vertex_attribute_data[vertex_id][k]-attribute_min_value[k])/attr1[k];
				}
				if(face_box==null)
					face_box=new box(new point(px,py,pz));
				else
					face_box=face_box.add(new box(new point(px,py,pz)));
				
				default_attr_0=attr0_x +"	"+attr0_y +"	"+attr0_z +"	"+attr0_w;	
				default_attr_1=attr1[0]+"	"+attr1[1]+"	"+attr1[2]+"	"+attr1[3];	
				for(int k=4;k<attribute_number;k++)
					default_attr_1+=","+attr1[k];
				
				fw.println("/*	"+(j-3)+".location		*/	",px+"	"+py+"	"+pz+"	"+pw);
						//	POSITION and VERTEX_ID
				fw.println("/*	"+(j-3)+".normal		*/	",nx+"	"+ny+"	"+nz+"	"+nw);
						//	NORMAL and NORMAL_LENGTH
				fw.println("/*	"+(j-3)+".attribute_0	*/	",default_attr_0);
						//	CENTER_POSITION_FOR_CLIP,UNIT_TYPE,UNIT_ID
				fw.println("/*	"+(j-3)+".attribute_1	*/	",default_attr_1);
						// ALL ATTRIBUTES
			}
		}

		fw.close();
		
		String str[]=new String[]{

				"/*	version					*/	2021.07.15",
				"/*	origin material			*/	0	0	0	1",
				"/*	default material		*/	0	0	0	0",
				"/*	origin  vertex_location_extra_data	*/	1",
				"/*	default vertex_location_extra_data	*/	1",
				"/*	default vertex_normal_extra_data	*/	1",
				"/*	max_attribute_number	*/	2",
				"							"+default_attr_0,
				"							"+default_attr_1,
				"",
				"/*	body_number		*/	1",
				"/*	body 0 name		*/	inp_body	/*	face_number	*/	1",
				"/* 	face 0 name					*/  inp_face",
				"/*		face_type					*/  unknown	/*	parameter_number	*/	0	/*	parameter	*/",
				"/*		total_face_primitive_number	*/	"+unit_data.length,
				"/*		face_attribute_number		*/	2",
				"/*		face_face_box				*/	"+((face_box==null)?"nobox":(
						face_box.p[0].x+"	"+face_box.p[0].y+"	"+face_box.p[0].z+"	"+
						face_box.p[1].x+"	"+face_box.p[1].y+"	"+face_box.p[1].z)),
				"",
				"/*		face_loop_number			*/	0",
				""
		};
		
		fw=new file_writer(file_name,charset);
		for(int i=0,ni=str.length;i<ni;i++)
			fw.println(str[i]);
		fw.close();
	}
	private void write_material_file(String file_name,String charset)
	{
		file_writer fw=new file_writer(file_name,charset);
		
		for(int i=0;i<attribute_number;i++){
			fw.print  (				attribute_name[i]);
			fw.print  ("		",	attribute_unit[i]);
			fw.print  ("		",	attribute_min_value[i]);
			fw.println("		",	attribute_max_value[i]);
		}
		fw.println();
		
		fw.close();
	}
	public inp_converter(String mesh_file_name,String mesh_charset,String target_file_name,String target_charset)
	{
		file_reader fr=new file_reader(mesh_file_name,mesh_charset);
		
		vertex_number	=fr.get_int();
		unit_number		=fr.get_int();
		attribute_number=fr.get_int();
		fr.get_line();
		
		debug_information.println("Begin convert inp mesh format :	",mesh_file_name);
		debug_information.println((new Date()).toString()+":Input vertics......");
		input_vertex_location(fr);
		debug_information.println((new Date()).toString()+":Input units......");
		input_unit(fr);
		debug_information.println((new Date()).toString()+":Input attributes......");
		input_vertex_attribute(fr);
		
		fr.close();
		
		debug_information.println((new Date()).toString()+":caculate normal......");
		caculate_normal_data();
		
		debug_information.println((new Date()).toString()+":write mesh......");
		write_mesh_file(target_file_name,target_charset);
		
		debug_information.println((new Date()).toString()+":write attribute......");
		write_material_file(mesh_file_name+".attribute",target_charset);
		
		debug_information.println("End convert inp mesh format");
	}
	
	private static void value_short(String file_name,String file_charset)
	{
		int value_length=12;
		
		file_reader fr=new file_reader(file_name,file_charset);
		file_writer fw=new file_writer(file_name+".short",file_charset);
		
		while(!(fr.eof())){
			String str=fr.get_string();
			if(str==null)
				continue;
			if((str=str.trim()).length()<=0)
				continue;
			for(int index_id;;)
				if((index_id=str.indexOf(','))<0) {
					double value=Double.parseDouble(str);
					fw.println(format_change.double_to_decimal_string(value,value_length));
					break;
				}else{
					double value=Double.parseDouble(str.substring(0,index_id));
					fw.print(format_change.double_to_decimal_string(value,value_length),",");
					str=str.substring(index_id+1);
				}
		}
		fr.close();
		fw.close();
		file_writer.file_delete(file_name);
		file_writer.file_rename(file_name+".short", file_name);
	}
	
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		new inp_converter(
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_0\\part.inp",
				"GBK",
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_0\\part.inp.mesh",
				"GBK");	
		value_short("F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_0\\part.inp.mesh.face", "GBK");
		
		new inp_converter(
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_1\\part.inp",
				"GBK",
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_1\\part.inp.mesh",
				"GBK");
		value_short("F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_1\\part.inp.mesh.face", "GBK");
		
		new inp_converter(
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_2\\part.inp",
				"GBK",
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_2\\part.inp.mesh",
				"GBK");
		value_short("F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_2\\part.inp.mesh.face", "GBK");
		
		new inp_converter(
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_3\\part.inp",
				"GBK",
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_3\\part.inp.mesh",
				"GBK");
		value_short("F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_3\\part.inp.mesh.face", "GBK");
		
		new inp_converter(
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_4\\part.inp",
				"GBK",
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_4\\part.inp.mesh",
				"GBK");
		value_short("F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_4\\part.inp.mesh.face", "GBK");
		
		new inp_converter(
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_5\\part.inp",
				"GBK",
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_5\\part.inp.mesh",
				"GBK");
		value_short("F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_5\\part.inp.mesh.face", "GBK");
		
		new inp_converter(
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_6\\part.inp",
				"GBK",
				"F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_6\\part.inp.mesh",
				"GBK");
		value_short("F:\\water_all\\data\\project\\part\\inp_part\\part_inp\\part_6\\part.inp.mesh.face", "GBK");
		
		debug_information.println("End");
	}
}
