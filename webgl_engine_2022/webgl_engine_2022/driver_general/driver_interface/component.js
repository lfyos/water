function create_bind_group(request_url,render_driver,render)
{
	this.is_busy_flag		=true;
	this.should_delete_flag	=false;
	
	this.create=async function(request_url,render_driver,render)
	{
		if(typeof(request_url)!="string"){
			this.texture 		=	render.webgpu.device.createTexture(
				{
					size:
					{
						width	:	request_url.canvas_width,
						height	:	request_url.canvas_height
					},
					format		:	"rgba16float",
					usage		:	 GPUTextureUsage.TEXTURE_BINDING 
									|GPUTextureUsage.COPY_DST
									|GPUTextureUsage.RENDER_ATTACHMENT
		    	});
		}else{
			var my_response 	=	await fetch(request_url);
		   	var my_imageBitmap	=	await createImageBitmap(await my_response.blob());
		    
			this.texture 		=	render.webgpu.device.createTexture(
				{
					size:
					{
						width	:	my_imageBitmap.width,
						height	:	my_imageBitmap.height
					},
					format		:	"rgba16float",
					usage		:	 GPUTextureUsage.TEXTURE_BINDING 
									|GPUTextureUsage.COPY_DST
									|GPUTextureUsage.RENDER_ATTACHMENT
		    	});
			render.webgpu.device.queue.copyExternalImageToTexture(
				{
					source	:	my_imageBitmap
				},
				{
					texture	:	this.texture
				},
				{
					width	:	my_imageBitmap.width,
					height	:	my_imageBitmap.height
				});
		}	
		this.buffer_size=Float32Array.BYTES_PER_ELEMENT*64;
		this.buffer=render.webgpu.device.createBuffer(
		{
			size	:	this.buffer_size,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
	
		var resource_entries=[
			{	// system buffer
				binding		:	0,
				resource	:
				{
					buffer	:	this.buffer,
					size	:	this.buffer_size
				}
			},
			{	//texture
				binding		:	1,
				resource	:	this.texture.createView()
			},
			{
				//sampler
				binding		:	2,
				resource	:	render.webgpu.device.createSampler(
					{
						addressModeU	:	"mirror-repeat",
						addressModeV	:	"mirror-repeat",
						magFilter		:	"linear",
						minFilter		:	"linear",
						mipmapFilter	:	"linear"
					})
			}
		];
		this.bindgroup=render.webgpu.device.createBindGroup(
			{
				layout		:	render_driver.bindgroup_layout,
				entries		:	resource_entries
			});
		this.is_busy_flag=false;
		if(this.should_delete_flag)
			this.destroy();
	};
	
	this.destroy=function ()
	{
		this.should_delete_flag=true;
		if(this.is_busy_flag)	
			return;

		if(this.buffer!=null){
			this.buffer.destroy();
			this.buffer=null;
		}
		if(this.texture!=null){
			this.texture.destroy();
			this.texture=null;
		}
	};

	this.create(request_url,render_driver,render);
};

function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.init_data			=init_data;
	this.image_bind_group	=new create_bind_group(this.init_data,render_driver,render);
	this.data				=[0,0,0,0,	0,	0,0,0,0];
	
	this.draw_component=function(method_data,render_data,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		if(method_data.method_id!=1)
			return;
		if(this.image_bind_group.is_busy_flag)
			return;
		render.webgpu.device.queue.writeBuffer(
			this.image_bind_group.buffer,0,new Float32Array(this.data));

		var rpe=render.webgpu.render_pass_encoder;
		rpe.setPipeline(render_driver.pipeline);		
		render.webgpu.render_pass_encoder.setBindGroup(1,this.image_bind_group.bindgroup);

		var p=part_object.buffer_object.face.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		}
	};
	this.append_component_parameter=function(
		component_id,	driver_id,			render_id,		part_id,
		buffer_id,		buffer_data_item,
		part_object,	part_driver,		render_driver,	render)
	{
		this.data=buffer_data_item;
	}
};
