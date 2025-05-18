package kernel_component;

import kernel_file_manager.file_reader;

public class component extends component_core_7
{
	public void destroy()
	{
		super.destroy();
	}
	public component(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
	}
}