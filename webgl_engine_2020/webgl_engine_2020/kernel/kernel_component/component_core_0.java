package kernel_component;

import kernel_common_class.change_name;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;

public class component_core_0 
{
	public component_clip clip;
	public component_uniparameter 	uniparameter;
	public component_multiparameter multiparameter[];
	public component_initialization initialization;
	
	public void destroy()
	{
		clip=null;
		uniparameter=null;
		if(multiparameter!=null) {
			for(int i=0,ni=multiparameter.length;i<ni;i++)
				multiparameter[i]=null;
			multiparameter=null;
		}
		if(initialization!=null) {
			initialization.destroy();
			initialization=null;
		}
	}
	public component_core_0(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			change_name reverse_mount_component_name,part_type_string_sorter type_string_sorter,
			boolean part_list_flag,long default_display_bitmap,int max_child_number)
	{
		clip=new component_clip();
		uniparameter=new component_uniparameter(fr.lastModified_time,part_list_flag);
		multiparameter=new component_multiparameter[ek.scene_par.multiparameter_number];
		for(int i=0,ni=multiparameter.length;i<ni;i++)
			multiparameter[i]=new component_multiparameter(default_display_bitmap);
		initialization=new component_initialization();
	}
}
