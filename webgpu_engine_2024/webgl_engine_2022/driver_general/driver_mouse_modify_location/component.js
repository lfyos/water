function send_to_webserver(component_id,event_operation,scene)
{
	var ep=scene.component_event_processor[component_id];
	var camera_component_id	=scene.camera.camera_object_parameter[ep.render_data.camera_id].component_id;
	var operate_component_id=((scene.event_component.mouse.function_id>=200)&&(ep.selected_component_id>=0))
					?(ep.selected_component_id):(camera_component_id);
	
	if(event_operation!="mousedown"){			
		scene.caller.call_server_component(component_id,0,
		[
			["event_operation",		event_operation],
			["operate_component_id",operate_component_id]
		]);
		return;
	}
	
	scene.caller.call_server_component(component_id,0,
		[
			["event_operation",		event_operation],
			["operate_component_id",operate_component_id]
		],"json").
	then(
		function(response_data)
		{
			if(response_data==null)
				return;
			if(event_operation!="mousedown")
				return;
			ep.selected_component_id=response_data;
		});
}

function send_location_to_webserver(command_str,component_id,scene,do_delay_flag)
{
	var ep=scene.component_event_processor[component_id];
	var camera_component_id	=scene.camera.camera_object_parameter[ep.render_data.camera_id].component_id;
	var operate_component_id=((scene.event_component.mouse.function_id>=200)&&(ep.selected_component_id>=0))
				?(ep.selected_component_id):(camera_component_id);
	var current_time=(new Date()).getTime();
	if(do_delay_flag)
		if((current_time-ep.send_mousemove_time)<scene.modifier_time_parameter.delay_time_length)
			return;
	ep.send_mousemove_time=current_time;

	var str="";
					
	str+="&distance="		+(scene.camera.camera_object_parameter[ep.render_data.camera_id].distance.toString());
	str+="&half_fovy_tanl=" +(scene.camera.camera_object_parameter[ep.render_data.camera_id].half_fovy_tanl.toString());
					
	str+="&data=";
		
	var move_loca=scene.component_location_data.get_component_move_location(operate_component_id);
	for(var i=0,ni=move_loca.length;i<ni;i++)
		str+=((i==0)?"":",")+(move_loca[i].toString());

	if(typeof(ep.clip_plane_modification)=="undefined")
		ep.clip_plane_modification=0.0;
	
	str+="&clip_plane_modification="+ep.clip_plane_modification.toString();
	ep.clip_plane_modification=0.0;
		
	send_to_webserver(component_id,command_str+str,scene);
}

