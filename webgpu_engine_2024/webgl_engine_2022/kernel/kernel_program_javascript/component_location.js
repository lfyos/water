function construct_component_location_object(my_component_number,my_computer,my_webgpu)
{
	this.component_number	=my_component_number;
	this.computer			=my_computer;
	this.webgpu				=my_webgpu;


	this.component_move_buffer=null;
	this.component_relative_buffer=null;
	this.component_parent_id_buffer=null;

	this.version_id			=3;
	this.identify_matrix	=[	1,	0,	0,	0,		0,	1,	0,	0,		0,	0,	1,	0,		0,	0,	0,	1];
	
	this.component			=new Array();
	
	for(var i=0,ni=this.component_number;i<ni;i++){
		this.component[i]={
			move_version_id			:	2,
			absolute_version_id		:	1,
			caculate_location_flag	:	false,
			
			relative_matrix			:	this.identify_matrix,
			move_matrix				:	this.identify_matrix,
			absolute_location		:	this.identify_matrix,
			
			parent_id				:	-1,
			depth					:	0
		}
	}
	this.set_component_move_location=function(component_id,loca)
	{
		if((component_id>=0)&&(component_id<this.component.length)){
			this.component[component_id].move_matrix	=loca;
			this.component[component_id].move_version_id=this.version_id++;
			
			this.webgpu.device.queue.writeBuffer(this.component_move_buffer,
				Float32Array.BYTES_PER_ELEMENT*this.identify_matrix.length*component_id,
				new Float32Array(loca));
		}
	};
	this.decode_location=function(data)
	{
		if(data.length<=0)
			return this.identify_matrix;
		
		if(data.length>=16)
			return [
				data[ 0],data[ 1],data[ 2],data[ 3],
				data[ 4],data[ 5],data[ 6],data[ 7],
				data[ 8],data[ 9],data[10],data[11],
				data[12],data[13],data[14],data[15]
			];
		
		var my_data=[0,0,0,0,0,0],code=Math.round(data[data.length-1]);
		for(var i=5,j=data.length-2;((i>=0)&&(j>=0));i--,code=(code>>1))
			if((code&1)!=0)
				my_data[i]=data[j--];
		return this.computer.create_move_rotate_matrix(
				my_data[0],my_data[1],my_data[2],my_data[3],my_data[4],my_data[5]);
	};
	this.set_component_location=function(component_loca_buffer)
	{
		var identify_matrix_length=Float32Array.BYTES_PER_ELEMENT*this.identify_matrix.length;
		for(var i=0,ni=component_loca_buffer.length,my_version_id=this.version_id++;i<ni;i++){
			var component_id									= component_loca_buffer[i][0];
			this.component[component_id].caculate_location_flag	=(component_loca_buffer[i][1]>0)?true:false;
			this.component[component_id].move_matrix			=this.decode_location(component_loca_buffer[i][2]);
			if(component_loca_buffer[i].length>3)
				this.component[component_id].relative_matrix=this.decode_location(component_loca_buffer[i][3]);
			this.component[component_id].move_version_id=my_version_id;
			
			var buffer_position=identify_matrix_length*component_id;
			this.webgpu.device.queue.writeBuffer(this.component_move_buffer,buffer_position,
					new Float32Array(this.component[component_id].move_matrix));
			if(component_loca_buffer[i].length>3)
				this.webgpu.device.queue.writeBuffer(this.component_relative_buffer,buffer_position,
					new Float32Array(this.component[component_id].relative_matrix));
		}
	};
	this.get_component_move_location=function(component_id)
	{
		return ((component_id<0)||(component_id>=(this.component.length)))
				?(this.identify_matrix):(this.component[component_id].move_matrix);
	};
	this.get_component_location=function(component_id)
	{
		if((component_id<0)||(component_id>=(this.component.length)))
			return this.identify_matrix;
		if(typeof(this.component[component_id])!="object")
			return this.identify_matrix;
		
		if(this.component[component_id].caculate_location_flag){
			if(this.component[component_id].absolute_version_id<this.component[component_id].move_version_id){
				this.component[component_id].absolute_version_id=this.component[component_id].move_version_id;
				this.component[component_id].absolute_location	=this.component[component_id].move_matrix;
			}
			return this.component[component_id].absolute_location;
		}
		
		var parent_id;

		if((parent_id=this.component[component_id].parent_id)<0){
			if(this.component[component_id].absolute_version_id<this.component[component_id].move_version_id){
				this.component[component_id].absolute_version_id=this.component[component_id].move_version_id;
				this.component[component_id].absolute_location=this.computer.matrix_multiplication(
						this.component[component_id].relative_matrix,this.component[component_id].move_matrix);
			}
			return this.component[component_id].absolute_location;
		}
		if(typeof(this.component[parent_id])!="object")
			return this.identify_matrix;

		var number=0,loca=this.get_component_location(parent_id);

		if(this.component[component_id].absolute_version_id<this.component[parent_id].absolute_version_id){
			this.component[component_id].absolute_version_id=this.component[parent_id].absolute_version_id;
			number++;
		}
		if(this.component[component_id].absolute_version_id<this.component[component_id].move_version_id){
			this.component[component_id].absolute_version_id=this.component[component_id].move_version_id;
			number++;
		}
		
		if(number<=0)
			return this.component[component_id].absolute_location;
		
		loca=this.computer.matrix_multiplication(loca,this.component[component_id].relative_matrix);
		loca=this.computer.matrix_multiplication(loca,this.component[component_id].move_matrix);
		this.component[component_id].absolute_location=loca;
		
		return loca;
	};
	this.caculate_depth=function(current_component_id,current_depth,component_array_sorted_by_id)
	{
		this.component[current_component_id].depth=current_depth;
		
		var p=component_array_sorted_by_id[current_component_id];
		for(var i=0,ni=p.component_children.length;i<ni;i++)
			this.caculate_depth(p.component_children[i].component_id,
					current_depth+1,component_array_sorted_by_id);
	}
	this.do_initialize=function(component_array_sorted_by_id,id_buffer)
	{
		var parent_component_id=this.component_number-1,pp,p=component_array_sorted_by_id;
		for(var component_id=0;component_id<this.component_number;component_id++){
			if((pp=p[component_id].component_parent)==null)
				parent_component_id=component_id;
			else
				this.component[component_id].parent_id=pp.component_id;
		}
		this.caculate_depth(parent_component_id,0,component_array_sorted_by_id);
	
		var identify_matrix_data	=new Float32Array(this.identify_matrix);
		var identify_matrix_length	=Float32Array.BYTES_PER_ELEMENT*this.identify_matrix.length;
	
		this.component_move_buffer=this.webgpu.device.createBuffer(
			{
				size	:	this.component_number*identify_matrix_length,
				usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.STORAGE
			});
		this.component_relative_buffer=this.webgpu.device.createBuffer(
			{
				size	:	this.component_number*identify_matrix_length,
				usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.STORAGE
			});
		this.component_parent_id_buffer=this.webgpu.device.createBuffer(
			{
				size	:	this.component_number*Int32Array.BYTES_PER_ELEMENT,
				usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.STORAGE
			});
			
		for(var p,i=0,ni=this.component_number;i<ni;i++){
			this.webgpu.device.queue.writeBuffer(this.component_move_buffer,
				 identify_matrix_length*i,identify_matrix_data);
			this.webgpu.device.queue.writeBuffer(this.component_relative_buffer,
				identify_matrix_length*i,identify_matrix_data);
		}
		var my_parent_id_array=new Array(this.component_number);
		for(var i=0,ni=this.component_number;i<ni;i++)
			my_parent_id_array[i]=this.component[i].parent_id;
		this.webgpu.device.queue.writeBuffer(this.component_parent_id_buffer,0,new Int32Array(my_parent_id_array));
	
		var my_component_bindgroup_layout_entries=[
			{	//id_info
				binding		:	0,
				visibility	:	GPUShaderStage.COMPUTE,
				buffer		:
				{
					type		:	"storage"
				}
			},
			{	//move_matrix
				binding		:	1,
				visibility	:	GPUShaderStage.COMPUTE,
				buffer		:
				{
					type		:	"read-only-storage"
				}
			},
			{	//relative_matrix
				binding		:	2,
				visibility	:	GPUShaderStage.COMPUTE,
				buffer		:
				{
					type		:	"read-only-storage"
				}
			},
			{	//parent_id
				binding		:	3,
				visibility	:	GPUShaderStage.COMPUTE,
				buffer		:
				{
					type		:	"read-only-storage"
				}
			}
		];
		var my_component_bindgroup_layout=this.webgpu.device.createBindGroupLayout(
			{
				entries	:my_component_bindgroup_layout_entries
			});
		var my_component_resource_entries=[
				{	//id_info
					binding		:	0,
					resource	:
						{
							buffer	:	id_buffer
						}
				},
				{	//move_matrix
					binding		:	1,
					resource	:
						{
							buffer	:	this.component_move_buffer
						}
				},
				{
					//relative_matrix
					binding		:	2,
					resource	:
						{
							buffer	:	this.component_relative_buffer
						}
				},
				{	//parent_id
					binding		:	3,
					resource	:
						{
							buffer	:	this.component_parent_id_buffer
						}
				}
			];
		this.component_bindgroup=this.webgpu.device.createBindGroup(
			{
				layout	:	my_component_bindgroup_layout,
				entries	:	my_component_resource_entries
			});	
		var my_component_module=this.webgpu.device.createShaderModule(
			{
				code:
				"struct id_information																	\n"+
				"{																						\n"+
				"			matrix				:	mat4x4<f32>,										\n"+
					
				"			data				:	array<vec4<f32>,10>,								\n"+
					
				"			render_id			:	i32,												\n"+
				"			part_id				:	i32,												\n"+
					
				"			data_buffer_id		:	i32,												\n"+
					
				"			component_id		:	i32,												\n"+
				"			driver_id			:	i32,												\n"+
				
				"			component_system_id	:	i32,												\n"+
					
				"			tmp_int_0			:	i32,												\n"+
				"			tmp_int_1			:	i32													\n"+
				"}																						\n"+
				"@group(0) @binding(0)	var<storage,read_write> id_info			: array<id_information>;\n"+
				"@group(0) @binding(1)	var<storage,read> 		move_matrix		: array<mat4x4<f32>>;	\n"+
				"@group(0) @binding(2)	var<storage,read> 		relative_matrix	: array<mat4x4<f32>>; 	\n"+
				"@group(0) @binding(3)	var<storage,read> 		parent_id		: array<i32>;			\n"+
				
				"@compute @workgroup_size(1)															\n"+
				"		fn location_main(@builtin(global_invocation_id)global_id: vec3<u32>)			\n"+
				"{																						\n"+
				"	var component_matrix=mat4x4<f32>(													\n"+
				"			vec4<f32>(1.0,0.0,0.0,0.0),vec4<f32>(0.0,1.0,0.0,0.0),						\n"+
				"			vec4<f32>(0.0,0.0,1.0,0.0),vec4<f32>(0.0,0.0,0.0,1.0));						\n"+
				"	var component_id=id_info[global_id.x].component_id;									\n"+				
				"	for(;component_id>=0;component_id=parent_id[component_id]){							\n"+
				"		component_matrix=move_matrix[component_id]		*component_matrix;				\n"+
				"		component_matrix=relative_matrix[component_id]	*component_matrix;				\n"+
				"	}																					\n"+
				"	id_info[global_id.x].matrix=component_matrix;										\n"+
				"}																						\n"
			});
			this.component_pipeline=this.webgpu.device.createComputePipeline(
				{
					layout	:	this.webgpu.device.createPipelineLayout(
							{
								bindGroupLayouts:[my_component_bindgroup_layout]
							}),
					compute	:	{
						module		:	my_component_module,
						entryPoint	:	"location_main"
					}
				}
			);
	}
	this.destroy=function()
	{
		if(this.component_move_buffer!=null){
			this.component_move_buffer.destroy();
			this.component_move_buffer=null;
		}
		if(this.component_relative_buffer!=null){
			this.component_relative_buffer.destroy();
			this.component_relative_buffer=null;
		}
		if(this.component_parent_id_buffer!=null){
			this.component_parent_id_buffer.destroy();
			this.component_parent_id_buffer=null;
		}
	};
};

function compute_scene_component_location_routine(scene)
{
	scene.webgpu.compute_pass_encoder.setPipeline(scene.component_location_data.component_pipeline);
	scene.webgpu.compute_pass_encoder.setBindGroup(0,scene.component_location_data.component_bindgroup);
	scene.webgpu.compute_pass_encoder.dispatchWorkgroups(scene.system_bindgroup_id.length);		
}
