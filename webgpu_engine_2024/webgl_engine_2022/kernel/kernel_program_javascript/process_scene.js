function construct_scene_interface(my_scene)
{
	this.scene=my_scene;
	
	this.set_system_buffer=function()
	{
		this.scene.system_buffer.set_system_buffer(this.scene);
	}
	
	this.get_render_buffer_number=function()
	{
		return this.scene.render_buffer_array.length;
	}
	this.get_do_render_flag=function(render_buffer_id)
	{
		return this.scene.render_buffer_array[render_buffer_id].do_render_flag;
	}
	this.get_target_name=function(render_buffer_id)
	{
		return this.scene.render_buffer_array[render_buffer_id].target_name;
	}
	
	this.create_scene_sequence_target=function(
			target_sequence_id,render_buffer_id,scene_target_array)
	{
		create_scene_sequence_target_routine(
					target_sequence_id,render_buffer_id,scene_target_array,this.scene);
	}
	this.destroy_scene_sequence_target=function(
			target_sequence_id,render_buffer_id,scene_target_array)
	{
		destroy_scene_sequence_target_routine(
					target_sequence_id,render_buffer_id,scene_target_array,this.scene);
	}
	this.draw_scene_sequence_target=function(
			target_sequence_id,render_buffer_id,scene_target_array)
	{
		return draw_scene_sequence_target_routine(
					target_sequence_id,render_buffer_id,scene_target_array,this.scene);
	}
	this.complete_render_target=async function(render_buffer_id)
	{
		var render_data=this.scene.render_buffer_array[render_buffer_id];
		
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
	
	this.process_scene=function()
	{
		if(this.scene.terminate_flag)
			return 0;
			
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
		
		var fun_array=this.scene.routine_array;
		this.scene.routine_array=new Array();
		for(var i=0,ni=fun_array.length;i<ni;i++)
			if(typeof(fun_array[i])=="function")
				if(fun_array[i](this.scene))
					this.scene.routine_array.push(fun_array[i]);
					
		return this.scene.parameter.engine_touch_time_length;
	}
}
