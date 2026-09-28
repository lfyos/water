package kernel_part;

import java.util.ArrayList;

import kernel_common_class.tree_string_search_container;
import kernel_common_class.tree_search_container_tree_node;

public class permanent_part_id_encoder
{
	private ArrayList<tree_string_search_container<Integer>>encoder_tree_list;
	
	public permanent_part_id_encoder()
	{
		encoder_tree_list=new ArrayList<tree_string_search_container<Integer>>();
	}
	public int encoder(String part_type_string,int part_type_id)
	{
		if(part_type_id<0)
			return 0;
		while(encoder_tree_list.size()<=part_type_id)
			encoder_tree_list.add(new tree_string_search_container<Integer>(null));

		tree_search_container_tree_node <String,Integer> tree_node;
		tree_string_search_container<Integer> encoder_tree=encoder_tree_list.get(part_type_id);
		
		if((tree_node=encoder_tree.search_tree_node(part_type_string))==null){
			encoder_tree.add(part_type_string,1);
			return 0;
		}else{
			int ret_val=tree_node.list.get(0);
			tree_node.list.set(0,ret_val+1);
			return ret_val;
		}
	}
}
