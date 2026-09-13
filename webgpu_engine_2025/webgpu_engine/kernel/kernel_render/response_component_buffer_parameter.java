package kernel_render;

import kernel_buffer.response_flag;
import kernel_common_class.debug_information;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_driver.component_instance_driver;
import kernel_part.part;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class response_component_buffer_parameter
{
	private scene_kernel sk;
	private client_information ci;
	private long current_touch_time;
	
	private component_instance_driver test_should_response_parameter(
			component_link_list cll,long current_touch_time,
			scene_kernel sk,client_information ci,render_component_counter rcc)
	{
		component_instance_driver my_instance_driver;
		if((my_instance_driver=ci.component_instance_driver_cont.
			get_component_instance_driver(cll.comp,cll.driver_id))==null)
				return null;

		long old_parameter_version=my_instance_driver.get_component_parameter_version();
		long new_parameter_version=cll.comp.driver_array.get(cll.driver_id).get_component_parameter_version();
		if(old_parameter_version==new_parameter_version)
			return null;
		
		if(rcc.update_component_parameter_number<sk.scene_par.most_update_parameter_number)
			return my_instance_driver;
		if((current_touch_time-cll.comp.uniparameter.touch_time)<=sk.scene_par.touch_time_length)
			return my_instance_driver;
		if(ci.parameter.comp!=null)
			if(ci.parameter.comp.component_id==cll.comp.component_id)
				return my_instance_driver;
		return null;
	}
	private void response(int render_id,int part_id,component_link_list cll,
			response_flag create_flag,render_component_counter rcc)
	{
		for(component_instance_driver my_instance_driver;cll!=null;cll=cll.next_list_item) {
			if((my_instance_driver=test_should_response_parameter(cll,current_touch_time,sk,ci,rcc))==null)
				continue;
			part my_part=cll.comp.driver_array.get(cll.driver_id).component_part;
			
			if(create_flag.first_item_flag) {
				ci.request_response.print("[");
				create_flag.first_item_flag=false;
			}else
				ci.request_response.print(",[");
			
			if((create_flag.render_id!=my_part.render_id)||(create_flag.part_id!=my_part.part_id)) {
				create_flag.render_id			=my_part.render_id;
				create_flag.part_id				=my_part.part_id;
				ci.request_response.print(my_part.render_id).print(",",my_part.part_id).print(",");
			}
			int data_buffer_id=cll.comp.driver_array.get(cll.driver_id).same_part_component_driver_id;
			ci.request_response.print(data_buffer_id).print(",");
			
			try{
				my_instance_driver.create_component_parameter(sk,ci);
			}catch(Exception e){
				e.printStackTrace();
				
				part p=cll.comp.driver_array.get(cll.driver_id).component_part;
				
				debug_information.println("create_component_parameter in instance_driver fail:	",e.toString());
				debug_information.println("Component name:	",	cll.comp.component_name);
				debug_information.println("Driver ID:		",	cll.driver_id);
				debug_information.println("Part user name:	",	p.user_name);
				debug_information.println("Part system name:",	p.system_name);
				debug_information.println("Mesh file name:	",	p.directory_name+p.mesh_file_name);
			}
			ci.request_response.print("]");
			
			rcc.update_component_parameter_number++;
			my_instance_driver.update_component_parameter_version(
					cll.comp.driver_array.get(cll.driver_id).get_component_parameter_version());
		}	
	}
	public response_component_buffer_parameter(
			scene_kernel my_sk,client_information my_ci,render_component_counter rcc)
	{
		sk=my_sk;
		ci=my_ci;
		current_touch_time=sk.current_time.nanoseconds();
		response_flag create_flag=new response_flag();
		
		ci.request_response.print(",[");
		for(int ppc[][]=sk.process_part_sequence.process_parts_sequence,i=0,ni=ppc.length;i<ni;i++) {
			int render_id=ppc[i][0],part_id=ppc[i][1];
			if(ci.not_acknowledge_render_part_id[render_id][part_id])
				continue;
			for(component_collector collector:ci.target_component_collector_list) {
				if(collector==null)
					continue;
				component_link_list cll=collector.component_collector[render_id][part_id];
				response(render_id,part_id,cll,create_flag,rcc);
			}
		}
		ci.request_response.print("]");
	}
}
