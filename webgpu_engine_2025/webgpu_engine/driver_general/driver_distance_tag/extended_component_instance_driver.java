package driver_distance_tag;

import java.util.ArrayList;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_transformation.point;
import kernel_scene.client_information;
import kernel_component.component_container;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private distance_tag_list tag_list;
	private boolean front_show_flag;
	private ArrayList<boolean[]>visible_flag_list;

	public void destroy()
	{
		super.destroy();
		
		if(tag_list!=null) {
			tag_list.destroy();
			tag_list=null;
		}
		visible_flag_list=null;
	}
	public extended_component_instance_driver(
			component my_comp,int my_driver_id,distance_tag_list my_tag_array)
	{
		super(my_comp,my_driver_id);

		tag_list=my_tag_array;
		front_show_flag=false;
		visible_flag_list=new ArrayList<boolean[]>();
	}
	public void response_initialization_component_data(scene_kernel sk,client_information ci)
	{
	}
	private boolean test_not_visible(component comp,
			int parameter_channel_id,component_container component_cont)
	{
		while(comp!=null)
			if(comp.multiparameter[parameter_channel_id].effective_display_flag)
				comp=component_cont.get_component(comp.parent_component_id);
			else
				return true;
		return false;
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		if(cr.target.main_display_target_flag)
			if(tag_list.test_location_modify(sk,ci))
				comp.driver_array.get(driver_id).update_component_parameter_version();
		for(int index_id;(index_id=visible_flag_list.size())<=cr.target.target_id;)
			visible_flag_list.add(index_id,new boolean[0]);
		
		int display_number=0,number=tag_list.distance_tag_list.size();
		boolean old_visible_flag[]=visible_flag_list.get(cr.target.target_id);
		boolean new_visible_flag[]=new boolean[number];
		for(int i=0;i<number;i++){
			component distance_comp;
			new_visible_flag[i]=false;
			distance_tag_item p=tag_list.distance_tag_list.get(i);
			if((distance_comp=sk.component_cont.get_component(p.p0_component_id))==null)
				continue;
			if(test_not_visible(distance_comp,cr.target.parameter_channel_id,sk.component_cont))
				continue;
			if((distance_comp=sk.component_cont.get_component(p.px_component_id))==null)
				continue;
			if(test_not_visible(distance_comp,cr.target.parameter_channel_id,sk.component_cont))
				continue;
			switch(p.state){
			case "begin":
			case "process":
			case "end":
				new_visible_flag[i]=true;
				display_number++;
				break;
			}
		}
		
		if(old_visible_flag.length!=number) {
			visible_flag_list.set(cr.target.target_id,new_visible_flag);
			update_component_render_version(cr.target.target_id,0);
		}else {
			for(int i=0;i<number;i++)
				if(old_visible_flag[i]!=new_visible_flag[i]){
					visible_flag_list.set(cr.target.target_id,new_visible_flag);
					update_component_render_version(cr.target.target_id,0);
					break;
				}
		}	
		return (display_number<=0)?true:false;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		boolean visible_flag[]=visible_flag_list.get(cr.target.target_id);
		int number=visible_flag.length;
		if(number<=0)
			ci.request_response.print("[]");
		else {
			ci.request_response.print(visible_flag[0]?"[1":"[0");
			for(int i=1;i<number;i++)
				ci.request_response.print(visible_flag[i]?",1":",0");
			ci.request_response.print("]");
		}
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		distance_tag_item p;
		
		ci.request_response.print("[");
		for(int i=0,ni=tag_list.distance_tag_list.size();i<ni;i++) 
			switch((p=tag_list.distance_tag_list.get(i)).state){
			case "begin":
			case "process":
			case "end":
				component distance_comp=sk.component_cont.get_component(p.p0_component_id);
				point p0=distance_comp.absolute_location.multiply(p.p0);
				point dy=distance_comp.absolute_location.multiply(p.py).sub(p0);
				distance_comp=sk.component_cont.get_component(p.px_component_id);
				point dx=distance_comp.absolute_location.multiply(p.px).sub(p0);
				ci.request_response.
					print((i<=0)?"[":",[",p.get_tag_string(tag_list.display_precision,sk,ci)).
					print(",",p0.x).print(",",p0.y).print(",",p0.z).
					print(",",dx.x).print(",",dx.y).print(",",dx.z).
					print(",",dy.x).print(",",dy.y).print(",",dy.z).
					print((p.state.compareTo("end")==0)	?",1":",0").
					print(front_show_flag				?",1]":",0]");
				break;
			default:
				ci.request_response.print((i<=0)?"[]":",[]");
				break;
			}
		ci.request_response.print("]");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		String str=ci.request_response.get_parameter("operation");
		
		switch((str==null)?"":str) {
		default:
			break;
		case "front_show":
			front_show_flag=ci.request_response.get_boolean("front_show",false);
			update_component_parameter_version(0);
			return null;
		case "mark":
			if(tag_list.mark_distance_tag(sk,ci))
				return null;
			tag_list.save(sk);
			break;
		case "touch":
			if(tag_list.touch_distance_tag(sk,ci))
				return null;
			break;
		case "extra":
			tag_list.set_extra_distance_tag(sk,ci);
			break;
		case "type":
			if(tag_list.set_distance_tag_type(sk,ci))
				return null;
			tag_list.save(sk);
			break;
		case "title":
			if(tag_list.title_distance_tag(sk,ci))
				return null;
			tag_list.save(sk);
			break;
		case "clear":
			if(tag_list.clear_distance_tag(sk,ci))
				return null;
			tag_list.save(sk);
			break;
		case "clear_all":
			tag_list.clear_all_distance_tag(sk,ci);
			tag_list.save(sk);
			break;
		case "modify":
			if(tag_list.modify_distance_tag(sk,ci))
				return null;
			break;
		case "swap_component":
			tag_list.swap_tag_component_selection(sk,ci);
			break;
		case "locate_component":
			tag_list.locate_tag_component(sk,ci);
			break;
		case "save":
			tag_list.save(sk);
			return null;
		case "load":
			tag_list.load(sk);
			break;
		case "jason":
			tag_list.jason(sk,ci);
			return null;
		}
		comp.driver_array.get(driver_id).update_component_parameter_version();
		return null;
	}
}
