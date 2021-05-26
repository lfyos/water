package kernel_mesh;

import kernel_transformation.box;

public interface face_mesh 
{
	public int get_vertex_number(auxiliary_file_handler f);
	public double[] get_vertex(int index_id,auxiliary_file_handler f);
	public String get_vertex_extra_data(int index_id,auxiliary_file_handler f);
	
	public int get_normal_number(auxiliary_file_handler f);
	public double[] get_normal(int index_id,auxiliary_file_handler f);
	public String get_normal_extra_data(int index_id,auxiliary_file_handler f);
	
	public int get_attribute_number(int attribute_id,auxiliary_file_handler f);
	public double[] get_attribute(int index_id,int attribute_id,auxiliary_file_handler f);
	public String get_attribute_extra_data(int index_id,int attribute_id,auxiliary_file_handler f);

	public int get_attribute_number();
	public int get_primitive_number();
	public int get_primitive_vertex_number(int primitive_id,auxiliary_file_handler f);
	public String[] get_primitive_material(int primitive_id,auxiliary_file_handler f);
	public int get_primitive_vertex_index(int primitive_id,int index_id,auxiliary_file_handler f);
	public int get_primitive_normal_index(int primitive_id,int index_id,auxiliary_file_handler f);
	public int get_primitive_attribute_index(int primitive_id,int index_id,int attribute_id,auxiliary_file_handler f);
	
	public int add_attribute(double x,double y,double z,String w);
	
	public box get_face_box();
	public String[] get_box_material();
	
	public face_mesh clone();
	public void destroy();
}
