package kernel_part;

import kernel_common_class.tree_string_search_container;
import kernel_common_class.tree_search_container_tree_node;

public class permanent_part_id_encoder
{
	private tree_string_search_container<Integer>encoder_tree;
	
	public permanent_part_id_encoder()
	{
		encoder_tree=new tree_string_search_container<Integer>(null);
	}
	public int encoder(String part_type_string)
	{
		tree_search_container_tree_node<String,Integer> my_tree_node;
		if((my_tree_node=encoder_tree.search_tree_node(part_type_string))==null){
			encoder_tree.add(part_type_string,1);
			return 0;
		}else{
			int ret_val=my_tree_node.list.get(0);
			my_tree_node.list.set(0,ret_val+1);
			return ret_val;
		}
	}
}
