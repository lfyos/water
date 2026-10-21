package kernel_camera;

import kernel_part.part;
import kernel_transformation.box;
import kernel_component.component;
import kernel_transformation.plane;
import kernel_transformation.point;
import kernel_scene.client_parameter;
import kernel_transformation.location;
import kernel_common_class.const_value;
import kernel_driver.modifier_container;
import kernel_component.component_container;

public class locate_camera
{
	private camera cam;
	private box locate_box;
	
	private location move_location;
	public double distance,half_fovy_tanl;
	
	public locate_camera(camera my_cam)
	{
		cam				=my_cam;
		locate_box		=null;
		
		move_location	=cam.eye_component.move_location;
		distance		=cam.parameter.distance;
		half_fovy_tanl	=cam.parameter.half_fovy_tanl;

		return;
	}
	private location caculate_rotate_right_modify_location(point direction,
			point rotate_point,point start_point,point end_point,location absolute_location)
	{
		if(direction!=null)
			if(direction.distance2()>const_value.min_value2){
				plane rotate_project_plane=new plane(rotate_point,rotate_point.add(direction));
				location plane_project_location=rotate_project_plane.project_to_plane_location();
				start_point	=plane_project_location.multiply(start_point);
				end_point	=plane_project_location.multiply(end_point);
			}
		point start_direction=start_point.sub(rotate_point);
		if(start_direction.distance2()<const_value.min_value2)
			return new location();
		start_direction=start_direction.expand(1.0);
		
		point end_direction=end_point.sub(rotate_point);
		if(end_direction.distance2()<const_value.min_value2)
			return new location();
		end_direction=end_direction.expand(1.0);
		
		point aix_direction=start_direction.cross(end_direction);
		if(aix_direction.distance2()<const_value.min_value2)
			return new location();
		aix_direction=aix_direction.expand(1.0);
		
		start_point		=rotate_point.add(start_direction);
		end_point		=rotate_point.add(end_direction);
		point aix_point	=rotate_point.add(aix_direction);
		
		point start_terminated_direction=start_direction.cross(aix_direction).expand(1.0);
		point end_terminated_direction	=end_direction.  cross(aix_direction).expand(1.0);
		point start_terminated_point	=rotate_point.add(start_terminated_direction);
		point end_terminated_point		=rotate_point.add(end_terminated_direction);

		location start_location	=new location(rotate_point,aix_point,start_point,start_terminated_point);
		location end_location	=new location(rotate_point,aix_point,end_point,  end_terminated_point);
	
		location left_modify_location	=end_location.multiply(start_location.negative());
		location new_absolute_location	=left_modify_location.multiply(absolute_location);
		location right_modify_location	=absolute_location.negative().multiply(new_absolute_location);

		return right_modify_location;
	}
	public location direction_locate(point direction,location coordinate_location,boolean direction_type_flag)
	{
		location loca;

		if(direction_type_flag){
			if(coordinate_location!=null) {
				point origin_point	 =coordinate_location.multiply(new point(0,0,0));
				point direction_point=coordinate_location.multiply(direction);
				direction=direction_point.sub(origin_point);
			}
			point rotate_point	=cam.eye_component.absolute_location.multiply(new point(0,0,0));
			point start_point	=cam.eye_component.absolute_location.multiply(new point(0,0,1));
			point end_point		=rotate_point.add(direction);
			loca=caculate_rotate_right_modify_location(null,
					rotate_point,start_point,end_point,cam.eye_component.absolute_location);
		}else{
			point d_y=new point(0,1,0);
			point d_z=direction.expand(1);
			point d_x=d_y.cross(d_z);
		
			if(d_x.distance2()<const_value.min_value2){
				d_x=new point(1,0,0);
				d_y=d_z.cross(d_x).expand(1);
				d_x=d_y.cross(d_z).expand(1);
			}else{
				d_x=d_x.expand(1);
				d_y=d_z.cross(d_x).expand(1);
			}
			if(coordinate_location!=null){
				point p0=coordinate_location.multiply(new point(0,0,0));
				d_x=coordinate_location.multiply(d_x).sub(p0).expand(1);
				d_y=coordinate_location.multiply(d_y).sub(p0).expand(1);
				d_z=coordinate_location.multiply(d_z).sub(p0).expand(1);
			}
			point p0=cam.eye_component.absolute_location.multiply(new point(0,0,0));
			loca=new location(p0,p0.add(d_x),p0.add(d_y),p0.add(d_z));
			loca=loca.multiply(location.standard_negative);
			loca=cam.eye_component.caculate_negative_absolute_location().multiply(loca);
		}
		return cam.eye_component.move_location.multiply(loca);
	}	
	public location rotation_locate(point start_direction,point end_direction,location coordinate_location)
	{
		if(  (start_direction.distance2()<=const_value.min_value2)
		   ||(end_direction.  distance2()<=const_value.min_value2))
				return new location(cam.eye_component.move_location);
		if(coordinate_location!=null){
			point p0=coordinate_location.multiply(new point(0,0,0));
			start_direction=coordinate_location.multiply(start_direction).sub(p0);
			end_direction  =coordinate_location.multiply(end_direction  ).sub(p0);
		}
		location absolute_location=cam.eye_component.absolute_location;
		point p0=absolute_location.multiply(new point(0,0,0));
		location loca=caculate_rotate_right_modify_location(null,
				p0,p0.add(start_direction),p0.add(end_direction),absolute_location);

		return cam.eye_component.move_location.multiply(loca);
	}
	public location rotation_locate(point direction,double alf,location coordinate_location)
	{
		if(coordinate_location!=null){
			point p0=coordinate_location.multiply(new point(0,0,0));
			direction=coordinate_location.multiply(direction).sub(p0);
		}
		
		if(direction.distance2()<const_value.min_value2)
			return new location(cam.eye_component.move_location);

		point direction_z=direction.expand(1.0);

		location loca=cam.eye_component.absolute_location;
		point point_0=loca.multiply(new point(0,0,0));
		point point_x=loca.multiply(new point(1,0,0));
		point point_y=loca.multiply(new point(0,1,0));
		point point_z=loca.multiply(new point(0,0,1));
	
		loca =(new plane(point_0,point_0.add(direction_z))).project_to_plane_location();
		point_x=loca.multiply(point_x);
		point_y=loca.multiply(point_y);
		point_z=loca.multiply(point_z);
		double dx2=point_x.sub(point_0).distance2();
		double dy2=point_y.sub(point_0).distance2();
		double dz2=point_z.sub(point_0).distance2();
		
		if((dy2>=dx2)&&(dy2>=dz2))
			point_x=point_y;
		else if((dz2>=dx2)&&(dz2>=dy2))
			point_x=point_z;

		point direction_x=point_x.sub(point_0).expand(1.0);
		point_x=point_0.add(direction_x);
		
		point_z=point_0.add(direction_z);
		
		point direction_y=direction_z.cross(direction_x).expand(1.0);
		point_y=point_0.add(direction_y);
		
		loca=new location(point_0,point_x,point_y,point_z);
		loca=loca.multiply(location.standard_negative);
		
		point_x=new point(1,0,0);
		point start_direction=loca.multiply(point_x).sub(point_0);
		loca=loca.multiply(location.move_rotate(0,0,0,0,0,alf));
		point end_direction=loca.multiply(point_x).sub(point_0);
		
		return rotation_locate(start_direction,end_direction,null);
	}
	
