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
	var camera_number					=render_data[3];
	
    this.link_name						=render_data[4];
    this.title							=render_data[5];
    this.parameter						=render_data[6];
    
    this.render_shader_data		=new Array();
    this.render_initialize_data	=new Array();
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

	this.target_array				=new Array();
	
	this.routine_array				=new Array();
	
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
   	
	this.render_driver				=new Array(render_number);
	this.part_driver				=new Array(render_number);
	this.part_array					=new Array(render_number);
	
	for(var i=0;i<render_number;i++){
		this.render_driver[i]		=null;
		this.part_driver[i]			=new Array();
		this.part_array[i]			=new Array();
	}

	this.component_location_data	=new construct_component_location_object(component_number,this.computer,this.webgpu);
	this.component_render_data		=new construct_component_render_parameter(render_number);
	this.modifier_time_parameter	=new construct_modifier_time_parameter(modifier_container_number);
	this.vertex_data_downloader		=new construct_download_vertex_data(this.webgpu,this.parameter.max_loading_number);
	this.camera						=new construct_camera_object(camera_number,this.component_location_data,this.computer);
	this.system_buffer				=new construct_system_buffer(this);
	this.operate_component			=new construct_operate_component(this);
	this.collector_loader			=new construct_collector_loader_object(this);
	
	this.pickup						=new construct_pickup_object();
	this.pickup_array				=[
		this.pickup.fork(),this.pickup.fork(),this.pickup.fork(),this.pickup.fork(),this.pickup.fork(),
		this.pickup.fork(),this.pickup.fork(),this.pickup.fork(),this.pickup.fork(),this.pickup.fork()
	];
	this.highlight					=this.pickup.fork();
	
	this.current_time				=0;
	this.modifier_current_time		=new Array(modifier_container_number);
	for(var i=0;i<modifier_container_number;i++)
		this.modifier_current_time[i]=0;
	this.browser_current_time		=0;
	
	this.collector_stack_version	=0;

	this.terminate_flag				=false;
	this.terminate=function()
	{
		fetch(this.url_with_channel+"&command=termination");
		this.terminate_flag=true;
	};
	this.append_routine_function=function(my_routine_function)
	{
		this.routine_array.push(my_routine_function);
		return this.routine_array.length-1;
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
};