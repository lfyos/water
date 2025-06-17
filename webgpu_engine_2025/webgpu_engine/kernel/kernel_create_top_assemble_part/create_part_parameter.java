package kernel_create_top_assemble_part;

import kernel_part.part;
import kernel_part.part_parameter;

public class create_part_parameter 
{
	public static part_parameter create(
			part p,long my_last_modified_time,
			double create_top_part_assembly_precision2,
			double create_top_part_discard_precision2)
	{
		return new part_parameter(
			p.part_par.part_type_string,
			p.part_par.assemble_part_name,
			p.part_par.directory_name,
			p.part_par.file_name,
					
			p.part_par.render_load_assemble_type,
			p.part_par.part_load_assemble_type,
					
			my_last_modified_time,
					
			p.part_par.process_sequence_id,
					
			p.part_par.max_file_head_length,
			p.part_par.max_file_data_length,
			p.part_par.max_buffer_object_data_length,
					
			p.part_par.lod_precision_scale,
					
			create_top_part_assembly_precision2,
			create_top_part_discard_precision2,
			create_top_part_discard_precision2,
					
			p.part_par.create_face_buffer_object_bitmap,
			p.part_par.create_edge_buffer_object_bitmap,
			p.part_par.create_point_buffer_object_bitmap,
					
			p.part_par.max_part_load_thread_number,
					
			false,
					
			p.part_par.location_match_direction,
			p.part_par.symmetry_flag);
	}
}
