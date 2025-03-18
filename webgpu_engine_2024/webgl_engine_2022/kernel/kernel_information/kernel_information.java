package kernel_information;

import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class kernel_information extends jason_creator
{
	private scene_kernel sk;
	private client_information ci;
	
	public void print()
	{
		component_container_information cci;
		cci=new component_container_information(
				sk.create_parameter.scene_directory_name,
				sk.create_parameter.scene_file_name,sk.component_cont,ci);
		print("scene_name",						sk.scene_name);
		print("link_name",						sk.link_name);
		
		print("system_par",						new system_parameter_information(sk.system_par,ci));
		print("scene_par",						new scene_parameter_information(sk.scene_par,ci));
		
		print("component_cont",					cci);
		
		print("original_part_number",			sk.component_cont.original_part_number);
		print("effective_part_number",			sk.process_part_sequence.process_parts_sequence.length);
		
		print("total_buffer_object_file_number",		sk.process_part_sequence.total_buffer_object_file_number);
		print("total_buffer_object_text_data_length",	sk.process_part_sequence.total_buffer_object_text_data_length);
		
		print("modifier",						new modifier_information(sk.modifier_cont,ci));
	}
	public kernel_information(scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		sk=my_sk;
		ci=my_ci;
	}
}
