package kernel_part;

import java.io.File;

import kernel_common_class.compress_file_data;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class primitive_from_file implements primitive_interface
{
	private file_reader face_file,edge_file,point_file;
	private String face_file_name,edge_file_name,point_file_name;
	private String gzip_face_file_name,gzip_edge_file_name,gzip_point_file_name;
	private String file_charset;
	
	public String[]get_primitive_material(int body_id,int face_id,int primitive_id)
	{
		return new String[] {face_file.get_string(),face_file.get_string(),face_file.get_string(),face_file.get_string()};
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
		return face_file.get_string();
	}
	
	public double[]get_primitive_vertex_normal_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		return new double[] {face_file.get_double(),face_file.get_double(),face_file.get_double()};
	}
	public String get_primitive_vertex_normal_extra_data(int body_id,int face_id,int primitive_id,int vertex_id)
	{
		return face_file.get_string();
	}
	
	public double[]get_primitive_vertex_attribute_data(int body_id,int face_id,int primitive_id,int vertex_id,int attribute_id)
	{
		return new double[] {face_file.get_double(),face_file.get_double(),face_file.get_double()};
	}
	public String get_primitive_vertex_attribute_extra_data(int body_id,int face_id,int primitive_id,int vertex_id,int attribute_id)
	{
		return face_file.get_string();
	}

	public double[]get_edge_location_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		return new double[] {edge_file.get_double(),edge_file.get_double(),edge_file.get_double()};
	}
	public String get_edge_extra_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		return edge_file.get_string();
	}
	public String[] get_edge_material(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		return new String[] {edge_file.get_string(),edge_file.get_string(),edge_file.get_string(),edge_file.get_string()};
	}
	
	public double[]get_point_location_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		return new double[] {point_file.get_double(),point_file.get_double(),point_file.get_double()};
	}
	public String get_point_extra_data(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		return point_file.get_string();
	}
	public String[] get_point_material(int body_id,int face_id,int loop_id,int edge_id,int point_id)
	{
		return new String[] {point_file.get_string(),point_file.get_string(),point_file.get_string(),point_file.get_string()};
	}
	
	public void destroy(long my_max_compress_file_length,int my_response_block_size)
	{
		File f,gf;
		
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
		
		if(my_max_compress_file_length<=0)
			return;
		
		if((f=new File(face_file_name)).exists()){
			if(f.length()<=my_max_compress_file_length){
				if((gf=new File(gzip_face_file_name)).exists())
					gf.delete();
			}else{
				long last_time=f.lastModified();
				if(!((gf=new File(gzip_face_file_name)).exists()))
					compress_file_data.do_compress(f,gf,my_response_block_size,"gzip");
				f.delete();
				new File(gzip_face_file_name).setLastModified(last_time);
			}
		}
		if((f=new File(edge_file_name)).exists()){
			if(f.length()<=my_max_compress_file_length){
				if((gf=new File(gzip_edge_file_name)).exists())
					gf.delete();
			}else{
				long last_time=f.lastModified();
				if(!((gf=new File(gzip_edge_file_name)).exists()))
					compress_file_data.do_compress(f,gf,my_response_block_size,"gzip");
				f.delete();
				new File(gzip_edge_file_name).setLastModified(last_time);
			}
		}
		
		if((f=new File(point_file_name)).exists()){
			if(f.length()<=my_max_compress_file_length){
				if((gf=new File(gzip_point_file_name)).exists())
					gf.delete();
			}else{
				long last_time=f.lastModified();
				if(!((gf=new File(gzip_point_file_name)).exists()))
					compress_file_data.do_compress(f,gf,my_response_block_size,"gzip");
				f.delete();
				new File(gzip_point_file_name).setLastModified(last_time);
			}
		}
	}
	public primitive_from_file(String my_file_name,String my_file_charset,
			long my_max_comment_file_length,int my_response_block_size)
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
				if(my_max_comment_file_length>0)
					if(new File(face_file_name).length()>my_max_comment_file_length)
						file_writer.delete_comment(face_file_name,file_charset);
				new File(face_file_name).setLastModified(last_time);
			}
		if(!((f=new File(edge_file_name)).exists()))
			if((gf=new File(gzip_edge_file_name)).exists()) {
				long last_time=gf.lastModified();
				compress_file_data.do_uncompress(f, gf, my_response_block_size, "gzip");
				if(my_max_comment_file_length>0)
					if(new File(edge_file_name).length()>my_max_comment_file_length)
						file_writer.delete_comment(edge_file_name,file_charset);
				new File(edge_file_name).setLastModified(last_time);
			}
		if(!((f=new File(point_file_name)).exists()))
			if((gf=new File(gzip_point_file_name)).exists()) {
				long last_time=gf.lastModified();
				compress_file_data.do_uncompress(f, gf, my_response_block_size, "gzip");
				if(my_max_comment_file_length>0)
					if(new File(point_file_name).length()>my_max_comment_file_length)
						file_writer.delete_comment(point_file_name,file_charset);
				new File(point_file_name).setLastModified(last_time);
			}
		face_file	=new file_reader(face_file_name, file_charset);
		edge_file	=new file_reader(edge_file_name, file_charset);
		point_file	=new file_reader(point_file_name,file_charset);
	}
}
