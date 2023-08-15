function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.component_id				=component_id;
	this.driver_id					=driver_id;
	this.should_update_server_flag	=true;

	this.multisample_texture=new Array(render.webgpu.canvas.length);
	this.depth_texture		=new Array(render.webgpu.canvas.length);
	for(var i=0,ni=render.webgpu.canvas.length;i<ni;i++){
		this.multisample_texture[i]=null;
		this.depth_texture[i]=null;
	}
	this.draw_component=function(method_data,render_data,
			render_id,part_id,data_buffer_id,component_id,driver_id,
			component_render_parameter,component_buffer_parameter,
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
						["id"				,	render.webgpu.current_canvas_id],
						["width_height"		,	width_height_str		]
					];
			render.caller.call_server_component(this.component_id,this.driver_id,par);
		}
	}

	this.begin_render_target=function(target_sequence_id,
			render_data,target_part_object,target_part_driver,target_render_driver,render)
	{
		if(target_sequence_id!=0)
			return null;

		var my_gpu_texture			=render.webgpu.context		[render_data.target_texture_id].getCurrentTexture();
		var my_multisample_texture	=this.multisample_texture	[render_data.target_texture_id];
		var my_depth_texture		=this.depth_texture			[render_data.target_texture_id];
		
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
			this.multisample_texture[render_data.target_texture_id]=my_multisample_texture;	
			
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
			this.depth_texture[render_data.target_texture_id]=my_depth_texture;

			this.should_update_server_flag=true;
		}while(false);
		
		render.webgpu.render_pass_encoder = render.webgpu.command_encoder.beginRenderPass(
		{
			colorAttachments		: 
			[
				(render.parameter.multisample>1)
				?{
					view			:	my_multisample_texture.createView(),
					resolveTarget	:	my_gpu_texture.createView(),
					clearValue		:	{ r: 0.0, g: 0.0, b: 0.0, a: 1.0 },
					loadOp			:	"clear",
					storeOp			:	"store"
				}
				:{
					view			:	my_gpu_texture.createView(),
					clearValue		:	{ r: 0.0, g: 0.0, b: 0.0, a: 1.0 },
					loadOp			:	"clear",
					storeOp			:	"store"
				}
			],
			depthStencilAttachment	:
			{
				view				:	my_depth_texture.createView(),
				
				depthClearValue		:	1.0,
				depthLoadOp			:	"clear",
				depthStoreOp		:	"store",
				
				stencilClearValue	:	0,
   				stencilLoadOp		:	"clear",
   				stencilStoreOp		:	"store"
			}
		});
		return [
				{
					method_id:	2
				},
				{
					method_id:	3
				},
				{
					method_id:	4
				},
				{
					method_id:	5
				}
			];
	};
	this.render_target_parameter=function(render_data,
			target_part_object,target_part_driver,target_render_driver,render)
	{
		var my_gpu_texture=render.webgpu.context[render_data.target_texture_id].getCurrentTexture();
		return {
					target_width	:	my_gpu_texture.width,
					target_height	:	my_gpu_texture.height
				};
	};
	
	this.destroy=function()
	{
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