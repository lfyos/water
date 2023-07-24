package driver_movement;

import kernel_common_class.jason_string;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.point;
import kernel_transformation.location;

public class movement_get_location extends movement_design_base
{
	public movement_get_location(engine_kernel ek,client_information ci,movement_manager manager)
	{
		super(ek,ci,manager);
		
		movement_location_to_move_rotate p;
		String coordinate_str=(comp==null)?"":ci.request_response.get_parameter("coordinate");
		switch((coordinate_str==null)?"":coordinate_str) {
		case "view":
			p=new movement_location_to_move_rotate(comp.absolute_location,comp.move_location,
					ci.display_camera_result.cam.eye_component.absolute_location);
			break;
		case "camera":
			p=new movement_location_to_move_rotate(comp.absolute_location,comp.move_location,
					ci.display_camera_result.cam.eye_component.absolute_location.multiply(new point(0,0,0)));
			break;
		default:
			coordinate_str="component";
			p=new movement_location_to_move_rotate((comp==null)?new location():comp.move_location);
			break;
		}
		int movement_number=0;
		double time_length=1000000000;
		boolean start_state_flag=true,terminate_state_flag=false;
		String par[]=null;
		
		if(manager.designed_move!=null)
			if(manager.designed_move.move!=null) {
				if(manager.designed_move.move.movement!=null) {
					if((movement_number=manager.designed_move.move.movement.length)>0) {
						int index_id=manager.designed_move.move.movement.length-1;
						par=manager.designed_move.move.movement[index_id].start_parameter;
						time_length=manager.designed_move.move.movement[index_id].time_length;
					}
				}
				start_state_flag	=manager.designed_move.move.start_state_flag;
				terminate_state_flag=manager.designed_move.move.terminate_state_flag;
			}

		ci.request_response.println("{");
		
		ci.request_response.print  ("	\"component_id\"		:	",
			(comp==null)?-1:comp.component_id).println(",");
		ci.request_response.print  ("	\"component_name\"	:	",
			jason_string.change_string((comp==null)?"no_component_name":comp.component_name)).println(",");
	
		ci.request_response.print  ("	\"movement_number\" 	:	",movement_number).println(",");
		
		ci.request_response.print  ("	\"coordinate\"		:	\"",coordinate_str).println("\",");
		ci.request_response.print  ("	\"mx\"			:	",	p.m_x).println(",");
		ci.request_response.print  ("	\"my\"	 		:	",	p.m_y).println(",");
		ci.request_response.print  ("	\"mz\" 			:	",	p.m_z).println(",");
		ci.request_response.print  ("	\"rx\"			:	",	p.r_x).println(",");
		ci.request_response.print  ("	\"ry\"			:	",	p.r_y).println(",");
		ci.request_response.print  ("	\"rz\" 			:	",	p.r_z).println(",");
		
		ci.request_response.print  ("	\"parameter\" 		:	[");
		if(par!=null)
			for(int i=0,ni=par.length;i<ni;i++) {
				String str=jason_string.change_string(par[i]);
				ci.request_response.print  ("	",str).println((i==(ni-1))?"":",");
			}
		ci.request_response.println("],");

		ci.request_response.print  ("	\"time_length\" 		:	",time_length).println(",");

		ci.request_response.println("	\"start_state_flag\" 	:	",		start_state_flag?"true,":"false,");
		ci.request_response.println("	\"terminate_state_flag\" 	:	",	terminate_state_flag?"true":"false");

		ci.request_response.println("}");
	}
}
