package kernel_engine;

import java.io.File;

import kernel_common_class.debug_information;
import kernel_common_class.sorter;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_interface.client_process_bar;
import kernel_part.part;

public class part_boftal_creator extends sorter <part,String>
{
	private system_parameter system_par;
	private scene_parameter scene_par;
	
	public int compare_data(part s,part t)
	{
		String dir_s=file_directory.part_file_directory(s,system_par,scene_par);
		String dir_t=file_directory.part_file_directory(t,system_par,scene_par);
		return dir_s.compareTo(dir_t);
	}
	public int compare_key(part s,String t)
	{
		return file_directory.part_file_directory(s,system_par,scene_par).compareTo(t);
	}
	public part_boftal_creator(int part_type_id,
			String file_name,String file_charset,part my_data_array[],
			system_parameter my_system_par,scene_parameter my_scene_par,
			client_process_bar process_bar,String process_bar_title)
	{
		system_par=my_system_par;
		scene_par=my_scene_par;
		data_array=my_data_array;
		int part_number=data_array.length;
		
		debug_information.println("Create boftal file: start.....	",process_bar_title);
		debug_information.println("Total part number:"+data_array.length,",created part number:"+part_number);

		File f;
		if((f=new File(file_name)).exists()) 
			if(f.length()>0){
				boolean exit_flag=true;
				long boftal_last_time=f.lastModified();
		
				for(int i=0;i<part_number;i++)
					if(data_array[i].boftal.buffer_object_head_last_modify_time>=boftal_last_time) {
						exit_flag=false;
						break;
					}
				if(exit_flag) 
					return;
			}

		do_sort();

		if((process_bar!=null)&&(process_bar_title!=null))
			process_bar.set_process_bar(true, process_bar_title, "",0,part_number);

		int cut_directory_length=system_par.proxy_par.proxy_data_root_directory_name.length();
		file_writer fw=new  file_writer(file_name,file_charset);
		fw.println(part_number);
		
		for(int i=0;i<part_number;i++) {
			if((process_bar!=null)&&(process_bar_title!=null))
				process_bar.set_process_bar(false, process_bar_title, "",i,part_number);
			
			String part_temporary_file_directory=file_directory.part_file_directory(data_array[i],system_par,scene_par);
			String boftal_file_name=part_temporary_file_directory+"mesh.boftal";
			fw.println(part_temporary_file_directory.substring(cut_directory_length));

			file_reader fr=new file_reader(boftal_file_name,fw.get_charset());
			for(String str;!(fr.eof());)
				if((str=fr.get_string())!=null)
					fw.println(str);
			fr.close();
			fw.println();
		}		
		fw.close();
		
		if((process_bar!=null)&&(process_bar_title!=null))
			process_bar.set_process_bar(false, process_bar_title,"",part_number,part_number);

		debug_information.println(
			"Create boftal file: finished!	",(process_bar_title==null)?"":process_bar_title);
	}
}
