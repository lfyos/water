package driver_distance_tag;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_transformation.point;
import kernel_scene.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private distance_tag_list tag_list;
	private boolean front_show_flag;

	public void destroy()
	{
		super.destroy();
		tag_list=null;
	}
	public extended_component_instance_driver(
			component my_comp,int my_driver_id,distance_tag_list my_tag_array)
	{
		super(my_comp,my_driver_id);

		tag_list=my_tag_array;
		front_show_flag=false;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		if(tag_list.test_location_modify(sk,ci))
			comp.driver_array.get(driver_id).update_component_parameter_version();
		
		int display_number=0;
		for(int i=0,ni=tag_list.distance_tag_list.size();i<ni;i++){
			distance_tag_item p=tag_list.distance_tag_list.get(i);
			var distance_comp=sk.component_cont.get_component(p.p0_component_id);
			if(distance_comp!=null) {
				var my_par=distance_comp.multiparameter[cr.target.parameter_channel_id];
				if(my_par.effective_display_flag)
					if((distance_comp=sk.component_cont.get_component(p.px_component_id))!=null) {
						my_par=distance_comp.multiparameter[cr.target.parameter_channel_id];
						if(my_par.effective_display_flag)
							switch(p.state){
							case "begin":
							case "process":
							case "end":
								display_number++;
								break;
							}
					}
			}
		}
		return (display_number<=0);
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		String pre_str="[";
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
					print(pre_str,p.get_tag_str(tag_list.display_precision,sk,ci)).
					print(",",p0.x).print(",",p0.y).print(",",p0.z).
					print(",",dx.x).print(",",dx.y).print(",",dx.z).
					print(",",dy.x).print(",",dy.y).print(",",dy.z).
					print((p.state.compareTo("end")==0)?",1":",0",front_show_flag?",1]":",0]");
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
