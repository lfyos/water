function construct_component_driver(component_ids,init_data,create_data,part_object,part_driver,render_driver,scene)
{
	this.component_ids				=component_ids;
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
	this.multisample_texture=new Array(scene.webgpu.canvas.length);
	this.depth_texture		=new Array(scene.webgpu.canvas.length);
	for(var i=0,ni=scene.webgpu.canvas.length;i<ni;i++){
		this.multisample_texture[i]=null;
		this.depth_texture[i]=null;
	}
	this.draw_component=function(method_data,render_parameter,
			target_data,part_object,part_driver,render_driver,scene)
	{
		if(this.should_update_server_flag){
			this.should_update_server_flag=false;	
			var width_height_str="";
			for(var i=0,ni=scene.webgpu.context.length;i<ni;i++){
				var my_texture=scene.webgpu.context[i].getCurrentTexture();
				width_height_str+=((i<=0)?"":"_")	+my_texture.width
				width_height_str+="_"				+my_texture.height;
			}
			var par=[
						["operation",		"width_height"],
						["width_height",	width_height_str]
					];
			scene.caller.call_server_component(
				this.component_ids.component_id,this.component_ids.driver_id,par);
		}
	}
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)  
	{
		this.clear_color=buffer_data_item;
	}
	
	this.begin_scene_target=function(scene_target_array,render_data,
			target_part_object,target_part_driver,target_render_driver,scene)
	{
		if((typeof(scene_target_array[0])=="object")&&(scene_target_array[0]!=null))
			return render_data.target_id;
		
		var my_target_texture_id=Math.floor(render_data.target_texture_id/2.0);
		if((my_target_texture_id<0)||(my_target_texture_id>=this.target_parameter.length))
			return render_data.target_id;
		
		var clear_color		=this.clear_color		[my_target_texture_id];
		var canvas_id		=this.target_parameter	[my_target_texture_id].canvas_id;
		var load_operation	=this.target_parameter	[my_target_texture_id].load_operation;

		var my_gpu_texture			=scene.webgpu.context		[canvas_id].getCurrentTexture();
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
			my_multisample_texture=scene.webgpu.device.createTexture(
				{
					size	:
					{
						width	:	my_gpu_texture.width,
						height	:	my_gpu_texture.height
					},
					sampleCount	:	scene.parameter.multisample,
					format		:	scene.webgpu.gpu.getPreferredCanvasFormat(),
					usage		:	GPUTextureUsage.RENDER_ATTACHMENT
				});
			this.multisample_texture[canvas_id]=my_multisample_texture;	
			
			my_depth_texture=scene.webgpu.device.createTexture(
				{
					size	:
					{
						width	:	my_gpu_texture.width,
						height	:	my_gpu_texture.height
					},
					sampleCount	:	scene.parameter.multisample,
					format		:	"depth24plus-stencil8",
					usage		:	GPUTextureUsage.RENDER_ATTACHMENT
				});
			this.depth_texture[canvas_id]=my_depth_texture;

			this.should_update_server_flag=true;
		}while(false);
		
		var my_pass_descriptor=
		{
			colorAttachments		: 
			[
				{
					view			:	my_multisample_texture.createView(),
					resolveTarget	:	my_gpu_texture.createView(),
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
		
		scene_target_array[0]={
			pass_descriptor		:	my_pass_descriptor,
			
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
		return render_data.target_id;
	};
	
	this.destroy=function()
	{
		for(var i=0,ni=this.multisample_texture.length;i<ni;i++)
			if(this.multisample_texture[i]!=null){
				this.multisample_texture[i].destroy();
				this.multisample_texture[i]=null;
			}
		for(var i=0,ni=this.depth_texture.length;i<ni;i++)
			if(this.depth_texture[i]!=null){
				this.depth_texture[i].destroy();
				this.depth_texture[i]=null;
			}
	}
};