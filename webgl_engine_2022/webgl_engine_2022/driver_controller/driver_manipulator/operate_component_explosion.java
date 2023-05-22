package driver_manipulator;

import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_array;
import kernel_component.component_collector;
import kernel_driver.location_modifier;
import kernel_driver.modifier_container_timer;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_transformation.plane;
import kernel_transformation.point;

public class operate_component_explosion 
{
	private static location caculate_location(component comp,point center_point,
			double t,double direction_x,double direction_y,double direction_z)
	{
		box my_box;
		point my_center_point;
		if((my_box=comp.get_component_box(true))!=null)
			my_center_point=my_box.center();
		else
			my_center_point=comp.absolute_location.multiply(new point(0,0,0));
		
		if((Math.abs(direction_x)+Math.abs(direction_y)+Math.abs(direction_z))>const_value.min_value){
			point pp=my_center_point.add(new point(direction_x,direction_y,direction_z));
			center_point=new plane(my_center_point,pp).project_to_plane_location().multiply(center_point);
		}
		
		location negative_loca=comp.caculate_negative_absolute_location();
		center_point=negative_loca.multiply(center_point);
		my_center_point=negative_loca.multiply(my_center_point);
		
		point direction=my_center_point.sub(center_point).scale(t);
		
		return comp.move_location.multiply(
				location.move_rotate(direction.x,direction.y,direction.z,0,0,0));
	}
	public static void do_explosion(int modifier_container_id,
			long touch_time_length,engine_kernel ek,client_information ci)
	{
		String str;
		component_array comp_array=new component_array(ek.component_cont.root_component.component_id+1);
		
		for(component my_comp;;){
			if((str=ci.request_response.get_parameter("component"))!=null) {
				if(str.length()>0){
					String request_charset=ci.request_response.implementor.get_request_charset();
					try {
						str=java.net.URLDecoder.decode(str,request_charset);
						str=java.net.URLDecoder.decode(str,request_charset);
					}catch(Exception e) {
						;
					}
					if((my_comp=ek.component_cont.search_component(str))!=null) {
						comp_array.add_component(my_comp);
						if(comp_array.comp_list.size()>0)
							break;
					}
				}
				return;
			}
			if((str=ci.request_response.get_parameter("component_id"))!=null) {
				if(str.length()>0)
					if((my_comp=ek.component_cont.get_component(Integer.decode(str)))!=null) {
						comp_array.add_component(my_comp);
						if(comp_array.comp_list.size()>0)
							break;
					}
				return;
			}
			if((str=ci.request_response.get_parameter("list"))!=null) {
				component_collector cc[];
				if((cc=ek.collector_stack.get_all_collector())!=null)
					try{
						int list_id=cc.length-Integer.decode(str);
						if((list_id>=0)&&(list_id<cc.length)){
							comp_array.add_collector(cc[list_id]);
							if(comp_array.comp_list.size()>0)
								break;
						}
					}catch(Exception e) {
						;
					}
				return;
			}
			comp_array.add_selected_component(ek.component_cont.root_component,false);
			if(comp_array.comp_list.size()<=0)
				comp_array.add_component(ek.component_cont.root_component);	
			if(comp_array.comp_list.size()<=0)
				return;
			break;
		}

		comp_array.make_to_children();
		comp_array.remove_not_in_part_list_component(true);
		if(comp_array.comp_list.size()<=0)
			return;
		
		box my_box=comp_array.get_box();
		if(my_box==null)
			return;
		point center_point=my_box.center();
		if(my_box.distance2()<const_value.min_value)
			return;

		boolean reset_flag=false;
		if((str=ci.request_response.get_parameter("explosion"))!=null)
			if(str.toLowerCase().compareTo("reset")==0)
				reset_flag=true;
		
		ek.component_cont.root_component.reset_component(ek.component_cont);
		
		double t=0,direction_x=0,direction_y=0,direction_z=0;
		try{
			if((str=ci.request_response.get_parameter("t"))!=null)
				t=Double.parseDouble(str);
			if((str=ci.request_response.get_parameter("x"))!=null)
				direction_x=Double.parseDouble(str);
			if((str=ci.request_response.get_parameter("y"))!=null)
				direction_y=Double.parseDouble(str);
			if((str=ci.request_response.get_parameter("z"))!=null)
				direction_z=Double.parseDouble(str);
		}catch(Exception e){
			return;
		}
		
		for(int i=0,ni=comp_array.comp_list.size();i<ni;i++) {
			modifier_container_timer timer=ek.modifier_cont[modifier_container_id].get_timer();
			long start_time=timer.get_current_time();
			long terminate_time=ci.display_camera_result.cam.parameter.switch_time_length+start_time;
			
			component my_comp=comp_array.comp_list.get(i);
			location new_move_location;
			if(reset_flag)
				new_move_location=new location();
			else
				new_move_location=caculate_location(my_comp,
						center_point,t,direction_x,direction_y,direction_z);
			
			location_modifier lm=new location_modifier(my_comp,start_time,
					my_comp.move_location,terminate_time,new_move_location,true,true);
			lm.touch_time_length=touch_time_length;
			ek.modifier_cont[modifier_container_id].add_modifier(lm);
		}
	}
}
