package driver_camera_operation;

import kernel_part.face;
import kernel_part.part_rude;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_transformation.box;
import kernel_transformation.point;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_camera.locate_camera;
import kernel_driver.component_driver;
import kernel_transformation.location;
import kernel_common_class.const_value;
import kernel_component.component_array;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private boolean show_flag;
	private int modifier_container_id;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,int my_modifier_container_id)
	{
		super(my_comp,my_driver_id);
		modifier_container_id=my_modifier_container_id;
		show_flag=true;
		display_parameter.body_title="��";
		display_parameter.face_title="��";
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		return show_flag?false:true;
	}
	public void create_render_parameter(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print(0);
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
	private location get_component_location(scene_kernel sk,client_information ci)
	{
		String str;
		component comp;
		if((str=ci.request_response.get_parameter("coordinate"))!=null){
			if(str.compareTo("camera")==0){
				if(ci.display_camera_result!=null)
					if((comp=ci.display_camera_result.cam.eye_component)!=null)
						return comp.absolute_location;
			}else if(str.compareTo("selection")==0){
				if((comp=sk.component_cont.search_component())!=null)
					if(comp.uniparameter.selected_flag)
						return comp.absolute_location;
			}
		}
		return null;
	}
	private void body_face_direct_rotate(
			boolean direct_rotate_flag,scene_kernel sk,client_information ci)
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
		component_driver c_d=comp.driver_array.get(driver_id);
		if(c_d.component_part==null)
			return;
		part_rude part_mesh=c_d.component_part.part_mesh;
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
		
		location loca=get_component_location(sk,ci);
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
			sk.modifier_cont[modifier_container_id],true,
			loca,ci.display_camera_result.cam.parameter);
	}
	private void camera_direct(scene_kernel sk,client_information ci)
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
		location loca=get_component_location(sk,ci);
		loca=lc.direction_locate(new point(x,y,z),loca,get_client_type_flag(ci));
		ci.display_camera_result.cam.mark_restore_stack();
		ci.display_camera_result.cam.push_restore_stack(
			sk.modifier_cont[modifier_container_id],true,
			loca,ci.display_camera_result.cam.parameter);
	}
	private void camera_rotate(scene_kernel sk,client_information ci)
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
		location loca=get_component_location(sk,ci);
		loca=lc.rotation_locate(new point(x0,y0,z0),new point(x1,y1,z1),loca);
		
		ci.display_camera_result.cam.mark_restore_stack();
		ci.display_camera_result.cam.push_restore_stack(
			sk.modifier_cont[modifier_container_id],true,
			loca,ci.display_camera_result.cam.parameter);
	}
	private void locate(scene_kernel sk,client_information ci)
	{
		String str;
		locate_camera lc=new locate_camera(ci.display_camera_result.cam);
		double scale_value=-1.0;
		if((str=ci.request_response.get_parameter("scale"))!=null)
			if((scale_value=Double.parseDouble(str))<const_value.min_value)
				scale_value=-1.0;
		component locate_comp=null;
		if((str=ci.request_response.get_parameter("component_name"))!=null) {
			String request_charset=ci.request_response.implementor.get_request_charset();
			try {
				str=java.net.URLDecoder.decode(str,request_charset);
				str=java.net.URLDecoder.decode(str,request_charset);
			}catch(Exception e) {
				str=null;
			}
			if(str!=null)
				locate_comp=sk.component_cont.search_component(str);
		}
		if((str=ci.request_response.get_parameter("component_id"))!=null)
			locate_comp=sk.component_cont.get_component(Integer.decode(str));
		
		if(locate_comp!=null){
			boolean mandatory_movement_flag=true,mandatory_scale_flag=true;
			if((str=ci.request_response.get_parameter("mandatory_movement"))!=null)
				switch(str.toLowerCase()) {
				case "no":
				case "false":
					mandatory_movement_flag=false;
					break;
				}
			if((str=ci.request_response.get_parameter("mandatory_scale"))!=null)
				switch(str.toLowerCase()) {
				case "no":
				case "false":
					mandatory_scale_flag=false;
					break;
				}
			box my_box,body_box,face_box;
			while(true) {
				if((my_box=locate_comp.get_component_box(false))==null)
					if((my_box=locate_comp.get_component_box(true))==null){
						point pp=locate_comp.absolute_location.multiply(new point(0,0,0));
						my_box=new box(pp,pp);
						break;
					}
				if(my_box.distance2()<const_value.min_value2) {
					my_box=null;
					break;
				}
				if(locate_comp.driver_number()<=0)
					break;
				component_driver c_d=locate_comp.driver_array.get(0);
				if(c_d.component_part==null)
					break;
				part_rude pr;
				if((pr=c_d.component_part.part_mesh)==null)
					break;
				if((str=ci.request_response.get_parameter("body_id"))==null)
					break;
				int body_id=-1,face_id=-1;
				try{
					body_id=Integer.parseInt(str);
					if((str=ci.request_response.get_parameter("face_id"))!=null)
						face_id=Integer.parseInt(str);
				}catch(Exception e){
					break;
				}
				if((body_id<0)||(body_id>=pr.body_number()))
					break;
				if((face_id>=0)&&(face_id<pr.body_array[body_id].face_number()))
					if((face_box=pr.body_array[body_id].face_array[face_id].face_box)!=null){
						my_box=locate_comp.absolute_location.multiply(face_box);
						if(my_box.distance2()<const_value.min_value2) {
							my_box=null;
							break;
						}
						break;
					}
				if((body_box=pr.body_array[body_id].body_box)!=null) {
					my_box=locate_comp.absolute_location.multiply(body_box);
					if(my_box.distance2()<const_value.min_value2) {
						my_box=null;
						break;
					}
				}
				break;
			}
			lc.locate_on_components(sk.modifier_cont[modifier_container_id],
				my_box,null,scale_value,true,mandatory_movement_flag,mandatory_scale_flag);
			return;
		}
		
		point p0=null,p1=null;
		
		if((str=ci.request_response.get_parameter("clear_pickup"))!=null)
			switch(str.toLowerCase()) {
			case "yes":
			case "true":
				ci.parameter.comp=null;
				break;
			}
		if(ci.parameter.comp!=null){
			location loca=ci.display_camera_result.negative_matrix;
			loca=ci.parameter.comp.caculate_negative_absolute_location().multiply(loca);
			double local_xy[]=ci.display_camera_result.target.target_view.caculate_view_local_xy(ci.parameter.x,ci.parameter.y);
			p0=loca.multiply(new point(local_xy[0],local_xy[1],ci.parameter.depth));
			p1=loca.multiply(new point(local_xy[0],local_xy[1],ci.parameter.depth+1.0));
		}
		lc.locate_on_components(sk.modifier_cont[modifier_container_id],
				sk.component_cont,ci.display_camera_result,ci.parameter,null,scale_value,
				sk.modifier_cont[modifier_container_id].get_timer().get_current_time(),true,true,true,p0,p1);
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		String str;
		double scale_value;
		
		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;
		switch(str){
		case "body_face_direct":	
			body_face_direct_rotate(true,sk,ci);
			return null;
		case "body_face_rotate":	
			body_face_direct_rotate(false,sk,ci);
			return null;
		case "direct":	
			camera_direct(sk,ci);
			return null;
		case "rotate":
			camera_rotate(sk,ci);
			return null;
		case "locate":
			locate(sk,ci);
			return null;
		case "retreat":
			ci.display_camera_result.cam.pop_restore_stack(sk.modifier_cont[modifier_container_id]);
			return null;
		case "project":
			if((str=ci.request_response.get_parameter("project"))!=null)
				ci.display_camera_result.cam.parameter.projection_type_flag=(str.compareTo("frustum")==0)?true:false;
			return null;
		case "view_move":
			sk.modifier_cont[modifier_container_id].process(sk, ci,true);
			if((str=ci.request_response.get_parameter("value"))!=null)
				ci.display_camera_result.cam.parameter.movement_flag=(str.compareTo("true")==0)?true:false;
			return null;
		case "view_direct":
			sk.modifier_cont[modifier_container_id].process(sk, ci,true);
			if((str=ci.request_response.get_parameter("value"))!=null)
				ci.display_camera_result.cam.parameter.direction_flag=(str.compareTo("true")==0)?true:false;
			return null;
		case "view_scale":
			sk.modifier_cont[modifier_container_id].process(sk, ci,true);
			if((str=ci.request_response.get_parameter("value"))!=null){
				scale_value=Math.abs(ci.display_camera_result.cam.parameter.scale_value);
				scale_value=(str.compareTo("true")==0)?scale_value:(-scale_value);
				ci.display_camera_result.cam.parameter.scale_value=scale_value;
			}
			return null;
		case "change_type":
		{
			if(sk.component_cont.root_component==null)
				return null;
			sk.modifier_cont[modifier_container_id].process(sk, ci,true);
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
			
			box my_box;
			component_array effective_comp_container=new component_array();
			effective_comp_container.add_part_list_component(sk.component_cont.root_component);
			if((my_box=effective_comp_container.get_box())==null){
				effective_comp_container.clear_compoment();
				effective_comp_container.add_component(sk.component_cont.root_component);
				if((my_box=effective_comp_container.get_box())==null)
					if((my_box=sk.component_cont.root_component.get_component_box(false))==null)
						my_box=sk.component_cont.root_component.get_component_box(true);
			}
			if(my_box!=null){
				ci.display_camera_result.cam.parameter.distance=my_box.distance();
				ci.display_camera_result.cam.parameter.half_fovy_tanl
					=screen_distance/ci.display_camera_result.cam.parameter.distance;
				ci.display_camera_result.cam.parameter.change_type_flag=false;
			}
			return null;
		}
		case "show_hide":
			update_component_parameter_version(0);
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
