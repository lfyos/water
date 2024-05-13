function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.coodinate_buffer=null;
	this.coordinate_component_id=null;
	
	this.draw_component=function(method_data,render_data,
			render_id,part_id,component_id,driver_id,component_render_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		var rpe	=render.webgpu.render_pass_encoder;
		rpe.setPipeline(render_driver.pipeline);
		
		for(var i=0,ni=this.coordinate_component_id.length;i<ni;i++){
			render.system_buffer.set_system_bindgroup(render_data.render_buffer_id,
				(this.coordinate_component_id[i]<0)
					?(project_matrix.camera_component_id)
					:(this.coordinate_component_id[i]),
				-1,render);
			rpe.setVertexBuffer(1,this.coodinate_buffer,
					Float32Array.BYTES_PER_ELEMENT*4*i,Float32Array.BYTES_PER_ELEMENT*4);
			
			var p=part_object.buffer_object.edge.region_data;
			for(var j=0,nj=p.length;j<nj;j++){
				rpe.setVertexBuffer(0,p[j].buffer);
				rpe.draw(p[j].item_number);
			}
		}
	}
	this.append_component_parameter=function(
			component_id,		driver_id,		render_id,		part_id,
			buffer_data_item,	part_object,	part_driver,	render_driver,	render)
	{
		var buffer_data=new Array();
		this.coordinate_component_id=new Array();
		for(var i=0,ni=buffer_data_item.length;i<ni;i++){
			buffer_data.push(0,0,0,buffer_data_item[i][0]);
			this.coordinate_component_id.push(buffer_data_item[i][1]);
		}

		if(this.coodinate_buffer!=null)
			this.coodinate_buffer.destroy();
		this.coodinate_buffer=render.webgpu.device.createBuffer({
				size	:	Float32Array.BYTES_PER_ELEMENT*buffer_data.length,
				usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.VERTEX
			});
		render.webgpu.device.queue.writeBuffer(
			this.coodinate_buffer,0,new Float32Array(buffer_data));
	};
	this.destroy=function()
	{
		if(this.coodinate_buffer!=null){
			this.coodinate_buffer.destroy();
			this.coodinate_buffer=null;
		}
	}
};
