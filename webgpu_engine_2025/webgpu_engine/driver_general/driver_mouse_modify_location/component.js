function send_to_webserver(component_id,event_operation,scene)
{
	var ep=scene.component_event_processor[component_id];
	var operate_component_id=(scene.event_component.mouse.function_id>=200)?(ep.selected_component_id)
			:(scene.camera.camera_object_parameter[ep.target_data.camera_id].component_id);		
			
	var call_parameter=[
			["event_operation",		event_operation],
			["operate_component_id",operate_component_id]
	];
	if(event_operation!="mousedown")
		scene.caller.call_server_component(component_id,0,call_parameter);
	else
		scene.caller.call_server_component(component_id,0,call_parameter,"json").
			then(
				function(response_data)
				{
					ep.selected_component_id=response_data;
				});
}
function send_location_to_webserver(command_str,component_id,scene,do_delay_flag)
{
	var current_time=(new Date()).getTime();
	var ep=scene.component_event_processor[component_id];

	if(do_delay_flag)
		if((current_time-ep.send_mousemove_time)<scene.parameter.delay_time_length)
			return;
	ep.send_mousemove_time=current_time;

	var operate_component_id=(scene.event_component.mouse.function_id>=200)?(ep.selected_component_id)
			:(scene.camera.camera_object_parameter[ep.target_data.camera_id].component_id);
				
	var str="";
					
	str+="&distance="		+(scene.camera.camera_object_parameter[ep.target_data.camera_id].distance.toString());
	str+="&half_fovy_tanl=" +(scene.camera.camera_object_parameter[ep.target_data.camera_id].half_fovy_tanl.toString());
					
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
	var camera_component_id			=scene.camera.camera_object_parameter[ep.target_data.camera_id].component_id;
	var camera_component_location	=scene.component_location_data.get_component_location(camera_component_id);
	
	var center_point				=scene.computer.caculate_coordinate(camera_component_location,0,0,0);
	var	selected_component_location	=(ep.selected_component_id<0)?camera_component_location:
								(scene.component_location_data.get_component_location(ep.selected_component_id));
	var p,pl,matrix;

	switch(scene.event_component.mouse.function_id%100){
	default:
	case 0:
		return [world_point_0,world_point_1];
	case 1:
		pl		=scene.computer.create_plane_from_two_point(
					center_point,scene.computer.add_operation(center_point,[1,0,0,1]));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 2:
		pl		=scene.computer.create_plane_from_two_point(
					center_point,scene.computer.add_operation(center_point,[0,1,0,1]));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 3:
		pl		=scene.computer.create_plane_from_two_point(
					center_point,scene.computer.add_operation(center_point,[0,0,1,1]));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 4:
		p=scene.computer.sub_operation(
				scene.computer.caculate_coordinate(selected_component_location,1,0,0),
				scene.computer.caculate_coordinate(selected_component_location,0,0,0));
		pl		=scene.computer.create_plane_from_two_point(
						center_point,scene.computer.add_operation(center_point,p));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 5:
		p=scene.computer.sub_operation(
				scene.computer.caculate_coordinate(selected_component_location,0,1,0),
				scene.computer.caculate_coordinate(selected_component_location,0,0,0));
		pl		=scene.computer.create_plane_from_two_point(center_point,scene.computer.add_operation(center_point,p));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 6:
		p=scene.computer.sub_operation(
				scene.computer.caculate_coordinate(selected_component_location,0,0,1),
				scene.computer.caculate_coordinate(selected_component_location,0,0,0));
		pl		=scene.computer.create_plane_from_two_point(
					center_point,scene.computer.add_operation(center_point,p));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 7:
		matrix			=scene.computer.matrix_negative(camera_component_location);
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		world_point_0[1]=0;
		world_point_1[1]=0;
		matrix			=camera_component_location;
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		return [world_point_0,world_point_1];
	case 8:
		matrix			=scene.computer.matrix_negative(camera_component_location);
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		world_point_0[0]=0;
		world_point_1[0]=0;
		matrix			=camera_component_location;
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		return [world_point_0,world_point_1];
	case 9:
		p=scene.computer.sub_operation(
				scene.computer.caculate_coordinate(camera_component_location,0,0,1),
				scene.computer.caculate_coordinate(camera_component_location,0,0,0));
		pl		=scene.computer.create_plane_from_two_point(center_point,scene.computer.add_operation(center_point,p));
		matrix	=scene.computer.project_to_plane_location(pl[0],pl[1],pl[2],pl[3],1.0);
		return	[	scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]),
					scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2])
				];
	case 10:
		world_point_0[1]=world_point_1[1];
		world_point_0[2]=world_point_1[2];
		return [world_point_0,world_point_1];
	case 11:
		world_point_0[0]=world_point_1[0];
		world_point_0[2]=world_point_1[2];
		return [world_point_0,world_point_1];
	case 12:
		world_point_0[0]=world_point_1[0];
		world_point_0[1]=world_point_1[1];
		return [world_point_0,world_point_1];
	case 13:
		matrix			=scene.computer.matrix_negative(selected_component_location);
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		world_point_0[1]=world_point_1[1];
		world_point_0[2]=world_point_1[2];
		matrix			=selected_component_location;
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		return [world_point_0,world_point_1];
	case 14:
		matrix			=scene.computer.matrix_negative(selected_component_location);
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		world_point_0[0]=world_point_1[0];
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
		world_point_0[1]=world_point_1[1];
		matrix			=selected_component_location;
		world_point_0	=scene.computer.caculate_coordinate(matrix,world_point_0[0],world_point_0[1],world_point_0[2]);
		world_point_1	=scene.computer.caculate_coordinate(matrix,world_point_1[0],world_point_1[1],world_point_1[2]);
		return [world_point_0,world_point_1];
	}
}
function camera_move(ep,world_point_0,world_point_1,scene)
{
	var camera_component_id		=scene.camera.camera_object_parameter[ep.target_data.camera_id].component_id;
	var global_location			=scene.component_location_data.get_component_location(camera_component_id);

	var local_location			=scene.computer.matrix_negative(global_location);
	var local_point_0			=scene.computer.caculate_coordinate(local_location,
									world_point_0[0],world_point_0[1],world_point_0[2]);
	var local_point_1			=scene.computer.caculate_coordinate(local_location,
									world_point_1[0],world_point_1[1],world_point_1[2]);
	var difference				=scene.computer.sub_operation(local_point_1,local_point_0);
	var modify_location			=scene.computer.create_move_rotate_matrix(
									difference[0],difference[1],difference[2],0,0,0);
		
	var move_location			=scene.component_location_data.get_component_move_location(camera_component_id);
	var	new_move_location		=scene.computer.matrix_multiplication(move_location,modify_location);
		
	scene.component_location_data.set_component_move_location(camera_component_id,new_move_location);
	
	return true;
}
function component_move(ep,world_point_0,world_point_1,scene)
{
	if(ep.selected_component_id<0)
		return false;
	var global_location		=scene.component_location_data.get_component_location(ep.selected_component_id);
	var	local_location		=scene.computer.matrix_negative(global_location);
	var local_point_0		=scene.computer.caculate_coordinate(local_location,
								world_point_0[0],world_point_0[1],world_point_0[2]);
	var local_point_1		=scene.computer.caculate_coordinate(local_location,
								world_point_1[0],world_point_1[1],world_point_1[2]);
	var difference			=scene.computer.sub_operation(local_point_1,local_point_0);
	var modify_location		=scene.computer.create_move_rotate_matrix(
								difference[0],difference[1],difference[2],0,0,0);
	var old_move_location	=scene.component_location_data.get_component_move_location(ep.selected_component_id);
	var	new_move_location	=scene.computer.matrix_multiplication(old_move_location,modify_location);
			
	scene.component_location_data.set_component_move_location(ep.selected_component_id,new_move_location);

	return true;
}
function left_location_for_rotation(world_point_0,world_point_1,center_point,scene)
{
	var world_dir_0=scene.computer.sub_operation(world_point_0,center_point);
	if(scene.computer.dot_operation(world_dir_0,world_dir_0)<scene.computer.min_value2())
		return null;
	world_dir_0		=scene.computer.expand_operation(world_dir_0,1.0);
	world_point_0	=scene.computer.add_operation(center_point,world_dir_0);
		
	var world_dir_1=scene.computer.sub_operation(world_point_1,center_point);
	if(scene.computer.dot_operation(world_dir_1,world_dir_1)<scene.computer.min_value2())
		return null;
	world_dir_1		=scene.computer.expand_operation(world_dir_1,1.0);
	world_point_1	=scene.computer.add_operation(center_point,world_dir_1);
	
	var aix_dir=scene.computer.cross_operation(world_dir_0,world_dir_1);
	if(scene.computer.dot_operation(aix_dir,aix_dir)<scene.computer.min_value2())
		return null;
	aix_dir=scene.computer.expand_operation(aix_dir,1.0);
	var aix_point=scene.computer.add_operation(center_point,aix_dir);

	var right_dir_0=scene.computer.cross_operation(aix_dir,world_dir_0);
	if(scene.computer.dot_operation(right_dir_0,right_dir_0)<scene.computer.min_value2())
		return null;
	right_dir_0=scene.computer.expand_operation(right_dir_0,1.0);
	var right_point_0=scene.computer.add_operation(center_point,right_dir_0);
	
	var right_dir_1=scene.computer.cross_operation(aix_dir,world_dir_1);
	if(scene.computer.dot_operation(right_dir_1,right_dir_1)<scene.computer.min_value2())
		return null;
	right_dir_1=scene.computer.expand_operation(right_dir_1,1.0);
	var right_point_1=scene.computer.add_operation(center_point,right_dir_1);

	var loca_0=scene.computer.create_point_location(center_point,aix_point,world_point_0,right_point_0);
	var loca_1=scene.computer.create_point_location(center_point,aix_point,world_point_1,right_point_1);

	return scene.computer.matrix_multiplication(loca_1,scene.computer.matrix_negative(loca_0));
}
function camera_rotate(ep,world_point_0,world_point_1,center_point,scene)
{
	var left_adjust;
		
	if((left_adjust=left_location_for_rotation(world_point_0,world_point_1,center_point,scene))==null)
		return false;

	var camera_component_id		=scene.camera.camera_object_parameter[ep.target_data.camera_id].component_id;
	var old_location			=scene.component_location_data.get_component_location(camera_component_id);
	
	var negitave_old_location	=scene.computer.matrix_negative(old_location);
	
	var new_location			=scene.computer.matrix_multiplication(left_adjust,old_location);
	var right_modify_location	=scene.computer.matrix_multiplication(negitave_old_location,new_location);
	var move_location			=scene.component_location_data.get_component_move_location(camera_component_id);
	var new_move_location		=scene.computer.matrix_multiplication(move_location,right_modify_location);
	
	scene.component_location_data.set_component_move_location(camera_component_id,new_move_location);
	
	return true;
}
function component_rotate(ep,world_point_0,world_point_1,center_point,scene)
{
	var left_adjust;
		
	if(ep.selected_component_id<0)
		return false;

	if((left_adjust=left_location_for_rotation(world_point_0,world_point_1,center_point,scene))==null)
		return false;
			
	var	old_location			=scene.component_location_data.get_component_location(ep.selected_component_id);
	var negitave_old_location	=scene.computer.matrix_negative(old_location);
	var new_location			=scene.computer.matrix_multiplication(left_adjust,old_location);
	var right_modify_location	=scene.computer.matrix_multiplication(negitave_old_location,new_location);
	var move_location			=scene.component_location_data.get_component_move_location(ep.selected_component_id);
	var new_move_location		=scene.computer.matrix_multiplication(move_location,right_modify_location);
	scene.component_location_data.set_component_move_location(ep.selected_component_id,new_move_location);

	return true;
}

