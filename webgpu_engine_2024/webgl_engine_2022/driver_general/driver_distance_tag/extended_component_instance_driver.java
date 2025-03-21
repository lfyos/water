package driver_distance_tag;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_transformation.point;
import kernel_common_class.jason_string;
import kernel_driver.component_instance_driver;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class extended_component_instance_driver extends component_instance_driver
{
	private distance_tag_array tag_array;
	private boolean front_show_flag;

	public void destroy()
	{
		super.destroy();
		tag_array=null;
	}
	public extended_component_instance_driver(
			component my_comp,int my_driver_id,distance_tag_array my_tag_array)
	{
		super(my_comp,my_driver_id);
		tag_array=my_tag_array;
		front_show_flag=false;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
		ci.request_response.print(jason_string.change_string(tag_array.tag_root_menu_component_name));
	}
	public boolean check(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		if(tag_array.test_location_modify(sk,ci))
			comp.driver_array.get(driver_id).update_component_parameter_version();
		
		int display_number=0;
		for(int i=0,ni=tag_array.distance_tag_array.length;i<ni;i++){
			component distance_comp;
			distance_tag_item p=tag_array.distance_tag_array[i];
			if((distance_comp=sk.component_cont.get_component(p.p0_component_id))!=null)
				if(distance_comp.get_effective_display_flag(cr.target.parameter_channel_id))
					if((distance_comp=sk.component_cont.get_component(p.px_component_id))!=null)
						if(distance_comp.get_effective_display_flag(cr.target.parameter_channel_id))
							switch((p=tag_array.distance_tag_array[i]).state){
							case 0:
							case 1:
							case 2:
								display_number++;
								break;
							}
		}
		return (display_number<=0);
	}
	public void create_render_parameter(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		String pre_str="[";
		distance_tag_item p;
		
		ci.request_response.print("[");
		for(int i=0,ni=tag_array.distance_tag_array.length;i<ni;i++)
			switch((p=tag_array.distance_tag_array[i]).state){
			case 0:
			case 1:
			case 2:
				component distance_comp=sk.component_cont.get_component(p.p0_component_id);
				point p0=distance_comp.absolute_location.multiply(p.p0);
				point dy=distance_comp.absolute_location.multiply(p.py).sub(p0);
				distance_comp=sk.component_cont.get_component(p.px_component_id);
				point dx=distance_comp.absolute_location.multiply(p.px).sub(p0);
				ci.request_response.
					print(pre_str,p.get_tag_str(tag_array.display_precision,sk,ci)).
					print(",",p0.x).print(",",p0.y).print(",",p0.z).
					print(",",dx.x).print(",",dx.y).print(",",dx.z).
					print(",",dy.x).print(",",dy.y).print(",",dy.z).
					print((p.state==2)?",1":",0",front_show_flag?",1]":",0]");
				pre_str=",[";
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
			str=ci.request_response.get_parameter("front_show");
			switch((str==null)?"":str){
			case "true":
			case "yes":
				front_show_flag=true;
				break;
			case "false":
			case "no":
				front_show_flag=false;
				break;
			default:
				return null;
			};
			update_component_parameter_version(0);
			return null;
		case "mark":
			if(tag_array.mark_distance_tag(sk,ci))
				return null;
			tag_array.save(sk);
			break;
		case "touch":
			if(tag_array.touch_distance_tag(sk,ci))
				return null;
			break;
		case "extra":
			tag_array.set_extra_distance_tag(sk,ci);
			break;
		case "type":
			if(tag_array.set_distance_tag_type(sk,ci))
				return null;
			tag_array.save(sk);
			break;
		case "title":
			if(tag_array.title_distance_tag(sk,ci))
				return null;
			tag_array.save(sk);
			break;
		case "clear":
			if(tag_array.clear_distance_tag(sk,ci))
				return null;
			tag_array.save(sk);
			break;
		case "clear_all":
			tag_array.clear_all_distance_tag(sk,ci);
			tag_array.save(sk);
			break;
		case "modify":
			if(tag_array.modify_distance_tag(sk,ci))
				return null;
			break;
		case "swap_component":
			tag_array.swap_tag_component_selection(sk,ci);
			break;
		case "locate_component":
			tag_array.locate_tag_component(sk,ci);
			break;
		case "save":
			tag_array.save(sk);
			return null;
		case "load":
			tag_array.load(sk);
			break;
		case "jason":
			tag_array.jason(sk,ci);
			return null;
		}
		comp.driver_array.get(driver_id).update_component_parameter_version();
		return null;
	}
}
