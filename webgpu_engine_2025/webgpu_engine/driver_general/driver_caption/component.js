function construct_component_driver(component_ids,init_data,create_data,part_object,part_driver,render_driver,scene)
{
	this.component_ids=component_ids;
	
	this.parameter={
		display_width	:	1,
		start_time		:	0
	};
	this.texture=scene.webgpu.device.createTexture(
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
	this.sampler=scene.webgpu.device.createSampler(
		 {
		 	addressModeU	:	"repeat",
		 	addressModeV	:	"repeat",
		 	magFilter		:	"linear",
		 	minFilter		:	"linear",
		 	mipmapFilter	:	"linear"
		 });

	this.texture_bindgroup=scene.webgpu.device.createBindGroup(
		{
			layout		:	render_driver.texture_bindgroup_layout,
			entries		:	[
				{	//texture
					binding		:	0,
					resource	:	this.texture.createView()
				},
				{
					//sampler
					binding		:	1,
					resource	:	this.sampler
				}
			]
		});
		
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)  
	{
		var my_texture_width		=part_object.material[0].texture_width;
		var my_texture_height		=part_object.material[0].texture_height;
		var my_texture_width_scale	=part_object.material[0].texture_width_scale;

		scene.webgpu.canvas_2d.width	=my_texture_width;
		scene.webgpu.canvas_2d.height	=my_texture_height;
	
		scene.webgpu.context_2d.fillStyle="rgb(0,0,0)";
		scene.webgpu.context_2d.fillRect(0,0,my_texture_width,my_texture_height);
		
		scene.webgpu.context_2d.fillStyle		="rgb(255,255,255)";
		scene.webgpu.context_2d.font			=part_object.material[0].font;
		scene.webgpu.context_2d.textBaseline	="middle";
		scene.webgpu.context_2d.textAlign		="left";
		scene.webgpu.context_2d.fillText(buffer_data_item,0,my_texture_height/2);
		
		this.parameter={
			display_width	:	scene.webgpu.context_2d.measureText(buffer_data_item).width,
			start_time		:	(new Date()).getTime()
		}
						
		scene.webgpu.device.queue.copyExternalImageToTexture(
			{
				source	:	scene.webgpu.canvas_2d
			},
			{
				texture	:	this.texture
			},
			{
				width	:	my_texture_width,
				height	:	my_texture_height
			});

		scene.system_buffer.set_id_information_data([0,my_texture_width_scale,0,1],
				this.component_ids.component_id,this.component_ids.driver_id);
	};
	
	this.draw_component=function(method_data,render_parameter,
			target_data,part_object,part_driver,render_driver,scene)
	{
		var my_texture_width		=part_object.material[0].texture_width;
		var my_texture_width_scale	=part_object.material[0].texture_width_scale;
		var my_texture_speed		=part_object.material[0].texture_speed;
		if(this.parameter.display_width>(my_texture_width*my_texture_width_scale)){
			var time_diff=(new Date()).getTime()-this.parameter.start_time;
			var texture_x_shift=my_texture_speed*time_diff-1.0;
			scene.system_buffer.set_id_information_data([texture_x_shift],
				this.component_ids.component_id,this.component_ids.driver_id);
		}
		var rpe	=scene.webgpu.render_pass_encoder;
		rpe.setPipeline(render_driver.pipeline);
		rpe.setBindGroup(1,this.texture_bindgroup);
		
		var p=part_object.buffer_object.face.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		}
	};
	
	this.destroy=function()
	{
		if(this.texture!=null){
			this.texture.destroy();
			this.texture=null;
		}
		if(this.sampler!=null){
			this.sampler=null;
		}
	}
};
