package driver_mouse_modify_location;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_transformation.point;
import kernel_camera.locate_camera;
import kernel_transformation.location;
import kernel_driver.location_modifier;
import kernel_common_class.const_value;
import kernel_component.component_array;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private double view_range[],low_precision_scale,mouse_rotate_scale;
	private boolean rotate_type_flag,exchange_point_flag,change_type_flag;
	private int modifier_container_id;

	public void destroy()
	{
		super.destroy();
		view_range=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,double my_view_range[],
			double my_low_precision_scale,double my_mouse_rotate_scale,boolean my_rotate_type_flag,
			boolean my_exchange_point_flag,boolean my_change_type_flag,int my_modifier_container_id)
	{
		super(my_comp,my_driver_id);

		view_range				=my_view_range;
		low_precision_scale		=my_low_precision_scale;
		mouse_rotate_scale		=my_mouse_rotate_scale;
		rotate_type_flag		=my_rotate_type_flag;
		exchange_point_flag		=my_exchange_point_flag;
		change_type_flag		=my_change_type_flag;
		modifier_container_id=my_modifier_container_id;
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		if(ci.display_camera_result==null)
			return true;
		if(ci.display_camera_result.target.target_id!=cr.target.target_id)
			return true;
		if(!(cr.target.main_display_target_flag))
			return true;
		if(ci.display_camera_result.cam.parameter.change_type_flag^change_type_flag) {
			change_type_flag=change_type_flag?false:true;
			update_component_parameter_version(0);
		}
		return false;
	}
	public void create_render_parameter(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[",comp.component_id			);
		ci.request_response.print(",",low_precision_scale		);
		ci.request_response.print(",",mouse_rotate_scale		);
		ci.request_response.print(",",rotate_type_flag?1:0		);
		ci.request_response.print(",",change_type_flag?1:0		);
		ci.request_response.print(",",exchange_point_flag?1:0	);
		ci.request_response.print("]"							);
	}
	private void get_data(boolean update_flag,
			component operate_component,engine_kernel ek,client_information ci)
	{
		String str;
		boolean display_flag=false;
		
		if((str=ci.request_response.get_parameter("distance"))!=null){
			double old_value=ci.display_camera_result.cam.parameter.distance;
			double new_value=Double.parseDouble(str);
			
			if(Math.abs(new_value-old_value)>const_value.min_value){
				ci.display_camera_result.cam.parameter.distance=new_value;
				display_flag=true;
			}
		}
		if((str=ci.request_response.get_parameter("half_fovy_tanl"))!=null){
			double old_value=ci.display_camera_result.cam.parameter.half_fovy_tanl;
			double new_value=Double.parseDouble(str);
			if(Math.abs(new_value-old_value)>const_value.min_value){
				ci.display_camera_result.cam.parameter.half_fovy_tanl=new_value;
				display_flag=true;
			}
		}
		if(operate_component!=null)
			if(operate_component.component_id!=ek.component_cont.root_component.component_id)
				if((str=ci.request_response.get_parameter("data"))!=null){
					str+=",";
					double data[]=new double[16];
					for(int i=0,j=0,k=0,ni=data.length;i<ni;i++,j=k){
						for(k=j;;k++){
							if(k>=str.length())
								return;
							if(str.charAt(k)==',')
								break;
						}
						data[i]=Double.parseDouble(str.substring(j,k++));
					}
					operate_component.set_component_move_location((new location(data)).normalize(),ek.component_cont);
					ci.render_buffer.location_buffer.synchronize_location_version(operate_component,ek,update_flag);
					
					display_flag=true;
				}
		if(ci.clip_plane!=null)
			if((str=ci.request_response.get_parameter("clip_plane_modification"))!=null)
				ci.clip_plane.D+=Double.parseDouble(str);
		
		if(display_flag)
			if(ci.display_camera_result!=null)
				if(ci.display_camera_result.target!=null)
					ci.render_buffer.cam_buffer.synchronize_camera_buffer(
						ek.camera_cont,ci.display_camera_result.target.camera_id);
	}
	private void reset_component_location(component comp,long start_time,engine_kernel ek,client_information ci)
	{
		for(int i=0,ni=comp.children_number();i<ni;i++)
			reset_component_location(comp.children[i],start_time,ek,ci);
		
		if(comp.move_location.is_not_identity_matrix())
			ek.modifier_cont[modifier_container_id].add_modifier(
				new location_modifier(comp,start_time,comp.move_location,
					ci.display_camera_result.cam.parameter.switch_time_length+start_time,new location(),true,true));
	}
	private static void add_in_list_component(component comp,component_array comp_array)
	{
		int child_number;
		if((child_number=comp.children_number())<=0) {
			if(comp.uniparameter.part_list_flag)
				comp_array.add_component(comp);
		}else {
			for(int i=0;i<child_number;i++)
				add_in_list_component(comp.children[i],comp_array);
		}
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str;
		if(ci.display_camera_result==null)
			return null;
		if((str=ci.request_response.get_parameter("operate_component_id"))==null)
			return null;		
		component operate_component=ek.component_cont.get_component(Integer.decode(str));
		if((str=ci.request_response.get_parameter("event_operation"))==null)
			return null;
		int camera_component_id=ci.display_camera_result.cam.eye_component.component_id;

		switch(str){
		case "mousedown":
			if(operate_component!=null)
				if(operate_component.component_id==camera_component_id)
					ci.display_camera_result.cam.mark_restore_stack();
			int select_component_id=-1;
			if(ci.parameter.comp!=null)
				select_component_id=ci.parameter.comp.component_id;
			if(select_component_id<0)
				if((operate_component=ek.component_cont.search_component())!=null)
					if(operate_component.component_id!=ek.component_cont.root_component.component_id)
						if(operate_component.component_id!=camera_component_id) 
							select_component_id=operate_component.component_id;
			ci.request_response.print(select_component_id);
			return null;
		case "mouseup":
			get_data(true,operate_component,ek,ci);
			if(operate_component!=null){
				operate_component.uniparameter.do_response_location_flag=true;
				operate_component.set_component_move_location(operate_component.move_location,ek.component_cont);
				if(operate_component.component_id==camera_component_id)
					ci.display_camera_result.cam.push_restore_stack(
						ek.modifier_cont[modifier_container_id],false,
						ci.display_camera_result.cam.eye_component.move_location,
						ci.display_camera_result.cam.parameter);
			}
			return null;
		case "mousemove":
			get_data(false,operate_component,ek,ci);
			return null;
		case "touchend":
		case "dblclick_view_no_pickup":
			ci.parameter.comp=null;
		case "dblclick_view":
			if(str.compareTo("touchend")==0){
				if(ci.parameter.comp==null)
					return null;
				if((ci.parameter.x<view_range[0])||(ci.parameter.x>view_range[1]))
					return null;
				if((ci.parameter.y<view_range[2])||(ci.parameter.y>view_range[3]))
					return null;
			}
			point p0=null,p1=null;
			if(ci.parameter.comp!=null){
				double local_xy[]=ci.display_camera_result.target.target_view.
							caculate_view_local_xy(ci.parameter.x,ci.parameter.y);
				location loca=ci.display_camera_result.negative_matrix;
				loca=ci.parameter.comp.caculate_negative_absolute_location().multiply(loca);
				p0=loca.multiply(new point(local_xy[0],local_xy[1],ci.parameter.depth));
				p1=loca.multiply(new point(local_xy[0],local_xy[1],ci.parameter.depth+1.0));
			}
			(new locate_camera(ci.display_camera_result.cam)).locate_on_components(
					ek.modifier_cont[modifier_container_id],ek.component_cont,ci.display_camera_result,
					ci.parameter,null,ci.display_camera_result.cam.parameter.scale_value,
					ek.modifier_cont[modifier_container_id].get_timer().get_current_time(),
					true,true,true,p0,p1);
			return null;
		case "dblclick_origin_no_pickup":
			ci.parameter.comp=null;
		case "dblclick_origin":
			(new locate_camera(ci.display_camera_result.cam)).locate_on_origin(
					ek.modifier_cont[modifier_container_id],ek.component_cont,
					ci.parameter,true,true,true);
			return null;
		case "dblclick_component":
			component_array c_a=new component_array();
			
			if((str=ci.request_response.get_parameter("priority"))==null)
				str="pickup";
			if(str.toLowerCase().compareTo("pickup")==0)
				if(ci.parameter.comp!=null)
					c_a.add_component(ci.parameter.comp);
			if(c_a.comp_list.size()<=0) 
				if(operate_component!=null)
					if(operate_component.component_id!=ek.component_cont.root_component.component_id)
						if(operate_component.component_id!=camera_component_id) 
							c_a.add_component(operate_component);
			if(c_a.comp_list.size()<=0)
				c_a.add_selected_component(ek.component_cont.root_component,false);
			if(c_a.comp_list.size()<=0)
				add_in_list_component(ek.component_cont.root_component,c_a);
			
			c_a.make_to_ancestor(ek.component_cont);
			long start_time=ek.modifier_cont[modifier_container_id].get_timer().get_current_time();
			for(int i=0,ni=c_a.comp_list.size();i<ni;i++)
				reset_component_location(c_a.comp_list.get(i),start_time,ek,ci);
			return null;
		default:
			return null;
		}
	}
}
