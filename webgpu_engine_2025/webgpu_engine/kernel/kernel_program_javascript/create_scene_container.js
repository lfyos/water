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
	this.process_render_collector=function(my_collector,my_collector_flag)
	{
		var target_name_array=Object.keys(my_collector).sort();
		for(var i=0,ni=target_name_array.length;i<ni;i++){
			var scene_pass_array=new Array();
			var do_render_flag=new Array();
			var p=my_collector[target_name_array[i]];
			
			for(var j=0,nj=p.length;j<nj;j++)	//interface scene_target_begin
				do_render_flag[j]=p[j].scene_object.scene_interface.	
					scene_target_begin(p[j].target_id,scene_pass_array);
			
			for(var pass_id=0,pass_number=scene_pass_array.length;pass_id<pass_number;pass_id++){
				if(typeof(scene_pass_array[pass_id])!="object")
					continue;
				if(scene_pass_array[pass_id]==null)
					continue;
				var my_pass_descriptor=scene_pass_array[pass_id].pass_descriptor;
				if(my_collector_flag)
					this.webgpu.render_pass_encoder=this.webgpu.device.createRenderBundleEncoder(my_pass_descriptor);
				else
					this.webgpu.render_pass_encoder=this.webgpu.command_encoder.beginRenderPass(my_pass_descriptor);
					
				if(typeof(this.webgpu.render_pass_encoder)!="object")
					continue;
				if(this.webgpu.render_pass_encoder==null)
					continue;
				for(var j=0,nj=p.length;j<nj;j++)
					if(do_render_flag[j])		//interface draw_scene_target
						p[j].scene_object.scene_interface.
								draw_scene_target(p[j].target_id,scene_pass_array,pass_id);
				if(my_collector_flag)
					scene_pass_array[pass_id].render_bundle=this.webgpu.render_pass_encoder.finish();
				else{		
					this.webgpu.render_pass_encoder.end();
					scene_pass_array[pass_id].render_bundle=null;
				}
				this.webgpu.render_pass_encoder=null;
			}
			for(var j=0,nj=p.length;j<nj;j++)							//interface scene_target_end
				p[j].scene_object.scene_interface.scene_target_end(p[j].target_id,scene_pass_array);
		}
	}
	this.draw_scene=async function()
	{
		while(!(this.terminate_flag)){
			var scene_touch_time_length		=Number.MAX_SAFE_INTEGER;
			var draw_render_collector		=new Object();
			var bundle_render_collector		=new Object();
			var scene_interface_collector	=new Array();
			var my_scene_object				=this.scene_object;
			this.scene_object				=new Object();

			var scene_name_array=Object.keys(my_scene_object).sort();
			for(var scene_id=0,i=0,ni=scene_name_array.length;i<ni;i++){
				var my_scene=my_scene_object[scene_name_array[i]];
				if(typeof(my_scene)!="object")
					continue;
				if(my_scene==null)
					continue;
				if(my_scene.terminate_flag)
					continue;
				this.scene_object[scene_name_array[i]]=my_scene;

				var si=my_scene.scene_interface;
				scene_interface_collector.push(si);
				
				var my_scene_touch_time_length=si.front_process_scene(scene_id++);	//interface front_process_scene
				if(my_scene_touch_time_length<scene_touch_time_length)
					scene_touch_time_length=my_scene_touch_time_length;

				for(var j=0,nj=si.get_target_number();j<nj;j++){				//interface get_target_number
					var target_par=si.get_target_parameter(j);					//interface get_target_parameter
					if(target_par==null)
						continue;
					if(!(target_par.do_render_flag))
						continue;
					if(target_par.target_or_bundle_flag){
						if(!(Array.isArray(draw_render_collector[target_par.target_name])))
							draw_render_collector[target_par.target_name]=new Array();
						draw_render_collector[target_par.target_name].push(
						{
							scene_object	:	my_scene,
							target_id		:	target_par.target_id
						});
					}else{
						if(!(Array.isArray(bundle_render_collector[target_par.target_name])))
								bundle_render_collector[target_par.target_name]=new Array();
						bundle_render_collector[target_par.target_name].push(
						{
							scene_object	:	my_scene,
							target_id		:	target_par.target_id
						});
					}
				}
			}
			
			this.webgpu.command_encoder		=this.webgpu.device.createCommandEncoder();
			this.webgpu.compute_pass_encoder=this.webgpu.command_encoder.beginComputePass();
			
			for(var i=0,ni=scene_interface_collector.length;i<ni;i++)				
				scene_interface_collector[i].set_system_buffer_and_compute_component_location();
													//interface set_system_buffer_and_compute_component_location

			this.webgpu.compute_pass_encoder.end();
			this.webgpu.compute_pass_encoder=null;
			
			this.process_render_collector(bundle_render_collector,	true);
			this.process_render_collector(draw_render_collector,	false);

			this.webgpu.device.queue.submit([this.webgpu.command_encoder.finish()]);
			this.webgpu.command_encoder=null;

			await this.webgpu.device.queue.onSubmittedWorkDone();
			
			if(this.terminate_flag)
				break;
			var scene_name_array=Object.keys(this.scene_object);
			for(var i=0,ni=scene_name_array.length;i<ni;i++){
				if(this.terminate_flag)
					break;
				var my_scene=this.scene_object[scene_name_array[i]];
				for(var j=0,nj=my_scene.scene_interface.get_target_number();j<nj;j++){
					if(my_scene.terminate_flag||this.terminate_flag)
						break;
					if(my_scene.scene_interface.get_target_parameter(j).do_render_flag)
						await my_scene.scene_interface.complete_render_target(j);	//interface complete_render_target
				}
			}
			if(this.terminate_flag)
				break;
			var scene_name_array=Object.keys(this.scene_object);
			for(var i=0,ni=scene_name_array.length;i<ni;i++){
				if(this.terminate_flag)
					break;
				var my_scene=this.scene_object[scene_name_array[i]];
				if(my_scene.terminate_flag)
					continue;
				my_scene.scene_interface.back_process_scene();						//interface back_process_scene
			}
			if(this.terminate_flag)
				break;

			await new Promise((resolve)=>
			{
				window.requestAnimationFrame(resolve);
				setTimeout(resolve,scene_touch_time_length/1000000);
			});
		}
	}	
	this.create_scene=async function(create_scene_program,
		client_scene_name,create_parameter,my_draw_canvas_id,user_process_bar_function)
	{
		var old_scene=this.scene_object[client_scene_name];
		if((typeof(old_scene)!="object")||(old_scene==null)){
			var new_scene=await create_scene_program.create_scene(this.webgpu,
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
		this.url_create_scene	=null;
		this.this_create_scene	=null;
	}
}