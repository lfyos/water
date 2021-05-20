package driver_tag;

import kernel_camera.camera_result;
import kernel_common_class.const_value;
import kernel_common_class.format_change;
import kernel_component.component;
import kernel_engine.client_information;
import kernel_engine.client_parameter;
import kernel_engine.engine_kernel;
import kernel_transformation.location;
import kernel_transformation.plane;
import kernel_transformation.point;

public class tag_instance 
{
	public int state,direction_flag,tag_component_id;
	public point p0,right_direct,up_direct;
	
	public driver_text.text_item t_item;
	
	public boolean is_display()
	{
		switch(state){
		case 1:
		case 2:
		case 3:
			if(right_direct.distance2()>const_value.min_value2)
				if(up_direct.distance2()>const_value.min_value2)
					return true;
			return false;
		default:
			return false;
		}
	}
	private point adjust_up_direct(camera_result cam_result,double view_text_height)
	{		
		point h_center	=p0.add(right_direct.scale(0.5));
		point tag_center=h_center.add(up_direct.scale(0.5));
		plane pl		=new plane(p0,h_center,tag_center);
		
		if(pl.error_flag)
			return new point();
	
		point h_center_view		=cam_result.matrix.multiply(h_center);
		point tag_center_view	=cam_result.matrix.multiply(tag_center);
		point view_dir=tag_center_view.sub(h_center_view);
		
		view_dir.z=0;
		view_dir=view_dir.expand(view_text_height);
		double x=tag_center_view.x+view_dir.x,y=tag_center_view.y+view_dir.y;
		point p0=cam_result.negative_matrix.multiply(new point(x,y,0));
		point p1=cam_result.negative_matrix.multiply(new point(x,y,1));
		point cross_point=pl.insect_location(p0).multiply(p1);
		
		if(cross_point.distance2()<const_value.min_value2)
			return new point();
		
		double aspect_value,scale_value;
		
		aspect_value =cam_result.target.view_volume_box.p[1].x-cam_result.target.view_volume_box.p[0].x;
		aspect_value/=cam_result.target.view_volume_box.p[1].y-cam_result.target.view_volume_box.p[0].y;
		aspect_value-=1;
		
		scale_value=right_direct.expand(1.0).dot(cam_result.right_direct);
		scale_value=1+Math.abs(scale_value*aspect_value);
		
		return cross_point.sub(tag_center).scale(scale_value);
	}
	
