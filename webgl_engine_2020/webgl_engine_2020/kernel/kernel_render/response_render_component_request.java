package kernel_render;

import kernel_buffer.component_render;
import kernel_buffer.component_render_buffer;
import kernel_buffer.modifier_parameter_buffer;
import kernel_buffer.part_mesh_loader;
import kernel_camera.camera_result;
import kernel_render.render_target;
import kernel_common_class.nanosecond_timer;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_driver.modifier_container_timer;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_directory;
import kernel_part.part;
import kernel_transformation.plane;

public class response_render_component_request
{
	private static component_collector collect_render_parts(plane mirror_plane,
			engine_kernel ek,				client_information ci,
			boolean my_do_discard_lod_flag,	boolean my_do_selection_lod_flag, 
			int target_camera_id,			camera_result cam_result)
	{	
		component_render ren_buf;
		
		long t0=nanosecond_timer.absolute_nanoseconds();
		
		int render_buffer_id=cam_result.get_render_buffer_id(ci);
		int pps[][]=ek.process_part_sequence.process_parts_sequence;
		int id_array[][][][]=ek.component_cont.part_component_id_and_driver_id;
		component_render_buffer	buffer=ci.render_buffer.component_buffer;

		for(int i=0,ni=pps.length;i<ni;i++)
			if((ren_buf=buffer.get_render_buffer(pps[i][0],pps[i][1],
				render_buffer_id,id_array[pps[i][0]][pps[i][1]].length))!=null)
					ren_buf.clear_clip_flag(ek.component_cont);
		
		component_collector list_result=(new list_component_on_collector(
			true,my_do_discard_lod_flag,my_do_selection_lod_flag,false,true,ek,ci,cam_result)).collector;
		
		long current_time=ek.current_time.nanoseconds();
		for(int i=0,ni=list_result.component_collector.length;i<ni;i++)
			if(list_result.component_collector[i]!=null)
				for(int j=0,nj=list_result.component_collector[i].length;j<nj;j++)
					for(component_link_list cll=list_result.component_collector[i][j];cll!=null;cll=cll.next_list_item)
						cll.comp.render_touch_time=current_time;
		
		for(int i=0,ni=pps.length;i<ni;i++)
			if((ren_buf=buffer.get_render_buffer(pps[i][0],pps[i][1],
				render_buffer_id,id_array[pps[i][0]][pps[i][1]].length))!=null)
					ren_buf.test_clip_flag_of_delete_component(cam_result,
						ek.component_cont,cam_result.target.parameter_channel_id);
		
		long t1=nanosecond_timer.absolute_nanoseconds();
		
		ci.request_response.print("[",render_buffer_id);
		ci.render_buffer.cam_buffer.response_camera_data(
				mirror_plane,cam_result,ci,ek.camera_cont,target_camera_id);
		
		response_component_render_parameter.response(
				render_buffer_id,list_result,cam_result,ek,ci);
		
		ci.render_buffer.clip_buffer.response(
				render_buffer_id,cam_result.target.clip_plane,ci.request_response);
		ci.request_response.print("]");
		
		ci.render_buffer.location_buffer.put_in_list(cam_result.cam.eye_component,ek);
		
		long t2=nanosecond_timer.absolute_nanoseconds();
		ci.statistics_client.collect_time_length+=t1-t0;
		ci.statistics_client.output_time_length +=t2-t1;
		
		return list_result;
	}
	private static int process_target(engine_kernel ek,client_information ci)
	{
		render_target t;
		camera_result cr;
		render_target target_array[]=ci.target_container.get_render_target(ek.component_cont.root_component);
		int target_number=ci.target_container.get_render_target_number();
		if(ci.target_component_collector_array.length<target_number){
			component_collector bak_collector[]=ci.target_component_collector_array;
			camera_result		bak_camera_result[]=ci.target_camera_result_array;
			ci.target_component_collector_array=new component_collector[target_number];
			ci.target_camera_result_array=new camera_result[target_number];
			
			for(int i=0,ni=bak_collector.length;i<ni;i++) {
				ci.target_component_collector_array[i]=bak_collector[i];
				ci.target_camera_result_array[i]=bak_camera_result[i];
			}
			for(int i=bak_collector.length;i<target_number;i++){
				ci.target_component_collector_array[i]=null;
				ci.target_camera_result_array[i]=null;
			}
		}
		for(int i=0,ni=target_array.length;i<ni;i++)
			if((t=target_array[i]).camera_id>=0)
				if(t.camera_id<ek.camera_cont.camera_array.length)
					ci.target_camera_result_array[t.target_id]=new camera_result(
							ek.camera_cont.camera_array[t.camera_id],t,ek.component_cont);

		for(int i=0,ni=target_array.length;i<ni;i++)
			if((t=target_array[i]).selection_target_flag)
				if((cr=ci.target_camera_result_array[t.target_id])!=null){
					ci.selection_camera_result=cr;
					break;
				}
		double view_coordinate[]=null;
		for(int i=0,ni=target_array.length;i<ni;i++)
			if((t=target_array[i]).main_display_target_flag)
				if((cr=ci.target_camera_result_array[t.target_id])!=null)
					if((view_coordinate=cr.caculate_view_coordinate(ci))!=null){
						ci.display_camera_result=cr;
						break;
					}
		ci.request_response.print(",[");
		for(int i=0,response_number=0,ni=target_array.length;i<ni;i++){
			t=target_array[i];
			cr=ci.target_camera_result_array[t.target_id];
			ci.request_response.print(((response_number++)==0)?"[":",[");
			component_collector collector=collect_render_parts(t.mirror_plane,ek,ci,
				t.selection_target_flag?false:(t.do_discard_lod_flag),
				t.selection_target_flag?false:(t.do_selection_lod_flag),
				t.camera_id,cr);
			ci.target_component_collector_array[t.target_id]=collector;
			
			if(ci.display_camera_result!=null)
				if(ci.display_camera_result.target.target_id==t.target_id)
					ci.display_component_collector=collector;
			if(ci.selection_camera_result!=null)
				if(ci.selection_camera_result.target.target_id==t.target_id)
					ci.selection_component_collector=collector;
			
			ci.request_response.print(",",t.target_id);
			ci.request_response.print(",",ci.target_container.get_do_render_number(t.target_id));
			ci.render_buffer.target_buffer.response_parameter(	ci.request_response,
				t.target_id,			t.render_target_id,		t.parameter_channel_id,
				t.framebuffer_width,	t.framebuffer_height,	t.render_target_number,
				t.viewport);
			ci.request_response.print("]");
		}
		
		ci.request_response.print("],[");
		
		{
			response_component_buffer_parameter rcbp=new response_component_buffer_parameter(ek,ci);
			for(int i=0,ni=ci.target_component_collector_array.length;i<ni;i++)
				if(ci.target_component_collector_array[i]!=null)
					rcbp.response(ci.target_component_collector_array[i]);
		}
		
		ci.request_response.print("]");
		
		if(view_coordinate==null)
			return -1;
		else
			return (int)(view_coordinate[3]);
	}
	private static void response_parameter(engine_kernel ek,client_information ci,long delay_time_length)
	{
		long my_current_time_difference;
		my_current_time_difference =ek.current_time.nanoseconds();
		my_current_time_difference-=ci.render_buffer.response_current_time_pointer;
		ci.render_buffer.response_current_time_pointer+=my_current_time_difference;

		ci.request_response.print("[",ek.collector_stack.get_collector_version());
		ci.request_response.print(",",delay_time_length);
		ci.request_response.print(",",my_current_time_difference);
		ci.request_response.print(",[");
		String pre_str="";
		for(int i=0,ni=ek.scene_par.max_modifier_container_number;i<ni;i++){
			modifier_container_timer timer=ek.modifier_cont[i].get_timer();
			modifier_parameter_buffer old_p=ci.render_buffer.modifier_parameter[i];
			modifier_parameter_buffer new_p=new modifier_parameter_buffer(timer.get_timer_adjust_value());
			if(new_p.timer_adjust_value!=old_p.timer_adjust_value){
				ci.request_response.print(pre_str,i);
				ci.request_response.print(",",new_p.timer_adjust_value-old_p.timer_adjust_value);
				ci.render_buffer.modifier_parameter[i]=new_p;
				pre_str=",";
			}
		}
		ci.request_response.print("]]");
	}
	private static int response_buffer_object_proxy_request(part p,engine_kernel ek,client_information ci)
	{
		String directory_name=file_directory.part_file_directory(p,ek.system_par,ek.scene_par)+"mesh.";
		
		String proxy_url_directory=ci.get_request_url_header()+"&command=buffer&operation=buffer_data";
		proxy_url_directory+="&render="+(p.render_id)+"&part="+(p.part_id)+"&data_file=";
		
		int ret_val=0;
		final String type_str[]={"face","edge","point"};
		
		for(int i=0,ni=type_str.length;i<ni;i++){
			ci.request_response.print((i<=0)?"[":",[");
			for(int j=0,nj=p.boftal.buffer_object_text_file_length[i].length;j<nj;j++){
				ci.request_response.print((j<=0)?"[":",[");
				if(!(p.boftal.buffer_object_file_in_head_flag[i][j])){
					ret_val++;
					ci.request_response.print(p.boftal.buffer_object_text_file_length[i][j]);
					
					String proxy_url,file_name=directory_name+type_str[i]+Integer.toString(j)+".gzip_binary";
					if((proxy_url=ci.get_file_proxy_url(file_name,ek.system_par))==null)
						proxy_url=proxy_url_directory+type_str[i]+j;
					ci.request_response.print(",\"",proxy_url).print("\"");
				}
				ci.request_response.print("]");
			}
			ci.request_response.print("]");
		}
		return ret_val;
	}
	private static void response_buffer_object_request(engine_kernel ek,
		client_information ci,int requesting_number,int max_request_number)
	{
		ci.request_response.print(",[");
		for(int i=requesting_number;i<max_request_number;i++){
			part_mesh_loader pml=ci.render_buffer.mesh_loader;
			int request_package[]=pml.get_request_package(ek.process_part_sequence);
			if(request_package==null)
				break;
			int part_type_id	=request_package[0];
			int part_package_id	=request_package[1];
			
			String package_file_name;
			int package_render_part_id[][];
			long package_length;
			boolean package_flag;
			
			switch(part_type_id){
			default:
			case 0:
				package_file_name=ek.render_cont.system_part_package.package_file_name[part_package_id];
				package_render_part_id=ek.process_part_sequence.system_render_part_id[part_package_id];
				package_length=ek.render_cont.system_part_package.package_length[part_package_id];
				package_flag=ek.render_cont.system_part_package.package_flag[part_package_id];
				break;
			case 1:
				package_file_name=ek.render_cont.type_part_package.package_file_name[part_package_id];
				package_render_part_id=ek.process_part_sequence.type_render_part_id[part_package_id];
				package_length=ek.render_cont.type_part_package.package_length[part_package_id];
				package_flag=ek.render_cont.type_part_package.package_flag[part_package_id];
				break;
			case 2:
				package_file_name=ek.render_cont.scene_part_package.package_file_name[part_package_id];
				package_render_part_id=ek.process_part_sequence.scene_render_part_id[part_package_id];
				package_length=ek.render_cont.scene_part_package.package_length[part_package_id];
				package_flag=ek.render_cont.scene_part_package.package_flag[part_package_id];
				break;
			}

			String package_proxy_url;
			if((package_proxy_url=ci.get_file_proxy_url(package_file_name,ek.system_par))==null)
				package_proxy_url=ci.get_request_url_header()
					+"&command=buffer&operation=buffer_package&package="
					+part_type_id+"_"+part_package_id;

			ci.request_response.print((i<=requesting_number)?"[\"":",[\"",package_proxy_url).
								print("\",",package_length).
								print(package_flag?",true,[":",false,[");

			for(int j=0,nj=package_render_part_id.length;j<nj;j++){
				int render_id=package_render_part_id[j][0];
				int part_id  =package_render_part_id[j][1];
				part p=ek.render_cont.renders[render_id].parts[part_id];
				ci.request_response.print((j<=0)?"[":",[",	p.render_id).
									print(",",				p.part_id).
									print(",",				p.part_from_id).
									print(",",				p.part_package_sequence_id);
				ci.request_response.print(",[");
				i+=response_buffer_object_proxy_request(p,ek,ci);
				ci.request_response.print("]]");
			}
			ci.request_response.print("]]");
		}
		ci.request_response.print("]");
	}
	private static void display_data_load_message(engine_kernel ek,client_information ci)
	{
		String str;

		if((str=ci.request_response.get_parameter("loaded_length"))==null)
			return;
		int index_id;
		if((index_id=str.indexOf("_"))<0)
			return;
		int loaded_buffer_object_file_number=Integer.decode(str.substring(0,index_id));
		if((index_id=(str=str.substring(index_id+1)).indexOf("_"))<0)
			return;
		long loaded_buffer_object_data_length=Long.decode(str.substring(0,index_id));

		if((index_id=(str=str.substring(index_id+1)).indexOf("_"))<0)
			return;
		int render_id=Integer.decode(str.substring(0,index_id  ));
		int part_id  =Integer.decode(str.substring(  index_id+1));
			
		if((str=ci.request_response.get_parameter("language"))==null)
			str="english";
		else
			str=str.toLowerCase().trim();
		str=ek.system_par.language_change_name.search_change_name("load+"+str,"Load");

		long total_length=ek.process_part_sequence.total_buffer_object_text_data_length;
		if(ek.process_part_sequence.total_buffer_object_text_data_length>0){
			str+="["+Integer.toString(loaded_buffer_object_file_number);
			str+=":"+Integer.toString(ek.process_part_sequence.total_buffer_object_file_number);
			
			str+="/"+Long.toString(loaded_buffer_object_data_length/1024);
			str+="K:"+Long.toString(total_length/1024);
			
			double percentage=100;
			percentage*=loaded_buffer_object_data_length;
			percentage/=total_length;
			str+="K/"+Integer.toString((int)percentage)+"%]";
		}
		part p;
		if((render_id>=0)&&(render_id<ek.render_cont.renders.length))
			if((part_id>=0)&&(part_id<ek.render_cont.renders[render_id].parts.length))
				if((p=ek.render_cont.renders[render_id].parts[part_id])!=null)
					str+=":"+p.user_name;
		
		ci.message_display.set_display_message(str,
				(loaded_buffer_object_data_length>=total_length)?1000*1000*1000*10:-1);
	}
	public static void do_render(engine_kernel ek,client_information ci,long delay_time_length)
	{
		ci.statistics_client.transportation_time_length=ci.request_response.request_time-ci.statistics_client.last_access_time;
		if(ci.statistics_client.transportation_time_length<=0)
			ci.statistics_client.transportation_time_length=1;		
		ci.statistics_client.caculate_time_length=nanosecond_timer.absolute_nanoseconds()-ci.request_response.request_time;	

		int my_loading_request_number=0,max_loading_request_number=1,index_id;
		{
			String str;
			if((str=ci.request_response.get_parameter("requesting_number"))!=null)
				if((index_id=str.indexOf("_"))>0){
					if((my_loading_request_number=Integer.decode(str.substring(0,index_id)))<0)
						my_loading_request_number=0;
					str=str.substring(index_id+1);
					
					if((index_id=str.indexOf("_"))>0)
						max_loading_request_number=Integer.decode(str.substring(0,index_id));
					else
						max_loading_request_number=Integer.decode(str);
					str=str.substring(index_id+1);
					
					if(max_loading_request_number<1)
						max_loading_request_number=1;
				}
			if(max_loading_request_number>ek.system_par.max_loading_number)
				max_loading_request_number=ek.system_par.max_loading_number;
		}
		
		display_data_load_message(ek,ci);

		ci.render_buffer.mesh_loader.test_request_package(max_loading_request_number,max_loading_request_number);
		
		ci.statistics_client.start(delay_time_length,my_loading_request_number);
				
		ci.request_response.print("[");
		response_parameter(ek,ci,delay_time_length);
		int my_target_viewport_id=process_target(ek,ci);
		ci.render_buffer.location_buffer.response_location(ek,ci);
		ci.render_buffer.current_buffer.response_current(ek, ci,my_target_viewport_id);
		response_buffer_object_request(ek,ci,my_loading_request_number,max_loading_request_number);
		ci.request_response.print("]");
		
		long my_current_time=nanosecond_timer.absolute_nanoseconds();
		
		ci.statistics_client.all_time_length=my_current_time-ci.statistics_client.last_access_time;
		ci.statistics_client.last_access_time=my_current_time;
		ci.statistics_client.render_data_length=ci.request_response.output_data_length;

		return;
	}
}
