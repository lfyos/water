package kernel_part;

import kernel_file_manager.file_writer;

public class create_grid
{
	public int width,height,attribute_number;
	private double vertex_data[][],normal_data[][],attribute_data[][][];
	private double min_vertex_data[],max_vertex_data[],average_attribute_data[][];
	
	private double[][] create_vertex_data(int width,int height)
	{
		double ret_val[][]=new double[(1+width)*(1+height)][];
		for(int i=0,k=0;i<=height;i++)
			for(int j=0;j<=width;j++)
				ret_val[k++]=new double[]{(width ==0)?0:(double)j/(double)width,(height==0)?0:(double)i/(double)height,0,1};
		return ret_val;
	}
	public double[][]vertex_data_modifier(double old_vertex_data[][])
	{
		double new_vertex_data[][]=new double[old_vertex_data.length][];
		for(int i=0,ni=old_vertex_data.length;i<ni;i++){
			new_vertex_data[i]=new double[old_vertex_data[i].length];
			for(int j=0,nj=old_vertex_data[i].length;j<nj;j++)
				new_vertex_data[i][j]=old_vertex_data[i][j];
		}
		return new_vertex_data;
	}
	public double[][]normal_data_modifier(double old_normal_data[][])
	{
		double new_normal_data[][]=new double[old_normal_data.length][];
		for(int i=0,ni=old_normal_data.length;i<ni;i++){
			new_normal_data[i]=new double[old_normal_data[i].length];
			for(int j=0,nj=old_normal_data[i].length;j<nj;j++)
				new_normal_data[i][j]=old_normal_data[i][j];
		}
		return new_normal_data;
	}
	public double[][]attribute_data_modifier(double old_attribute_data[][],int attribute_id)
	{
		double new_attribute_data[][]=new double[old_attribute_data.length][];
		for(int i=0,ni=old_attribute_data.length;i<ni;i++){
			new_attribute_data[i]=new double[old_attribute_data[i].length];
			for(int j=0,nj=old_attribute_data[i].length;j<nj;j++)
				new_attribute_data[i][j]=old_attribute_data[i][j];
		}
		return new_attribute_data;
	}
	private void create_data()
	{
		vertex_data=vertex_data_modifier(create_vertex_data(width,height));
		min_vertex_data=new double[] {vertex_data[0][0],vertex_data[0][1],vertex_data[0][2],vertex_data[0][3]};
		max_vertex_data=new double[] {vertex_data[0][0],vertex_data[0][1],vertex_data[0][2],vertex_data[0][3]};
		for(int vertex_id=1,vertex_number=vertex_data.length;vertex_id<vertex_number;vertex_id++) 
			for(int i=0,ni=4;i<ni;i++){
				double value=vertex_data[vertex_id][i];
				if(min_vertex_data[i]>value)
					min_vertex_data[i]=value;
				if(max_vertex_data[i]<value)
					max_vertex_data[i]=value;
			}
		
		normal_data=normal_data_modifier(create_vertex_data(width,height));
		
		attribute_data=new double[attribute_number][][];
		average_attribute_data=new double[attribute_number][];
		for(int attribute_id=0;attribute_id<attribute_number;attribute_id++) {
			attribute_data[attribute_id]=attribute_data_modifier(create_vertex_data(width,height),attribute_id);
			average_attribute_data[attribute_id]=new double[] {0,0,0,0};
			for(int i=0,ni=attribute_data[attribute_id].length;i<ni;i++) {
				average_attribute_data[attribute_id][0]+=attribute_data[attribute_id][i][0];
				average_attribute_data[attribute_id][1]+=attribute_data[attribute_id][i][1];
				average_attribute_data[attribute_id][2]+=attribute_data[attribute_id][i][2];
				average_attribute_data[attribute_id][3]+=attribute_data[attribute_id][i][3];
			}
			average_attribute_data[attribute_id][0]/=attribute_data[attribute_id].length;
			average_attribute_data[attribute_id][1]/=attribute_data[attribute_id].length;
			average_attribute_data[attribute_id][2]/=attribute_data[attribute_id].length;
			average_attribute_data[attribute_id][3]/=attribute_data[attribute_id].length;
		}
	}
	private void create_mesh_file(String file_name,String file_charset)
	{
		String str[];
		file_writer f=new file_writer(file_name,file_charset);
		str=new String[] {
				"/*	version					*/	2021.07.15",
				"/*	origin material			*/	0	0	0	0",
				"/*	default material		*/	0	0	0	0",
				"/*	origin extra_data		            */	1",
				"/*	default vertex_location_extra_data	*/	1",
				"/*	default vertex_normal_extra_data	*/	1",
				"/*	max_attribute_number	*/  "+attribute_number
		};
		for(int i=0,ni=str.length;i<ni;i++)
			f.println(str[i]);
		for(int attribute_id=0;attribute_id<attribute_number;attribute_id++)
			f.  print  ("		/*	attribute_"+attribute_id+"		*/").
				print  ("	",average_attribute_data[attribute_id][0]).
				print  ("	",average_attribute_data[attribute_id][1]).
				print  ("	",average_attribute_data[attribute_id][2]).
				println("	",average_attribute_data[attribute_id][3]);

		str=new String[] {
				"",
				"/*  body_number	*/	1",
				"/*  body_name		*/	grid_body	/*	face_number	*/	1",
				"/*  	face  name	*/  grid_face",
				"/*		face type	*/  unknown		/*  parameter number	*/	0	/*	prameter	*/",
				"/*		total_face_primitive_number	*/	"+(2*width*height),
	            "/*		face_attribute_number		*/  "+attribute_number
		};
		for(int i=0,ni=str.length;i<ni;i++)
			f.println(str[i]);
		
		f.	print  ("/*		face_face_box               */").
			print  ("	",min_vertex_data[0]).print  ("	",min_vertex_data[1]).print  ("	",min_vertex_data[2]).
			print  ("	",max_vertex_data[0]).print  ("	",max_vertex_data[1]).println("	",max_vertex_data[2]).
			println();
		
		str=new String[] {
				"/*  	face loop number					*/  1",
				"/*		Loop NO.0	edge number				*/  2",
				"/*			Loop NO.0  Edge NO.0			*/",
				"/*			curve type						*/	segment	/*	parameter number	*/	0	/*	parameter	*/",
				"			start_not_effective",
				"			end_not_effective",
				"/*			parameter_point_extra_data		*/	1",
				"/*			parameter_point_material    	*/	0	0	0	0"
		};
		for(int i=0,ni=str.length;i<ni;i++)
			f.println(str[i]);
		f.	print  ("/*			face_edge_box               	*/").
		print  ("	",min_vertex_data[0]).print  ("	",min_vertex_data[1]).print  ("	",min_vertex_data[2]).
		print  ("	",max_vertex_data[0]).print  ("	",max_vertex_data[1]).println("	",max_vertex_data[2]);
		             
		f.println("/*			total_edge_primitive_number		*/	",2*(width*(height+1)+(width+1)*height));
		f.println("/*			total_point_primitive_number	*/	0");
		f.println().println();
		
		str=new String[] {
				"/*			Loop NO.0  Edge NO.1			*/",
				"/*			curve type						*/	render_point_set	/*	parameter number	*/	0	/*	parameter	*/",
				"			start_not_effective",
				"			end_not_effective",
				"/*			parameter_point_extra_data		*/	1",
				"/*			parameter_point_material   		*/	0	0	0	0"
		};
		for(int i=0,ni=str.length;i<ni;i++)
			f.println(str[i]);
		f.	print  ("/*			face_edge_box               	*/").
		print  ("	",min_vertex_data[0]).print  ("	",min_vertex_data[1]).print  ("	",min_vertex_data[2]).
		print  ("	",max_vertex_data[0]).print  ("	",max_vertex_data[1]).println("	",max_vertex_data[2]);
		             
		f.println("/*			total_edge_primitive_number		*/	0");
		f.println("/*			total_point_primitive_number	*/	",(width+1)*(height+1));
		f.println().println();

		f.close();
	}
	private void create_face_file(String file_name,String file_charset,String attribute_name[])
	{
		file_writer f=new file_writer(file_name,file_charset);
		for(int primitive_id=0,height_id=0;height_id<height;height_id++) {
			for(int width_id=0;width_id<width;width_id++) {
				int vertex_id[][]=new int[][]{
					new int[]{
						(0+width_id)+(0+height_id)*(1+width),
						(1+width_id)+(0+height_id)*(1+width),
						(1+width_id)+(1+height_id)*(1+width)
					},
					new int[]{
						(1+width_id)+(1+height_id)*(1+width),
						(0+width_id)+(1+height_id)*(1+width),
						(0+width_id)+(0+height_id)*(1+width)
					}
				};
				for(int triangle_id=0,triangle_number=vertex_id.length;triangle_id<triangle_number;triangle_id++,primitive_id++){
					f.print  ("/*	triangle:",		primitive_id);
					f.print  (" Material	*/	",	width_id);
					f.print  ("	",					height_id);
					f.print  ("	",					primitive_id);
					f.println("	",					primitive_id);
					f.println("/*	vertex number		*/	",vertex_id[triangle_id].length);

					for(int i=0,ni=vertex_id[triangle_id].length;i<ni;i++) {
						int my_vertex_id=vertex_id[triangle_id][i];
						f.print  ("	/*	"+i+"."+attribute_name[0]+"	*/  ",vertex_data[my_vertex_id]);
						f.print  ("	/*	"      +attribute_name[1]+"	*/  ",normal_data[my_vertex_id]);
						for(int j=0,nj=attribute_number;j<nj;j++)
							f.print  ("	/*	"+attribute_name[j+2]+"	*/  ",attribute_data[j][my_vertex_id]);
						f.println();
					}
				}
			}
		}
		f.close();
	}
	
