package kernel_render;

import kernel_common_class.debug_information;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_driver.component_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_part.part;

public class response_component_buffer_parameter
{
	private int part_print_number=0;
	private long current_touch_time;
	private engine_kernel ek;
	private client_information ci;
	
	private component_instance_driver test_should_response_parameter(
			component_link_list cll,long current_touch_time,
			engine_kernel ek,client_information ci,int should_increase)
	{
		component_instance_driver in_dr;
		if((in_dr=ci.component_instance_driver_cont.get_component_instance_driver(cll.comp,cll.driver_id))==null)
			return null;

		long old_parameter_version=in_dr.get_component_parameter_version();
		long new_parameter_version=cll.comp.driver_array.get(cll.driver_id).get_component_parameter_version();
		if(old_parameter_version==new_parameter_version)
			return null;
		ci.statistics_client.should_update_component_parameter_number+=should_increase;
		if(ci.statistics_client.update_component_parameter_number<ek.scene_par.most_update_parameter_number)
			return in_dr;
		if((current_touch_time-cll.comp.uniparameter.touch_time)<=ek.scene_par.touch_time_length)
			return in_dr;
		if(ci.parameter.comp!=null)
			if(ci.parameter.comp.component_id==cll.comp.component_id)
				return in_dr;
		return null;
	}
	public void response(component_collector collector)
	{
		for(int i=0,ni=ek.process_part_sequence.process_parts_sequence.length;i<ni;i++){
			int render_id=ek.process_part_sequence.process_parts_sequence[i][0];
			int part_id=ek.process_part_sequence.process_parts_sequence[i][1];
			for(component_link_list cll=collector.component_collector[render_id][part_id];cll!=null;cll=cll.next_list_item){
				component_instance_driver in_dr;
				if((in_dr=test_should_response_parameter(cll,current_touch_time,ek,ci,0))==null)
					continue;
				ci.request_response.print(((part_print_number++)>0)?",[":"[",render_id);
				ci.request_response.print(",",part_id);
				ci.request_response.print(",[");

				for(int parameter_print_number=0;cll!=null;cll=cll.next_list_item){
					if((in_dr=test_should_response_parameter(cll,current_touch_time,ek,ci,1))==null)
						continue;
					ci.request_response.print(((parameter_print_number++)>0)?",[":"[",
							cll.comp.driver_array.get(cll.driver_id).same_part_component_driver_id);
					ci.request_response.print(",");
					try{
						in_dr.create_component_parameter(ek,ci);
					}catch(Exception e){
						part p=cll.comp.driver_array.get(cll.driver_id).component_part;
						
						debug_information.println("create_component_parameter in instance_driver fail:	",e.toString());
						debug_information.println("Component name:	",	cll.comp.component_name);
						debug_information.println("Driver ID:		",	cll.driver_id);
						debug_information.println("Part user name:	",	p.user_name);
						debug_information.println("Part system name:",	p.system_name);
						debug_information.println("Mesh file name:	",	p.directory_name+p.mesh_file_name);
						
						e.printStackTrace();
					}
					ci.request_response.print("]");
					ci.statistics_client.update_component_parameter_number++;
					in_dr.update_component_parameter_version(
							cll.comp.driver_array.get(cll.driver_id).get_component_parameter_version());
				}
				ci.request_response.print("]]");
				break;
			}
		}
	}
	
	public response_component_buffer_parameter(engine_kernel my_ek,client_information my_ci)
	{
		ek=my_ek;
		ci=my_ci;
		
		part_print_number=0;
		current_touch_time=ek.current_time.nanoseconds();
	}
}
