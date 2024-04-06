function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.parameter={
		display_width	:	1,
		start_time		:	0
	};
	this.texture=render.webgpu.device.createTexture(
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
			resource	:	this.texture.createView()
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
	this.texture_bindgroup=render.webgpu.device.createBindGroup(
		{
			layout		:	render_driver.texture_bindgroup_layout,
			entries		:	resource_entries
		});
		
	this.vertex_buffer=render.webgpu.device.createBuffer(
		{
			size	:	16,
			usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.VERTEX 
		});

	this.append_component_parameter=function(
			component_id,		driver_id,		render_id,		part_id,
			buffer_data_item,	part_object,	part_driver,	render_driver,	render)
	{
		var my_texture_width	=part_object.material[0].texture_width;
		var my_texture_height	=part_object.material[0].texture_height;
		var my_canvas_width		=part_object.material[0].canvas_width;
		
		this.parameter={
			display_width	:	render.webgpu.context_2d.measureText(buffer_data_item).width,
			start_time		:	(new Date()).getTime()
		}
					
		render.webgpu.canvas_2d.width	=my_texture_width;
		render.webgpu.canvas_2d.height	=my_texture_height;
	
		render.webgpu.context_2d.fillStyle="rgb(0,0,0)";
		render.webgpu.context_2d.fillRect(0,0,my_texture_width,my_texture_height);
		
		render.webgpu.context_2d.fillStyle		="rgb(255,255,255)";
		render.webgpu.context_2d.font			=part_object.material[0].font;
		render.webgpu.context_2d.textBaseline	="middle";
		render.webgpu.context_2d.textAlign		="left";
		render.webgpu.context_2d.fillText(buffer_data_item,0,my_texture_height/2);
						
		render.webgpu.device.queue.copyExternalImageToTexture(
			{
				source	:	render.webgpu.canvas_2d
			},
			{
				texture	:	this.texture
			},
			{
				width	:	my_texture_width,
				height	:	my_texture_height
			});
		render.webgpu.device.queue.writeBuffer(this.vertex_buffer,0,
				new Float32Array([0,my_canvas_width/my_texture_width,0,1]));
	};	
	
	this.draw_component=function(method_data,render_data,
			render_id,part_id,component_id,driver_id,component_render_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		var my_texture_width	=part_object.material[0].texture_width;
		var my_canvas_width		=part_object.material[0].canvas_width;
		if(this.parameter.display_width>my_canvas_width){
			var time_diff=(new Date()).getTime()-this.parameter.start_time;
			var texture_diff=part_object.material[0].texture_speed*time_diff-1.0;	
			render.webgpu.device.queue.writeBuffer(this.vertex_buffer,0,
				new Float32Array([
					texture_diff*my_canvas_width/my_texture_width,
					my_canvas_width/my_texture_width,0,1]));
		}
		var rpe	=render.webgpu.render_pass_encoder;
		rpe.setPipeline(render_driver.pipeline);
		rpe.setVertexBuffer(1,this.vertex_buffer);
		rpe.setBindGroup(1,this.texture_bindgroup);
		
		var p=part_object.buffer_object.face.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		}
	};
	
	this.destroy=function()
	{
		this.parameter=null;
		this.texture_bindgroup=null;
		
		if(this.texture!=null){
			this.texture.destroy();
			this.texture=null;
		}
		if(this.vertex_buffer!=null){
			this.vertex_buffer.destroy();
			this.vertex_buffer=null;
		}
		
		this.draw_component=null;
		this.append_component_parameter=null;
	}
};