function world_point_modifier(component_id,world_point_0,world_point_1,scene)
{
	var ep							=scene.component_event_processor[component_id];
	var camera_component_id			=scene.camera.camera_object_parameter[ep.render_data.camera_id].component_id;
	var camera_component_location	=scene.component_location_data.get_component_location(camera_component_id);
	var center_point				=scene.computer.caculate_coordinate(camera_component_location,0,0,0);
	var	selected_component_location	=camera_component_location;

	var p,pl,matrix;
		
	if(ep.selected_component_id>=0)
		selected_component_location=scene.component_location_data.get_component_location(ep.selected_component_id);
		
	switch(scene.event_component.mouse.function_id%100){
	case 2:
		pl		=scene.computer.create_plane_from_two_point(
					center_point,scene.computer.add_operation(center_point,[1,0,0,1]));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 3:
	case 50:
	case 51:
		pl		=scene.computer.create_plane_from_two_point(
					center_point,scene.computer.add_operation(center_point,[0,1,0,1]));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 4:
		pl		=scene.computer.create_plane_from_two_point(
					center_point,scene.computer.add_operation(center_point,[0,0,1,1]));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 5:
		p=scene.computer.sub_operation(
				scene.computer.caculate_coordinate(selected_component_location,1,0,0),
				scene.computer.caculate_coordinate(selected_component_location,0,0,0));
		pl		=scene.computer.create_plane_from_two_point(
						center_point,scene.computer.add_operation(center_point,p));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 6:
		p=scene.computer.sub_operation(
				scene.computer.caculate_coordinate(selected_component_location,0,1,0),
				scene.computer.caculate_coordinate(selected_component_location,0,0,0));
		pl		=scene.computer.create_plane_from_two_point(center_point,scene.computer.add_operation(center_point,p));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 7:
		p=scene.computer.sub_operation(
				scene.computer.caculate_coordinate(selected_component_location,0,0,1),
				scene.computer.caculate_coordinate(selected_component_location,0,0,0));
		pl		=scene.computer.create_plane_from_two_point(
					center_point,scene.computer.add_operation(center_point,p));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 8:
		matrix			=scene.computer.matrix_negative(camera_component_location);
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		world_point_0[1]=0;
		world_point_1[1]=0;
		matrix			=camera_component_location;
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		return [world_point_0,world_point_1];
	case 9:
		matrix			=scene.computer.matrix_negative(camera_component_location);
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		world_point_0[0]=0;
		world_point_1[0]=0;
		matrix			=camera_component_location;
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		return [world_point_0,world_point_1];
	case 10:
		p=scene.computer.sub_operation(
				scene.computer.caculate_coordinate(camera_component_location,0,0,1),
				scene.computer.caculate_coordinate(camera_component_location,0,0,0));
		pl		=scene.computer.create_plane_from_two_point(center_point,scene.computer.add_operation(center_point,p));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 11:
		world_point_0[1]=world_point_1[1];
		world_point_0[2]=world_point_1[2];
		return [world_point_0,world_point_1];
	case 12:
		world_point_0[0]=world_point_1[0];
		world_point_0[2]=world_point_1[2];
		return [world_point_0,world_point_1];
	case 13:
		world_point_0[0]=world_point_1[0];
		world_point_0[1]=world_point_1[1];
		return [world_point_0,world_point_1];
	case 14:
		matrix			=scene.computer.matrix_negative(selected_component_location);
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		world_point_0[1]=world_point_1[1];
		world_point_0[2]=world_point_1[2];
		matrix			=selected_component_location;
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		return [world_point_0,world_point_1];
	case 15:
		matrix			=scene.computer.matrix_negative(selected_component_location);
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		world_point_0[0]=world_point_1[0];
		world_point_0[2]=world_point_1[2];
		matrix			=selected_component_location;
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		return [world_point_0,world_point_1];
	case 16:
		matrix			=scene.computer.matrix_negative(selected_component_location);
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		world_point_0[0]=world_point_1[0];
		world_point_0[1]=world_point_1[1];
		matrix			=selected_component_location;
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		return [world_point_0,world_point_1];
	default:
		return [world_point_0,world_point_1];
	}
}

function camera_move(ep,world_point_0,world_point_1,scene)
{
	var camera_component_id			=scene.camera.camera_object_parameter[ep.render_data.camera_id].component_id;
	var camera_component_location	=scene.component_location_data.get_component_location(camera_component_id);
	var loca			=scene.computer.matrix_negative(camera_component_location);
	var local_point_0	=scene.computer.caculate_coordinate(loca,world_point_0[0],world_point_0[1],world_point_0[2]);
	var local_point_1	=scene.computer.caculate_coordinate(loca,world_point_1[0],world_point_1[1],world_point_1[2]);
	var diff			=scene.computer.sub_operation(local_point_1,local_point_0);
		loca			=scene.computer.create_move_rotate_matrix(diff[0],diff[1],diff[2],0,0,0);
		
	var move_loca		=scene.component_location_data.get_component_move_location(camera_component_id);
		move_loca		=scene.computer.matrix_multiplication(move_loca,loca);
		scene.component_location_data.set_component_move_location(camera_component_id,move_loca);
		
		return true;
}
function component_move(ep,world_point_0,world_point_1,scene)
{
	if(ep.selected_component_id<0)
		return false;
	var global_loca		=scene.component_location_data.get_component_location(ep.selected_component_id);
	var	local_loca		=scene.computer.matrix_negative(global_loca);
	var local_point_0	=scene.computer.caculate_coordinate(local_loca,world_point_0[0],world_point_0[1],world_point_0[2]);
	var local_point_1	=scene.computer.caculate_coordinate(local_loca,world_point_1[0],world_point_1[1],world_point_1[2]);
	var modify_loca		=scene.computer.create_move_rotate_matrix(
								local_point_0[0]-local_point_1[0],
								local_point_0[1]-local_point_1[1],
								local_point_0[2]-local_point_1[2],
								0,0,0);
	var move_loca		=scene.component_location_data.get_component_move_location(ep.selected_component_id);
	var	new_move_loca	=scene.computer.matrix_multiplication(move_loca,modify_loca);
			
	scene.component_location_data.set_component_move_location(ep.selected_component_id,new_move_loca);
		
	return true;
}
	
