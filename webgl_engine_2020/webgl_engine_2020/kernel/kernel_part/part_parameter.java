package kernel_part;

import kernel_file_manager.file_reader;

public class part_parameter
{
	public String	part_type_string,assemble_part_name,directory_name,file_name;
	public long 	last_modified_time;
	
	public int		process_sequence_id;
	public long		max_file_head_length,max_file_data_length,max_buffer_object_data_length,max_comment_file_length,max_compress_file_length;
	public double	scale_value,lod_precision_scale;
	public double	assembly_precision2,discard_precision2,bottom_box_discard_precision2;
	public long		create_face_buffer_object_bitmap,create_edge_buffer_object_bitmap,create_point_buffer_object_bitmap;
	public int 		max_component_data_buffer_number,max_part_load_thread_number;
	public int 		max_mesh_load_thread_number,max_part_list_load_thread_number;
	
	public boolean	do_create_bottom_box_flag,combine_to_part_package_flag;
	public boolean	delete_buffer_object_text_file_flag,clear_model_file_flag[];

	public part_parameter(
			String	my_part_type_string,
			String	my_assemble_part_name,
			String	my_directory_name,
			String	my_file_name,
			
			long	my_last_modified_time,
			
			int		my_process_sequence_id,
			
			long	my_max_file_head_length,
			long	my_max_file_data_length,
			long	my_max_buffer_object_data_length,
			long	my_max_comment_file_length,
			long	my_max_compress_file_length,
			
			double	my_scale_value,
			double	my_lod_precision_scale,
			
			double	my_assembly_precision2,
			double	my_discard_precision2,
			double	my_bottom_box_discard_precision2,
			
			long	my_create_face_buffer_object_bitmap,
			long	my_create_edge_buffer_object_bitmap,
			long	my_create_point_buffer_object_bitmap,
			
			int		my_max_component_data_buffer_number,
			int		my_max_part_load_thread_number,
			int		my_max_mesh_load_thread_number,
			int		my_max_part_list_load_thread_number,
			
			boolean	my_do_create_bottom_box_flag,
			boolean my_combine_to_part_package_flag,
			boolean my_delete_buffer_object_text_file_flag,
			boolean my_clear_model_file_flag[])
	{
		part_type_string				=my_part_type_string;
		assemble_part_name				=my_assemble_part_name;
		directory_name					=my_directory_name;
		file_name						=my_file_name;
		
		last_modified_time				=my_last_modified_time;
		
		process_sequence_id				=my_process_sequence_id;
		
		max_file_head_length			=my_max_file_head_length;
		max_file_data_length			=my_max_file_data_length;
		max_buffer_object_data_length	=my_max_buffer_object_data_length;
		max_comment_file_length			=my_max_comment_file_length;
		max_compress_file_length		=my_max_compress_file_length;
		
		scale_value						=my_scale_value;
		lod_precision_scale				=my_lod_precision_scale;
		assembly_precision2				=my_assembly_precision2;
		discard_precision2				=my_discard_precision2;
		bottom_box_discard_precision2	=my_bottom_box_discard_precision2;
		
		create_face_buffer_object_bitmap=my_create_face_buffer_object_bitmap;
		create_edge_buffer_object_bitmap=my_create_edge_buffer_object_bitmap;
		create_point_buffer_object_bitmap=my_create_point_buffer_object_bitmap;
		
		max_component_data_buffer_number=(my_max_component_data_buffer_number<=0)?1:my_max_component_data_buffer_number;
		max_part_load_thread_number		=(my_max_part_load_thread_number<=0)	?1:my_max_part_load_thread_number;
		max_mesh_load_thread_number		=(my_max_mesh_load_thread_number<=0)	?1:my_max_mesh_load_thread_number;
		max_part_list_load_thread_number=(my_max_part_list_load_thread_number<=0)?1:my_max_part_list_load_thread_number;
		
		do_create_bottom_box_flag			=my_do_create_bottom_box_flag;
		combine_to_part_package_flag		=my_combine_to_part_package_flag;
		delete_buffer_object_text_file_flag	=my_delete_buffer_object_text_file_flag;
		clear_model_file_flag				=new boolean[my_clear_model_file_flag.length];
		for(int i=0,ni=clear_model_file_flag.length;i<ni;i++)
			clear_model_file_flag[i]=my_clear_model_file_flag[i];
	}
	
