package driver_movement;

import kernel_camera.camera_parameter;
import kernel_common_class.format_change;
import kernel_component.component;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.location;
import kernel_transformation.point;

public class movement_desinger
{
	private engine_kernel ek;
	private client_information ci;
	private movement_manager manager;
	
	private movement_design_parameter parameter;
	
	private void set_designed_move()
	{
		if(manager==null)
			return;
		if(manager.designed_move==null)
			manager.designed_move=new movement_tree();
		
		component design_component=ek.component_cont.get_component(parameter.design_component_id);
		
		manager.designed_move.node_name					=parameter.node_name;
		manager.designed_move.sound_file_name			=parameter.sound_file_name;
		manager.designed_move.description				=parameter.move_description;
		manager.designed_move.sequence_flag				=true;
		
		if(manager.designed_move.move==null)
			manager.designed_move.move					=new movement_item_container(design_component);
		manager.designed_move.move.moved_component_id	=design_component.component_id;
		manager.designed_move.move.moved_component_name	=design_component.component_name;
		manager.designed_move.move.start_state_flag		=parameter.start_show_flag;
		manager.designed_move.move.terminate_state_flag	=parameter.terminate_show_flag;
		if(manager.designed_move.match==null)
			manager.designed_move.match					=new movement_match_container();
		manager.designed_move.children					=null;	
	}
	public void add_movement()
	{	
		if(success_flag)
			return;
		
		ci.request_response.print(0);
		
		if(ci.display_camera_result==null)
			return;
		if(ci.display_camera_result.target==null)
			return;
		if(manager.designed_move.move==null)
			return;
		if(manager.designed_move.move.movement==null)
			return;
		if(ek.camera_cont.camera_array==null)
			return;

		movement_item bak[];
		if((bak=manager.designed_move.move.movement).length<=1)
			return;
		manager.designed_move.move.movement=new movement_item[bak.length-1];
		for(int i=0,ni=bak.length-1;i<ni;i++)
			manager.designed_move.move.movement[i]=bak[i];

		camera_parameter cam_par=ci.display_camera_result.cam.parameter;
		long switch_time_length=cam_par.movement_flag?cam_par.switch_time_length:0;
		
		if(manager.root_movement==null)
			manager.root_movement=new movement_tree();
		if(manager.root_movement.children==null){
			manager.root_movement.children=new movement_tree[1];
			manager.root_movement.children[0]=manager.designed_move;
		}else{
			manager.movement_start(ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
				manager.root_movement.movement_tree_id,ek.component_cont,true,switch_time_length);
			movement_tree tmp[]=new movement_tree[manager.root_movement.children.length+1];
			for(int i=0;i<(manager.root_movement.children.length);i++)
				tmp[i+1]=manager.root_movement.children[i];
			tmp[0]=manager.designed_move;
			manager.root_movement.children=tmp;
		}
		manager.designed_move=null;
		manager.reset(ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
				ek.component_cont,switch_time_length);
		
		component design_component=ek.component_cont.get_component(parameter.design_component_id);
		if(design_component!=null)
			design_component.modify_location(new location(),ek.component_cont);	
		manager.last_edit_move_id=-1;
	}
	
	public void add_point()
	{	
		if(success_flag)
			return;
		
		movement_item_container mic=manager.designed_move.move;
		movement_item tmp[]=mic.movement;
		movement_item p=new movement_item(parameter.time_length,
				parameter.parameter,parameter.design_location,null,new location());
		
		if(tmp==null)
			mic.movement=new movement_item[1];
		else{
			mic.movement=new movement_item[mic.movement.length+1];
			for(int i=0;i<tmp.length;i++)
				mic.movement[i]=tmp[i];
		}
		mic.movement[mic.movement.length-1]=p;
		if(mic.movement.length>1){
			p=mic.movement[mic.movement.length-2];
			mic.movement[mic.movement.length-2]=new movement_item(p.time_length,
				p.start_parameter,p.start_location,parameter.parameter,parameter.design_location);
		}
		ci.request_response.print(mic.movement.length);
	}	
	public void delete_point()
	{
		if(success_flag)
			return;
		
		movement_item_container mic=manager.designed_move.move;
		if(mic.movement==null){
			ci.request_response.print(0);
			return;
		}
		if(mic.movement.length<=1){
			mic.movement=null;
			ci.request_response.print(0);
			return;
		}
		movement_item tmp[]=new movement_item[mic.movement.length-1];
		for(int i=0,ni=tmp.length;i<ni;i++)
			tmp[i]=mic.movement[i];
		mic.movement=tmp;
		ci.request_response.print(mic.movement.length);
	}
	
	public void set_location()
	{
		if(success_flag)
			return;
		
		component design_component=ek.component_cont.get_component(parameter.design_component_id);
		if(design_component!=null)
			if(parameter.design_location!=null)
				design_component.modify_location(parameter.design_location,ek.component_cont);
		if(manager.designed_move.move.movement==null)
			ci.request_response.print(0);
		else
			ci.request_response.print(manager.designed_move.move.movement.length);
	}
	public void get_location()
	{
		if(success_flag)
			return;
		
		component design_component=ek.component_cont.get_component(parameter.design_component_id);
		
		movement_location_to_move_rotate p=new movement_location_to_move_rotate(design_component.move_location);
		
		if(design_component!=null) {
			String str;
			if((str=ci.request_response.get_parameter("coordinate"))!=null){
				if(str.compareTo("view")==0)
					p=new movement_location_to_move_rotate(
						design_component.absolute_location,design_component.move_location,
						ci.display_camera_result.cam.eye_component.absolute_location);
				else if(str.compareTo("camera")==0)
					p=new movement_location_to_move_rotate(
						design_component.absolute_location,design_component.move_location,
						ci.display_camera_result.cam.eye_component.absolute_location.multiply(new point(0,0,0)));
			}
		}
		ci.request_response.println("{");
		
		if(manager.designed_move.move.movement==null)
			ci.request_response.println("\"number\" : 0,");
		else
			ci.request_response.println("\"number\" : ",manager.designed_move.move.movement.length+",");
		ci.request_response.println("\"mx\" : ",
				format_change.double_to_decimal_string(p.m_x,ek.scene_par.display_precision)+",");
		ci.request_response.println("\"my\" : ",
				format_change.double_to_decimal_string(p.m_y,ek.scene_par.display_precision)+",");
		ci.request_response.println("\"mz\" : ",
				format_change.double_to_decimal_string(p.m_z,ek.scene_par.display_precision)+",");
		ci.request_response.println("\"rx\" : ",
				format_change.double_to_decimal_string(p.r_x,ek.scene_par.display_precision)+",");
		ci.request_response.println("\"ry\" : ",
				format_change.double_to_decimal_string(p.r_y,ek.scene_par.display_precision)+",");
		ci.request_response.println("\"rz\" : ",
				format_change.double_to_decimal_string(p.r_z,ek.scene_par.display_precision));
		ci.request_response.println("}");
	}
	
	private boolean success_flag;
	
	public movement_desinger(engine_kernel my_ek,client_information my_ci,movement_manager my_manager)
	{
		success_flag=true;
		ek=my_ek;
		ci=my_ci;
		manager=my_manager;
		parameter=new movement_design_parameter(ek,ci);
		component design_component=ek.component_cont.get_component(parameter.design_component_id);
		if(design_component==null)
			return;
		if((ek.component_cont.root_component.component_id)==(parameter.design_component_id))
			return;
		set_designed_move();
		success_flag=false;
		return;
	}
}
