function init_system_bindgroup(render)
{
	var system_bindgroup_layout_entries=
	[
		{	// system buffer
			binding		:	0,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	false
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
		{	// id buffer
			binding		:	2,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	true
			}
		}
	];
	render.system_bindgroup_layout=render.webgpu.device.createBindGroupLayout(
		{
			entries	:	system_bindgroup_layout_entries
		});
	
	render.system_buffer.id_buffer_data_length =40;
	
	var system_stride		=render.webgpu.adapter.limits.minUniformBufferOffsetAlignment;
	var user_stride			=0;
		user_stride+=Float32Array.BYTES_PER_ELEMENT*render.component_location_data.identify_matrix.length;
		user_stride+=Float32Array.BYTES_PER_ELEMENT*render.system_buffer.id_buffer_data_length;
		user_stride+=Int32Array.BYTES_PER_ELEMENT*render.system_bindgroup_id[0].length;
	
	render.system_buffer.id_stride		=(system_stride<user_stride)?user_stride:system_stride;
	render.system_buffer.id_buffer_size =user_stride;
	
	render.system_buffer.id_buffer=render.webgpu.device.createBuffer(
		{
			size	:	render.system_buffer.id_stride*render.system_bindgroup_id.length,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
	
	var my_bindgroup_entries=[
		{	// system buffer
			binding		:	0,
			resource	:
			{
				buffer	:	render.system_buffer.system_buffer,
				size	:	render.system_buffer.system_buffer_size
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
		{	// id buffer
			binding		:	2,
			resource	:
			{
				buffer	:	render.system_buffer.id_buffer,
				size	:	render.system_buffer.id_buffer_size
			}
		}
	];
	render.system_buffer.system_bindgroup=render.webgpu.device.createBindGroup(
	{
		layout	:	render.system_bindgroup_layout,
		entries	:	my_bindgroup_entries
	});	
	
	render.system_buffer.location_version=new Array(render.system_bindgroup_id.length);
	for(var i=0,ni=render.system_buffer.location_version.length;i<ni;i++)
		render.system_buffer.location_version[i]=-1;

	return;
}
