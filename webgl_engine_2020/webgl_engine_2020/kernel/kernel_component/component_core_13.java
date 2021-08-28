package kernel_component;

import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_engine.component_container;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;

public class component_core_13 extends component_core_12
{
	public String component_directory_name,component_file_name,component_charset;
	
	public void destroy()
	{
		super.destroy();
		
		component_directory_name=null;
		component_file_name=null;
		component_charset=null;
	}
	public void reset_component(component_container component_cont)
	{
		uniparameter.do_response_location_flag=true;
		
		should_caculate_location_flag=true;
		caculate_one_selection(component_cont);
		caculate_location(component_cont);
		
		for(int i=0,n=children_number();i<n;i++)
			children[i].reset_component(component_cont);

		caculate_children_location_modify_flag();
		
		for(int i=0,ni=multiparameter.length;i<ni;i++)
			caculate_assembly_flag(i);
		
		should_caculate_box_flag=true;
		caculate_box(true);
		
		for(int i=0,ni=multiparameter.length;i<ni;i++)
			caculate_effective_display_flag(i);
		
		uniparameter.discard_precision2=-1;
		for(int i=0,ni=driver_number();i<ni;i++)
			if(driver_array[i].component_part.part_par.discard_precision2>0.0){
				if(uniparameter.discard_precision2<0.0)
					uniparameter.discard_precision2=driver_array[i].component_part.part_par.discard_precision2;
				else if(driver_array[i].component_part.part_par.discard_precision2<uniparameter.discard_precision2)
					uniparameter.discard_precision2=driver_array[i].component_part.part_par.discard_precision2;
			}
		for(int i=0,n=children_number();i<n;i++){
			double child_discard_precision2=children[i].uniparameter.discard_precision2;
			if(child_discard_precision2>0.0){
				if(uniparameter.discard_precision2<0.0)
					uniparameter.discard_precision2=child_discard_precision2;
				else if(uniparameter.discard_precision2>child_discard_precision2)
					uniparameter.discard_precision2=child_discard_precision2;
			}
		}
	}
	public component_core_13(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			change_name reverse_mount_component_name,part_type_string_sorter type_string_sorter,
			boolean part_list_flag,long default_display_bitmap,int max_child_number)
	{
		super(token_string,ek,request_response,fr,pcfps,
			change_part_name,mount_component_name,reverse_mount_component_name,
			type_string_sorter,part_list_flag,default_display_bitmap,max_child_number);

		component_directory_name=fr.directory_name;
		component_file_name		=fr.file_name;
		component_charset		=fr.get_charset();
	}
}
