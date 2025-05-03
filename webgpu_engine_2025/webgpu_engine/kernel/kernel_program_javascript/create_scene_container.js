function create_scene_container_routine(my_webgpu)
{
	this.webgpu				=my_webgpu;
	this.scene_object		=new Object();
	this.event_scene_name	=null;
	this.terminate_flag		=false;
	
	this.scene_container_event_listener_array=new Array();
	for(var i=0,ni=this.webgpu.canvas.length;i<ni;i++){
		var p=new construct_scene_container_event_listener(i,this.webgpu.canvas,this);
		this.scene_container_event_listener_array[i]=p;
	}
	
	this.draw_scene=async function()
	{
		while(!(this.terminate_flag)){
			var engine_touch_time_length=Number.MAX_SAFE_INTEGER;
			var draw_render_collector	=new Object();
			var my_scene_object			=this.scene_object;
			this.scene_object			=new Object();

			this.webgpu.command_encoder=this.webgpu.device.createCommandEncoder();
			this.webgpu.compute_pass_encoder=this.webgpu.command_encoder.beginComputePass();
			
			var my_scene,scene_name_array=Object.keys(my_scene_object).sort();
			for(var i=0,ni=scene_name_array.length;i<ni;i++){
				if(typeof(my_scene=my_scene_object[scene_name_array[i]])!="object")
					continue;
				if(my_scene==null)
					continue;
				if(my_scene.terminate_flag)
					continue;
				this.scene_object[scene_name_array[i]]=my_scene;

				var si=my_scene.scene_interface;
				var my_engine_touch_time_length=si.process_scene(i);
				if(my_engine_touch_time_length<engine_touch_time_length)
					engine_touch_time_length=my_engine_touch_time_length;

				var set_system_buffer_flag=false;
				for(var j=0,nj=si.get_render_buffer_number();j<nj;j++){
					if(!(si.get_do_render_flag(j)))
						continue;
					set_system_buffer_flag=true;
					var target_name=si.get_target_name(j);
					if(!(Array.isArray(draw_render_collector[target_name])))
						draw_render_collector[target_name]=new Array();
					draw_render_collector[target_name].push(
					{
						scene_name			:	scene_name_array[i],
						render_buffer_id	:	j
					});
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
					this.scene_object[p[j].scene_name].scene_interface.
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
						this.scene_object[p[j].scene_name].scene_interface.
								draw_scene_target(scene_pass_array,pass_id,p[j].render_buffer_id);
	
					this.webgpu.render_pass_encoder.end();
					this.webgpu.render_pass_encoder=null;
				}
				for(var j=0,nj=p.length;j<nj;j++)
					this.scene_object[p[j].scene_name].scene_interface.
						destroy_scene_target(p[j].render_buffer_id,scene_pass_array);
			}
			
			this.webgpu.device.queue.submit([this.webgpu.command_encoder.finish()]);
			this.webgpu.command_encoder=null;
			
			await this.webgpu.device.queue.onSubmittedWorkDone();
			
			if(this.terminate_flag)
				break;
			
			var scene_name_array=Object.keys(this.scene_object);
			for(var i=0;(i<scene_name_array.length)&&(!(this.terminate_flag));i++){
				var my_scene=this.scene_object[scene_name_array[i]];
				for(var j=0,nj=my_scene.scene_interface.get_render_buffer_number();j<nj;j++){
					if(my_scene.terminate_flag||this.terminate_flag)
						break;
					if(my_scene.scene_interface.get_do_render_flag(j))
						await my_scene.scene_interface.complete_render_target(j);
				}
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
	this.url_scene_create=async function(url,
		client_scene_name,create_parameter,my_draw_canvas_id,user_process_bar_function)
	{
		var my_program=await import(url);
		var old_scene=this.scene_object[client_scene_name];
		if((typeof(old_scene)!="object")||(old_scene==null)){
			var new_scene=await my_program.create_scene(this.webgpu,
					my_draw_canvas_id,create_parameter,user_process_bar_function);
			old_scene=this.scene_object[client_scene_name];
			if((typeof(old_scene)!="object")||(old_scene==null))
				this.scene_object[client_scene_name]=new_scene;
			else
				new_scene.destroy();
		}
		return this.scene_object[client_scene_name];
	}
	this.this_scene_create=async function(
		client_scene_name,create_parameter,my_draw_canvas_id,user_process_bar_function)
	{
		var old_scene=this.scene_object[client_scene_name];
		if((typeof(old_scene)!="object")||(old_scene==null)){
			var new_scene=await create_scene(this.webgpu,
					my_draw_canvas_id,create_parameter,user_process_bar_function);
			old_scene=this.scene_object[client_scene_name];
			if((typeof(old_scene)!="object")||(old_scene==null))
				this.scene_object[client_scene_name]=new_scene;
			else
				new_scene.destroy();
		}
		return this.scene_object[client_scene_name];
	}
	this.destroy=function()
	{
		this.terminate_flag=true;
		
		var scene_name_array=Object.keys(this.scene_object);
		for(var i=0,ni=scene_name_array.length;i<ni;i++){
			var my_scene=this.scene_object[scene_name_array[i]];
			this.scene_object[scene_name_array[i]]=null;
			if((typeof(my_scene)=="object")&&(my_scene!=null))
				if(!(my_scene.terminate_flag))
					if(typeof(my_scene.destroy)=="function")
						my_scene.destroy();
		}
		this.scene_object=new Object();	
		
		for(var i=0,ni=this.scene_container_event_listener_array.length;i<ni;i++)
			this.scene_container_event_listener_array[i].destroy();
		this.scene_container_event_listener_array=new Array();
		
		if(this.webgpu!=null){
			this.webgpu.destroy();
			this.webgpu=null;
		}
		
		this.event_scene_name	=null;
		this.draw_scene			=null;
		this.url_scene_create	=null;
		this.this_scene_create	=null;
		this.destroy			=null;
	}
}