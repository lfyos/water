async function request_render_data(scene)
{
	function create_request_url(scene)
	{
		var request_url=scene.url_with_channel+"&command=component&method=update_render";

		var min_value=scene.computer.min_value();
		if(Math.abs(scene.view_bak.x-scene.view.x)>min_value){
			scene.view_bak.x=scene.view.x;
			request_url+="&x="+(scene.view.x.toString());
		};
		if(Math.abs(scene.view_bak.y-scene.view.y)>min_value){
			scene.view_bak.y=scene.view.y;
			request_url+="&y="+(scene.view.y.toString());
		};
		
		if(Math.abs(scene.view_bak.main_target_x-scene.view.main_target_x)>min_value){
			scene.view_bak.main_target_x=scene.view.main_target_x;
			request_url+="&mtx="+(scene.view.main_target_x.toString());
		};
		if(Math.abs(scene.view_bak.main_target_y-scene.view.main_target_y)>min_value){
			scene.view_bak.main_target_y=scene.view.main_target_y;
			request_url+="&mty="+(scene.view.main_target_y.toString());
		};
		
		var id,value;

		if(scene.view_bak.canvas_id!=scene.webgpu.current_canvas_id){
			scene.view_bak.canvas_id=scene.webgpu.current_canvas_id;
			request_url+="&canvas="	+scene.webgpu.current_canvas_id.toString();
		};
		if((scene.view_bak.component!=scene.pickup.component_id)||(scene.view_bak.driver!=scene.pickup.driver_id)){
			scene.view_bak.component	=scene.pickup.component_id;
			scene.view_bak.driver		=scene.pickup.driver_id;
			request_url+="&component="	+scene.pickup.component_id.toString();
			request_url+="_"			+scene.pickup.driver_id.toString();
		};
		if(scene.view_bak.primitive_type_id!=(id=scene.pickup.primitive_type_id)){
			scene.view_bak.primitive_type_id=id;
			request_url+="&type="+id.toString();
		};
		if(scene.view_bak.body!=(id=scene.pickup.body_id)){
			scene.view_bak.body=id;
			request_url+="&body="+id.toString();
		};
		if(scene.view_bak.face!=(id=scene.pickup.face_id)){
			scene.view_bak.face=id;
			request_url+="&face="+id.toString();
		};
		if(scene.view_bak.loop!=(id=scene.pickup.loop_id)){
			scene.view_bak.loop=id;
			request_url+="&loop="+id.toString();
		};
		if(scene.view_bak.edge!=(id=scene.pickup.edge_id)){
			scene.view_bak.edge=id;
			request_url+="&edge="+id.toString();
		};
		if(scene.view_bak.primitive!=(id=scene.pickup.primitive_id)){
			scene.view_bak.primitive=id;
			request_url+="&primitive="+id.toString();
		};
		if(scene.view_bak.vertex!=(id=scene.pickup.vertex_id)){
			scene.view_bak.vertex=id;
			request_url+="&vertex="+id.toString();
		};
		if(Math.abs(scene.view_bak.depth-(value=scene.pickup.depth))>min_value){
			scene.view_bak.depth=value;
			request_url+="&depth="+(new Number(value)).toPrecision(6);
		}
		var distance2=0;		
		value=(scene.view_bak.value[0]-scene.pickup.value[0]);	distance2+=Math.abs(value);
		value=(scene.view_bak.value[1]-scene.pickup.value[1]);	distance2+=Math.abs(value);
		value=(scene.view_bak.value[2]-scene.pickup.value[2]);	distance2+=Math.abs(value);
		
		if(distance2>min_value){
			scene.view_bak.value=[scene.pickup.value[0],scene.pickup.value[1],scene.pickup.value[2]];
			request_url+="&value="	+(new Number(scene.pickup.value[0])).toPrecision(6);
			request_url+="_"		+(new Number(scene.pickup.value[1])).toPrecision(6);
			request_url+="_"		+(new Number(scene.pickup.value[2])).toPrecision(6);
		};
		if(scene.vertex_data_downloader.acknowledge_render_part_id!=null){
			request_url+="&acknowledge="+scene.vertex_data_downloader.acknowledge_render_part_id;
			scene.vertex_data_downloader.acknowledge_render_part_id=null;
		};
		if(scene.vertex_data_downloader.response_loaded_length!=(scene.vertex_data_downloader.loaded_buffer_object_data_length)){
			scene.vertex_data_downloader.response_loaded_length=scene.vertex_data_downloader.loaded_buffer_object_data_length;
			request_url+="&loaded_length="+	scene.vertex_data_downloader.loaded_buffer_object_file_number.toString();
			request_url+="_"+ 				scene.vertex_data_downloader.loaded_buffer_object_data_length.toString();
			request_url+="_"+				scene.vertex_data_downloader.loading_render_id.toString();
			request_url+="_"+				scene.vertex_data_downloader.loading_part_id.toString();
		};
		{
			var requesting_number,max_request_number=scene.vertex_data_downloader.max_loading_number;

			requesting_number =scene.vertex_data_downloader.current_loading_mesh_number;
			requesting_number+=scene.vertex_data_downloader.request_render_part_id.length;
			requesting_number+=scene.vertex_data_downloader.buffer_head_request_queue.length;
			request_url+="&requesting_number="+requesting_number+"_"+max_request_number;
		};
		
		var mouse_down_flag=false;
		for(var i=0,ni=scene.event_listener.length;i<ni;i++)
			mouse_down_flag|=scene.event_listener[i].mouse_down_flag;

		request_url+="&precision="+(mouse_down_flag?"false":"true");
		request_url+="&length=";
		request_url+=request_url.length.toString();

		return request_url;
	};

	function parse_target_parameter(response_data,scene)
	{
		for(var i=0,ni=scene.render_buffer_array.length;i<ni;i++)
			scene.render_buffer_array[i].do_render_flag=false;

		for(var i=0,ni=response_data.length;i<ni;){
			var my_render_buffer_id	=response_data[i++];
			var my_data				=response_data[i++];

			while(my_render_buffer_id>=scene.render_buffer_array.length)
				scene.render_buffer_array.push({
					do_render_flag	:	false
				});

			var p=scene.render_buffer_array[my_render_buffer_id];
			p.do_render_flag	=true;
			p.render_buffer_id	=my_render_buffer_id;

			for(var j=0,nj=my_data.length;j<nj;)
				switch(my_data[j++]){
				default:
					break;
				case 0:
					var my_target_component_id	=my_data[j++];
					var my_target_driver_id		=my_data[j++];
					var my_ids=scene.component_array_sorted_by_id;
					my_ids=my_ids[my_target_component_id].component_ids;
					p.target_ids={
						component_id		:	my_target_component_id,
						driver_id			:	my_target_driver_id,
						render_id			:	my_ids[my_target_driver_id][0],
						part_id				:	my_ids[my_target_driver_id][1],
						data_buffer_id		:	my_ids[my_target_driver_id][2]
					};
					break;
				case 1:
					p.target_texture_id	=my_data[j++];
					p.target_name		=my_data[j++];	
					break;
				case 2:
					p.camera_id=my_data[j++];
					break;
				case 3:
					p.view_volume_box=[
						[	my_data[j++],	my_data[j++],	my_data[j++],	1	],
						[	my_data[j++],	my_data[j++],	my_data[j++],	1	]
					];
					break;
				case 4:
					p.clip_plane=null;
					p.clip_plane_matrix=scene.computer.create_move_rotate_matrix(0,0,0,0,0,0);
					break;
				case 5:
					p.clip_plane		=[my_data[j++],my_data[j++],my_data[j++],my_data[j++]];
					p.clip_plane_matrix	=scene.computer.project_to_plane_location(
						p.clip_plane[0],p.clip_plane[1],p.clip_plane[2],p.clip_plane[3],1.0);
					break;
				case 6:
					p.camera_transformation_matrix=scene.computer.identity_matrix;
					break;
				case 7:
					p.camera_transformation_matrix=[
							my_data[j++],my_data[j++],my_data[j++],my_data[j++],
							my_data[j++],my_data[j++],my_data[j++],my_data[j++],
							my_data[j++],my_data[j++],my_data[j++],my_data[j++],
							my_data[j++],my_data[j++],my_data[j++],my_data[j++]
						];
					break;
				case 8:
					p.main_display_target_flag=true;
					break;
				case 9:
					p.main_display_target_flag=false;
					break;
				case 10:
					p.target_view_parameter=new Object();
					p.target_view_parameter.view_x0			=my_data[j++];
					p.target_view_parameter.view_y0			=my_data[j++];
					p.target_view_parameter.view_width		=my_data[j++];
					p.target_view_parameter.view_height		=my_data[j++];
					p.target_view_parameter.whole_view_width=my_data[j++];
					p.target_view_parameter.whole_view_height=my_data[j++];
					break;
				}
		}
		return;
	}
	
	async function fetch_web_server_response_data(request_url,scene)
	{
		var fetch_start_time=(new Date()).getTime();
		
		if(scene.terminate_flag)
			return true;
		var render_promise=await fetch(request_url,scene.fetch_parameter.request_render_data);
   		if(scene.terminate_flag)
			return true;
		if(!(render_promise.ok)){
			alert("request fetch_web_server_response_data fail: "+request_url);
			return true;
		}
		
		var response_data;
		try{
			response_data=await render_promise.json();
		}catch(e){
			alert("parse fetch_web_server_response_data fail: "+e.toString());
			alert(request_url);
			return true;
		}
		if(scene.terminate_flag)
			return true;
		
		var fetch_end_time=(new Date()).getTime();
		
		scene.collector_stack_version=response_data[0][0];
		scene.modifier_current_time=scene.modifier_time_parameter.modify_parameter(response_data[0]);
		scene.browser_current_time=(fetch_start_time+fetch_end_time)/2.0;
		
		parse_target_parameter(response_data[1],scene);
		
		scene.component_render_data.modify_component_render_parameter(response_data[2],response_data[3],scene);
		scene.component_render_data.modify_component_buffer_parameter(response_data[4],scene);
		scene.camera.modify_camera_data(response_data[5]);
		scene.component_location_data.set_component_location(response_data[6]);
		for(var p=response_data[7],i=0,ni=p.length;i<ni;i++)
			scene.vertex_data_downloader.buffer_head_request_queue.push(p[i]);
		
		return false;
	};
	
	for(var start_time=0;;){
		if(scene.terminate_flag)
			break;
		var current_time=(new Date()).getTime();
		var my_delay_time_length=scene.modifier_time_parameter.delay_time_length;
		if((my_delay_time_length-=(current_time-start_time))>0){
			await new Promise(resolve=>
			{
				setTimeout(resolve,my_delay_time_length);
			});
			continue;
		}
		start_time=current_time;
		if(await fetch_web_server_response_data(create_request_url(scene),scene))
			break;
		if(scene.terminate_flag)
			break;
		await new Promise(resolve=>
		{
			window.requestAnimationFrame(resolve);
			setTimeout(resolve,scene.parameter.engine_touch_time_length/1000000);
		});
	}
}