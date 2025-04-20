function construct_component_driver(component_ids,init_data,part_object,part_driver,render_driver,scene)
{
	this.component_ids	=component_ids;
	
	this.box_component_id=-1;
	this.buffer=scene.webgpu.device.createBuffer(
		{
			size	:	Float32Array.BYTES_PER_ELEMENT*8,
			usage	:	GPUBufferUsage.VERTEX|GPUBufferUsage.COPY_DST
		});
	this.draw_component=function(method_data,render_parameter,
			project_matrix,target_data,part_object,part_driver,render_driver,scene)	
	{
		scene.system_buffer.set_system_bindgroup(
			target_data.render_buffer_id,method_data.method_id,
			this.box_component_id,-1,scene);

		var rpe	=scene.webgpu.render_pass_encoder;
		rpe.setPipeline(render_driver.pipeline);
		rpe.setVertexBuffer(1,this.buffer);
		var p=part_object.buffer_object.edge.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		}
	};
	
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)  
	{
		scene.webgpu.device.queue.writeBuffer(this.buffer,0,new Float32Array(buffer_data_item[0]));
		this.box_component_id=buffer_data_item[1];
	};
	this.destroy=function()
	{
		if(this.buffer!=null){
			this.buffer.destroy();
			this.buffer=null;
		}
	}
};