function left_location_for_rotation(world_point_0,world_point_1,center_point,scene)
{
	var diff;
	
	var world_dir_0		=scene.computer.expand_operation(scene.computer.sub_operation(world_point_0,center_point),1.0);
	var world_dir_1		=scene.computer.expand_operation(scene.computer.sub_operation(world_point_1,center_point),1.0);
		world_point_0	=scene.computer.add_operation(center_point,world_dir_0);
		world_point_1	=scene.computer.add_operation(center_point,world_dir_1);

	diff=scene.computer.sub_operation(world_point_0,world_point_1);
	if(scene.computer.dot_operation(diff,diff)<scene.computer.min_value2())
		return null;
	diff=scene.computer.sub_operation(center_point,world_point_0);
	if(scene.computer.dot_operation(diff,diff)<scene.computer.min_value2())
		return null;
	diff=scene.computer.sub_operation(center_point,world_point_1);
	if(scene.computer.dot_operation(diff,diff)<scene.computer.min_value2())
		return null;
	
	var aix_dir			=scene.computer.expand_operation(
							scene.computer.cross_operation(world_dir_0,world_dir_1),1.0);
	var aix_point		=scene.computer.add_operation(center_point,aix_dir);
		
	var right_dir_0		=scene.computer.expand_operation(
							scene.computer.cross_operation(aix_dir,world_dir_0),1.0);
	var right_point_0	=scene.computer.add_operation(center_point,right_dir_0);
		
	var right_dir_1		=scene.computer.expand_operation(
							scene.computer.cross_operation(aix_dir,world_dir_1),1.0);
	var right_point_1	=scene.computer.add_operation(center_point,right_dir_1);

	var loca_0			=scene.computer.create_point_location(center_point,aix_point,world_point_0,right_point_0);
	var loca_1			=scene.computer.create_point_location(center_point,aix_point,world_point_1,right_point_1);
		
	return	scene.computer.matrix_multiplication(loca_1,scene.computer.matrix_negative(loca_0));
}
	
