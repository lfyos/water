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
	var max_target_number				=render_data[4];
	var max_method_number				=render_data[5];
	
    this.link_name						=render_data[6];
    this.title							=render_data[7];
    this.parameter						=render_data[8];
    
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

	this.render_buffer_array		=new Array();
	
	this.routine_array				=new Array();
	
	this.view=
	{
		x		:	-0.0,
		y		:	-0.0
	};
	this.view_bak=
	{
		x				:	-2,
		y				:	-2,
				
		component		:	-2,
		driver			:	-2,
		
		body			:	-2,
		face			:	-2,
		loop			:	-2,
		edge			:	-2,
		primitive		:	-2,
		vertex			:	-2,
		
		depth			:	-2,
		value			:	[-2,-2,-2]
	};
	
	this.caller						=new construct_server_caller(this);
	
	this.event_listener				=new Array(this.webgpu.canvas.length);
	for(var i=0,ni=this.event_listener.length;i<ni;i++)
		this.event_listener[i]=new construct_event_listener(i,this);

    this.computer					=new construct_computation_object();
   	
	this.render_driver				=new Array(render_number);
	this.part_driver				=new Array(render_number);
	this.part_array					=new Array(render_number);
	
	for(var i=0;i<render_number;i++){
		this.render_driver[i]		=null;
		this.part_driver[i]			=new Array();
		this.part_array[i]			=new Array();
	}
	this.system_buffer				=new construct_system_buffer(max_target_number,max_method_number,this);
	
	this.set_system_bindgroup_by_component=function(target_id,method_id,component_id,driver_id)
	{
		if((this.webgpu.render_pass_encoder==null)||(typeof(component_id)!="number"))
			return;

		this.component_location_data.get_component_location_and_update_buffer(component_id);
		
		var component_system_id;
		var p=this.component_array_sorted_by_id[component_id];
		
		driver_id=(typeof(driver_id)!="number")?-1:driver_id;
		if((driver_id<0)||(driver_id>=p.component_ids.length))
			component_system_id=p.component_system_id;
		else
			component_system_id=p.component_ids[driver_id][3];

		this.webgpu.render_pass_encoder.setBindGroup(this.system_buffer.system_bindgroup_id,
			this.component_system_id_buffer[component_system_id].bindgroup_object,
				[
					this.system_buffer.method_buffer_stride*method_id,
					this.system_buffer.target_buffer_stride*target_id
				]);
	}
	this.set_system_bindgroup_by_part=function(target_id,method_id,render_id,part_id,buffer_id)
	{
		var p=this.part_component_id_and_driver_id[render_id][part_id][buffer_id];
		this.set_system_bindgroup_by_component(target_id,method_id,p[0],p[1])
	};
	
	this.component_location_data	=new construct_component_location_object(component_number,this.computer,this.webgpu);
	this.component_render_data		=new construct_component_render_parameter(render_number);
	this.modifier_time_parameter	=new construct_modifier_time_parameter(modifier_container_number);
	this.vertex_data_downloader		=new construct_download_vertex_data(this.webgpu,this.parameter.max_loading_number);
	this.camera						=new construct_camera_object(camera_number,this.component_location_data,this.computer);
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
	
	this.texture_to_texture_copy	=construct_texture_to_texture_copy;
};