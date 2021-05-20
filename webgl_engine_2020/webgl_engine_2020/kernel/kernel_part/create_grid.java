package kernel_part;

import kernel_file_manager.file_writer;

public class create_grid
{
	private double[][] create_vertex_data(int width,int height)
	{
		double ret_val[][]=new double[(1+width)*(1+height)][];
		for(int i=0,k=0;i<=height;i++)
			for(int j=0;j<=width;j++)
				ret_val[k++]=new double[]
						{
								(width ==0)?0:(double)j/(double)width,
								(height==0)?0:(double)i/(double)height,
								0,1
						};
		return ret_val;
	}
	private void create_primitive(
			file_writer f,String attribute_name[],boolean type_flag,
			int width,int height,int width_id,int height_id,int primitive_id)
	{
		if(type_flag){
			f.print  ("/*				Primitive:",		primitive_id);
			f.print  (" Material	*/   ",					width_id);
			f.print  ("	",									height_id);
			f.print  ("	",									primitive_id);
			f.println("	0");
			for(int i=0,ni=attribute_name.length;i<ni;i++){
				f.print  ("/*				Primitive:",	primitive_id);
				f.print  (" ",								attribute_name[i]);
				f.print  (" Index	*/	",					(0+width_id)+(0+height_id)*(1+width));
				f.print  ("	",								(1+width_id)+(0+height_id)*(1+width));
				f.print  ("	",								(1+width_id)+(1+height_id)*(1+width));
				f.println("	-1");
			}
			
			f.print  ("/*				Primitive:",		primitive_id);
			f.print  (" Material	*/   ",					width_id);
			f.print  ("	",									height_id);
			f.print  ("	",									primitive_id);
			f.println("	1");
			for(int i=0,ni=attribute_name.length;i<ni;i++){
				f.print  ("/*				Primitive:",	primitive_id);
				f.print  (" ",								attribute_name[i]);
				
				f.print  (" Index	*/	",					(1+width_id)+(1+height_id)*(1+width));
				f.print  ("	",								(0+width_id)+(1+height_id)*(1+width));
				f.print  ("	",								(0+width_id)+(0+height_id)*(1+width));
				f.println("	-1");
			}
		}else{
			f.print  ("/*				Primitive:",		primitive_id);
			f.print  (" Material	*/   ",					height_id);
			f.print  ("	",									width_id);
			f.print  ("	",									primitive_id);
			f.println("	2");
			for(int i=0,ni=attribute_name.length;i<ni;i++){
				f.print  ("/*				Primitive:",	primitive_id);
				f.print  (" ",								attribute_name[i]);
				f.print  (" Index	*/	",					(0+width_id)+(0+height_id)*(1+width));
				f.println("	-1");
			}
		}
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
	public void do_create(String file_name,
			boolean type_flag,int width,int height,
			String attribute_name[],String file_system_charset)
	{
		width	=(width<1)?0:width;
		height	=(height<1)?0:height;
		
		file_writer f=new file_writer(file_name,file_system_charset);

		f.println("/*	version          */		2019.08.05");
		f.println("/*	origin material  */		0  0  0  1");
		f.println("/*	body_number      */		1");
		
		f.println();
		f.println("/*	Body NO. 0  Name		*/	grid_body	/*  face number	*/	1");
		
		f.println("/*		Face 0 Name		*/	grid_face");
		f.println("/*			Face Type		*/  unknown		/*	Parameter Number	*/	0	/*	parameter	*/");   
		f.println("/*			Texture Number	*/  ",attribute_name.length-2);

		{
			double vertex_data[][]=vertex_data_modifier(create_vertex_data(width,height));
			f.println();
			f.println("/*			Vertex Number	*/  ",vertex_data.length);
			for(int i=0,ni=vertex_data.length;i<ni;i++){
					f.print  ("/*				Vertex:	",	i);
					f.print  ("		*/	",					vertex_data[i][0]);
					f.print  ("	",							vertex_data[i][1]);
					f.print  ("	",							vertex_data[i][2]);
					f.println("	",							vertex_data[i][3]);
				}
		}
		
		{
			double normal_data[][]=normal_data_modifier(create_vertex_data(width,height));
			f.println();
			f.println("/*			normal Number	*/  ",normal_data.length);
			for(int i=0,ni=normal_data.length;i<ni;i++){
					f.print  ("/*				normal:	",	i);
					f.print  ("		*/	",					normal_data[i][0]);
					f.print  ("	",							normal_data[i][1]);
					f.print  ("	",							normal_data[i][2]);
					f.println("	",							normal_data[i][3]);
				}
		}
		
		for(int j=0,nj=attribute_name.length-2;j<nj;j++){
			double attribute_data[][]=attribute_data_modifier(create_vertex_data(width,height),j);
			f.println();
			f.print  ("/*			",attribute_name[j+2]);
			f.println(" Number	*/  ",attribute_data.length);
			for(int i=0,ni=attribute_data.length;i<ni;i++){
					f.print  ("/*				",	attribute_name[j+2]);
					f.print  ("		*/	",			attribute_data[i][0]);
					f.print  ("	",					attribute_data[i][1]);
					f.print  ("	",					attribute_data[i][2]);
					f.println("	",					attribute_data[i][3]);
				}
		}
		
		f.println();
		f.println("/*			Primitive Number	*/	",(type_flag?(2*width*height):((width+1)*(height+1))));
		for(int height_id=0,primitive_id=0,height_n=type_flag?height:(height+1);height_id<height_n;height_id++)
			for(int width_id=0,width_n=type_flag?width:(width+1);width_id<width_n;width_id++)
				create_primitive(f,attribute_name,type_flag,width,height,width_id,height_id,primitive_id++);
		
		f.println("/*			Loop Number	*/	0");
		f.println();
		f.println();
		f.close();
		
		return;
	}
	public create_grid()
	{
	}
}
