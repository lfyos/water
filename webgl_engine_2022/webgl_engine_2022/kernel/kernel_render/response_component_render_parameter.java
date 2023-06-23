package kernel_render;

import kernel_buffer.component_render;
import kernel_camera.camera_result;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;


public class response_component_render_parameter
{
	public static void response(int render_buffer_id,
			component_collector collector,camera_result cam_result,
			engine_kernel ek,client_information ci)
	{
		int pps[][]=ek.process_part_sequence.process_parts_sequence;
		int pcd[][][][]=ek.component_cont.part_component_id_and_driver_id;
		long render_current_time=ek.current_time.nanoseconds();
		
		for(int i=0,ni=pps.length;i<ni;i++){
			int render_id=pps[i][0],part_id=pps[i][1];
			component_render ren_buf=ci.render_buffer.component_buffer.get_render_buffer(
					render_id,part_id,render_buffer_id,pcd[render_id][part_id].length);
			if(ren_buf!=null){
				component_link_list cll=collector.component_collector[render_id][part_id];
				ren_buf.mark(cll,ci,cam_result,render_buffer_id);
			}
		}
		ci.request_response.print(",[[");
		
		for(int append_part_number=0,i=0,ni=pps.length;i<ni;i++){
			int render_id=pps[i][0],part_id=pps[i][1];
			if(ci.not_acknowledge_render_part_id[render_id][part_id]) 
				continue;
			if(collector.component_collector[render_id][part_id]==null)
				continue;
			component_render ren_buf=ci.render_buffer.component_buffer.get_render_buffer(
						render_id, part_id,render_buffer_id,pcd[render_id][part_id].length);
			if(ren_buf==null)
				continue;
			for(int type_id=0;type_id<2;type_id++){
				component_link_list cll=(type_id==0)?ren_buf.append_cll:ren_buf.refresh_cll;
				if(cll==null)
					continue;
				int all_number=0;
				all_number+=ci.statistics_client.component_append_number;
				all_number+=ci.statistics_client.component_refresh_number;
				if(all_number>ek.scene_par.most_component_append_number) {
					long lastest_touch_time=(type_id==0)
							?ren_buf.lastest_append_touch_time
							:ren_buf.lastest_refresh_touch_time;
					if((render_current_time-lastest_touch_time)>ek.scene_par.touch_time_length)
						continue;
				}
				append_part_number=ren_buf.create_append_render_parameter(append_part_number,
						cll,render_current_time,ek,ci,cam_result,render_buffer_id);
			};
			if(ren_buf.do_append_instance_number>0)
				ci.request_response.print("]");
		}
		ci.request_response.print("],[");

		for(int print_number=0,type_id=0;type_id<2;type_id++)
			for(int i=0,ni=pps.length;i<ni;i++){
				int render_id=pps[i][0],part_id=pps[i][1];
				component_render ren_buf=ci.render_buffer.component_buffer.get_render_buffer(
							render_id, part_id,render_buffer_id,pcd[render_id][part_id].length);
				if(ren_buf==null)
					continue;
				component_link_list cll;
				if(type_id==0){
					if((cll=ren_buf.delete_in_cll)==null)
						continue;
					if(ci.statistics_client.component_delete_number>ek.scene_par.most_component_delete_number)
						if((render_current_time-ren_buf.lastest_in_delete_touch_time)>ek.scene_par.touch_time_length)
							continue;
				}else {
					if((cll=ren_buf.delete_out_cll)==null)
						continue;
					if(ci.statistics_client.component_delete_number>ek.scene_par.most_component_delete_number)
						if((render_current_time-ren_buf.lastest_out_delete_touch_time)>ek.scene_par.touch_time_length)
							continue;
				}
				ci.request_response.print(((print_number++)<=0)?"":",",render_id);
				ci.request_response.print(",",part_id);
				ci.request_response.print(",[");
				ren_buf.create_delete_render_parameter(cll,render_current_time,ek,ci,cam_result);
				ci.request_response.print("]");
			}
		ci.request_response.print("]]");
		
		for(int i=0,ni=pps.length;i<ni;i++){
			int render_id=pps[i][0],part_id=pps[i][1];
			component_render ren_buf=ci.render_buffer.component_buffer.get_render_buffer(
					render_id,part_id,render_buffer_id,pcd[render_id][part_id].length);
			if(ren_buf!=null)
				ren_buf.register_location(ek,ci);
		}
	}
}
