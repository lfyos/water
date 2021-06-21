package convert_old_part_rude_2021_07_01;


import kernel_file_manager.file_reader;
import kernel_transformation.point;
import kernel_transformation.box;

public class edge_tesslation_external_point implements edge_tessellation
{
	private int current_point_id,load_number,point_number;
	
	private point tessellation_point[];
	private String tessellation_extra_data[],tessellation_material[][];
	
	private void test(int index_id,auxiliary_file_handler f)
	{
		if(load_number==0){
			tessellation_point		[0]=new point(f.edge_file);
			tessellation_extra_data	[0]=f.edge_file.get_string();
			tessellation_material	[0]=new String[] {
					f.edge_file.get_string(),
					f.edge_file.get_string(),
					f.edge_file.get_string(),
					f.edge_file.get_string()
			};
			load_number++;
		}
		
		if(load_number==1) {
			if(index_id==0)
				return;
			tessellation_point		[1]=new point(f.edge_file);
			tessellation_extra_data	[1]=f.edge_file.get_string();
			tessellation_material	[1]=new String[] {
					f.edge_file.get_string(),
					f.edge_file.get_string(),
					f.edge_file.get_string(),
					f.edge_file.get_string()
			};
			load_number++;
		}

		for(;(current_point_id+1)<index_id;current_point_id++) {
			tessellation_point		[0]=tessellation_point		[1];
			tessellation_extra_data	[0]=tessellation_extra_data	[1];
			tessellation_material	[0]=tessellation_material	[1];
					
			tessellation_point		[1]=new point(f.edge_file);
			tessellation_extra_data	[1]=f.edge_file.get_string();
			tessellation_material	[1]=new String[] {
					f.edge_file.get_string(),
					f.edge_file.get_string(),
					f.edge_file.get_string(),
					f.edge_file.get_string()
			};
			load_number++;
		}
	}
	
	public int tessellation_point_number()
	{
		return point_number;
	};
	
	public point get_tessellation_point(int index_id,auxiliary_file_handler f)
	{
		test(index_id,f);
		
		switch(index_id-current_point_id){
		default:
		case 0:
			return tessellation_point[0];
		case 1:
			return tessellation_point[1];
		}
	};
	public String get_tessellation_extra_data(int index_id,auxiliary_file_handler f)
	{
		test(index_id,f);
		
		switch(index_id-current_point_id) {
		default:
		case 0:
			return tessellation_extra_data[0];
		case 1:
			return tessellation_extra_data[1];
		}
	};
	public String[] get_tessellation_material(int index_id,auxiliary_file_handler f)
	{
		test(index_id,f);
		
		switch(index_id-current_point_id) {
		default:
		case 0:
			return tessellation_material[0];
		case 1:
			return tessellation_material[1];
		}
	};
	public edge_tessellation clone()
	{
		return this;
	};
	public void destroy()
	{
		tessellation_point=null;
		tessellation_extra_data=null;
		tessellation_material=null;
	};
	public edge_tesslation_external_point(file_reader fr,face_edge edge)
	{
		point_number=fr.get_int();
		edge.edge_box=new box(fr);
		
		tessellation_point		=new point[2];
		tessellation_extra_data	=new String[2];
		tessellation_material	=new String[2][];
		
		current_point_id=0;
		load_number=0;
	}
}