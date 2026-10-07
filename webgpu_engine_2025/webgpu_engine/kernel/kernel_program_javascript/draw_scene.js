function set_system_buffer_and_compute_component_location_routine(scene)
{
	scene.system_buffer.set_system_buffer();
	scene.component_location_data.compute_component_location();
}
function get_target_number_routine(scene)
{
	return scene.render_buffer_array.length;
}
function get_target_parameter_routine(target_id,scene)
{
	var p=scene.render_buffer_array[target_id];
	return	{
				target_id				:	target_id,
				do_render_flag			:	p.do_render_flag,
				target_or_bundle_flag	:	p.target_or_bundle_flag,
				target_name				:	p.target_name
			};
}
function front_process_scene_routine(scene_id,scene)
{
	if(scene.terminate_flag)
		return 0;
		
	scene.scene_id=scene_id;
		
	var new_fun_array=new Array();
	var old_fun_array=scene.routine_object.before_draw_scene_routine_array;
	for(var i=0,ni=old_fun_array.length;i<ni;i++)
		if(typeof(old_fun_array[i])=="function")
			if(old_fun_array[i](scene))
				new_fun_array.push(old_fun_array[i]);
	scene.routine_object.before_draw_scene_routine_array=new_fun_array;
	
	scene.vertex_data_downloader.process_load_package_request_queue(scene);

	var start_time=(new Date()).getTime();
	if(scene.browser_current_time>0){
		var pass_time=(start_time-scene.browser_current_time)*1000*1000;
		var new_current_time=scene.modifier_time_parameter.webserver_current_time+pass_time;
		if(scene.current_time<=new_current_time)
			scene.current_time=new_current_time;
		else
			scene.current_time++;

		for(var i=0,ni=scene.modifier_current_time.length;i<ni;i++){
			new_current_time =scene.modifier_time_parameter.caculate_current_time(i)+pass_time;
			if(scene.modifier_current_time[i]<new_current_time)
				scene.modifier_current_time[i]=new_current_time;
			else
				scene.modifier_current_time[i]++;
		}
	}
	return scene.init_parameter.scene_touch_time_length;
}
function back_process_scene_routine(scene)
{
	if(scene.terminate_flag)
		return 0;
	var new_fun_array=new Array();
	var old_fun_array=scene.routine_object.after_draw_scene_routine_array;
	
	for(var i=0,ni=old_fun_array.length;i<ni;i++)
		if(typeof(old_fun_array[i])=="function")
			if(old_fun_array[i](scene))
				new_fun_array.push(old_fun_array[i]);

	scene.routine_object.after_draw_scene_routine_array=new_fun_array;
}

function scene_target_begin_routine(target_id,scene_target_array,scene)
{
	var render_data=scene.render_buffer_array[target_id];
	if(!(render_data.do_render_flag))
		return false;
	var render_id		=render_data.target_ids.render_id;
	var part_id			=render_data.target_ids.part_id;
	var data_buffer_id	=render_data.target_ids.data_buffer_id;

	var target_render_driver	=scene.render_driver[render_id];
	var target_part_driver		=scene.part_driver[render_id][part_id];
	var target_part_object		=scene.part_array[render_id][part_id];
	if((typeof(target_part_object)!="object")||(target_part_object==null))
		return false;
	var target_component_driver	=target_part_object.component_driver_array[data_buffer_id];
	if((typeof(target_component_driver)!="object")||(target_component_driver==null))
		return false;
	if(typeof(target_component_driver.begin_scene_target)!="function")
		return false;
	if(!(target_component_driver.begin_scene_target(scene_target_array,
		render_data,target_part_object,target_part_driver,target_render_driver,scene)))
			return false;
		
	var render_data_from=null;
	if(render_data.target_id_from>=0)
		render_data_from=scene.render_buffer_array[render_data.target_id_from];
	render_data.project_matrix=scene.camera.compute_camera_data(render_data,render_data_from);
	scene.system_buffer.set_target_buffer(render_data,render_data_from);

	return true;
}

