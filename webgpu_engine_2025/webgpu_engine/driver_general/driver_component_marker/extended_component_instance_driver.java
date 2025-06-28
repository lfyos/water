package driver_component_marker;

import kernel_scene.scene_kernel;
import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_camera.locate_camera;
import kernel_transformation.point;
import kernel_driver.component_driver;
import kernel_scene.client_information;
import kernel_common_class.jason_string;
import kernel_component.component_selection;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private component_marker_container cmc;

	private int modifier_container_id;
	
	public void destroy()
	{
		super.destroy();
		
		if(cmc!=null)
			cmc=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,
			component_marker_container my_cmc,int my_modifier_container_id)
	{
		super(my_comp,my_driver_id);

		cmc=my_cmc;
		modifier_container_id=my_modifier_container_id;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		if(cmc.pickup_flag){
			if(!(cr.target.main_display_target_flag)) 
				return true;
			if(ci.parameter.comp==null)
				return true;
			if(ci.parameter.comp.driver_array.size()<=0)
				return true;
			component_driver c_d=ci.parameter.comp.driver_array.get(0);
			if(c_d.component_part==null)
				return true;
			point p;
			if((p=ci.display_camera_result.caculate_local_focus_point(ci.parameter))==null)
				return true;
			
			cmc.clear_all_component_marker(sk,false);
			cmc.component_marker_list.add(new component_marker(
				ci.parameter.comp,c_d.component_part.user_name,p.x,p.y,p.z));
			update_component_parameter_version(0);
			ci.render_buffer.location_buffer.put_in_list(ci.parameter.comp,sk);
			return false;
		}
		if(cmc.component_marker_list.size()<=0)
			return true;
		if(cr.target.main_display_target_flag) {
			component my_comp;
			component_marker my_cm;
			for(int i=0,ni=cmc.component_marker_list.size();i<ni;i++)
				if((my_cm=cmc.component_marker_list.get(i))!=null){
					if((my_comp=sk.component_cont.get_component(my_cm.marker_component_id))!=null) 
						ci.render_buffer.location_buffer.put_in_list(my_comp,sk);
					else{
						cmc.component_marker_list.remove(i);
						update_component_parameter_version(0);
						ni--;
						i--;
					}
				}
		}
		return false;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		component_marker my_cm;
		component my_comp;
		String pre_str="[";
		
		ci.request_response.print("[");
		for(int i=0,ni=cmc.component_marker_list.size();i<ni;i++) {
			if((my_cm=cmc.component_marker_list.get(i))==null)
				continue;
			if((my_comp=sk.component_cont.get_component(my_cm.marker_component_id))==null)
				continue;
			ci.request_response.
				print(pre_str,	my_comp.component_id).
				print(",",		my_cm.marker_x).
				print(",",		my_cm.marker_y).
				print(",",		my_cm.marker_z).
				print(",",		jason_string.change_string(my_cm.marker_text.trim())).
				print(			cmc.pickup_flag?",true]":",false]");
			pre_str=",[";
		}
		ci.request_response.print("]");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		component operate_comp;
		component_marker operate_cm;

		String str,marker_text,pre_char="";
		switch(((str=ci.request_response.get_parameter("operation"))==null)?"":str) {
		default:
			break;
		case "jason":
			ci.request_response.println("[");
			
			for(int i=0,ni=cmc.component_marker_list.size();i<ni;i++) {
				component_marker my_cm;
				if((my_cm=cmc.component_marker_list.get(i))==null)
					continue;
				if((operate_comp=sk.component_cont.get_component(my_cm.marker_component_id))==null)
					continue;
				ci.request_response.println(pre_char);pre_char=",";
				
				ci.request_response.println("	{");
				ci.request_response.print  ("		\"marker_id\":		",		my_cm.marker_id).		println(",");
				
				str=jason_string.change_string(operate_comp.component_name);
				ci.request_response.print  ("		\"component_name\":	",	str).						println(",");
				ci.request_response.print  ("		\"component_id\":	",	my_cm.marker_component_id).	println(",");
				ci.request_response.print  ("		\"marker_x\":		",	my_cm.marker_x).			println(",");
				ci.request_response.print  ("		\"marker_y\":		",	my_cm.marker_y).			println(",");
				ci.request_response.print  ("		\"marker_z\":		",	my_cm.marker_z).			println(",");
				
				str=jason_string.change_string(my_cm.marker_text);
				ci.request_response.println("		\"marker_text\":		",	str).print("	}");
			}
			ci.request_response.println().println("]");
			break;
		case "clear_all":
			cmc.clear_all_component_marker(sk,true);
			if(cmc.global_private_flag)
				comp.driver_array.get(driver_id).update_component_parameter_version();
			else
				update_component_parameter_version(0);
			break;
		case "clear":
			if((str=ci.request_response.get_parameter("marker_id"))==null)
				break;
			cmc.clear_component_marker(Long.parseLong(str),sk);
			if(cmc.global_private_flag)
				comp.driver_array.get(driver_id).update_component_parameter_version();
			else
				update_component_parameter_version(0);
			break;
		case "delete":
		case "swap_select":
		case "locate":
			if(ci.parameter.comp==null)
				break;
			if(ci.parameter.comp.component_id!=comp.component_id)
				break;
			if((ci.parameter.body_id<0)||(ci.parameter.body_id>=cmc.component_marker_list.size()))
				break;
			operate_cm=cmc.component_marker_list.get(ci.parameter.body_id);
			if((operate_comp=sk.component_cont.get_component(operate_cm.marker_component_id))==null)
				break;	
			switch(str){
			case "delete":
				cmc.component_marker_list.remove(ci.parameter.body_id);
				if(cmc.global_private_flag)
					comp.driver_array.get(driver_id).update_component_parameter_version();
				else
					update_component_parameter_version(0);
				break;
			case "swap_select":
				new component_selection(sk).switch_selected_flag(operate_comp,sk.component_cont);
				break;
			case "locate":
				new locate_camera(sk.camera_cont.get(ci.display_camera_result.target.camera_id)).
						locate_on_components(sk.modifier_cont[modifier_container_id],
							new box(operate_comp.absolute_location.multiply(
									operate_cm.marker_x,operate_cm.marker_y,operate_cm.marker_z)),
							null,-1.0,true,false,false);
				break;
			}
			break;
		case "add":
		case "append":
			if(cmc.pickup_flag)
				break;
			if((marker_text=ci.request_response.get_parameter("value"))==null)
				break;
			String request_charset=ci.request_response.implementor.get_request_charset();
			try{
				marker_text=java.net.URLDecoder.decode(marker_text,request_charset);
				marker_text=java.net.URLDecoder.decode(marker_text,request_charset);
			}catch(Exception e){
				break;
			}
			point operated_point=null;
			operate_comp=null;
			switch(str){
			case "append":
				if((operated_point=ci.display_camera_result.caculate_local_focus_point(ci.parameter))!=null)
					operate_comp=ci.parameter.comp;
				break;
			case "add":
				operated_point=new point(
					((str=ci.request_response.get_parameter("x"))==null)?0:Double.parseDouble(str),
					((str=ci.request_response.get_parameter("y"))==null)?0:Double.parseDouble(str),
					((str=ci.request_response.get_parameter("z"))==null)?0:Double.parseDouble(str));
				if((str=ci.request_response.get_parameter("component_id"))!=null)
					if((operate_comp=sk.component_cont.get_component(Integer.decode(str)))!=null)
						break;
				if((str=ci.request_response.get_parameter("component_name"))!=null){
					try {
						str=java.net.URLDecoder.decode(str,request_charset);
						str=java.net.URLDecoder.decode(str,request_charset);
					}catch(Exception e) {
						break;
					}
					operate_comp=sk.component_cont.search_component(str);
				}
				break;
			}
			if((operate_comp==null)||(operated_point==null)||(marker_text.length()<=0)) {
				ci.request_response.println("-1");
				break;
			}
			if(operate_comp.component_id==comp.component_id) {
				ci.request_response.println("-1");
				break;
			}
			if(!(operate_comp.uniparameter.part_list_flag)) {
				ci.request_response.println("-1");
				break;
			}
			ci.request_response.println(
				cmc.append_component_marker(sk,operate_comp,
					marker_text,operated_point.x,operated_point.y,operated_point.z));
			if(cmc.global_private_flag)
				comp.driver_array.get(driver_id).update_component_parameter_version();
			else
				update_component_parameter_version(0);
			break;
		}
		return null;
	}
}