	public location point_locate(point center_point,location direction_loca)
	{
		locate_box=null;
		
		if(direction_loca==null)
			direction_loca=cam.eye_component.absolute_location;

		point p0=direction_loca.multiply(new point(0,0,0));
		point dx=direction_loca.multiply(new point(1,0,0)).sub(p0).expand(1.0);
		point dy=direction_loca.multiply(new point(0,1,0)).sub(p0).expand(1.0);
		point dz=direction_loca.multiply(new point(0,0,1)).sub(p0).expand(1.0);

		location loca=new location(center_point,
			center_point.add(dx),center_point.add(dy),center_point.add(dz));
		loca=loca.multiply(location.standard_negative);
		loca=cam.eye_component.caculate_negative_absolute_location().multiply(loca);

		move_location=cam.eye_component.move_location.multiply(loca);

		return move_location;
	}
	public location box_locate(box my_locate_box,location direction_loca)
	{
		location ret_val=point_locate(my_locate_box.center(),direction_loca);
		locate_box=my_locate_box;
		return ret_val;
	}
	public void scale(double scale_value)
	{
		double box_distance,half_distance;
		if(locate_box!=null)
			if(scale_value>=const_value.min_value)
				if((box_distance=locate_box.distance())>=const_value.min_value){
					half_distance	=box_distance/2.0;
					distance		=half_distance/cam.parameter.half_fovy_tanl;
					half_fovy_tanl	=half_distance/cam.parameter.distance;
					distance		/=scale_value;
					half_fovy_tanl	/=scale_value;
				}
		return;
	}
	private boolean push_camera_modifier(modifier_container modifier_cont,
			boolean switch_camera_flag,boolean mandatory_movement_flag,boolean mandatory_scale_flag)
	{
		if(switch_camera_flag&&(cam.parameter.movement_flag|mandatory_movement_flag)){
			if(!(location.is_not_same_location(cam.eye_component.move_location,move_location))){
				if(cam.parameter.scale_value<=const_value.min_value)
					if(!mandatory_scale_flag)
						return false;
				if(cam.parameter.change_type_flag){
					if(Math.abs(cam.parameter.distance-distance)<const_value.min_value)
						return false;
				}else{
					if(Math.abs(cam.parameter.half_fovy_tanl-half_fovy_tanl)<const_value.min_value)
						return false;
				}
			}
			cam.mark_restore_stack();
			camera_parameter par=new camera_parameter(cam.parameter);
			
			if(cam.parameter.change_type_flag){
				if((cam.parameter.scale_value>const_value.min_value)|mandatory_scale_flag)
					par.distance=distance;
			}else{
				if((cam.parameter.scale_value>const_value.min_value)|mandatory_scale_flag)
					par.half_fovy_tanl=half_fovy_tanl;
			}
			cam.push_restore_stack(modifier_cont,switch_camera_flag,move_location,par);
			return true;
		}
		return false;
	}
	public boolean locate_on_origin(modifier_container modifier_cont,
			component_container component_cont,client_parameter par,
			boolean switch_camera_flag,boolean mandatory_movement_flag,boolean mandatory_scale_flag)
	{
		component comp;
		if((comp=par.comp)==null){
			if((comp=component_cont.latest_selected_component())==null)
				return false;;
			if(!(comp.uniparameter.effective_selected_flag))
				return false;
		}
		point_locate(comp.absolute_location.multiply(new point(0,0,0)),null);
		return push_camera_modifier(modifier_cont,
					switch_camera_flag,mandatory_movement_flag,mandatory_scale_flag);
	}
	public boolean locate_on_box_and_scale(
			modifier_container modifier_cont,box my_box,location dir,double my_scale_value,
			boolean switch_camera_flag,boolean mandatory_movement_flag,boolean mandatory_scale_flag)
	{
		if(my_box==null)
			return false;
		box_locate(my_box,dir);
		scale(my_scale_value);
		return push_camera_modifier(modifier_cont,
				switch_camera_flag,mandatory_movement_flag,mandatory_scale_flag);
	}
	private location client_parameter_locate(component_container component_cont,
			client_parameter par,location dir,point p0,point p1)
	{
		if(par!=null)
			if(par.comp!=null){
				box my_box;
				part my_part;
				for(int i=0,n=par.comp.driver_array.size();i<n;i++)
					if((my_part=par.comp.driver_array.get(i).component_part)!=null)
						if((my_box=my_part.secure_caculate_part_box(par.comp,i,
								par.body_id,par.face_id,par.primitive_id,
								par.vertex_id,par.loop_id,par.edge_id,p0,p1))!=null)
							if(my_box.distance2()>const_value.min_value2)
								return box_locate(par.comp.absolute_location.multiply(my_box),dir);
				if((my_box=par.comp.get_component_box(false))==null)
					if((my_box=par.comp.get_component_box(true))==null)
						return point_locate(par.comp.absolute_location.multiply(new point(0,0,0)),dir);
				return box_locate(my_box,dir);
			}
		return null;
	}
	private location camera_result_locate(component_container component_cont,
			camera_result display_camera_result,location dir)
	{
		box my_box;
		if(display_camera_result.target!=null)
			if((my_box=component_cont.get_effective_box(
					display_camera_result.target.parameter_channel_id))!=null)
				return box_locate(my_box,dir);
		return null;
	}
	public void locate_on_components(
			modifier_container modifier_cont,component_container component_cont,
			camera_result display_camera_result,client_parameter par,location dir,
			double my_scale_value,long start_time,boolean switch_camera_flag,
			boolean mandatory_movement_flag,boolean mandatory_scale_flag,point p0,point p1)
	{
		if(client_parameter_locate(component_cont,par,dir,p0,p1)==null)
			if(camera_result_locate(component_cont,display_camera_result,dir)==null)
				return;
		scale(my_scale_value);
		push_camera_modifier(modifier_cont,
				switch_camera_flag,mandatory_movement_flag,mandatory_scale_flag);
	}
}
