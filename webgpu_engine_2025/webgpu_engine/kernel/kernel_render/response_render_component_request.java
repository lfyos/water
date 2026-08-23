package kernel_render;

import java.util.ArrayList;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_camera.camera_result;
import kernel_buffer.component_render;
import kernel_scene.client_information;
import kernel_file_manager.file_directory;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_buffer.component_render_buffer;
import kernel_driver.modifier_container_timer;
import kernel_buffer.modifier_parameter_buffer;
import kernel_part.buffer_object_file_modify_time_and_length_item;

public class response_render_component_request
{
	private static component_collector collect_render_parts(
			ArrayList<response_render_data> render_data_list,
			scene_kernel sk,client_information ci,camera_result cam_result)
	{	
		component_render ren_buf;
		
		int pps[][]=sk.process_part_sequence.process_parts_sequence;
		int id_array[][][][]=sk.component_cont.part_component_id_and_driver_id;
		component_render_buffer	buffer=ci.render_buffer.component_buffer;

		for(int i=0,ni=pps.length;i<ni;i++) {
			int render_id=pps[i][0],part_id=pps[i][1];
			if((ren_buf=buffer.get_render_buffer(render_id,part_id,
					cam_result.target.target_id,id_array[render_id][part_id].length))!=null)
					ren_buf.clear_clip_flag(sk.component_cont);
		}
		var list=new list_component_on_collector(sk,ci,cam_result);
		
		long current_time=sk.current_time.nanoseconds();
		for(int i=0,ni=list.collector.component_collector.length;i<ni;i++)
			if(list.collector.component_collector[i]!=null)
				for(int j=0,nj=list.collector.component_collector[i].length;j<nj;j++) {
					component_link_list cll=list.collector.component_collector[i][j];
					for(;cll!=null;cll=cll.next_list_item)
						cll.comp.render_touch_time=current_time;
				}
		for(int i=0,ni=pps.length;i<ni;i++)
			if((ren_buf=buffer.get_render_buffer(pps[i][0],pps[i][1],
					cam_result.target.target_id,id_array[pps[i][0]][pps[i][1]].length))!=null)
					ren_buf.test_clip_flag_of_delete_component(cam_result,
						sk.component_cont,cam_result.target.parameter_channel_id);
		
		render_data_list.add(render_data_list.size(),new response_render_data(list.collector,cam_result));
		
		return list.collector;
	}
	private static void process_target(scene_kernel sk,client_information ci,render_component_counter rcc)
	{
		camera_result cr;
		render_target rt;
		render_target target_list[]=ci.target_container.get_render_target();
		int target_number=target_list.length;

		for(int pos;(pos=ci.target_component_collector_list.size())<target_number;){
			ci.target_component_collector_list.add(pos,null);
			ci.target_camera_result_list.add(pos,null);
		}
		for(int i=0;i<target_number;i++)
			if((rt=target_list[i])!=null)
				if((rt.camera_id>=0)&&(rt.camera_id<sk.camera_cont.size()))
					ci.target_camera_result_list.set(rt.target_id,
							new camera_result(sk.camera_cont.get(rt.camera_id),rt,sk.component_cont));
		for(int i=0;i<target_number;i++)
			if((rt=target_list[i])!=null)
				if(rt.main_display_target_flag)
					if((cr=ci.target_camera_result_list.get(rt.target_id))!=null){
						ci.display_camera_result=cr;
						break;
					}
		ArrayList<response_render_data> render_data_list=new ArrayList<response_render_data> (); 
		
		ci.request_response.print(",[");
		for(int response_number=0,i=0;i<target_number;i++)
			if((rt=target_list[i])!=null){
				cr=ci.target_camera_result_list.get(rt.target_id);
				ci.render_buffer.location_buffer.put_in_list(cr.cam.eye_component,sk);
	
				component_collector collector=collect_render_parts(render_data_list,sk,ci,cr);
				ci.target_component_collector_list.set(rt.target_id,collector);
				if(ci.display_camera_result!=null)
					if(ci.display_camera_result.target.target_id==rt.target_id)
						ci.display_component_collector=collector;
				
				
				if((response_number++)>0)
					ci.request_response.print(",");
				ci.render_buffer.target_buffer.response_parameter(rt,ci.request_response);
			}
		ci.request_response.print("]");
		
		response_component_render_parameter.response(render_data_list,sk,ci,rcc);
	}
	private static void response_parameter(scene_kernel sk,client_information ci,long delay_time_length)
	{
		long my_current_time_difference;
		my_current_time_difference =sk.current_time.nanoseconds();
		my_current_time_difference-=ci.render_buffer.response_current_time_pointer;
		ci.render_buffer.response_current_time_pointer+=my_current_time_difference;

		ci.request_response.print("[",sk.collector_stack.get_collector_version());
		ci.request_response.print(",",delay_time_length);
		ci.request_response.print(",",my_current_time_difference);
		for(int i=0,ni=sk.scene_par.max_modifier_container_number;i<ni;i++){
			modifier_container_timer timer=sk.modifier_cont[i].get_timer();
			modifier_parameter_buffer old_p=ci.render_buffer.modifier_parameter[i];
			modifier_parameter_buffer new_p=new modifier_parameter_buffer(timer.get_timer_adjust_value());
			if(new_p.timer_adjust_value==old_p.timer_adjust_value)
				continue;
			ci.request_response.print(",",i);
			ci.request_response.print(",",new_p.timer_adjust_value-old_p.timer_adjust_value);
			ci.render_buffer.modifier_parameter[i]=new_p;
		}
		ci.request_response.print("]");
	}
	private static int response_buffer_object_request(part p,scene_kernel sk,client_information ci)
	{
		String directory_name=file_directory.part_temporary_directory(p,sk.system_par,sk.scene_par)+"mesh.";
		String url_directory=ci.request_url_header+"&command=buffer&method=buffer_data"
								+"&render="+(p.render_id)+"&part="+(p.part_id)+"&data_file=";
		
		int ret_val=0;
		String type_str[]={"face","edge","point"};

		for(int i=0,ni=type_str.length;i<ni;i++){
			ArrayList<buffer_object_file_modify_time_and_length_item>item_list;
			ci.request_response.print((i<=0)?"[":",[");
			for(int j=0,nj=(item_list=p.boftal.boftal_list.get(i)).size();j<nj;j++){
				buffer_object_file_modify_time_and_length_item item=item_list.get(j);
				ci.request_response.print((j<=0)?"[":",[");
				if(!(item.buffer_object_file_in_head_flag)){
					String file_name=directory_name+type_str[i]+Integer.toString(j)+".gzip_text";
					String my_url=ci.caculate_file_proxy_url(file_name,
							sk.system_par.network_data_charset,sk.system_par);
					if(my_url==null)
						my_url=url_directory+type_str[i]+j+"&random="+Math.random();
					ci.request_response.print(item.buffer_object_text_file_length).
										print(",\"",my_url).print("\"");
					ret_val++;
				}
				ci.request_response.print("]");
			}
			ci.request_response.print("]");
		}
		return ret_val;
	}
	private static void response_buffer_object_request(
		scene_kernel sk,client_information ci,int current_loading_number,int max_loading_number)
	{
		ci.request_response.print(",[");
		for(int request_package[],i=current_loading_number;i<max_loading_number;i++){
			if((request_package=ci.render_buffer.mesh_loader.get_request_package(sk.process_part_sequence))==null)
				break;

			long package_length;
			String package_file_name;
			ArrayList<int[]> package_render_part_id;

			int part_type_id	=request_package[0];
			int part_package_id	=request_package[1];
			switch(part_type_id){
			case 0:
				package_file_name		=sk.render_cont.system_part_package.package_file_name[part_package_id];
				package_render_part_id	=sk.process_part_sequence.system_package_render_part_id.get(part_package_id);
				package_length			=sk.render_cont.system_part_package.package_length[part_package_id];
				break;
			case 1:
				package_file_name		=sk.render_cont.scene_part_package.package_file_name[part_package_id];
				package_render_part_id	=sk.process_part_sequence.scene_package_render_part_id.get(part_package_id);
				package_length			=sk.render_cont.scene_part_package.package_length[part_package_id];
				break;
			default:
				package_file_name		=sk.render_cont.type_part_package[part_type_id-2].package_file_name[part_package_id];
				package_render_part_id	=sk.process_part_sequence.type_package_render_part_id.get(part_type_id-2).get(part_package_id);
				package_length			=sk.render_cont.type_part_package[part_type_id-2].package_length[part_package_id];
				break;
			}

			String package_url=ci.caculate_file_proxy_url(
					package_file_name,sk.system_par.network_data_charset,sk.system_par);
			if(package_url==null) {
				package_url =ci.request_url_header;
				package_url+="&command=buffer&method=buffer_package&package=";
				package_url+=part_type_id+"_"+part_package_id;
				package_url+="&random="+Math.random();
			}
			ci.request_response.print((i<=current_loading_number)?"[\"":",[\"").
				print(package_url).print("\",",package_length).print(",[");

			for(int j=0,nj=package_render_part_id.size();j<nj;j++){
				int render_part_id[]=package_render_part_id.get(j);
				int render_id		=render_part_id[0];
				int part_id			=render_part_id[1];
				part p=sk.render_cont.renders.get(render_id).parts.get(part_id);
				ci.request_response.print((j<=0)?"[":",[",	p.render_id).
									print(",",				p.part_id).
									print(",",				p.part_package_sequence_id);
				ci.request_response.print(",[");
				i+=response_buffer_object_request(p,sk,ci);
				ci.request_response.print("]]");
			}
			ci.request_response.print("]]");
		}
		ci.request_response.print("]");
	}
	
