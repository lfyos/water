function new_render_driver(	render_id,render_name,init_data,shader_code,text_array,render)
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
				render.system_buffer.system_bindgroup_layout
			]
		}),
		vertex	:
		{
			module			:	my_module,
			entryPoint		:	"vertex_main",
			buffers			:
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
						{	//material
							format			:	"float32x4",
							offset			:	32,
							shaderLocation	:	1
						}
					]
				},
				{
					arrayStride	:	32,
					stepMode	:	"instance",
					attributes	:
					[
						{	//p0
							format			:	"float32x4",
							offset			:	0,
							shaderLocation	:	2
						},
						{	//p1
							format			:	"float32x4",
							offset			:	16,
							shaderLocation	:	3
						}
					]
				}
			]
		},
		fragment	:
		{
			module		:	my_module,
			entryPoint	:	"fragment_main",
			targets	: 
			[
				{
					format		:	render.webgpu.gpu.getPreferredCanvasFormat()
				}
			],
		},
		primitive	:
		{
			topology:"line-list",
		},
		depthStencil	:
		{
			format				:	"depth24plus-stencil8",
			depthWriteEnabled	:	true,
    		depthCompare		:	"less-equal",

   			stencilFront		:	{},
    		stencilBack			:	{},

 			stencilReadMask		:	0xFFFFFFFF,
			stencilWriteMask	:	0xFFFFFFFF,
		}
	};
	if(render.parameter.multisample>1)
		pipeline_descr.multisample={count:render.parameter.multisample};
	this.pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	this.new_part_driver=construct_part_driver;
	
	this.method_render_flag=[false,false,true];
}