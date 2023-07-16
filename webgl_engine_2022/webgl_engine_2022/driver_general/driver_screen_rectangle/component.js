function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	if(typeof(this.ep=render.component_event_processor[component_id])!="object"){
		this.ep={data:[0,0,0.5,0.5]};
		render.component_event_processor[component_id]=this.ep;
	}else if(!(Array.isArray(this.ep.data)))
		this.ep.data=[0,0,0.5,0.5];
	
	this.buffer=this.method_buffer	=render.webgpu.device.createBuffer(
		{
			size	:	Float32Array.BYTES_PER_ELEMENT*4,
			usage	:	GPUBufferUsage.VERTEX|GPUBufferUsage.COPY_DST
		});
	this.draw_component=function(method_data,render_data,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		if(method_data.method_id!=1)
			return;
		
		render.webgpu.device.queue.writeBuffer(this.buffer,0,new Float32Array(this.ep.data));
		
		var rpe	=render.webgpu.render_pass_encoder;
		rpe.setPipeline(render_driver.pipeline);
		rpe.setVertexBuffer(1,this.buffer);
		
		var p=part_object.buffer_object.edge.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		}
	}
};
