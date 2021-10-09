package driver_camera_operation;

import kernel_camera.camera;
import kernel_camera.camera_result;
import kernel_camera.locate_camera;
import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_array;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_transformation.point;
import kernel_part.face;
import kernel_part.part_rude;


public class extended_instance_driver extends instance_driver
{
	private boolean show_flag;
	private int camera_modifier_id;
	private double left_down_x,left_down_y,right_up_x,right_up_y;
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id,int my_camera_modifier_id)
	{
		super(my_comp,my_driver_id);
		show_flag=true;
		camera_modifier_id=my_camera_modifier_id;
		left_down_x=0;
		left_down_y=0;
		right_up_x=0;
		right_up_y=0;
		display_parameter.body_title="Ìו";
		display_parameter.face_title="Ãז";
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if(!show_flag)
			return true;
		if(!(cr.target.selection_target_flag))
			if(cr.target.framebuffer_width>0)
				if(cr.target.framebuffer_height>0)
					return true;
		if(ci.display_camera_result.target.view_volume_box.p[0].x!=left_down_x) {
			left_down_x=ci.display_camera_result.target.view_volume_box.p[0].x;
			update_component_parameter_version(0);
		}
		if(ci.display_camera_result.target.view_volume_box.p[0].y!=left_down_y) {
			left_down_y=ci.display_camera_result.target.view_volume_box.p[0].y;
			update_component_parameter_version(0);
		}
		if(ci.display_camera_result.target.view_volume_box.p[1].x!=right_up_x) {
			right_up_x=ci.display_camera_result.target.view_volume_box.p[1].x;
			update_component_parameter_version(0);
		}
		if(ci.display_camera_result.target.view_volume_box.p[1].y!=right_up_y) {
			right_up_y=ci.display_camera_result.target.view_volume_box.p[1].y;
			update_component_parameter_version(0);
		}
		return false;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[",comp.component_id);
		ci.request_response.print(",",left_down_x);
		ci.request_response.print(",",left_down_y);
		ci.request_response.print(",",right_up_x);
		ci.request_response.print(",",right_up_y);
		ci.request_response.print("]");
	}
	private boolean get_client_type_flag(client_information ci)
	{
		String str=ci.request_response.get_parameter("type");
		if(str==null)
			str="false";
		switch(str.toLowerCase()){
		case "yes":
		case "true":
			return true;
		case "no":
		case "false":
			return false;
		default:
			return false;
		}
	}
	private location get_component_location(engine_kernel ek,client_information ci)
	{
		String str;
		component comp;
		if((str=ci.request_response.get_parameter("coordinate"))!=null){
			if(str.compareTo("camera")==0){
				if(ci.display_camera_result!=null)
					if((comp=ci.display_camera_result.cam.eye_component)!=null)
						return comp.absolute_location;
			}else if(str.compareTo("selection")==0){
				if((comp=ek.component_cont.search_component())!=null)
					if(comp.uniparameter.selected_flag)
						return comp.absolute_location;
			}
		}
		return null;
	}
	private void body_face_direct_rotate(
			boolean direct_rotate_flag,engine_kernel ek,client_information ci)
	{
		if(ci.parameter.comp==null)
			return;
		if(ci.parameter.body_id<0)
			return;
		if(ci.parameter.face_id<0)
			return;
		if(ci.parameter.comp.component_id!=comp.component_id)
			return;
		if(comp.driver_number()<=0)
			return;
		if(comp.driver_array[driver_id].component_part==null)
			return;
		part_rude part_mesh=comp.driver_array[driver_id].component_part.part_mesh;
		if(part_mesh==null)
			return;
		if(ci.parameter.body_id>=part_mesh.body_number())
			return;
		if(ci.parameter.face_id>=part_mesh.body_array[ci.parameter.body_id].face_number())
			return;
		face fa=part_mesh.body_array[ci.parameter.body_id].face_array[ci.parameter.face_id];

		double par[];
		if((par=fa.fa_face.face_parameter)==null)
			return;
		if(par.length<3)
			return;
		if(fa.fa_face.face_type.compareTo("plane")!=0)
			return;
		
		point p=new point(par[0],par[1],par[2]);
		if(ci.display_camera_result.to_me_direct.dot(p)<0)
			p=p.reverse();
		
		location loca=get_component_location(ek,ci);
		locate_camera lc=new locate_camera(ci.display_camera_result.cam);
		if(direct_rotate_flag)
			loca=lc.direction_locate(p,loca,get_client_type_flag(ci));
		else{
			String str;
			if((str=ci.request_response.get_parameter("alf"))==null)
				return;
			double alf=Double.parseDouble(str);
			loca=lc.rotation_locate(p,(get_client_type_flag(ci)?(-1.0):1.0)*alf,loca);
		}
		ci.display_camera_result.cam.mark_restore_stack();
		ci.display_camera_result.cam.push_restore_stack(
				ek.modifier_cont[camera_modifier_id],true,
				loca,ci.display_camera_result.cam.parameter);
	}
	private void camera_direct(engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("x"))==null)
			return;
		double x=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("y"))==null)
			return;
		double y=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("z"))==null)
			return;
		double z=Double.parseDouble(str);
		
		locate_camera lc=new locate_camera(ci.display_camera_result.cam);
		location loca=get_component_location(ek,ci);
		loca=lc.direction_locate(new point(x,y,z),loca,get_client_type_flag(ci));
		ci.display_camera_result.cam.mark_restore_stack();
		ci.display_camera_result.cam.push_restore_stack(
				ek.modifier_cont[camera_modifier_id],true,
				loca,ci.display_camera_result.cam.parameter);
	}
	
	private void camera_rotate(engine_kernel ek,client_information ci)
	{
		String str;
		
		if((str=ci.request_response.get_parameter("x0"))==null)
			return;
		double x0=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("y0"))==null)
			return;
		double y0=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("z0"))==null)
			return;
		double z0=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("x1"))==null)
			return;
		double x1=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("y1"))==null)
			return;
		double y1=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("z1"))==null)
			return;
		double z1=Double.parseDouble(str);
		
		locate_camera lc=new locate_camera(ci.display_camera_result.cam);
		location loca=get_component_location(ek,ci);
		loca=lc.rotation_locate(new point(x0,y0,z0),new point(x1,y1,z1),loca);
		
		ci.display_camera_result.cam.mark_restore_stack();
		ci.display_camera_result.cam.push_restore_stack(ek.modifier_cont[camera_modifier_id],
				true,loca,ci.display_camera_result.cam.parameter);
	}
	private String client_parameter_request(client_information ci,camera camera_array[])
	{
		String str;
		if((str=ci.request_response.get_parameter("discard_lod_value"))!=null)
			switch(str.toLowerCase()) {
			case "true":
			case "yes":
			case "on":
				ci.parameter.do_discard_lod_flag=true;
				break;
			case "false":
			case "no":
			case "off":
				ci.parameter.do_discard_lod_flag=false;
				break;
			}
		if((str=ci.request_response.get_parameter("selection_lod_value"))!=null)
			switch(str.toLowerCase()) {
			case "true":
			case "yes":
			case "on":
				ci.parameter.do_selection_lod_flag=true;
				break;
			case "false":
			case "no":
			case "off":
				ci.parameter.do_selection_lod_flag=false;
				break;
			}
		return null;
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		String str;
		double scale_value,value;

		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;
		
		switch(str){
		case "set_camera_data":
			if((str=ci.request_response.get_parameter("data"))!=null){
				double data[]=(new location()).get_location_data();
				for(int index_id,i=0,ni=data.length;(i<ni)&&(str.length()>0);i++) {
					if((index_id=str.indexOf(','))<0) {
						data[i]=Double.parseDouble(str);
						break;
					}
					data[i]=Double.parseDouble(str.substring(0,index_id));
					str=str.substring(index_id+1);
				}
				location loca=(new location(data)).normalize();
				if((str=ci.request_response.get_parameter("rotate"))!=null)
					loca=loca.multiply(location.move_rotate(0,0,0,0,0,Double.parseDouble(str)));
				ci.display_camera_result.cam.eye_component.modify_location(loca,ek.component_cont);
			}
			if((str=ci.request_response.get_parameter("distance"))!=null)
				ci.display_camera_result.cam.parameter.distance=Math.abs(Double.parseDouble(str));
			
			if((str=ci.request_response.get_parameter("modifier"))!=null) {
				int new_camera_modifier_id=Integer.decode(str);
				if(new_camera_modifier_id>=0)
					if(ek.modifier_cont.length<=new_camera_modifier_id)
						camera_modifier_id=new_camera_modifier_id;
			}
			if((str=ci.request_response.get_parameter("flag"))!=null)
				switch(str.toLowerCase().trim()){
				case "yes":
				case "true":
					ci.display_camera_result.cam.eye_component.uniparameter.cacaulate_location_flag=true;
					break;
				case "no":
				case "false":
					ci.display_camera_result.cam.eye_component.uniparameter.cacaulate_location_flag=false;
					break;
				default:
					break;
				}
		case "get_camera_data":
			ci.request_response.println("{");
			ci.request_response.print  ("	\"matrix\"	:	",
					ci.display_camera_result.cam.eye_component.move_location.get_location_data());
			ci.request_response.println(",");
			
			ci.request_response.println("	\"distance\"	:	",
					ci.display_camera_result.cam.parameter.distance);
			ci.request_response.println(",");
			
			ci.request_response.println("	\"flag\"	:	",
					ci.display_camera_result.cam.eye_component.uniparameter.cacaulate_location_flag?"true":"false");
			
			ci.request_response.println("}");
			return null;
		case "body_face_direct":	
			body_face_direct_rotate(true,ek,ci);
			return null;
		case "body_face_rotate":	
			body_face_direct_rotate(false,ek,ci);
			return null;
		case "direct":	
			camera_direct(ek,ci);
			return null;
		case "rotate":
			camera_rotate(ek,ci);
			return null;
		case "locate":
			if((str=ci.request_response.get_parameter("clear_pickup"))!=null)
				switch(str.toLowerCase()) {
				case "yes":
				case "true":
					ci.parameter.comp=null;
					break;
				}
			locate_camera lc=(new locate_camera(ci.display_camera_result.cam));
			scale_value=-1.0;
			if((str=ci.request_response.get_parameter("scale"))!=null)
				if((scale_value=Double.parseDouble(str))<const_value.min_value)
					scale_value=-1.0;
			component locate_comp=null;
			if((str=ci.request_response.get_parameter("component_name"))!=null)
				locate_comp=ek.component_cont.search_component(str);
			if((str=ci.request_response.get_parameter("component_id"))!=null)
				locate_comp=ek.component_cont.get_component(Integer.decode(str));
			if(locate_comp!=null){
				box my_box;
				if((my_box=locate_comp.get_component_box(false))==null)
					if((my_box=locate_comp.get_component_box(true))==null){
						point pp=locate_comp.absolute_location.multiply(new point(0,0,0));
						my_box=new box(pp,pp);
					}
				lc.locate_on_components(ek.modifier_cont[camera_modifier_id],
						my_box,null,scale_value,ci.parameter.aspect,true,true,true);
				locate_comp.modify_location(locate_comp.move_location,ek.component_cont);
				return null;
			}
			point p0=null,p1=null;
			if(ci.parameter.comp!=null){
				location loca=ci.selection_camera_result.negative_matrix;
				loca=ci.parameter.comp.absolute_location.negative().multiply(loca);
				p0=loca.multiply(new point(0,0,ci.parameter.depth));
				p1=loca.multiply(new point(0,0,ci.parameter.depth+1.0));
			}
			lc.locate_on_components(ek.modifier_cont[camera_modifier_id],
					ek.component_cont,ci.display_camera_result,ci.parameter,null,
					scale_value,ci.parameter.aspect,
					ek.modifier_cont[camera_modifier_id].get_timer().get_current_time(),
					true,true,true,p0,p1);
			return null;
		case "retreat":
			ci.display_camera_result.cam.pop_restore_stack(ek.modifier_cont[camera_modifier_id]);
			return null;
		case "project":
			if((str=ci.request_response.get_parameter("project"))!=null)
				ci.display_camera_result.cam.parameter.projection_type_flag=(str.compareTo("frustum")==0)?true:false;
			return null;
		case "view_move":
			ek.modifier_cont[camera_modifier_id].clear_modifier(ek, ci);
			if((str=ci.request_response.get_parameter("value"))!=null)
				ci.display_camera_result.cam.parameter.movement_flag=(str.compareTo("true")==0)?true:false;
			return null;
		case "view_direct":
			ek.modifier_cont[camera_modifier_id].clear_modifier(ek, ci);
			if((str=ci.request_response.get_parameter("value"))!=null)
				ci.display_camera_result.cam.parameter.direction_flag=(str.compareTo("true")==0)?true:false;
			return null;
		case "view_scale":
			ek.modifier_cont[camera_modifier_id].clear_modifier(ek, ci);
			if((str=ci.request_response.get_parameter("value"))!=null){
				scale_value=Math.abs(ci.display_camera_result.cam.parameter.scale_value);
				scale_value=(str.compareTo("true")==0)?scale_value:(-scale_value);
				ci.display_camera_result.cam.parameter.scale_value=scale_value;
			}
			return null;
		case "change_type":
		{	
			if(ek.component_cont.root_component==null)
				return null;

			ek.modifier_cont[camera_modifier_id].clear_modifier(ek, ci);
			if((str=ci.request_response.get_parameter("value"))==null)
				return null;
			boolean new_change_type_flag=(str.compareTo("true")==0)?true:false;
			if(!(ci.display_camera_result.cam.parameter.change_type_flag^new_change_type_flag))
				return null;
			
			double screen_distance;
			screen_distance =ci.display_camera_result.cam.parameter.distance;
			screen_distance*=ci.display_camera_result.cam.parameter.half_fovy_tanl;
			
			if(new_change_type_flag){
				ci.display_camera_result.cam.parameter.half_fovy_tanl
					=ci.display_camera_result.cam.parameter.bak_half_fovy_tanl;
				ci.display_camera_result.cam.parameter.distance
					=screen_distance/ci.display_camera_result.cam.parameter.half_fovy_tanl;
				ci.display_camera_result.cam.parameter.change_type_flag=true;
				return null;
			}
			
			box engine_box;
			component_array effective_comp_container=new component_array(
					ek.component_cont.root_component.component_id+1);
			effective_comp_container.add_part_list_component(ek.component_cont.root_component);
			if((engine_box=effective_comp_container.get_box())==null){
				effective_comp_container.clear_compoment();
				effective_comp_container.add_component(ek.component_cont.root_component);
				if((engine_box=effective_comp_container.get_box())==null)
					if((engine_box=ek.component_cont.root_component.get_component_box(false))==null)
						engine_box=ek.component_cont.root_component.get_component_box(true);
			}
			if(engine_box!=null){
				ci.display_camera_result.cam.parameter.distance=engine_box.distance();
				ci.display_camera_result.cam.parameter.half_fovy_tanl
					=screen_distance/ci.display_camera_result.cam.parameter.distance;
				ci.display_camera_result.cam.parameter.change_type_flag=false;
			}
			return null;
		}
		case "display_precision":
			if((str=ci.request_response.get_parameter("low_value"))!=null)
				if((value=Double.parseDouble(str))>=(const_value.min_value))
					ci.display_camera_result.cam.parameter.low_precision_scale=value;
			if((str=ci.request_response.get_parameter("high_value"))!=null)
				if((value=Double.parseDouble(str))>=(const_value.min_value))
					ci.display_camera_result.cam.parameter.high_precision_scale=value;
			return null;
		case "client_parameter":
			client_parameter_request(ci,ek.camera_cont.camera_array);
			return null;
		case "show_hide":
			ci.instance_container.get_driver(comp,driver_id).update_component_parameter_version(0);
			if((str=ci.request_response.get_parameter("show_hide"))!=null){
				switch(str.toLowerCase()){
				case "yes":
				case "true":
					show_flag=true;
					break;
				case "no":
				case "false":
					show_flag=false;
					break;
				default:
					break;
				}
			}
			return null;
		}
		return null;
	}
}
