package kernel_component;

import kernel_file_manager.file_reader;

public class component_core_6 extends component_core_5
{
	public boolean children_location_modify_flag;
	
	public void destroy()
	{
		super.destroy();
	}
	public void caculate_children_location_modify_flag()
	{
		int n;
		if((n=children_number())<=0){	
			children_location_modify_flag=false;
			return;
		}
		for(int i=0;i<n;i++)
			if(children[i].children_location_modify_flag){
				children_location_modify_flag=true;
				return;
			}
		for(int i=0;i<n;i++)
			if(children[i].move_location.is_not_identity_matrix()){
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