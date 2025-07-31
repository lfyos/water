function construct_component_driver(component_ids,init_data,create_data,part_object,part_driver,render_driver,scene)
{
	this.component_ids	=component_ids;
	
	this.buffer=scene.webgpu.device.createBuffer(
		{
			size	:	Float32Array.BYTES_PER_ELEMENT*8,
			usage	:	GPUBufferUsage.VERTEX|GPUBufferUsage.COPY_DST
		});
	scene.webgpu.device.queue.writeBuffer(this.buffer,0,
		new Float32Array([init_data[0],init_data[1],0,1,init_data[2],init_data[3],0,1]));
	
	this.sampler=scene.webgpu.device.createSampler(
		{
			addressModeU	:	"mirror-repeat",
			addressModeV	:	"mirror-repeat",
			magFilter		:	"linear",
			minFilter		:	"linear",
			mipmapFilter	:	"linear"
		})
	
	var ep=scene.component_event_processor[this.component_ids.component_id];
	if((typeof(ep)!="object")||(ep==null))
		scene.component_event_processor[this.component_ids.component_id]=(ep=new Object());

	ep.texture=null;
	this.bindgroup=null;
	
	ep.set_target=function(my_texture)
	{
		this.texture=my_texture;
	}

	this.draw_component=function(method_data,render_parameter,
			target_data,part_object,part_driver,render_driver,scene)		
	{
		var ep=scene.component_event_processor[this.component_ids.component_id];
		if(ep.texture!=null){
			var resource_entries=[
				{	//texture
					binding		:	0,
					resource	:	ep.texture.createView()
				},
				{
					//sampler
					binding		:	1,
					resource	:	this.sampler
				}
			];
			this.bindgroup=scene.webgpu.device.createBindGroup(
			{
				layout		:	render_driver.bindgroup_layout,
				entries		:	resource_entries
			});
			ep.texture=null;
		}
		if(this.bindgroup==null)
			return;	
			
		var rpe=scene.webgpu.render_pass_encoder;
		rpe.setPipeline(render_driver.pipeline);
		rpe.setBindGroup(1,this.bindgroup);
		rpe.setVertexBuffer(1,this.buffer);
		var p=part_object.buffer_object.face.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		};
	};
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)  
	{
		
	};
	this.destroy=function()
	{
		if(this.buffer!=null){
			this.buffer.destroy();
			this.buffer=null;
		};
	}	
};
