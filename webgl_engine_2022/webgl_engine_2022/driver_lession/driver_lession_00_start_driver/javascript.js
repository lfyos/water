function my_create_part_driver(part_object,render_driver,render)
{
	this.draw_component=function (method_data,target_data,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,render_driver,render)	
	{
		var p	=part_object.buffer_object.face.region_data;
		var rpe	=render.webgpu.render_pass_encoder;

		rpe.setPipeline(render_driver.pipeline);

		for(var i=0,ni=component_render_parameter.length;i<ni;i++){
			var buffer_id=component_render_parameter[i];
			render.set_system_bindgroup_by_part(
				target_data.render_buffer_id,
				method_data.method_id,
				part_object.render_id,
				part_object.part_id,
				buffer_id);

			for(var j=0,nj=p.length;j<nj;j++){
				rpe.setVertexBuffer(0,p[j].buffer);
				rpe.draw(p[i].item_number);
			}
		}
	}
	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		var ret_val=
		{
			material_id		:	buffer_object_data.material_id,
			region_box		:	buffer_object_data.region_box,
			region_data		:	(request_type_string!="face")?new Array():buffer_object_data.region_data,
			item_number		:	(request_type_string!="face")?0:(buffer_object_data.item_number),
			item_size		:	(request_type_string!="face")?4:
						(buffer_object_data.region_data.length/buffer_object_data.item_number),
			private_data	:	null
		};
		return ret_val;
	}
	this.destroy=function()
	{
			this.draw_component		=null;
			this.decode_vertex_data	=null;
	}
}


function main(	render_id,		render_name,
				init_data,		text_array,
				shader_code,	render)
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
			module	:	my_module,
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
		    
		fragment	:
		{
			module		:	my_module,
			
			entryPoint	:	"fragment_main",
			targets	: 
			[
				{
					format	:	render.webgpu.gpu.getPreferredCanvasFormat(),
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
	
	this.pipeline = render.webgpu.device.createRenderPipeline(pipeline_descr);

	this.create_part_driver=my_create_part_driver;
	
	this.destroy=function()
	{
		this.pipeline=null;
	}
}