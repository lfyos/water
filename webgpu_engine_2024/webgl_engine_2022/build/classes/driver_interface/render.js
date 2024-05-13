function new_render_driver(	render_id,render_name,init_data,shader_code,text_array,render)
{
	var layout_entries=[
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
			entries	:layout_entries
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
				render.system_buffer.system_bindgroup_layout,
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
						{	//texture
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
			entryPoint	:	"fragment_id_fun",
			targets		: 
			[
				{
					format		:	"rgba32sint"
				},
				{
					format		:	"rgba32sint"
				}
			],
		},
		primitive		:
		{
			topology	:	"triangle-list",
		},
		depthStencil	:
		{
			format				:	"depth24plus-stencil8",
			depthWriteEnabled	:	true,
    		depthCompare		:	"less-equal",
		}
	};
	
	pipeline_descr.vertex.constants				={method_id:0};
	this.id_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.fragment.entryPoint			="fragment_color_fun";
	pipeline_descr.fragment.targets.length		=1;
	pipeline_descr.fragment.targets[0].format	=render.webgpu.gpu.getPreferredCanvasFormat();
	if(render.parameter.multisample>1)
		pipeline_descr.multisample={count:render.parameter.multisample};
	
	pipeline_descr.vertex.constants				={method_id:2};
	this.color_pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	this.new_part_driver=construct_part_driver;
	
	this.method_render_flag=[true,false,true];
}
