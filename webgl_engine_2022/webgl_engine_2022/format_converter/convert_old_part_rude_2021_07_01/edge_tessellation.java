package convert_old_part_rude_2021_07_01;

import kernel_transformation.point;

public interface edge_tessellation 
{
	public int tessellation_point_number();
	public point get_tessellation_point(int index_id,auxiliary_file_handler f);
	public String get_tessellation_extra_data(int index_id,auxiliary_file_handler f);
	public String[] get_tessellation_material(int index_id,auxiliary_file_handler f);

	public edge_tessellation clone();
	
	public void destroy();
}