package driver_component_marker;

import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_camera.locate_camera;
import kernel_transformation.point;
import kernel_driver.component_driver;
import kernel_common_class.jason_string;
import kernel_component.component_selection;
import kernel_driver.component_instance_driver;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class extended_component_instance_driver extends component_instance_driver
{
	private component_marker_container cmc;

	private int modifier_container_id;
	private boolean display_flag[];
	
	public void destroy()
	{
		super.destroy();
		cmc=null;
		display_flag=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,
			component_marker_container my_cmc,int my_modifier_container_id)
	{
		super(my_comp,my_driver_id);

		cmc=my_cmc;
		modifier_container_id=my_modifier_container_id;
		display_flag=new boolean[] {};
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
		for(int i=0,ni=cmc.component_marker_array.length;i<ni;i++)
			ci.render_buffer.location_buffer.put_in_list(
				sk.component_cont.get_component(cmc.component_marker_array[i].marker_component_id),sk);
	}
	public boolean check(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		if(cmc.pickup_flag){
			if(!(cr.target.main_display_target_flag)) 
				return true;
			if(ci.parameter.comp==null)
				return true;
			if(ci.parameter.comp.driver_number()<=0)
				return true;
			component_driver c_d=ci.parameter.comp.driver_array.get(0);
			if(c_d.component_part==null)
				return true;
			point p;
			if((p=ci.display_camera_result.caculate_local_focus_point(ci.parameter))==null)
				return true;
			String marker_text=c_d.component_part.user_name;
			cmc.component_marker_array=new component_marker[]{
					new component_marker(ci.parameter.comp,marker_text,p.x,p.y,p.z)};
			display_flag=new boolean[]{true};
			update_component_parameter_version(0);
			return false;
		}
		if(cmc.component_marker_array.length<=0)
			return true;
		if(cr.target.main_display_target_flag) {
			if(cmc.component_marker_array.length!=display_flag.length) {
				display_flag=new boolean[cmc.component_marker_array.length];
				for(int i=0,ni=display_flag.length;i<ni;i++)
					display_flag[i]=true;
				update_component_parameter_version(0);
			}
			for(int i=0,ni=cmc.component_marker_array.length;i<ni;i++){
				component my_comp=sk.component_cont.get_component(
						cmc.component_marker_array[i].marker_component_id);
				if(my_comp==null) {
					if(display_flag[i])
						update_component_parameter_version(0);
					display_flag[i]=false;
				}else{
					boolean new_display_flag=my_comp.get_effective_display_flag(cr.target.parameter_channel_id);
					if(display_flag[i]^new_display_flag) {
						display_flag[i]=new_display_flag;
						update_component_parameter_version(0);
					}
				}
			}
		}
		return false;
	}
	public void create_render_parameter(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print("[");
		String pre_str="";
		int n1=cmc.component_marker_array.length;
		int n2=display_flag.length;
		for(int i=0,ni=(n1<n2)?n1:n2;i<ni;i++)
			if(display_flag[i]){
				component_marker p=cmc.component_marker_array[i];
				component my_comp=sk.component_cont.get_component(p.marker_component_id);
				ci.request_response.print(pre_str).
					print("[",my_comp.component_id).
					print(",",p.marker_x).
					print(",",p.marker_y).
					print(",",p.marker_z).
					print(",",jason_string.change_string(p.marker_text.trim())).
					print(cmc.pickup_flag?",true]":",false]");
				pre_str=",";
			}
		ci.request_response.print("]");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		component operate_comp;
		component_marker operate_cm;

		String str,marker_text;
		switch(((str=ci.request_response.get_parameter("operation"))==null)?"":str) {
		default:
			break;
		case "jason":
			ci.request_response.println("[");
			for(int i=0,ni=cmc.component_marker_array.length;i<ni;i++) {
				component_marker p=cmc.component_marker_array[i];
				operate_comp=sk.component_cont.get_component(p.marker_component_id);
				String marker_component_name=(operate_comp==null)?"":(operate_comp.component_name);
				ci.request_response.println("	{");
				ci.request_response.print  ("		\"marker_id\":		",		p.marker_id).println(",");
				str=jason_string.change_string(marker_component_name);
				ci.request_response.print  ("		\"component_name\":	",		str).println(",");
				ci.request_response.print  ("		\"component_id\":		",	p.marker_component_id).println(",");
				ci.request_response.print  ("		\"marker_x\":		",		p.marker_x).println(",");
				ci.request_response.print  ("		\"marker_y\":		",		p.marker_y).println(",");
				ci.request_response.print  ("		\"marker_z\":		",		p.marker_z).println(",");
				str=jason_string.change_string(p.marker_text);
				ci.request_response.println("		\"marker_text\":		",	str);
				ci.request_response.println("	}",(i<(ni-1))?",":"");
			}
			ci.request_response.println("]");
			break;
		case "clear_all":
			cmc.clear_all_component_marker(sk);
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
			if((ci.parameter.body_id<0)||(ci.parameter.body_id>=cmc.component_marker_array.length))
				break;
			operate_cm=cmc.component_marker_array[ci.parameter.body_id];
			if((operate_comp=sk.component_cont.get_component(operate_cm.marker_component_id))==null)
				break;	
			switch(str){
			case "delete":
				cmc.delete_component_marker(ci.parameter.body_id,sk);
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
			if(cmc.pickup_flag){
				cmc.component_marker_array=new component_marker[]{new component_marker(comp,"pickup",0,0,0)};
				update_component_parameter_version(0);
				break;
			}
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
