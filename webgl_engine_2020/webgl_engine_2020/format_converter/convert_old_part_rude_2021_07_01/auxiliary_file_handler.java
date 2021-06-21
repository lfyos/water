package convert_old_part_rude_2021_07_01;

import java.io.File;
import kernel_file_manager.file_reader;

public class auxiliary_file_handler 
{
	public file_reader face_file,edge_file;
	
	public auxiliary_file_handler(String file_name,String file_charset)
	{
		face_file=null;
		edge_file=null;
		
		String face_file_name	=file_name+".face";
		String edge_file_name	=file_name+".edge";
		
		if(new File(face_file_name).exists())
			face_file=new file_reader(face_file_name,file_charset);
		if(new File(edge_file_name).exists())
			edge_file=new file_reader(edge_file_name,file_charset);
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
