package kernel_engine;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_interface.client_process_bar;
import kernel_part.part;

public class delete_part_files 
{
	static public void do_delete(
			ArrayList<part> list,client_process_bar cpb,
			system_parameter system_par,scene_parameter scene_par)
	{
		if(cpb!=null)
			cpb.set_process_bar(true, "delete_part_file","", 0,1);
		
		int number;
		if((number=list.size())<=0)
			number=1;
		
		if(cpb!=null)
			cpb.set_process_bar(true, "delete_part_file","", 0,number);
		
		for(int i=0,ni=list.size();i<ni;i++) {
			part p=list.get(i);
			if(cpb!=null)
				cpb.set_process_bar(false, "delete_part_file","",i,number);
			
			if(p.part_par==null)
				continue;
			
			String clear_file_name_array[]=new String[]
			{
				p.mesh_file_name,
				p.material_file_name,
				p.description_file_name,
				p.audio_file_name,
				p.mesh_file_name+".face",
				p.mesh_file_name+".face.gzip",
				p.mesh_file_name+".edge",
				p.mesh_file_name+".edge.gzip"
			};
			
			File f;
			
			for(int j=0,nj=clear_file_name_array.length;j<nj;j++)
				if(p.part_par.clear_model_file_flag[j])
					if((f=new File(p.directory_name+file_reader.separator(clear_file_name_array[j]))).exists())
						f.delete();

			String part_temporary_file_directory=file_directory.part_file_directory(p,system_par,scene_par);
			if(p.part_par.clear_buffer_head_file_flag)
				if((f=new File(part_temporary_file_directory+"mesh.head.gzip_text")).exists())
					f.delete();
			if(p.part_par.clear_buffer_boftal_file_flag)
				if((f=new File(part_temporary_file_directory+"mesh.boftal")).exists())
					if(f.length()>0){
						long t=f.lastModified();
						try{
							new FileWriter(f).close();
						}catch(Exception e) {
							;
						}
						f.setLastModified(t);
					}
		}
		if(cpb!=null)
			cpb.set_process_bar(false, "delete_part_file","", number,number);
	}
}
