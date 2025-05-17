package kernel_component;

import kernel_file_manager.file_reader;

public class component_core_6 extends component_core_5
{
	private boolean children_location_modify_flag;
	
	public void destroy()
	{
		super.destroy();
	}
	public boolean get_children_location_modify_flag()
	{
		return children_location_modify_flag;
	}
	public void caculate_children_location_modify_flag()
	{
		int i,child_number;
		
		for(i=0,child_number=children.size();i<child_number;i++)
			if(children.get(i).get_children_location_modify_flag()){
				children_location_modify_flag=true;
				return;
			}
		for(i=0;i<child_number;i++)
			if(children.get(i).move_location.is_not_identity_matrix()){
				children_location_modify_flag=true;
				return;
			}
		children_location_modify_flag=false;
		return;
	}
	
	public component_core_6(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);

		children_location_modify_flag=false;
	}
}