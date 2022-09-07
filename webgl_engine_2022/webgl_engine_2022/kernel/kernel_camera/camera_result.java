package kernel_camera;

import kernel_render.render_target;
import kernel_transformation.location;
import kernel_transformation.plane;
import kernel_transformation.point;
import kernel_transformation.box;
import kernel_transformation.projection_matrix;
import kernel_transformation.screen_move_matrix;
import kernel_transformation.tetrahedron;
import kernel_component.component;
import kernel_component.component_container;
import kernel_engine.client_information;
import kernel_engine.client_parameter;

public class camera_result
{
	public camera 			cam;
	public render_target	target;
	
	public location			camera_absolute_matrix;
	public location			screen_move_matrix,		negative_screen_move_matrix;
	
	public location			frustem_matrix,			negative_frustem_matrix;
	public location			orthographic_matrix,	negative_orthographic_matrix;
	
	public location			matrix,					negative_matrix;
	
	public void destroy()
	{
		cam=null;
		target=null;
		
		camera_absolute_matrix=null;
		screen_move_matrix=null;
		negative_screen_move_matrix=null;
		frustem_matrix=null;
		negative_frustem_matrix=null;
		orthographic_matrix=null;
		negative_orthographic_matrix=null;
		matrix=null;
		negative_matrix=null;
		
		left_up_far=null;
		left_down_far=null;
		right_up_far=null;
		right_down_far=null;
		left_up_near=null;
		left_down_near=null;
		right_up_near=null;
		right_down_near=null;
		
		eye_point=null;
		center_point=null;
		right_direct=null;
		up_direct=null;
		to_me_direct=null;
		
		left_plane=null;
		right_plane=null;
		up_plane=null;
		down_plane=null;
		near_plane=null;
		far_plane=null;
		
		if(clip!=null)
			for(int i=0,ni=clip.length;i<ni;i++)
				clip[i]=null;
		clip=null;
	}
	
	private void basic_init()
	{
		if(target.mirror_plane==null)
			camera_absolute_matrix=cam.eye_component.absolute_location;
		else
			camera_absolute_matrix=target.mirror_plane.reflect_by_plane_location().
				multiply(cam.eye_component.absolute_location).
				multiply(new location(new double[]
					{
							1,	 0,		0,		0,
							0,	-1,		0,		0,
							0,	 0,		1,		0,
							0,	 0,		0,		1
					}));
		
		location negative_lookat_matrix	=camera_absolute_matrix.multiply(location.move_rotate(0,0,cam.parameter.distance,0,0,0));
		location lookat_matrix			=negative_lookat_matrix.negative();

		screen_move_matrix 		smm	=new screen_move_matrix(target.view_volume_box);
		screen_move_matrix			=smm.screen_move_matrix;
		negative_screen_move_matrix	=smm.negative_screen_move_matrix;

		projection_matrix 		pm	=new projection_matrix(cam.parameter.near_ratio,cam.parameter.far_ratio,
												cam.parameter.half_fovy_tanl,cam.parameter.distance);
		frustem_matrix				=screen_move_matrix.multiply(pm.frustem_matrix).multiply(lookat_matrix);
		negative_frustem_matrix		=negative_lookat_matrix.multiply(pm.negative_frustem_matrix).multiply(negative_screen_move_matrix);
		orthographic_matrix			=screen_move_matrix.multiply(pm.orthographic_matrix).multiply(lookat_matrix);
		negative_orthographic_matrix=negative_lookat_matrix.multiply(pm.negative_orthographic_matrix).multiply(negative_screen_move_matrix);

		matrix			=cam.parameter.projection_type_flag?frustem_matrix			:orthographic_matrix;
		negative_matrix	=cam.parameter.projection_type_flag?negative_frustem_matrix	:negative_orthographic_matrix;
	}
	public point			left_up_far, left_down_far, right_up_far, right_down_far;
	public point			left_up_near,left_down_near,right_up_near,right_down_near;
	
	public box				view_box;
	
	public point			eye_point,center_point;
	public point			right_direct,up_direct,to_me_direct;
	
	private void caculate_view_points_and_box()
	{
		left_up_far		=negative_matrix.multiply(new point(-1, 1, 1));
		left_down_far	=negative_matrix.multiply(new point(-1,-1, 1));
		right_up_far	=negative_matrix.multiply(new point( 1, 1, 1));
		right_down_far	=negative_matrix.multiply(new point( 1,-1, 1));

		left_up_near	=negative_matrix.multiply(new point(-1, 1,-1));
		left_down_near	=negative_matrix.multiply(new point(-1,-1,-1));
		right_up_near	=negative_matrix.multiply(new point( 1, 1,-1));
		right_down_near	=negative_matrix.multiply(new point( 1,-1,-1));
		
		view_box=new box(new point[] 
				{
					left_up_far,	left_up_near,	left_down_far,	left_down_near,
					right_up_far,	right_up_near,	right_down_far,	right_down_near
				});
		
		eye_point		=camera_absolute_matrix.multiply(new point(0,0,cam.parameter.distance));
		center_point	=camera_absolute_matrix.multiply(new point(0,0,0));
		
		right_direct	=camera_absolute_matrix.multiply(new point(1,0,0)).sub(center_point).expand(1.0);
		up_direct		=camera_absolute_matrix.multiply(new point(0,1,0)).sub(center_point).expand(1.0);
		to_me_direct	=camera_absolute_matrix.multiply(new point(0,0,1)).sub(center_point).expand(1.0);
	}
	
	public plane		left_plane,right_plane,up_plane,down_plane,near_plane,far_plane;
	
