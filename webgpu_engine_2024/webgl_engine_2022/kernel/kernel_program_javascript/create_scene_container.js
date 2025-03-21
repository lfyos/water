function create_scene_container_routine(my_webgpu)
{
	this.webgpu		=my_webgpu;
	this.scene_array=new Array();
	
	this.draw_scene_array=async function()
	{
		while(true){
			var engine_touch_time_length=0;
			var draw_render_collector	=new Array();
			var my_scene_array			=this.scene_array;
			this.scene_array			=new Array();

			this.webgpu.command_encoder=this.webgpu.device.createCommandEncoder();
			
			for(var i=0;i<my_scene_array.length;i++){
				var my_scene;
				if((my_scene=my_scene_array[i])==null)
					continue;
				if(my_scene.terminate_flag)
					continue;
				this.scene_array.push(my_scene);
				var my_engine_touch_time_length=my_scene.scene_interface.process_scene();
				if(engine_touch_time_length=0)
					engine_touch_time_length=my_engine_touch_time_length;
				else if(my_engine_touch_time_length<engine_touch_time_length)
					engine_touch_time_length=my_engine_touch_time_length;
				
				var set_system_buffer_flag=false;
				for(var j=0,nj=my_scene.scene_interface.get_render_buffer_number();j<nj;j++){
					if(my_scene.scene_interface.get_do_render_flag(j)){
						var target_name=my_scene.scene_interface.get_target_name(j);
						if(!(Array.isArray(draw_render_collector[target_name])))
							draw_render_collector[target_name]=new Array();
						draw_render_collector[target_name].push(
							{
								scene_id			:	this.scene_array.length-1,
								render_buffer_id	:	j
							});
						set_system_buffer_flag=true;
					}
				}
				if(set_system_buffer_flag)
					my_scene.scene_interface.set_system_buffer();
			}
			
			var target_name_array=Object.keys(draw_render_collector).sort();
			for(var i=0,ni=target_name_array.length;i<ni;i++){
				var scene_target_array=new Array();
				var p=draw_render_collector[target_name_array[i]];
				for(var target_sequence_id=0;;target_sequence_id++){
					for(var j=0,nj=p.length;j<nj;j++)
						ret_val+=this.scene_array[p[j].scene_id].
							scene_interface.create_scene_sequence_target(
							target_sequence_id,p[j].render_buffer_id,scene_target_array);
					var scene_target;
					if(typeof(scene_target=scene_target_array[target_sequence_id])!="object")
						break;
					if(scene_target==null)
						break;

					this.webgpu.render_pass_encoder=this.webgpu.command_encoder.
							beginRenderPass(scene_target.pass_descriptor);
					if(typeof(this.webgpu.render_pass_encoder)!="object")
						break;
					if(this.webgpu.render_pass_encoder==null)
						break;
						
					var ret_val=0;
					for(var j=0,nj=p.length;j<nj;j++)
						ret_val+=this.scene_array[p[j].scene_id].scene_interface.
								draw_scene_sequence_target(target_sequence_id,
									p[j].render_buffer_id,scene_target_array);
					
					this.webgpu.render_pass_encoder.end();
					this.webgpu.render_pass_encoder=null;
					
					for(var j=0,nj=p.length;j<nj;j++)
						ret_val+=this.scene_array[p[j].scene_id].
							scene_interface.destroy_scene_sequence_target(
							target_sequence_id,p[j].render_buffer_id,scene_target_array);
							
					if(ret_val<=0)
						break;	
				}
			}
			
			this.webgpu.device.queue.submit([this.webgpu.command_encoder.finish()]);
			this.webgpu.command_encoder=null;
			
			await this.webgpu.device.queue.onSubmittedWorkDone();
			
			for(var i=0;i<this.scene_array.length;i++)
				for(var j=0,nj=this.scene_array[i].scene_interface.get_render_buffer_number();j<nj;j++)
					if(!(this.scene_array[i].terminate_flag))
						if(this.scene_array[i].scene_interface.get_do_render_flag(j))
							await this.scene_array[i].scene_interface.complete_render_target(j);
							
			await new Promise((resolve)=>
			{
				window.requestAnimationFrame(resolve);
				if(engine_touch_time_length>0)
					setTimeout(resolve,engine_touch_time_length/1000000);
			});
		}
	}	
	this.url_scene_create=async function(url,create_parameter,my_draw_canvas_id,user_process_bar_function)
	{
		var my_program	=await import(url);
		var my_scene	=await my_program.create_scene(
				this.webgpu,my_draw_canvas_id,create_parameter,user_process_bar_function);
		this.scene_array[this.scene_array.length]=my_scene;
		
		return my_scene;
	}
	this.this_scene_create=async function(create_parameter,my_draw_canvas_id,user_process_bar_function)
	{
		var my_scene=await create_scene(this.webgpu,my_draw_canvas_id,create_parameter,user_process_bar_function);
		this.scene_array[this.scene_array.length]=my_scene;
		
		return my_scene;
	}
	this.destroy=function()
	{
		for(var i=0;i<this.scene_array.length;i++)
			if(this.scene_array[i]!=null){
				if(!(this.scene_array[i].terminate_flag))
					if(typeof(this.scene_array[i].destroy)=="function")
						this.scene_array[i].destroy();
				this.scene_array[i]=null;
			}
		this.canvas_array	=null;
		this.scene_array	=null;
		this.scene_draw		=null;
		this.scene_create	=null;
		this.destroy		=null;

	}
}