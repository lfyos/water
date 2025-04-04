function create_scene_container_routine(my_webgpu)
{
	this.webgpu				=my_webgpu;
	this.scene_array		=new Array();
	this.current_scene_id	=0;
	this.terminate_flag		=false;
	
	this.scene_container_event_listener_array=new Array();
	for(var i=0,ni=this.webgpu.canvas.length;i<ni;i++){
		var p=new construct_scene_container_event_listener(i,this.webgpu.canvas,this);
		this.scene_container_event_listener_array[i]=p;
	}
	this.draw_scene_array=async function()
	{
		while(!(this.terminate_flag)){
			var engine_touch_time_length=0;
			var draw_render_collector	=new Object();
			var my_scene_array			=this.scene_array;
			this.scene_array			=new Array();

			this.webgpu.command_encoder=this.webgpu.device.createCommandEncoder();
			this.webgpu.compute_pass_encoder=this.webgpu.command_encoder.beginComputePass();
			
			for(var my_scene,i=0,ni=my_scene_array.length;i<ni;i++){
				if(typeof(my_scene=my_scene_array[i])!="object")
					continue;
				if(my_scene==null)
					continue;
				if(my_scene.terminate_flag)
					continue;
				var my_scene_index_id=this.scene_array.length;
				this.scene_array[my_scene_index_id]=my_scene;

				var si=my_scene.scene_interface;
				var my_engine_touch_time_length=si.process_scene();
				
				if(engine_touch_time_length=0)
					engine_touch_time_length=my_engine_touch_time_length;
				else if(my_engine_touch_time_length<engine_touch_time_length)
					engine_touch_time_length=my_engine_touch_time_length;
				
				var set_system_buffer_flag=false;
				for(var j=0,nj=si.get_render_buffer_number();j<nj;j++)
					if(si.get_do_render_flag(j)){
						var target_name=si.get_target_name(j);
						if(!(Array.isArray(draw_render_collector[target_name])))
							draw_render_collector[target_name]=new Array();
						draw_render_collector[target_name].push(
						{
							scene_id		:my_scene_index_id,
							render_buffer_id:j,
							project_matrix	:si.set_scene_target(my_scene_index_id,j)
						});
						set_system_buffer_flag=true;
					}
				if(set_system_buffer_flag){
					si.set_system_buffer();
					si.compute_scene_component_location();
				}
			}
			this.webgpu.compute_pass_encoder.end();
			this.webgpu.compute_pass_encoder=null;
			
			var target_name_array=Object.keys(draw_render_collector).sort();
			for(var i=0,ni=target_name_array.length;i<ni;i++){
				var scene_pass_array=new Array();
				var p=draw_render_collector[target_name_array[i]];
				
				for(var j=0,nj=p.length;j<nj;j++)
					this.scene_array[p[j].scene_id].scene_interface.
						create_scene_target(p[j].render_buffer_id,scene_pass_array);
						
				for(var pass_id=0,pass_number=scene_pass_array.length;pass_id<pass_number;pass_id++){
					if((typeof(scene_pass_array[pass_id])!="object")||(scene_pass_array[pass_id]==null))
						continue;
					this.webgpu.render_pass_encoder=this.webgpu.command_encoder.
							beginRenderPass(scene_pass_array[pass_id].pass_descriptor);
					if(typeof(this.webgpu.render_pass_encoder)!="object")
						continue;
					if(this.webgpu.render_pass_encoder==null)
						continue;
					for(var j=0,nj=p.length;j<nj;j++)
						this.scene_array[p[j].scene_id].scene_interface.draw_scene_target(
							p[j].project_matrix,scene_pass_array,pass_id,p[j].render_buffer_id);

					this.webgpu.render_pass_encoder.end();
					this.webgpu.render_pass_encoder=null;
				}
				for(var j=0,nj=p.length;j<nj;j++)
					this.scene_array[p[j].scene_id].scene_interface.
						destroy_scene_target(p[j].render_buffer_id,scene_pass_array);
			}
			
			this.webgpu.device.queue.submit([this.webgpu.command_encoder.finish()]);
			this.webgpu.command_encoder=null;
			
			await this.webgpu.device.queue.onSubmittedWorkDone();
			
			if(this.terminate_flag)
				break;
			
			for(var i=0;i<this.scene_array.length;i++)
				for(var j=0,nj=this.scene_array[i].scene_interface.get_render_buffer_number();j<nj;j++)
					if(!(this.scene_array[i].terminate_flag))
						if(this.scene_array[i].scene_interface.get_do_render_flag(j)){
							await this.scene_array[i].scene_interface.complete_render_target(j);
							if(this.terminate_flag)
								break;
						}
			if(this.terminate_flag)
				break;
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
		this.terminate_flag=true;
		
		this.webgpu.destroy();
		this.webgpu=null;
		
		for(var i=0;i<this.scene_array.length;i++)
			if(this.scene_array[i]!=null){
				if(!(this.scene_array[i].terminate_flag))
					if(typeof(this.scene_array[i].destroy)=="function")
						this.scene_array[i].destroy();
				this.scene_array[i]=null;
			}
		this.draw_scene_array.length=0;	
		
		for(var i=0,ni=this.scene_container_event_listener_array.length;i<ni;i++)
			this.scene_container_event_listener_array[i].destroy();
		this.scene_container_event_listener_array.length=0;
		
		this.url_scene_create		=null;
		this.this_scene_create		=null;
		this.destroy				=null;
	}
}