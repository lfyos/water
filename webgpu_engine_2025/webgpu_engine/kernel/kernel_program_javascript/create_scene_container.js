function create_scene_container_main(my_webgpu)
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
			var p=my_collector[target_name_array[i]];
			var do_render_flag=new Array();
			var scene_pass_array=new Array();

			for(var j=0,nj=p.length;j<nj;j++)
				do_render_flag[j]=p[j].scene_object.scene_interface.	
					scene_target_begin(p[j].target_id,scene_pass_array);
			
			for(var pass_id=0,pass_number=scene_pass_array.length;pass_id<pass_number;pass_id++){
				if((typeof(scene_pass_array[pass_id])!="object")||(scene_pass_array[pass_id]==null))
					continue;
				
				var my_pass_descriptor=scene_pass_array[pass_id].pass_descriptor;
				if(my_collector_flag)
					this.webgpu.render_pass_encoder=this.webgpu.device.createRenderBundleEncoder(my_pass_descriptor);
				else
					this.webgpu.render_pass_encoder=this.webgpu.command_encoder.beginRenderPass(my_pass_descriptor);
					
				if((typeof(this.webgpu.render_pass_encoder)!="object")||(this.webgpu.render_pass_encoder==null))
					continue;
				for(var j=0,nj=p.length;j<nj;j++)
					if(do_render_flag[j])
						p[j].scene_object.scene_interface.draw_scene_target(
								p[j].target_id,scene_pass_array,pass_id);
				
				if(my_collector_flag)
					scene_pass_array[pass_id].render_bundle=this.webgpu.render_pass_encoder.finish();
				else{		
					this.webgpu.render_pass_encoder.end();
					scene_pass_array[pass_id].render_bundle=null;
				}
				this.webgpu.render_pass_encoder=null;
			}
			
			for(var j=0,nj=p.length;j<nj;j++)
				p[j].scene_object.scene_interface.scene_target_end(p[j].target_id,scene_pass_array);
		}
	}
	this.draw_scene=async function()
	{
		while(!(this.terminate_flag)){
			this.webgpu.command_encoder		=this.webgpu.device.createCommandEncoder();
			this.webgpu.compute_pass_encoder=this.webgpu.command_encoder.beginComputePass();
			
			var scene_touch_time_length		=Number.MAX_SAFE_INTEGER;
			var draw_render_collector		=new Object();
			var bundle_render_collector		=new Object();
			
			var my_scene_object				=this.scene_object;
			this.scene_object				=new Object();
			
			var scene_name_array=Object.keys(my_scene_object).sort();
			for(var scene_id=0,i=0,ni=scene_name_array.length;i<ni;i++){
				var my_scene=my_scene_object[scene_name_array[i]];
				if((typeof(my_scene)!="object")||(my_scene==null))
					continue;
				if(my_scene.terminate_flag)
					continue;
				this.scene_object[scene_name_array[i]]=my_scene;

				var my_scene_touch_time_length=my_scene.scene_interface.front_process_scene(scene_id++);
				if(my_scene_touch_time_length<scene_touch_time_length)
					scene_touch_time_length=my_scene_touch_time_length;

				var effective_target_number=0;
				for(var j=0,nj=my_scene.scene_interface.get_target_number();j<nj;j++){
					var target_par=my_scene.scene_interface.get_target_parameter(j);
					if(target_par==null)
						continue;
					if(!(target_par.do_render_flag))
						continue;
					var collect_item={
						scene_object	:	my_scene,
						target_id		:	target_par.target_id
					};
					if(target_par.target_or_bundle_flag){
						if(!(Array.isArray(draw_render_collector[target_par.target_name])))
							draw_render_collector[target_par.target_name]=new Array();
						draw_render_collector[target_par.target_name].push(collect_item);
					}else{
						if(!(Array.isArray(bundle_render_collector[target_par.target_name])))
								bundle_render_collector[target_par.target_name]=new Array();
						bundle_render_collector[target_par.target_name].push(collect_item);
					}
					effective_target_number++;
				}
				if(effective_target_number>0)
					my_scene.scene_interface.set_system_buffer_and_compute_component_location();
			}
			
			this.webgpu.compute_pass_encoder.end();
			this.webgpu.compute_pass_encoder=null;
			
			this.process_render_collector(bundle_render_collector,	true);
			this.process_render_collector(draw_render_collector,	false);

			this.webgpu.device.queue.submit([this.webgpu.command_encoder.finish()]);
			this.webgpu.command_encoder=null;

			await this.webgpu.device.queue.onSubmittedWorkDone();
			
			if(this.terminate_flag)
				break;
			var scene_name_array=Object.keys(my_scene_object).sort();
			for(var i=0,ni=scene_name_array.length;i<ni;i++){
				if(this.terminate_flag)
					break;
				var my_scene=this.scene_object[scene_name_array[i]];
				if(my_scene.terminate_flag)
					continue;
				for(var j=0,nj=my_scene.scene_interface.get_target_number();j<nj;j++){
					if(my_scene.terminate_flag||this.terminate_flag)
						break;
					if(my_scene.scene_interface.get_target_parameter(j).do_render_flag)
						await my_scene.scene_interface.scene_target_complete(j);
				}
			}
			if(this.terminate_flag)
				break;
			var scene_name_array=Object.keys(my_scene_object).sort();
			for(var i=0,ni=scene_name_array.length;i<ni;i++){
				if(this.terminate_flag)
					break;
				var my_scene=this.scene_object[scene_name_array[i]];
				if(my_scene.terminate_flag)
					continue;
				my_scene.scene_interface.back_process_scene();
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
		my_event_scene_name,create_parameter,my_draw_canvas_id,user_process_bar_function)
	{
		var old_scene=this.scene_object[my_event_scene_name];
		if((typeof(old_scene)!="object")||(old_scene==null)){
			var new_scene=await create_scene_program.create_scene(this.webgpu,
					my_draw_canvas_id,create_parameter,user_process_bar_function);
			old_scene=this.scene_object[my_event_scene_name];
			if((typeof(old_scene)!="object")||(old_scene==null))
				this.scene_object[my_event_scene_name]=new_scene;
			else
				new_scene.destroy();
		}
		if(this.event_scene_name==null)
			this.event_scene_name=my_event_scene_name;
		return this.scene_object[my_event_scene_name];
	}
	this.set_event_scene_name=function(my_event_scene_name)
	{
		this.event_scene_name=my_event_scene_name;
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

async function create_scene_container_routine(my_canvas_array)
{
	var my_webgpu;
	if((my_webgpu=await create_webgpu(my_canvas_array)).error_flag)
		return null;
	var my_scene_container=new create_scene_container_main(my_webgpu);
	my_scene_container.draw_scene();
	return my_scene_container;
}
