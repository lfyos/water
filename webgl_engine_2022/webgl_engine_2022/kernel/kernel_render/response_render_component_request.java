package kernel_render;

import java.util.ArrayList;

import kernel_part.part;
import kernel_engine.engine_kernel;
import kernel_camera.camera_result;
import kernel_buffer.component_render;
import kernel_buffer.part_mesh_loader;
import kernel_engine.client_information;
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
			int render_buffer_id,	render_target rt,
			engine_kernel ek,		client_information ci,
			camera_result cam_result)
	{	
		component_render ren_buf;
		
		int pps[][]=ek.process_part_sequence.process_parts_sequence;
		int id_array[][][][]=ek.component_cont.part_component_id_and_driver_id;
		component_render_buffer	buffer=ci.render_buffer.component_buffer;

		for(int i=0,ni=pps.length;i<ni;i++) {
			int render_id=pps[i][0],part_id=pps[i][1];
			if((ren_buf=buffer.get_render_buffer(render_id,part_id,
				render_buffer_id,id_array[render_id][part_id].length))!=null)
					ren_buf.clear_clip_flag(ek.component_cont);
		}
		
		component_collector list_result=(new list_component_on_collector(false,
			rt.do_discard_lod_flag,rt.do_selection_lod_flag,false,true,ek,ci,cam_result)).collector;
		
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
		
		render_data_list.add(render_data_list.size(),
				new response_render_data(render_buffer_id,list_result,cam_result));
		
		return list_result;
	}
	private static void process_target(engine_kernel ek,client_information ci,render_component_counter rcc)
	{
		render_target rt;
		camera_result cr;
		int target_number=ci.target_container.get_render_target_number();
		for(int pos;(pos=ci.target_component_collector_list.size())<target_number;){
			ci.target_component_collector_list.add(pos,null);
			ci.target_camera_result_list.add(pos,null);
		}

		render_target target_list[]=ci.target_container.get_render_target();
		
		for(int i=0,ni=target_list.length;i<ni;i++)
			if((rt=target_list[i]).camera_id>=0)
				if(rt.camera_id<ek.camera_cont.size())
					ci.target_camera_result_list.set(rt.target_id,
						new camera_result(ek.camera_cont.get(rt.camera_id),rt,ek.component_cont));
		
		for(int i=0,ni=target_list.length;i<ni;i++)
			if((rt=target_list[i]).main_display_target_flag)
				if((cr=ci.target_camera_result_list.get(rt.target_id))!=null){
					ci.display_camera_result=cr;
					break;
				}
		
		ArrayList<response_render_data> render_data_list=new ArrayList<response_render_data> (); 
		
		ci.request_response.print(",[");
		for(int response_number=0,i=0,ni=target_list.length;i<ni;i++)
			if((rt=target_list[i])!=null)
				if(rt.do_render_flag){
					cr=ci.target_camera_result_list.get(rt.target_id);
					int render_buffer_id=cr.get_render_buffer_id(ci);
					
					ci.render_buffer.location_buffer.put_in_list(cr.cam.eye_component,ek);
					ci.request_response.print(((response_number++)<=0)?"":",",render_buffer_id);
					component_collector collector=collect_render_parts(render_data_list,render_buffer_id,rt,ek,ci,cr);
					ci.target_component_collector_list.set(rt.target_id,collector);
					if(ci.display_camera_result!=null)
						if(ci.display_camera_result.target.target_id==rt.target_id)
							ci.display_component_collector=collector;
					
					ci.render_buffer.target_buffer.response_parameter(render_buffer_id,rt,ci.request_response);
				}
		ci.request_response.print("]");
		
		response_component_render_parameter.response(render_data_list,ek,ci,rcc);
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
		for(int i=0,ni=ek.scene_par.max_modifier_container_number;i<ni;i++){
			modifier_container_timer timer=ek.modifier_cont[i].get_timer();
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
	private static int response_buffer_object_request(part p,engine_kernel ek,client_information ci)
	{
		String directory_name=file_directory.part_file_directory(p,ek.system_par,ek.scene_par)+"mesh.";
		String url_directory=ci.request_url_header+"&command=buffer&operation=buffer_data"
								+"&render="+(p.render_id)+"&part="+(p.part_id)+"&data_file=";
		
		int ret_val=0;
		String type_str[]={"face","edge","point"};
		
		for(int i=0,ni=type_str.length;i<ni;i++){
			ArrayList<buffer_object_file_modify_time_and_length_item> item_list=p.boftal.list.get(i);
			ci.request_response.print((i<=0)?"[":",[");
			for(int j=0,nj=item_list.size();j<nj;j++){
				buffer_object_file_modify_time_and_length_item item=item_list.get(j);
				ci.request_response.print((j<=0)?"[":",[");
				if(!(item.buffer_object_file_in_head_flag)){
					String my_url,file_name=directory_name+type_str[i]+Integer.toString(j)+".gzip_text";
					if((my_url=ci.get_file_proxy_url(file_name,ek.system_par))==null)
						my_url=url_directory+type_str[i]+j;
					ci.request_response.print(item.buffer_object_text_file_length).print(",\"",my_url).print("\"");
					ret_val++;
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
		boolean only_request_flag=false;
		
		ci.request_response.print(",[");
		for(int i=requesting_number;i<max_request_number;i++){
			part_mesh_loader pml=ci.render_buffer.mesh_loader;
			int request_package[]=pml.get_request_package(
					ek.process_part_sequence,only_request_flag);
			if(request_package==null)
				break;
			only_request_flag=true;

			int part_type_id	=request_package[0];
			int part_package_id	=request_package[1];
			
			long package_length;
			String package_file_name;
			ArrayList<int[]> package_render_part_id;

			switch(part_type_id){
			default:
			case 0:
				package_file_name		=ek.render_cont.system_part_package.package_file_name[part_package_id];
				package_render_part_id	=ek.process_part_sequence.system_package_render_part_id.get(part_package_id);
				package_length			=ek.render_cont.system_part_package.package_length[part_package_id];
				break;
			case 1:
				package_file_name		=ek.render_cont.type_part_package.package_file_name[part_package_id];
				package_render_part_id	=ek.process_part_sequence.type_package_render_part_id.get(part_package_id);
				package_length			=ek.render_cont.type_part_package.package_length[part_package_id];
				break;
			case 2:
				package_file_name		=ek.render_cont.scene_part_package.package_file_name[part_package_id];
				package_render_part_id	=ek.process_part_sequence.scene_package_render_part_id.get(part_package_id);
				package_length			=ek.render_cont.scene_part_package.package_length[part_package_id];
				break;
			}

			String package_url=ci.get_file_proxy_url(package_file_name,ek.system_par);
			if(package_url==null) {
				package_url =ci.request_url_header;
				package_url+="&command=buffer&operation=buffer_package&package=";
				package_url+=part_type_id+"_"+part_package_id;
			}
			ci.request_response.print((i<=requesting_number)?"[\"":",[\"").
				print(package_url).print("\",",package_length).print(",[");

			for(int j=0,nj=package_render_part_id.size();j<nj;j++){
				int render_part_id[]=package_render_part_id.get(j);
				int render_id		=render_part_id[0];
				int part_id			=render_part_id[1];
				part p=ek.render_cont.renders.get(render_id).parts.get(part_id);
				ci.request_response.print((j<=0)?"[":",[",	p.render_id).
									print(",",				p.part_id).
									print(",",				p.part_package_sequence_id);
				ci.request_response.print(",[");
				i+=response_buffer_object_request(p,ek,ci);
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
		int loading_render_id=Integer.decode(str.substring(0,index_id  ));
		int loading_part_id  =Integer.decode(str.substring(  index_id+1));
			
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
		if((loading_render_id>=0)&&(loading_render_id<ek.render_cont.renders.size())) {
			render r=ek.render_cont.renders.get(loading_render_id);
			if((loading_part_id>=0)&&(loading_part_id<r.parts.size())) {
				part p=r.parts.get(loading_part_id);
				if(p!=null)
					str+=":"+p.user_name;
			}
		}
		ci.message_display.set_display_message(str,
				(loaded_buffer_object_data_length>=total_length)?1000*1000*1000*10:-1);
	}
	public static void do_render(engine_kernel ek,client_information ci,long delay_time_length)
	{
		int my_loading_request_number=0,max_loading_request_number=ek.system_par.max_loading_number,index_id;
		String str;
		if((str=ci.request_response.get_parameter("requesting_number"))!=null)
			if((index_id=str.indexOf("_"))>0){
				if((my_loading_request_number=Integer.decode(str.substring(0,index_id)))<0)
					my_loading_request_number=0;
				if((max_loading_request_number=Integer.decode(str.substring(index_id+1)))<1)
					max_loading_request_number=1;
				if(max_loading_request_number>ek.system_par.max_loading_number)
					max_loading_request_number=ek.system_par.max_loading_number;
				ci.render_buffer.mesh_loader.test_request_package(max_loading_request_number);
			}

		display_data_load_message(ek,ci);

		render_component_counter rcc=new render_component_counter();
				
		ci.request_response.print("[");
		
		response_parameter(ek,ci,delay_time_length);
		process_target(ek,ci,rcc);
		new response_component_buffer_parameter(ek,ci,rcc);
		ci.render_buffer.cam_buffer.response_camera_buffer_data(ci,ek.camera_cont);
		ci.render_buffer.location_buffer.response_location(ek,ci,rcc);
		response_buffer_object_request(ek,ci,my_loading_request_number,max_loading_request_number);
		
		ci.request_response.print("]");

		return;
	}
}
