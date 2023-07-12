function my_create_part_driver(part_object,render_driver,render)
{
	this.replace_render_parameter=function(instance_id,
			old_instance_data,new_instance_data,part_object,render_buffer_id,render)
	{
		console.log(render_buffer_id+":replace : "+instance_id);	
	};
	this.append_render_parameter=function(instance_id,new_instance_data,part_object,render_buffer_id,render)
	{
		console.log(render_buffer_id+":append : "+instance_id);
	};
	this.delete_render_parameter=function(
				delete_index_id,	delete_data,
				last_index_id,		last_data,
				part_object,		render_buffer_id,
				render)
	{
		console.log(render_buffer_id+":delete : "+delete_index_id+"/"+last_index_id);
	};
	
	this.texture			=new Array(part_object.part_component_id_and_driver_id.length);
	this.texture_bindgroup	=new Array(part_object.part_component_id_and_driver_id.length);
	this.parameter			=new Array(part_object.part_component_id_and_driver_id.length);
	
	for(var i=0,ni=this.texture.length;i<ni;i++){
		this.texture[i]=render.webgpu.device.createTexture(
				{
					size:
					{
						width	:	part_object.material[0].texture_width,
						height	:	part_object.material[0].texture_height
					},
					format		:	 "rgba16float",
					usage		:	 GPUTextureUsage.TEXTURE_BINDING 
									|GPUTextureUsage.COPY_DST
									|GPUTextureUsage.RENDER_ATTACHMENT
			    });
		var resource_entries=[
					{	//texture
						binding		:	0,
						resource	:	this.texture[i].createView()
					},
					{
						//sampler
						binding		:	1,
						resource	:	render.webgpu.device.createSampler(
							{
								addressModeU	:	"repeat",
								addressModeV	:	"repeat",
								magFilter		:	"linear",
								minFilter		:	"linear",
								mipmapFilter	:	"linear"
							})
					}
				];
		this.texture_bindgroup[i]=render.webgpu.device.createBindGroup(
				{
					layout		:	render_driver.texture_bindgroup_layout,
					entries		:	resource_entries
				});
	};
				
	this.vertex_buffer=render.webgpu.device.createBuffer(
		{
			size	:	part_object.part_component_id_and_driver_id.length*16,
			usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.VERTEX 
		});
		
	this.draw_component=function(method_data,target_data,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,render_driver,render)	
	{
		if(method_data.method_id!=1)
			return;
		for(var i=0,ni=component_render_parameter.length;i<ni;i++){
			var buffer_id=component_render_parameter[i];
			var p=component_buffer_parameter[buffer_id];
			if(p.length<=0)
				continue;

			var display_info;
			while(p.length>0)
				display_info=p.shift();
				
			var my_texture_width	=part_object.material[0].texture_width;
			var my_texture_height	=part_object.material[0].texture_height;
			var my_canvas_width		=part_object.material[0].canvas_width;
			
			this.parameter[buffer_id]={
				display_width	:	render.webgpu.context_2d.measureText(display_info).width,
				start_time		:	(new Date()).getTime()
			}
				
			render.webgpu.canvas_2d.width	=my_texture_width;
			render.webgpu.canvas_2d.height	=my_texture_height;
			render.webgpu.context_2d.width	=my_texture_width;
			render.webgpu.context_2d.height	=my_texture_height;
			
			render.webgpu.context_2d.fillStyle="rgb(0,0,0)";
			render.webgpu.context_2d.fillRect(0,0,my_texture_width,my_texture_height);
	
			render.webgpu.context_2d.fillStyle		="rgb(255,255,255)";
			render.webgpu.context_2d.font			=part_object.material[0].font;
			render.webgpu.context_2d.textBaseline	="middle";
			render.webgpu.context_2d.textAlign		="left";
			render.webgpu.context_2d.fillText(display_info,0,my_texture_height/2);
					
			render.webgpu.device.queue.copyExternalImageToTexture(
				{
					source	:	render.webgpu.canvas_2d
				},
				{
					texture	:	this.texture[buffer_id]
				},
				{
					width	:	my_texture_width,
					height	:	my_texture_height
				});
		}
		
		var p	=part_object.buffer_object.face.region_data;
		var rpe	=render.webgpu.render_pass_encoder;

		rpe.setPipeline(render_driver.pipeline);
		for(var i=0,ni=component_render_parameter.length;i<ni;i++){
			var buffer_id=component_render_parameter[i];
			var par=this.parameter[buffer_id];
			render.set_system_bindgroup_by_part(
				target_data.render_buffer_id,
				method_data.method_id,
				part_object.render_id,
				part_object.part_id,
				buffer_id);
				
			var my_texture_width	=part_object.material[0].texture_width;
			var my_texture_height	=part_object.material[0].texture_height;
			var my_canvas_width		=part_object.material[0].canvas_width;
			var par=this.parameter[buffer_id];

			if(par.display_width<=my_canvas_width)
				render.webgpu.device.queue.writeBuffer(
					this.vertex_buffer,16*buffer_id,new Float32Array(
						[0,my_canvas_width/my_texture_width,0,1]));
			else{
				var time_diff=(new Date()).getTime()-par.start_time;
				var texture_diff=part_object.material[0].texture_speed*time_diff-1.0;
				
				render.webgpu.device.queue.writeBuffer(this.vertex_buffer,16*buffer_id,
					new Float32Array([
						texture_diff*my_canvas_width/my_texture_width,
						my_canvas_width/my_texture_width,
						0,1]));
			}
			rpe.setVertexBuffer(1,this.vertex_buffer,buffer_id*16);
			rpe.setBindGroup(1,this.texture_bindgroup[buffer_id]);
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
	var texture_bindgroup_layout_entries=[
		{	//texture
			binding		:	0,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//sampler
			binding		:	1,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			sampler		:
			{
				type	:	"filtering"
			}
		}
	];
	
	this.texture_bindgroup_layout=render.webgpu.device.createBindGroupLayout(
		{
			entries	:texture_bindgroup_layout_entries
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
						},
						{	//attribute
							format			:	"float32x4",
							offset			:	64,
							shaderLocation	:	2
						}
					]
				},
				{
					arrayStride	:	16,
					stepMode	:	"instance",
					attributes	:
					[
						{	//parameter
							format			:	"float32x4",
							offset			:	0,
							shaderLocation	:	3
						}
					]
				}
			]
		},
		fragment		:
		{
			module		:	my_module,
			entryPoint	:	"fragment_main",
			targets		: 
			[
				{
					format	:	render.webgpu.gpu.getPreferredCanvasFormat()
				},
				{
					format		:	"rgba32sint",
					writeMask	:	0
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
	
	this.pipeline=render.webgpu.device.createRenderPipeline(pipeline_descr);
	this.create_part_driver=my_create_part_driver;
	this.destroy=function()
	{
		this.pipeline=null;
	}
}