function init_ids_of_part_and_component(
	sorted_component_name_id,part_component_id_and_driver_id,render)
{
	var component_number=sorted_component_name_id.length;
	
	render.component_array_sorted_by_name	=new Array(component_number);
	render.component_array_sorted_by_id		=new Array(component_number);
	
	for(var i=0;i<component_number;i++){
		var my_component_name		=sorted_component_name_id[i][0];
		var my_component_id	 		=sorted_component_name_id[i][1];
		var my_component_children	=sorted_component_name_id[i][2];
			
		var p={
				component_name		:	my_component_name,
				component_id		:	my_component_id,
				component_parent	:	null,
				component_children	:	my_component_children
		};
		render.component_array_sorted_by_name[i]=p;
		render.component_array_sorted_by_id[my_component_id]=p;
	};
	
	for(var i=0;i<component_number;i++){
		var p=render.component_array_sorted_by_id[i];
		var my_component_children=new Array(p.component_children.length);
		for(var j=0,nj=my_component_children.length;j<nj;j++)
			my_component_children[j]=render.component_array_sorted_by_id[p.component_children[j]];
		p.component_children=my_component_children;
	};
	
	for(var i=0;i<component_number;i++){
		var p=render.component_array_sorted_by_id[i];
		for(var j=0,nj=p.component_children.length;j<nj;j++)
			p.component_children[j].component_parent=p;
	};
	
	var component_render_id_and_part_id=new Array(component_number);
	for(var i=0;i<component_number;i++)
		component_render_id_and_part_id[i]=new Array();
		
	var permanent_render_part_id	=new Array();
	var component_system_id			=new Array();
	
	var render_number=part_component_id_and_driver_id.length;
	for(var render_id=0;render_id<render_number;render_id++){
		permanent_render_part_id[render_id]=new Array();
		if(typeof(render.part_initialize_data[render_id])=="undefined")
			render.part_initialize_data[render_id]=new Array();

		var part_number=part_component_id_and_driver_id[render_id].length;
		for(var part_id=0;part_id<part_number;part_id++){
			var id_array=part_component_id_and_driver_id[render_id][part_id];
			permanent_render_part_id[render_id][part_id]={
				permanent_part_id	:	id_array.pop(),
				permanent_render_id	:	id_array.pop()
			}
			for(var buffer_id=0,buffer_number=id_array.length;buffer_id<buffer_number;buffer_id++){				
				var component_id			=id_array[buffer_id][0];
				var driver_id				=id_array[buffer_id][1];
				var my_component_system_id	=component_system_id.length;
				id_array[buffer_id][2]		=my_component_system_id;
				
				component_render_id_and_part_id[component_id][driver_id]=[render_id,part_id,buffer_id,my_component_system_id];
				component_system_id[my_component_system_id]=[
					render_id,		part_id,	buffer_id,
					component_id,	driver_id,
					my_component_system_id
				];
			}
			if(typeof(render.part_initialize_data[render_id][part_id])=="undefined")
				render.part_initialize_data[render_id][part_id]=null;
		};
	};
	
	for(var i=0,ni=component_render_id_and_part_id.length;i<ni;i++)
		for(var j=0,nj=component_render_id_and_part_id[i].length;j<nj;j++)
			if(typeof(component_render_id_and_part_id[i][j])=="undefined")
				component_render_id_and_part_id[i][j]=[-1,-1,-1,-1];

	render.component_render_id_and_part_id	=component_render_id_and_part_id;
	render.component_system_id				=component_system_id;
	render.permanent_render_part_id			=permanent_render_part_id;
	render.part_component_id_and_driver_id	=part_component_id_and_driver_id;
	
	
	render.system_bindgroup_layout=render.webgpu.device.createBindGroupLayout({
			entries:[
				{	//method  buffer
					binding		:	0,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	true
					}
				},
				{	//target buffer
					binding		:	1,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	true
					}
				},
				{	// component buffer
					binding		:	2,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	false
					}
				},
				{	// id buffer
					binding		:	3,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	false
					}
				},
				{	// system buffer
					binding		:	4,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	false
					}
				}
			]});

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
		
		var my_bindgroup_object=render.webgpu.device.createBindGroup({
		layout	:	render.system_bindgroup_layout,
		entries	:	[
				{	//method  buffer
					binding		:	0,
					resource	:	{
						buffer	:	render.system_buffer.method_buffer,
						size	:	render.system_buffer.method_buffer_size
					}
				},
				{	//target buffer
					binding		:	1,
					resource	:	{
						buffer	:	render.system_buffer.target_buffer,
						size	:	render.system_buffer.target_buffer_size 
					}
				},
				{	// component buffer
					binding		:	2,
					resource	:	{
						buffer	:	render.component_location_data.buffer[my_component_id],
						size	:	Float32Array.BYTES_PER_ELEMENT*render.component_location_data.identify_matrix.length
					}
				},
				{	// id buffer
					binding		:	3,
					resource	:	{
						buffer	:	my_buffer,
						size	:	Int32Array.BYTES_PER_ELEMENT*p.length
					}
				},
				{	// system buffer
					binding		:	4,
					resource	:	{
						buffer	:	render.system_buffer.system_buffer,
						size	:	render.system_buffer.system_buffer_size
					}
				}
			]
		});	
		render.component_system_id_buffer[i]={
			buffer				:	my_buffer,
			bindgroup_object	:	my_bindgroup_object
		}
	}
	return;
}
