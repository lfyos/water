package kernel_part;

import java.util.ArrayList;

import kernel_common_class.tree_string_search_container;

public class permanent_part_id_encoder
{
	private tree_string_search_container<Integer> encoder_tree;
	
	public permanent_part_id_encoder()
	{
		encoder_tree=new tree_string_search_container<Integer>();
	}
	public int encoder(String part_type_string)
	{
		String my_key[]=new String[]{part_type_string};
		ArrayList<Integer>list=encoder_tree.search(my_key);
		if(list==null) {
			encoder_tree.add(my_key,1);
			return 0;
		}else{
			int ret_val=list.get(0);
			list.set(0,ret_val+1);
			return ret_val;
		}
	}
}