	private static void display_data_load_message(scene_kernel sk,client_information ci)
	{
		String str;

		if((str=ci.request_response.get_parameter("loaded_length"))==null)
			return;
		int index_id;
		if((index_id=str.indexOf("_"))<0)
			return;
		ci.loaded_file_number=Integer.decode(str.substring(0,index_id));
		if((index_id=(str=str.substring(index_id+1)).indexOf("_"))<0)
			return;
		ci.loaded_data_length=Long.decode(str.substring(0,index_id));

		String display_message=sk.system_par.language_change_name.search_change_name(
				"load+"+ci.request_response.language_str,"Load");
		
		if(sk.process_part_sequence.total_data_length>0){
			display_message+="["+ci.loaded_file_number+":"+sk.process_part_sequence.total_file_number;
			display_message+="/"+(ci.loaded_data_length/1024)+"K:";
			display_message+=(sk.process_part_sequence.total_data_length/1024)+"K/";
			double value=ci.loaded_data_length*100.0/sk.process_part_sequence.total_data_length;
			display_message+=((int)(Math.round(value)))+"%]";
		}
		
		int loading_render_id,loading_part_id;
		if((index_id=(str=str.substring(index_id+1)).indexOf("_"))>0){
			loading_render_id=Integer.decode(str.substring(0,index_id  ));
			loading_part_id  =Integer.decode(str.substring(  index_id+1));
			if((loading_render_id>=0)&&(loading_render_id<sk.render_cont.renders.size())) {
				render r=sk.render_cont.renders.get(loading_render_id);
				if((loading_part_id>=0)&&(loading_part_id<r.parts.size())) {
					part p=r.parts.get(loading_part_id);
					if(p!=null)
						display_message+=":"+p.user_name;
				}
			}
		}
		
		ci.message_display.set_display_message(display_message,
			(ci.loaded_data_length>=sk.process_part_sequence.total_data_length)?1000*1000*1000*10:-1);
	}
	public static void do_render(scene_kernel sk,client_information ci,long delay_time_length)
	{
		String str;
		int index_id,current_loading_number=0;
		int max_loading_number=sk.system_par.default_max_loading_number;
		
		if((str=ci.request_response.get_parameter("requesting_number"))!=null)
			if((index_id=str.indexOf("_"))>0) {
				try{
					max_loading_number=Integer.decode(str.substring(index_id+1));
					current_loading_number=Integer.decode(str.substring(0,index_id));
				}catch(Exception e){
					;
				}
				if(current_loading_number<0)
					current_loading_number=0;
				if(max_loading_number<1)
					max_loading_number=1;
				if(max_loading_number>sk.system_par.max_loading_number)
					max_loading_number=sk.system_par.max_loading_number;
			}
		
		ci.render_buffer.mesh_loader.clear_request_package_id(max_loading_number);

		display_data_load_message(sk,ci);

		render_component_counter rcc=new render_component_counter();
				
		ci.request_response.print("[");
		
		response_parameter(sk,ci,delay_time_length);
		process_target(sk,ci,rcc);
		new response_component_buffer_parameter(sk,ci,rcc);
		ci.render_buffer.cam_buffer.response_camera_buffer_data(ci,sk.camera_cont);
		ci.render_buffer.location_buffer.response_location(sk,ci,rcc);
		response_buffer_object_request(sk,ci,current_loading_number,max_loading_number);
		
		ci.request_response.print("]");

		return;
	}
}
