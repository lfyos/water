function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.component_id	=component_id;
	this.driver_id		=driver_id;

	this.id_depth_texture=render.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"depth24plus-stencil8",
			usage	:	GPUTextureUsage.RENDER_ATTACHMENT
		});
	this.id_texture_0=render.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"rgba32sint",
			usage	:	GPUTextureUsage.COPY_SRC|GPUTextureUsage.COPY_DST|GPUTextureUsage.RENDER_ATTACHMENT
		});	
	this.id_texture_1=render.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"rgba32sint",
			usage	:	GPUTextureUsage.COPY_SRC|GPUTextureUsage.COPY_DST|GPUTextureUsage.RENDER_ATTACHMENT
		});	
	this.id_buffer_0	=render.webgpu.device.createBuffer(
		{
			size	:	Int32Array.BYTES_PER_ELEMENT*4,
			usage	:	GPUBufferUsage.MAP_READ|GPUBufferUsage.COPY_DST
		});	
	this.id_buffer_1	=render.webgpu.device.createBuffer(
		{
			size	:	Int32Array.BYTES_PER_ELEMENT*4,
			usage	:	GPUBufferUsage.MAP_READ|GPUBufferUsage.COPY_DST
		});	
/////////////////////////////////////////////////////////////////////////////////

	this.value_depth_texture=render.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"depth24plus-stencil8",
			usage	:	GPUTextureUsage.RENDER_ATTACHMENT
		});
	this.value_texture=render.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"rgba32float",
			usage	:	GPUTextureUsage.COPY_SRC|GPUTextureUsage.COPY_DST|GPUTextureUsage.RENDER_ATTACHMENT
		});	
	this.value_buffer=render.webgpu.device.createBuffer(
		{
			size	:	Float32Array.BYTES_PER_ELEMENT*4,
			usage	:	GPUBufferUsage.MAP_READ|GPUBufferUsage.COPY_DST
		});	