function camera_rotate(ep,world_point_0,world_point_1,center_point,scene)
{
	var left_adjust;
		
	if((left_adjust=left_location_for_rotation(world_point_0,world_point_1,center_point,scene))==null)
		return false;
	
	var camera_component_id			=scene.camera.camera_object_parameter[ep.render_data.camera_id].component_id;	
	var camera_component_location	=scene.component_location_data.get_component_location(camera_component_id);
	var new_location				=scene.computer.matrix_multiplication(left_adjust,camera_component_location);
	var loca						=scene.computer.matrix_multiplication(
											scene.computer.matrix_negative(camera_component_location),new_location);
	var move_loca					=scene.component_location_data.get_component_move_location(camera_component_id);
	move_loca						=scene.computer.matrix_multiplication(move_loca,loca);
	scene.component_location_data.set_component_move_location(camera_component_id,move_loca);
	return true;
}
function component_rotate(ep,world_point_0,world_point_1,center_point,scene)
{
	var left_adjust;
		
	if(ep.selected_component_id<0)
		return false;

	if((left_adjust=left_location_for_rotation(world_point_0,world_point_1,center_point,scene))==null)
		return false;
			
	var	selected_component_location	=scene.component_location_data.get_component_location(ep.selected_component_id);
			
	var new_location	=scene.computer.matrix_multiplication(left_adjust,selected_component_location);
	var loca			=scene.computer.matrix_multiplication(
								scene.computer.matrix_negative(selected_component_location),new_location);
	var move_loca		=scene.component_location_data.get_component_move_location(ep.selected_component_id);
	move_loca			=scene.computer.matrix_multiplication(move_loca,scene.computer.matrix_negative(loca));
	scene.component_location_data.set_component_move_location(ep.selected_component_id,move_loca);

	return true;
}
function change_matrix(component_id,vp_0,vp_1,scene)
{
	var ep=scene.component_event_processor[component_id];
	var camera_distance	=scene.camera.camera_object_parameter[ep.render_data.camera_id].distance;
	var half_fovy_tanl=scene.camera.camera_object_parameter[ep.render_data.camera_id].half_fovy_tanl;
	var project_matrix	=scene.camera.compute_camera_data(ep.render_data);
	
	var camera_component_id	=scene.camera.camera_object_parameter[ep.render_data.camera_id].component_id;	
	var camera_component_location=scene.component_location_data.get_component_location(camera_component_id);
	var center_point	=scene.computer.caculate_coordinate(camera_component_location,0,0,0);
	var eye_point		=scene.computer.caculate_coordinate(camera_component_location,0,0,camera_distance);
	
	switch(scene.event_component.mouse.function_id%100){
	case 0:
		vp_1=scene.computer.sub_operation(vp_1,vp_0);
		vp_0=[0,0,0,1];
		break;
	default:
		break;
	}		
	
	var world_point,world_point_0,world_point_1,view_mix_point,world_point_diff;
	
	switch(Math.floor(scene.event_component.mouse.function_id/100)){
	default:
		return;
	case 0:
	case 2:
		if(ep.rotate_type_flag){
			var middle_point=scene.computer.caculate_coordinate(camera_component_location,0,0,camera_distance/2.0);
			view_mix_point	=scene.computer.caculate_coordinate(project_matrix.matrix,middle_point[0],middle_point[1],middle_point[2]);
			world_point_0	=scene.computer.caculate_coordinate(project_matrix.negative_matrix,vp_0[0],vp_0[1],view_mix_point[2]);
			world_point_1	=scene.computer.caculate_coordinate(project_matrix.negative_matrix,vp_1[0],vp_1[1],view_mix_point[2]);
			world_point_diff=scene.computer.sub_operation(world_point_1,world_point_0);
			world_point_diff=scene.computer.scale_operation(world_point_diff,ep.mouse_rotate_scale/half_fovy_tanl);
			world_point_1	=scene.computer.add_operation(world_point_0,world_point_diff);
			break;
		}
	case 1:
	case 3:
		view_mix_point	=scene.computer.caculate_coordinate(project_matrix.matrix,center_point[0],center_point[1],center_point[2]);
		world_point_0	=scene.computer.caculate_coordinate(project_matrix.negative_matrix,vp_0[0],vp_0[1],view_mix_point[2]);
		world_point_1	=scene.computer.caculate_coordinate(project_matrix.negative_matrix,vp_1[0],vp_1[1],view_mix_point[2]);
		break;
	}
	
	world_point	=world_point_modifier(component_id,world_point_0,world_point_1,scene);
	world_point_0	=world_point[0];
	world_point_1	=world_point[1];

	switch(Math.floor(scene.event_component.mouse.function_id/100)){
	case 0:
		if(ep.rotate_type_flag)
			camera_rotate(ep,world_point_0,world_point_1,center_point,scene);
		else if(ep.exchange_point_flag)
			camera_rotate(ep,world_point_1,world_point_0,eye_point,scene);
		else
			camera_rotate(ep,world_point_0,world_point_1,eye_point,scene);
		break;
	case 1:
		if(ep.rotate_type_flag)
			camera_move(ep,world_point_0,world_point_1,scene);
		else if(ep.exchange_point_flag)
			camera_move(ep,world_point_1,world_point_0,scene);
		else
			camera_move(ep,world_point_0,world_point_1,scene);
		break;
	case 2:
		if(ep.rotate_type_flag)
			component_rotate(ep,world_point_0,world_point_1,center_point,scene);
		else if(ep.exchange_point_flag)
			component_rotate(ep,world_point_1,world_point_0,eye_point,scene);
		else
			component_rotate(ep,world_point_0,world_point_1,eye_point,scene);
		break;
	case 3:
		if(ep.rotate_type_flag)
			component_move(ep,world_point_0,world_point_1,scene);
		else if(ep.exchange_point_flag)
			component_move(ep,world_point_1,world_point_0,scene);
		else
			component_move(ep,world_point_0,world_point_1,scene);
		break;
	default:
		break;
	}
}

function mousedown(event,component_id,scene)
{
	var ep=scene.component_event_processor[component_id];
	if(ep.render_data==null)
		return false;
		
	switch(event.button){
	case 0:
		ep.mouse_up_flag=false;
		ep.last_point=[scene.view.x,scene.view.y,0,1];
		send_to_webserver(component_id,"mousedown",scene);
		break;
	case 2:	
		if(event.ctrlKey||event.shiftKey||event.altKey){
			scene.caller.call_server_component(ep.part_init_data.movement_component_name,"all",
				[["operation","design"],["move_method","listdesignbuffer"]],"json").
			then(
				function(response_data)
				{
					if(response_data==null)
						return;
					if(response_data.movement.length<=0)
						return;
					var p=ep.part_init_data.movement_abstract_menu_component_name;
					if((p=scene.operate_component.get_component_event_processor(p))!=null)
						p.active_list(response_data,scene.operate_component.last_operate_component_id,scene);
				});
		}else{
			var value;
			if(scene.pickup.component_id>=0)
				if((value=prompt("输入标注文字"))!=null)
					if((value=value.trim()).length>0)
						scene.caller.call_server_component(ep.part_init_data.mark_component_name,"all",
							[["operation","append"],["value",encodeURIComponent(encodeURIComponent(value))]]);
		}
		break;
	default:
		break;
	}
	
	return false;
}

