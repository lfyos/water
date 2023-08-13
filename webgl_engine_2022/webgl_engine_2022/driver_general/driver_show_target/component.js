function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	var ep=render.component_event_processor[component_id];
	if(typeof(ep)!="object"){
		render.component_event_processor[component_id]=new Object();
		ep=render.component_event_processor[component_id];
	}
	
	ep.bindgroup_layout=render_driver.bindgroup_layout;
	ep.bindgroup=null;
	
	ep.set_target=function(my_texture)
	{
		var resource_entries=[
			{	//texture
				binding		:	0,
				resource	:	my_texture.createView()
			},
			{
				//sampler
				binding		:	1,
				resource	:	this.device.createSampler(
					{
						addressModeU	:	"mirror-repeat",
						addressModeV	:	"mirror-repeat",
						magFilter		:	"linear",
						minFilter		:	"linear",
						mipmapFilter	:	"linear"
					})
			}
		];
		this.bindgroup=this.device.createBindGroup(
			{
				layout		:	this.bindgroup_layout,
				entries		:	resource_entries
			});
	};
	
	this.component_id=component_id;
	this.buffer=this.method_buffer	=render.webgpu.device.createBuffer(
		{
			size	:	Float32Array.BYTES_PER_ELEMENT*8,
			usage	:	GPUBufferUsage.VERTEX|GPUBufferUsage.COPY_DST
		});
	
	this.draw_component=function(method_data,render_data,
			render_id,part_id,data_buffer_id,component_id,driver_id,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		var ep=render.component_event_processor[this.component_id];
		if(ep.bindgroup==null)
			return;	
			
		var rpe=render.webgpu.render_pass_encoder;
		rpe.setPipeline(render_driver.pipeline);
		rpe.setBindGroup(1,ep.bindgroup);
		rpe.setVertexBuffer(1,this.buffer);
		var p=part_object.buffer_object.face.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		};
	};
	
	this.append_component_parameter=function(
				component_id,	driver_id,			render_id,			part_id,
				data_buffer_id,	buffer_data_item,	buffer_data_array,
				part_object,	part_driver,		render_driver,		render)
	{
		render.webgpu.device.queue.writeBuffer(this.buffer,0,new Float32Array(buffer_data_item));
	};
};
