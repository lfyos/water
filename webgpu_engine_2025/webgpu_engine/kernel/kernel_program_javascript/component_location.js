function construct_component_location_object(my_component_number,my_computer,my_webgpu)
{
	this.component_number	=my_component_number;
	this.computer			=my_computer;
	this.webgpu				=my_webgpu;

	this.component_move_buffer			=null;
	this.component_relative_buffer		=null;
	this.component_absolute_buffer		=null;
	this.component_parent_id_buffer		=null;
	this.component_location_flag_buffer	=null;

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
			
			parent_id				:	-1
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
	this.set_component_location_flag=function(component_id,new_caculate_location_flag)
	{
		if((component_id>=0)&&(component_id<this.component.length)){
			this.component[component_id].move_version_id=this.version_id++;
				
			var old_caculate_location_flag=this.component[component_id].caculate_location_flag;
			this.component[component_id].caculate_location_flag=new_caculate_location_flag;
			if(old_caculate_location_flag^new_caculate_location_flag)
				this.webgpu.device.queue.writeBuffer(this.component_location_flag_buffer,
					Int32Array.BYTES_PER_ELEMENT*component_id,
					new Int32Array(new_caculate_location_flag?1:0));
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
			var component_id									=component_loca_buffer[i][0];
			var old_caculate_location_flag						=this.component[component_id].caculate_location_flag;
			var new_caculate_location_flag						=(component_loca_buffer[i][1]>0)?true:false;
			this.component[component_id].caculate_location_flag	=new_caculate_location_flag;
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
			if(old_caculate_location_flag^new_caculate_location_flag)
				this.webgpu.device.queue.writeBuffer(this.component_location_flag_buffer,
					Int32Array.BYTES_PER_ELEMENT*component_id,new Int32Array(new_caculate_location_flag?1:0));
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
	this.do_component_location_initialization=function(component_array_sorted_by_id,
				id_buffer,camera_buffer,system_id_number,camera_number,
				common_shader_data_structure,location_shader_program)
	{
		for(var i=0,ni=this.component_number;i<ni;i++){
			var p=component_array_sorted_by_id[i].component_parent;
			this.component[i].parent_id=(p==null)?-1:(p.component_id);
		}
		var buffer_length=Float32Array.BYTES_PER_ELEMENT*this.identify_matrix.length*this.component_number;
		this.component_move_buffer=this.webgpu.device.createBuffer(
			{
				size	:	buffer_length,
				usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.STORAGE
			});
		this.component_relative_buffer=this.webgpu.device.createBuffer(
			{
				size	:	buffer_length,
				usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.STORAGE
			});
		this.component_absolute_buffer=this.webgpu.device.createBuffer(
			{
				size	:	buffer_length,
				usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.STORAGE
			});	
		this.component_parent_id_buffer=this.webgpu.device.createBuffer(
			{
				size	:	this.component_number*Int32Array.BYTES_PER_ELEMENT,
				usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.STORAGE
			});
		this.component_location_flag_buffer=this.webgpu.device.createBuffer(
			{
				size	:	this.component_number*Int32Array.BYTES_PER_ELEMENT,
				usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.STORAGE
			});

		var my_parent_id_array=new Array(this.component_number);
		var my_location_flag_array=new Array(this.component_number);
		for(var i=0,ni=this.component_number;i<ni;i++){
			my_parent_id_array[i]		=this.component[i].parent_id;
			my_location_flag_array[i]	=0;
		}
		this.webgpu.device.queue.writeBuffer(
				this.component_parent_id_buffer,	0,new Int32Array(my_parent_id_array));
		this.webgpu.device.queue.writeBuffer(
				this.component_location_flag_buffer,0,new Int32Array(my_location_flag_array));

		var my_component_bindgroup_layout_entries=[
			{	//id_info
				binding		:	0,
				visibility	:	GPUShaderStage.COMPUTE,
				buffer		:
				{
					type		:	"storage"
				}
			},
			{	//absolute_matrix
				binding		:	1,
				visibility	:	GPUShaderStage.COMPUTE,
				buffer		:
				{
					type		:	"storage"
				}
			},
			{	//camera_information
				binding		:	2,
				visibility	:	GPUShaderStage.COMPUTE,
				buffer		:
				{
					type		:	"storage"
				}
			},
			{	//relative_matrix
				binding		:	3,
				visibility	:	GPUShaderStage.COMPUTE,
				buffer		:
				{
					type		:	"read-only-storage"
				}
			},
			{	//move_matrix
				binding		:	4,
				visibility	:	GPUShaderStage.COMPUTE,
				buffer		:
				{
					type		:	"read-only-storage"
				}
			},
			{	//parent_id
				binding		:	5,
				visibility	:	GPUShaderStage.COMPUTE,
				buffer		:
				{
					type		:	"read-only-storage"
				}
			},
			{
				//location_flag
				binding		:	6,
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
				{	//absolute_matrix
					binding		:	1,
					resource	:
					{
						buffer	:	this.component_absolute_buffer
					}
				},
				{	//camera_information
					binding		:	2,
					resource	:
					{
						buffer	:	camera_buffer
					}
				},
				{	//move_matrix
					binding		:	3,
					resource	:
					{
						buffer	:	this.component_move_buffer
					}
				},
				{
					//relative_matrix
					binding		:	4,
					resource	:
					{
						buffer	:	this.component_relative_buffer
					}
				},
				{	//parent_id
					binding		:	5,
					resource	:
					{
						buffer	:	this.component_parent_id_buffer
					}
				},
				{
					//location_flag
					binding		:	6,
					resource	:
					{
						buffer	:	this.component_location_flag_buffer
					}
				}
			];
		this.component_bindgroup=this.webgpu.device.createBindGroup(
			{
				layout	:	my_component_bindgroup_layout,
				entries	:	my_component_resource_entries
			});	
		var my_pipeline_layout=this.webgpu.device.createPipelineLayout(
			{
				bindGroupLayouts:[my_component_bindgroup_layout]
			});
		var my_component_module=this.webgpu.device.createShaderModule(
			{
				code:common_shader_data_structure+location_shader_program
			});
			
		this.component_workgroup_size=Math.ceil(Math.exp(Math.log(this.component_number)/3.0));
		while((this.component_workgroup_size*this.component_workgroup_size*this.component_workgroup_size)<this.component_number)
			this.component_workgroup_size++;
		this.system_id_workgroup_size=Math.ceil(Math.exp(Math.log(system_id_number)/3.0));
		while((this.system_id_workgroup_size*this.system_id_workgroup_size*this.system_id_workgroup_size)<system_id_number)
			this.system_id_workgroup_size++;

		this.compute_location_pipeline=this.webgpu.device.createComputePipeline(
		{
			layout	:	my_pipeline_layout,
			compute	:	
			{
				module		:	my_component_module,
				entryPoint	:	"compute_location_main",
				constants	:
				{
					component_number			:	this.component_number,
					component_workgroup_size	:	this.component_workgroup_size
				}
			}
		});
		this.set_location_pipeline=this.webgpu.device.createComputePipeline(
		{
			layout	:	my_pipeline_layout,
			compute	:	
			{
				module		:	my_component_module,
				entryPoint	:	"set_location_main",
				constants	:
				{
					system_id_number			:	system_id_number,
					camera_number				:	camera_number,
					system_id_workgroup_size	:	this.system_id_workgroup_size
				}
			}
		});
	}
	this.compute_component_location=function()
	{
		var encoder=this.webgpu.compute_pass_encoder;
		
		encoder.setBindGroup(0,this.component_bindgroup);
		
		encoder.setPipeline(this.compute_location_pipeline);
		encoder.dispatchWorkgroups(
			this.component_workgroup_size,
			this.component_workgroup_size,
			this.component_workgroup_size);	
		
		encoder.setPipeline(this.set_location_pipeline);
		encoder.dispatchWorkgroups(
			this.system_id_workgroup_size,
			this.system_id_workgroup_size,
			this.system_id_workgroup_size);
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
		if(this.component_absolute_buffer!=null){
			this.component_absolute_buffer.destroy();
			this.component_absolute_buffer=null;
		}
		if(this.component_parent_id_buffer!=null){
			this.component_parent_id_buffer.destroy();
			this.component_parent_id_buffer=null;
		}
		if(this.component_location_flag_buffer!=null){
			this.component_location_flag_buffer.destroy();
			this.component_location_flag_buffer=null;
		}
	};
};
