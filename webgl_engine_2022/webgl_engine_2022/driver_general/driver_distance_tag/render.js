function new_render_driver(
	render_id,render_name,init_data,text_array,shader_code,render)
{
	var my_bindgroup_layout_entries=[
		{	// parameter buffer
			binding		:	0,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	false
			}
		},
		{	//texture
			binding		:	1,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//sampler
			binding		:	2,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			sampler		:
			{
				type	:	"filtering"
			}
		}
	];
	this.bindgroup_layout=render.webgpu.device.createBindGroupLayout(
	{
		entries	:	my_bindgroup_layout_entries
	});
	
	var my_module=render.webgpu.device.createShaderModule(
			{
				code: shader_code
			});
	var pipeline_descr=
	{
		layout: render.webgpu.device.createPipelineLayout(
		{
			bindGroupLayouts:
			[
				render.system_bindgroup_layout,
				this.bindgroup_layout
			]
		}),
		vertex:
		{
			module		:	my_module,
			entryPoint	:	"vertex_main",
			buffers		:
			[
				{
					arrayStride	:	80,
						
					stepMode	:	"vertex",
						
					attributes	:
					[
						{	//vertex
							format			:	"float32x4",
							offset			:	0,
							shaderLocation	:	0
						},
						{	//attribute
							format			:	"float32x4",
							offset			:	64,
							shaderLocation	:	1
						}
					]
				}
			]
		}, 
		fragment		:
		{
			module		:	my_module,
			
			entryPoint	:	"fragment_main",
			targets	: 
			[
				{
					format		:	render.webgpu.gpu.getPreferredCanvasFormat(),
					blend		:
					{
						color	:
						{
							operation	:	"add",
    						srcFactor	:	"src-alpha",
							dstFactor	:	"one-minus-src-alpha"
						},
						alpha	:
						{
							operation	:	"add",
    						srcFactor	:	"src-alpha",
							dstFactor	:	"one-minus-src-alpha"
						}
					},
					writeMask	:	GPUColorWrite. ALL
				},
				{
					format		:	"rgba32sint",
					writeMask	:	GPUColorWrite. ALL
				}
			],
		},
		primitive	:
		{
			topology:"triangle-list",
		},
		depthStencil	:
		{
			format				:	"depth24plus-stencil8",
			depthWriteEnabled	:	true,
    		depthCompare		:	"less",

   			stencilFront		:	{},
    		stencilBack			:	{},

 			stencilReadMask		:	0xFFFFFFFF,
			stencilWriteMask	:	0xFFFFFFFF,
			
			depthBias			:	0,
    		depthBiasSlopeScale	:	0,
    		depthBiasClamp		:	0
		}
	};

	pipeline_descr.vertex.constants				={primitive_type:0};
	pipeline_descr.fragment.constants			={primitive_type:0};
	pipeline_descr.primitive.topology="triangle-list";
	this.face_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.vertex.constants				={primitive_type:1};
	pipeline_descr.fragment.constants			={primitive_type:1};
	pipeline_descr.primitive.topology="line-list";
	this.edge_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.vertex.constants				={primitive_type:2};
	pipeline_descr.fragment.constants			={primitive_type:2};
	pipeline_descr.primitive.topology="point-list";
	this.point_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);

	this.new_part_driver=construct_part_driver;
	
	this.destroy=function()
	{
		this.bindgroup_layout	=null;
		this.face_pipeline		=null;
		this.edge_pipeline		=null;
		this.point_pipeline		=null;
		
		this.new_part_driver	=null;
	}
}