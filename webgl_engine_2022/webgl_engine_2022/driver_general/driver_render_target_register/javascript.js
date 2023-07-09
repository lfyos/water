function my_create_part_driver(part_object,render_driver,render)
{
	this.should_update_server_flag=true;
	
	this.color_texture	=new Array(render.webgpu.canvas.length);
	this.canvas_copy	=new Array(render.webgpu.canvas.length);
	this.depth_texture	=new Array(render.webgpu.canvas.length);
	this.id_texture		=new Array(render.webgpu.canvas.length);
	for(var i=0,ni=this.color_texture.length;i<ni;i++){
		this.canvas_copy[i]		=null;
		this.color_texture[i]	=null;
		this.depth_texture[i]	=null;
		this.id_texture[i]		=null;
	}
	this.id_texture_2=render.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	16,
				height	:	1
			},
			format	:	"rgba32sint",
			usage	:	GPUTextureUsage.COPY_SRC|GPUTextureUsage.COPY_DST
		});	
	this.id_buffer		=render.webgpu.device.createBuffer(
		{
			size	:	16,
			usage	:	GPUBufferUsage.MAP_READ|GPUBufferUsage.COPY_DST
		});

	this.value_texture_for_value=render.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"rgba32float",
			usage	:	GPUTextureUsage.RENDER_ATTACHMENT|GPUTextureUsage.COPY_SRC
		});
	this.depth_texture_for_value=render.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"depth24plus-stencil8",
			usage	:	GPUTextureUsage.RENDER_ATTACHMENT
		});
	this.id_texture_for_value=render.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"rgba32sint",
			usage	:	GPUTextureUsage.RENDER_ATTACHMENT|GPUTextureUsage.COPY_SRC
		});
	this.value_texture_2_for_value=render.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	16,
				height	:	1
			},
			format	:	"rgba32float",
			usage	:	GPUTextureUsage.COPY_SRC|GPUTextureUsage.COPY_DST
		});	
	this.value_buffer_for_value=render.webgpu.device.createBuffer(
		{
			size	:	16,
			usage	:	GPUBufferUsage.MAP_READ|GPUBufferUsage.COPY_DST
		});

	this.draw_component=async function (method_data,target_data,
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
						["id"				,	render.webgpu.current_canvas_id],
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
	
	this.copy_id_texture=function(
			render_data,target_part_object,target_render_driver,render)
	{
		if(render_data.target_texture_id!=render.webgpu.current_canvas_id)
			return;
				
		var my_id_texture=this.id_texture[render_data.target_texture_id];
		var texture_width=my_id_texture.width;
		var texture_height=my_id_texture.height;
		var texture_x=Math.round(texture_width* (1.0+render.view.x)/2.0);
		var texture_y=Math.round(texture_height*(1.0-render.view.y)/2.0);
		
		if((texture_x<=0)||(texture_x>=(texture_width-1)))
			return;
		if((texture_y<=0)||(texture_y>=(texture_height-1)))
			return;
		
		render.webgpu.command_encoder.copyTextureToTexture(
			{	//source
				texture	:	my_id_texture,
				origin	:
				{
					x	:	texture_x,
					y	:	texture_y
				}
			},
			{	//destination
				texture	:	this.id_texture_2,
				origin	:
				{
					x	:	0,
					y	:	0
				}
			},
			{	//copysize
				width	:	1,
				height	:	1
			});
		render.webgpu.command_encoder.copyTextureToBuffer(
			{	//source
				texture	:	this.id_texture_2,
				origin	:
				{
					x	:	0,
					y	:	0
				}
			},
			{	//destination
				buffer	:	this.id_buffer,
				offset			:	0,
    			bytesPerRow		:	Int32Array.BYTES_PER_ELEMENT*16*4,
    			rowsPerImage	:	1
			},
			{	//copysize
				width	:	1,
				height	:	1
			});	
	}
	this.copy_value_texture=function(
			render_data,target_part_object,target_render_driver,render)
	{
		render.webgpu.command_encoder.copyTextureToTexture(
			{	//source
				texture	:	this.value_texture_for_value,
				origin	:
				{
					x	:	0,
					y	:	0
				}
			},
			{	//destination
				texture	:	this.value_texture_2_for_value,
				origin	:
				{
					x	:	0,
					y	:	0
				}
			},
			{	//copysize
				width	:	1,
				height	:	1
			});
		render.webgpu.command_encoder.copyTextureToBuffer(
			{	//source
				texture	:	this.value_texture_2_for_value,
				origin	:
				{
					x	:	0,
					y	:	0
				}
			},
			{	//destination
				buffer	:	this.value_buffer_for_value,
				offset			:	0,
    			bytesPerRow		:	Float32Array.BYTES_PER_ELEMENT*16*4,
    			rowsPerImage	:	1
			},
			{	//copysize
				width	:	1,
				height	:	1
			});	
	}
	
	this.end_render_target=async function(
			render_data,target_part_object,target_render_driver,render)
	{
		if(render_data.target_texture_id<0)
			this.copy_value_texture(render_data,target_part_object,target_render_driver,render);
		else{
			var my_gpu_texture=render.webgpu.context[render_data.target_texture_id].getCurrentTexture();
			this.canvas_copy[render_data.target_texture_id].do_copy(true,
				[
					-1,-1,	 2, 2
				],
				[	 
					 0, 1,	 1, -1
				],			
				my_gpu_texture,render.webgpu);
			this.copy_id_texture(render_data,target_part_object,target_render_driver,render);
		}
	}
	
	this.begin_render_target_for_id=async function(
			render_data,target_part_object,target_render_driver,render)
	{
		var my_color_texture,my_canvas_copy,my_depth_texture,my_id_texture;
		var my_gpu_texture=render.webgpu.context[render_data.target_texture_id].getCurrentTexture();
		
		do{
			my_color_texture	=this.color_texture	[render_data.target_texture_id];
			my_canvas_copy		=this.canvas_copy	[render_data.target_texture_id];
			my_depth_texture	=this.depth_texture	[render_data.target_texture_id];
			my_id_texture		=this.id_texture	[render_data.target_texture_id];

			if(my_color_texture!=null){
				if(my_gpu_texture.width==my_color_texture.width)
					if(my_gpu_texture.height==my_color_texture.height)
						break;
				
				my_color_texture.	destroy();
				my_canvas_copy.		destroy();
				my_depth_texture.	destroy();
				my_id_texture.		destroy();
			}
			
			this.should_update_server_flag=true;
			
			this.color_texture[render_data.target_texture_id]=render.webgpu.device.createTexture(
				{
					size	:
					{
						width	:	my_gpu_texture.width,
						height	:	my_gpu_texture.height
					},
					format	:	"rgba32float",
					usage	:	 GPUTextureUsage.RENDER_ATTACHMENT
								|GPUTextureUsage.TEXTURE_BINDING
								|GPUTextureUsage.COPY_SRC
				});
			this.canvas_copy[render_data.target_texture_id]=new render.texture_to_texture_copy(
				this.color_texture[render_data.target_texture_id],my_gpu_texture.format,render.webgpu);

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
					view			:	my_color_texture.createView(),
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

	this.begin_render_target_for_value=async function(
			render_data,target_part_object,target_render_driver,render)
	{
		render.webgpu.render_pass_encoder = render.webgpu.command_encoder.beginRenderPass(
		{
			colorAttachments		: 
			[
				{
					view			:	this.value_texture_for_value.createView(),
					clearValue		:	{ r: 0.0, g: 0.0, b: 0.0, a: 1.0 },
					loadOp			:	"clear",
					storeOp			:	"store"
				},			
				{
					view			:	this.id_texture_for_value.createView(),
					clearValue		:	{ r: -1, g: -1, b: -1, a: -1 },
					loadOp			:	"clear",
					storeOp			:	"store"
				}			
			],
			depthStencilAttachment	:
			{
				view				:	this.depth_texture_for_value.createView(),
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
			}
		];
		return  ret_val;
	};
	
	this.begin_render_target=async function(render_data,target_part_object,target_render_driver,render)
	{
		if(render_data.target_texture_id>=0)
			return await this.begin_render_target_for_id(
						render_data,target_part_object,target_render_driver,render);
		else
			return await this.begin_render_target_for_value(
						render_data,target_part_object,target_render_driver,render);
	}

	this.complete_render_target_for_id=async function(
			render_data,target_part_object,target_render_driver,render)
	{
		if(render_data.target_texture_id!=render.webgpu.current_canvas_id)
			return;
		
		var my_buffer=this.id_buffer,my_length=Int32Array.BYTES_PER_ELEMENT*4;
		await my_buffer.mapAsync(GPUMapMode.READ,0,my_length);
		var p=new Int32Array(my_buffer.getMappedRange(0,my_length).slice());
		my_buffer.unmap();
		
		var component_system_id	=p[0];
		var part_system_id		=p[1];
		var primitive_id		=p[2];
		var vertex_id			=p[3];

		render.pickup.part_id		=-1;
		render.pickup.buffer_id		=-1;
		render.pickup.component_id	=-1;
		render.pickup.driver_id		=-1;
		render.pickup.render_id		=-1;

		if((component_system_id>=0)&&(component_system_id<render.component_system_id.length)){
			p=render.component_system_id[component_system_id];
			render.pickup.render_id		=p[0];
			render.pickup.part_id		=p[1];
			render.pickup.buffer_id		=p[2];
			
			render.pickup.component_id	=p[3];
			render.pickup.driver_id		=p[4];
		}
		render.pickup.body_id			=-1;
		render.pickup.face_id			=-1;
		render.pickup.loop_id			=-1;
		render.pickup.edge_id			=-1;
		render.pickup.primitive_id		=-1;
		render.pickup.vertex_id			=-1;
		
		if((render.pickup.render_id<0)||(render.pickup.part_id<0)||(part_system_id<0))
			return;
		if(render.pickup.render_id>=render.part_array.length)
			return;
		if(render.pickup.part_id>=render.part_array[render.pickup.render_id].length)
			return;
		var my_item_ids=render.part_array[render.pickup.render_id][render.pickup.part_id].item_ids;
		if(part_system_id>=my_item_ids.length)
			return;
		my_item_ids=my_item_ids[part_system_id];
		switch(my_item_ids[1]){
		default:
		case 0:	// part origin id
			render.pickup.vertex_id=0;
			return;
		case 1:	//	body ID
			render.pickup.body_id=my_item_ids[2];
			return;
		case 2:	//face ID
		case 3:	//face_face ID
		case 4:	//face_curve_id
			 render.pickup.body_id=my_item_ids[2];
			 render.pickup.face_id=my_item_ids[3];
			if(primitive_id>=0)
				render.pickup.primitive_id=primitive_id;
			if(vertex_id>=0)
				render.pickup.vertex_id=vertex_id;
			 return;
		case 5:	//face loop ID
			 render.pickup.body_id=my_item_ids[2];
			 render.pickup.face_id=my_item_ids[3];
			 render.pickup.loop_id=my_item_ids[4];
			 return;
		case 6:	//face edge ID
			 render.pickup.body_id=my_item_ids[2];
			 render.pickup.face_id=my_item_ids[3];
			 render.pickup.loop_id=my_item_ids[4];
			 render.pickup.edge_id=my_item_ids[5];
			if(primitive_id>=0)
				render.pickup.primitive_id=primitive_id;
			if(vertex_id>0)
				render.pickup.vertex_id=vertex_id;
			return;
		}
	}
	this.complete_render_target_for_value=async function(
			render_data,target_part_object,target_render_driver,render)
	{
		var my_buffer=this.value_buffer_for_value;
		var my_length=Float32Array.BYTES_PER_ELEMENT*4;
		await my_buffer.mapAsync(GPUMapMode.READ,0,my_length);
		var p=my_buffer.getMappedRange(0,my_length).slice();
		my_buffer.unmap();
		
		p=new Float32Array(p);
		render.pickup.value=[p[0],p[1],p[2]];
		render.pickup.depth=p[3];
	}
	
	this.complete_render_target=async function(
			render_data,target_part_object,target_render_driver,render)
	{
		if(render_data.target_texture_id>=0)
			await this.complete_render_target_for_id(
								render_data,target_part_object,target_render_driver,render);
		else
			await this.complete_render_target_for_value(
								render_data,target_part_object,target_render_driver,render);
	}
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