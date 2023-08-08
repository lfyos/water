function new_render_driver(
	render_id,render_name,init_data,text_array,shader_code,render)
{
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
				render.system_bindgroup_layout
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
						{	//normal
							format			:	"float32x4",
							offset			:	16,
							shaderLocation	:	1
						},
						{	//material
							format			:	"float32x4",
							offset			:	32,
							shaderLocation	:	2
						},
						{	//ID
							format			:	"float32x4",
							offset			:	48,
							shaderLocation	:	3
						},
						{	//attribute
							format			:	"float32x4",
							offset			:	64,
							shaderLocation	:	4
						}
					]
				},
				{
					arrayStride	:	16,
					stepMode	:	"instance",
					attributes	:
					[
						{	//scale
							format			:	"float32x4",
							offset			:	0,
							shaderLocation	:	5
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
					format		:	"rgba32float",
					writeMask	:	GPUColorWrite.ALL
				},
				{
					format		:	"rgba32sint",
					writeMask	:	GPUColorWrite.ALL
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
	pipeline_descr.fragment.targets[0].format	="rgba32float";
	pipeline_descr.fragment.targets[1].writeMask=GPUColorWrite.ALL;
	pipeline_descr.primitive.topology			="triangle-list";
	this.value_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);

	pipeline_descr.vertex.constants				={primitive_type:1};
	pipeline_descr.fragment.constants			={primitive_type:1};
	pipeline_descr.fragment.targets[0].format	=render.webgpu.gpu.getPreferredCanvasFormat();
	pipeline_descr.fragment.targets[1].writeMask=GPUColorWrite.ALL;
	pipeline_descr.primitive.topology			="triangle-list";
	this.color_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.vertex.constants				={primitive_type:2};
	pipeline_descr.fragment.constants			={primitive_type:2};
	pipeline_descr.fragment.targets[0].format	=render.webgpu.gpu.getPreferredCanvasFormat();
	pipeline_descr.fragment.targets[1].writeMask=0;
	pipeline_descr.primitive.topology			="line-list";
	this.line_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);

	pipeline_descr.vertex.constants				={primitive_type:3};
	pipeline_descr.fragment.constants			={primitive_type:3};
	pipeline_descr.fragment.targets[0].format	=render.webgpu.gpu.getPreferredCanvasFormat();
	pipeline_descr.fragment.targets[1].writeMask=0;
	pipeline_descr.primitive.topology			="point-list";
	this.point_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	this.new_part_driver=construct_part_driver;
	
	this.destroy=function()
	{
		this.value_pipeline	=null;
		this.color_pipeline	=null;
		this.line_pipeline	=null;
		this.point_pipeline	=null;
	}
}