	private void create_edge_file(String file_name,String file_charset)
	{
		file_writer f=new file_writer(file_name,file_charset);
		int primitive_id=0;
		for(int height_id=0;height_id<=height;height_id++) {
			for(int width_id=0;width_id<width;width_id++,primitive_id++) {
				int vertex_id_0=(0+width_id)+(0+height_id)*(1+width);
				int vertex_id_1=(1+width_id)+(0+height_id)*(1+width);
				f.print  ("/*  1.Edge:"+primitive_id+" vertex location	*/	",vertex_data[vertex_id_0]);
				f.println("	/*	material	*/    ",(0+width_id)+"	"+height_id+"	"+primitive_id+"	"+primitive_id);
				f.print  ("/*  1.Edge:"+primitive_id+" vertex location	*/	",vertex_data[vertex_id_1]);
				f.println("	/*	material	*/    ",(1+width_id)+"	"+height_id+"	"+primitive_id+"	"+primitive_id);
			}
		}
		for(int width_id=0;width_id<=width;width_id++) {
			for(int height_id=0;height_id<height;height_id++,primitive_id++){
				int vertex_id_0=(0+width_id)+(0+height_id)*(1+width);
				int vertex_id_1=(0+width_id)+(1+height_id)*(1+width);
				f.print  ("/*  2.Edge:"+primitive_id+" vertex location	*/	",vertex_data[vertex_id_0]);
				f.println("	/*	material	*/    ",width_id+"	"+(0+height_id)+"	"+primitive_id+"	"+primitive_id);
				
				f.print  ("/*  2.Edge:"+primitive_id+" vertex location	*/	",vertex_data[vertex_id_1]);
				f.println("	/*	material	*/    ",width_id+"	"+(1+height_id)+"	"+primitive_id+"	"+primitive_id);
			};
		}
		f.close();
	}
	private void create_point_file(String file_name,String file_charset)
	{
		file_writer f=new file_writer(file_name,file_charset);
		int primitive_id=0;
		for(int height_id=0;height_id<=height;height_id++)
			for(int width_id=0;width_id<=width;width_id++,primitive_id++) {
				int vertex_id=width_id+height_id*(1+width);
				f.print  ("/*  Point:"+primitive_id+" 	vertex location	*/	",vertex_data[vertex_id]);
				f.println("	/*	material	*/    ",width_id+"	"+height_id+"	"+primitive_id+"	"+primitive_id);
			};
			
		f.close();
	}
	public create_grid(String file_name,String file_charset,int my_width,int my_height,String attribute_name[])
	{
		width =(my_width <1)?0:my_width;
		height=(my_height<1)?0:my_height;
		if((attribute_number=attribute_name.length-2)<0)
			attribute_number=0;
		
		create_data();
		create_mesh_file(file_name,file_charset);
		create_face_file(file_name+".face",file_charset,attribute_name);
		create_edge_file(file_name+".edge",file_charset);
		create_point_file(file_name+".point",file_charset);
	}
}
