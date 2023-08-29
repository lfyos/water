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
				primitive_type	:	0,
				point_size		:	init_data.point_size
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
			module		:	my_module,
			
			entryPoint	:	"fragment_main",
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
    		depthCompare		:	"less",

   			stencilFront		:	{},
    		stencilBack			:	{},

 			stencilReadMask		:	0xFFFFFFFF,
			stencilWriteMask	:	0xFFFFFFFF,
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
			format:"rgba32float"
		}
	];
	
	var normal_color_targets=[
		{
			format:render.webgpu.gpu.getPreferredCanvasFormat()
		}
	];
	
	pipeline_descr.fragment.targets=id_target;
	
	pipeline_descr.fragment.entryPoint="fragment_id_face";
	pipeline_descr.vertex.constants.primitive_type	=0;
	this.id_face_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.fragment.entryPoint="fragment_id_point";
	pipeline_descr.vertex.constants.primitive_type	=1;
	this.id_point_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.fragment.targets=value_target;
	pipeline_descr.fragment.entryPoint="fragment_value_face";
	pipeline_descr.vertex.constants.primitive_type	=2;
	this.value_face_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.fragment.entryPoint="fragment_value_point";
	pipeline_descr.vertex.constants.primitive_type	=3;
	this.value_point_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	if(render.parameter.multisample>1)
		pipeline_descr.multisample={count:render.parameter.multisample};
		
	pipeline_descr.fragment.targets=normal_color_targets;
		
	pipeline_descr.fragment.entryPoint="fragment_color_face";
	pipeline_descr.vertex.constants.primitive_type	=4;
	this.color_face_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.primitive.topology="line-list";
	pipeline_descr.fragment.entryPoint="fragment_color_edge";
	pipeline_descr.vertex.constants.primitive_type	=5;
	this.color_edge_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);

	pipeline_descr.primitive.topology="line-list";
	pipeline_descr.fragment.entryPoint="fragment_color_frame";
	pipeline_descr.vertex.constants.primitive_type	=6;
	this.color_frame_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);

	pipeline_descr.primitive.topology="triangle-list";
	pipeline_descr.fragment.entryPoint="fragment_color_pickup_point";
	pipeline_descr.vertex.constants.primitive_type	=7;
	this.color_pickup_point_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.primitive.topology="triangle-list";
	pipeline_descr.fragment.entryPoint="fragment_color_normal_point";
	pipeline_descr.vertex.constants.primitive_type	=8;
	this.color_normal_point_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);

	this.new_part_driver=construct_part_driver;
	
	this.method_render_flag=[true,true,true];
	
	this.destroy=function()
	{
		this.pipeline=null;
		this.method_render_flag	=null;
	}
}