	private plane		clip[];
	
	private int add_clip_component(plane new_clip_plane)
	{
		if(new_clip_plane.error_flag)
			return -1;
		if(clip==null)
			clip=new plane[1];
		else{
			plane []bak_clip=clip;
			clip=new plane[clip.length+1];
			for(int i=0,ni=bak_clip.length;i<ni;i++)
				clip[i]=bak_clip[i];
		}
		clip[clip.length-1]=new plane(new_clip_plane);
		return clip.length-1;
	}
	private void caculate_clip_planes()
	{
		up_plane		=new plane(right_up_near,	right_up_far,	left_up_far);
		down_plane		=new plane(left_down_near,	left_down_far,	right_down_far);
		left_plane		=new plane(left_up_near,	left_up_far,	left_down_far);
		right_plane		=new plane(right_down_near,	right_down_far,	right_up_far);
		near_plane		=new plane(right_up_near,	left_up_near,	left_down_near);
		far_plane		=new plane(right_up_far,	right_down_far,	left_down_far);
		
		clip			=null;
		add_clip_component(up_plane);
		add_clip_component(down_plane);
		add_clip_component(left_plane);
		add_clip_component(right_plane);
		
		add_clip_component(near_plane);
		add_clip_component(far_plane);
		
		if(target.clip_plane!=null)
			add_clip_component(target.clip_plane);
	}
	private void caculate_component_location(component comp,component_container component_cont)
	{
		if(comp!=null){
			component comp_parent;
			if((comp_parent=component_cont.get_component(comp.parent_component_id))!=null)
				caculate_component_location(comp_parent,component_cont);
			comp.caculate_location(component_cont);
		}
	}
	public camera_result(camera my_cam,render_target my_cam_target,component_container component_cont)
	{
		cam=my_cam;
		target=my_cam_target;
		
		caculate_component_location(cam.eye_component,component_cont);
		
		basic_init();
		caculate_view_points_and_box();
		caculate_clip_planes();
	}
	public point caculate_local_focus_point(client_parameter parameter)
	{
		if(parameter.comp==null)
			return null;
		int driver_id,driver_number=parameter.comp.driver_number();
		if((driver_id=parameter.comp.fix_render_driver_id)<0)
			driver_id=0;
		if(driver_id>=driver_number)
			return null;
		
		if(parameter.comp.driver_array[driver_id].component_part==null)
			return null;
		if(!(parameter.comp.uniparameter.part_list_flag))
			return null;
		
		location comp_negative_loca=parameter.comp.caculate_negative_absolute_location();
		point p0=comp_negative_loca.multiply(negative_matrix.multiply(new point(0,0,parameter.depth+0.0)));
		point p1=comp_negative_loca.multiply(negative_matrix.multiply(new point(0,0,parameter.depth+1.0)));

		box my_box=parameter.comp.driver_array[driver_id].component_part.secure_caculate_part_box(
					parameter.comp,driver_id,parameter.body_id,parameter.face_id,parameter.loop_id,
					parameter.edge_id,parameter.point_id,p0,p1);
		if(my_box==null)
			return null;
		
		return my_box.center();
	}
	
	public boolean clipper_test(component comp,component_container component_cont,int parameter_channel_id)
	{
		if(comp.clip.has_done_clip_flag)
			return comp.clip.can_be_clipped_flag;
		
		comp.clip.has_done_clip_flag=true;
		comp.clip.can_be_clipped_flag=false;
		
		component parent=component_cont.get_component(comp.parent_component_id);
		comp.clip.clear_clip_plane();
		
		plane []comp_clip_plane;
		if((parent==null)||(comp.clip.clipper_test_depth<=0))
			comp_clip_plane=clip;
		else{
			if(clipper_test(parent,component_cont,parameter_channel_id)){
				comp.caculate_location(component_cont);
				comp.caculate_box(false);
				comp.clip.can_be_clipped_flag=true;
				return true;
			}
			comp_clip_plane=parent.clip.clip_plane;
		}
		
		comp.caculate_location(component_cont);
		comp.caculate_box(false);
		
		for(int i=0,ni=(comp_clip_plane==null)?0:comp_clip_plane.length;i<ni;i++){
			switch(comp_clip_plane[i].clip_component_test(comp,parameter_channel_id)){
			case 0:
				comp.clip.can_be_clipped_flag=true;
				return true;					//total box is outside,all can be clipped,unnecessary to to clip test 
			case 8:
				break;							//inner to one clip plane
			default:
				comp.clip.close_clip_plane_number+=comp.clip.add_clip_plane(comp_clip_plane[i]);
												//some inside,some outside,clip can not be decided					
				break;									
			}
		}
		if((comp.clip.clip_plane==null)||(comp.children_number()>0)||(comp.model_box==null))
			return false;
		
		tetrahedron undecided_box=new tetrahedron(comp.absolute_location,comp.model_box);
		for(int i=0,ni=comp.clip.clip_plane.length;i<ni;i++){
			tetrahedron p=undecided_box;
			undecided_box=null;
			for(;p!=null;p=p.next)
				undecided_box=p.clip_tetrahedron(comp.clip.clip_plane[i],undecided_box);
		}
		if(undecided_box!=null)
			return false;
		comp.clip.can_be_clipped_flag=true;
		
		return true;
	}
	public int get_render_buffer_id(client_information ci)
	{
		return target.get_render_buffer_id(ci.parameter.high_or_low_precision_flag);
	}
	public double []caculate_view_coordinate(client_information ci)
	{
		return target.caculate_view_coordinate(ci.parameter.x,ci.parameter.y);
	}
}
