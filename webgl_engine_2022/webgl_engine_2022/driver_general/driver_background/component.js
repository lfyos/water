async function download_external_texture(render_id,part_id,file_name,render)
{
    var my_imageBitmap=await createImageBitmap(
			await render.caller.call_server_part(
					render_id,part_id,[["file",file_name]],"blob"));
	var my_texture=render.webgpu.device.createTexture(
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
			texture	:	my_texture
		},
		{
			width	:	my_imageBitmap.width,
			height	:	my_imageBitmap.height
		});
	return my_texture;
}

function create_texture_bind_group()
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
	
	this.is_busy_flag			=false;
	this.should_delete_flag		=false;

	this.destroy=function ()
	{
		if(this.is_busy_flag){
			this.should_delete_flag=true;
			return;
		}
		if(this.left_texture!=null){
			this.left_texture.destroy();
			this.left_texture=null;
		}
		if(this.right_texture!=null){
			this.right_texture.destroy();
			this.right_texture=null;
		}
		if(this.top_texture!=null){
			this.top_texture.destroy();
			this.top_texture=null;
		}
		if(this.down_texture!=null){
			this.down_texture.destroy();
			this.down_texture=null;
		}
		if(this.front_texture!=null){
			this.front_texture.destroy();
			this.front_texture=null;
		}
		if(this.back_texture!=null){
			this.back_texture.destroy();
			this.back_texture=null;
		}
		if(this.no_box_texture!=null){
			this.no_box_texture.destroy();
			this.no_box_texture=null;
		}
		if(this.texture_bindgroup!=null)
			this.texture_bindgroup=null;
	};
	this.create=async function (
		my_directory_name,part_object,render_driver,render)
	{
		var render_id	=part_object.render_id;
		var part_id		=part_object.part_id;
		this.is_busy_flag=true;
		
		this.left_texture=await download_external_texture(
				render_id,part_id,my_directory_name+"/left.jpg",render);
		this.right_texture=await download_external_texture(
				render_id,part_id,my_directory_name+"/right.jpg",render);
		this.top_texture=await download_external_texture(
				render_id,part_id,my_directory_name+"/top.jpg",render);
		this.down_texture=await download_external_texture(
				render_id,part_id,my_directory_name+"/down.jpg",render);
		this.front_texture=await download_external_texture(
				render_id,part_id,my_directory_name+"/front.jpg",render);
		this.back_texture=await download_external_texture(
				render_id,part_id,my_directory_name+"/back.jpg",render);
		this.no_box_texture=await download_external_texture(
				render_id,part_id,my_directory_name+"/no_box.jpg",render);

		var resource_entries=[
			{	//left
				binding		:	0,
				resource	:	this.left_texture.createView()
			},
			{	//right
				binding		:	1,
				resource	:	this.right_texture.createView()
			},
			{	//top
				binding		:	2,
				resource	:	this.top_texture.createView()
			},
			{	//down
				binding		:	3,
				resource	:	this.down_texture.createView()
			},
			{	//front
				binding		:	4,
				resource	:	this.front_texture.createView()
			},
			{	//back
				binding		:	5,
				resource	:	this.back_texture.createView()
			},
			{	//no_box
				binding		:	6,
				resource	:	this.no_box_texture.createView()
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
		
		this.texture_bindgroup=render.webgpu.device.createBindGroup(
			{
				layout		:	render_driver.texture_bindgroup_layout,
				entries		:	resource_entries
			});
		
		this.is_busy_flag=false;
		if(this.should_delete_flag)
			this.destroy();
		this.should_delete_flag=false;
	};
};

function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.texture_bind_group=new create_texture_bind_group();
	
	this.draw_component=function(method_data,render_data,
			render_id,part_id,data_buffer_id,component_id,driver_id,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		if(this.texture_bind_group.is_busy_flag)
			return;
			
		var mode,rpe=render.webgpu.render_pass_encoder;
		
		if((mode=component_buffer_parameter[component_buffer_parameter.length-1][0])>0)	
			rpe.setPipeline(render_driver.box_pipeline);
		else if(mode==0)
			rpe.setPipeline(render_driver.no_box_pipeline);
		else
			return;

		render.webgpu.render_pass_encoder.setBindGroup(1,this.texture_bind_group.texture_bindgroup);

		var p=part_object.buffer_object.face.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		}
	};

	this.append_component_parameter=function(
		component_id,	driver_id,			render_id,		part_id,
		data_buffer_id,	buffer_data_item,	buffer_data_array,
		part_object,	part_driver,		render_driver,	render)
	{
		this.texture_bind_group.destroy();
		this.texture_bind_group=new create_texture_bind_group();
		this.texture_bind_group.create(buffer_data_item[1],part_object,render_driver,render);
	}
	this.destroy=function()
	{
		if(this.texture_bind_group!=null){
			this.texture_bind_group.destroy();
			this.texture_bind_group=null;
		};
		this.append_component_parameter=null;
		this.draw_component=null;
	}
};
