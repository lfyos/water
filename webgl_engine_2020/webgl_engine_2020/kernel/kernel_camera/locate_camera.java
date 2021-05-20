package kernel_camera;

import kernel_engine.component_container;
import kernel_common_class.const_value;
import kernel_driver.modifier_container;
import kernel_engine.client_parameter;
import kernel_part.part;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_transformation.plane;
import kernel_transformation.point;

public class locate_camera
{
	private box locate_box;
	private camera cam;
	
	private location move_location;
	public double distance,half_fovy_tanl;
	
	public locate_camera(camera my_cam)
	{
		cam=my_cam;
		locate_box=null;
		
		move_location	=cam.eye_component.move_location;
		distance		=cam.parameter.distance;
		half_fovy_tanl	=cam.parameter.half_fovy_tanl;
	
		return;
	}
	private location caculate_rotate_right_modify_location(point aix,
			point rotate_point,point start_point,point end_point,location absolute_location)
	{
		if(aix!=null){
			location loca	=(new plane(rotate_point,rotate_point.add(aix))).project_to_plane_location();
			start_point		=loca.multiply(start_point);
			end_point		=loca.multiply(end_point);
		}
		if(start_point.sub(rotate_point).distance2()<const_value.min_value2)
			return new location();
		if(end_point.sub(rotate_point).distance2()<const_value.min_value2)
			return new location();
		start_point	=start_point.sub(rotate_point).expand(1.0).add(rotate_point);
		end_point	=  end_point.sub(rotate_point).expand(1.0).add(rotate_point);
		
		point aix_point=start_point.sub(rotate_point).cross(end_point.sub(rotate_point));
		if(aix_point.distance2()<const_value.min_value2)
			return new location();
		aix_point=aix_point.expand(1.0).add(rotate_point);
		
		point start_t=start_point.sub(rotate_point).cross(aix_point.sub(rotate_point));
		if(start_t.distance2()<const_value.min_value2)
			return new location();
		start_t=start_t.expand(1.0).add(rotate_point);
		
		point end_t=end_point.sub(rotate_point).cross(aix_point.sub(rotate_point));
		if(end_t.distance2()<const_value.min_value2)
			return new location();
		end_t=end_t.expand(1.0).add(rotate_point);

		location start_loca				=new location(rotate_point,	aix_point,	start_point,start_t);
		location end_loca				=new location(rotate_point,	aix_point,	end_point,	end_t);
		location left_modify_location	=end_loca.multiply(start_loca.negative());
		location new_absolute_location	=left_modify_location.multiply(absolute_location);
		location right_modify_location	=absolute_location.negative().multiply(new_absolute_location);
		
		return right_modify_location;
	}
	public location direction_locate(point direction,location coordinate_location,boolean direction_type_flag)
	{
		location loca;
		
		if(direction_type_flag){
			if(coordinate_location!=null)
				direction=coordinate_location.multiply(direction).sub(coordinate_location.multiply(new point(0,0,0)));
			point rotate_point=cam.eye_component.absolute_location.multiply(new point(0,0,0));
			loca=caculate_rotate_right_modify_location(null,rotate_point,
					cam.eye_component.absolute_location.multiply(new point(0,0,1)),
					rotate_point.add(direction),cam.eye_component.absolute_location);
		}else{
			point d_y=new point(0.0,1.0,0.0);
			point d_z=direction.expand(1.0);
			point d_x=d_y.cross(d_z);
		
			if(d_x.distance2()<(const_value.min_value2)){
				d_x=new point(1.0,0.0,0.0);
				d_y=d_z.cross(d_x).expand(1.0);
				d_x=d_y.cross(d_z).expand(1.0);
			}else{
				d_x=d_x.expand(1.0);		
				d_y=d_z.cross(d_x).expand(1.0);
			}
			if(coordinate_location!=null){
				point p0=coordinate_location.multiply(new point(0,0,0));
				d_x=coordinate_location.multiply(d_x).sub(p0);
				d_y=coordinate_location.multiply(d_y).sub(p0);
				d_z=coordinate_location.multiply(d_z).sub(p0);
			}
			
			point p0=cam.eye_component.absolute_location.multiply(new point(0,0,0));
			loca=new location(p0,p0.add(d_x),p0.add(d_y),p0.add(d_z));
			loca=loca.multiply(location.standard_negative);
			loca=cam.eye_component.absolute_location.negative().multiply(loca);
		}
		return cam.eye_component.move_location.multiply(loca);
	}	
	public location rotation_locate(point start_point,point end_point,location coordinate_location)
	{
		if((start_point.distance2()<=const_value.min_value2)||(end_point.distance2()<=const_value.min_value2))
			return new location(cam.eye_component.move_location);
		if(coordinate_location!=null){
			point p0=coordinate_location.multiply(new point(0,0,0));
			start_point=coordinate_location.multiply(start_point).sub(p0);
			end_point=coordinate_location.multiply(end_point).sub(p0);
		}
		location absolute_location=cam.eye_component.absolute_location;
		point p0=absolute_location.multiply(new point(0,0,0));
		location loca=caculate_rotate_right_modify_location(null,
				p0,p0.add(start_point),p0.add(end_point),absolute_location);
		
		return cam.eye_component.move_location.multiply(loca);
	}
	public location rotation_locate(point direction,double alf,location coordinate_location)
	{
		if(coordinate_location!=null)
			direction=coordinate_location.multiply(direction).sub(coordinate_location.multiply(new point(0,0,0)));

		location loca=cam.eye_component.absolute_location;
		point p0=loca.multiply(new point(0,0,0));
		point px=loca.multiply(new point(1,0,0));
		point py=loca.multiply(new point(0,1,0));
		point pz=loca.multiply(new point(0,0,1));
	
		loca =(new plane(p0,p0.add(direction))).project_to_plane_location();
		px=loca.multiply(px);
		py=loca.multiply(py);
		pz=loca.multiply(pz);
		double dx2=px.sub(p0).distance2();
		double dy2=py.sub(p0).distance2();
		double dz2=pz.sub(p0).distance2();
		
		if((dz2>=dx2)&&(dz2>=dy2))
			px=pz;
		else if((dy2>=dx2)&&(dy2>=dz2))
			px=py;
		px=px.sub(p0).expand(1.0).add(p0);
		pz=direction.expand(1.0).add(p0);
		py=pz.sub(p0).cross(px.sub(p0)).expand(1.0).add(p0);
		loca=(new location(p0,px,py,pz)).multiply(location.standard_negative);
		
		px=new point(1,0,0);
		point ps=loca.multiply(px).sub(p0);
		loca=loca.multiply(location.move_rotate(0,0,0,0,0,alf));
		point pt=loca.multiply(px).sub(p0);
		
		return rotation_locate(ps,pt,null);
	}
	
