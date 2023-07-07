function my_create_part_driver(part_object,render_driver,render)
{
	this.should_update_server_flag=false;
	this.current_canvas_id=render.webgpu.current_canvas_id;	
	
	this.depth_texture	=new Array(render.webgpu.canvas.length);
	this.id_texture		=new Array(render.webgpu.canvas.length);
	
	for(var i=0,ni=this.depth_texture.length;i<ni;i++){
		this.depth_texture[i]	=null;
		this.id_texture[i]		=null;
	}

	this.draw_component=function (method_data,target_data,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,render_driver,render)	
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
						["id"				,	this.current_canvas_id	],
						["width_height"		,	width_height_str		]
					];
			for(var i=0,ni=component_render_parameter.length;i<ni;i++){
				var buffer_id=component_render_parameter[i];
				var p=component_buffer_parameter[buffer_id];
				while(p.length>1)
					p.shift();
				var component_id=p[0][0],driver_id=p[0][1];
				render.caller.call_server_component(component_id,driver_id,par);
			}
		}
	}

	this.end_render_target=function(render_data,render)
	{
		return;
	}
	
	this.begin_render_target=function(render_data,render)
	{
		var my_depth_texture,my_id_texture;
		var my_gpu_texture		=render.webgpu.context[render_data.target_texture_id].getCurrentTexture();
		
		do{
			my_depth_texture	=this.depth_texture	[render_data.target_texture_id];
			my_id_texture		=this.id_texture	[render_data.target_texture_id];

			if(my_depth_texture!=null){
				if(my_gpu_texture.width==my_depth_texture.width)
					if(my_gpu_texture.height==my_depth_texture.height)
						break;
				my_depth_texture.	destroy();
				my_id_texture.		destroy();
			}
			
			this.should_update_server_flag=true;
			
			this.depth_texture[render_data.target_texture_id]=render.webgpu.device.createTexture(
				{
					size	:
					{
						width	:	my_gpu_texture.width,
						height	:	my_gpu_texture.height
					},
					format	:	"depth24plus-stencil8",
					usage	:	GPUTextureUsage.RENDER_ATTACHMENT
				});
			this.id_texture[render_data.target_texture_id]=render.webgpu.device.createTexture(
				{
					size	:
					{
						width	:	my_gpu_texture.width,
						height	:	my_gpu_texture.height
					},
					format	:	"rgba32sint",
					usage	:	GPUTextureUsage.RENDER_ATTACHMENT|GPUTextureUsage.COPY_SRC
				});	
		}while(true);
		
		render.webgpu.render_pass_encoder = render.webgpu.command_encoder.beginRenderPass(
		{
			colorAttachments		: 
			[
				{
					view			:	my_gpu_texture.createView(),
					clearValue		:	{ r: 0.0, g: 0.0, b: 0.0, a: 1.0 },
					loadOp			:	"clear",
					storeOp			:	"store"
				},			
				{
					view			:	my_id_texture.createView(),
					clearValue		:	{ r: -1, g: -1, b: -1, a: -1 },
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
		
		var ret_val=
		[
			{
				method_id:	0
			},
			{
				method_id:	1
			},
			{
				method_id:	2
			},
			{
				method_id:	3
			},
			{
				method_id:	4
			}
		];
		return  ret_val;
	};
	
	this.destroy=function()
	{
	
	};
}

function main(	render_id,		render_name,
				init_data,		text_array,
				shader_code,	render)
{
	this.create_part_driver=my_create_part_driver;
	this.destroy=function()
	{
	
	};
}
