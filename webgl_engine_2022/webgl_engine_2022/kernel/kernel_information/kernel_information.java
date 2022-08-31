package kernel_information;

import kernel_engine.engine_kernel;
import kernel_engine.client_information;

public class kernel_information extends jason_creator
{
	private engine_kernel ek;
	private client_information ci;
	
	public void print()
	{
		component_container_information cci;
		cci=new component_container_information(
				ek.create_parameter.scene_directory_name,
				ek.create_parameter.scene_file_name,ek.component_cont,ci);
		print("title",							ek.create_parameter.scene_title);
		print("scene_name",						ek.create_parameter.scene_name);
		print("link_name",						ek.create_parameter.link_name);
		
		print("system_par",						new system_parameter_information(ek.system_par,ci));
		print("scene_par",						new scene_parameter_information(ek.scene_par,ci));
		
		print("component_cont",					cci);
		
		print("original_part_number",			ek.component_cont.original_part_number);
		print("effective_part_number",			ek.process_part_sequence.process_parts_sequence.length);
		
		print("total_buffer_object_file_number",		ek.process_part_sequence.total_buffer_object_file_number);
		print("total_buffer_object_text_data_length",	ek.process_part_sequence.total_buffer_object_text_data_length);
		
		print("modifier",						new modifier_information(ek.modifier_cont,ci));
	}
	public kernel_information(engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
	}
}
