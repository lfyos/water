function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.box_component_id=-1;
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
		render.set_system_bindgroup(
			render_data.render_buffer_id,method_data.method_id,this.box_component_id,-1);

		var rpe	=render.webgpu.render_pass_encoder;
		rpe.setPipeline(render_driver.pipeline);
		rpe.setVertexBuffer(1,this.buffer);
		var p=part_object.buffer_object.edge.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		}
	};

	this.append_component_parameter=function(
				component_id,	driver_id,			render_id,		part_id,
				data_buffer_id,		buffer_data_item,	buffer_data_array,
				part_object,	part_driver,		render_driver,	render)
	{
		render.webgpu.device.queue.writeBuffer(this.buffer,0,new Float32Array(buffer_data_item[0]));
		this.box_component_id=buffer_data_item[1];
	};
};
