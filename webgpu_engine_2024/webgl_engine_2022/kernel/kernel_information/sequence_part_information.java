package kernel_information;

import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class sequence_part_information extends jason_creator
{
	private client_information ci;
	private scene_kernel sk;
	
	public void print()
	{
		jason_creator jc[]=new jason_creator[sk.process_part_sequence.process_parts_sequence.length];
		for(int i=0,ni=jc.length;i<ni;i++) {
			int render_id	=sk.process_part_sequence.process_parts_sequence[i][0];
			int part_id		=sk.process_part_sequence.process_parts_sequence[i][1];
			jc[i]=new part_with_component_information(sk.render_cont.renders.get(render_id).parts.get(part_id),sk,ci);
		}
		print("part_number",					jc.length);
		print("total_buffer_object_file_number",
				sk.process_part_sequence.total_buffer_object_file_number);
		print("total_buffer_object_text_data_length",	sk.process_part_sequence.total_buffer_object_text_data_length);
		print("part",jc);
	}
	public sequence_part_information(scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		sk=my_sk;
		ci=my_ci;
	}
}
