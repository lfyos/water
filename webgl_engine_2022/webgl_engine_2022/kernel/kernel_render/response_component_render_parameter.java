package kernel_render;

import java.util.ArrayList;

import kernel_buffer.component_render;
import kernel_buffer.response_flag;
import kernel_component.component_link_list;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class response_component_render_parameter
{
	public static void response(
			ArrayList<response_render_data> render_data_list,
			engine_kernel ek,client_information ci)
	{
		int pps[][]=ek.process_part_sequence.process_parts_sequence;
		int pcd[][][][]=ek.component_cont.part_component_id_and_driver_id;
		long render_current_time=ek.current_time.nanoseconds();
		response_flag create_flag=new response_flag();
		
		ci.request_response.print(",[");
		
		for(int i=0,ni=pps.length;i<ni;i++){
			int render_id=pps[i][0],part_id=pps[i][1];
			if(ci.not_acknowledge_render_part_id[render_id][part_id]) 
				continue;
			for(int list_id=0,list_size=render_data_list.size();list_id<list_size;list_id++) {
				response_render_data rrd=render_data_list.get(list_id);
				if(rrd.collector.component_collector[render_id][part_id]==null)
					continue;
				component_render ren_buf=ci.render_buffer.component_buffer.get_render_buffer(
						render_id,part_id,rrd.render_buffer_id,pcd[render_id][part_id].length);
				if(ren_buf==null)
					continue;
				component_link_list cll=rrd.collector.component_collector[render_id][part_id];
				ren_buf.mark(cll,ci,rrd.cam_result,rrd.render_buffer_id);

				for(int type_id=0;type_id<2;type_id++){
					if((cll=(type_id==0)?ren_buf.append_cll:ren_buf.refresh_cll)==null)
						continue;
					int all_number=0;
					all_number+=ci.statistics_client.component_append_number;
					all_number+=ci.statistics_client.component_refresh_number;
					if(all_number>ek.scene_par.most_component_append_number){
						long lastest_touch_time=(type_id==0)
								?ren_buf.lastest_append_touch_time:ren_buf.lastest_refresh_touch_time;
						if((render_current_time-lastest_touch_time)>ek.scene_par.touch_time_length)
							continue;
					}
					ren_buf.create_append_render_parameter((type_id==0)?true:false,create_flag,
								cll,render_current_time,ek,ci,rrd.cam_result,rrd.render_buffer_id);
				}
			}
		}
		ci.request_response.print("],[");
		create_flag=new response_flag();
		
		for(int i=0,ni=pps.length;i<ni;i++){
			int render_id=pps[i][0],part_id=pps[i][1];
			for(int type_id=0;type_id<2;type_id++){
				for(int list_id=0,list_size=render_data_list.size();list_id<list_size;list_id++) {
					response_render_data rrd=render_data_list.get(list_id);
					if(rrd.collector.component_collector[render_id][part_id]==null)
						continue;
					component_render ren_buf=ci.render_buffer.component_buffer.get_render_buffer(
							render_id,part_id,rrd.render_buffer_id,pcd[render_id][part_id].length);
					if(ren_buf==null)
						continue;
					component_link_list cll;
					if(type_id==0){
						if((cll=ren_buf.delete_in_cll)==null)
							continue;
						if(ci.statistics_client.component_delete_number>ek.scene_par.most_component_delete_number)
							if((render_current_time-ren_buf.lastest_in_delete_touch_time)>ek.scene_par.touch_time_length)
								continue;
					}else{
						if((cll=ren_buf.delete_out_cll)==null)
							continue;
						if(ci.statistics_client.component_delete_number>ek.scene_par.most_component_delete_number)
							if((render_current_time-ren_buf.lastest_out_delete_touch_time)>ek.scene_par.touch_time_length)
								continue;
					}
					ren_buf.create_delete_render_parameter(create_flag,
						render_id,part_id,rrd.render_buffer_id,cll,render_current_time,ek,ci);
				}
			}
		}
		
		ci.request_response.print("]");
		
		for(int i=0,ni=pps.length;i<ni;i++){
			int render_id=pps[i][0],part_id=pps[i][1];
			if(ci.not_acknowledge_render_part_id[render_id][part_id]) 
				continue;
			for(int list_id=0,list_size=render_data_list.size();list_id<list_size;list_id++) {
				response_render_data rrd=render_data_list.get(list_id);
				component_render ren_buf=ci.render_buffer.component_buffer.get_render_buffer(
						render_id,part_id,rrd.render_buffer_id,pcd[render_id][part_id].length);
				if(ren_buf!=null)
					ren_buf.register_location(ek,ci);
			}
		}
	}
}
