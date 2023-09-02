function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.display_parameter=null;
	
	this.draw=function(render,part_driver,my_region_data,my_pipeline)
	{
		var my_material=part_driver.material_bindgroup_array;
		var my_material_number=my_material.length-1;
		var selected_flag=this.display_parameter.effective_selected_flag;
	
		var rpe=render.webgpu.render_pass_encoder;	
		rpe.setStencilReference(0);
		rpe.setPipeline(my_pipeline);
		
		for(var i=0,ni=my_region_data.length;i<ni;i++){
			var my_material_id=selected_flag?my_material_number:
					(my_material_number<=0)?0:
					(my_region_data[i].material_id%my_material_number);
			if((my_material_id>=0)&&(my_material_id<my_material.length)){
				rpe.setBindGroup(1,my_material[my_material_id].bindgroup);
				rpe.setVertexBuffer(0,my_region_data[i].buffer);
				rpe.draw(my_region_data[i].item_number);
			}
		}
	};
	this.draw_component=function(method_data,render_data,
			render_id,part_id,component_id,driver_id,component_render_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		if(part_driver.material_bindgroup_flag)
			return;
		var display_value_id=this.display_parameter.display_value_id;
		display_value_id=(display_value_id<=0)?0:Math.floor(1+(display_value_id-1)/4);
		display_value_id=(display_value_id>=render_driver.pipeline_array.length)?0:display_value_id;
		var my_pipeline	=render_driver.pipeline_array[display_value_id];
		var display_bitmap	=component_render_parameter;
		
		switch(method_data.method_id){
		default:
			break;
		case 0:
			if((display_bitmap&1)!=0){
				var p=part_object.buffer_object.face.region_data;
				if(this.display_parameter.close_clip_plane_number<=0)
					this.draw(render,part_driver,p,my_pipeline.id_face_pipeline_no_clip);
				else{
					this.draw(render,part_driver,p,my_pipeline.id_face_pipeline_do_clip);
					this.draw(render,part_driver,p,my_pipeline.id_face_pipeline_do_close);
				}
				var p=part_object.buffer_object.point.region_data;
				this.draw(render,part_driver,p,my_pipeline.id_point_pipeline);
			}
			break;
		case 1:
			if((display_bitmap&2)!=0){
				var p=part_object.buffer_object.face.region_data;
				if(this.display_parameter.close_clip_plane_number<=0)
					this.draw(render,part_driver,p,my_pipeline.value_face_pipeline_no_clip);
				else{
					this.draw(render,part_driver,p,my_pipeline.value_face_pipeline_do_clip);
					this.draw(render,part_driver,p,my_pipeline.value_face_pipeline_do_close);
				}
				var p=part_object.buffer_object.point.region_data;
				this.draw(render,part_driver,p,my_pipeline.value_point_pipeline);
			}
			break;
		case 2:
			break;
		case 3:
			if((display_bitmap&4)!=0){
				var p=part_object.buffer_object.face.region_data;
				if(this.display_parameter.close_clip_plane_number<=0)
					this.draw(render,part_driver,p,my_pipeline.depth_face_pipeline_no_clip);
				else{
					this.draw(render,part_driver,p,my_pipeline.depth_face_pipeline_do_clip);
					this.draw(render,part_driver,p,my_pipeline.depth_face_pipeline_do_close);
				}
			}
			break;
		case 4:
			if((display_bitmap&8)!=0){
				var p=part_object.buffer_object.edge.region_data;
				this.draw(render,part_driver,p,my_pipeline.color_edge_pipeline);
			}
			if((display_bitmap&16)!=0){
				var p=part_object.buffer_object.frame.region_data;
				this.draw(render,part_driver,p,my_pipeline.color_frame_pipeline);
			}
			if((display_bitmap&32)!=0){
				var p=part_object.buffer_object.point.region_data;
				this.draw(render,part_driver,p,my_pipeline.color_normal_point_pipeline);
			}
			break;
		case 5:
			if((display_bitmap&64)!=0){
				var p=part_object.buffer_object.face.region_data;
				if(this.display_parameter.close_clip_plane_number<=0)
					this.draw(render,part_driver,p,my_pipeline.color_face_pipeline_no_clip);
				else{
					this.draw(render,part_driver,p,my_pipeline.color_face_pipeline_do_clip);
					this.draw(render,part_driver,p,my_pipeline.color_face_pipeline_do_close);
				}
				if((render.pickup.component_id==component_id)&&(render.pickup.driver_id==driver_id)){
					var p=part_object.buffer_object.point.region_data;
					this.draw(render,part_driver,p,my_pipeline.color_pickup_point_pipeline);
				}
			}
			break;
		}
		return;
	};
	this.append_component_parameter=function(
			component_id,		driver_id,		render_id,		part_id,
			buffer_data_item,	part_object,	part_driver,	render_driver,	render)
	{
		this.display_parameter=
		{
			transparency_value		:	 buffer_data_item[0],
			close_clip_plane_number	:	 buffer_data_item[1],
			display_value_id		:	 buffer_data_item[2],
			effective_selected_flag	:	(buffer_data_item[3]>0)?true:false
		}
		render.system_buffer.set_system_bindgroup_data(
			[
				this.display_parameter.transparency_value,
				this.display_parameter.close_clip_plane_number,
				this.display_parameter.display_value_id,
				this.display_parameter.effective_selected_flag?1:0
			],
			component_id,driver_id,render)
		
	};
	this.destroy=function(render)
	{
		this.display_parameter			=null;
		this.draw_component				=null;
		this.append_component_parameter	=null;
		
		if(render.component_event_processor[this.component_id]!=null){
			if(typeof(render.component_event_processor[this.component_id].destroy)=="function")
				render.component_event_processor[this.component_id].destroy(render);
			render.component_event_processor[this.component_id]=null;
		}
	};
};
