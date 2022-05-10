package kernel_information;

import kernel_component.component_container;
import kernel_engine.client_information;

public class component_container_information extends jason_creator
{
	private String scene_directory_name,scene_file_name;
	private component_container cc;
	
	public void print()
	{
		print("scene_directory_name",			scene_directory_name);
		print("scene_file_name",				scene_file_name);
		print("root_component_id",				cc.root_component.component_id);
		print("part_component_number",			cc.part_component_number);
		print("exist_part_component_number",	cc.exist_part_component_number);
		print("top_assemble_component_number",	cc.top_assemble_component_number);
		print("total_face_primitive_number",	cc.total_face_primitive_number);
		print("total_edge_primitive_number",	cc.total_edge_primitive_number);
		print("total_point_primitive_number",	cc.total_point_primitive_number);
	}
	public component_container_information(
			String my_scene_directory_name,String my_scene_file_name,
			component_container my_cc,client_information my_ci)
	{
		super(my_ci.request_response);
		scene_directory_name=my_scene_directory_name;
		scene_file_name=my_scene_file_name;
		cc=my_cc;
	}
}
