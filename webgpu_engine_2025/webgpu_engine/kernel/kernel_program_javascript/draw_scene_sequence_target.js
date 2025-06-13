function create_scene_target_routine(target_parameter,scene_target_array,scene)
{
	var target_id		=target_parameter.target_id;
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
	if(typeof(target_component_driver.begin_scene_target)!="function")
		return;
	target_component_driver.begin_scene_target(scene_target_array,render_data,
			target_part_object,target_part_driver,target_render_driver,scene);
	return;
}

function destroy_scene_target_routine(target_parameter,scene_target_array,scene)
{
	var target_id		=target_parameter.target_id;
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
function draw_scene_target_routine(target_parameter,scene_target_array,pass_id,scene)
{
	var target_id		=target_parameter.target_id;
	var render_data		=scene.render_buffer_array[target_id];
	var render_id		=render_data.target_ids.render_id;
	var part_id			=render_data.target_ids.part_id;
	var data_buffer_id	=render_data.target_ids.data_buffer_id;
	
	var scene_target=scene_target_array[pass_id];
	if((typeof(scene_target)!="object")||(scene_target==null))
		return;
	var method_array=scene_target.method_array;
	if(!(Array.isArray(method_array)))
		return;
	if(method_array.length<=0)
		return;

	var view_x0				=render_data.target_view_parameter.view_x0;
	var view_y0				=render_data.target_view_parameter.view_y0;
	var view_width			=render_data.target_view_parameter.view_width;	
	var view_height			=render_data.target_view_parameter.view_height;
	var whole_view_width	=render_data.target_view_parameter.whole_view_width;
	var whole_view_height	=render_data.target_view_parameter.whole_view_height;
		
	if(render_data.main_display_target_flag){
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
		for(var render_id=0,render_number=scene.part_array.length;render_id<render_number;render_id++){
			if((typeof(scene.part_array[render_id])!="object")||(scene.part_array[render_id]==null))
				continue;
			var render_driver=scene.render_driver[render_id];
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
				if(render_data.target_id>=component_render_parameter.length)
					continue;
			   	var render_parameter_array=component_render_parameter[render_data.target_id];
				for(var j=0,nj=render_parameter_array.length;j<nj;j++){
					var data_buffer_id	=render_parameter_array[j][0];
					var render_parameter=render_parameter_array[j][1];
					var component_driver=part_object.component_driver_array[data_buffer_id];
					var component_ids	=part_object.part_component_id_and_driver_id[data_buffer_id];

					scene.system_buffer.set_system_bindgroup(
							render_data.target_id,method_array[i].method_id,
							component_ids.component_id,component_ids.driver_id,scene);
					component_driver.draw_component(method_array[i],render_parameter,
							render_data,part_object,part_driver,render_driver,scene);
				}
			}
		}
	}
}