	public location locate(point center_point,location dir)
	{
		locate_box=null;
		
		if(dir==null)
			dir=cam.eye_component.absolute_location;
		point p0=dir.multiply(new point(0,0,0));
		point px=dir.multiply(new point(1,0,0)).sub(p0).expand(1.0);
		point py=dir.multiply(new point(0,1,0)).sub(p0).expand(1.0);
		point pz=dir.multiply(new point(0,0,1)).sub(p0).expand(1.0);
			
		location loca=new location(center_point,center_point.add(px),center_point.add(py),center_point.add(pz));
		loca=loca.multiply(location.standard_negative).normalize();
		loca=cam.eye_component.absolute_location.negative().multiply(loca);

		move_location=move_location.multiply(loca);
			
		return move_location;
	}
	public location locate(box my_locate_box,location dir)
	{
		location ret_val=locate(my_locate_box.center(),dir);
		locate_box=my_locate_box;
		return ret_val;
	}
	private location locate(
			component_container component_cont,camera_result display_camera_result,
			client_parameter par,location dir,point p0,point p1)
	{
		box my_box;
		
		if(par!=null)
			if(par.comp!=null){
				for(int i=0,n=par.comp.driver_number();i<n;i++){
					part my_part=par.comp.driver_array[i].component_part;
					if((my_box=my_part.secure_caculate_part_box(par.comp,i,
							par.body_id,par.face_id,par.loop_id,par.edge_id,par.point_id,p0,p1))!=null)
						return locate(par.comp.absolute_location.multiply(my_box),dir);
				}
				if((my_box=par.comp.get_component_box(false))==null)
					if((my_box=par.comp.get_component_box(true))==null)
						return locate(par.comp.absolute_location.multiply(new point(0,0,0)),dir);
				return locate(my_box,dir);
			}
		if(display_camera_result.target!=null)
			if((my_box=component_cont.get_effective_box(
					display_camera_result.target.parameter_channel_id))!=null)
				return locate(my_box,dir);
		return null;
	}
	public void scale(double scale_value,double aspect_value)
	{
		if(locate_box!=null)
			if(scale_value>=(const_value.min_value))
				if(locate_box.distance2()>=(const_value.min_value2)){
					double box_distance=locate_box.distance();
					distance		=box_distance/(cam.parameter.half_fovy_tanl	*scale_value)/2.0;
					half_fovy_tanl	=box_distance/(cam.parameter.distance		*scale_value)/2.0;
					if(aspect_value<1.0) {
						distance		/=aspect_value;
						half_fovy_tanl	/=aspect_value;
					}
				}
		return;
	}
	private boolean locate_on_components(modifier_container modifier_cont,
			boolean switch_camera_flag,boolean mandatory_movement_flag,boolean mandatory_scale_flag)
	{
		if(switch_camera_flag&&(cam.parameter.movement_flag|mandatory_movement_flag)){
			if(!(location.is_not_same_location(cam.eye_component.move_location,move_location))){
				if(cam.parameter.scale_value<const_value.min_value)
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
	public boolean locate_on_components(modifier_container modifier_cont,
			box my_box,location dir,double my_scale_value,double my_aspect_value,
			boolean switch_camera_flag,boolean mandatory_movement_flag,boolean mandatory_scale_flag)
	{
		if(my_box!=null){
			locate(my_box,dir);
			scale(my_scale_value,my_aspect_value);
			return locate_on_components(modifier_cont,switch_camera_flag,
					mandatory_movement_flag,mandatory_scale_flag);
		}
		return false;
	}
	public void locate_on_components(
			modifier_container modifier_cont,component_container component_cont,
			camera_result display_camera_result,client_parameter par,location dir,
			double my_scale_value,double my_aspect_value,long start_time,boolean switch_camera_flag,
			boolean mandatory_movement_flag,boolean mandatory_scale_flag,point p0,point p1)
	{
		if(locate(component_cont,display_camera_result,par,dir,p0,p1)!=null){
			scale(my_scale_value,my_aspect_value);
			locate_on_components(modifier_cont,switch_camera_flag,
					mandatory_movement_flag,mandatory_scale_flag);
		}
	}
}
