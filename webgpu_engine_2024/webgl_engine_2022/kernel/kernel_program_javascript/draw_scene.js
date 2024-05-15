function draw_scene_routine(render_data,render)
{
	var render_id		=render_data.target_ids.render_id;
	var part_id			=render_data.target_ids.part_id;
	var data_buffer_id	=render_data.target_ids.data_buffer_id;
			
	var target_render_driver	=render.render_driver[render_id];
	var target_part_driver		=render.part_driver[render_id][part_id];
	var target_part_object		=render.part_array[render_id][part_id];
	if((typeof(target_part_object)!="object")||(target_part_object==null))
		return;
	var target_component_driver	=target_part_object.component_driver_array[data_buffer_id];
	if((typeof(target_component_driver)!="object")||(target_component_driver==null))
		return;
	if(typeof(target_component_driver.begin_render_target)!="function")
		return;
	var project_matrix=render.camera.compute_camera_data(render_data);
	render.system_buffer.set_target_buffer(render_data,project_matrix,render);

	for(var target_sequence_id=0;;target_sequence_id++){
		var render_target=target_component_driver.begin_render_target(target_sequence_id,
				render_data,target_part_object,target_part_driver,target_render_driver,render);
		if(render_target==null)
			break;
		var method_array=render_target.method_array;
		if(!(Array.isArray(method_array)))
			continue;
		if(method_array.length<=0)
			continue;	
		
		render.webgpu.render_pass_encoder=render.webgpu.
				command_encoder.beginRenderPass(render_target.pass_descriptor);
		if(typeof(render.webgpu.render_pass_encoder)!="object")
			continue;
		if(render.webgpu.render_pass_encoder==null)
			continue;

		var view_x0=(render_data.target_view_parameter.view_x0<0)?0:
			(render_data.target_view_parameter.view_x0>=render_target.target_view.width)
			?(render_target.target_view.width-1):(render_data.target_view_parameter.view_x0);
		var view_y0=(render_data.target_view_parameter.view_y0<0)?0:
			(render_data.target_view_parameter.view_y0>=render_target.target_view.height)
			?(render_target.target_view.height-1):(render_data.target_view_parameter.view_y0);
		var view_width=(render_data.target_view_parameter.view_width<1)?1:
			((render_data.target_view_parameter.view_width+view_x0)>render_target.target_view.width)
			?(render_target.target_view.width-view_x0):(render_data.target_view_parameter.view_width);	
		var view_height=(render_data.target_view_parameter.view_height<1)?1:
			((render_data.target_view_parameter.view_height+view_y0)>render_target.target_view.height)
			?(render_target.target_view.height-view_y0):(render_data.target_view_parameter.view_height);
		var whole_view_width=render_data.target_view_parameter.whole_view_width;
		var whole_view_height=render_data.target_view_parameter.whole_view_height;
		
		if(render_data.main_display_target_flag){
			render.view.main_target_x=0.5*(render.view.x+1.0)*whole_view_width -view_x0;
			render.view.main_target_x=2.0*render.view.main_target_x/view_width -1.0;

			render.view.main_target_y=0.5*(render.view.y+1.0)*whole_view_height-view_y0;
			render.view.main_target_y=2.0*render.view.main_target_y/view_height-1.0;
		}
		var my_viewport={
			x			:	view_x0,
			y			:	render_target.target_view.height-view_y0-view_height,
			width		:	view_width,
			height		:	view_height,
			min_depth	:	0,
			max_depth	:	1
		};
		render.webgpu.render_pass_encoder.setViewport(
			my_viewport.x,			my_viewport.y,
			my_viewport.width,		my_viewport.height,
			my_viewport.min_depth,	my_viewport.max_depth);
	
		for(var i=0,ni=method_array.length;i<ni;i++){
			if(typeof(target_component_driver.begin_render_method)=="function")
				target_component_driver.begin_render_method(target_sequence_id,
					method_array[i],render_data,project_matrix,
					target_part_object,target_part_driver,target_render_driver,render);
			for(var render_id=0,render_number=render.part_array.length;render_id<render_number;render_id++){
				if((typeof(render.part_array[render_id])!="object")||(render.part_array[render_id]==null))
					continue;
				var render_driver=render.render_driver[render_id];
				if(method_array[i].method_id<0)
					continue;
				if(method_array[i].method_id>=render_driver.method_render_flag.length)
					continue;	
				if(!(render_driver.method_render_flag[method_array[i].method_id]))
					continue;
				for(var part_id=0,part_number=render.part_array[render_id].length;part_id<part_number;part_id++){	
					var part_object=render.part_array[render_id][part_id];
					if(	  (typeof(part_object)!="object")||(part_object==null))
						continue;
					var part_driver=render.part_driver[render_id][part_id];
					if((typeof(part_driver)!="object")||(part_driver==null))
						continue;
					var component_render_parameter	=part_object.component_render_parameter;
					if(render_data.render_buffer_id>=component_render_parameter.length)
						continue;
				   	var render_parameter_array=component_render_parameter[render_data.render_buffer_id];
					for(var j=0,nj=render_parameter_array.length;j<nj;j++){
						var data_buffer_id		=render_parameter_array[j][0];
						var render_parameter	=render_parameter_array[j][1];
						var component_driver	=part_object.component_driver_array[data_buffer_id];
						var component_ids		=part_object.part_component_id_and_driver_id[data_buffer_id];
						var component_id		=component_ids[0];
						var driver_id			=component_ids[1];

						render.system_buffer.set_system_bindgroup(
							render_data.render_buffer_id,component_id,driver_id,render);
						component_driver.draw_component(method_array[i],render_data,
							render_id,part_id,component_id,driver_id,render_parameter,
							project_matrix,part_object,part_driver,render_driver,render);
					}
				}
			}
			if(typeof(target_component_driver.end_render_method)=="function")
				target_component_driver.end_render_method(target_sequence_id,
					method_array[i],render_data,project_matrix,
					target_part_object,target_part_driver,target_render_driver,render);
		}	
		if(render.webgpu.render_pass_encoder!=null){
			render.webgpu.render_pass_encoder.end();
			render.webgpu.render_pass_encoder=null;
		}
		if(typeof(target_component_driver.end_render_target)=="function")
			target_component_driver.end_render_target(target_sequence_id,
				render_data,target_part_object,target_part_driver,target_render_driver,render);
	}
}
async function draw_scene_main(part_init_data,component_init_data,render)
{
	while(!(render.terminate_flag)){
		
		render.vertex_data_downloader.process_buffer_head_request_queue(part_init_data,component_init_data,render);
		
		var start_time=(new Date()).getTime();
		if(render.browser_current_time>0){
			var pass_time=(start_time-render.browser_current_time)*1000*1000;
			var new_current_time=render.modifier_time_parameter.webserver_current_time+pass_time;
			if(render.current_time<=new_current_time)
				render.current_time=new_current_time;
			else
				render.current_time++;

			for(var i=0,ni=render.modifier_current_time.length;i<ni;i++){
				new_current_time =render.modifier_time_parameter.caculate_current_time(i)+pass_time;
				if(render.modifier_current_time[i]<new_current_time)
					render.modifier_current_time[i]=new_current_time;
				else
					render.modifier_current_time[i]++;
			}
		}
		
		var fun_array=render.routine_array;
		render.routine_array=new Array();
		for(var i=0,ni=fun_array.length;i<ni;i++)
			if(typeof(fun_array[i])=="function")
				if(fun_array[i](render))
					render.routine_array.push(fun_array[i]);
		
		var my_command_buffer=new Array();
		render.system_buffer.set_system_buffer(render);
		for(var i=0,ni=render.render_buffer_array.length;i<ni;i++)
			if(render.render_buffer_array[i].do_render_flag){
				render.webgpu.command_encoder=render.webgpu.device.createCommandEncoder();
				draw_scene_routine(render.render_buffer_array[i],render);
				my_command_buffer.push(render.webgpu.command_encoder.finish());
				render.webgpu.command_encoder=null;
			}
		render.webgpu.device.queue.submit(my_command_buffer);
		await render.webgpu.device.queue.onSubmittedWorkDone();
		
		if(render.terminate_flag)
			return;
		for(var i=0,ni=render.render_buffer_array.length;i<ni;i++){
			if(render.terminate_flag)
				return;
			var render_data=render.render_buffer_array[i];
			if(!(render_data.do_render_flag))
				continue;

			var render_id		=render_data.target_ids.render_id;
			var part_id			=render_data.target_ids.part_id;
			var data_buffer_id	=render_data.target_ids.data_buffer_id;
			
			var target_render_driver	=render.render_driver[render_id];
			var target_part_driver		=render.part_driver[render_id][part_id];
			var target_part_object		=render.part_array[render_id][part_id];
			if((typeof(target_part_object)!="object")||(target_part_object==null))
				continue;
			var target_component_driver	=target_part_object.component_driver_array[data_buffer_id];
			if((typeof(target_component_driver)!="object")||(target_component_driver==null))
				continue;
			if(typeof(target_component_driver.complete_render_target)!="function")
				continue;

			await target_component_driver.complete_render_target(render_data,
						target_part_object,target_part_driver,target_render_driver,render);
		}

		if(render.terminate_flag)
			return;
		await new Promise((resolve)=>
			{
				window.requestAnimationFrame(resolve);
				setTimeout(resolve,render.parameter.engine_touch_time_length/1000000);
			});
	}
}