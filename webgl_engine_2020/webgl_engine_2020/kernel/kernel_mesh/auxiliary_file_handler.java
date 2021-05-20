package kernel_mesh;

import java.io.File;

import kernel_part.part;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class auxiliary_file_handler 
{
	public file_reader face_file,edge_file;
	
	public static void delete_auxiliary_file(part p)
	{
		String file_name=p.directory_name+p.mesh_file_name;
		String face_file_name	=file_name+".face";
		String edge_file_name	=file_name+".edge";
		
		if(new File(face_file_name).exists())
			file_writer.file_delete(face_file_name);
		if(new File(edge_file_name).exists())
			file_writer.file_delete(edge_file_name);
	}
	public static void delete_comment(part p,String charset)
	{
		String file_name=p.directory_name+p.mesh_file_name;
		String face_file_name	=file_name+".face";
		String edge_file_name	=file_name+".edge";
		
		if(new File(face_file_name).exists())
			file_writer.delete_comment(face_file_name,charset);
		if(new File(edge_file_name).exists())
			file_writer.delete_comment(edge_file_name,charset);
	}
	
	public auxiliary_file_handler(part p)
	{
		face_file=null;
		edge_file=null;
		
		if(p==null)
			return;
		if(p.mesh_file_name==null)
			return;
		String file_name=p.directory_name+p.mesh_file_name;
		String face_file_name	=file_name+".face";
		String edge_file_name	=file_name+".edge";
		
		if(new File(face_file_name).exists())
			face_file=new file_reader(face_file_name,p.file_charset);
		if(new File(edge_file_name).exists())
			edge_file=new file_reader(edge_file_name,p.file_charset);
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
	}
}