function change_matrix(component_id,vp_0,vp_1,scene)
{
	var ep=scene.component_event_processor[component_id];
	var camera_parameter	=scene.camera.camera_object_parameter[ep.target_data.camera_id];
	var camera_component_id	=camera_parameter.component_id;
	var camera_distance		=camera_parameter.distance;
	var half_fovy_tanl		=camera_parameter.half_fovy_tanl;
	var project_matrix		=scene.camera.compute_camera_data(ep.target_data);
	
	var camera_component_location=scene.component_location_data.get_component_location(camera_component_id);
	var center_point		=scene.computer.caculate_coordinate(camera_component_location,0,0,0);
	var eye_point			=scene.computer.caculate_coordinate(camera_component_location,0,0,camera_distance);
	
	var world_point_0,world_point_1;
	
	switch(Math.floor(scene.event_component.mouse.function_id/100)){
	default:
		return;
	case 0:	//camera_rotate
	case 2:	//component_rotate
		if(ep.rotate_type_flag){
			var middle_point	=scene.computer.caculate_coordinate(camera_component_location,0,0,camera_distance/2.0);
			var point_depth		=scene.computer.caculate_coordinate(project_matrix.matrix,
										middle_point[0],middle_point[1],middle_point[2])[2];
			world_point_0		=scene.computer.caculate_coordinate(
										project_matrix.negative_matrix,vp_0[0],vp_0[1],point_depth);
			world_point_1		=scene.computer.caculate_coordinate(
										project_matrix.negative_matrix,vp_1[0],vp_1[1],point_depth);
			var world_point_diff=scene.computer.scale_operation(
										scene.computer.sub_operation(world_point_1,world_point_0),
										ep.mouse_rotate_scale/half_fovy_tanl);
			world_point_1		=scene.computer.add_operation(world_point_0,world_point_diff);
			break;
		}
	case 1:	//camera_move
	case 3:	//component_move
		var point_depth=scene.computer.caculate_coordinate(project_matrix.matrix,
										center_point[0],center_point[1],center_point[2])[2];
		world_point_0	=scene.computer.caculate_coordinate(project_matrix.negative_matrix,
										vp_0[0],vp_0[1],point_depth);
		world_point_1	=scene.computer.caculate_coordinate(project_matrix.negative_matrix,
										vp_1[0],vp_1[1],point_depth);
		break;
	}
	
	var world_point	=world_point_modifier(component_id,world_point_0,world_point_1,scene);
	var rotate_center_point=ep.rotate_type_flag?center_point:eye_point;
	switch(Math.floor(scene.event_component.mouse.function_id/100)){
	case 0:	//camera_rotate
		camera_rotate	(ep,world_point[0],world_point[1],rotate_center_point,scene);
		break;
	case 1:	//camera_move
		camera_move		(ep,world_point[0],world_point[1],scene);
		break;
	case 2:	//component_rotate
		component_rotate(ep,world_point[1],world_point[0],rotate_center_point,scene);
		break;
	case 3:	//component_move
		component_move	(ep,world_point[1],world_point[0],scene);
		break;
	default:
		break;
	}
}

