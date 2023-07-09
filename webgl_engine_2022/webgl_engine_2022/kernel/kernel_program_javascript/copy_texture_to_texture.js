function construct_texture_to_texture_copy(
	my_source_texture,my_destination_format,my_webgpu)
{
	this.destroy=function()
	{
		this.pipeline			=null;
		this.bindgroup			=null;
		
		this.do_copy			=null;
	}

	this.vertex_buffer=my_webgpu.device.createBuffer(
		{
			size	:	Float32Array.BYTES_PER_ELEMENT*16,
			usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.VERTEX,
		});

	var my_module=my_webgpu.device.createShaderModule(
		{
			code: 	
				"struct to_vertex_struct											"+
				"{																	"+
				"	@builtin(vertex_index)	vertex_index	:	u32,				"+
				"	@location(0) 			par0			:	vec4<f32>,			"+
				"	@location(1)			par1			:	vec4<f32>,			"+
				"	@location(2)			par2			:	vec4<f32>,			"+
				"	@location(3)			par3			:	vec4<f32>			"+
				"};																	"+
				
				"struct inout_struct{												"+
				"	@location(0)			texture_coord	:	vec4<f32>,			"+
				"	@builtin(position)		position		:	vec4<f32>			"+
				"};																	"+
				"@group(0) @binding(0) var source_texture:texture_2d<f32>;			"+
				"@group(0) @binding(1) var source_sampler:sampler;					"+
	
				"@vertex															"+
				"fn vertex_main(to_vertex	:	to_vertex_struct)-> inout_struct	"+
				"{																	"+
				"	var vert_pos=array<vec2<f32>,4>(								"+
				"		to_vertex.par0.xy,		to_vertex.par0.zw,					"+
				"		to_vertex.par1.xy,		to_vertex.par1.zw					"+
				"	);																"+
				"	var text_pos=array<vec2<f32>, 4>(								"+
				"		to_vertex.par2.xy,		to_vertex.par2.zw,					"+
				"		to_vertex.par3.xy,		to_vertex.par3.zw					"+
				"	);																"+
				"	var index_array	=array<u32,6>(0,1,3,	0,3,2);					"+
				"	var vertex_index=index_array[to_vertex.vertex_index];				"+
				"	var io	:	inout_struct;										"+
				"	io.position		=vec4<f32>(vert_pos[vertex_index],0.0,1.0);		"+
				"	io.texture_coord=vec4<f32>(text_pos[vertex_index],0.0,1.0);		"+
				"	return io;														"+
				"}																	"+
	
				"@fragment															"+
				"fn fragment_main(io:inout_struct)-> @location(0) vec4<f32>			"+
				"{																	"+
				"	return textureSample(	source_texture,source_sampler,			"+
				"							io.texture_coord.xy);					"+
				"}																	"	
		});
	var my_bindgroup_layout=my_webgpu.device.createBindGroupLayout(
		{
			entries	:
			[
				{
					binding		:	0,
					visibility	:	GPUShaderStage.FRAGMENT,
					texture		:
					{
						sampleType		:	"unfilterable-float",
		    			viewDimension	:	"2d",
		   				multisampled	:	false
					}
				},
				{	//sampler
					binding		:	1,
					visibility	:	GPUShaderStage.FRAGMENT,
					sampler		:
					{
						type	:	 "non-filtering"
					}
				}
			]
		});

	this.pipeline=my_webgpu.device.createRenderPipeline(
		{
			layout			: my_webgpu.device.createPipelineLayout(
			{
				bindGroupLayouts:
				[
					my_bindgroup_layout
				]
			}),
		
			vertex			:
			{
				module		:	my_module,
				entryPoint	:	"vertex_main",
				buffers		:
				[
					{
						arrayStride	:	Float32Array.BYTES_PER_ELEMENT*16,
						stepMode	:	"instance",
						attributes	:
						[
							{
									format			:	"float32x4",
    								offset			:	0,
									shaderLocation	:	0
							},
							{
									format			:	"float32x4",
    								offset			:	Float32Array.BYTES_PER_ELEMENT*4,
									shaderLocation	:	1
							},
							{
									format			:	"float32x4",
    								offset			:	Float32Array.BYTES_PER_ELEMENT*8,
									shaderLocation	:	2
							},
							{
									format			:	"float32x4",
    								offset			:	Float32Array.BYTES_PER_ELEMENT*12,
									shaderLocation	:	3
							}
						]
					}
				]
			},
				    
			fragment		:
			{
				module		:	my_module,
				
				entryPoint	:	"fragment_main",
				targets		: 
				[
					{
						format	:	my_destination_format
					}
				]
			},
			primitive	:
			{
				topology:"triangle-list"
			}
		});	

	this.bindgroup=my_webgpu.device.createBindGroup(
		{
			layout		:	my_bindgroup_layout,
			entries		:	[
				{
					binding		:	0,
					resource	:	my_source_texture.createView()
				},
				{
					binding		:	1,
					resource	:	my_webgpu.device.createSampler(
						{
							addressModeU	:	"mirror-repeat",
							addressModeV	:	"mirror-repeat",
							magFilter		:	"nearest",
							minFilter		:	"nearest",
							mipmapFilter	:	"nearest"
						})
				}
			]
		});	
	
	this.do_copy=function(clear_or_load_flag,
		vertex_pos,texture_pos,my_destination_texture,my_webgpu)
	{
		var my_data=new Array(),x0,y0,dx,dy
		
		x0=vertex_pos[0];	y0=vertex_pos[1];	dx=vertex_pos[2];	dy=vertex_pos[3];
		my_data.push(	x0,y0,	x0+dx,y0,	x0,y0+dy,	x0+dx,y0+dy);
		
		x0=texture_pos[0];	y0=texture_pos[1];	dx=texture_pos[2];	dy=texture_pos[3];
		my_data.push(	x0,y0,	x0+dx,y0,	x0,y0+dy,	x0+dx,y0+dy);
		
		my_webgpu.device.queue.writeBuffer(this.vertex_buffer,0,new Float32Array(my_data));
					
		var render_pass_encoder = my_webgpu.command_encoder.beginRenderPass(
		{
			colorAttachments		: 
			[
				{
					view			:	my_destination_texture.createView(),
					clearValue		:	{ r: 0.0, g: 0.0, b: 0.0, a: 1.0 },
					loadOp			:	clear_or_load_flag?"clear":"load",
					storeOp			:	"store"
				}	
			]
		});	
		render_pass_encoder.setPipeline(this.pipeline);
		render_pass_encoder.setBindGroup(0,this.bindgroup);
		render_pass_encoder.setVertexBuffer(0,this.vertex_buffer);
    	render_pass_encoder.draw(6); 
    	render_pass_encoder.end();
	}	
}