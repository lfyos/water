package driver_manipulator;

import java.util.ArrayList;

import kernel_camera.locate_camera;
import kernel_common_class.change_name;
import kernel_common_class.const_value;
import kernel_common_class.common_reader;
import kernel_common_class.component_collector_jason_component;
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
	private static component_collector get_collector(engine_kernel ek,client_information ci)
	{
		String str,request_charset=ci.request_response.implementor.get_request_charset();
		if((str=ci.request_response.get_parameter("list_id"))!=null)
			return ek.collector_stack.get_collector_by_list_id(Long.decode(str));
		if((str=ci.request_response.get_parameter("list_title"))!=null){
			try{
				str=java.net.URLDecoder.decode(str,request_charset);
				str=java.net.URLDecoder.decode(str,request_charset);
			}catch(Exception e) {
				return null;
			}
			return ek.collector_stack.get_collector_by_list_title(str);
		}
		return ek.collector_stack.get_top_collector();
	}
	private static void part_list_selection(int modifier_container_id,int select_type_id,
			String request_charset,engine_kernel ek,client_information ci,
			driver_audio_player.extended_component_driver acd)
	{
		String str;
		component my_comp=null;
		component_selection cs=new component_selection(ek);
		component_array comp_con=new component_array();

		if((str=ci.request_response.get_parameter("component_id"))!=null) {
			my_comp=ek.component_cont.get_component(Integer.decode(str));
		}else if((str=ci.request_response.get_parameter("component_name"))!=null) {
			try{
				str=java.net.URLDecoder.decode(str,request_charset);
				str=java.net.URLDecoder.decode(str,request_charset);
			}catch(Exception e) {
				return;
			}
			my_comp=ek.component_cont.search_component(str);
		}
		if(my_comp!=null){
			comp_con.add_component(my_comp);
			switch(select_type_id){
			case 1:
				cs.set_selected_flag(my_comp,ek.component_cont);
				break;
			case 2:
				cs.clear_selected_flag(my_comp,ek.component_cont);
				break;
			case 3:
				cs.switch_selected_flag(my_comp,ek.component_cont);
				break;
			default:
				return;
			}
		}else {
			component_collector collector;
			if((collector=get_collector(ek,ci))==null)
				return;
			if(collector.component_number<=0)
				return;
			
			int render_id=-1,part_id=-1;
			if((str=ci.request_response.get_parameter("render_id"))!=null)
				render_id=Integer.decode(str);
			if((str=ci.request_response.get_parameter("part_id"))!=null)
				part_id=Integer.decode(str);
			
			for(int i=0,ni=collector.component_collector.length;i<ni;i++){
				if(collector.component_collector[i]==null)
					continue;
				if(render_id>=0)
					if(i!=render_id)
						continue;
				for(int j=0,nj=collector.component_collector[i].length;j<nj;j++){
					component_link_list cll;
					if((cll=collector.component_collector[i][j])==null)
						continue;
					if(part_id>=0)
						if(j!=part_id)
							continue;
					for(;cll!=null;cll=cll.next_list_item){
						comp_con.add_component(cll.comp);
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
		}
		
		acd.set_audio(null);
		
		if(ci.display_camera_result.cam.parameter.movement_flag)
			if(comp_con.comp_list.size()>0)
				if(select_type_id!=2)
					(new locate_camera(ci.display_camera_result.cam)).locate_on_components(
						ek.modifier_cont[modifier_container_id],comp_con.get_box(),null,
						ci.display_camera_result.cam.parameter.scale_value,true,true,
						ci.display_camera_result.cam.parameter.scale_value>const_value.min_value);
		return;
	}
	public static void part_list_request(
			int modifier_container_id,boolean save_component_name_or_id_flag,component comp,
			engine_kernel ek,client_information ci,driver_audio_player.extended_component_driver acd)
	{
		String str,request_charset=ci.request_response.implementor.get_request_charset();
		component_collector collector=null;
		component_array comp_cont;
		
		if((str=ci.request_response.get_parameter("operation"))==null)
			return;
		switch(str.toLowerCase()){
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
		case "selection":
			if((str=ci.request_response.get_parameter("selection"))==null)
				return;
			switch(str.toLowerCase()){
			case "select":
				part_list_selection(modifier_container_id,1,request_charset,ek,ci,acd);
				return;
			case "unselect":
				part_list_selection(modifier_container_id,2,request_charset,ek,ci,acd);
				return;
			case "swap":
				part_list_selection(modifier_container_id,3,request_charset,ek,ci,acd);
				return;
			default:
				return;
			}
		case "title":
		{
			if((collector=get_collector(ek,ci))==null)
				return;
			acd.set_audio(null);
			int flag=0;
			if((str=ci.request_response.get_parameter("title"))!=null)
				try{
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
					str=(str==null)?"":str;
					collector.title=(collector.title==null)?"":collector.title;
					if(collector.title.compareTo(str)!=0) {
						collector.title=str;
						flag++;
					}
				}catch(Exception e){
					;
				}
			if((str=ci.request_response.get_parameter("description"))!=null)
				try{
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
					str=(str==null)?"":str;
					collector.description=(collector.description==null)?"":collector.description;
					if(collector.description.compareTo(str)!=0) {
						collector.description=str;
						flag++;
					}
				}catch(Exception e){
					;
				}
			if(flag>0)
				ek.collector_stack.update_collector_version();
			return;
		}
		case "delete":
			if((collector=get_collector(ek,ci))==null)
				return;
			acd.set_audio(null);
			ek.collector_stack.delete_collector(collector.list_id,ek.component_cont);
			if((collector=ek.collector_stack.get_top_collector())!=null)
				acd.set_audio(collector.audio_file_name);
			return;
		case "top":
			if((collector=get_collector(ek,ci))==null)
				return;
			acd.set_audio(null);
			collector=ek.collector_stack.delete_collector(collector.list_id,ek.component_cont);
			if(collector!=null){
				long old_id=collector.list_id;
				ek.collector_stack.push_collector(false,ek.system_par,ek.scene_par,
							collector,ek.component_cont,ek.render_cont.renders);
				collector.list_id=old_id;
				acd.set_audio(collector.audio_file_name);
			}
			return;
		case "create":
			boolean part_list_flag_effective_flag=true; 
			switch(((str=ci.request_response.get_parameter("part_list"))!=null)?(str.toLowerCase().trim()):"true"){
			default:
			case "true":
			case "yes":
				part_list_flag_effective_flag=true; 
				break;
			case "false":
			case "no":
				part_list_flag_effective_flag=false;
				break;
			}
			comp_cont=new component_array();
			if((str=ci.request_response.get_parameter("component_name"))!=null){
				try {
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e) {
					str=null;
				}
				if(str!=null) {
					component my_comp=ek.component_cont.search_component(str);
					if(my_comp!=null)
						comp_cont.add_component(my_comp);
					if(comp_cont.comp_list.size()<=0)
						return;
				}
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
				if(comp_cont.comp_list.size()<=0)
					return;
			}else{
				comp_cont.add_selected_component(ek.component_cont.root_component,false);
				if(comp_cont.comp_list.size()<=0)
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
			ArrayList<component_collector> cc_list=new ArrayList<component_collector>();
			switch(str){
			default:
			case "all":
				cc_list=ci.target_component_collector_list;
				break;
			case "display":
				cc_list.add(ci.display_component_collector);
				break;
			}
			for(int i=cc_list.size()-1;i>=0;i--) {
				component_collector cc=cc_list.get(i);
				if(cc!=null)
					ek.collector_stack.push_collector(false,ek.system_par,
						ek.scene_par,cc,ek.component_cont,ek.render_cont.renders);
			}
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
			ek.collector_stack.save(fw,ek.component_cont,
				ci.request_response.get_boolean("top",false),save_component_name_or_id_flag);
			fw.close();
			return;
		case "load":
		{
			file_reader cr=new file_reader(
					ek.collector_stack.component_collector_stack_file_name,
					ek.collector_stack.component_collector_stack_file_charset);
			ek.collector_stack.load(cr,ek.component_cont,ek.system_par,ek.scene_par,ek.render_cont.renders);
			cr.close();
			acd.set_audio(null);
			if((collector=ek.collector_stack.get_top_collector())!=null)
				acd.set_audio(collector.audio_file_name);
			return;
		}
		case "download":
			ek.collector_stack.save(ci.request_response,ek.component_cont,
				ci.request_response.get_boolean("top",false),save_component_name_or_id_flag);
			return;
		case "upload":
		{
			common_reader cr=new common_reader(ci.request_response.implementor.get_content_stream(),ci.request_response.get_charset());
			ek.collector_stack.load(cr,ek.component_cont,ek.system_par,ek.scene_par,ek.render_cont.renders);
			cr.close();			
			acd.set_audio(null);
			if((collector=ek.collector_stack.get_top_collector())!=null)
				acd.set_audio(collector.audio_file_name);
			return;
		}
		case "upload_webpage":
			String my_upload_url=ci.request_url_header;
			my_upload_url+="&command=component&method=event&event_method=part_list&operation=upload";
			my_upload_url+="&event_driver_id=all&event_component_id="+Integer.toString(comp.component_id);
			
			if((str=ci.request_response.get_parameter("change_name"))==null)
				str="";
			if((str=str.trim()).length()>0){
				try{
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e){
					;
				}
			}
			new upload_web_page(ci,new change_name(null,str,null),"text/plain",
					ci.request_response.get_charset(),my_upload_url).create_web_page();
			return;
		default:
			return;
		}
	}
}
