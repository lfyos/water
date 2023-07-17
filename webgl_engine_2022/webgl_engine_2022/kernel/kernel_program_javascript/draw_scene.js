function draw_scene_routine(render_data,render)
{
	var render_id	=render_data.target_ids.render_id;
	var part_id		=render_data.target_ids.part_id;
	var buffer_id	=render_data.target_ids.buffer_id;
			
	var target_render_driver	=render.render_driver[render_id];
	var target_part_driver		=render.part_driver[render_id][part_id];
	var target_part_object		=render.part_array[render_id][part_id];
	if((typeof(target_part_object)!="object")||(target_part_object==null))
		return;
	var target_component_driver	=target_part_object.component_driver_array[buffer_id];
	if((typeof(target_component_driver)!="object")||(target_component_driver==null))
		return;
	if(typeof(target_component_driver.begin_render_target)!="function")
		return;
	var method_array=target_component_driver.begin_render_target(render_data,
				target_part_object,target_part_driver,target_render_driver,render);
	if(!(Array.isArray(method_array)))
		return;
	if(method_array.length<=0)
		return;

	var project_matrix	=render.camera.compute_camera_data(render_data);	
	render.system_buffer.set_target_buffer(render_data.render_buffer_id,project_matrix);
			
	for(var i=0,ni=method_array.length;i<ni;i++){
		if(typeof(target_component_driver.begin_render_method)=="function")
			target_component_driver.begin_render_method(
				method_array[i],render_data,project_matrix,
				target_part_object,target_part_driver,target_render_driver,render);
		for(var render_id=0,render_number=render.part_array.length;render_id<render_number;render_id++){
			if(typeof(render.part_array[render_id])=="undefined")
				continue;
			for(var part_id=0,part_number=render.part_array[render_id].length;part_id<part_number;part_id++){
				var part_object=render.part_array[render_id][part_id];	
				if((typeof(part_object)!="object")||(part_object==null))
					continue;
				var component_render_parameter	=part_object.component_render_parameter;
				var component_buffer_parameter	=part_object.component_buffer_parameter;
				var component_data_buffer_id	=part_object.component_data_buffer_id;
				if(	  (render_data.render_buffer_id>=component_render_parameter.length)
					||(render_data.render_buffer_id>=component_data_buffer_id.length))
						continue;
			   	component_render_parameter	=component_render_parameter[render_data.render_buffer_id];
			   	component_data_buffer_id	=component_data_buffer_id[render_data.render_buffer_id];
			   	if((component_render_parameter.length<=0)||(component_data_buffer_id.length<=0))
			   		continue;
				var part_driver=render.part_driver[render_id][part_id];
				if((typeof(part_driver)=="object")&&(part_driver!=null))
					if(typeof(part_driver.draw_component)=="function"){
				    	part_driver.draw_component(method_array[i],render_data,
							component_render_parameter,component_buffer_parameter,
							project_matrix,part_object,render.render_driver[render_id],render);
							continue;
					}
				var instance_number=component_render_parameter.length;
				for(var instance_id=0;instance_id<instance_number;instance_id++){
					var buffer_id=component_data_buffer_id[instance_id];
					var component_driver=part_object.component_driver_array[buffer_id];
					if((typeof(component_driver)=="object")&&(component_driver!=null))
						if(typeof(component_driver.draw_component)=="function"){
							render.set_system_bindgroup_by_part(
								render_data.render_buffer_id,
								method_array[i].method_id,
								render_id,part_id,buffer_id);
								component_driver.draw_component(method_array[i],render_data,
									component_render_parameter[instance_id],
									component_buffer_parameter[buffer_id],
									project_matrix,part_object,part_driver,
									render.render_driver[render_id],render);
						}
				}
			}
		}
	}
	
	if(typeof(target_component_driver.end_render_method)=="function")
		target_component_driver.end_render_method(
			method_array[i],render_data,project_matrix,
			target_part_object,target_part_driver,target_render_driver,render);
	if(render.webgpu.render_pass_encoder!=null){
		render.webgpu.render_pass_encoder.end();
		render.webgpu.render_pass_encoder=null;
	}
	if(typeof(target_component_driver.end_render_target)=="function")
		target_component_driver.end_render_target(render_data,
			target_part_object,target_part_driver,target_render_driver,render);
}
async function draw_scene_main(render)
{
	while(!(render.terminate_flag)){
		for(var i=render.vertex_data_downloader.current_loading_mesh_number,ni=render.parameter.max_loading_number;i<ni;)
				i+=render.vertex_data_downloader.request_buffer_object_data(render);
		render.vertex_data_downloader.process_buffer_head_request_queue(render);
		
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

		var command_encoder_buffer=new Array();
		render.system_buffer.set_system_buffer();
		for(var i=0,ni=render.render_buffer_array.length;i<ni;i++)
			if(render.render_buffer_array[i].do_render_flag){
				render.webgpu.command_encoder=render.webgpu.device.createCommandEncoder();
				draw_scene_routine(render.render_buffer_array[i],render);
				command_encoder_buffer.push(render.webgpu.command_encoder.finish());
				render.webgpu.command_encoder=null;
			}
		render.webgpu.device.queue.submit(command_encoder_buffer);

		await render.webgpu.device.queue.onSubmittedWorkDone();
		
		for(var i=0,ni=render.render_buffer_array.length;i<ni;i++){
			var render_data=render.render_buffer_array[i];
			if(!(render_data.do_render_flag))
				continue;

			var render_id	=render_data.target_ids.render_id;
			var part_id		=render_data.target_ids.part_id;
			var buffer_id	=render_data.target_ids.buffer_id;
			
			var target_render_driver	=render.render_driver[render_id];
			var target_part_driver		=render.part_driver[render_id][part_id];
			var target_part_object		=render.part_array[render_id][part_id];
			if((typeof(target_part_object)!="object")||(target_part_object==null))
				continue;
			var target_component_driver	=target_part_object.component_driver_array[buffer_id];
			if((typeof(target_component_driver)!="object")||(target_component_driver==null))
				continue;
			if(typeof(target_component_driver.complete_render_target)!="function")
				continue;
			await target_component_driver.complete_render_target(render_data,
						target_part_object,target_part_driver,target_render_driver,render);
		}

		await new Promise((resolve)=>
			{
				window.requestAnimationFrame(resolve);
				setTimeout(resolve,render.parameter.engine_touch_time_length/1000000);
			});
	}
}