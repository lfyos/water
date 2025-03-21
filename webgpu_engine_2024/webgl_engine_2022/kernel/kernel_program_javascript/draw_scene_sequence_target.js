function create_scene_sequence_target_routine(
	target_sequence_id,render_buffer_id,scene_target_array,scene)
{
	var render_data		=scene.render_buffer_array[render_buffer_id];
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
	if(typeof(target_component_driver.begin_scene_target)!="function")
		return;

	var scene_target;
	if(typeof(scene_target=scene_target_array[target_sequence_id])!="object")
		scene_target=null;
	scene_target=target_component_driver.begin_scene_target(
			scene_target,target_sequence_id,render_data,
			target_part_object,target_part_driver,target_render_driver,scene);
	if(typeof(scene_target)!="object")
		return;
	if(scene_target==null)
		return;
	scene_target_array[target_sequence_id]=scene_target;
}

function destroy_scene_sequence_target_routine(
	target_sequence_id,render_buffer_id,scene_target_array,scene)
{
	var render_data		=scene.render_buffer_array[render_buffer_id];
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

	var scene_target;
	if(typeof(scene_target=scene_target_array[target_sequence_id])!="object")
		return;
	if(scene_target==null)
		return;
		
	target_component_driver.end_scene_target(
		scene_target,target_sequence_id,render_data,
		target_part_object,target_part_driver,target_render_driver,scene);
	
	return;
}

function draw_scene_sequence_target_routine(
	target_sequence_id,render_buffer_id,scene_target_array,scene)
{
	var render_data		=scene.render_buffer_array[render_buffer_id];
	var render_id		=render_data.target_ids.render_id;
	var part_id			=render_data.target_ids.part_id;
	var data_buffer_id	=render_data.target_ids.data_buffer_id;
			
	var target_render_driver	=scene.render_driver[render_id];
	var target_part_driver		=scene.part_driver[render_id][part_id];
	var target_part_object		=scene.part_array[render_id][part_id];
	if((typeof(target_part_object)!="object")||(target_part_object==null))
		return 0;
	var target_component_driver	=target_part_object.component_driver_array[data_buffer_id];
	if((typeof(target_component_driver)!="object")||(target_component_driver==null))
		return 0;

	var scene_target;
	if(typeof(scene_target=scene_target_array[target_sequence_id])!="object")
		return 0;
	if(scene_target==null)
		return 0;

	var method_array=scene_target.method_array;
	if(!(Array.isArray(method_array)))
		return 0;
	if(method_array.length<=0)
		return 0;
		
	var project_matrix=scene.camera.compute_camera_data(render_data);
	scene.system_buffer.set_target_buffer(render_data,project_matrix,scene);

	var view_x0=(render_data.target_view_parameter.view_x0<0)?0:
			(render_data.target_view_parameter.view_x0>=scene_target.target_view.width)
			?(scene_target.target_view.width-1):(render_data.target_view_parameter.view_x0);
	var view_y0=(render_data.target_view_parameter.view_y0<0)?0:
			(render_data.target_view_parameter.view_y0>=scene_target.target_view.height)
			?(scene_target.target_view.height-1):(render_data.target_view_parameter.view_y0);
	var view_width=(render_data.target_view_parameter.view_width<1)?1:
			((render_data.target_view_parameter.view_width+view_x0)>scene_target.target_view.width)
			?(scene_target.target_view.width-view_x0):(render_data.target_view_parameter.view_width);	
	var view_height=(render_data.target_view_parameter.view_height<1)?1:
			((render_data.target_view_parameter.view_height+view_y0)>scene_target.target_view.height)
			?(scene_target.target_view.height-view_y0):(render_data.target_view_parameter.view_height);
	var whole_view_width=render_data.target_view_parameter.whole_view_width;
	var whole_view_height=render_data.target_view_parameter.whole_view_height;
		
	if(render_data.main_display_target_flag){
		scene.view.main_target_x=0.5*(scene.view.x+1.0)*whole_view_width -view_x0;
		scene.view.main_target_x=2.0*scene.view.main_target_x/view_width -1.0;

		scene.view.main_target_y=0.5*(scene.view.y+1.0)*whole_view_height-view_y0;
		scene.view.main_target_y=2.0*scene.view.main_target_y/view_height-1.0;
	}
	var my_viewport={
		x			:	view_x0,
		y			:	scene_target.target_view.height-view_y0-view_height,
		width		:	view_width,
		height		:	view_height,
		min_depth	:	0,
		max_depth	:	1
	};
	scene.webgpu.render_pass_encoder.setViewport(
		my_viewport.x,			my_viewport.y,
		my_viewport.width,		my_viewport.height,
		my_viewport.min_depth,	my_viewport.max_depth);
	
	var ret_val=0;
	
	for(var i=0,ni=method_array.length;i<ni;i++){
		if(typeof(target_component_driver.begin_render_method)=="function")
			target_component_driver.begin_render_method(
				scene_target,target_sequence_id,method_array[i],render_data,project_matrix,
				target_part_object,target_part_driver,target_render_driver,scene);
		for(var render_id=0,render_number=scene.part_array.length;render_id<render_number;render_id++){
			if((typeof(scene.part_array[render_id])!="object")||(scene.part_array[render_id]==null))
				continue;
			var render_driver=scene.render_driver[render_id];
			if(method_array[i].method_id<0)
				continue;
			if(method_array[i].method_id>=render_driver.method_render_flag.length)
				continue;	
			if(!(render_driver.method_render_flag[method_array[i].method_id]))
				continue;
			for(var part_id=0,part_number=scene.part_array[render_id].length;part_id<part_number;part_id++){	
				var part_object=scene.part_array[render_id][part_id];
				if(	  (typeof(part_object)!="object")||(part_object==null))
					continue;
				var part_driver=scene.part_driver[render_id][part_id];
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

					scene.system_buffer.set_system_bindgroup(
						render_data.render_buffer_id,component_id,driver_id,scene);
					component_driver.draw_component(method_array[i],render_data,
						render_id,part_id,component_id,driver_id,render_parameter,
						project_matrix,part_object,part_driver,render_driver,scene);
				}
				ret_val++;
			}
		if(typeof(target_component_driver.end_render_method)=="function")
			target_component_driver.end_render_method(
				scene_target,target_sequence_id,method_array[i],render_data,project_matrix,
				target_part_object,target_part_driver,target_render_driver,scene);
		}
	}
	
	return ret_val;
}
