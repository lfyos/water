function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.draw_component=function(method_data,render_data,
			render_id,part_id,component_id,driver_id,component_render_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		var p,rpe	=render.webgpu.render_pass_encoder;
		switch(method_data.method_id){
		default:
			return;
		case 0:
			p=part_object.buffer_object.face.region_data;
			rpe.setPipeline(render_driver.id_face_pipeline);
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			}
			p=part_object.buffer_object.point.region_data;
			rpe.setPipeline(render_driver.id_point_pipeline);
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			}
			break;
		case 1:
			p=part_object.buffer_object.face.region_data;
			rpe.setPipeline(render_driver.value_face_pipeline);
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			}
			p=part_object.buffer_object.point.region_data;
			rpe.setPipeline(render_driver.value_point_pipeline);
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			}
			break;
		case 2:
			p=part_object.buffer_object.face.region_data;
			rpe.setPipeline(render_driver.color_face_pipeline);
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			}
			
			p=part_object.buffer_object.edge.region_data;
			rpe.setPipeline(render_driver.color_edge_pipeline);
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			}
			if(render.pickup.component_id==component_id)
				if(render.pickup.driver_id==driver_id){
					p=part_object.buffer_object.point.region_data;
					rpe.setPipeline(render_driver.color_pickup_point_pipeline);
					for(var i=0,ni=p.length;i<ni;i++){
						rpe.setVertexBuffer(0,p[i].buffer);
						rpe.draw(p[i].item_number);
					}
			}
			break;
		}
		return;
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
		
		if(render.component_event_processor[this.component_id]!=null){
			if(typeof(render.component_event_processor[this.component_id].destroy)=="function")
				render.component_event_processor[this.component_id].destroy(render);
			render.component_event_processor[this.component_id]=null;
		}
	}
};
