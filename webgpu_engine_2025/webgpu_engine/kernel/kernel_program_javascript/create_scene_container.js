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
			var p=my_collector[target_name_array[i]];

			for(var j=0,nj=p.length;j<nj;j++)
				p[j].scene_object.scene_interface.create_scene_target(p[j].target_parameter,scene_pass_array);

			for(var pass_id=0,pass_number=scene_pass_array.length;pass_id<pass_number;pass_id++){
				if((typeof(scene_pass_array[pass_id])!="object")||(scene_pass_array[pass_id]==null))
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
					p[j].scene_object.scene_interface.draw_scene_target(p[j].target_parameter,scene_pass_array,pass_id);
				
				if(my_collector_flag)
					scene_pass_array[pass_id].render_bundle=this.webgpu.render_pass_encoder.finish();
				else{		
					this.webgpu.render_pass_encoder.end();
					scene_pass_array[pass_id].render_bundle=null;
				}
				this.webgpu.render_pass_encoder=null;
			}

			for(var j=0,nj=p.length;j<nj;j++)
				p[j].scene_object.scene_interface.destroy_scene_target(p[j].target_parameter,scene_pass_array);
		}
	}
	
	this.draw_scene=async function()
	{
		while(!(this.terminate_flag)){
			var engine_touch_time_length=Number.MAX_SAFE_INTEGER;
			var draw_render_collector	=new Object();
			var bundle_render_collector	=new Object();
			var scene_interface_array	=new Array();
			var my_scene_object			=this.scene_object;
			this.scene_object			=new Object();

			var my_scene,scene_name_array=Object.keys(my_scene_object).sort();
			for(var scene_id=0,i=0,ni=scene_name_array.length;i<ni;i++){
				if(typeof(my_scene=my_scene_object[scene_name_array[i]])!="object")
					continue;
				if(my_scene==null)
					continue;
				if(my_scene.terminate_flag)
					continue;
				this.scene_object[scene_name_array[i]]=my_scene;

				var si=my_scene.scene_interface,scene_interface_flag=false,my_engine_touch_time_length;
				if((my_engine_touch_time_length=si.front_process_scene(scene_id++))<engine_touch_time_length)
					engine_touch_time_length=my_engine_touch_time_length;

				for(var target_par,j=0,nj=si.get_render_buffer_number();j<nj;j++){
					if((target_par=si.get_target_parameter(j)).do_render_flag){
						if(target_par.target_or_bundle_flag){
							if(!(Array.isArray(draw_render_collector[target_par.target_name])))
								draw_render_collector[target_par.target_name]=new Array();
							draw_render_collector[target_par.target_name].push(
							{
								scene_object		:	my_scene,
								target_parameter	:	target_par
							});
							scene_interface_flag=true;
						}else{
							if(!(Array.isArray(bundle_render_collector[target_par.target_name])))
									bundle_render_collector[target_par.target_name]=new Array();
							bundle_render_collector[target_par.target_name].push(
							{
								scene_object		:	my_scene,
								target_parameter	:	target_par
							});
						}
					}
				}
				
				if(scene_interface_flag)
					scene_interface_array.push(si);
			}
			
			this.process_render_collector(bundle_render_collector,true);

			this.webgpu.command_encoder		=this.webgpu.device.createCommandEncoder();
			this.webgpu.compute_pass_encoder=this.webgpu.command_encoder.beginComputePass();
			
			for(var i=0,ni=scene_interface_array.length;i<ni;i++)
				scene_interface_array[i].set_system_buffer_and_compute_component_location();
			
			this.webgpu.compute_pass_encoder.end();
			this.webgpu.compute_pass_encoder=null;
			
			this.process_render_collector(draw_render_collector,false);

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
				for(var j=0,nj=my_scene.scene_interface.get_render_buffer_number();j<nj;j++){
					if(my_scene.terminate_flag||this.terminate_flag)
						break;
					if(my_scene.scene_interface.get_target_parameter(j).do_render_flag)
						await my_scene.scene_interface.complete_render_target(j);
				}
			}
			if(this.terminate_flag)
				break;
			var scene_name_array=Object.keys(this.scene_object);
			for(var i=0,ni=scene_name_array.length;i<ni;i++){
				if(this.terminate_flag)
					break;
				var my_scene=this.scene_object[scene_name_array[i]];
				if(!(my_scene.terminate_flag))
					my_scene.scene_interface.back_process_scene();
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