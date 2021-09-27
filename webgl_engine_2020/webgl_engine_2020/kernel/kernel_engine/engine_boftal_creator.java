package kernel_engine;

import java.io.File;
import java.io.FileWriter;

import kernel_render.render;
import kernel_part.part;
import kernel_common_class.sorter;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_interface.client_process_bar;

public class engine_boftal_creator extends sorter <part,String>
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
		String dir_s=file_directory.part_file_directory(s,system_par,scene_par);
		return dir_s.compareTo(t);
	}
	public engine_boftal_creator(String file_name,String file_charset,render renders[],
		system_parameter my_system_par,scene_parameter my_scene_par,client_process_bar process_bar)
	{
		system_par=my_system_par;
		scene_par=my_scene_par;
		
		part p;
		String str;
		int number=0;
		boolean should_not_create_flag=true;
		long last_time=new File(file_name).lastModified();
		for(int render_id=0,render_number=renders.length;render_id<render_number;render_id++)
			if(renders[render_id]!=null)
				if(renders[render_id].parts!=null)
					for(int part_id=0,part_number=renders[render_id].parts.length;part_id<part_number;part_id++)
						if((p=renders[render_id].parts[part_id]).part_par.engine_boftal_flag)
							switch(p.part_type_id) {
							case 1:
							case 2:
								number++;
								if(p.boftal.buffer_object_head_last_modify_time>=last_time) 
									should_not_create_flag=false;
								break;
							}
		if(should_not_create_flag)
			return;
		
		process_bar.set_process_bar(true, "create_buffer_object_file", 0, number);
		
		data_array=new part[number];
		number=0;
		for(int render_id=0,render_number=renders.length;render_id<render_number;render_id++)
			if(renders[render_id]!=null)
				if(renders[render_id].parts!=null)
					for(int part_id=0,part_number=renders[render_id].parts.length;part_id<part_number;part_id++)
						if((p=renders[render_id].parts[part_id]).part_par.engine_boftal_flag)
							switch(p.part_type_id) {
							case 1:
							case 2:
								data_array[number++]=p;
								break;
							}
		
		do_sort(-1,new part[number]);
		
		
		int cut_directory_length=system_par.proxy_par.proxy_data_root_directory_name.length();
		file_writer fw=new  file_writer(file_name,file_charset);
		
		fw.println(number);
		
		for(int i=0;i<number;i++) {
			process_bar.set_process_bar(false, "create_buffer_object_file", i, number);

			String part_temporary_file_directory=file_directory.part_file_directory(data_array[i],system_par,scene_par);
			String boftal_file_name=part_temporary_file_directory+"mesh.boftal";
			fw.println(part_temporary_file_directory.substring(cut_directory_length));

			file_reader fr=new file_reader(boftal_file_name,fw.get_charset());
			while(!(fr.eof()))
				if((str=fr.get_string())!=null)
					fw.println(str);
			fr.close();
						
			if(scene_par!=null)
				if(scene_par.scene_fast_load_flag)
					if(data_array[i].part_par.engine_boftal_flag)
						if(data_array[i].part_par.free_part_memory_flag)
							switch(data_array[i].part_type_id){
							case 2:
								File f=new File(boftal_file_name);
								long t=f.lastModified();
								try{
									if(f.exists())
										new FileWriter(boftal_file_name).close();
									else
										f.createNewFile();
								}catch(Exception e) {
									;
								}
								f.setLastModified(t);
								break;
							}
			fw.println();
		}
		
		fw.close();
		
		process_bar.set_process_bar(false, "create_buffer_object_file",number,number);
	}
}
