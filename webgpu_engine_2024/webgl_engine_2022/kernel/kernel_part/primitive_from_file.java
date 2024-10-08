package kernel_part;

import java.io.File;

import kernel_common_class.compress_file_data;
import kernel_file_manager.file_reader;

public class primitive_from_file implements primitive_interface
{
	private file_reader face_file,edge_file,point_file;
	
	private String face_file_name,edge_file_name,point_file_name;
	private String gzip_face_file_name,gzip_edge_file_name,gzip_point_file_name;
	private String file_charset;
	
	public String[]get_primitive_material(int body_id,int face_id,int primitive_id)
	{
		String ret_val[]=new String[4];
		for(int i=0;i<4;i++)
			if((ret_val[i]=face_file.get_string())==null)
				ret_val[i]="0";
			else if(ret_val[i].trim().length()<=0)
				ret_val[i]="0";
		return ret_val;
	}
	public int get_primitive_vertex_number(int body_id,int face_id,int primitive_id)
	{
		return face_file.get_int();
	}
	public double[]get_primitive_vertex_location_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		return new double[] {face_file.get_double(),face_file.get_double(),face_file.get_double()};
	}
	public String get_primitive_vertex_location_extra_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		String ret_val=face_file.get_string();
		return (ret_val==null)?"1":(ret_val.trim().length()<=0)?"1":ret_val;
	}
	
	public double[]get_primitive_vertex_normal_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		return new double[] {face_file.get_double(),face_file.get_double(),face_file.get_double()};
	}
	public String get_primitive_vertex_normal_extra_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		String ret_val=face_file.get_string();
		return (ret_val==null)?"1":(ret_val.trim().length()<=0)?"1":ret_val;
	}
	
	public double[]get_primitive_vertex_attribute_data(int body_id,int face_id,int primitive_id,int vertex_id,int attribute_id)
	{
		return new double[] {face_file.get_double(),face_file.get_double(),face_file.get_double()};
	}
	public String get_primitive_vertex_attribute_extra_data(int body_id,int face_id,int primitive_id,int vertex_id,int attribute_id)
	{
		String ret_val=face_file.get_string();
		return (ret_val==null)?"1":(ret_val.trim().length()<=0)?"1":ret_val;
	}

	public double[]get_edge_location_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		return new double[] {edge_file.get_double(),edge_file.get_double(),edge_file.get_double()};
	}
	public String get_edge_extra_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		String ret_val=edge_file.get_string();
		return (ret_val==null)?"1":(ret_val.trim().length()<=0)?"1":ret_val;
	}
	public String[] get_edge_material(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		String ret_val[]=new String[4];
		for(int i=0;i<4;i++)
			if((ret_val[i]=edge_file.get_string())==null)
				ret_val[i]="0";
			else if(ret_val[i].trim().length()<=0)
				ret_val[i]="0";
		return ret_val;
	}
	public double[]get_point_location_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		return new double[] {point_file.get_double(),point_file.get_double(),point_file.get_double()};
	}
	public String get_point_extra_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		String ret_val=point_file.get_string();
		return (ret_val==null)?"1":(ret_val.trim().length()<=0)?"1":ret_val;
	}
	public String[] get_point_material(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		String ret_val[]=new String[4];
		for(int i=0;i<4;i++)
			if((ret_val[i]=point_file.get_string())==null)
				ret_val[i]="0";
			else if(ret_val[i].trim().length()<=0)
				ret_val[i]="0";
		return ret_val;
	}
	
	public void destroy()
	{
		if(face_file!=null) {
			face_file.close();
			face_file=null;
		}
		if(edge_file!=null) {
			edge_file.close();
			edge_file=null;
		}
		if(point_file!=null) {
			point_file.close();
			point_file=null;
		}
	}
	public primitive_from_file(String my_file_name,String my_file_charset,int my_response_block_size)
	{
		File f,gf;
		
		face_file_name		=my_file_name+".face";
		edge_file_name		=my_file_name+".edge";
		point_file_name		=my_file_name+".point";
		gzip_face_file_name	=my_file_name+".face.gzip";
		gzip_edge_file_name	=my_file_name+".edge.gzip";
		gzip_point_file_name=my_file_name+".point.gzip";
		file_charset		=my_file_charset;
		
		if(!((f=new File(face_file_name)).exists()))
			if((gf=new File(gzip_face_file_name)).exists()){
				long last_time=gf.lastModified();
				compress_file_data.do_uncompress(f,gf,my_response_block_size, "gzip");
				new File(face_file_name).setLastModified(last_time);
				gf.delete();
			}
		if(!((f=new File(edge_file_name)).exists()))
			if((gf=new File(gzip_edge_file_name)).exists()) {
				long last_time=gf.lastModified();
				compress_file_data.do_uncompress(f, gf, my_response_block_size, "gzip");
				new File(edge_file_name).setLastModified(last_time);
				gf.delete();
			}
		if(!((f=new File(point_file_name)).exists()))
			if((gf=new File(gzip_point_file_name)).exists()) {
				long last_time=gf.lastModified();
				compress_file_data.do_uncompress(f, gf, my_response_block_size, "gzip");
				new File(point_file_name).setLastModified(last_time);
				gf.delete();
			}
		face_file	=new file_reader(face_file_name, file_charset);
		edge_file	=new file_reader(edge_file_name, file_charset);
		point_file	=new file_reader(point_file_name,file_charset);
	}
}
