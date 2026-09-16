package kernel_mesh;

import java.io.File;

import kernel_part.part_rude;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.debug_information;

public class load_part_mesh 
{
	public static part_rude load(String my_file_path_name,
			String my_tmp_directory_name,String file_charset)
	{
		part_rude part_mesh;
		String version_string;

		file_reader fr=new file_reader(my_file_path_name,file_charset);
		if(fr.error_flag()) {
			debug_information.println("load_part_mesh error:	",my_file_path_name);
			return null;
		}
		
		fr.mark_start();

		switch(((version_string=fr.get_string())==null)?"":version_string){
		case "2021.07.15":
			fr.close();
			var part_mesh_2021_07_15=new part_rude_2021_07_15(my_file_path_name,file_charset);
			file_writer fw=new file_writer(my_tmp_directory_name+"mesh.tmp",file_charset);
			part_mesh_2021_07_15.write_out(fw);
			fw.close();
			fr=new file_reader(fw.directory_name+fw.file_name,file_charset);
			part_mesh=new part_rude(fr);
			fr.close();
			new File(fr.directory_name+fr.file_name).delete();
			
			return part_mesh;
		case "simple":
		case "2026.09.16":
			fr.mark_terminate(true);
			part_mesh=new part_rude(fr);
			fr.close();			
			return part_mesh;
		default:
			debug_information.println("Find no part mesh version:	",my_file_path_name);
			fr.close();
			return null;
		}
	}
}
