package kernel_component;

import kernel_file_manager.file_reader;
import kernel_driver.component_driver;

public class component_core_12  extends component_core_11
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
		for(int i=0,ni=driver_number();i<ni;i++) {
			component_driver c_d=driver_array.get(i);
			if(c_d.component_part.part_par.discard_precision2>0.0){
				if(uniparameter.discard_precision2<0.0)
					uniparameter.discard_precision2=c_d.component_part.part_par.discard_precision2;
				else if(c_d.component_part.part_par.discard_precision2<uniparameter.discard_precision2)
					uniparameter.discard_precision2=c_d.component_part.part_par.discard_precision2;
			}
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
	public component_core_12(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,double lod_precision_scale,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,lod_precision_scale,ccp);

		component_directory_name=fr.directory_name;
		component_file_name		=fr.file_name;
		component_charset		=fr.get_charset();
	}
}