function mouseup(event,component_id,scene)
{
	var ep=scene.component_event_processor[component_id];
	if(ep.render_data==null)
		return false;

	scene.caller.call_server_component(ep.part_init_data.movement_component_name,"all",
		[["operation","virtual_mount"],["virtual_mount","terminate_follow"]]);
	switch(event.button){
	case 0:
		ep.mouse_up_flag=true;
		send_location_to_webserver("mouseup",ep.component_id,scene,false);
		break;
	case 2:	
		break;
	default:
		break;
	}
	return false;		
}
	
function mousemove(event,component_id,scene)
{
	var ep=scene.component_event_processor[component_id];
	if(ep.render_data==null)
		return false;
	if(ep.mouse_up_flag)
		return false;
	var dx=scene.view.x-ep.last_point[0];
	var dy=scene.view.y-ep.last_point[1];
	if(scene.computer.min_value2()>=(dx*dx+dy*dy))
		return false;
	var this_point=[scene.view.x,scene.view.y,0,1];
		
	if(event.ctrlKey||event.altKey||event.shiftKey)
		ep.last_point=[
			this_point[0]+ep.low_precision_scale*(ep.last_point[0]-this_point[0]),
			this_point[1]+ep.low_precision_scale*(ep.last_point[1]-this_point[1]),
			0,1
		];
			
	change_matrix(component_id,this_point,ep.last_point,scene);
	ep.last_point=this_point;
		
	send_location_to_webserver("mousemove",ep.component_id,scene,true);
		
	return false;
}
function dblclick(event,component_id,scene)
{
	var ep=scene.component_event_processor[component_id];
	if(ep.render_data==null)
		return false;
		
	if(event.button!=0)
		return false;
		
	var event_operation;
	
	if(scene.event_component.mouse.function_id>=200)
		event_operation="dblclick_component";
	else if(event.ctrlKey)
		event_operation=event.shiftKey?"dblclick_origin_no_pickup":"dblclick_origin";
	else
		event_operation=event.shiftKey?"dblclick_view_no_pickup":"dblclick_view";
			
	send_to_webserver(component_id,event_operation,scene);
		
	return false;
}
function mousewheel(event,component_id,scene)
{
	var ep,mouse_wheel_number		=0;
	
	if((ep=scene.component_event_processor[component_id]).render_data==null)
		return false;
		
	var camera_component_id		=scene.camera.camera_object_parameter[ep.render_data.camera_id].component_id;
	ep.move_loca				=scene.component_location_data.component[camera_component_id].move_matrix;
	
	if(typeof(event.wheelDelta)=="number")
			mouse_wheel_number+=event.wheelDelta/2000.0;			//for chrome,opera
	else if(typeof(event.detail)=="number")
			mouse_wheel_number-=event.detail/50.0;					//for firefox
	else
			return false;		
	if(event.ctrlKey||event.altKey||event.shiftKey)	
		mouse_wheel_number*=ep.low_precision_scale;

	if(ep.change_type_flag)
		scene.camera.camera_object_parameter[ep.render_data.camera_id].distance		/=Math.exp(mouse_wheel_number);
	else
		scene.camera.camera_object_parameter[ep.render_data.camera_id].half_fovy_tanl	/=Math.exp(mouse_wheel_number);

	send_location_to_webserver("mousemove",ep.component_id,scene,true);
	
	return false;
}

