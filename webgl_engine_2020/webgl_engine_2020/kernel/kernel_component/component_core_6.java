package kernel_component;

import kernel_engine.engine_kernel;
import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_engine.part_type_string_sorter;

public class component_core_6 extends component_core_5
{
	public void destroy()
	{
		super.destroy();
	}
	private void decrease_children_number(String token_string,engine_kernel ek,
			client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			part_type_string_sorter type_string_sorter,
			boolean part_list_flag,long default_display_bitmap,int max_child_number)
	{
		if(max_child_number<=2)
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
				String id_str="_"+(ek.scene_par.inserted_component_and_part_id++);
				fr.insert_string(new String[]
				{
					ek.scene_par.inserted_component_name+id_str,
					ek.scene_par.inserted_part_name+id_str,
					"1","0","0","0",
					"0","1","0","0",
					"0","0","1","0",
					"0","0","0","1",
					"0"
				});
				children[i]=new component(token_string,ek,request_response,fr,pcfps,
					change_part_name,mount_component_name,type_string_sorter,
					part_list_flag,default_display_bitmap,max_child_number);

				children[i].children=new component[(bak_children.length-collect_number)/(ni-i)];
				for(int j=0,nj=children[i].children.length;j<nj;j++)
					children[i].children[j]=bak_children[collect_number++];
			}
		}
	}
	public component_core_6(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			part_type_string_sorter type_string_sorter,
			boolean part_list_flag,long default_display_bitmap,int max_child_number)
	{
		super(token_string,ek,request_response,fr,pcfps,
			change_part_name,mount_component_name,type_string_sorter,
			part_list_flag,default_display_bitmap,max_child_number);
		
		if(children!=null){
			if(children.length<=0)
				children=null;
			else if((children.length>max_child_number)&&(max_child_number>2))
				decrease_children_number(token_string,ek,request_response,fr,pcfps,
					change_part_name,mount_component_name,type_string_sorter,
					uniparameter.part_list_flag,default_display_bitmap,max_child_number);
		}
		for(int i=0,ni=children_number();i<ni;i++)
			if(uniparameter.file_last_modified_time<children[i].uniparameter.file_last_modified_time)
				uniparameter.file_last_modified_time=children[i].uniparameter.file_last_modified_time;
	}
}