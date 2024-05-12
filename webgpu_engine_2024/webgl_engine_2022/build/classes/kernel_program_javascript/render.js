function construct_render_routine(my_webgpu,my_url,
	my_user_name,my_pass_word,my_language_name,
	my_container_id,my_channel_id,render_data,default_fetch_parameter)
{
	var component_number				=render_data[0];
	var render_number					=render_data[1];
	var modifier_container_number		=render_data[2];
	var camera_number					=render_data[3];
	
    this.link_name						=render_data[4];
    this.parameter						=render_data[5];

	this.fetch_parameter			=default_fetch_parameter;
	this.webgpu						=my_webgpu;
	this.url						=my_url;
	this.url_without_channel		=this.url
					+"?user_name="	+my_user_name
					+"&pass_word="	+my_pass_word
					+"&language="	+my_language_name
					+"&container="	+my_container_id;
	this.url_with_channel			=this.url_without_channel
					+"&channel="	+my_channel_id.toString();
	
	this.user_event_processor		=new Object();
	this.user_call_processor		=new Object();
	
	this.system_event_processor		=new Object();
	this.system_call_processor		=new Object();

	this.component_event_processor	=new Array(component_number);
	this.component_call_processor	=new Array(component_number);
	
	this.event_component			=
	{
		mouse	:
		{
			component_name	:	null,
			function_id	:	0
		},
		touch	:
		{
			component_name	:	null,
			function_id	:	0
		},
		keyboard	:
		{
			component_name	:	null,
			function_id	:	0
		},
		touch	:
		{
			component_name	:	null,
			function_id	:	0
		}
	};

	this.render_buffer_array		=new Array();
	
	this.routine_array				=new Array();
	
	this.view=
	{
		x					:	-10.0,
		y					:	-10.0,
		main_target_x		:	-10.0,
		main_target_y		:	-10.0
	};
	this.view_bak=
	{
		x					:	-2,
		y					:	-2,
		main_target_x		:	-2,
		main_target_y		:	-2,
		
		canvas_id			:	-2,
					
		component			:	-2,
		driver				:	-2,
		
		primitive_type_id	:	-2,
		
		body				:	-2,
		face				:	-2,
		loop				:	-2,
		edge				:	-2,
		primitive			:	-2,
		vertex				:	-2,
		
		depth				:	-2,
		value				:	[-2,-2,-2]
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
	
	this.component_location_data	=new construct_component_location_object(component_number,this.computer,this.webgpu);
	this.component_render_data		=new construct_component_render_parameter(render_number);
	this.modifier_time_parameter	=new construct_modifier_time_parameter(modifier_container_number);
	this.vertex_data_downloader		=new construct_download_vertex_data(this.webgpu,this.parameter.max_loading_number);
	this.camera						=new construct_camera_object(camera_number,this.component_location_data,this.computer);
	this.operate_component			=new construct_operate_component(this);
	this.collector_loader			=new construct_collector_loader_object(this);
	this.system_buffer				=null;
	
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

	this.terminate_flag=false;
	
	this.destroy=function()
	{
		if(this.terminate_flag)
			return;
		this.terminate_flag=true;

		fetch(this.url_with_channel+"&command=termination");
		
		if(this.event_listener!=null)
			for(var i=0,ni=this.event_listener.length;i<ni;i++){
				if(typeof(this.event_listener[i])=="object")
					if(this.event_listener[i]!=null)
						if(typeof(this.event_listener[i].destroy)=="function")
							this.event_listener[i].destroy(this);
				this.event_listener[i]=null;
			}
		this.event_listener=null;

		if(typeof(this.user_event_processor)=="object")
			if(this.user_event_processor!=null)
				if(typeof(this.user_event_processor.destroy)=="function")
					this.user_event_processor.destroy(this);
		this.user_event_processor=null;
		
		if(typeof(this.user_call_processor)=="object")
			if(this.user_call_processor!=null)
				if(typeof(this.user_call_processor.destroy)=="function")
					this.user_call_processor.destroy(this);	
		this.user_call_processor=null;
		
		if(typeof(this.system_event_processor)=="object")
			if(this.system_event_processor!=null)
				if(typeof(this.system_event_processor.destroy)=="function")
					this.system_event_processor.destroy(this);	
		this.system_event_processor=null;

		if(typeof(this.system_call_processor)=="object")
			if(this.system_call_processor!=null)
				if(typeof(this.system_call_processor.destroy)=="function")
					this.system_call_processor.destroy(this);	
		this.system_call_processor=null;
		
		if(this.component_event_processor!=null)
			for(var i=0,ni=this.component_event_processor.length;i<ni;i++){
				if(typeof(this.component_event_processor[i])=="object")
					if(this.component_event_processor[i]!=null)
						if(typeof(this.component_event_processor[i].destroy)=="function")
							this.component_event_processor[i].destroy(this);
				this.component_event_processor[i]=null;
			}
		this.component_event_processor=null;
		
		if(this.component_call_processor!=null)
			for(var i=0,ni=this.component_call_processor.length;i<ni;i++){
				if(typeof(this.component_call_processor[i])=="object")
					if(this.component_call_processor[i]!=null)
						if(typeof(this.component_call_processor[i].destroy)=="function")
							this.component_call_processor[i].destroy(this);
				this.component_call_processor[i]=null;
			}
		this.component_call_processor=null;
		
		if(this.render_driver!=null)
			for(var i=0,ni=this.render_driver.length;i<ni;i++){
				if(typeof(this.render_driver[i])=="object")
					if(this.render_driver[i]!=null)
						if(typeof(this.render_driver[i].destroy)=="function")
							this.render_driver[i].destroy(this);
				this.render_driver[i]=null;
			}
		this.render_driver=null;
		
		if(this.part_driver!=null)
			for(var i=0,ni=this.part_driver.length;i<ni;i++){
				for(var j=0,nj=this.part_driver[i].length;j<nj;j++){
					if(typeof(this.part_driver[i][j])=="object")
						if(this.part_driver[i][j]!=null)
							if(typeof(this.part_driver[i][j].destroy)=="function")
								this.part_driver[i][j].destroy(this);
					this.part_driver[i][j]=null;
				}
				this.part_driver[i]=null;
			}
		this.part_driver=null;
		
		if(this.part_array!=null)
			for(var i=0,ni=this.part_array.length;i<ni;i++){
				for(var j=0,nj=this.part_array[i].length;j<nj;j++){
					if(typeof(this.part_array[i][j])=="object")
						if(this.part_array[i][j]!=null)
							if(typeof(this.part_array[i][j].destroy)=="function")
								this.part_array[i][j].destroy(this);
					this.part_array[i][j]=null;
				}
				this.part_array[i]=null;
			}
		this.part_array=null;
		
		this.link_name			=null;
		this.title				=null;
		this.parameter			=null;
		this.url				=null;
		this.url_without_channel=null;
		this.url_with_channel	=null;
			
		this.event_component	=null;
		this.render_buffer_array=null;
		this.routine_array		=null;
			
		this.view		=null;
		this.view_bak	=null;
		
		if(this.caller!=null)
			this.caller.destroy(this);
		this.caller=null;

		if(this.component_location_data!=null)
			this.component_location_data.destroy(this);
		this.component_location_data=null;
		
		if(this.component_render_data!=null)
			this.component_render_data.destroy(this);
		this.component_render_data=null;
		
		if(this.modifier_time_parameter!=null)
			this.modifier_time_parameter.destroy(this);
		this.modifier_time_parameter=null;
		
		if(this.vertex_data_downloader!=null)
			this.vertex_data_downloader.destroy(this);
		this.vertex_data_downloader=null;
		
		if(this.camera!=null)
			this.camera.destroy(this);
		this.camera=null;
		
		if(this.operate_component!=null)
			this.operate_component.destroy(this);
		this.operate_component=null;
		
		if(this.collector_loader!=null)
			this.collector_loader.destroy(this);
		this.collector_loader=null;
		
		if(this.system_buffer!=null)
			this.system_buffer.destroy(this);
		this.system_buffer=null;
		
		if(this.computer!=null)
			this.computer.destroy(this);
		this.computer=null;
		
		if(this.webgpu!=null)
			this.webgpu.destroy();
		this.webgpu=null;
			
		this.pickup					=null;
		this.pickup_array			=null;
		this.highlight				=null;
			
		this.current_time			=null;
		this.modifier_current_time	=null;
		
		this.append_routine_function=null;
	};
	
	this.append_routine_function=function(my_routine_function)
	{
		this.routine_array.push(my_routine_function);
		return this.routine_array.length-1;
	};
};