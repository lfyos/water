package kernel_interface;

import kernel_file_manager.file_reader;

public class user_statistics 
{
	public int user_component_number,user_kernel_number;
	public int max_user_component_number,max_user_kernel_number;
	
	public user_statistics(file_reader f)
	{
		user_component_number=0;
		user_kernel_number=0;
		max_user_component_number=f.get_int();
		max_user_kernel_number=f.get_int();
	}
}
