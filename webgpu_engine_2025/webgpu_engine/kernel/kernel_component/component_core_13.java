package kernel_component;

import kernel_file_manager.file_reader;

public class component_core_13 extends component_core_12
{
	private long component_bundle_render_reference_number;
	
	public void increase_component_bundle_render_reference_number(component_container component_cont)
	{
		component parent_comp;
		component_bundle_render_reference_number++;
		if((parent_comp=component_cont.get_component(this.parent_component_id))!=null)
			parent_comp.increase_component_bundle_render_reference_number(component_cont);
	}
	public void decrease_component_bundle_render_reference_number(component_container component_cont)
	{
		component parent_comp;
		component_bundle_render_reference_number--;
		if((parent_comp=component_cont.get_component(this.parent_component_id))!=null)
			parent_comp.decrease_component_bundle_render_reference_number(component_cont);
	}
	public long get_component_bundle_render_reference_number()
	{
		return component_bundle_render_reference_number;
	}
	public long caculate_component_bundle_render_reference_number()
	{
		component_bundle_render_reference_number=0;
		for(int i=0,ni=children_number();i<ni;i++)
			component_bundle_render_reference_number+=children[i].caculate_component_bundle_render_reference_number();
		for(int i=0,ni=driver_number();i<ni;i++)
			if(driver_array.get(i).component_driver_can_not_bundle_render_flag)
				component_bundle_render_reference_number++;
		return component_bundle_render_reference_number;
	}
	public component_core_13(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		component_bundle_render_reference_number=0;
	}
}
