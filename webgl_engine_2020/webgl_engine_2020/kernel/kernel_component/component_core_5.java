package kernel_component;

import java.io.File;

import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;

public class component_core_5 extends component_core_4
{
	public void destroy()
	{
		super.destroy();
	}
	private void install_mount_component(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			part_type_string_sorter type_string_sorter,
			long default_display_bitmap,int max_child_number)
	{
		for(int search_id;(search_id=mount_component_name.search(component_name))>=0;){
			String assemble_file_name=file_reader.separator(mount_component_name.data_array[search_id][1]);
			mount_component_name.delete(search_id);
			
			String directory_name_array[]=new String[] {
					fr.directory_name,
					ek.scene_directory_name+"assemble_component"+File.separatorChar,
					ek.scene_par.directory_name+"assemble_component"+File.separatorChar,
					ek.system_par.default_parameter_directory+"assemble_component"+File.separatorChar,
					""
			};
			String charset_name_array[]=new String[] {
					fr.get_charset(),
					ek.scene_charset,
					ek.scene_par.parameter_charset,
					ek.system_par.local_data_charset,
					ek.system_par.local_data_charset
			};
			for(int i=0,ni=directory_name_array.length;i<ni;i++)
				if(new File(directory_name_array[i]+assemble_file_name).exists()){
					file_reader mount_fr=new file_reader(
						directory_name_array[i]+assemble_file_name,charset_name_array[i]);
					debug_information.println("assemble_file_name:	",
							directory_name_array[i]+assemble_file_name);
					debug_information.println("assemble_file_charset:	",charset_name_array[i]);
					try{
						append_child(1,new component[]{
							new component(token_string,ek,request_response,mount_fr,pcfps,
									change_part_name,mount_component_name,type_string_sorter,
									uniparameter.part_list_flag,default_display_bitmap,max_child_number)});
					}catch(Exception e) {
						debug_information.println("Create scene fail: "+e.toString()+"	",
								directory_name_array[i]+assemble_file_name);
						e.printStackTrace();
					}
					mount_fr.close();
					break;
				}
		}
	}
	public component_core_5(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			part_type_string_sorter type_string_sorter,
			boolean part_list_flag,long default_display_bitmap,int max_child_number)
	{
		super(token_string,ek,request_response,fr,pcfps,change_part_name,mount_component_name,
				type_string_sorter,part_list_flag,default_display_bitmap,max_child_number);
		
		install_mount_component(token_string,ek,request_response,fr,pcfps,
				change_part_name,mount_component_name,type_string_sorter,
				default_display_bitmap,max_child_number);
	}
}