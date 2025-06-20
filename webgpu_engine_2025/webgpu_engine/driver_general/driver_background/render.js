function new_render_driver(	render_id,render_name,init_data,shader_code,text_array,scene)
{
	var texture_bindgroup_layout_entries=[
		{	//left
			binding		:	0,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:	{}
		},
		{	//right
			binding		:	1,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:	{}
		},
		{	//top
			binding		:	2,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:	{}
		},
		{	//down
			binding		:	3,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:	{}
		},
		{	//front
			binding		:	4,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:	{}
		},
		{	//back
			binding		:	5,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:	{}
		},
		{	//no box
			binding		:	6,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:	{}
		},
		{	//sampler
			binding		:	7,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			sampler		:	{}
		}
	];
	this.texture_bindgroup_layout=scene.webgpu.device.createBindGroupLayout(
			{
				entries	:texture_bindgroup_layout_entries
			});
	
	var my_module=scene.webgpu.device.createShaderModule(
			{
				code: shader_code
			});
			
	var pipeline_descr=
	{
		layout: scene.webgpu.device.createPipelineLayout(
		{
			bindGroupLayouts:
			[
				scene.system_buffer.system_bindgroup_layout,
				this.texture_bindgroup_layout
			]
		}),
		vertex:
		{
			module		:	my_module,
			entryPoint	:	"vertex_main",
			buffers		:
			[
				{
					arrayStride	:	64,
					stepMode	:	"vertex",
					attributes	:
					[
						{	//vertex
							format			:	"float32x4",
							offset			:	0,
							shaderLocation	:	0
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
					format		:	scene.webgpu.gpu.getPreferredCanvasFormat()
				}
			]
		},
		primitive		:
		{
			topology	:	"triangle-list",
		},
		multisample		:
		{
			count		:	scene.parameter.multisample
		},
		depthStencil	:
		{
			format				:	"depth24plus-stencil8",
			depthWriteEnabled	:	true,
    		depthCompare		:	 "less-equal",

   			stencilFront		:	{},
    		stencilBack			:	{},

 			stencilReadMask		:	0xFFFFFFFF,
			stencilWriteMask	:	0xFFFFFFFF,
		}
	};

	pipeline_descr.fragment.constants={no_box_mode:true};
	this.no_box_pipeline = scene.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.fragment.constants={no_box_mode:false};
	this.box_pipeline = scene.webgpu.device.createRenderPipeline(pipeline_descr);

	this.new_part_driver=construct_part_driver;
	
	this.method_render_flag=[false,false,true];
}