function scene_target_end_routine(target_id,scene_target_array,scene)
{
	var render_data		=scene.render_buffer_array[target_id];
	var render_id		=render_data.target_ids.render_id;
	var part_id			=render_data.target_ids.part_id;
	var data_buffer_id	=render_data.target_ids.data_buffer_id;
			
	var target_render_driver	=scene.render_driver[render_id];
	var target_part_driver		=scene.part_driver[render_id][part_id];
	var target_part_object		=scene.part_array[render_id][part_id];
	if((typeof(target_part_object)!="object")||(target_part_object==null))
		return;

	var target_component_driver	=target_part_object.component_driver_array[data_buffer_id];
	if((typeof(target_component_driver)!="object")||(target_component_driver==null))
		return;
	if(typeof(target_component_driver.end_scene_target)!="function")
		return;
	target_component_driver.end_scene_target(scene_target_array,render_data,
		target_part_object,target_part_driver,target_render_driver,scene);
	return;
}
async function scene_target_complete_routine(target_id,scene)
{
	var render_data=scene.render_buffer_array[target_id];
	
	var render_id		=render_data.target_ids.render_id;
	var part_id			=render_data.target_ids.part_id;
	var data_buffer_id	=render_data.target_ids.data_buffer_id;
					
	var target_render_driver	=scene.render_driver[render_id];
	var target_part_driver		=scene.part_driver[render_id][part_id];
	var target_part_object		=scene.part_array[render_id][part_id];
				
	if((typeof(target_part_object)!="object")||(target_part_object==null))
		return;
	var target_component_driver	=target_part_object.component_driver_array[data_buffer_id];
	if((typeof(target_component_driver)!="object")||(target_component_driver==null))
		return;
	if(typeof(target_component_driver.scene_target_complete)!="function")
		return;
	await target_component_driver.scene_target_complete(render_data,
			target_part_object,target_part_driver,target_render_driver,scene);
}
function draw_scene_target_routine(target_id,scene_target_array,pass_id,scene)
{
	var scene_target=scene_target_array[pass_id];
	if((typeof(scene_target)!="object")||(scene_target==null))
		return;
	var method_array=scene_target.method_array;
	if(!(Array.isArray(method_array)))
		return;
	if(method_array.length<=0)
		return;

	var target_render_data	=scene.render_buffer_array[target_id];
	
	var view_x0				=target_render_data.target_view_parameter.view_x0;
	var view_y0				=target_render_data.target_view_parameter.view_y0;
	var view_width			=target_render_data.target_view_parameter.view_width;	
	var view_height			=target_render_data.target_view_parameter.view_height;
	var whole_view_width	=target_render_data.target_view_parameter.whole_view_width;
	var whole_view_height	=target_render_data.target_view_parameter.whole_view_height;

	if(target_render_data.main_display_target_flag){
		scene.view.main_target_x=0.5*(scene.view.x+1.0)*whole_view_width -view_x0;
		scene.view.main_target_x=2.0*scene.view.main_target_x/view_width -1.0;
		scene.view.main_target_y=0.5*(scene.view.y+1.0)*whole_view_height-view_y0;
		scene.view.main_target_y=2.0*scene.view.main_target_y/view_height-1.0;
	}

	scene.webgpu.render_pass_encoder.setViewport(
		view_x0,whole_view_height-(view_y0+view_height),view_width,view_height,0,1);

	for(var i=0,ni=method_array.length;i<ni;i++){
		if(method_array[i].method_id<0)
			continue;
		var render_number=scene.part_array.length;
		for(var render_id=0;render_id<render_number;render_id++){
			if((typeof(scene.part_array[render_id])!="object")||(scene.part_array[render_id]==null))
				continue;
			var render_driver=scene.render_driver[render_id];
			if(method_array[i].method_id>=render_driver.method_render_flag.length)
				continue;	
			if(!(render_driver.method_render_flag[method_array[i].method_id]))
				continue;
			var part_number=scene.part_array[render_id].length;
			for(var part_id=0;part_id<part_number;part_id++){	
				var part_object=scene.part_array[render_id][part_id];
				if((typeof(part_object)!="object")||(part_object==null))
					continue;
				var part_driver=scene.part_driver[render_id][part_id];
				if((typeof(part_driver)!="object")||(part_driver==null))
					continue;
				var component_render_parameter=part_object.component_render_parameter;
				if(target_render_data.target_id>=component_render_parameter.length)
					continue;
			   	var render_parameter_array=component_render_parameter[target_render_data.target_id];
				for(var j=0,nj=render_parameter_array.length;j<nj;j++){
					var data_buffer_id	=render_parameter_array[j][0];
					var render_parameter=render_parameter_array[j][1];
					var component_driver=part_object.component_driver_array[data_buffer_id];
					var component_ids	=part_object.part_component_id_and_driver_id[data_buffer_id];

					scene.system_buffer.set_system_bindgroup(target_id,
						method_array[i].method_id,component_ids.component_id,component_ids.driver_id);

					component_driver.draw_component(method_array[i],render_parameter,
						target_render_data,part_object,part_driver,render_driver,scene);
				}
			}
		}
	}
}
