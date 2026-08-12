package kernel_part;

import java.util.ArrayList;

import kernel_common_class.tree_search_container;

public class part_container_for_process_sequence extends tree_search_container<part,part>
{
	public ArrayList<part> data_list;
	
	public part_container_for_process_sequence(
			ArrayList<part> my_part_list,
			double my_box_distance_difference_scale,
			double my_buffer_data_length_difference_scale)
	{
		super(new comparator_for_part_container_for_process_sequence(
				my_box_distance_difference_scale,
				my_buffer_data_length_difference_scale));
		if(my_part_list!=null)
			for(var my_part:my_part_list)
				add(my_part,my_part);
		data_list=tree_get_value_list();
	}
}