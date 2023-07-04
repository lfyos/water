function my_create_part_driver(part_object,render_driver,render)
{
	this.buffer=new Array();

	this.draw_component=function (method_data,target_data,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,render_driver,render)	
	{		
		if(target_data.render_buffer_id>=this.buffer.length)
			return;
		var rpe	=render.webgpu.render_pass_encoder;
		var instance_number=component_render_parameter.length
		rpe.setPipeline(render_driver.pipeline);

		for(var instance_id=0;instance_id<instance_number;instance_id++){		
			if(instance_id>=this.buffer[target_data.render_buffer_id].length)
				break;
			if(this.buffer[target_data.render_buffer_id][instance_id]==null)
				continue;
			var instance_data=component_render_parameter[instance_id];
			for(var i=0,ni=instance_data.length;i<ni;i++){
				render.set_system_bindgroup_by_component(
					target_data.render_buffer_id,method_data.method_id,
					(instance_data[i][1]>=0)?(instance_data[i][1]):(project_matrix.camera_component_id),
					-1);					
				for(var p=part_object.buffer_object.edge.region_data,j=0,nj=p.length;j<nj;j++){
					rpe.setVertexBuffer(0,p[j].buffer);
					rpe.setVertexBuffer(1,this.buffer[target_data.render_buffer_id][instance_id],
							Float32Array.BYTES_PER_ELEMENT*i);
					rpe.draw(p[i].item_number);
				}
			}	
		}			
	}
	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object,render)
	{
		var ret_val=
		{
			material_id		:	buffer_object_data.material_id,
			region_box		:	buffer_object_data.region_box,
			region_data		:	(request_type_string!="edge")?new Array():buffer_object_data.region_data,
			item_number		:	(request_type_string!="edge")?0:(buffer_object_data.item_number),
			item_size		:	(request_type_string!="edge")?4:
						(buffer_object_data.region_data.length/buffer_object_data.item_number),
			private_data	:	null
		};
		return ret_val;
	}

	this.replace_render_parameter=function(	instance_id,
		old_instance_data,new_instance_data,part_object,render_buffer_id,render)
	{
		this.append_render_parameter(instance_id,
			new_instance_data,part_object,render_buffer_id,render);
	}
	this.append_render_parameter=function(instance_id,
		new_instance_data,part_object,render_buffer_id,render)
	{
		while(render_buffer_id>=this.buffer.length)
			this.buffer.push(new Array());
		while(instance_id>=this.buffer[render_buffer_id].length)
			this.buffer[render_buffer_id].push(null);
		if(this.buffer [render_buffer_id][instance_id]!=null){
			this.buffer[render_buffer_id][instance_id].destroy();
			this.buffer[render_buffer_id][instance_id]=null;
		}
		var length_data=new Array(new_instance_data.length);
		for(var i=0,ni=length_data.length;i<ni;i++)
			length_data[i]=new_instance_data[i][0];
		
		this.buffer[render_buffer_id][instance_id]=render.webgpu.device.createBuffer(
			{
				size	:	length_data.length*Float32Array.BYTES_PER_ELEMENT,
				usage	:	GPUBufferUsage.VERTEX|GPUBufferUsage.COPY_DST
			});
		render.webgpu.device.queue.writeBuffer(
			this.buffer[render_buffer_id][instance_id],0,new Float32Array(length_data));
	}	
	this.delete_render_parameter=function(
		current_instance_id,	delete_instance_data,
		last_instance_id,		last_instance_data,
		part_object,			render_buffer_id,		render)
	{
		var current_buffer	=this.buffer[render_buffer_id][current_instance_id];
		var last_buffer		=this.buffer[render_buffer_id][last_instance_id];
	
		this.buffer[render_buffer_id][last_instance_id]		=current_buffer;
		this.buffer[render_buffer_id][current_instance_id]	=last_buffer;
		
		if(this.buffer[render_buffer_id][last_instance_id]!=null){
			this.buffer[render_buffer_id][last_instance_id].destroy();
			this.buffer[render_buffer_id][last_instance_id]=null;
		}
	}
	this.append_component_parameter=function(buffer_id,buffer_data,part_object,render)
	{
		
	};
	this.shift_component_parameter=function(buffer_id,buffer_data,part_object,render)
	{
		
	};
	
	this.destroy=function()
	{
		for(var i=0,ni=this.buffer.length;i<ni;i++)
			if(this.buffer[i]!=null){
				for(var j=0,nj=this.buffer[i].length;j<nj;j++)
					if(this.buffer[i][j]!=null){
						this.buffer[i][j].destroy();
						this.buffer[i][j]=null;
					};
				this.buffer[i]=null;
			}
		this.buffer=null;
		this.draw_component		=null;
		this.decode_vertex_data	=null;
	}	
}

function main(	render_id,		render_name,
				init_data,		text_array,
				shader_code,	render)
{
	var my_module=render.webgpu.device.createShaderModule(
			{
				code: shader_code
			});
			
	var pipeline_descr=
	{
		layout: render.webgpu.device.createPipelineLayout(
		{
			bindGroupLayouts:
			[
				render.system_bindgroup_layout
			]
		}),

		vertex:
		{
			module	:	my_module,
			entryPoint	:	"vertex_main",
			buffers		:
			[
				{
					arrayStride	:	64,
						
					stepMode	:	"vertex",
						
					attributes	:
					[
						{	//vertex
							format			:	"float32x4",
							offset			:	0,
							shaderLocation	:	0
						},
						{	//material
							format			:	"float32x4",
							offset			:	32,
							shaderLocation	:	1
						}
					]
				},
				{
					arrayStride	:	4,
						
					stepMode	:	"instance",
						
					attributes	:
					[
						{	//line length
							format			:	"float32",
							offset			:	0,
							shaderLocation	:	2
						}
					]
				}
			]
		},
		    
		fragment	:
		{
			module		:	my_module,
			
			entryPoint	:	"fragment_main",
			targets	: 
			[
				{
					format: render.webgpu.gpu.getPreferredCanvasFormat(),
				}
			],
		},
		primitive	:
		{
			topology	:	"line-list"
		}
	};
	
	this.pipeline = render.webgpu.device.createRenderPipeline(pipeline_descr);

	this.create_part_driver=my_create_part_driver;
	
	this.destroy=function()
	{
		this.pipeline=null;
	}
}