function caculate_low_precision_scale(event)
{
	var index_id=0;
	
	if(event.shiftKey)
		index_id+=1;
	if(event.ctrlKey)
		index_id+=2;
	if(event.altKey)
		index_id+=4;
	
	var my_scale_array=[
		1.0,
		2.0,
		4.0,
		8.0,
		16.0,
		32.0,
		64.0,
		128.0
	];
	return 1.0/my_scale_array[index_id];
}
function mousedown(event,component_id,scene)
{
	var ep=scene.component_event_processor[component_id];
	if(ep.target_data==null)
		return false;
		
	switch(event.button){
	case 0:
		ep.mouse_up_flag=false;
		ep.last_point=[scene.view.x,scene.view.y,0,1];
		send_to_webserver(component_id,"mousedown",scene);
		break;
	case 2:	
		if(event.ctrlKey||event.shiftKey||event.altKey){
			scene.caller.call_server_component(ep.movement_component_name,"all",
				[["operation","design"],["move_method","listdesignbuffer"]],"json").
			then(
				function(response_data)
				{
					if(response_data==null)
						return;
					if(response_data.movement.length<=0)
						return;
					var p=ep.movement_abstract_menu_component_name;
					if((p=scene.operate_component.get_component_event_processor(p))!=null)
						p.active_list(response_data,scene.operate_component.last_operate_component_id,scene);
				});
		}else{
			var value;
			if(scene.pickup.component_id>=0)
				if((value=prompt("输入标注文字"))!=null)
					if((value=value.trim()).length>0)
						scene.caller.call_server_component(ep.mark_component_name,"all",
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
	if(ep.target_data==null)
		return false;

	scene.caller.call_server_component(ep.movement_component_name,"all",
		[["operation","virtual_mount"],["virtual_mount","terminate_follow"]]);
	switch(event.button){
	case 0:
		ep.mouse_up_flag=true;
		send_location_to_webserver("mouseup",component_id,scene,false);
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
	if(ep.target_data==null)
		return false;
	if(ep.mouse_up_flag)
		return false;
	var dx=scene.view.x-ep.last_point[0];
	var dy=scene.view.y-ep.last_point[1];
	if((dx*dx+dy*dy)<(scene.computer.min_value2()))
		return false;
	var this_point=[scene.view.x,scene.view.y,0,1];
	var my_low_precision_scale=caculate_low_precision_scale(event);
	change_matrix(component_id,this_point,
		[
			this_point[0]+my_low_precision_scale*(ep.last_point[0]-this_point[0]),
			this_point[1]+my_low_precision_scale*(ep.last_point[1]-this_point[1]),
			0,1
		],scene);
	ep.last_point=this_point;
		
	send_location_to_webserver("mousemove",component_id,scene,true);
		
	return false;
}
function dblclick(event,component_id,scene)
{
	var ep=scene.component_event_processor[component_id];
	if(ep.target_data==null)
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
	var mouse_wheel_number=0;
	var ep=scene.component_event_processor[component_id];
	if(ep.target_data==null)
		return false;
	
	var camera_component_id	=scene.camera.camera_object_parameter[ep.target_data.camera_id].component_id;
	ep.move_loca			=scene.component_location_data.component[camera_component_id].move_matrix;
	
	if(typeof(event.wheelDelta)=="number")
			mouse_wheel_number+=event.wheelDelta/2000.0;			//for chrome,opera
	else if(typeof(event.detail)=="number")
			mouse_wheel_number-=event.detail/50.0;					//for firefox
	else
			return false;

	mouse_wheel_number*=caculate_low_precision_scale(event);

	var p=scene.camera.camera_object_parameter[ep.target_data.camera_id];
	if(ep.change_type_flag)
		p.distance		/=Math.exp(mouse_wheel_number);
	else
		p.half_fovy_tanl/=Math.exp(mouse_wheel_number);
	p.should_update_buffer_data_flag=true;
		
	send_location_to_webserver("mousemove",component_id,scene,true);
	
	return false;
}
function mobile_phone(event,component_id,scene)
{
	var ep=scene.component_event_processor[component_id];
	
	if(ep.target_data==null)
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
			send_location_to_webserver("mousemove",component_id,scene,true);
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
			var p=scene.camera.camera_object_parameter[ep.target_data.camera_id];
			if(ep.change_type_flag)
				p.distance		 /=Math.exp(mouse_wheel_number);
			else
				p.half_fovy_tanl/=Math.exp(mouse_wheel_number);
			p.should_update_buffer_data_flag=true;
			
			send_location_to_webserver("mousemove",component_id,scene,false);
		case "touchstart":
			ep.touchstart_distance_2=dx*dx+dy*dy;
			break;
		}
		break;
	}
	return false;		
}
function construct_component_driver(component_ids,init_data,create_data,part_object,part_driver,render_driver,scene)
{
	this.component_ids=component_ids;
	
	var ep=new Object();
	
	ep.change_type_flag			=true;
	ep.mouse_rotate_scale		= create_data[0];
	ep.rotate_type_flag			=(create_data[1]>0.5)?true:false;
	ep.mark_component_name		= create_data[2];
	ep.movement_component_name	= create_data[3];
	ep.movement_abstract_menu_component_name=create_data[4];
	
	ep.send_mousemove_time	=0;
	ep.mouse_up_flag		=true;
	
	ep.selected_component_id=-1;
	ep.target_data			=null;
			
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
	if(typeof(old_ep=scene.component_event_processor[this.component_ids.component_id])=="object")
		ep=Object.assign(old_ep,ep);
		
	scene.component_event_processor[this.component_ids.component_id]=ep;

	this.draw_component=function(method_data,render_parameter,
			target_data,part_object,part_driver,render_driver,scene)	
	{
		var ep=scene.component_event_processor[this.component_ids.component_id];
		ep.target_data=target_data;
	}
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)  
	{
		var ep=scene.component_event_processor[this.component_ids.component_id];
		ep.change_type_flag=(buffer_data_item>0.5)?true:false;
	}
};
