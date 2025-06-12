function construct_scene_interface(my_scene)
{
	this.scene=my_scene;
	
	this.set_system_buffer_and_compute_component_location=function()
	{
		this.scene.system_buffer.set_system_buffer(this.scene);
		this.scene.component_location_data.compute_component_location();
	}
	this.get_render_buffer_number=function()
	{
		return this.scene.render_buffer_array.length;
	}
	this.get_target_parameter=function(target_id)
	{
		var p=this.scene.render_buffer_array[target_id];
		var ret_val=
			{
				target_id				:	target_id,
				do_render_flag			:	p.do_render_flag,
				target_or_bundle_flag	:	p.target_or_bundle_flag,
				target_name				:	p.target_name,
				target_ids				:	p.target_ids
			};
		return ret_val;
	}
	this.create_scene_target=function(target_parameter,scene_target_array)
	{
		create_scene_target_routine(target_parameter,scene_target_array,this.scene);
	}
	this.destroy_scene_target=function(target_parameter,scene_target_array)
	{
		destroy_scene_target_routine(target_parameter,scene_target_array,this.scene);
	}
	this.draw_scene_target=function(target_parameter,scene_target_array,pass_id)
	{
		draw_scene_target_routine(target_parameter,scene_target_array,pass_id,this.scene);
	}
	this.complete_render_target=async function(target_id)
	{
		var render_data=this.scene.render_buffer_array[target_id];
		
		var render_id		=render_data.target_ids.render_id;
		var part_id			=render_data.target_ids.part_id;
		var data_buffer_id	=render_data.target_ids.data_buffer_id;
						
		var target_render_driver	=this.scene.render_driver[render_id];
		var target_part_driver		=this.scene.part_driver[render_id][part_id];
		var target_part_object		=this.scene.part_array[render_id][part_id];
					
		if((typeof(target_part_object)!="object")||(target_part_object==null))
			return;
		var target_component_driver	=target_part_object.component_driver_array[data_buffer_id];
		if((typeof(target_component_driver)!="object")||(target_component_driver==null))
			return;
		if(typeof(target_component_driver.complete_render_target)!="function")
			return;
		await target_component_driver.complete_render_target(render_data,
				target_part_object,target_part_driver,target_render_driver,this.scene);
	}
	
	this.front_process_scene=function(scene_id)
	{
		if(this.scene.terminate_flag)
			return 0;

		this.scene.scene_id=scene_id;

		this.scene.vertex_data_downloader.process_buffer_head_request_queue(this.scene);
			
		var start_time=(new Date()).getTime();
		if(this.scene.browser_current_time>0){
			var pass_time=(start_time-this.scene.browser_current_time)*1000*1000;
			var new_current_time=this.scene.modifier_time_parameter.webserver_current_time+pass_time;
			if(this.scene.current_time<=new_current_time)
				this.scene.current_time=new_current_time;
			else
				this.scene.current_time++;
	
			for(var i=0,ni=this.scene.modifier_current_time.length;i<ni;i++){
				new_current_time =this.scene.modifier_time_parameter.caculate_current_time(i)+pass_time;
				if(this.scene.modifier_current_time[i]<new_current_time)
					this.scene.modifier_current_time[i]=new_current_time;
				else
					this.scene.modifier_current_time[i]++;
			}
		}
		
		for(var render_data,i=0,ni=this.scene.render_buffer_array.length;i<ni;i++)
			if((render_data=this.scene.render_buffer_array[i]).do_render_flag){
				render_data.project_matrix=this.scene.camera.compute_camera_data(render_data);
				this.scene.system_buffer.set_target_buffer(render_data,this.scene);
			}

		return this.scene.parameter.engine_touch_time_length;
	}
	
	this.back_process_scene=function()
	{
		if(this.scene.terminate_flag)
			return 0;
		var fun_array=this.scene.routine_array;
		this.scene.routine_array=new Array();
		for(var i=0,ni=fun_array.length;i<ni;i++)
			if(typeof(fun_array[i])=="function")
				if(fun_array[i](this.scene))
					this.scene.routine_array.push(fun_array[i]);
	}
}
