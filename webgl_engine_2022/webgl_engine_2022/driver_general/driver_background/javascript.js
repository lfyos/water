async function download_external_texture(request_url,render)
{
	var my_response 	=	await fetch(request_url);
    var my_imageBitmap	=	await createImageBitmap(await my_response.blob());
    
	var my_texture 		=	render.webgpu.device.createTexture(
			{
				size:
				{
					width	:	my_imageBitmap.width,
					height	:	my_imageBitmap.height
				},
				format		:	"rgba8unorm",
				usage		:	 GPUTextureUsage.TEXTURE_BINDING 
								|GPUTextureUsage.COPY_DST
								|GPUTextureUsage.RENDER_ATTACHMENT
	    	});
	render.webgpu.device.queue.copyExternalImageToTexture(
		{
			source	:	my_imageBitmap
		},
		{
			texture	:	my_texture
		},
		{
			width	:	my_imageBitmap.width,
			height	:	my_imageBitmap.height
		});
	return my_texture;
}
async function create_texture_bind_group(my_directory_name,texture_bind_group_object,part_object,render_driver,render)
{
	texture_bind_group_object.left_texture=await download_external_texture(
				render.caller.create_part_request_string(part_object.render_id,part_object.part_id,
					[["file",my_directory_name+"/left.jpg"]]),render);
	texture_bind_group_object.right_texture=await download_external_texture(
				render.caller.create_part_request_string(part_object.render_id,part_object.part_id,
					[["file",my_directory_name+"/right.jpg"]]),render);

	texture_bind_group_object.top_texture=await download_external_texture(
				render.caller.create_part_request_string(part_object.render_id,part_object.part_id,
					[["file",my_directory_name+"/top.jpg"]]),render);
	texture_bind_group_object.down_texture=await download_external_texture(
				render.caller.create_part_request_string(part_object.render_id,part_object.part_id,
					[["file",my_directory_name+"/down.jpg"]]),render);
	
	texture_bind_group_object.front_texture=await download_external_texture(
				render.caller.create_part_request_string(part_object.render_id,part_object.part_id,
					[["file",my_directory_name+"/front.jpg"]]),render);
	texture_bind_group_object.back_texture=await download_external_texture(
				render.caller.create_part_request_string(part_object.render_id,part_object.part_id,
				[["file",my_directory_name+"/back.jpg"]]),render);
	
	texture_bind_group_object.no_box_texture=await download_external_texture(
				render.caller.create_part_request_string(part_object.render_id,part_object.part_id,
				[["file",my_directory_name+"/no_box.jpg"]]),render);

	var resource_entries=[
		{	//left
			binding		:	0,
			resource	:	texture_bind_group_object.left_texture.createView()
		},
		{	//right
			binding		:	1,
			resource	:	texture_bind_group_object.right_texture.createView()
		},
		{	//top
			binding		:	2,
			resource	:	texture_bind_group_object.top_texture.createView()
		},
		{	//down
			binding		:	3,
			resource	:	texture_bind_group_object.down_texture.createView()
		},
		{	//front
			binding		:	4,
			resource	:	texture_bind_group_object.front_texture.createView()
		},
		{	//back
			binding		:	5,
			resource	:	texture_bind_group_object.back_texture.createView()
		},
		{	//no_box
			binding		:	6,
			resource	:	texture_bind_group_object.no_box_texture.createView()
		},
		{
			//sampler
			binding		:	7,
			resource	:	render.webgpu.device.createSampler(
				{
					addressModeU	:	"mirror-repeat",
					addressModeV	:	"mirror-repeat",
					magFilter		:	"nearest",
					minFilter		:	"nearest",
					mipmapFilter	:	"nearest"
				})
		}
	];
	
	texture_bind_group_object.texture_bindgroup=render.webgpu.device.createBindGroup(
		{
			layout		:	render_driver.texture_bindgroup_layout,
			entries		:	resource_entries
		});
}

function destroy_texture_bind_group(texture_bind_group_object)
{
	if(texture_bind_group_object.left_texture!=null){
		texture_bind_group_object.left_texture.destroy();
		texture_bind_group_object.left_texture=null;
	}
	if(texture_bind_group_object.right_texture!=null){
		texture_bind_group_object.right_texture.destroy();
		texture_bind_group_object.right_texture=null;
	}
	if(texture_bind_group_object.top_texture!=null){
		texture_bind_group_object.top_texture.destroy();
		texture_bind_group_object.top_texture=null;
	}
	if(texture_bind_group_object.down_texture!=null){
		texture_bind_group_object.down_texture.destroy();
		texture_bind_group_object.down_texture=null;
	}
	if(texture_bind_group_object.front_texture!=null){
		texture_bind_group_object.front_texture.destroy();
		texture_bind_group_object.front_texture=null;
	}
	if(texture_bind_group_object.back_texture!=null){
		texture_bind_group_object.back_texture.destroy();
		texture_bind_group_object.back_texture=null;
	}
	if(texture_bind_group_object.no_box_texture!=null){
		texture_bind_group_object.no_box_texture.destroy();
		texture_bind_group_object.no_box_texture=null;
	}
	if(texture_bind_group_object.texture_bindgroup!=null)
		texture_bind_group_object.texture_bindgroup=null;
}

