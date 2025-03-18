package kernel_component;

import kernel_file_manager.file_reader;

public class component_core_5 extends component_core_4
{
	public void destroy()
	{
		super.destroy();
	}
	private void decrease_children_number(String token_string,file_reader fr,
			boolean part_list_flag,boolean normalize_location_flag,component_construction_parameter ccp)
	{
		if(children==null)
			return;
		if(children.length<=0) {
			children=null;
			return;
		}
		int max_child_number=ccp.sk.scene_par.max_child_number;
		if(children.length<=max_child_number)
			return;
		if(ccp.sk.scene_par.max_child_number<=2)
			return;
		
		for(int child_number;(child_number=children.length)>max_child_number;){
			if((child_number=(int)(Math.sqrt(child_number)))>max_child_number)
				child_number=max_child_number;
			if(child_number<2)
				child_number=2;
			
			component bak_children[]=children;
			
			if((children.length%child_number)==0)
				children=new component[0+(children.length/child_number)];
			else
				children=new component[1+(children.length/child_number)];
			
			if(bak_children.length<=children.length) {
				children=bak_children;
				return;
			}
			for(int collect_number=0,i=0,ni=children.length;i<ni;i++) {
				String id_str="_"+(ccp.sk.scene_par.inserted_component_and_part_id++);
				fr.push_string_array(new String[]
				{
					ccp.sk.scene_par.inserted_component_name+id_str,
					ccp.sk.scene_par.inserted_part_name+id_str,
					"1","0","0","0",
					"0","1","0","0",
					"0","0","1","0",
					"0","0","0","1",
					"0"
				});
				children[i]=new component(token_string,fr,part_list_flag,normalize_location_flag,ccp);

				children[i].children=new component[(bak_children.length-collect_number)/(ni-i)];
				for(int j=0,nj=children[i].children.length;j<nj;j++)
					children[i].children[j]=bak_children[collect_number++];
			}
		}
	}
	
	public component_core_5(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		decrease_children_number(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		for(int i=0,ni=children_number();i<ni;i++)
			if(uniparameter.file_last_modified_time<children[i].uniparameter.file_last_modified_time)
				uniparameter.file_last_modified_time=children[i].uniparameter.file_last_modified_time;
	}
}