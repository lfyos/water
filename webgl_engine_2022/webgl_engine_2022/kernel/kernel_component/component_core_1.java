package kernel_component;

import kernel_file_manager.file_reader;

public class component_core_1 extends component_core_0
{
	public int component_id,parent_component_id;
	public String component_name,part_name; 
	
	public void destroy()
	{
		super.destroy();
		
		component_name=null;
		part_name=null;
	}
	public void caculate_one_selection(component_container component_cont)
	{
		component parent=component_cont.get_component(parent_component_id);
		uniparameter.effective_selected_flag=uniparameter.selected_flag;
		if(parent!=null)
			uniparameter.effective_selected_flag|=parent.uniparameter.effective_selected_flag;
	}
	public component_core_1(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,double lod_precision_scale,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,lod_precision_scale,ccp);
		
		component_id=-1;
		parent_component_id=-1;

		if((component_name=fr.get_string())==null)
			component_name=new String(token_string);
		else
			component_name=token_string+component_name;

		if((part_name=fr.get_string())==null)
			part_name="";
	}
}