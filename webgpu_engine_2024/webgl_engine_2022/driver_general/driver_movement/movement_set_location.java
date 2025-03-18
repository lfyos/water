package driver_movement;

import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_transformation.location;
import kernel_transformation.point;

public class movement_set_location  extends movement_design_base
{
	private location move_rotate(
			double mx,double my,double mz,double rx,double ry,double rz,
			location comp_absolute_loca,location comp_move_loca,location rotate_comp_loca)
	{
		location comp_loca=comp_absolute_loca.multiply(comp_move_loca.negative());
		location loca=rotate_comp_loca.negative().multiply(comp_loca);
		loca=location.move_rotate(mx,my,mz,rx,ry,rz).multiply(loca);
		loca=rotate_comp_loca.multiply(loca);
		loca=new location(
				loca.multiply(new point(0,0,0)),
				loca.multiply(new point(1,0,0)),
				loca.multiply(new point(0,1,0)),
				loca.multiply(new point(0,0,1)));
		loca=loca.multiply(location.standard_negative);
		
		return comp_loca.negative().multiply(loca);
	}
	private location move_rotate(
			double mx,double my,double mz,double rx,double ry,double rz,
			location comp_absolute_loca,location comp_move_loca,point rotate_center)
	{
		location loca=new location(rotate_center,	rotate_center.add(new point(1,0,0)),
				rotate_center.add(new point(0,1,0)),rotate_center.add(new point(0,0,1)));
		loca=loca.multiply(location.standard_negative);
		return move_rotate(mx,my,mz,rx,ry,rz,comp_absolute_loca,comp_move_loca,loca);
	}
	public movement_set_location(scene_kernel sk,client_information ci,movement_manager manager)
	{
		super(sk,ci,manager);
		if(comp==null)
			return;
		String coordinate_str=ci.request_response.get_parameter("coordinate");
		switch((coordinate_str==null)?"":coordinate_str){
		case "view":
			comp.set_component_move_location(
				move_rotate(
					get_double(ci,"mx",0),get_double(ci,"my",0),get_double(ci,"mz",0),
					get_double(ci,"rx",0),get_double(ci,"ry",0),get_double(ci,"rz",0),
					comp.absolute_location,comp.move_location,
					ci.display_camera_result.cam.eye_component.absolute_location),
				sk.component_cont);
			break;
		case "camera":
			comp.set_component_move_location(
				move_rotate(
					get_double(ci,"mx",0),get_double(ci,"my",0),get_double(ci,"mz",0),
					get_double(ci,"rx",0),get_double(ci,"ry",0),get_double(ci,"rz",0),
					comp.absolute_location,comp.move_location,
					ci.display_camera_result.cam.eye_component.absolute_location.multiply(new point(0,0,0))),
				sk.component_cont);
			break;
		default:
			comp.set_component_move_location(
				location.move_rotate(
					get_double(ci,"mx",0),get_double(ci,"my",0),get_double(ci,"mz",0),
					get_double(ci,"rx",0),get_double(ci,"ry",0),get_double(ci,"rz",0)),
				sk.component_cont);
			break;
		}
	}
}
