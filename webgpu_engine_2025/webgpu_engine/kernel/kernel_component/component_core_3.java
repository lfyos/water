package kernel_component;

import java.util.ArrayList;

import kernel_part.part;
import kernel_file_manager.file_reader;

public class component_core_3 extends component_core_2
{
	public void destroy()
	{
		super.destroy();
	}
	private void decrease_children_number(file_reader fr,component_construction_parameter ccp)
	{
		int child_number,max_child_number;

		if((max_child_number=ccp.sk.scene_par.max_child_number)<=2)
			return;
		if((child_number=children.size())<=max_child_number)
			return;
		
		int new_child_number;
		if((new_child_number=(int)Math.sqrt(child_number))>max_child_number)
			new_child_number=max_child_number;
		if(new_child_number<2)
			new_child_number=2;
		
		ArrayList<component> bak_children=children;
		children=new ArrayList<component>();
		
		for(int i=0,collect_number=0;i<new_child_number;i++){
			String my_component_name,my_part_name;
			for(int j=0;;j++){
				String id_str		="_"+component_name+"_"+i+"_"+j;
				my_component_name	=ccp.sk.scene_par.inserted_component_name	+id_str;
				my_part_name		=ccp.sk.scene_par.inserted_part_name		+id_str;
				ArrayList<part>my_part_list=ccp.sk.part_cont.search_part(my_part_name);
				if(my_part_list==null)
					break;
				if(my_part_list.size()<=0)
					break;
			};
			fr.push_string(new String[]
			{
				my_component_name,
				my_part_name,
				"identity",
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
		}

		var my_component=this;
		for(component my_child:children){
			my_component=my_child;
			my_component.decrease_children_number(fr,ccp);
		}
	}
	public int append_component(file_reader fr,component_construction_parameter ccp)
	{
		int ret_val=ccp.clsc.add_component(fr,component_name,children,
			uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp);
		decrease_children_number(fr,ccp);
		for(int i=0,ni=children.size();i<ni;i++)
			ret_val+=children.get(i).append_component(fr,ccp);
		return ret_val;
	}
	public component_core_3(String token_string,file_reader fr,
			boolean part_list_flag,boolean normalize_location_flag,
			component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		ccp.clsc.add_component(fr,component_name,children,
			uniparameter.part_list_flag,uniparameter.normalize_location_flag,ccp);
		decrease_children_number(fr,ccp);
	}
}