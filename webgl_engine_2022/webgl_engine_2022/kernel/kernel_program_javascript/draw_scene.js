
function draw_scene_routine(render)
{
	var render_data=render.component_render_data.render_data;
	var render_do_render_number=new Array();
	
	for(var i=0,ni=render_data.length,draw_buffer_id=0;i<ni;i++){
		var render_buffer_id	=render_data[i][0];
		var target_id			=render_data[i][1];
		var do_render_number	=render_data[i][2];

		if(do_render_number<0)
			continue;

		render_data[i][2]--;
			
		var render_target_id	= render.target_buffer[target_id][0][0];
		var parameter_channel_id= render.target_buffer[target_id][0][1];
		var width				= render.target_buffer[target_id][0][2];
		var height				= render.target_buffer[target_id][0][3];
		var render_target_number= render.target_buffer[target_id][0][4];
		var viewport			= render.target_buffer[target_id][1];

		var project_matrix=render.camera.compute_camera_data(render_buffer_id);
		var render_framebuffer;
		var this_draw_buffer_id=-1;
			
		if((width>0)&&(height>0)){
			if(typeof(render.target)=="undefined")
				render.target=new Array();
				
				if(typeof(render.target[render_target_id])=="undefined")
					render.target[render_target_id]=new construct_framebuffer(
							render.gl,width,height,render_target_number);
				else if((render.target[render_target_id].render_target_number!=render_target_number)
					||(render.target[render_target_id].width!=width)||(render.target[render_target_id].height!=height))
				{
					render.target[render_target_id].delete_framebuffer(render.gl);
					render.target[render_target_id]=new construct_framebuffer(render.gl,width,height,render_target_number);
				};
				if(typeof(render.target[target_id])=="undefined")
					render.target[target_id]=new construct_framebuffer(render.gl,width,height,render_target_number);
				
				render.target[target_id].project_matrix=project_matrix;
				render.gl.bindFramebuffer(render.gl.FRAMEBUFFER,render.target[render_target_id].frame);
				
				render_framebuffer=render.target[render_target_id];
			}else{
				width	=render.canvas.width;
				height	=render.canvas.height;
				
				render.gl.bindFramebuffer(render.gl.FRAMEBUFFER,null);
				
				this_draw_buffer_id=draw_buffer_id++;
				{
					//According to draw_buffer_id, engine identifies drawing buffer here.Its parameter is as following.
					//GL_NONE,GL_FRONT_LEFT,GL_FRONT_RIGHT,GL_BACK_LEFT,GL_BACK_RIGHT,GL_FRONT,GL_BACK,GL_LEFT,GL_RIGHT,GL_FRONT_AND_BACK
					//At present, WebGL doesn't support multi-buffers. Maybe it will support in the future.
					//					2019.08.05
				};
				render_framebuffer=null;
			};
			render.uniform_block.bind_target(project_matrix,render.clip_plane_array[render_buffer_id],
					render.clip_plane_matrix_array[render_buffer_id],width,height,this_draw_buffer_id);
			render.viewport			=new Object();
			render.viewport.width		=width;
			render.viewport.height	=height;
			render.viewport.viewport	=viewport;
			
			if((target_id>=0)&&(target_id<(render.target_processor.length)))
				if(typeof(render.target_processor[target_id])=="object")
					if(typeof(render.target_processor[target_id].before_target_render)=="function")
						render.target_processor[target_id].before_target_render(
									target_id,render_target_id,width,height,render_target_number,this);

			var render_list=render.component_render_data.get_render_list(render_buffer_id);
			var pass_do_render_number=new Array();
			
			for(var viewport_id=0,viewport_number=viewport.length;viewport_id<viewport_number;viewport_id++){
				var method_id	=viewport[viewport_id][4];
				var clear_color	=viewport[viewport_id][5];
				var my_viewport=[
					Math.round((viewport[viewport_id][0]+1.0)*width /2.0),
					Math.round((viewport[viewport_id][1]+1.0)*height/2.0),
					Math.round((viewport[viewport_id][2]    )*width /2.0),
					Math.round((viewport[viewport_id][3]    )*height/2.0),
					width,height,(render_framebuffer==null)?true:false
				];
				
				my_viewport[2]=(my_viewport[2]<1)?1:(my_viewport[2]);
				my_viewport[3]=(my_viewport[3]<1)?1:(my_viewport[3]);
				
				var my_clear_color=[0,0,0,0],my_clear_flag=0;
				if(typeof(clear_color)!="undefined"){
					this.gl.clearColor(clear_color[0],clear_color[1],clear_color[2],clear_color[3]);
					this.gl.clearDepth(1.0);
					this.gl.clearStencil(0);
					
					if((viewport_id<=0)&&(target_id==render_target_id)){
						this.gl.disable(this.gl.SCISSOR_TEST);
						this.gl.clear(this.gl.COLOR_BUFFER_BIT|this.gl.DEPTH_BUFFER_BIT|this.gl.STENCIL_BUFFER_BIT);
					}else{
						this.gl.enable(this.gl.SCISSOR_TEST);
						this.gl.scissor(my_viewport[0],my_viewport[1],my_viewport[2],my_viewport[3]); 
						this.gl.clear(this.gl.COLOR_BUFFER_BIT|this.gl.DEPTH_BUFFER_BIT|this.gl.STENCIL_BUFFER_BIT);
					};
					my_clear_color=[clear_color[0],clear_color[1],clear_color[2],clear_color[3]];
					my_clear_flag=1;
				};
				
				this.gl.disable(this.gl.SCISSOR_TEST);
				this.gl.viewport(my_viewport[0],my_viewport[1],my_viewport[2],my_viewport[3]);
				this.gl.enable(this.gl.DEPTH_TEST);
				
				this.uniform_block.bind_pass(method_id,my_clear_flag,my_viewport,my_clear_color);

				this.render_component(render_list,render_buffer_id,parameter_channel_id,
					method_id,project_matrix,my_viewport,render_do_render_number,pass_do_render_number);
			};
			if((target_id>=0)&&(target_id<(this.target_processor.length)))
				if(typeof(this.target_processor[target_id])=="object")
					if(typeof(this.target_processor[target_id].after_target_render)=="function")
						this.target_processor[target_id].after_target_render(
									target_id,render_target_id,width,height,render_target_number,this);
		};
		this.do_execute_render_number++;
};

