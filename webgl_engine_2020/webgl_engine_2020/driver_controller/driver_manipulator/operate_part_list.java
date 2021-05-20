package driver_manipulator;

import kernel_camera.locate_camera;
import kernel_common_class.change_name;
import kernel_common_class.const_value;
import kernel_common_class.common_reader;
import kernel_common_class.upload_web_page;
import kernel_component.component;
import kernel_component.component_array;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_component.component_selection;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class operate_part_list 
{
	private static void part_list_selection(
			int select_type_id,int camera_modifier_id,
			engine_kernel ek,client_information ci,
			driver_audio.extended_component_driver acd)
	{
		String str;
		int render_id=-1,part_id=-1,component_id=-1;
		component_collector collector;
		
		component_array comp_con=new component_array(ek.component_cont.root_component.component_id+1);
		component_collector loaded_part_collector=new component_collector(ek.render_cont.renders);
		
		if((collector=ek.collector_stack.get_top_collector())==null)
			return;
		if(collector.component_number<=0)
			return;
		if((str=ci.request_response.get_parameter("render_id"))!=null)
			render_id=Integer.decode(str);
		if((str=ci.request_response.get_parameter("part_id"))!=null)
			part_id=Integer.decode(str);
		if((str=ci.request_response.get_parameter("component_id"))!=null)
			component_id=Integer.decode(str);
		for(int i=0,ni=collector.component_collector.length;i<ni;i++){
			if(collector.component_collector[i]!=null)
				for(int j=0,nj=collector.component_collector[i].length;j<nj;j++){
					component_selection cs=new component_selection(ek);
					component_link_list cll=collector.component_collector[i][j];
					for(;cll!=null;cll=cll.next_list_item){
						if(cll.comp.driver_number()<=0)
							continue;
						if(cll.comp.driver_array[0].component_part==null)
							continue;
						if(!(cll.comp.uniparameter.part_list_flag))
							continue;
						if(render_id>=0)
							if(i!=render_id)
								continue;
						if(part_id>=0)
							if(j!=part_id)
								continue;
						if(component_id>=0)
							if(cll.comp.component_id!=component_id)
								continue;
						comp_con.add_component(cll.comp);
						for(int k=0,nk=cll.comp.driver_number();k<nk;k++)
							if(cll.comp.children_number()<=0)
								if(cll.comp.driver_array[k].component_part.mesh_file_name!=null)
									loaded_part_collector.register_component(cll.comp,k);
						switch(select_type_id){
						case 1:
							cs.set_selected_flag(cll.comp,ek.component_cont);
							break;
						case 2:
							cs.clear_selected_flag(cll.comp,ek.component_cont);
							break;
						case 3:
							cs.switch_selected_flag(cll.comp,ek.component_cont);
							break;
						default:
							return;
						}
					}
				}
		}
		acd.set_audio(null);
		
		if(select_type_id==2)
			return;
		if(comp_con.component_number<=0)
			return;
		if(ci.display_camera_result.cam.parameter.movement_flag)
			(new locate_camera(ci.display_camera_result.cam)).locate_on_components(
				ek.modifier_cont[camera_modifier_id],comp_con.get_box(),null,
				ci.display_camera_result.cam.parameter.scale_value,ci.parameter.aspect,true,true,
				ci.display_camera_result.cam.parameter.scale_value>const_value.min_value);
		return;
	}
	public static void part_list_request(boolean save_component_name_or_id_flag,
			component comp,engine_kernel ek,client_information ci,int camera_modifier_id,
			change_name language_change_name,driver_audio.extended_component_driver acd)
	{
		String str;
		component_collector collector=null;
		component_array comp_cont;
		component_selection cs;
		common_reader cr;
		
		if((str=ci.request_response.get_parameter("operation"))==null)
			return;
		
		switch(str.toLowerCase()){
		case "webpage":
			if((str=ci.request_response.get_parameter("list_type"))==null)
				str="simple";
			(new component_collector_web_page(ci,				ek.collector_stack.get_top_collector(),
					ek.collector_stack.get_all_collector(),		str,ci.channel_id,comp.component_id,
					ek.scene_par.component_sort_type,			ek.scene_par.component_sort_min_distance,
					language_change_name)).create_web_page();
			return;
		case "part_list_jason_part":
			new component_collector_jason_part(ek.collector_stack.get_top_collector(),ci,ek);
			return;
		case "part_list_jason_component":
			str=ci.request_response.get_parameter("flag");
			switch((str==null)?"true":(str.toLowerCase().trim())){
			case "true":
			case "yes":
			case "on":
				str="true";
				break;
			}
			new component_collector_jason_component(
				str.compareTo("true")==0,ek.collector_stack.get_top_collector(),ci,ek);
			return;
		case "upload_webpage":
			str="command=component&method=event";
			str+="&event_component_id="+Integer.toString(comp.component_id);
			str+="&event_driver_id=all&event_method=part_list&operation=";
			(new upload_web_page(ci,language_change_name,"text",ci.request_response.get_charset(),
				str+"upload",str+"webpage&list_type=collector")).create_web_page();
			return;
		case "upload":
			cr=new common_reader(
				ci.request_response.implementor.get_content_stream(),ci.request_response.get_charset());
			ek.collector_stack.load(cr,ek.component_cont,
					ek.system_par,ek.scene_par,ek.render_cont.renders);
			cr.close();			
			acd.set_audio(null);
			if((collector=ek.collector_stack.get_top_collector())!=null)
				acd.set_audio(collector.audio_file_name);
			return;
		case "selection":
			if((str=ci.request_response.get_parameter("selection"))==null)
				return;
			switch(str.toLowerCase()){
			case "select":
				part_list_selection(1,camera_modifier_id,ek,ci,acd);
				return;
			case "unselect":
				part_list_selection(2,camera_modifier_id,ek,ci,acd);
				return;
			case "swap":
				part_list_selection(3,camera_modifier_id,ek,ci,acd);
				return;
			default:
				return;
			}
		case "manipulation":
			if((str=ci.request_response.get_parameter("list_id"))!=null)
				collector=ek.collector_stack.get_one_collector(Long.decode(str));
			if(collector==null)
				if((collector=ek.collector_stack.get_top_collector())==null)
					return;
			acd.set_audio(null);

			if((str=ci.request_response.get_parameter("manipulation"))==null)
				return;
			switch(str.toLowerCase()){
			default:
				return;
			case "title":
				if((str=ci.request_response.get_parameter("title"))!=null)
					try{
						str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
						str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
						collector.title=str;
					}catch(Exception e){
						;
					}
				if((str=ci.request_response.get_parameter("description"))!=null)
					try{
						str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
						str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
						collector.description=str;
					}catch(Exception e){
						;
					}
				return;
			case "delete":
				ek.collector_stack.delete_collector(collector.list_id,ek.component_cont);
				if((collector=ek.collector_stack.get_top_collector())!=null)
					acd.set_audio(collector.audio_file_name);
				return;
			case "top":
				ek.collector_stack.push_collector(ek.system_par,ek.scene_par,
					ek.collector_stack.delete_collector(collector.list_id,ek.component_cont),
					ek.component_cont,ek.render_cont.renders);
				acd.set_audio(collector.audio_file_name);
				
				return;
			case "select":
				comp_cont=new component_array(ek.component_cont.root_component.component_id+1);
				comp_cont.add_collector(collector);
				cs=new component_selection(ek);	
				cs.set_component_container_selected(comp_cont,ek.component_cont);
				break;
			case "unselect":
				comp_cont=new component_array(
						ek.component_cont.root_component.component_id+1);
				comp_cont.add_collector(collector);
				cs=new component_selection(ek);	
				for(int i=0,ni=comp_cont.component_number;i<ni;i++)
					cs.clear_selected_flag(comp_cont.comp[i],ek.component_cont);
				return;
			case "swap":
				comp_cont=new component_array(ek.component_cont.root_component.component_id+1);
				comp_cont.add_collector(collector);
				cs=new component_selection(ek);	
				for(int i=0,ni=comp_cont.component_number;i<ni;i++)
					cs.switch_selected_flag(comp_cont.comp[i],ek.component_cont);
				break;
			}
			if(comp_cont.component_number>0)
				if(ci.display_camera_result.cam.parameter.movement_flag)
					(new locate_camera(ci.display_camera_result.cam)).locate_on_components(
						ek.modifier_cont[camera_modifier_id],comp_cont.get_box(),null,
						ci.display_camera_result.cam.parameter.scale_value,ci.parameter.aspect,true,true,
						ci.display_camera_result.cam.parameter.scale_value>const_value.min_value);
			return;
		case "create":
			boolean part_list_flag_effective_flag=true; 
			switch(((str=ci.request_response.get_parameter("part_list"))!=null)?(str.toLowerCase().trim()):"true"){
			default:
			case "true":
				part_list_flag_effective_flag=true; 
				break;
			case "false":
				part_list_flag_effective_flag=false;
				break;
			}
			comp_cont=new component_array(ek.component_cont.root_component.component_id+1);
			if((str=ci.request_response.get_parameter("component_name"))!=null){
				component my_comp=ek.component_cont.search_component(str);
				if(my_comp!=null)
					comp_cont.add_component(my_comp);
				if(comp_cont.component_number<=0)
					return;
			}else if((str=ci.request_response.get_parameter("component_id"))!=null){
				int component_id;
				component my_comp;
				try {
					component_id=Integer.decode(str);
				}catch(Exception e) {
					component_id=-1;
				}
				if((my_comp=ek.component_cont.get_component(component_id))!=null)
					comp_cont.add_component(my_comp);
				if(comp_cont.component_number<=0)
					return;
			}else{
				comp_cont.add_selected_component(ek.component_cont.root_component);
				if(comp_cont.component_number<=0)
					comp_cont.add_component(ek.component_cont.root_component);
			}
			collector=ek.collector_stack.push_component_array(part_list_flag_effective_flag,
					ek.system_par,ek.scene_par,comp_cont,ek.component_cont,ek.render_cont.renders);
			acd.set_audio(null);
			if((collector=ek.collector_stack.get_top_collector())!=null)
				acd.set_audio(collector.audio_file_name);
			return;
		case "target":
			if((str=ci.request_response.get_parameter("target"))==null)
				str="display";
			component_collector cc[];
			switch(str){
			default:
			case "all":
				cc=ci.target_component_collector_array;
				break;
			case "display":
				cc=new component_collector[]{ci.display_component_collector};
				break;
			case "selection":
				cc=new component_collector[]{ci.selection_component_collector};
			}
			for(int i=cc.length-1;i>=0;i--)
				if(cc[i]!=null)
					ek.collector_stack.push_collector(ek.system_par,
						ek.scene_par,cc[i],ek.component_cont,ek.render_cont.renders);
			acd.set_audio(null);
			if((collector=ek.collector_stack.get_top_collector())!=null)
				acd.set_audio(collector.audio_file_name);
			return;
		case "clear":
			acd.set_audio(null);
			ek.collector_stack.pop(ek.component_cont,true);
			return;
		case "save":
			file_writer fw=new file_writer(
					ek.collector_stack.component_collector_stack_file_name,
					ek.collector_stack.component_collector_stack_file_charset);
			ek.collector_stack.save(fw,ek.component_cont,save_component_name_or_id_flag);
			fw.close();
			return;
		case "load":
			cr=new file_reader(
					ek.collector_stack.component_collector_stack_file_name,
					ek.collector_stack.component_collector_stack_file_charset);
			ek.collector_stack.load(cr,ek.component_cont,ek.system_par,ek.scene_par,ek.render_cont.renders);
			cr.close();
			acd.set_audio(null);
			if((collector=ek.collector_stack.get_top_collector())!=null)
				acd.set_audio(collector.audio_file_name);
			return;
		case "download":
			ek.collector_stack.save(ci.request_response,ek.component_cont,save_component_name_or_id_flag);
			return;
		default:
			return;
		}
	}
}
