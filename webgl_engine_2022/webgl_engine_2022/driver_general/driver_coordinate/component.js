function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.draw_component=function(method_data,render_data,
			render_id,part_id,data_buffer_id,component_id,driver_id,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		if(method_data.method_id!=1)
			return;
		var rpe	=render.webgpu.render_pass_encoder;
		rpe.setPipeline(render_driver.pipeline);
		render.set_system_bindgroup(render_data.render_buffer_id,
			method_data.method_id,project_matrix.camera_component_id,-1);
		var p=part_object.buffer_object.edge.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		}
	}
};