function create_texture_bind_group_object()
{
	this.left_texture			=null;
	this.right_texture			=null;
	this.top_texture			=null;
	this.down_texture			=null;
	this.front_texture			=null;
	this.back_texture			=null;
	this.no_box_texture			=null;
	this.texture_bindgroup		=null;

	this.mode					=-1;
	this.directory_name			=null;
}

function my_create_part_driver(part_object,render_driver,render)
{
	this.texture_bind_group_array=new Array();
	
	this.draw_component=function(method_data,target_data,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,render_driver,render)	
	{	
		if(method_data.method_id==0)
			return;
			
		for(var i=0,ni=component_render_parameter.length;i<ni;i++){
			var buffer_id=component_render_parameter[i];
			while(buffer_id>=this.texture_bind_group_array.length)
				this.texture_bind_group_array.push(new create_texture_bind_group_object());

			var p=component_buffer_parameter[buffer_id];
			while(p.length>0){
				this.texture_bind_group_array[buffer_id].mode			=p[0][0];
				this.texture_bind_group_array[buffer_id].directory_name	=p[0][1];
				p.shift();
			}
			var my_directory_name;
			if((my_directory_name=this.texture_bind_group_array[buffer_id].directory_name)!=null){
				this.texture_bind_group_array[buffer_id].directory_name=null;
				destroy_texture_bind_group(this.texture_bind_group_array[buffer_id]);
				create_texture_bind_group(my_directory_name,
					this.texture_bind_group_array[buffer_id],part_object,render_driver,render);
			}
		}

		var p	=part_object.buffer_object.face.region_data;
		var rpe	=render.webgpu.render_pass_encoder;

		for(var i=0,ni=component_render_parameter.length;i<ni;i++){
			var buffer_id=component_render_parameter[i];
			if(this.texture_bind_group_array[buffer_id].texture_bindgroup==null)
				continue;
			if(this.texture_bind_group_array[buffer_id].mode>0)		
				rpe.setPipeline(render_driver.box_pipeline);
			else if(this.texture_bind_group_array[buffer_id].mode==0)
				rpe.setPipeline(render_driver.no_box_pipeline);
			else
				continue;

			render.webgpu.render_pass_encoder.setBindGroup(1,
					this.texture_bind_group_array[buffer_id].texture_bindgroup);
					
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

function create_texture_bindgroup_layout(render)
{
	var texture_bindgroup_layout_entries=[
		{	//left
			binding		:	0,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//right
			binding		:	1,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//top
			binding		:	2,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//down
			binding		:	3,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//front
			binding		:	4,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//back
			binding		:	5,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//no box
			binding		:	6,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			texture		:
			{
				sampleType		:	"float",
    			viewDimension	:	"2d",
   				multisampled	:	false
			}
		},
		{	//sampler
			binding		:	7,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			sampler		:
			{
				type	:	"filtering"
			}
		}
	];
	
	return render.webgpu.device.createBindGroupLayout(
				{
					entries	:texture_bindgroup_layout_entries
				});
}

function main(	render_id,		render_name,
				init_data,		text_array,
				shader_code,	render)
{
	this.texture_bindgroup_layout=create_texture_bindgroup_layout(render);
	
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
			module	:	my_module,
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
		    
		fragment	:
		{
			module		:	my_module,
			
			entryPoint	:	"fragment_main",
			targets	: 
			[
				{
					format		:	"rgba32float"
				},
				{
					format		:	"rgba32sint",
					writeMask	:	0
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
    		depthCompare		:	 "less-equal",

   			stencilFront		:	{},
    		stencilBack			:	{},

 			stencilReadMask		:	0xFFFFFFFF,
			stencilWriteMask	:	0xFFFFFFFF,
			
			depthBias			:	0,
    		depthBiasSlopeScale	:	0,
    		depthBiasClamp		:	0
		}
	};

	pipeline_descr.fragment.constants={no_box_mode:true};
	this.no_box_pipeline = render.webgpu.device.createRenderPipeline(pipeline_descr);
	
	pipeline_descr.fragment.constants={no_box_mode:false};
	this.box_pipeline = render.webgpu.device.createRenderPipeline(pipeline_descr);

	this.create_part_driver=my_create_part_driver;
	
	this.destroy=function()
	{
		this.pipeline=null;
	}
}