async function draw_scene(render)
{
	while(!(render.terminate_flag)){
		var start_time=(new Date()).getTime();
		if(render.browser_current_time>0){
			var pass_time=(start_time-render.browser_current_time)*1000*1000;
			var new_current_time=render.modifier_time_parameter.webserver_current_time+pass_time;
			if(render.current_time<=new_current_time)
				render.current_time=new_current_time;
			else
				render.current_time++;
			for(var i=0,ni=render.modifier_current_time.length;i<ni;i++){
				new_current_time =render.modifier_time_parameter.caculate_current_time(i)+pass_time;
				if(render.modifier_current_time[i]<new_current_time)
					render.modifier_current_time[i]=new_current_time;
				else
					render.modifier_current_time[i]++;
			}
		};
		
		var fun_array=render.routine_array;
		render.routine_array=new Array();
		for(var i=0,ni=fun_array.length;i<ni;i++)
			if(typeof(fun_array[i])=="function")
				if(fun_array[i](render))
					render.routine_array.push(fun_array[i]);
		
		draw_scene_routine(render);
		
		do{
			if((render.render_request_start_time-render.last_event_time)<=render.parameter.download_minimal_time_length)
				if((render.render_request_start_time-render.engine_start_time)>render.parameter.download_start_time_length)
					break;
					
			for(var i=render.vertex_data_downloader.current_loading_mesh_number,ni=render.parameter.max_loading_number;i<ni;)
				i+=render.vertex_data_downloader.request_buffer_object_data(render);
			render.vertex_data_downloader.process_buffer_head_request_queue(render);
		}while(false);
		
		render.render_time_length=(new Date()).getTime()-start_time;
		
		await new Promise(resolve=>
		{
			window.requestAnimationFrame(resolve);
			SetTimeout(resolve,render.parameter.engine_touch_time_length/1000000);
		});
	}
}