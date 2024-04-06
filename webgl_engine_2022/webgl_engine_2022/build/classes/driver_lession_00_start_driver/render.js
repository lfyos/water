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
					format		:	"rgba32float"
				},
				{
					format		:	"rgba32sint"
				}
			]
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
	pipeline_descr.fragment.entryPoint="fragment_id_fun";
	pipeline_descr.fragment.targets=[
		{
			format:"rgba32sint"
		},
		{
			format:"rgba32sint"
		}
	];
	this.id_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.fragment.entryPoint="fragment_value_fun";
	pipeline_descr.fragment.targets=[
		{
			format:"rgba32float"
		}
	];
	this.value_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.fragment.entryPoint="fragment_render_fun";
	pipeline_descr.fragment.targets=[
		{
			format:render.webgpu.gpu.getPreferredCanvasFormat()
		}
	];

	if(render.parameter.multisample>1)
		pipeline_descr.multisample={count:render.parameter.multisample};
		
	this.render_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);

	this.method_render_flag=[true,true,true];
	
	this.new_part_driver=construct_part_driver;
	
	this.destroy=function()
	{
		this.id_pipeline		=null;
		this.value_pipeline		=null;
		this.render_pipeline	=null;
		this.method_render_flag	=null;
		
		this.new_part_driver	=null;
	}
}