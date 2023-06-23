function construct_render_routine(my_webgpu,
	my_url,my_user_name,my_pass_word,my_language_name,create_data)
{
    var my_channel_id					=create_data[0]
    var	my_render_initialize_data		=create_data[1];
	var	my_part_initialize_data			=create_data[2];
	var	my_component_initialize_data	=create_data[3];
	var	render_data						=create_data[4];
	
	var component_number				=render_data[0];
	var render_number					=render_data[1];
	var modifier_container_number		=render_data[2];
	var camera_component_id				=render_data[3];
	
    this.link_name						=render_data[4];
    this.title							=render_data[5];
    this.parameter						=render_data[6];
    
    this.render_initialize_data=new Array();
    for(var i=0,ni=my_render_initialize_data.length-1;i<ni;i+=2){
		var my_data		=my_render_initialize_data[i+0];
		var render_id	=my_render_initialize_data[i+1];
		this.render_initialize_data[render_id]=my_data;
	};

	this.part_initialize_data=new Array();
	for(var i=0,ni=my_part_initialize_data.length-1;i<ni;i+=3){
		var my_data		=my_part_initialize_data[i+0];
		var render_id	=my_part_initialize_data[i+1];
		var part_id		=my_part_initialize_data[i+2];
		if(typeof(this.part_initialize_data[render_id])=="undefined")
			this.part_initialize_data[render_id]=new Array();
		this.part_initialize_data[render_id][part_id]=my_data;
	};
	
	this.component_initialize_data=new Array(component_number);
	for(var i=0;i<component_number;i++)
		this.component_initialize_data[i]=new Array();
	for(var i=0,ni=my_component_initialize_data.length-1;i<ni;i+=3){
		var my_data				=my_component_initialize_data[i+0];
		var my_component_id		=my_component_initialize_data[i+1];
		var my_driver_id		=my_component_initialize_data[i+2];
		this.component_initialize_data[my_component_id][my_driver_id]=my_data;
	}

	this.engine_start_time			=(new Date()).getTime();
	this.last_event_time			=this.engine_start_time;

	this.webgpu						=my_webgpu;
	this.url						=my_url;
	this.url_without_channel		=this.url
					+"?user_name="	+my_user_name
					+"&pass_word="	+my_pass_word
					+"&language="	+my_language_name;
	this.url_with_channel			=this.url_without_channel
					+"&channel="	+my_channel_id.toString()
	
	this.user_event_processor		=new Object();
	this.user_call_processor		=new Object();
	
	this.system_event_processor		=new Object();
	this.system_call_processor		=new Object();

	this.component_event_processor	=new Array(component_number);
	this.component_call_processor	=new Array(component_number);
	
	this.event_component			=new Object();

	this.target_processor			=new Array();
	
	this.routine_array				=new Array();
	
	this.current=
	{
		render_buffer_id			:	0,
		target_id					:	0,
		target_viewport_id			:	0,
		camera_id					:	0,
		camera_component_id			:	0,
		high_or_low_precision_flag	:	true,

		loading_number				:	-1,
		loaded_length				:	-1,
		
		calling_server_number		:	0
	};
	this.view=
	{
		x		:	-0.0,
		y		:	-0.0
	};
	this.view_bak=
	{
		x				:	-this.view.x,
		y				:	-this.view.y,
		
		canvas_width	:	-1,
		canvas_height	:	-1,
		
		component		:	-2,
		
		body			:	-2,
		face			:	-2,
		loop			:	-2,
		edge			:	-2,
		vertex			:	-2,
		point			:	-2,
		
		depth			:	-2,
		value			:	-2,
		
		in_canvas_flag	:	false
	};
	
	this.caller						=new construct_server_caller(this);
	this.event_listener				=new construct_event_listener(this);
    this.computer					=new construct_computation_object();
     
    this.target_buffer				=new Array();

	this.clip_plane_array			=new Array();
	this.clip_plane_matrix_array	=new Array();
	
	this.render_driver				=new Array(render_number);
	this.part_array					=new Array(render_number);
	for(var i=0;i<render_number;i++){
		this.render_driver[i]		=null;
		this.part_array[i]			=new Array();
	}

	this.component_location_data	=new construct_component_location_object(component_number,this.computer,this.webgpu);
	this.component_render_data		=new construct_component_render_parameter(render_number);
	this.modifier_time_parameter	=new construct_modifier_time_parameter(modifier_container_number);
	this.vertex_data_downloader		=new construct_download_vertex_data(this.webgpu,this.parameter.max_loading_number);
	this.camera						=new construct_camera_object(camera_component_id,this.component_location_data,this.computer);
	this.system_buffer				=new construct_system_buffer(this);
	this.deviceorientation			=new construct_deviceorientation(this.computer);
	this.utility					=new construct_render_utility(this);
	this.collector_loader			=new construct_collector_loader_object(this);
	
	this.engine_do_render_number	=new Array();
	
	this.pickup						=new construct_pickup_object();
	this.pickup_array				=[
		this.pickup.fork(),this.pickup.fork(),this.pickup.fork(),this.pickup.fork(),this.pickup.fork(),
		this.pickup.fork(),this.pickup.fork(),this.pickup.fork(),this.pickup.fork(),this.pickup.fork()
	];
	this.highlight					=this.pickup.fork();
	
	this.data_time_length			=1;
	this.render_time_length			=1;
	this.render_interval_length		=1;
	
	this.current_time				=0;
	this.modifier_current_time		=new Array(modifier_container_number);
	for(var i=0;i<modifier_container_number;i++)
		this.modifier_current_time[i]=0;
	this.browser_current_time		=0;
	
	this.do_execute_render_number	=0;
	this.collector_stack_version	=0;

	
	this.terminate_flag				=false;
	this.terminate=function()
	{
		fetch(this.url_with_channel+"&command=termination");
		this.terminate_flag=true;
	};
	
	this.render_component=function(render_list,render_buffer_id,parameter_channel_id,
				method_id,project_matrix,viewport,render_do_render_number,pass_do_render_number)
	{
		for(var last_render_id=-1,i=0,ni=render_list.length;i<ni;i++){
			var render_id			 =render_list[i][0];
			var part_id				 =render_list[i][1];
			var component_render_data=render_list[i][2];
			if(typeof(this.render_program.render_program[render_id])!="object")
    			continue;
			var shader_program=this.render_program.render_program[render_id].shader_program;
			if(typeof(shader_program)!="object")
    			continue;
			var draw_function=this.render_program.render_program[render_id].draw_function;
			if(typeof(draw_function)!="function")
    			continue;
			if(typeof(this.buffer_object.buffer_object[render_id])!="object")
    			continue;
	    	if(typeof(this.buffer_object.buffer_object[render_id][part_id])!="object")
		    	continue;
			if(this.part_array[render_id][part_id].find_error_flag)
				continue;
			if(typeof(this.engine_do_render_number[render_id])=="undefined")
				this.engine_do_render_number[render_id]={
						render_do_render_number	:	0,
						part_do_render_number	:	new Array()
				};
			if(typeof(this.engine_do_render_number[render_id].part_do_render_number[part_id])=="undefined")
				this.engine_do_render_number[render_id].part_do_render_number[part_id]=0;
			if(typeof(render_do_render_number[render_id])=="undefined")
				render_do_render_number[render_id]={
						render_do_render_number	:	0,
						part_do_render_number	:	new Array()
				};
			if(typeof(render_do_render_number[render_id].part_do_render_number[part_id])=="undefined")
				render_do_render_number[render_id].part_do_render_number[part_id]=0;
			if(typeof(pass_do_render_number[render_id])=="undefined")
				pass_do_render_number[render_id]={
						render_do_render_number	:	0,
						part_do_render_number	:	new Array()
				};
			if(typeof(pass_do_render_number[render_id].part_do_render_number[part_id])=="undefined")
				pass_do_render_number[render_id].part_do_render_number[part_id]=0;
			if(last_render_id!=render_id){
				last_render_id=render_id;
				this.gl.useProgram(shader_program);
			};
			
			this.gl.bindVertexArray(null);
			
		    draw_function(				method_id,  	parameter_channel_id,
		    	render_id,				part_id,		render_buffer_id,
		    	component_render_data,	project_matrix,		
		    	{
		    		viewport_x0		:	viewport[0],
		    		viewport_y0		:	viewport[1],
		    		viewport_width	:	viewport[2],
		    		viewport_height	:	viewport[3],
		    		target_width	:	viewport[4],
		    		target_height	:	viewport[5],
		    		target_flag		:	viewport[6]
		    	},
		    	{
		    		engine_render	:	(this.engine_do_render_number[render_id].render_do_render_number)++,
		    		engine_part		:	(this.engine_do_render_number[render_id].part_do_render_number[part_id])++,
			    		
		    		render_render	:	(render_do_render_number[render_id].render_do_render_number)++,
		    		render_part		:	(render_do_render_number[render_id].part_do_render_number[part_id])++,
			    		
		    		pass_render		:	(pass_do_render_number[render_id].render_do_render_number)++,
		    		pass_part		:	(pass_do_render_number[render_id].part_do_render_number[part_id])++
		    	},
		    	this);
		};
	};
	this.render_routine=function()
	{
		if(typeof(this.render_data)=="undefined")
			return;

		var render_do_render_number=new Array();
		this.uniform_block.bind_system(this);
		this.gl.depthRange(0.0,1.0);

		for(var i=0,ni=this.render_data.length,draw_buffer_id=0;i<ni;i++){
			var render_buffer_id	=this.render_data[i][0];
			var target_id			=this.render_data[i][1];
			var do_render_number	=this.render_data[i][2];

			if(do_render_number<0)
				continue;

			this.render_data[i][2]--;
			
			var render_target_id	= this.target_buffer[target_id][0][0];
			var parameter_channel_id= this.target_buffer[target_id][0][1];
			var width				= this.target_buffer[target_id][0][2];
			var height				= this.target_buffer[target_id][0][3];
			var render_target_number= this.target_buffer[target_id][0][4];
			var viewport			= this.target_buffer[target_id][1];

			var project_matrix=this.camera.compute_camera_data(render_buffer_id);
			var render_framebuffer;
			var this_draw_buffer_id=-1;
			
			if((width>0)&&(height>0)){
				if(typeof(this.target)=="undefined")
					this.target=new Array();
				
				if(typeof(this.target[render_target_id])=="undefined")
					this.target[render_target_id]=new construct_framebuffer(
							this.gl,width,height,render_target_number);
				else if((this.target[render_target_id].render_target_number!=render_target_number)
					||(this.target[render_target_id].width!=width)||(this.target[render_target_id].height!=height))
				{
					this.target[render_target_id].delete_framebuffer(this.gl);
					this.target[render_target_id]=new construct_framebuffer(this.gl,width,height,render_target_number);
				};
				if(typeof(this.target[target_id])=="undefined")
					this.target[target_id]=new construct_framebuffer(this.gl,width,height,render_target_number);
				
				this.target[target_id].project_matrix=project_matrix;
				this.gl.bindFramebuffer(this.gl.FRAMEBUFFER,this.target[render_target_id].frame);
				
				render_framebuffer=this.target[render_target_id];
			}else{
				width	=this.canvas.width;
				height	=this.canvas.height;
				
				this.gl.bindFramebuffer(this.gl.FRAMEBUFFER,null);
				
				this_draw_buffer_id=draw_buffer_id++;
				{
					//According to draw_buffer_id, engine identifies drawing buffer here.Its parameter is as following.
					//GL_NONE,GL_FRONT_LEFT,GL_FRONT_RIGHT,GL_BACK_LEFT,GL_BACK_RIGHT,GL_FRONT,GL_BACK,GL_LEFT,GL_RIGHT,GL_FRONT_AND_BACK
					//At present, WebGL doesn't support multi-buffers. Maybe it will support in the future.
					//					2019.08.05
				};
				render_framebuffer=null;
			};
			this.uniform_block.bind_target(project_matrix,this.clip_plane_array[render_buffer_id],
					this.clip_plane_matrix_array[render_buffer_id],width,height,this_draw_buffer_id);
			this.viewport			=new Object();
			this.viewport.width		=width;
			this.viewport.height	=height;
			this.viewport.viewport	=viewport;
			
			if((target_id>=0)&&(target_id<(this.target_processor.length)))
				if(typeof(this.target_processor[target_id])=="object")
					if(typeof(this.target_processor[target_id].before_target_render)=="function")
						this.target_processor[target_id].before_target_render(
									target_id,render_target_id,width,height,render_target_number,this);

			var render_list=this.component_render_data.get_render_list(render_buffer_id);
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
		this.gl.bindFramebuffer(this.gl.FRAMEBUFFER,null);
		this.do_execute_render_number++;
	};
	this.render_request_start_time=0;
	this.append_routine_function=function(my_routine_function)
	{
		this.routine_array.push(my_routine_function);
		return this.routine_array.length-1;
	};
	this.process_routine_function=function()
	{
		var fun_array=this.routine_array;
		this.routine_array=new Array();
		for(var i=0,ni=fun_array.length;i<ni;i++)
			if(typeof(fun_array[i])=="function")
				if(fun_array[i](this))
					this.routine_array.push(fun_array[i]);
	};
	this.do_render=function(my_render_interval_length,processs_bar_object)
	{
		if(this.terminate_flag){
			processs_bar_object.process_bar_id=null;
			return this.terminate_flag;
		}
		var start_time=(new Date()).getTime();
		this.render_interval_length=my_render_interval_length;
		if(this.browser_current_time>0){
			var pass_time=(start_time-this.browser_current_time)*1000*1000;
			var new_current_time=this.modifier_time_parameter.webserver_current_time+pass_time;
			if(this.current_time<=new_current_time)
				this.current_time=new_current_time;
			else
				this.current_time++;
			for(var i=0,ni=this.modifier_current_time.length;i<ni;i++){
				new_current_time =this.modifier_time_parameter.caculate_current_time(i)+pass_time;
				if(this.modifier_current_time[i]<new_current_time)
					this.modifier_current_time[i]=new_current_time;
				else
					this.modifier_current_time[i]++;
			}
		};
		
		this.process_routine_function();
		this.render_routine();

		do{
			if((this.render_request_start_time-this.last_event_time)<=this.parameter.download_minimal_time_length)
				if((this.render_request_start_time-this.engine_start_time)>this.parameter.download_start_time_length)
					break;

			for(var i=this.vertex_data_downloader.current_loading_mesh_number,ni=this.parameter.max_loading_number;i<ni;)
				i+=this.vertex_data_downloader.request_buffer_object_data(this);
			this.vertex_data_downloader.process_buffer_head_request_queue(this);
			
		}while(false);
		
		this.render_request(start_time,processs_bar_object);
		this.render_time_length=(new Date()).getTime()-start_time;
		
		return this.terminate_flag;
	};
	
	this.get_component_event_processor=function(my_component_name_or_id)
	{
		switch(typeof(my_component_name_or_id)){
		case "string":
			my_component_name_or_id=this.get_component_object_by_component_name(my_component_name_or_id);
			if(my_component_name_or_id==null)
				return null;
			
			my_component_name_or_id=my_component_name_or_id.component_id;
			break;
		case "number":
			break;
		default:
			return null;
		}
		if((my_component_name_or_id<0)||(my_component_name_or_id>=this.component_event_processor.length))
			return null;
		var ret_val=this.component_event_processor[my_component_name_or_id];
		return (typeof(ret_val)=="undefined")?null:ret_val;
	};
	this.get_component_call_processor=function(my_component_name_or_id)
	{
		switch(typeof(my_component_name_or_id)){
		case "string":
			my_component_name_or_id=this.get_component_object_by_component_name(my_component_name_or_id);
			if(my_component_name_or_id==null)
				return null;
			my_component_name_or_id=my_component_name_or_id.component_id;
			break;
		case "number":
			break;
		default:
			return null;
		}
		if((my_component_name_or_id<0)||(my_component_name_or_id>=this.component_call_processor.length))
			return null;
		
		var ret_val=this.component_call_processor[my_component_name_or_id];
		return (typeof(ret_val)=="undefined")?null:ret_val;
	};
	this.get_component_object_by_component_id=function(my_component_id)
	{
		if(my_component_id<0)
			return null;
		if(my_component_id>=this.component_array_sorted_by_id.length)
			return null;
		return this.component_array_sorted_by_id[my_component_id];
	};
	this.get_component_object_by_component_name=function(my_component_name)
	{
		for(var begin_pointer=0,end_pointer=this.component_array_sorted_by_name.length-1;begin_pointer<=end_pointer;){
			var middle_pointer=Math.floor((begin_pointer+end_pointer)/2.0);
			var array_component_name=this.component_array_sorted_by_name[middle_pointer].component_name;
			if(array_component_name<my_component_name)
				begin_pointer=middle_pointer+1;
			else if(array_component_name>my_component_name)
				end_pointer=middle_pointer-1;
			else 
				return this.component_array_sorted_by_name[middle_pointer];
		};
		return null;
	};
	this.get_component_processor=function(my_component_name)
	{
		var p,my_component_id;
		if(typeof(my_component_id=my_component_name)=="string"){
			if((p=this.get_component_object_by_component_name(my_component_id))==null)
				return null;
			my_component_id=p.component_id;
		}
		if(typeof(my_component_id)=="number")
			if((my_component_id>=0)&&(my_component_id<(this.component_event_processor.length)))
				if(typeof(p=this.component_event_processor[my_component_id])=="object")
					if(p!=null){
						p.component_id=my_component_id;
						return p;
					}
		return null;
	};
	
	this.set_event_component=function(event_component_name)
	{
		var cep,component_object,event_component_id
		if((component_object=this.get_component_processor(event_component_name))==null)
			return;
		if((event_component_id=component_object.component_id)<0)
			return;
		if(event_component_id>=this.component_event_processor.length)
			return;
		if(typeof(cep=this.component_event_processor[event_component_id])!="object")
			return;
		if(cep==null)
			return;
		if(typeof(cep.set_event_component)!="function")
			return;
		cep.set_event_component(event_component_id,this);
		
		return;
	};
};