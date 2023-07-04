package kernel_part;

public interface primitive_interface 
{
	public String[]get_primitive_material(int body_id,int face_id,int primitive_id);
	public int get_primitive_vertex_number(int body_id,int face_id,int primitive_id);
	
	public double[]get_primitive_vertex_location_data(int body_id,int face_id,int primitive_id,int vertex_id);
	public String get_primitive_vertex_location_extra_data(int body_id,int face_id,int primitive_id,int vertex_id);
	
	public double[]get_primitive_vertex_normal_data(int body_id,int face_id,int primitive_id,int vertex_id);
	public String get_primitive_vertex_normal_extra_data(int body_id,int face_id,int primitive_id,int vertex_id);
	
	public double[]get_primitive_vertex_attribute_data(int body_id,int face_id,int primitive_id,int vertex_id,int attribute_id);
	public String get_primitive_vertex_attribute_extra_data(int body_id,int face_id,int primitive_id,int vertex_id,int attribute_id);

	public double[]get_edge_location_data(int body_id,int face_id,int loop_id,int edge_id,int point_id);
	public String get_edge_extra_data(int body_id,int face_id,int loop_id,int edge_id,int point_id);
	public String[] get_edge_material(int body_id,int face_id,int loop_id,int edge_id,int point_id);
	
	public double[]get_point_location_data(int body_id,int face_id,int loop_id,int edge_id,int point_id);
	public String get_point_extra_data(int body_id,int face_id,int loop_id,int edge_id,int point_id);
	public String[] get_point_material(int body_id,int face_id,int loop_id,int edge_id,int point_id);
	
	public void destroy();
}
