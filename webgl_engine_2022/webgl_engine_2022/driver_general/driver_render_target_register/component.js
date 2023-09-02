function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.component_id				=component_id;
	this.driver_id					=driver_id;
	this.should_update_server_flag	=true;
	
	this.clear_color		=new Array();
	this.target_parameter	=new Array();
	for(var i=0,j=0,ni=init_data.length;i<ni;j++){
		this.clear_color		[j]=[0,0,0,1];
		this.target_parameter	[j]={
			canvas_id		:	init_data[i++],
			load_operation	:	(init_data[i++]>0)?"clear":"load"
		};
	}	
	this.multisample_texture=new Array(render.webgpu.canvas.length);
	this.depth_texture		=new Array(render.webgpu.canvas.length);
	for(var i=0,ni=render.webgpu.canvas.length;i<ni;i++){
		this.multisample_texture[i]=null;
		this.depth_texture[i]=null;
	}
	this.draw_component=function(method_data,render_data,
			render_id,part_id,component_id,driver_id,component_render_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		if(this.should_update_server_flag){
			this.should_update_server_flag=false;	
			var width_height_str="";
			for(var i=0,ni=render.webgpu.context.length;i<ni;i++){
				var my_texture=render.webgpu.context[i].getCurrentTexture();
				width_height_str+=((i<=0)?"":"_")	+my_texture.width
				width_height_str+="_"				+my_texture.height;
			}
			var par=[
						["operation",		"width_height"],
						["width_height",	width_height_str]
					];
			render.caller.call_server_component(this.component_id,this.driver_id,par);
		}
	}
	this.append_component_parameter=function(
			component_id,		driver_id,		render_id,		part_id,
			buffer_data_item,	part_object,	part_driver,	render_driver,	render)
	{
		this.clear_color=buffer_data_item;
	}
	this.begin_render_target=function(target_sequence_id,
			render_data,target_part_object,target_part_driver,target_render_driver,render)
	{
		if(target_sequence_id!=0)
			return null;
		if(render_data.target_texture_id<0)
			return null;
		if(render_data.target_texture_id>=this.target_parameter.length)
			return null;
		
		var clear_color		=this.clear_color[render_data.target_texture_id];
		var canvas_id		=this.target_parameter[render_data.target_texture_id].canvas_id;
		var load_operation	=this.target_parameter[render_data.target_texture_id].load_operation;

		var my_gpu_texture			=render.webgpu.context		[canvas_id].getCurrentTexture();
		var my_multisample_texture	=this.multisample_texture	[canvas_id];
		var my_depth_texture		=this.depth_texture			[canvas_id];
		
		do{
			if(typeof(my_depth_texture)=="object")
				if(my_depth_texture!=null){
					if(my_gpu_texture.width==my_depth_texture.width)
						if(my_gpu_texture.height==my_depth_texture.height)
							break;
					my_multisample_texture.destroy();
					my_depth_texture.destroy();
				}
			var texture_create_parameter=
				{
					size	:
					{
						width	:	my_gpu_texture.width,
						height	:	my_gpu_texture.height
					},
					format	:	render.webgpu.gpu.getPreferredCanvasFormat(),
					usage	:	GPUTextureUsage.RENDER_ATTACHMENT
				};
			if(render.parameter.multisample>1)
				texture_create_parameter.sampleCount=render.parameter.multisample;
				
			my_multisample_texture=render.webgpu.device.createTexture(texture_create_parameter);
			this.multisample_texture[canvas_id]=my_multisample_texture;	
			
			var texture_create_parameter=
				{
					size	:
					{
						width	:	my_gpu_texture.width,
						height	:	my_gpu_texture.height
					},
					format	:	"depth24plus-stencil8",
					usage	:	GPUTextureUsage.RENDER_ATTACHMENT
				}
			if(render.parameter.multisample>1)
				texture_create_parameter.sampleCount=render.parameter.multisample;
					
			my_depth_texture=render.webgpu.device.createTexture(texture_create_parameter);
			this.depth_texture[canvas_id]=my_depth_texture;

			this.should_update_server_flag=true;
		}while(false);
		
		var my_pass_descriptor=
		{
			colorAttachments		: 
			[
				(render.parameter.multisample>1)
				?{
					view			:	my_multisample_texture.createView(),
					resolveTarget	:	my_gpu_texture.createView(),
					clearValue		:	{ r: clear_color[0], g: clear_color[1], b: clear_color[2], a: clear_color[3] },
					loadOp			:	load_operation,
					storeOp			:	"store"
				}
				:{
					view			:	my_gpu_texture.createView(),
					clearValue		:	{ r: clear_color[0], g: clear_color[1], b: clear_color[2], a: clear_color[3] },
					loadOp			:	load_operation,
					storeOp			:	"store"
				}
			],
			depthStencilAttachment	:
			{
				view				:	my_depth_texture.createView(),
				
				depthClearValue		:	1.0,
				depthLoadOp			:	load_operation,
				depthStoreOp		:	"store",
				
				stencilClearValue	:	0,
   				stencilLoadOp		:	load_operation,
   				stencilStoreOp		:	"store"
			}
		};
		
		return 	{
				pass_descriptor		:	my_pass_descriptor,
				
				target_view			:	
				{
					width			:	my_gpu_texture.width,
					height			:	my_gpu_texture.height
				},
				method_array		:
				[
					{
						method_id	:	2		//render before depth rendering
					},
					{
						method_id	:	3		//render depth only
					},
					{
						method_id	:	4		//render after depth rendering
					},
					{
						method_id	:	5		//render for transparent rendering
					}
				]
			};
	};
	this.destroy=function(render)
	{
		this.draw_component				=null;
		this.append_component_parameter	=null;
		this.begin_render_target		=null;
		
		if(render.component_event_processor[this.component_id]!=null){
			if(typeof(render.component_event_processor[this.component_id].destroy)=="function")
				render.component_event_processor[this.component_id].destroy(render);
			render.component_event_processor[this.component_id]=null;
		}
		
		for(var i=0,ni=this.multisample_texture.length;i<ni;i++)
			if(this.multisample_texture[i]!=null){
				this.multisample_texture[i].destroy();
				this.multisample_texture[i]=null;
			}
		this.multisample_texture.length=0;
		for(var i=0,ni=this.depth_texture.length;i<ni;i++)
			if(this.depth_texture[i]!=null){
				this.depth_texture[i].destroy();
				this.depth_texture[i]=null;
			}
		this.depth_texture.length=0;
	}
};