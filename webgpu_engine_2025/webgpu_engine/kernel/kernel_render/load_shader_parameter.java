package kernel_render;

import kernel_component.component_load_source_container;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_part.permanent_part_id_encoder;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;

public class load_shader_parameter 
{
	public client_request_response request_response;
	
	public part_container_for_part_search pcps;
	public permanent_part_id_encoder part_id_encoder;
	
	public component_load_source_container component_load_source_cont;

	public system_parameter system_par;
	public scene_parameter scene_par;
	
	public load_shader_parameter(
			client_request_response my_request_response,
			part_container_for_part_search my_pcps,
			permanent_part_id_encoder my_part_id_encoder,
			component_load_source_container my_component_load_source_cont,
			system_parameter my_system_par,scene_parameter my_scene_par)
	{
		request_response=my_request_response;
		
		pcps=my_pcps;
		part_id_encoder=my_part_id_encoder;
		component_load_source_cont=my_component_load_source_cont;
		
		system_par=my_system_par;
		scene_par=my_scene_par;
	}
}
