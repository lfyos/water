package kernel_interface;

import kernel_file_manager.file_reader;

public class user_statistics 
{
	public int user_engine_kernel_number,user_engine_component_number,user_engine_part_face_number,user_engine_part_edge_number;
	public int user_max_engine_kernel_number,user_max_engine_component_number,user_max_engine_part_face_number,user_max_engine_part_edge_number;

	public user_statistics(file_reader f)
	{
		user_engine_kernel_number=0;
		user_engine_component_number=0;
		user_engine_part_face_number=0;
		user_engine_part_edge_number=0;
		user_max_engine_kernel_number	=f.get_int();
		user_max_engine_component_number=f.get_int();
		user_max_engine_part_face_number=f.get_int();
		user_max_engine_part_edge_number=f.get_int();
	}
}
