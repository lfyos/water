package kernel_component;

import java.util.ArrayList;

import kernel_part.part;
import kernel_file_manager.file_reader;

public class component_core_4 extends component_core_3
{
	public void destroy()
	{
		super.destroy();
	}
	private void decrease_children_number(file_reader fr,component_construction_parameter ccp)
	{
		int child_number,max_child_number;
		if((child_number=children.size())<=1)
			return;
		if((max_child_number=ccp.sk.scene_par.max_child_number)<=2)
			return;
		if(child_number<=max_child_number)
			return;
		
		int new_child_number;
		if((new_child_number=(int)Math.sqrt(child_number))>max_child_number)
			new_child_number=max_child_number;
		if(new_child_number<2)
			new_child_number=2;
		
		ArrayList<component> bak_children=children;
		children=new ArrayList<component>();
		
		ArrayList<component> decrease_children=new ArrayList<component>();
		
		for(int collect_number=0,i=0;i<new_child_number;i++){
			String my_component_name,my_part_name;
			do{
				String id_str="_"+(ccp.sk.scene_par.inserted_component_and_part_id++);
				my_component_name=ccp.sk.scene_par.inserted_component_name+id_str;
				my_part_name=ccp.sk.scene_par.inserted_part_name+id_str;
				ArrayList<part> my_part_list=ccp.pcfps.search_part(my_part_name);
				if(my_part_list!=null)
					if(my_part_list.size()>0)
						continue;
			}while(false);
			
			fr.push_string_array(new String[]
			{
				my_component_name,
				my_part_name,
				"1","0","0","0",
				"0","1","0","0",
				"0","0","1","0",
				"0","0","0","1",
				"0"
			});
			component my_comp=new component("",fr,
				uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp);
			my_comp.uniparameter.file_last_modified_time=uniparameter.file_last_modified_time;
			my_comp.children=new ArrayList<component>();
			int my_child_number=(bak_children.size()-collect_number)/(new_child_number-i);
			for(int j=0;j<my_child_number;j++)
				my_comp.children.add(bak_children.get(collect_number++));
			
			children.add(my_comp);
			if(my_comp.children.size()>max_child_number)
				decrease_children.add(my_comp);
		}
		
		var child_comp=this;
		for(int i=0,ni=decrease_children.size();i<ni;i++){
			child_comp=decrease_children.get(i);
			child_comp.decrease_children_number(fr,ccp);
		}
	}
	public int append_component(file_reader fr,component_construction_parameter ccp)
	{
		if(ccp.clsc.get_source_item_number()<=0)
			return 0;
		int ret_val=ccp.clsc.add_component(component_name,children,
			uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp);
		if(ret_val>0)
			decrease_children_number(fr,ccp);
		for(int i=0,ni=children.size();i<ni;i++)
			ret_val+=children.get(i).append_component(fr,ccp);
		return ret_val;
	}
	public component_core_4(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		if(ccp.clsc.get_source_item_number()>0)
			ccp.clsc.add_component(component_name,children,
				uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp);
		decrease_children_number(fr,ccp);
	}
}