//////////////////////////////////////////////////////////////////////////

	this.begin_render_target=function(target_sequence_id,
			render_data,target_part_object,target_part_driver,target_render_driver,render)
	{
		var my_pass_descriptor;
		
		switch(target_sequence_id){
		case 0:
			my_pass_descriptor=
			{
				colorAttachments		: 
				[
					{
						view			:	this.id_texture_0.createView(),
						clearValue		:	{ r: -1, g: -1, b: -1, a: -1 },
						loadOp			:	"clear",
						storeOp			:	"store"
					},
					{
						view			:	this.id_texture_1.createView(),
						clearValue		:	{ r: -1, g: -1, b: -1, a: -1 },
						loadOp			:	"clear",
						storeOp			:	"store"
					}
				],
				depthStencilAttachment	:
				{
					view				:	this.id_depth_texture.createView(),
					depthClearValue		:	1.0,
					depthLoadOp			:	"clear",
					depthStoreOp		:	"store",
					
					stencilClearValue	:	0,
	   				stencilLoadOp		:	"clear",
	   				stencilStoreOp		:	"store"
				}
			};
			return 	{
				pass_descriptor	:	my_pass_descriptor,
				
				target_view		:	
				{
					width		:	1,
					height		:	1
				},
				method_array	:
				[
					{
						method_id:	0
					}
				]
			};
		case 1:
			my_pass_descriptor=
			{
				colorAttachments		: 
				[
					{
						view			:	this.value_texture.createView(),
						clearValue		:	{ r:0.0,g:0.0,b:0.0,a:1.0 },
						loadOp			:	"clear",
						storeOp			:	"store"
					}
				],
				depthStencilAttachment	:
				{
					view				:	this.value_depth_texture.createView(),
					depthClearValue		:	1.0,
					depthLoadOp			:	"clear",
					depthStoreOp		:	"store",
					
					stencilClearValue	:	0,
	   				stencilLoadOp		:	"clear",
	   				stencilStoreOp		:	"store"
				}
			};
			return 	{
				pass_descriptor	:	my_pass_descriptor,
				
				target_view		:	
				{
					width		:	1,
					height		:	1
				},
				
				method_array	:
				[
					{
						method_id:	1
					}
				]
			};
		default:
			return null;
		}
	}
	this.end_render_target=function(target_sequence_id,
				render_data,target_part_object,target_part_driver,target_render_driver,render)
	{
		switch(target_sequence_id){
		case 0:
			render.webgpu.command_encoder.copyTextureToBuffer(
				{	//source
					texture	:	this.id_texture_0,
					origin	:
					{
						x	:	0,
						y	:	0
					}
				},
				{	//destination
					buffer			:	this.id_buffer_0,
					offset			:	0,
	    			bytesPerRow		:	Int32Array.BYTES_PER_ELEMENT*16*4,
	    			rowsPerImage	:	1
				},
				{	//copysize
					width	:	1,
					height	:	1
				});
			render.webgpu.command_encoder.copyTextureToBuffer(
				{	//source
					texture	:	this.id_texture_1,
					origin	:
					{
						x	:	0,
						y	:	0
					}
				},
				{	//destination
					buffer			:	this.id_buffer_1,
					offset			:	0,
	    			bytesPerRow		:	Int32Array.BYTES_PER_ELEMENT*16*4,
	    			rowsPerImage	:	1
				},
				{	//copysize
					width	:	1,
					height	:	1
				});
			break;
		case 1:
			render.webgpu.command_encoder.copyTextureToBuffer(
				{	//source
					texture	:	this.value_texture,
					origin	:
					{
						x	:	0,
						y	:	0
					}
				},
				{	//destination
					buffer			:	this.value_buffer,
					offset			:	0,
	    			bytesPerRow		:	Float32Array.BYTES_PER_ELEMENT*16*4,
	    			rowsPerImage	:	1
				},
				{	//copysize
					width	:	1,
					height	:	1
				});
			break;
		}
	}
	
	this.complete_render_target=async function(render_data,
		target_part_object,target_part_driver,target_render_driver,render)
	{
		var my_buffer=this.id_buffer_0;
		var my_length=Int32Array.BYTES_PER_ELEMENT*4;
		await my_buffer.mapAsync(GPUMapMode.READ,0,my_length);
		var p_id_0=new Int32Array(my_buffer.getMappedRange(0,my_length).slice());
		my_buffer.unmap();
		
		var my_buffer=this.id_buffer_1;
		var my_length=Int32Array.BYTES_PER_ELEMENT*4;
		await my_buffer.mapAsync(GPUMapMode.READ,0,my_length);
		var p_id_1=new Int32Array(my_buffer.getMappedRange(0,my_length).slice());
		my_buffer.unmap();
		
		var my_buffer=this.value_buffer;
		var my_length=Float32Array.BYTES_PER_ELEMENT*4;
		await my_buffer.mapAsync(GPUMapMode.READ,0,my_length);
		var p_value=new Float32Array(my_buffer.getMappedRange(0,my_length).slice());
		my_buffer.unmap();
	
		var system_bindgroup_id	=p_id_0[0];
		var part_body_id		=p_id_0[1];
		var part_face_id		=p_id_0[2];
		var primitive_type_id	=p_id_0[3];
	
		render.pickup.render_id			=-1;
		render.pickup.part_id			=-1;
		render.pickup.data_buffer_id	=-1;
		render.pickup.component_id		=-1;
		render.pickup.driver_id			=-1;
		
		render.pickup.primitive_type_id	=primitive_type_id;
		render.pickup.body_id			=-1;
		render.pickup.face_id			=-1;
		
		render.pickup.loop_id			=-1;
		render.pickup.edge_id			=-1;
		render.pickup.primitive_id		=-1;
		render.pickup.vertex_id			=-1;
		
		render.pickup.value				=[0,0,0];
		render.pickup.depth				=1.0;

		if((system_bindgroup_id>=0)&&(system_bindgroup_id<render.system_bindgroup_id.length)){
			var p=render.system_bindgroup_id[system_bindgroup_id];
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
		
		if(render.pickup.component_id<0)
			return;
		if(render.pickup.driver_id<0)
			return;
		if(render.pickup.body_id<0)
			return;
		if(render.pickup.face_id<0)
			return;
	
		render.pickup.loop_id		=p_id_1[0];
		render.pickup.edge_id		=p_id_1[1];
		render.pickup.primitive_id	=p_id_1[2];
		render.pickup.vertex_id		=p_id_1[3];
	
		render.pickup.value=[p_value[0],p_value[1],p_value[2]];
		render.pickup.depth=p_value[3];
	}
	
	this.draw_component=function(method_data,render_data,
			render_id,part_id,component_id,driver_id,component_render_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
	}
	this.append_component_parameter=function(
			component_id,		driver_id,		render_id,		part_id,
			buffer_data_item,	part_object,	part_driver,	render_driver,	render)
	{
	}
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
		
		if(this.id_depth_texture!=null){
			this.id_depth_texture.destroy();
			this.id_depth_texture=null;
		}
		if(this.id_texture_0!=null){
			this.id_texture_0.destroy();
			this.id_texture_0=null;	
		}
		if(this.id_texture_1!=null){	
			this.id_texture_1.destroy();
			this.id_texture_1=null;
		}
		if(this.id_buffer_0!=null){	
			this.id_buffer_0.destroy();
			this.id_buffer_0=null;
		}
		if(this.id_buffer_1!=null){	
			this.id_buffer_1.destroy();
			this.id_buffer_1=null;
		}
		
		if(this.value_depth_texture!=null){
			this.value_depth_texture.destroy();
			this.value_depth_texture=null;
		}
		if(this.value_texture!=null){
			this.value_texture.destroy();
			this.value_texture=null;
		}
		if(this.value_buffer!=null){	
			this.value_buffer.destroy();
			this.value_buffer=null;
		}
	}
};