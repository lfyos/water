function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.component_id				=component_id;
	this.driver_id					=driver_id;
	this.should_update_server_flag	=true;

	this.depth_texture	=new Array(render.webgpu.canvas.length);
	this.id_texture		=new Array(render.webgpu.canvas.length);
	for(var i=0,ni=render.webgpu.canvas.length;i<ni;i++){
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
/////////////////////////////////////////////////////////////////////////////////////////////
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
	
///////////////	
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
/////////////////////////		
	this.value_texture_2_for_id=render.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	16,
				height	:	1
			},
			format	:	"rgba32sint",
			usage	:	GPUTextureUsage.COPY_SRC|GPUTextureUsage.COPY_DST
		});
	this.value_buffer_for_id=render.webgpu.device.createBuffer(
		{
			size	:	16,
			usage	:	GPUBufferUsage.MAP_READ|GPUBufferUsage.COPY_DST
		});
		
	this.draw_component=function(method_data,render_data,
			render_id,part_id,data_buffer_id,component_id,driver_id,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		if(!(this.should_update_server_flag))
			return;
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
	this.copy_id_texture=function(render_data,render)
	{
		if(render_data.target_texture_id!=render.webgpu.current_canvas_id)
			return;

		var my_id_texture=this.id_texture[render_data.target_texture_id];
		var texture_width	=my_id_texture.width;
		var texture_height	=my_id_texture.height;
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
				buffer			:	this.id_buffer,
				offset			:	0,
    			bytesPerRow		:	Int32Array.BYTES_PER_ELEMENT*16*4,
    			rowsPerImage	:	1
			},
			{	//copysize
				width	:	1,
				height	:	1
			});
	}
	
	this.copy_value_texture=function(render)
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
			
			
		render.webgpu.command_encoder.copyTextureToTexture(
			{	//source
				texture	:	this.id_texture_for_value,
				origin	:
				{
					x	:	0,
					y	:	0
				}
			},
			{	//destination
				texture	:	this.value_texture_2_for_id,
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
				texture	:	this.value_texture_2_for_id,
				origin	:
				{
					x	:	0,
					y	:	0
				}
			},
			{	//destination
				buffer	:	this.value_buffer_for_id,
				offset			:	0,
    			bytesPerRow		:	Int32Array.BYTES_PER_ELEMENT*16*4,
    			rowsPerImage	:	1
			},
			{	//copysize
				width	:	1,
				height	:	1
			});
			
	}
	
	this.end_render_target=function(render_data,
			target_part_object,target_part_driver,target_render_driver,render)
	{
		if(render_data.target_texture_id<0)
			this.copy_value_texture(render);
		else
			this.copy_id_texture(render_data,render);
	}

	this.begin_render_target_for_id=function(render_data,render)
	{
		var my_depth_texture,my_id_texture;
		var my_gpu_texture=render.webgpu.context[render_data.target_texture_id].getCurrentTexture();

		do{
			my_depth_texture	=this.depth_texture	[render_data.target_texture_id];
			my_id_texture		=this.id_texture	[render_data.target_texture_id];
			
			if((my_depth_texture!=null)&&(my_id_texture!=null)){
				if(my_gpu_texture.width==my_depth_texture.width)
					if(my_gpu_texture.height==my_depth_texture.height)
						if(my_gpu_texture.width==my_id_texture.width)
							if(my_gpu_texture.height==my_id_texture.height)
								break;
				my_depth_texture.destroy();
				my_id_texture.destroy();
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
			},
			{
				method_id:	5
			}
		];
		return  ret_val;
	};

	this.begin_render_target_for_value=function(render)
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
	this.begin_render_target=function(render_data,
			target_part_object,target_part_driver,target_render_driver,render)
	{
		if(render_data.target_texture_id>=0)
			return this.begin_render_target_for_id(render_data,render);
		else
			return this.begin_render_target_for_value(render);
	}
	this.complete_render_target_for_id=async function(render_data,render)
	{
		if(render_data.target_texture_id!=render.webgpu.current_canvas_id)
			return;
		
		var my_buffer=this.id_buffer,my_length=Int32Array.BYTES_PER_ELEMENT*4;
		await my_buffer.mapAsync(GPUMapMode.READ,0,my_length);
		var p=new Int32Array(my_buffer.getMappedRange(0,my_length).slice());
		my_buffer.unmap();
		
		var system_bindgroup_id	=p[0];
		var part_body_id		=p[1];
		var part_face_id		=p[2];
		var primitive_type_id	=p[3];

		render.pickup.render_id			=-1;
		render.pickup.part_id			=-1;
		render.pickup.data_buffer_id	=-1;
		render.pickup.component_id		=-1;
		render.pickup.driver_id			=-1;
		
		render.pickup.primitive_type_id	=primitive_type_id;
		render.pickup.body_id			=-1;
		render.pickup.face_id			=-1;

		if((system_bindgroup_id>=0)&&(system_bindgroup_id<render.system_bindgroup_id.length)){
			p=render.system_bindgroup_id[system_bindgroup_id];
			render.pickup.render_id		=p[0];
			render.pickup.part_id		=p[1];
			render.pickup.data_buffer_id=p[2];
			render.pickup.component_id	=p[3];
			render.pickup.driver_id		=p[4];
			
			if((render.pickup.render_id>=0)&&(render.pickup.part_id>=0))
				if(render.pickup.render_id<render.part_array.length)
					if(render.pickup.part_id<render.part_array[render.pickup.render_id].length){
						render.pickup.body_id=part_body_id;
						render.pickup.face_id=part_face_id;
					}
		}
	}
	
	this.complete_render_target_for_value=async function(render)
	{
		do{
			if(render.pickup.component_id<0)
				break;
			if(render.pickup.driver_id<0)
				break;
			if(render.pickup.body_id<0)
				break;
			if(render.pickup.face_id<0)
				break;

			var my_buffer=this.value_buffer_for_id;				////////////////////////////////////////////////////
			var my_length=Int32Array.BYTES_PER_ELEMENT*4;
			await my_buffer.mapAsync(GPUMapMode.READ,0,my_length);
			var p=my_buffer.getMappedRange(0,my_length).slice();
			my_buffer.unmap();
			p=new Int32Array(p);
			
			var my_loop_id		=p[0];
			var my_edge_id		=p[1];
			var my_primitive_id	=p[2];
			var my_vertex_id	=p[3];
	
			my_buffer=this.value_buffer_for_value;
			my_length=Float32Array.BYTES_PER_ELEMENT*4;
			await my_buffer.mapAsync(GPUMapMode.READ,0,my_length);
			p=my_buffer.getMappedRange(0,my_length).slice();
			my_buffer.unmap();
			p=new Float32Array(p);
			
			render.pickup.value=[p[0],p[1],p[2]];
			render.pickup.depth=p[3];
			
			render.pickup.loop_id		=my_loop_id;
			render.pickup.edge_id		=my_edge_id;
			render.pickup.primitive_id	=my_primitive_id;
			render.pickup.vertex_id		=my_vertex_id;

			return;
		}while(false);
		
		render.pickup.loop_id		=-1;
		render.pickup.edge_id		=-1;
		render.pickup.primitive_id	=-1;
		render.pickup.vertex_id		=-1;
	}
	
	this.complete_render_target=async function(render_data,
			target_part_object,target_part_driver,target_render_driver,render)
	{
		if(render_data.target_texture_id>=0)
			await this.complete_render_target_for_id(render_data,render);
		else
			await this.complete_render_target_for_value(render);
	}
};