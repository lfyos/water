function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.draw_component=function(method_data,render_data,
			render_id,part_id,component_id,driver_id,component_render_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		var rpe	=render.webgpu.render_pass_encoder;
		switch(method_data.method_id){
		default:
			return;
		case 0:
			rpe.setPipeline(render_driver.id_pipeline);
			break;
		case 1:
			rpe.setPipeline(render_driver.value_pipeline);
			break;
		case 2:
			rpe.setPipeline(render_driver.render_pipeline);
			break;
		}
		var p=part_object.buffer_object.face.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		}
	}
	this.append_component_parameter=function(
			component_id,		driver_id,		render_id,		part_id,
			buffer_data_item,	part_object,	part_driver,	render_driver,	render)
	{
	};
	this.destroy=function(render)
	{
		this.draw_component				=null;
		this.append_component_parameter	=null;
	}
};
