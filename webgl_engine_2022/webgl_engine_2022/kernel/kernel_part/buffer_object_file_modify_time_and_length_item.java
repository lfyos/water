package kernel_part;

public class buffer_object_file_modify_time_and_length_item
{
	public long 	buffer_object_file_last_modify_time;
	public long	 	buffer_object_text_file_length;
	public boolean buffer_object_file_in_head_flag;
	
	public buffer_object_file_modify_time_and_length_item(
			long		my_buffer_object_file_last_modify_time,
			long		my_buffer_object_text_file_length,
			boolean		my_buffer_object_file_in_head_flag)
	{
		buffer_object_file_last_modify_time	=my_buffer_object_file_last_modify_time;
		buffer_object_text_file_length		=my_buffer_object_text_file_length;
		buffer_object_file_in_head_flag		=my_buffer_object_file_in_head_flag;
	}
}

