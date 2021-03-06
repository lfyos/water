function construct_render_routine(my_gl,my_user_name,my_pass_word,
				my_canvas,my_url,my_language_name,response_data)
{
	this.channel						=response_data[0].toString().trim();
    this.channel					   +="&user_name="+my_user_name;
    this.channel					   +="&pass_word="+my_pass_word;
    this.channel					   +="&language="+my_language_name;
    
	var	my_part_initialize_data			=response_data[1];
	var	my_instance_initialize_data		=response_data[2];
	var	render_data						=response_data[3];
	
	var component_number				=render_data[0];
	var render_number					=render_data[1];
	var modifier_container_number		=render_data[2];
	var camera_component_id				=render_data[3];
    this.link_name						=render_data[4];
    this.title							=render_data[5];
    this.parameter						=render_data[6];
    
	this.part_initialize_data=new Array();
	for(var i=0,ni=my_part_initialize_data.length-1;i<ni;i+=3){
		var my_data		=my_part_initialize_data[i+0];
		var render_id	=my_part_initialize_data[i+1];
		var part_id		=my_part_initialize_data[i+2];
		if(typeof(this.part_initialize_data[render_id])=="undefined")
			this.part_initialize_data[render_id]=new Array();
		this.part_initialize_data[render_id][part_id]=my_data;
	};
	
	this.instance_initialize_data=new Array(component_number);
	for(var i=0;i<component_number;i++)
		this.instance_initialize_data[i]=new Array();
	for(var i=0,ni=my_instance_initialize_data.length-1;i<ni;i+=3){
		var my_data				=my_instance_initialize_data[i+0];
		var my_component_id		=my_instance_initialize_data[i+1];
		var my_driver_id		=my_instance_initialize_data[i+2];
		this.instance_initialize_data[my_component_id][my_driver_id]=my_data;
	}

	this.gl							=my_gl;
	this.url						=my_url;
	this.url_and_channel			=my_url+"?channel="+this.channel;
	this.canvas						=my_canvas;
	this.language_name				=my_language_name;
	
	this.user_event_processor		=new Object();
	this.user_call_processor		=new Object();
	
	this.system_event_processor		=new Object();
	this.system_call_processor		=new Object();

	this.component_event_processor	=new Array(component_number);
	this.component_call_processor	=new Array(component_number);

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
		x		:	0.0,
		y		:	0.0,
		
        aspect	:	1.0
	};
	this.view_bak=
	{
		x				:	-this.view.x,
		y				:	-this.view.y,
		
		component		:	-2,
		
		body			:	-2,
		face			:	-2,
		loop			:	-2,
		edge			:	-2,
		vertex			:	-2,
		
		body_value		:	-2,
		face_value		:	-2,
		loop_value		:	-2,
		edge_value		:	-2,
		vertex_value	:	-2,
		
		point			:	-2,
		
		depth			:	-2,
		value			:	-2,
		
		aspect			:	-1
	};

	this.event_component			=new Object();

	this.event_listener				=new construct_event_listener(this);
	
    this.computer					=new construct_computation_object();
     
    this.target_buffer				=new Array();

	this.clip_plane_array			=new Array();
	this.clip_plane_matrix_array	=new Array();
	
	this.part_information			=new Array();

	this.component_location_data	=new construct_component_location_object(component_number,this.computer,this.gl);
	
	this.component_render_data		=new construct_component_render_object(render_number,this.parameter);

	this.modifier_time_parameter	=new construct_modifier_time_parameter(modifier_container_number);
	this.render_program				=new construct_program_object	(this.gl,this.parameter);
	this.buffer_object				=new construct_buffer_object	(this.gl,this.url,this.channel,this.parameter);
	this.camera						=new construct_camera_object	(camera_component_id,this.component_location_data,this.computer);
	this.uniform_block				=new construct_uniform_block_object(this.gl);
	this.deviceorientation			=new construct_deviceorientation(this.computer);
	this.utility					=new construct_render_utility(this);
	
	this.engine_do_render_number	=new Array();

	this.data_buffer				=new Array();
	this.pickup						=new construct_pickup_object();
	
	this.data_time_length			=1;
	this.render_time_length			=1;
	this.render_interval_length		=1;
	
	this.current_time				=0;
	this.modifier_current_time		=new Array(modifier_container_number);
	for(var i=0;i<modifier_container_number;i++)
		this.modifier_current_time[i]=0;
	
	this.browser_current_time		=0;
	
	this.do_execute_render_number			=0;
	this.do_render_request_response_number	=0;
	this.collector_stack_version			=0;

	this.process_part_component_id_and_driver_id=function(component_number,
			sorted_component_name_id,part_component_id_and_driver_id)
	{
		this.component_to_id_array  =new Array(component_number);
		this.component_to_name_array=new Array(component_number);
		for(var i=0,j=0,ni=sorted_component_name_id.length;i<ni;j++){
			var my_component_name=sorted_component_name_id[i++];
			var my_component_id	 =sorted_component_name_id[i++];
			var p={
					component_name	:	my_component_name,
					component_id	:	my_component_id
			};
			this.component_to_id_array[j]=p;
			this.component_to_name_array[my_component_id]=p;
		};
		var component_render_id_and_part_id=new Array(component_number);
		for(var i=0;i<component_number;i++)
			component_render_id_and_part_id[i]=new Array();
		var permanent_render_part_id=new Array();
		var render_number=part_component_id_and_driver_id.length;
		for(var render_id=0;render_id<render_number;render_id++){
			permanent_render_part_id[render_id]=new Array();
			if(typeof(this.part_initialize_data[render_id])=="undefined")
				this.part_initialize_data[render_id]=new Array();
			var part_number=part_component_id_and_driver_id[render_id].length;
			for(var part_id=0;part_id<part_number;part_id++){
				var id_array=part_component_id_and_driver_id[render_id][part_id];
				var permanent_part_id=id_array.pop();
				var permanent_render_id=id_array.pop();
				permanent_render_part_id[render_id][part_id]={
						permanent_render_id	:	permanent_render_id,
						permanent_part_id	:	permanent_part_id
				};
				for(var buffer_id=0,buffer_number=id_array.length;buffer_id<buffer_number;buffer_id++){
					var component_id=id_array[buffer_id][0];
					var driver_id=id_array[buffer_id][1];
					component_render_id_and_part_id[component_id][driver_id]=[render_id,part_id,buffer_id];
				}
				if(typeof(this.part_initialize_data[render_id][part_id])=="undefined")
					this.part_initialize_data[render_id][part_id]=null;
			};
		};
		this.component_render_id_and_part_id=component_render_id_and_part_id;
		this.permanent_render_part_id		=permanent_render_part_id;
		this.part_component_id_and_driver_id=part_component_id_and_driver_id;
		return;
	}
	this.get_component_buffer_parameter=function(buffer_data)
	{
		for(var i=0,ni=buffer_data.length;i<ni;i++){
			var render_id		=buffer_data[i][0];
			var part_id			=buffer_data[i][1];
			var part_buffer_data=buffer_data[i][2];

			if(typeof(this.data_buffer[render_id])=="undefined")
				this.data_buffer[render_id]=new Array();
			if(typeof(this.data_buffer[render_id][part_id])=="undefined")
				this.data_buffer[render_id][part_id]=new Array();
			for(var j=0,nj=part_buffer_data.length;j<nj;j++){
				var buffer_id			=part_buffer_data[j][0];
				var buffer_data_item	=part_buffer_data[j][1];
				var p=this.data_buffer[render_id][part_id][buffer_id];
				if(typeof(p)=="undefined"){
					p=new Array();
					this.data_buffer[render_id][part_id][buffer_id]=p;
				}
				p.push(buffer_data_item);
				
				if(typeof(this.part_information[render_id])=="undefined")
					continue;
				if(typeof(this.part_information[render_id][part_id])=="undefined")
					continue;
				var part_property=this.part_information[render_id][part_id].property;
				var max_number=part_property.max_component_data_buffer_number;
				while(p.length>max_number)
					p.shift();
			};
		};
	};
	this.get_component_render_parameter=function(response_text)
	{
		var render_buffer_id;
		try{
			var p=response_text;
			render_buffer_id=p[0];
			this.camera.modify_camera_data(render_buffer_id,p[1]);
			this.component_render_data.modify_component_render_data(render_buffer_id,p[2][0],p[2][1]);
			if(p.length>=4){
				if(p[3].length<4){
					this.clip_plane_array[render_buffer_id]				=[0,0,0,0];
					this.clip_plane_matrix_array[render_buffer_id]		=this.computer.create_move_rotate_matrix(0,0,0,0,0,0);
				}else{
					this.clip_plane_array[render_buffer_id]				=[p[3][0],p[3][1],p[3][2],p[3][3]];
					this.clip_plane_matrix_array[render_buffer_id]	
								=this.computer.project_to_plane_location( p[3][0],p[3][1],p[3][2],p[3][3],1.0);
				};
			}else if(typeof(this.clip_plane_array[render_buffer_id])=="undefined"){
				this.clip_plane_array[render_buffer_id]					=[0,0,0,0];
				this.clip_plane_matrix_array[render_buffer_id]			=this.computer.create_move_rotate_matrix(0,0,0,0,0,0);
			};
		}catch(e){
			if(this.parameter.debug_mode_flag){
				alert("get_component_render_parameter fail");
				alert(e.toString());
			}else{
				console.log("get_component_render_parameter fail");
				console.log(e.toString());
			};
			return -1;
		};
	    return render_buffer_id;
	};
	this.parse_current_information_request=function(current_information_request_response_data)
	{
		for(var p,i=0,ni=current_information_request_response_data.length;i<ni;i++){
			switch((p=current_information_request_response_data[i])[0]){
			case 0:
				this.current.render_buffer_id=p[1];
				break;
			case 1:
				this.current.target_id=p[1];
				break;
			case 2:
				this.current.target_viewport_id=p[1];
				break;
			case 3:
				this.current.camera_id=p[1];
				break;
			case 4:
				this.current.camera_component_id=p[1];
				break;
			case 6:
				this.current.high_or_low_precision_flag=(p[1]>0.5)?true:false;
				break;
			};
		};
	};
	this.parse_web_server_response_data=function(responseText,browser_start_time)
	{
		var response_data,start_time=(new Date()).getTime();
		try{
			response_data=JSON.parse(responseText);
			this.can_do_render_request_flag=true;
		}catch(e){
			this.can_do_render_request_flag	=false;
			
			console.log("\n\n\n\nparse_web_server_response_data fail:"+e.toString());
			console.log(responseText);
			console.log("\n\n\n\n");
			
			if(this.parameter.debug_mode_flag)
				alert("parse_web_server_response_data fail:"+e.toString());

			return;
		};
		this.collector_stack_version=response_data[0].shift();
		var p=this.modifier_time_parameter.modify_parameter(response_data[0]);
		for(var i=0,ni=p.length;i<ni;i++)
			if(p[i].update_flag){
				p[i].update_flag=false;
				this.modifier_current_time[i]=this.modifier_time_parameter.caculate_current_time(i);
			};
		this.browser_current_time=(browser_start_time+start_time)/2.0;
		this.render_data=new Array();
		for(var i=0,ni=response_data[1].length;i<ni;i++){
			var render_buffer_id=this.get_component_render_parameter(response_data[1][i][0]);
			var target_id		=response_data[1][i][1];
			var do_render_number=response_data[1][i][2];
			if(response_data[1][i].length>3)
				this.target_buffer[target_id]=response_data[1][i][3];
			this.render_data[i]=[render_buffer_id,target_id,(do_render_number>=0)?do_render_number:(1<<28)];
		};
		this.get_component_buffer_parameter(response_data[2]);
		this.component_location_data.modify_component_location(response_data[3]);
		this.parse_current_information_request(response_data[4]);
		for(var p=response_data[5],i=0,ni=p.length;i<ni;i++)
			this.buffer_object.buffer_head_request_queue.push(p[i]);
		this.data_time_length=(new Date()).getTime()-start_time;
	};
	this.render_component=function(render_list,render_buffer_id,parameter_channel_id,
				method_id,pass_id,project_matrix,render_do_render_number,pass_do_render_number)
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
			if(this.part_information[render_id][part_id].find_error_flag)
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
		    try{
		    	draw_function(
		    		method_id,  			pass_id,			parameter_channel_id,
		    		render_id,				part_id,			render_buffer_id,
		    		component_render_data,	project_matrix,
		    		{
		    			engine_render	:	(this.engine_do_render_number[render_id].render_do_render_number)++,
		    			engine_part		:	(this.engine_do_render_number[render_id].part_do_render_number[part_id])++,
			    		
		    			render_render	:	(render_do_render_number[render_id].render_do_render_number)++,
		    			render_part		:	(render_do_render_number[render_id].part_do_render_number[part_id])++,
			    		
		    			pass_render		:	(pass_do_render_number[render_id].render_do_render_number)++,
		    			pass_part		:	(pass_do_render_number[render_id].part_do_render_number[part_id])++
		    		},this);
		    }catch(e){
		    	var part_info=this.part_information[render_id][part_id];
		    	if(!(part_info.find_error_flag)){
			   		var err_msg;
			   		part_info.find_error_flag=true;
			   		err_msg =  "part system name:"	+part_info.information.system_name;
			   		err_msg+="\npart user   name:"	+part_info.information.user_name;
			   		err_msg+="\nmesh file   name:"	+part_info.information.mesh_file;
			   		err_msg+="\nmaterial file name:"+part_info.information.material_file;
			   		
			   		if(this.parameter.debug_mode_flag){
			   			alert("method_id:"+method_id.toString()+",driver fail:"+e.toString());
			   			alert(err_msg);
			   		}else{
			   			console.log("method_id:"+method_id.toString()+",driver fail:"+e.toString());
			   			console.log(err_msg);
			   		};
		    	};
		    };
		};
	};
	this.render_routine=function()
	{
		if(typeof(this.render_data)=="undefined")
			return;

		this.gl.depthRange(0.0,1.0);
		this.uniform_block.bind_system(this.pickup,
				this.current_time,this.canvas.width,this.canvas.height);
		
		var render_do_render_number=new Array();
		
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
			this.uniform_block.bind_target(project_matrix,
					this.clip_plane_array[render_buffer_id],
					this.clip_plane_matrix_array[render_buffer_id],
					width,height,this_draw_buffer_id);
			this.viewport			=new Object();
			this.viewport.width		=width;
			this.viewport.height	=height;
			this.viewport.viewport	=viewport;
			
			if((target_id>=0)&&(target_id<(this.target_processor.length)))
				if(typeof(this.target_processor[target_id])=="object")
					if(typeof(this.target_processor[target_id].before_target_render)=="function")
						try{
							this.target_processor[target_id].before_target_render(
									target_id,render_target_id,width,height,render_target_number,this);
						}catch(e){
							if(this.parameter.debug_mode_flag){
								alert("Execute before_target_render error,target id is "+target_id.toString());
								alert(e.toString());
							}else{
								console.log("Execute before_target_render error,target id is "+target_id.toString());
								console.log(e.toString());
							};
						};

			var render_list=this.component_render_data.get_render_list(render_buffer_id);
			var pass_do_render_number=new Array();
			
			for(var viewport_id=0,viewport_number=viewport.length;viewport_id<viewport_number;viewport_id++){
				var method_id	=viewport[viewport_id][4];
				var pass_id		=viewport[viewport_id][5];
				var clear_color	=viewport[viewport_id][6];
				var my_viewport=[
					Math.round((viewport[viewport_id][0]+1.0)*width /2.0),
					Math.round((viewport[viewport_id][1]+1.0)*height/2.0),
					Math.round((viewport[viewport_id][2]    )*width /2.0),
					Math.round((viewport[viewport_id][3]    )*height/2.0)
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
				
				this.uniform_block.bind_pass(method_id,pass_id,my_clear_flag,my_viewport,my_clear_color);

				this.render_component(render_list,render_buffer_id,parameter_channel_id,
					method_id,pass_id,project_matrix,render_do_render_number,pass_do_render_number);
			};
			if((target_id>=0)&&(target_id<(this.target_processor.length)))
				if(typeof(this.target_processor[target_id])=="object")
					if(typeof(this.target_processor[target_id].after_target_render)=="function")
						try{
							this.target_processor[target_id].after_target_render(
									target_id,render_target_id,width,height,render_target_number,this);
						}catch(e){
							if(this.parameter.debug_mode_flag){
								alert("Execute after_target_render error,target id is "+target_id.toString());
								alert(e.toString());
							}else{
								console.log("Execute after_target_render error,target id is "+target_id.toString());
								console.log(e.toString());
							};
						};
		};
		this.gl.bindFramebuffer(this.gl.FRAMEBUFFER,null);
		this.do_execute_render_number++;
	};
	this.can_do_render_request_flag=true;
	this.render_request_start_time=0;
	this.render_request=function(current_time)
	{	
		if(!(this.can_do_render_request_flag))
			return false;
		if((current_time-this.render_request_start_time)<(this.modifier_time_parameter.delay_time_length))
			return false;
		this.can_do_render_request_flag	=false;
		this.render_request_start_time	=(new Date()).getTime();
		var min_value=this.computer.min_value();
		var request_string=this.url+"?command=component&method=update_render&channel="+this.channel;
		this.view.aspect=this.canvas.width/this.canvas.height;
		if(Math.abs(this.view_bak.aspect-this.view.aspect)>min_value){
			this.view_bak.aspect=this.view.aspect;
			request_string+="&aspect="+(this.view.aspect.toString());
		};
		if(Math.abs(this.view_bak.x-this.view.x)>min_value){
			this.view_bak.x=this.view.x;
			request_string+="&x="+(this.view.x.toString());
		};
		if(Math.abs(this.view_bak.y-this.view.y)>min_value){
			this.view_bak.y=this.view.y;
			request_string+="&y="+(this.view.y.toString());
		};
		var id,value;
		id=this.pickup.component_id;
		if(this.view_bak.component!=id){
			this.view_bak.component=id;
			request_string+="&component="+id.toString();
		};
		id=this.pickup.body_id;
		value=this.pickup.body_value;
		if((this.view_bak.body!=id)||(Math.abs(this.view_bak.body_value-value)>min_value)){
			this.view_bak.body=id;
			this.view_bak.body_value=value;
			request_string+="&body="+value.toString()+";"+id.toString();
		};
		id=this.pickup.face_id;
		value=this.pickup.face_value;
		if((this.view_bak.face!=id)||(Math.abs(this.view_bak.face_value-value)>min_value)){
			this.view_bak.face=id;
			this.view_bak.face_value=value;
			request_string+="&face="+value.toString()+";"+id.toString();
		};
		id=this.pickup.loop_id;
		value=this.pickup.loop_value;
		if((this.view_bak.loop!=id)||(Math.abs(this.view_bak.loop_value-value)>min_value)){
			this.view_bak.loop=id;
			this.view_bak.loop_value=value;
			request_string+="&loop="+value.toString()+";"+id.toString();
		};
		id=this.pickup.edge_id;
		value=this.pickup.edge_value;
		if((this.view_bak.edge!=id)||(Math.abs(this.view_bak.edge_value-value)>min_value)){
			this.view_bak.edge=id;
			this.view_bak.edge_value=value;
			request_string+="&edge="+value.toString()+";"+id.toString();
		};
		id=this.pickup.vertex_id;
		value=this.pickup.vertex_value;
		if((this.view_bak.vertex!=id)||(Math.abs(this.view_bak.vertex_value-value)>min_value)){
			this.view_bak.vertex=id;
			this.view_bak.vertex_value=value;
			request_string+="&vertex="+value.toString()+";"+id.toString();
		};
		id=this.pickup.point_id;
		if(this.view_bak.point!=id){
			this.view_bak.point=id;
			request_string+="&point="+id.toString();
		};
		value=this.pickup.depth;	
		if(Math.abs(this.view_bak.depth-value)>min_value){
			this.view_bak.depth=value;
			request_string+="&depth="+(new Number(value)).toPrecision(6);
		}
		value=this.pickup.value;
		if(Math.abs(this.view_bak.value-value)>min_value){
			this.view_bak.value=value;
			request_string+="&value="+(new Number(value)).toPrecision(6);
		};
		if(this.current.loaded_length!=(this.buffer_object.loaded_buffer_object_data_length)){
			this.current.loaded_length=this.buffer_object.loaded_buffer_object_data_length;
			request_string+="&loaded_length="+	this.buffer_object.loaded_buffer_object_file_number.toString();
			request_string+="_"+ 				this.buffer_object.loaded_buffer_object_data_length.toString();
			request_string+="_"+				this.buffer_object.loading_render_id.toString();
			request_string+="_"+				this.buffer_object.loading_part_id.toString();
		};
		{
			var max_request_number;
			if((max_request_number=this.do_render_request_response_number)<=0)
				max_request_number=1;
			else if(max_request_number>this.parameter.max_loading_number)
				max_request_number=this.parameter.max_loading_number;

			var requesting_number=this.buffer_object.current_loading_mesh_number
			requesting_number+=this.buffer_object.request_render_part_id.length;
			requesting_number+=this.buffer_object.buffer_head_request_queue.length;
			request_string+="&requesting_number="+requesting_number+"_"+max_request_number;
		};
		
		request_string+="&data_time="		+this.data_time_length.toString();
		request_string+="&render_time="		+this.render_time_length.toString();
		request_string+="&read_time="		+this.pickup.read_time_length.toString();
		request_string+="&render_interval="	+this.render_interval_length.toString();
		request_string+="&length="			+request_string.length.toString();
	
		try{
			var cur=this,my_ajax=new XMLHttpRequest();
			my_ajax.onreadystatechange=function()
			{
				if(my_ajax.readyState!=4)
					return;
				if(my_ajax.status!=200){
					if(cur.parameter.debug_mode_flag)
						alert("render_request:my_ajax.status!=200 fail:"+my_ajax.status);
					else
						console.log("render_request:my_ajax.status!=200 fail:"+my_ajax.status);
					return;
				};
				cur.parse_web_server_response_data(my_ajax.responseText,my_ajax.browser_start_time);
				cur.do_render_request_response_number++;
				return;
			};
			my_ajax.browser_start_time=(new Date()).getTime();
			my_ajax.open("GET",request_string,true);
			my_ajax.send(null);
			my_ajax.browser_start_time=(new Date()).getTime();
		}catch(e){
			if(this.parameter.debug_mode_flag){
				alert("render_request error fail");
				alert(e.toString());
			}else{
				console.log("render_request error fail");
				console.log(e.toString());
			};
		};
		return true;
	};
	this.append_routine_function=function(my_routine_function,my_routine_id)
	{
		this.routine_array.push(
				{
					routine_id			:	my_routine_id.toString(),
					routine_function	:	my_routine_function
				});
		return this.routine_array.length-1;
	};
	this.process_routine_function=function()
	{
		var fun_array=this.routine_array;
		this.routine_array=new Array();
		
		for(var i=0,ni=fun_array.length;i<ni;i++)
			if(typeof(fun_array[i].routine_function)=="function")
				try{
					if(fun_array[i].routine_function(this))
						this.routine_array.push(fun_array[i]);
				}catch(e){
					if(this.parameter.debug_mode_flag){
						alert("Processing routine Array error,ID is "+fun_array[i].routine_id.toString());
						alert(e.toString());
					}else{
						console.log("Processing routine Array error,ID is "+fun_array[i].routine_id.toString());
						console.log(e.toString());
					}
				}
	};
	this.do_render=function(my_render_interval_length)
	{
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
				new_current_time =this.modifier_time_parameter.caculate_current_time(i);
				new_current_time+=this.modifier_time_parameter.modifier_time_parameter[i].speed*pass_time;
				
				if(typeof(this.modifier_current_time[i])=="undefined")
					this.modifier_current_time[i]=new_current_time;
				else if(this.modifier_current_time[i]<new_current_time)
					this.modifier_current_time[i]=new_current_time;
				else
					this.modifier_current_time[i]++;
			};
		};
		this.process_routine_function();
		this.render_routine();
		for(var i=0,ni=this.parameter.max_loading_number;i<ni;)
			i+=this.buffer_object.request_buffer_object_data(this);
		this.buffer_object.process_buffer_head_request_queue(this);
		this.render_request(start_time);
		this.render_time_length=(new Date()).getTime()-start_time;
	};
	this.terminate=function()
	{
		this.parameter.debug_mode_flag=false;
		try{
			var my_ajax=new XMLHttpRequest();
			my_ajax.open("GET",this.url+"?channel="+this.channel+"&command=termination",true);
			my_ajax.send(null);
		}catch(e){
			;
		};
	};
	this.get_component_to_id_object=function(my_component_name)
	{
		for(var begin_pointer=0,end_pointer=this.component_to_id_array.length-1;begin_pointer<=end_pointer;){
			var middle_pointer=Math.floor((begin_pointer+end_pointer)/2.0);
			var array_component_name=this.component_to_id_array[middle_pointer].component_name;
			if(array_component_name<my_component_name)
				begin_pointer=middle_pointer+1;
			else if(array_component_name>my_component_name)
				end_pointer=middle_pointer-1;
			else 
				return this.component_to_id_array[middle_pointer];
		};
		return null;
	};
	this.get_component_processor=function(my_component_name)
	{
		var my_component_id=my_component_name;
		if(typeof(my_component_id)=="string"){
			var p=this.get_component_to_id_object(my_component_id);
			if(p==null)
				return null;
			my_component_id=p.component_id;
		}
		if(typeof(my_component_id)=="number"){
			if(my_component_id>=0)
				if(my_component_id<(this.component_event_processor.length)){
					var ret_val=this.component_event_processor[my_component_id];
					if(typeof(ret_val)=="object")
						if(ret_val!=null){
							ret_val.component_id=my_component_id;
							return ret_val;
						}
				}
		}
		return null;
	};
	this.call_server=function(request_string,
			response_function,error_function,response_type_string,upload_data)
	{
		var cur=this;
		
		if(typeof(response_type_string)=="undefined")
			response_type_string="text";
		if(typeof(upload_data)=="undefined")
			upload_data=null;
		try{
			var my_ajax=new XMLHttpRequest();
			my_ajax.responseType=response_type_string;
			my_ajax.onreadystatechange=function()
			{
				if(my_ajax.readyState!=4)
					return;
				cur.current.calling_server_number--;

				if(typeof(response_function)!="function")
					return;
				if(my_ajax.status!=200){
					try{
						if(typeof(error_function)=="function")
							error_function(0,cur);
					}catch(e){
						if(cur.parameter.debug_mode_flag){
							alert("this.call_server=function(request_string,response_function,error_function) fail"+my_ajax.status);
							alert(e.toString());
						}else{
							console.log("this.call_server=function(request_string,response_function,error_function) fail"+my_ajax.status);
							console.log(e.toString());
						};
					};
					return;
				};
				
				var response_data;
				switch(response_type_string){
				default:
					response_data=my_ajax.responseText;
					break;
				case "text":
					try{
						response_data=JSON.parse(my_ajax.responseText);
					}catch(e){
						if(typeof(error_function)=="function")
							error_function(1,cur,e,my_ajax.responseText);
						response_data=my_ajax.responseText;
					};
					break;
				};
				try{
					response_function(response_data,cur);
				}catch(e){
					if(typeof(error_function)=="function")
						error_function(2,cur,e,response_data);
				};
				return;
			};
			my_ajax.open("GET",request_string,true);
			my_ajax.send(upload_data);
			this.current.calling_server_number++;
		}catch(e){
			if(typeof(error_function)=="function")
				error_function(3,this,e,request_string);
		};
	};
	this.create_part_request_string=function(render_id_or_part_name,part_id_or_driver_id,part_parameter)
	{
		var ret_val=this.url_and_channel+"&command=part&method=event";
		if(typeof(render_id_or_part_name)=="string"){
			ret_val+="&event_part_name="+encodeURIComponent(encodeURIComponent(render_id_or_part_name));
			ret_val+="&event_driver_id="+part_id_or_driver_id;
		}else
			ret_val+="&event_render_id="+render_id_or_part_name.toString()+"&event_part_id="+part_id_or_driver_id.toString();
		for(var i=0,ni=part_parameter.length;i<ni;i++)
			ret_val+="&"+part_parameter[i][0].toString()+"="+part_parameter[i][1].toString();		
		return ret_val;
	};
	this.create_part_request_by_component_string=function(
			component_id_or_component_name,component_driver_id,part_parameter)
	{
		var ret_val=this.url_and_channel+"&command=part&method=event";
		if(typeof(component_id_or_component_name)=="string")
			ret_val+="&event_component_name="+encodeURIComponent(encodeURIComponent(component_id_or_component_name));
		else
			ret_val+="&event_component_id="+component_id_or_component_name.toString();
		if(typeof(component_driver_id)!="undefined")
			ret_val+="&event_driver_id="+component_driver_id.toString();
		for(var i=0,ni=part_parameter.length;i<ni;i++)
			ret_val+="&"+part_parameter[i][0].toString()+"="+part_parameter[i][1].toString();
		return ret_val;
	};
	this.create_component_request_string=function(component_name_or_id,driver_id,component_parameter)
	{
		var ret_val=this.url_and_channel+"&command=component&method=event";
		if(typeof(component_name_or_id)=="string")
			ret_val+="&event_component_name="+encodeURIComponent(encodeURIComponent(component_name_or_id));
		else
			ret_val+="&event_component_id="+component_name_or_id.toString();
		if(typeof(driver_id)!="undefined")
			ret_val+="&event_driver_id="+driver_id.toString();
		for(var i=0,ni=component_parameter.length;i<ni;i++)
			ret_val+="&"+component_parameter[i][0].toString()+"="+component_parameter[i][1].toString();
		return ret_val;
	};
	this.call_server_engine=function(engine_parameter,
			response_function,error_function,response_type_string,upload_data)
	{
		var request_string=this.url_and_channel;
		for(var i=0,ni=engine_parameter.length;i<ni;i++)
			request_string+="&"+engine_parameter[i][0].toString()+"="+engine_parameter[i][1].toString();
		this.call_server(request_string,response_function,error_function,response_type_string,upload_data);
	};
	this.call_server_part=function(
			render_id_or_part_name,part_id_or_driver_id,part_parameter,
			response_function,error_function,response_type_string,upload_data)
	{
		this.call_server(
			this.create_part_request_string(
					render_id_or_part_name,part_id_or_driver_id,part_parameter),
			response_function,error_function,response_type_string,upload_data);
	};
	this.call_server_part_by_component=function(
			component_id_or_component_name,component_driver_id,part_parameter,
			response_function,error_function,response_type_string,upload_data)
	{
		this.call_server(
			this.create_part_request_by_component_string(
					component_id_or_component_name,component_driver_id,part_parameter),
			response_function,error_function,response_type_string,upload_data);
	};
	this.call_server_component=function(component_name_or_id,driver_id,
			component_parameter,response_function,error_function,response_type_string,upload_data)
	{
		this.call_server(
			this.create_component_request_string(
					component_name_or_id,driver_id,component_parameter),
			response_function,error_function,response_type_string),upload_data;
	};
	this.set_event_component=function(event_component_name)
	{
		this.append_routine_function(
			function(render)
			{
				var component_object=render.get_component_processor(event_component_name);

				if(typeof(component_object)=="boolean")
					return component_object?false:true;
				
				var event_component_id=component_object.component_id;
				if((event_component_id<0)||(event_component_id>=(render.component_event_processor.length)))
					return false;
				var cep=render.component_event_processor[event_component_id];
				if(typeof(cep)!="object")
					return false;
				if(cep==null)
					return false;
				if(typeof(cep.set_event_component)!="function")
					return false;
				try{
					cep.set_event_component(event_component_id,render);
     			}catch(e){
     				if(render.parameter.debug_mode_flag){
     					alert("Execute set_event_component processor error,component id is "+event_component_id.toString());
     					alert(e.toString());
     				}else{
     					console.log("Execute set_event_component processor error,component id is "+event_component_id.toString());
     					console.log(e.toString());
     				};
     			};
				return false;
			},"render.set_event_component() error"
		);
	};
};