package kernel_information;

import kernel_engine.engine_kernel;
import kernel_engine.client_information;

public class sequence_part_information extends jason_creator
{
	private client_information ci;
	private engine_kernel ek;
	
	public void print()
	{
		jason_creator jc[]=new jason_creator[ek.process_part_sequence.process_parts_sequence.length];
		for(int i=0,ni=jc.length;i<ni;i++) {
			int render_id	=ek.process_part_sequence.process_parts_sequence[i][0];
			int part_id		=ek.process_part_sequence.process_parts_sequence[i][1];
			jc[i]=new part_with_component_information(ek.render_cont.renders.get(render_id).parts.get(part_id),ek,ci);
		}
		print("part_number",					jc.length);
		print("total_buffer_object_file_number",
				ek.process_part_sequence.total_buffer_object_file_number);
		print("total_buffer_object_text_data_length",	ek.process_part_sequence.total_buffer_object_text_data_length);
		print("part",jc);
	}
	public sequence_part_information(engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
	}
}