	public void set_text(engine_kernel ek,client_information ci,camera_result cam_result,
			int display_text_component_id,int canvas_width,int canvas_height,double view_text_height)
	{
		String str;
		component text_comp,tag_comp;
		
		if((text_comp=ek.component_cont.get_component(display_text_component_id))==null)
			return;
	
		t_item.display_information=new String[]{""};
		
		t_item.text_square_width=0;
		t_item.text_square_height=0;
		
		t_item.canvas_height=canvas_height;
		
		t_item.view_or_model_coordinate_flag=false;
		
		if(is_display()){
			switch(direction_flag){
			default:
			case 0:
				str=format_change.double_to_decimal_string(
						right_direct.distance(),ek.scene_par.display_precision);
				t_item.display_information[0]=str.trim();
				break;
			case 1:
				str=format_change.double_to_decimal_string(
						Math.abs(right_direct.x),ek.scene_par.display_precision);
				t_item.display_information[0]="X:"+str.trim();
				break;
			case 2:
				str=format_change.double_to_decimal_string(
						Math.abs(right_direct.y),ek.scene_par.display_precision);
				t_item.display_information[0]="Y:"+str.trim();
				break;
			case 3:
				str=format_change.double_to_decimal_string(
						Math.abs(right_direct.z),ek.scene_par.display_precision);
				t_item.display_information[0]="Z:"+str.trim();
				break;
			case 4:
				if((tag_comp=ek.component_cont.get_component(tag_component_id))!=null){
					point p0=tag_comp.absolute_location.multiply(new point(0,0,0));
					point pp=tag_comp.absolute_location.multiply(new point(1,0,0));
					str=format_change.double_to_decimal_string(
							Math.abs(pp.sub(p0).expand(1.0).dot(right_direct)),
							ek.scene_par.display_precision);
					t_item.display_information[0]="LX:"+str.trim();
				}
				break;
			case 5:
				if((tag_comp=ek.component_cont.get_component(tag_component_id))!=null){
					point p0=tag_comp.absolute_location.multiply(new point(0,0,0));
					point pp=tag_comp.absolute_location.multiply(new point(0,1,0));
					str=format_change.double_to_decimal_string(
							Math.abs(pp.sub(p0).expand(1.0).dot(right_direct)),
							ek.scene_par.display_precision);
					t_item.display_information[0]="LY:"+str.trim();
				}
				break;
			case 6:
				if((tag_comp=ek.component_cont.get_component(tag_component_id))!=null){
					point p0=tag_comp.absolute_location.multiply(new point(0,0,0));
					point pp=tag_comp.absolute_location.multiply(new point(0,0,1));
					str=format_change.double_to_decimal_string(
							Math.abs(pp.sub(p0).expand(1.0).dot(right_direct)),
							ek.scene_par.display_precision);
					t_item.display_information[0]="LZ:"+str.trim();
				}
				break;
			case 7:
				str=format_change.double_to_decimal_string(
						Math.abs(right_direct.dot(cam_result.right_direct)),
						ek.scene_par.display_precision);
				t_item.display_information[0]="LR:"+str.trim();
				break;
			case 8:
				str=format_change.double_to_decimal_string(
						Math.abs(right_direct.dot(cam_result.up_direct)),
						ek.scene_par.display_precision);
				t_item.display_information[0]="UD:"+str.trim();
				break;
			case 9:
				str=format_change.double_to_decimal_string(
						Math.abs(right_direct.dot(cam_result.to_me_direct)),
						ek.scene_par.display_precision);
				t_item.display_information[0]="IO:"+str.trim();
				break;
			case 10://标注xy面
				{
					point p=(new plane(0,0,1,0)).project_to_plane_location().multiply(right_direct);
					str=format_change.double_to_decimal_string(p.distance(),ek.scene_par.display_precision);
					t_item.display_information[0]="XY:"+str.trim();
				}
				break;
			case 11://标注yz面
				{
					point p=(new plane(1,0,0,0)).project_to_plane_location().multiply(right_direct);
					str=format_change.double_to_decimal_string(p.distance(),ek.scene_par.display_precision);
					t_item.display_information[0]="YZ:"+str.trim();
				}
				break;
			case 12://标注zx面
				{
					point p=(new plane(0,1,0,0)).project_to_plane_location().multiply(right_direct);
					str=format_change.double_to_decimal_string(p.distance(),ek.scene_par.display_precision);
					t_item.display_information[0]="ZX:"+str.trim();
				}
				break;
			case 13://标注局部xy面
				if((tag_comp=ek.component_cont.get_component(tag_component_id))!=null){
					point p0=tag_comp.absolute_location.multiply(new point(0,0,0));
					point pz=tag_comp.absolute_location.multiply(new point(0,0,1));
					location loca=(new plane(p0,pz)).project_to_plane_location();
					str=format_change.double_to_decimal_string(
							loca.multiply(right_direct).sub(loca.multiply(new point(0,0,0))).distance(),
							ek.scene_par.display_precision);
					t_item.display_information[0]="LXY:"+str.trim();
				}
				break;
			case 14://标注局部yz面
				if((tag_comp=ek.component_cont.get_component(tag_component_id))!=null){
					point p0=tag_comp.absolute_location.multiply(new point(0,0,0));
					point px=tag_comp.absolute_location.multiply(new point(1,0,0));
					location loca=(new plane(p0,px)).project_to_plane_location();
					str=format_change.double_to_decimal_string(
							loca.multiply(right_direct).sub(loca.multiply(new point(0,0,0))).distance(),
							ek.scene_par.display_precision);
					t_item.display_information[0]="LYZ:"+str.trim();
				}
				break;
			case 15://标注局部zx面
				if((tag_comp=ek.component_cont.get_component(tag_component_id))!=null){
					point p0=tag_comp.absolute_location.multiply(new point(0,0,0));
					point py=tag_comp.absolute_location.multiply(new point(0,1,0));
					location loca=(new plane(p0,py)).project_to_plane_location();
					str=format_change.double_to_decimal_string(
							loca.multiply(right_direct).sub(loca.multiply(new point(0,0,0))).distance(),
							ek.scene_par.display_precision);
					t_item.display_information[0]="LZX:"+str.trim();
				}
				break;
			case 16://标注视面
				if((tag_comp=ek.component_cont.get_component(tag_component_id))!=null){
					point p0=cam_result.center_point;
					point p_eye=cam_result.eye_point;
					location loca=(new plane(p0,p_eye)).project_to_plane_location();
					str=format_change.double_to_decimal_string(
							loca.multiply(right_direct).sub(loca.multiply(new point(0,0,0))).distance(),
							ek.scene_par.display_precision);
					t_item.display_information[0]="View:"+str.trim();
				}
				break;
			}

			t_item.canvas_width=canvas_width*(t_item.display_information[0].length());
			
			double length=adjust_up_direct(cam_result,view_text_height).distance();
			if(length>const_value.min_value2){
				t_item.text_square_height=length;
				t_item.text_square_width=t_item.display_information[0].length()*length;

				point q0,dx,dy,dz;
				
				if(state==1){
					q0=p0.add(up_direct).add(right_direct);
					q0=q0.sub(right_direct.expand(t_item.text_square_width+length+length));
				}else{
					q0=p0.add(up_direct).add(right_direct.scale(0.5));
					q0=q0.sub(right_direct.expand(t_item.text_square_width/2));
				}
				
				dx=right_direct.expand(1.0);
				dy=up_direct.expand(1.0);
				dz=dx.cross(dy).expand(1.0);
				location loca=new location(q0,q0.add(dx),q0.add(dy),q0.add(dz));
				loca=loca.multiply(location.standard_negative);
				loca=loca.multiply(location.move_rotate(t_item.text_square_width/2, 0, 0,0,0,0));

				text_comp.modify_location(loca,ek.component_cont);
			}
		}
		for(int i=0,ni=text_comp.driver_number();i<ni;i++)
			if(text_comp.driver_array[i] instanceof driver_text.extended_component_driver)
				((driver_text.extended_component_driver)(text_comp.driver_array[i])).set_text(t_item);
		return ;
	}
	public void touch(
			engine_kernel ek,client_information ci,camera_result cam_result,
			int display_text_component_id,
			int my_direction_flag,client_parameter par,
			double view_text_height,int canvas_width,int canvas_height)
	{
		plane horizon_plane,vertical_plane;
		point right_point,view_point,cross_point,modify_dir;
		
		direction_flag=my_direction_flag;
		
		switch(state){
		default:
			return;
		case 1:
			if((right_point=ci.selection_camera_result.caculate_local_focus_point(ci.parameter))!=null)
				right_direct=par.comp.absolute_location.multiply(right_point).sub(p0);
		case 2:
			break;
		}
		
		right_point=p0.add(right_direct);
		if((horizon_plane=new plane(cam_result.eye_point,right_point,p0)).error_flag)
			return ;
		if((up_direct=new point(horizon_plane.A,horizon_plane.B,horizon_plane.C)).distance2()<const_value.min_value2)
			return ;
		if((vertical_plane=new plane(p0,right_point,p0.add(up_direct))).error_flag)
			return ;

		if(state==2){
			if(ci.display_camera_result==null)
				return;
			double viewport[]=ci.display_camera_result.caculate_view_coordinate(ci);
			if(viewport==null)
				return;
			view_point=new point(viewport[0],viewport[1],1);
		}else{
			up_direct				=up_direct.scale(-1);
			point up_point			=right_point.add(up_direct);
			point view_up_point		=cam_result.matrix.multiply(up_point);
			point view_right_point	=cam_result.matrix.multiply(right_point);
			point view_direction	=view_up_point.sub(view_right_point);
			view_direction.z=0;
			view_direction=view_direction.expand(view_text_height/2.0);
			view_point=view_right_point.add(view_direction);
		}
		{
			point p0=cam_result.negative_matrix.multiply(new point(view_point.x,view_point.y,0.0));
			point p1=cam_result.negative_matrix.multiply(new point(view_point.x,view_point.y,1.0));
			if((cross_point=vertical_plane.insect_location(p0).multiply(p1)).distance2()<const_value.min_value2)
				return;
		}
		up_direct=cross_point.sub(horizon_plane.project_to_plane_location().multiply(cross_point));
		
		if((modify_dir=adjust_up_direct(cam_result,view_text_height))!=null)
			up_direct=up_direct.sub(modify_dir.scale(0.5));
	
		return ;
	}
	public boolean test_undone_state()
	{
		switch(state){
		case 1://input one point
		case 2://input two point
			return true;
		case 0://unused
		default:// finished and fixed
			return false;
		}
	}
	public String set(
			engine_kernel ek,client_information ci,camera_result cam_result,
			int display_text_component_id,int my_direction_flag,client_parameter par,
			double view_text_height,int canvas_width,int canvas_height)
	{
		point my_point;
		
		switch(state){
		case 0://unused
			if(ci.parameter.comp!=null)
				if(ci.parameter.comp.uniparameter.part_list_flag){
					if((my_point=ci.selection_camera_result.caculate_local_focus_point(ci.parameter))!=null){
						p0=par.comp.absolute_location.multiply(my_point);
						right_direct=new point(0,0,0);
						up_direct	=new point(0,0,0);
						tag_component_id=par.comp.component_id;
						state=1;
						return "请选择第二个标注点";
					}
				}
			return null;
		case 1://input one point
			if(ci.parameter.comp!=null)
				if(ci.parameter.comp.uniparameter.part_list_flag){
					if((my_point=ci.selection_camera_result.caculate_local_focus_point(ci.parameter))!=null){
						right_direct=par.comp.absolute_location.multiply(my_point).sub(p0);
						if(right_direct.distance2()>=const_value.min_value2){
							up_direct=new point(0,0,0);
							state=2;
							return "请确认标注位置";
						}
					}
				}
			return "请选择第二个标注点";
		case 2://input two point
			if(up_direct.distance2()>=const_value.min_value2){
				state=3;
				return "";
			}
			return "请确认标注位置";
		default:// finished and fixed
			return null;
		}
	}
	public tag_instance()
	{
		state			=0;
		direction_flag	=0;
		p0				=new point(0,0,0);
		right_direct	=new point(0,0,0);
		up_direct		=new point(0,0,0);
		t_item			=new driver_text.text_item();
	}
}
