package kernel_part;

import java.util.ArrayList;

import kernel_common_class.tree_string_search_container;

public class permanent_part_id_encoder
{
	class encoder_part_id
	{
		public int permanent_part_id;
		public encoder_part_id()
		{
			permanent_part_id=0;
		}
	}
	private tree_string_search_container<encoder_part_id> encoder_tree;
	public permanent_part_id_encoder()
	{
		encoder_tree=new tree_string_search_container<encoder_part_id>();
	}
	public int encoder(String part_type_string)
	{
		ArrayList<encoder_part_id>list;

		for(String my_key[]=new String[]{part_type_string};(list=encoder_tree.search(my_key))==null;)
			encoder_tree.add(my_key,new encoder_part_id());
		
		return list.get(0).permanent_part_id++;
	}
}
