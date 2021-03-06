package kernel_engine;

import java.io.File;

import kernel_file_manager.file_directory;
import kernel_file_manager.file_writer;
import kernel_program_reader.program_file_reader;
import kernel_render.render_container;
import kernel_common_class.common_reader;
import kernel_common_class.debug_information;

public class copy_program 
{
	public static long copy_shader_programs(
			render_container render_cont,system_parameter system_par,scene_parameter scene_par)
	{
		common_reader common_shader_reader=program_file_reader.get_system_program_reader("common");
		if(common_shader_reader==null) {
			debug_information.println("Cann't get reader of common shader");
			return 0;
		}

		long common_shader_last_time=common_shader_reader.lastModified_time;
		long ret_val=common_shader_last_time;
		for(int i=0,ni=render_cont.renders.length;i<ni;i++) {
			if(render_cont.renders[i].parts==null)
				continue;
			if(render_cont.renders[i].parts.length<=0)
				continue;
			String target_directory=file_directory.render_file_directory(
					render_cont.renders[i].parts[0].part_type_id,
					render_cont.renders[i].parts[0].permanent_render_id,
					system_par,scene_par);
			String lock_file_name=target_directory+"program.lock";
			String program_file_name=target_directory+"program.txt";
			long program_last_time=new File(program_file_name).lastModified();
			long source_last_time=common_shader_last_time;
			long my_last_time;
			
			String type_name[]=new String[] {"vertex","fragment","geometry","tess_control","tess_evalue"};
			for(int j=0,nj=type_name.length;j<nj;j++){
				common_reader system_shader_reader=program_file_reader.get_system_program_reader(type_name[j]);
				if(system_shader_reader!=null){
					if(source_last_time<(my_last_time=system_shader_reader.lastModified_time))
						source_last_time=my_last_time;
					system_shader_reader.close();
				}
				common_reader user_shader_reader=program_file_reader.get_render_program_reader(render_cont.renders[i],type_name[j]);
				if(user_shader_reader!=null){
					if(source_last_time<(my_last_time=user_shader_reader.lastModified_time))
						source_last_time=my_last_time;
					user_shader_reader.close();
				}
			}
			common_reader decode_reader=program_file_reader.get_render_program_reader(render_cont.renders[i],"decode");
			if(decode_reader!=null) {
				if(source_last_time<(my_last_time=decode_reader.lastModified_time))
					source_last_time=my_last_time;
				decode_reader.close();
			}
			common_reader draw_reader=program_file_reader.get_render_program_reader(render_cont.renders[i],"draw");
			if(draw_reader!=null){
				if(source_last_time<(my_last_time=draw_reader.lastModified_time))
					source_last_time=my_last_time;
				draw_reader.close();
			}
			
			if(source_last_time<program_last_time) {
				if(ret_val<program_last_time)
					ret_val=program_last_time;
				continue;
			}
			
			system_par.system_exclusive_name_mutex.lock(lock_file_name);
			file_writer fw=new file_writer(program_file_name,system_par.network_data_charset);
			
			fw.print  ("[",render_cont.renders[i].parts[0].permanent_render_id);
			fw.println(",");
			
			decode_reader=program_file_reader.get_render_program_reader(render_cont.renders[i],"decode");
			if(decode_reader!=null) {
				decode_reader.get_text(fw);
				decode_reader.close();
			}else {
				debug_information.println(
						"copy_shader_programs fail,can Not get decode program for : ",program_file_name);
				fw.print  ("null");
			}
			fw.println(",");
			
			draw_reader=program_file_reader.get_render_program_reader(render_cont.renders[i],"draw");
			if(draw_reader!=null) {
				draw_reader.get_text(fw);
				draw_reader.close();
			}else {
				debug_information.println(
						"copy_shader_programs fail,can Not get draw program for : ",program_file_name);
				fw.print  ("null");
			}
			fw.println(",");

			for(int j=0,nj=type_name.length;j<nj;j++){
				String program_str="",str="";
				
				common_reader system_shader_reader;
				if((system_shader_reader=program_file_reader.get_system_program_reader(type_name[j]))!=null) {
					if((str=system_shader_reader.get_text())==null)
						str="";
					common_shader_reader.close();
				}
				program_str+=str+"\n";str="";
				
				if((common_shader_reader=program_file_reader.get_system_program_reader("common"))!=null) {
					if((str=common_shader_reader.get_text())==null)
						str="";
					common_shader_reader.close();
				}
				program_str+=str+"\n";str="";

				common_reader user_shader_reader;
				if((user_shader_reader=program_file_reader.get_render_program_reader(render_cont.renders[i],type_name[j]))!=null) {
					if((str=user_shader_reader.get_text())==null) {
						str="";
						program_str="";
					}
					user_shader_reader.close();
				}else {
					str="";
					program_str="";
				}
				program_str+=str+"\n";
				program_str=program_str.replace("\n","\\n\\r").replace("\"","\\\"").replace("\r","\\n\\r");

				fw.print  ("\"",program_str);
				fw.println("\"",(j==(nj-1))?"":",");
			}
			fw.println("]");
			fw.close();
			
			system_par.system_exclusive_name_mutex.unlock(lock_file_name);
			
			if(ret_val<(program_last_time=new File(fw.directory_name+fw.file_name).lastModified()))
				ret_val=program_last_time;
		}
		return ret_val;
	}
}
