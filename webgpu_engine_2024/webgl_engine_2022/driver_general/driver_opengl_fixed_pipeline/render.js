function create_one_render_driver(array_stride,material_offset,
			material_bindgroup_layout,module,render_material,render)
{
	var pipeline_descr=
	{
		layout: render.webgpu.device.createPipelineLayout(
		{
			bindGroupLayouts:
			[
				render.system_buffer.system_bindgroup_layout,
				material_bindgroup_layout
			]
		}),

		vertex:
		{
			module		:	module,
			entryPoint	:	"vertex_main",
			constants	:
			{
				primitive_type	:	0,
				clip_type		:	0,
				point_size		:	(typeof(render_material.point_size)!="number")
									?10:(render_material.point_size)
			},
			buffers		:
			[
				{
					arrayStride	:	Float32Array.BYTES_PER_ELEMENT*array_stride,
						
					stepMode	:	"vertex",
						
					attributes	:
					[
						{	//vertex
							format			:	"float32x4",
							offset			:	0,
							shaderLocation	:	0
						},
						{	//normal
							format			:	"float32x4",
							offset			:	16,
							shaderLocation	:	1
						},
						{	//material
							format			:	"float32x4",
							offset			:	Float32Array.BYTES_PER_ELEMENT*material_offset,
							shaderLocation	:	2
						},
						{	//ID
							format			:	"float32x4",
							offset			:	48,
							shaderLocation	:	3
						},
						{	//texture
							format			:	"float32x4",
							offset			:	64,
							shaderLocation	:	4
						}
					]
				}
			]
		},   
		fragment		:
		{
			module		:	module,
			entryPoint	:	"fragment_main",
			constants	:
			{
				primitive_type	:	0,
				clip_type		:	0
			},
			targets		:	null
		},
		primitive	:
		{
			topology:"triangle-list",
		},
		depthStencil	:
		{
			format				:	"depth24plus-stencil8",
			depthWriteEnabled	:	true,
    		depthCompare		:	"less-equal",

   			stencilFront		:	{},
    		stencilBack			:	{},

 			stencilReadMask		:	0x01,
			stencilWriteMask	:	0x01,
		}
	};
	var id_target=[
		{
			format		:	"rgba32sint",
			writeMask	:	GPUColorWrite.ALL
		},
		{
			format		:	"rgba32sint",
			writeMask	:	GPUColorWrite.ALL
		}
	];
	var value_target=[
		{
			format		:	"rgba32float",
			writeMask	:	GPUColorWrite.ALL
		}
	];
	var depth_color_targets=[
		{
			format		:	render.webgpu.gpu.getPreferredCanvasFormat(),
			writeMask	:	0
		}
	];
	var normal_color_targets=[
		{
			format		:	render.webgpu.gpu.getPreferredCanvasFormat(),
			writeMask	:	GPUColorWrite.ALL,
			blend		:
			{
				color	:
				{
					operation	:	"add",
    				srcFactor	:	"src-alpha",
    				dstFactor 	:	"one-minus-src-alpha"
				},
    			alpha	:
    			{
					operation	:	"add",
    				srcFactor	:	"src-alpha",
    				dstFactor 	:	"one-minus-src-alpha"    				
				}
			}
		}
	];
	
	var no_clip_stencil={
		compare			:	"always",
    	failOp			:	"keep",
    	depthFailOp		:	"keep",
    	passOp			:	"keep"
	};
	var do_clip_stencil={
		compare			:	"always",
    	failOp			:	"increment-wrap",
    	depthFailOp		:	"increment-wrap",
    	passOp			:	"increment-wrap"
	};
	var do_close_stencil={
		compare			:	"not-equal",
    	failOp			:	"keep",
    	depthFailOp		:	"keep",
    	passOp			:	"zero"
	};

	pipeline_descr.fragment.targets=id_target;

	pipeline_descr.primitive.topology="triangle-list";
	pipeline_descr.fragment.entryPoint="fragment_id_function";
	pipeline_descr.vertex.constants.primitive_type	=0;
	pipeline_descr.fragment.constants.primitive_type=0;
	pipeline_descr.vertex.constants.clip_type		=0;
	pipeline_descr.fragment.constants.clip_type		=0;
	pipeline_descr.depthStencil.stencilFront		=no_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=no_clip_stencil;
	this.id_face_pipeline_no_clip=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.vertex.constants.clip_type		=1;
	pipeline_descr.fragment.constants.clip_type		=1;
	pipeline_descr.depthStencil.stencilFront		=do_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=do_clip_stencil;
	this.id_face_pipeline_do_clip=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.vertex.constants.clip_type		=2;
	pipeline_descr.fragment.constants.clip_type		=2;
	pipeline_descr.depthStencil.stencilFront		=do_close_stencil;
	pipeline_descr.depthStencil.stencilBack			=do_close_stencil;
	this.id_face_pipeline_do_close=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.primitive.topology="triangle-list";
	pipeline_descr.fragment.entryPoint="fragment_id_function";
	pipeline_descr.vertex.constants.primitive_type	=1;
	pipeline_descr.fragment.constants.primitive_type=1;
	pipeline_descr.vertex.constants.clip_type		=1;
	pipeline_descr.fragment.constants.clip_type		=1;
	pipeline_descr.depthStencil.stencilFront		=no_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=no_clip_stencil;
	pipeline_descr.vertex.buffers[0].stepMode		="instance";
	this.id_point_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	pipeline_descr.vertex.buffers[0].stepMode		="vertex";
	
	pipeline_descr.fragment.targets=value_target;
	
	pipeline_descr.primitive.topology="triangle-list";
	pipeline_descr.fragment.entryPoint="fragment_value_function";
	pipeline_descr.vertex.constants.primitive_type	=2;
	pipeline_descr.fragment.constants.primitive_type=2;
	pipeline_descr.vertex.constants.clip_type		=0;
	pipeline_descr.fragment.constants.clip_type		=0;
	pipeline_descr.depthStencil.stencilFront		=no_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=no_clip_stencil;
	this.value_face_pipeline_no_clip=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.vertex.constants.clip_type		=1;
	pipeline_descr.fragment.constants.clip_type		=1;
	pipeline_descr.depthStencil.stencilFront		=do_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=do_clip_stencil;
	this.value_face_pipeline_do_clip=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.vertex.constants.clip_type		=2;
	pipeline_descr.fragment.constants.clip_type		=2;
	pipeline_descr.depthStencil.stencilFront		=do_close_stencil;
	pipeline_descr.depthStencil.stencilBack			=do_close_stencil;
	this.value_face_pipeline_do_close=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.primitive.topology="triangle-list";
	pipeline_descr.fragment.entryPoint="fragment_value_function";
	pipeline_descr.vertex.constants.primitive_type	=3;
	pipeline_descr.fragment.constants.primitive_type=3;
	pipeline_descr.vertex.constants.clip_type		=1;
	pipeline_descr.fragment.constants.clip_type		=1;
	pipeline_descr.depthStencil.stencilFront		=no_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=no_clip_stencil;
	pipeline_descr.vertex.buffers[0].stepMode		="instance";
	this.value_point_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	pipeline_descr.vertex.buffers[0].stepMode		="vertex";
	
	if(render.parameter.multisample>1)
		pipeline_descr.multisample={count:render.parameter.multisample};
		
	pipeline_descr.fragment.entryPoint="fragment_color_function";
	pipeline_descr.vertex.constants.primitive_type	=4;
	pipeline_descr.fragment.constants.primitive_type=4;
	
	pipeline_descr.fragment.targets=depth_color_targets;
	
	pipeline_descr.vertex.constants.clip_type		=0;
	pipeline_descr.fragment.constants.clip_type		=0;
	pipeline_descr.depthStencil.stencilFront		=no_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=no_clip_stencil;
	this.depth_face_pipeline_no_clip=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.vertex.constants.clip_type		=1;
	pipeline_descr.fragment.constants.clip_type		=1;
	pipeline_descr.depthStencil.stencilFront		=do_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=do_clip_stencil;
	this.depth_face_pipeline_do_clip=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.vertex.constants.clip_type		=2;
	pipeline_descr.fragment.constants.clip_type		=2;
	pipeline_descr.depthStencil.stencilFront		=do_close_stencil;
	pipeline_descr.depthStencil.stencilBack			=do_close_stencil;
	this.depth_face_pipeline_do_close=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.fragment.targets=normal_color_targets;
	
	pipeline_descr.vertex.constants.clip_type		=0;
	pipeline_descr.fragment.constants.clip_type		=0;
	pipeline_descr.depthStencil.stencilFront		=no_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=no_clip_stencil;
	this.color_face_pipeline_no_clip=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.vertex.constants.clip_type		=1;
	pipeline_descr.fragment.constants.clip_type		=1;
	pipeline_descr.depthStencil.stencilFront		=do_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=do_clip_stencil;
	this.color_face_pipeline_do_clip=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.vertex.constants.clip_type		=2;
	pipeline_descr.fragment.constants.clip_type		=2;
	pipeline_descr.depthStencil.stencilFront		=do_close_stencil;
	pipeline_descr.depthStencil.stencilBack			=do_close_stencil;
	this.color_face_pipeline_do_close=render.webgpu.device.createRenderPipeline(pipeline_descr);
		
	pipeline_descr.primitive.topology="line-list";
	pipeline_descr.fragment.entryPoint="fragment_color_function";
	pipeline_descr.vertex.constants.primitive_type	=5;
	pipeline_descr.fragment.constants.primitive_type=5;
	pipeline_descr.vertex.constants.clip_type		=1;
	pipeline_descr.fragment.constants.clip_type		=1;
	pipeline_descr.depthStencil.stencilFront		=no_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=no_clip_stencil;
	this.color_edge_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);

	pipeline_descr.primitive.topology="triangle-list";
	pipeline_descr.fragment.entryPoint="fragment_color_function";
	pipeline_descr.vertex.constants.primitive_type	=7;
	pipeline_descr.fragment.constants.primitive_type=7;
	pipeline_descr.vertex.constants.clip_type		=1;
	pipeline_descr.fragment.constants.clip_type		=1;
	pipeline_descr.depthStencil.stencilFront		=no_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=no_clip_stencil;
	pipeline_descr.vertex.buffers[0].stepMode		="instance";
	this.color_pickup_point_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.primitive.topology="triangle-list";
	pipeline_descr.fragment.entryPoint="fragment_color_function";
	pipeline_descr.vertex.constants.primitive_type	=8;
	pipeline_descr.fragment.constants.primitive_type=8;
	pipeline_descr.vertex.constants.clip_type		=1;
	pipeline_descr.fragment.constants.clip_type		=1;
	pipeline_descr.depthStencil.stencilFront		=no_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=no_clip_stencil;
	pipeline_descr.vertex.buffers[0].stepMode		="instance";
	this.color_normal_point_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.primitive.topology="line-strip";
	pipeline_descr.vertex.entryPoint="vertex_main_frame";
	pipeline_descr.fragment.entryPoint="fragment_color_function";
	pipeline_descr.vertex.constants.primitive_type	=6;
	pipeline_descr.fragment.constants.primitive_type=6;
	pipeline_descr.vertex.constants.clip_type		=1;
	pipeline_descr.fragment.constants.clip_type		=1;
	pipeline_descr.depthStencil.stencilFront		=no_clip_stencil;
	pipeline_descr.depthStencil.stencilBack			=no_clip_stencil;
	pipeline_descr.vertex.buffers[0].stepMode		="instance";
	pipeline_descr.vertex.buffers[0].arrayStride	=Float32Array.BYTES_PER_ELEMENT*array_stride*3;
	pipeline_descr.vertex.buffers[0].attributes		=[
		{	//vertex
			format			:	"float32x4",
			offset			:	0,
			shaderLocation	:	0
		},
		{	//normal
			format			:	"float32x4",
			offset			:	16,
			shaderLocation	:	1
		},
		{	//material
			format			:	"float32x4",
			offset			:	Float32Array.BYTES_PER_ELEMENT*material_offset,
			shaderLocation	:	2
		},
		{	//ID
			format			:	"float32x4",
			offset			:	48,
			shaderLocation	:	3
		},
		{	//texture
			format			:	"float32x4",
			offset			:	64,
			shaderLocation	:	4
		},
		
		{	//vertex
			format			:	"float32x4",
			offset			:	Float32Array.BYTES_PER_ELEMENT*array_stride+0,
			shaderLocation	:	5
		},
		{	//normal
			format			:	"float32x4",
			offset			:	Float32Array.BYTES_PER_ELEMENT*array_stride+16,
			shaderLocation	:	6
		},
		{	//material
			format			:	"float32x4",
			offset			:	Float32Array.BYTES_PER_ELEMENT*array_stride+Float32Array.BYTES_PER_ELEMENT*material_offset,
			shaderLocation	:	7
		},
		{	//ID
			format			:	"float32x4",
			offset			:	Float32Array.BYTES_PER_ELEMENT*array_stride+48,
			shaderLocation	:	8
		},
		{	//texture
			format			:	"float32x4",
			offset			:	Float32Array.BYTES_PER_ELEMENT*array_stride+64,
			shaderLocation	:	9
		},
		
		{	//vertex
			format			:	"float32x4",
			offset			:	2*Float32Array.BYTES_PER_ELEMENT*array_stride+0,
			shaderLocation	:	10
		},
		{	//normal
			format			:	"float32x4",
			offset			:	2*Float32Array.BYTES_PER_ELEMENT*array_stride+16,
			shaderLocation	:	11
		},
		{	//material
			format			:	"float32x4",
			offset			:	2*Float32Array.BYTES_PER_ELEMENT*array_stride+Float32Array.BYTES_PER_ELEMENT*material_offset,
			shaderLocation	:	12
		},
		{	//ID
			format			:	"float32x4",
			offset			:	2*Float32Array.BYTES_PER_ELEMENT*array_stride+48,
			shaderLocation	:	13
		},
		{	//texture
			format			:	"float32x4",
			offset			:	2*Float32Array.BYTES_PER_ELEMENT*array_stride+64,
			shaderLocation	:	14
		}
	];
	this.color_frame_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
}

