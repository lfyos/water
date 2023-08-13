function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	if(typeof(this.ep=render.component_event_processor[component_id])!="object"){
		render.component_event_processor[component_id]={};
		this.ep=render.component_event_processor[component_id];
	}
	this.ep.data=[0,0,0,0];
	this.ep.data_bak=[-10000000,-10000000,10000000,10000000];
	
	this.buffer=this.method_buffer	=render.webgpu.device.createBuffer(
		{
			size	:	Float32Array.BYTES_PER_ELEMENT*4,
			usage	:	GPUBufferUsage.VERTEX|GPUBufferUsage.COPY_DST
		});
	this.draw_component=function(method_data,render_data,
			render_id,part_id,data_buffer_id,component_id,driver_id,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		var diff=render.computer.min_value;
		diff-=Math.abs(this.ep.data[2]-this.ep.data[0]);
		diff-=Math.abs(this.ep.data[3]-this.ep.data[1]);
		if(diff>=0)
			return;
		for(var i=0,ni=this.ep.data.length;i<ni;i++)
			if(this.ep.data[i]!=this.ep.data_bak[i]){
				this.ep.data_bak=this.ep.data;
				render.webgpu.device.queue.writeBuffer(this.buffer,0,new Float32Array(this.ep.data));
				break;
			}
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
