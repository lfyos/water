package kernel_information;

import kernel_component.component_collector;
import kernel_scene.client_information;

public class component_collector_information extends jason_creator
{
	private component_collector cc;
	
	public void print()
	{
		print("render_number",				cc.render_number);
		print("part_number",				cc.part_number);
		print("component_number",			cc.component_number);
		print("render_part_number",			cc.render_part_number);
		print("render_component_number",	cc.render_component_number);
		print("part_component_number",		cc.part_component_number);
		
		print("total_face_primitive_number",cc.total_face_primitive_number);
		print("total_edge_primitive_number",cc.total_edge_primitive_number);
		print("total_point_primitive_number",cc.total_point_primitive_number);

		print("list_id",					cc.list_id);
		
		print("title",						cc.title);
		print("description",				cc.description);
		print("audio_file_name",			cc.audio_file_name);
	}
	public component_collector_information(component_collector my_cc,client_information my_ci)
	{
		super(my_ci.request_response);
		cc=my_cc;
	}
}
