function init_system_bindgroup(render)
{
	var system_bindgroup_layout_entries=
	[
		{	//method  buffer
			binding		:	0,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	true
			}
		},
		{	//target buffer
			binding		:	1,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	true
			}
		},
		{	// component buffer
			binding		:	2,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	false
			}
		},
		{	// id buffer
			binding		:	3,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	false
			}
		},
		{	// system buffer
			binding		:	4,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	false
			}
		}
	];
	render.system_bindgroup_layout=render.webgpu.device.createBindGroupLayout(
		{
			entries	:	system_bindgroup_layout_entries
		});
	
	render.component_system_id_buffer=new Array(render.component_system_id.length);
	for(var i=0,ni=render.component_system_id_buffer.length;i<ni;i++){
		var p=render.component_system_id[i];
		var my_component_id=p[3];
		var my_buffer=render.webgpu.device.createBuffer(
		{
			size	:	Int32Array.BYTES_PER_ELEMENT*render.component_system_id[i].length,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
		render.webgpu.device.queue.writeBuffer(my_buffer,0,new Int32Array(render.component_system_id[i]));
		
		var my_bindgroup_entries=[
			{	//method  buffer
				binding		:	0,
				resource	:
				{
					buffer	:	render.system_buffer.method_buffer,
					size	:	render.system_buffer.method_buffer_size
				}
			},
			{	//target buffer
				binding		:	1,
				resource	:
				{
					buffer	:	render.system_buffer.target_buffer,
					size	:	render.system_buffer.target_buffer_size 
				}
			},
			{	// component buffer
				binding		:	2,
				resource	:
				{
					buffer	:	render.component_location_data.buffer[my_component_id],
					size	:	Float32Array.BYTES_PER_ELEMENT*render.component_location_data.identify_matrix.length
				}
			},
			{	// id buffer
				binding		:	3,
				resource	:
				{
					buffer	:	my_buffer,
					size	:	Int32Array.BYTES_PER_ELEMENT*render.component_system_id[i].length
				}
			},
			{	// system buffer
				binding		:	4,
				resource	:
				{
					buffer	:	render.system_buffer.system_buffer,
					size	:	render.system_buffer.system_buffer_size
				}
			}
		];
		
		var my_bindgroup_object=render.webgpu.device.createBindGroup(
			{
				layout	:	render.system_bindgroup_layout,
				entries	:	my_bindgroup_entries
			});	

		render.component_system_id_buffer[i]={
			buffer				:	my_buffer,
			bindgroup_object	:	my_bindgroup_object
		}
	}
}