function mobile_phone(event,component_id,scene)
{
	event.preventDefault();
		
	var ep=scene.component_event_processor[component_id];
	
	if(ep.render_data==null)
		return false;
		
	var x0,y0,x1,y1,dx,dy,btn=scene.canvas;
		
	switch(event.touches.length){
	case 0:
		switch(event.type){
		default:
		case "touchmove":
		case "touchstart":
			break;	
		case "touchend":
			ep.mouse_up_flag=true;
			send_location_to_webserver("mouseup",component_id,scene,false);
			send_to_webserver(component_id,"touchend",scene);
			scene.view.x=0.0;
			scene.view.y=0.0;
			break;
		}
		break;
	case 1:	
		x1=event.touches[0].clientX-btn.offsetLeft;
		y1=btn.clientHeight-(event.touches[0].clientY-btn.offsetTop);	
		x1=2.0*((x1/btn.clientWidth )-0.5);
		y1=2.0*((y1/btn.clientHeight)-0.5);
			
		switch(event.type){
		case "touchstart":
			ep.mouse_up_flag=false;
			ep.last_point=[x1,y1,0,1];
			send_to_webserver(component_id,"mousedown",scene);
			break;
		case "touchmove":
			if(ep.mouse_up_flag)
				break;
			dx=x1-ep.last_point[0];
			dy=y1-ep.last_point[1];	
			if(scene.computer.min_value2()>=(dx*dx+dy*dy))
				break;
				
			change_matrix(component_id,[x1,y1,0,1],ep.last_point,scene);
				
			ep.last_point=[x1,y1,0,1];
			send_location_to_webserver("mousemove",ep.component_id,scene,true);
			break;
		}
		break;
	case 2:
		x1=event.touches[1].clientX-btn.offsetLeft;
		y1=btn.clientHeight-(event.touches[1].clientY-btn.offsetTop);	
		x1=2.0*((x1/btn.clientWidth )-0.5);
		y1=2.0*((y1/btn.clientHeight)-0.5);
			
		x0=event.touches[0].clientX-btn.offsetLeft;
		y0=btn.clientHeight-(event.touches[0].clientY-btn.offsetTop);	
		x0=2.0*((x0/btn.clientWidth )-0.5);
		y0=2.0*((y0/btn.clientHeight)-0.5);
			
		dx=x1-x0;
		dy=y1-y0;
			
		switch(event.type){
		case "touchmove":
			var mouse_wheel_number=0.125*Math.log((dx*dx+dy*dy)/(ep.touchstart_distance_2));
			if(ep.change_type_flag)
				scene.camera.camera_object_parameter[ep.render_data.camera_id].distance		 /=Math.exp(mouse_wheel_number);
			else
				scene.camera.camera_object_parameter[ep.render_data.camera_id].half_fovy_tanl/=Math.exp(mouse_wheel_number);
			send_location_to_webserver("mousemove",ep.component_id,scene,false);
		case "touchstart":
			ep.touchstart_distance_2=dx*dx+dy*dy;
			break;
		}
		break;
	}
	return false;		
}
function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	scene)
{
	var ep=new Object();
	
	ep.part_init_data	=part_driver.part_init_data;
	
	ep.send_mousemove_time	=0;			
	ep.mouse_up_flag		=true;
	
	ep.selected_component_id=-1;
	ep.render_data			=null;
			
	ep.mousedown	=mousedown;
	ep.mouseup		=mouseup;
	ep.mousemove	=mousemove;
	ep.dblclick		=dblclick;
	ep.mousewheel	=mousewheel;
			
	ep.touchstart	=mobile_phone;
	ep.touchend		=mobile_phone;
	ep.touchmove	=mobile_phone;

	ep.last_point=[0,0,0,1];
	ep.touchstart_distance_2=1.0;

	ep.selected_component_id=-1;
			
	ep.start_time=(new Date()).getTime();
	
	var old_ep;
	if(typeof(old_ep=scene.component_event_processor[component_id])=="object")
		ep=Object.assign(old_ep,ep);
		
	scene.component_event_processor[component_id]=ep;

	this.component_id=component_id;
	this.buffer_data=null;
	
	this.draw_component=function(method_data,render_data,
			render_id,part_id,component_id,driver_id,component_render_parameter,
			project_matrix,part_object,part_driver,render_driver,scene)	
	{
		var component_id		=this.buffer_data[0];
		var low_precision_scale	=this.buffer_data[1];
		var mouse_rotate_scale	=this.buffer_data[2];
		var rotate_type_flag	=(this.buffer_data[3]>0.5)?true:false;
		var change_type_flag	=(this.buffer_data[4]>0.5)?true:false;
		var exchange_point_flag	=(this.buffer_data[5]>0.5)?true:false;
			
		var ep=scene.component_event_processor[component_id];
		
		ep.render_data			=render_data;
		
		ep.component_id			=component_id;
		ep.low_precision_scale	=low_precision_scale;
		ep.mouse_rotate_scale	=mouse_rotate_scale;
		ep.rotate_type_flag		=rotate_type_flag;
		ep.change_type_flag		=change_type_flag;
		ep.exchange_point_flag	=exchange_point_flag;
	}
	this.append_component_parameter=function(
			component_id,		driver_id,		render_id,		part_id,
			buffer_data_item,	part_object,	part_driver,	render_driver,	scene)
	{
		this.buffer_data=buffer_data_item;
	}
};
