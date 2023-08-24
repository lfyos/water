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
				render.system_bindgroup_layout
			]
		}),

		vertex:
		{
			module		:	my_module,
			entryPoint	:	"vertex_main",
			constants	:
			{
				primitive_type	:	0
			},
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
						{	//ID
							format			:	"float32x4",
							offset			:	48,
							shaderLocation	:	1
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
							shaderLocation	:	2
						}
					]
				}
			]
		},
		fragment		:
		{
			module		:	my_module,
			entryPoint	:	"fragment_id_fun",
			targets	: 
			[
				{
					format		:	"rgba32sint"
				},
				{
					format		:	"rgba32sint"
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
	
	this.id_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.fragment.targets.length		=1;
	pipeline_descr.fragment.targets[0].format	=render.webgpu.gpu.getPreferredCanvasFormat();
	if(render.parameter.multisample>1)
		pipeline_descr.multisample={count:render.parameter.multisample};

	pipeline_descr.fragment.entryPoint			="fragment_face_fun";
	pipeline_descr.vertex.constants				={primitive_type:1};
	this.face_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.fragment.entryPoint			="fragment_edge_fun";
	pipeline_descr.vertex.constants				={primitive_type:2};
	pipeline_descr.primitive.topology			="line-list";
	this.edge_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);

	pipeline_descr.fragment.entryPoint			="fragment_point_fun";
	pipeline_descr.vertex.constants				={primitive_type:3};
	pipeline_descr.primitive.topology			="point-list";
	this.point_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	this.new_part_driver=construct_part_driver;
	
	this.method_render_flag=[true,false,true];
	
	this.destroy=function()
	{
		this.id_pipeline	=null;
		this.face_pipeline	=null;
		this.edge_pipeline	=null;
		this.point_pipeline	=null;
		
		this.new_part_driver=null;
		
		this.method_render_flag=null;
	}
}