	public part_parameter clone()
	{
		return new part_parameter(
				part_type_string,
				assemble_part_name,
				directory_name,
				file_name,
				
				last_modified_time,
				
				process_sequence_id,
				
				max_file_head_length,
				max_file_data_length,
				max_buffer_object_data_length,
				max_comment_file_length,
				max_compress_file_length,
				
				scale_value,
				lod_precision_scale,
				
				assembly_precision2,
				discard_precision2,
				bottom_box_discard_precision2,
				
				create_face_buffer_object_bitmap,
				create_edge_buffer_object_bitmap,
				create_point_buffer_object_bitmap,
				
				max_component_data_buffer_number,
				max_part_load_thread_number,
				max_mesh_load_thread_number,
				max_part_list_load_thread_number,

				do_create_bottom_box_flag,
				combine_to_part_package_flag,
				delete_buffer_object_text_file_flag,
				clear_model_file_flag);
	}
	
	public part_parameter box_part_parameter()
	{
		return new part_parameter(
				part_type_string,
				assemble_part_name,
				directory_name,
				file_name,
				
				last_modified_time,
				
				process_sequence_id,
				
				max_file_head_length,
				max_file_data_length,
				max_buffer_object_data_length,
				max_comment_file_length,
				max_compress_file_length,
				
				1.0,
				lod_precision_scale,
				
				assembly_precision2,
				bottom_box_discard_precision2,
				bottom_box_discard_precision2/2.0,
				
				create_face_buffer_object_bitmap,
				create_edge_buffer_object_bitmap,
				create_point_buffer_object_bitmap,
				
				max_component_data_buffer_number,
				max_part_load_thread_number,
				max_mesh_load_thread_number,
				max_part_list_load_thread_number,

				false,
				combine_to_part_package_flag,
				delete_buffer_object_text_file_flag,
				clear_model_file_flag);
	}
	
	public part_parameter(
			String my_part_type_string,String my_assemble_part_name,
			String parameter_file_name,String file_system_charset)
	{
		file_reader f=new file_reader(parameter_file_name,file_system_charset);
	
		part_type_string					=my_part_type_string;
		assemble_part_name					=my_assemble_part_name;
		directory_name						=f.directory_name;
		file_name							=f.file_name;
		
		last_modified_time					=f.lastModified_time;

		process_sequence_id					=f.get_int();
		
		max_file_head_length				=f.get_long();
		max_file_data_length				=f.get_long();
		max_buffer_object_data_length		=f.get_long();
		max_comment_file_length				=f.get_long();
		max_compress_file_length			=f.get_long();
		
		scale_value							=f.get_double();
		lod_precision_scale					=f.get_double();
		
		assembly_precision2					=f.get_double();	assembly_precision2		*=assembly_precision2;
		discard_precision2					=f.get_double();	discard_precision2		*=discard_precision2;

		bottom_box_discard_precision2		=f.get_double();	bottom_box_discard_precision2*=bottom_box_discard_precision2;
		
		create_face_buffer_object_bitmap	=f.get_long();
		create_edge_buffer_object_bitmap	=f.get_long();
		create_point_buffer_object_bitmap	=f.get_long();
		
		max_component_data_buffer_number	=((max_component_data_buffer_number=f.get_int())<=0)?1:max_component_data_buffer_number;
		max_part_load_thread_number			=((max_part_load_thread_number=f.get_int())<=0)?1:max_part_load_thread_number;
		max_mesh_load_thread_number			=((max_mesh_load_thread_number=f.get_int())<=0)?1:max_mesh_load_thread_number;
		max_part_list_load_thread_number	=((max_part_list_load_thread_number=f.get_int())<=0)?1:max_part_list_load_thread_number;
		
		do_create_bottom_box_flag			=f.get_boolean();
		combine_to_part_package_flag		=f.get_boolean();
		delete_buffer_object_text_file_flag	=f.get_boolean();
		clear_model_file_flag				=new boolean[]
		{
			f.get_boolean(),
			f.get_boolean(),
			f.get_boolean(),
			f.get_boolean(),
			f.get_boolean(),
			f.get_boolean(),
			f.get_boolean()
		};
		
		f.close();
	}
}
