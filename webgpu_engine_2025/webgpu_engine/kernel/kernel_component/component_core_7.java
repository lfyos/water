package kernel_component;

import kernel_driver.component_driver;
import kernel_file_manager.file_reader;

public class component_core_7  extends component_core_6
{
	public void reset_component(component_container component_cont,component parent)
	{
		uniparameter.do_response_location_flag=true;

		caculate_location(component_cont,true);

		if(parent==null)
			uniparameter.effective_selected_flag=uniparameter.selected_flag;
		else
			uniparameter.effective_selected_flag=parent.uniparameter.effective_selected_flag|uniparameter.selected_flag;
		
		for(int i=0,n=children.size();i<n;i++)
			children.get(i).reset_component(component_cont,(component)this);

		caculate_children_location_modify_flag();
		
		for(int i=0,ni=multiparameter.length;i<ni;i++) {
			caculate_effective_display_flag(i);
			caculate_assembly_flag(i);
		}
		caculate_box();
		
		uniparameter.discard_precision2=-1;
		for(int i=0,ni=driver_array.size();i<ni;i++) {
			component_driver c_d=driver_array.get(i);
			if(c_d.component_part.part_par.discard_precision2>0.0){
				if(uniparameter.discard_precision2<0.0)
					uniparameter.discard_precision2=c_d.component_part.part_par.discard_precision2;
				else if(c_d.component_part.part_par.discard_precision2<uniparameter.discard_precision2)
					uniparameter.discard_precision2=c_d.component_part.part_par.discard_precision2;
			}
		}
		for(int i=0,n=children.size();i<n;i++){
			double child_discard_precision2=children.get(i).uniparameter.discard_precision2;
			if(child_discard_precision2>0.0){
				if(uniparameter.discard_precision2<0.0)
					uniparameter.discard_precision2=child_discard_precision2;
				else if(uniparameter.discard_precision2>child_discard_precision2)
					uniparameter.discard_precision2=child_discard_precision2;
			}
		}
		if(uniparameter.discard_precision2<0)
			uniparameter.discard_precision2=1;
	}
	public component_core_7(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
	}
}
