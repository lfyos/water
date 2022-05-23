package kernel_information;

import kernel_engine.client_parameter;
import kernel_engine.client_information;

public class client_parameter_information extends jason_creator
{
	private client_information ci;
	
	public void print()
	{
		client_parameter cp=ci.parameter;
		
		print("body_id",					cp.body_id);
		print("face_id",					cp.face_id);
		print("vertex_id",					cp.vertex_id);
		print("loop_id",					cp.loop_id);
		print("edge_id",					cp.edge_id);
		print("point_id",					cp.point_id);
		
		print("x",							cp.x);
		print("y",							cp.y);
		print("aspect",						cp.aspect);
		print("depth",						cp.depth);
		print("value",						cp.value);
		
		print("high_or_low_precision_flag",	cp.high_or_low_precision_flag);
		
		print("request_length",				cp.request_length);
		print("max_client_loading_number",	cp.max_client_loading_number);
		
		print("selected_component",			new component_information(ci.parameter.comp,ci));
	}
	public client_parameter_information(client_information my_ci)
	{
		super(my_ci.request_response);
		ci=my_ci;
	}
}
