package kernel_mesh;

import kernel_part.part_rude;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

import java.io.File;

import kernel_common_class.debug_information;

public class load_part_mesh 
{
	public static part_rude load(String my_file_path,String file_charset)
	{
		part_rude part_mesh;
		String version_string;
		
		file_reader fr=new file_reader(my_file_path,file_charset);
		fr.mark_start();

		switch(((version_string=fr.get_string())==null)?"":version_string){
		case "2021.07.15":
			long last_time=fr.lastModified_time;
			fr.close();
			file_writer.file_copy(my_file_path,my_file_path+".old");
			var old_part_mesh=new part_rude_2021_07_15(my_file_path,file_charset);
			file_writer fw=new file_writer(my_file_path,file_charset);
			old_part_mesh.write_out(fw);
			fw.close();
			new File(my_file_path).setLastModified(last_time);
			fr=new file_reader(my_file_path,file_charset);
			part_mesh=new part_rude(fr);
			fr.close();
			file_writer.file_rename(my_file_path+".old",my_file_path);
			new File(my_file_path).setLastModified(last_time);
			return part_mesh;
		case "simple":
		case "2026.09.16":
			fr.mark_terminate(true);
			part_mesh=new part_rude(fr);
			fr.close();
			return part_mesh;
		default:
			debug_information.println("Find no part mesh version:	",my_file_path);
			fr.close();
			return null;
		}
	}
}
