async function request_render_data(render)
{
	function create_request_url(render)
	{
		var request_url=render.url_with_channel+"&command=component&method=update_render";

		var min_value=render.computer.min_value();
		if(Math.abs(render.view_bak.x-render.view.x)>min_value){
			render.view_bak.x=render.view.x;
			request_url+="&x="+(render.view.x.toString());
		};
		if(Math.abs(render.view_bak.y-render.view.y)>min_value){
			render.view_bak.y=render.view.y;
			request_url+="&y="+(render.view.y.toString());
		};
		if(Math.abs(render.view_bak.canvas_width-render.canvas.width)>min_value){
			render.view_bak.canvas_width=render.canvas.width;
			request_url+="&width="+(render.canvas.width.toString());
		};
		if(Math.abs(render.view_bak.canvas_height-render.canvas.height)>min_value){
			render.view_bak.canvas_height=render.canvas.height;
			request_url+="&height="+(render.canvas.height.toString());
		};
		if(render.view_bak.in_canvas_flag^render.event_listener.mouse_inside_canvas_flag){
			render.view_bak.in_canvas_flag=render.event_listener.mouse_inside_canvas_flag;
			request_url+="&in_canvas="+(render.event_listener.mouse_inside_canvas_flag?"yes":"no");
		};
		
		var id,value;

		if(render.view_bak.component!=(id=render.pickup.component_id)){
			render.view_bak.component=id;
			request_url+="&component="+id.toString();
		};
		if(render.view_bak.body!=(id=render.pickup.body_id)){
			render.view_bak.body=id;
			request_url+="&body="+id.toString();
		};
		if(render.view_bak.face!=(id=render.pickup.face_id)){
			render.view_bak.face=id;
			request_url+="&face="+id.toString();
		};
		if(render.view_bak.loop!=(id=render.pickup.loop_id)){
			render.view_bak.loop=id;
			request_url+="&loop="+id.toString();
		};
		if(render.view_bak.edge!=(id=render.pickup.edge_id)){
			render.view_bak.edge=id;
			request_url+="&edge="+id.toString();
		};
		if(render.view_bak.vertex!=(id=render.pickup.vertex_id)){
			render.view_bak.vertex=id;
			request_url+="&vertex="+id.toString();
		};
		if(render.view_bak.point!=(id=render.pickup.point_id)){
			render.view_bak.point=id;
			request_url+="&point="+id.toString();
		};
		if(Math.abs(render.view_bak.depth-(value=render.pickup.depth))>min_value){
			render.view_bak.depth=value;
			request_url+="&depth="+(new Number(value)).toPrecision(6);
		}
		if(Math.abs(render.view_bak.value-(value=render.pickup.value))>min_value){
			render.view_bak.value=value;
			request_url+="&value="+(new Number(value)).toPrecision(6);
		};
		
		if(render.vertex_data_downloader.acknowledge_render_part_id!=null){
			request_url+="&acknowledge="+render.vertex_data_downloader.acknowledge_render_part_id;
			render.vertex_data_downloader.acknowledge_render_part_id=null;
		};
		
		if(render.current.loaded_length!=(render.vertex_data_downloader.loaded_buffer_object_data_length)){
			render.current.loaded_length=render.vertex_data_downloader.loaded_buffer_object_data_length;
			request_url+="&loaded_length="+	render.vertex_data_downloader.loaded_buffer_object_file_number.toString();
			request_url+="_"+ 				render.vertex_data_downloader.loaded_buffer_object_data_length.toString();
			request_url+="_"+				render.vertex_data_downloader.loading_render_id.toString();
			request_url+="_"+				render.vertex_data_downloader.loading_part_id.toString();
		};
		{
			var requesting_number,max_request_number=render.parameter.max_loading_number;

			requesting_number =render.vertex_data_downloader.current_loading_mesh_number;
			requesting_number+=render.vertex_data_downloader.request_render_part_id.length;
			requesting_number+=render.vertex_data_downloader.buffer_head_request_queue.length;
			if((render.render_request_start_time-render.last_event_time)<=render.parameter.download_minimal_time_length)
				if((render.render_request_start_time-render.engine_start_time)>render.parameter.download_start_time_length)
					requesting_number=max_request_number;
			request_url+="&requesting_number="+requesting_number+"_"+max_request_number;
		};
		
		request_url+="&data_time="		+(render.data_time_length.toString());
		request_url+="&render_time="	+(render.render_time_length.toString());
		request_url+="&read_time="		+(render.pickup.read_time_length.toString());
		request_url+="&render_interval="+(render.render_interval_length.toString());
		request_url+="&precision="		+(render.event_listener.mouse_down_flag?"false":"true");
		request_url+="&length=";
		request_url+=request_url.length.toString();

		return request_url;
	};
	function parse_current_information_request(current_information_request_response_data,render)
	{
		for(var p,i=0,ni=current_information_request_response_data.length;i<ni;i++){
			switch((p=current_information_request_response_data[i])[0]){
			case 0:
				render.current.render_buffer_id=p[1];
				break;
			case 1:
				render.current.target_id=p[1];
				break;
			case 2:
				render.current.target_viewport_id=p[1];
				break;
			case 3:
				render.current.camera_id=p[1];
				break;
			case 4:
				render.current.camera_component_id=p[1];
				break;
			case 6:
				render.current.high_or_low_precision_flag=(p[1]>0.5)?true:false;
				break;
			};
		};
	};

	async function fetch_web_server_response_data(request_url,render)
	{
		var fetch_start_time=(new Date()).getTime();
		var render_promise=await fetch(request_url,
				{
   					method	:	"POST",
   					cache	:	"no-cache"
   				});
		if(!(render_promise.ok)){
			if(render.terminate_flag)
				return true;
			alert("request fetch_web_server_response_data fail: "+request_url);
			return true;
		}
		var response_data;
		try{
			response_data = await render_promise.json();
		}catch(e){
			if(render.terminate_flag)
				return true;
			alert("parse fetch_web_server_response_data fail: "+e.toString());
			alert(request_url);
			return true;
		}
		if(render.terminate_flag)
			return true;
		
		var fetch_end_time=(new Date()).getTime();
		
		render.collector_stack_version=response_data[0].shift();
		render.modifier_current_time=render.modifier_time_parameter.modify_parameter(response_data[0]);
		render.browser_current_time=(fetch_start_time+fetch_end_time)/2.0;
		
		render.component_render_data.render_data=new Array();
		for(var i=0,ni=response_data[1].length;i<ni;i++){
			var render_buffer_id	=render.component_render_data.get_component_render_parameter(response_data[1][i][0],render);
			var target_id			=response_data[1][i][1];
			var do_render_number	=response_data[1][i][2];
			if(response_data[1][i].length>3)
				render.target_buffer[target_id]=response_data[1][i][3];
			render.component_render_data.render_data.push(
				[render_buffer_id,target_id,(do_render_number>=0)?do_render_number:Number.MAX_VALUE]);
		};
		
		render.component_render_data.get_component_buffer_parameter(response_data[2],render);
		render.component_location_data.modify_component_location(response_data[3]);
		parse_current_information_request(response_data[4],render);
		
		for(var p=response_data[5],i=0,ni=p.length;i<ni;i++)
			render.vertex_data_downloader.buffer_head_request_queue.push(p[i]);
		render.data_time_length=(new Date()).getTime()-fetch_end_time;

		return false;
	};
	
	for(render.render_request_start_time=0;!(render.terminate_flag);){
		var current_time=(new Date()).getTime();
		var my_delay_time_length=render.modifier_time_parameter.delay_time_length;
		if((my_delay_time_length-=(current_time-render.render_request_start_time))>0){
			await new Promise(resolve=>{SetTimeout(resolve,my_delay_time_length);});
			continue;
		}
		render.render_request_start_time=current_time;
		if(await fetch_web_server_response_data(create_request_url(render),render))
			break;
		await new Promise(resolve=>
		{
			window.requestAnimationFrame(resolve);
			SetTimeout(resolve,render.parameter.engine_touch_time_length/1000000);
		});
	}
}