function new_render_driver(	render_id,render_name,init_data,shader_code,text_array,render)
{
	this.render_material=init_data;
	
	this.tmp_texture=render.webgpu.device.createTexture(
			{
				size:
				{
					width	:	1,
					height	:	1
				},
				format		:	"rgba16float",
				usage		:	 GPUTextureUsage.TEXTURE_BINDING 
								|GPUTextureUsage.COPY_DST
								|GPUTextureUsage.RENDER_ATTACHMENT
	    	});
	var my_bindgroup_layout_entries=[
		{	
			// material
			binding		:	0,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	false
			}
		},
		{	//texture_1
			binding		:	1,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//texture_2
			binding		:	2,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//texture_3
			binding		:	3,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//texture_4
			binding		:	4,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//sampler
			binding		:	5,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			sampler		:
			{
				type	:	"filtering"
			}
		},
		{	//sampler
			binding		:	6,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			sampler		:
			{
				type	:	"filtering"
			}
		},
		{	//sampler
			binding		:	7,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			sampler		:
			{
				type	:	"filtering"
			}
		},
		{	//sampler
			binding		:	8,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			sampler		:
			{
				type	:	"filtering"
			}
		}
	];
	this.material_bindgroup_layout=render.webgpu.device.createBindGroupLayout(
			{
				entries	:	my_bindgroup_layout_entries
			});
			
	this.new_part_driver=construct_part_driver;
	this.method_render_flag=[true,true,true,true,true,true];		
			
	var my_module=render.webgpu.device.createShaderModule(
			{
				code: shader_code
			});
	
	this.pipeline_array=new Array(this.render_material.material_offset.length);
	
	for(var i=0,ni=this.pipeline_array.length;i<ni;i++){
		this.pipeline_array[i]=new create_one_render_driver(
				this.render_material.array_stride,
				this.render_material.material_offset[i],
				this.material_bindgroup_layout,my_module,
				this.render_material,render);
	};
	
	this.destroy=function()
	{
		if(this.tmp_texture!=null){
			this.tmp_texture.destroy();
			this.tmp_texture=null;
		}	
	}
}
