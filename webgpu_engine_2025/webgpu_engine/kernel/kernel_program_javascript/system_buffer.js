function construct_system_buffer(my_scene,my_max_target_number,my_max_method_number)
{
	this.scene					=my_scene;
	this.max_target_number		=my_max_target_number;
	this.max_method_number		=my_max_method_number;
	this.identify_matrix_length	=this.scene.component_location_data.identify_matrix.length;
	this.identify_matrix_length*=Float32Array.BYTES_PER_ELEMENT;

	this.system_bindgroup	=null;

	var my_alignment=this.scene.webgpu.adapter.limits.minUniformBufferOffsetAlignment;
	
	var my_system_bindgroup_layout_entries=[
		{	//target buffer
			binding		:	0,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	true
			}
		},
		{	// method buffer
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
		},
		{	// system buffer
			binding		:	3,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	false
			}
		},
		{	// camera buffer
			binding		:	4,
			visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
			buffer		:
			{
				type				:	"uniform",
				hasDynamicOffset	:	false
			}
		}
	];
	this.system_bindgroup_layout=this.scene.webgpu.device.createBindGroupLayout({
		entries	:	my_system_bindgroup_layout_entries
	});	

//	init target buffer:	binding point 0

	this.target_buffer_stride=0;
	this.target_buffer_stride+=this.identify_matrix_length*12;
	this.target_buffer_stride+=Float32Array.BYTES_PER_ELEMENT*4*33;
	this.target_buffer_stride+=Int32Array.	BYTES_PER_ELEMENT*16;
	this.target_buffer_stride=Math.ceil(this.target_buffer_stride/my_alignment)*my_alignment;
	
	this.target_buffer	=this.scene.webgpu.device.createBuffer(
		{
			size	:	this.target_buffer_stride*this.max_target_number,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
		
//	init method_buffer	:	binding point 1		
	this.method_buffer_stride=Int32Array.BYTES_PER_ELEMENT*4+Float32Array.BYTES_PER_ELEMENT*4*16;
	this.method_buffer_stride=Math.ceil(this.method_buffer_stride/my_alignment)*my_alignment;
	
	this.method_buffer	=this.scene.webgpu.device.createBuffer(
		{
			size	:	this.method_buffer_stride*this.max_method_number,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
	for(var i=0,ni=this.max_method_number;i<ni;i++)
		this.scene.webgpu.device.queue.writeBuffer(
					this.method_buffer,this.method_buffer_stride*i,new Int32Array([i]));

//	init id_buffer	:	binding point 2
	
	this.id_buffer_data_length	=40;
	this.id_buffer_stride		=0;
	this.id_buffer_stride+=this.identify_matrix_length;
	this.id_buffer_stride+=Float32Array.BYTES_PER_ELEMENT*this.id_buffer_data_length;
	this.id_buffer_stride+=Int32Array.BYTES_PER_ELEMENT*8;
	
	if((this.id_buffer_stride%my_alignment)!=0)
		alert("id_buffer_stride error: "+this.id_buffer_stride);

	this.id_buffer=this.scene.webgpu.device.createBuffer(
		{
			size	:	this.id_buffer_stride*this.scene.system_bindgroup_id.length,
			usage	:	GPUBufferUsage.STORAGE|GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});

	var id_buffer_pointer=this.identify_matrix_length+Float32Array.BYTES_PER_ELEMENT*this.id_buffer_data_length;
	for(var i=0,ni=this.scene.system_bindgroup_id.length;i<ni;i++,id_buffer_pointer+=this.id_buffer_stride)	{
		var p=this.scene.system_bindgroup_id[i];
		p=[p.render_id,p.part_id,p.data_buffer_id,p.component_id,p.driver_id,p.system_bindgroup_id,0,0];
		this.scene.webgpu.device.queue.writeBuffer(this.id_buffer,id_buffer_pointer,new Int32Array(p));
	}
//	init system buffer:	binding point 3
	var my_system_buffer_size=0;
	my_system_buffer_size+=Int32Array.	BYTES_PER_ELEMENT*24;
	my_system_buffer_size+=Float32Array.BYTES_PER_ELEMENT*4;
	this.system_buffer	=this.scene.webgpu.device.createBuffer(
		{
			size	:	my_system_buffer_size,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});

//	init camera buffer:	binding point 4

	this.camera_buffer_step=0;
	this.camera_buffer_step+=Int32Array.BYTES_PER_ELEMENT*8;
	this.camera_buffer_step+=Float32Array.BYTES_PER_ELEMENT*12;
	this.camera_buffer_step+=this.identify_matrix_length;

	this.camera_buffer	=this.scene.webgpu.device.createBuffer(
		{
			size	:	this.scene.camera.camera_number*this.camera_buffer_step,
			usage	:	GPUBufferUsage.STORAGE|GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});

// init system_bindgroup
	this.system_bindgroup=this.scene.webgpu.device.createBindGroup(
		{
			layout	:	this.system_bindgroup_layout,
			entries	:	[
				{	//target buffer
					binding		:	0,
					resource	:
					{
						buffer	:	this.target_buffer,
						size	:	this.target_buffer_stride 
					}
				},
				{	// method buffer
					binding		:	1,
					resource	:
					{
						buffer	:	this.method_buffer,
						size	:	this.method_buffer_stride
					}
				},
				{	// id buffer
					binding		:	2,
					resource	:
					{
						buffer	:	this.id_buffer,
						size	:	this.id_buffer_stride
					}
				},
				{	// system buffer
					binding		:	3,
					resource	:
					{
						buffer	:	this.system_buffer,
						size	:	my_system_buffer_size
					}
				},
				{	// camera buffer
					binding		:	4,
					resource	:
					{
						buffer	:	this.camera_buffer,
						size	:	this.scene.camera.camera_number*this.camera_buffer_step
					}
				}
			]
		});
	
	this.set_system_buffer=function()
	{
		var t=this.scene.current_time;
		var nanosecond=t%1000;		t=Math.floor((t-nanosecond)/1000);
		var microsecond=t%1000;		t=Math.floor((t-microsecond)/1000);
		var da=new Date();			da.setTime(t);
		
		var int_data=[
			this.scene.pickup.component_id,
			this.scene.pickup.driver_id,
			
			this.scene.pickup.render_id,
			this.scene.pickup.part_id,
			
			this.scene.pickup.body_id,
			this.scene.pickup.face_id,
			this.scene.pickup.loop_id,
			this.scene.pickup.edge_id,
			this.scene.pickup.primitive_id,
			this.scene.pickup.vertex_id,
			
			this.scene.highlight.component_id,
			this.scene.highlight.body_id,
			this.scene.highlight.face_id,
			
			da.getFullYear(),
			da.getMonth(),
			da.getDate(),
			da.getHours(),
			da.getMinutes(),
			da.getSeconds(),
			da.getMilliseconds(),
			
			microsecond,
			nanosecond,
			
			0,0
		];

		var float_data=[
			this.scene.pickup.depth,
			this.scene.pickup.value[0],
			this.scene.pickup.value[1],
			this.scene.pickup.value[2]
		];

		this.scene.webgpu.device.queue.writeBuffer(this.system_buffer,0,	new Int32Array(int_data));
		this.scene.webgpu.device.queue.writeBuffer(this.system_buffer,
					int_data.length*Int32Array.BYTES_PER_ELEMENT,	new Float32Array(float_data));
					
		for(var p,i=0,ni=this.scene.camera.camera_object_parameter.length;i<ni;i++)
			if((p=this.scene.camera.camera_object_parameter[i]).should_update_buffer_data_flag){
				p.should_update_buffer_data_flag=false;
				int_data	=[
					p.component_id,				p.projection_type_flag?1:0,	
					p.light_camera_flag,		p.light_camera_flag_ex,		p.light_camera_flag_ex_ex,	
					0,0,0
				];
				float_data	=[
					p.distance,			p.bak_distance,
					p.half_fovy_tanl,	p.bak_half_fovy_tanl,
					p.near_value_ratio,	p.far_value_ratio
				];
				var offset=this.camera_buffer_step*i;
				this.scene.webgpu.device.queue.writeBuffer(this.camera_buffer,offset,new Int32Array(int_data));
				offset+=Int32Array.BYTES_PER_ELEMENT*int_data.length;
				this.scene.webgpu.device.queue.writeBuffer(this.camera_buffer,offset,new Float32Array(float_data));
			}
	};
	
	this.set_target_buffer=function(target_render_data,target_render_data_from)
	{
		if(target_render_data_from==null)
			target_render_data_from=target_render_data;
		
		var int_data=[
			target_render_data.target_view_parameter.view_x0,
			target_render_data.target_view_parameter.view_y0,
			target_render_data.target_view_parameter.view_width,
			target_render_data.target_view_parameter.view_height,
			target_render_data.target_view_parameter.whole_view_width,
			target_render_data.target_view_parameter.whole_view_height,
			target_render_data.main_display_target_flag?1:0,

			target_render_data_from.target_view_parameter.view_x0,
			target_render_data_from.target_view_parameter.view_y0,
			target_render_data_from.target_view_parameter.view_width,
			target_render_data_from.target_view_parameter.view_height,
			target_render_data_from.target_view_parameter.whole_view_width,
			target_render_data_from.target_view_parameter.whole_view_height,
			target_render_data_from.main_display_target_flag?1:0,

			this.scene.scene_id,
			target_render_data.camera_id
		];
		var matrix_array=[
			target_render_data.project_matrix.matrix,
			target_render_data.project_matrix.negative_matrix,
			target_render_data.project_matrix.projection_type_flag
				?(target_render_data.project_matrix.orthographic_matrix)
				:(target_render_data.project_matrix.frustem_matrix),
			target_render_data.project_matrix.projection_type_flag
				?(target_render_data.project_matrix.negative_orthographic_matrix)
				:(target_render_data.project_matrix.negative_frustem_matrix),
				
			target_render_data.project_matrix.screen_move_matrix,
			target_render_data.project_matrix.negative_screen_move_matrix,
			target_render_data.project_matrix.screen_move_matrix_from,
			target_render_data.project_matrix.negative_screen_move_matrix_from,
			
			target_render_data.project_matrix.lookat_matrix,
			target_render_data.project_matrix.negative_lookat_matrix,
			
			target_render_data.project_matrix.camera_absolute_matrix,
			
			target_render_data.project_matrix.clip_plane_matrix
		];
		var vector_array=[
			target_render_data.project_matrix.left_plane,	
			target_render_data.project_matrix.right_plane,
			target_render_data.project_matrix.up_plane,
			target_render_data.project_matrix.down_plane,
			target_render_data.project_matrix.near_plane,
			target_render_data.project_matrix.far_plane,
			target_render_data.project_matrix.center_plane,
			
			target_render_data.project_matrix.clip_plane,
			
			target_render_data.project_matrix.original_far_center_point,
			target_render_data.project_matrix.original_center_point,
			target_render_data.project_matrix.original_near_center_point,
			target_render_data.project_matrix.original_eye_point,
			
			target_render_data.project_matrix.far_center_point,
			target_render_data.project_matrix.center_point,
			target_render_data.project_matrix.near_center_point,
			target_render_data.project_matrix.eye_point,
			
			target_render_data.project_matrix.left_down_near_point,
			target_render_data.project_matrix.left_up_near_point,
			target_render_data.project_matrix.right_down_near_point,
			target_render_data.project_matrix.right_up_near_point,
			
			target_render_data.project_matrix.left_down_center_point,
			target_render_data.project_matrix.left_up_center_point,
			target_render_data.project_matrix.right_down_center_point,
			target_render_data.project_matrix.right_up_center_point,
			
			target_render_data.project_matrix.left_down_far_point,
			target_render_data.project_matrix.left_up_far_point,
			target_render_data.project_matrix.right_down_far_point,
			target_render_data.project_matrix.right_up_far_point,
			
			target_render_data.project_matrix.to_right_direction,
			target_render_data.project_matrix.to_up_direction,
			target_render_data.project_matrix.to_me_direction,
			
			target_render_data.project_matrix.view_volume_box[0],
			target_render_data.project_matrix.view_volume_box[1]
		];
		
		var float_data=new Array();
		
		for(var p,i=0,ni=matrix_array.length;i<ni;i++)
			if((p=matrix_array[i])==null)
				float_data.push(1,0,0,0,	0,1,0,0,	0,0,1,0,	0,0,0,1);
			else
				float_data.push(p[ 0],p[ 1],p[ 2],p[ 3],	p[ 4],p[ 5],p[ 6],p[ 7],
								p[ 8],p[ 9],p[10],p[11],	p[12],p[13],p[14],p[15]);
		
		for(var p,i=0,ni=vector_array.length;i<ni;i++)
			if((p=vector_array[i])==null)
				float_data.push(0,0,0,0);
			else
				float_data.push(p[0],p[1],p[2],p[3]);
		
		var offset=this.target_buffer_stride*target_render_data.target_id;
		this.scene.webgpu.device.queue.writeBuffer(this.target_buffer,offset,new Float32Array(float_data));
		offset+=float_data.length*Float32Array.BYTES_PER_ELEMENT;
		this.scene.webgpu.device.queue.writeBuffer(this.target_buffer,offset,new Int32Array(int_data));
	};
	this.set_id_information_data=function(id_data,component_id,driver_id)
	{
		if(!(Array.isArray(id_data)))
			return;
		if(id_data.length<=0)
			return;
		
		var my_id_data;
		if(id_data.length<=this.id_buffer_data_length)
			my_id_data=id_data;
		else{
			my_id_data=new Array(this.id_buffer_data_length);
			for(var i=0,ni=this.id_buffer_data_length;i<ni;i++)
				my_id_data[i]=id_data[i];
		}
			
		var my_id_buffer_index_id,p=this.scene.component_array_sorted_by_id[component_id];
		
		driver_id=(typeof(driver_id)!="number")?-1:driver_id;
		if((driver_id<0)||(driver_id>=p.component_ids.length))
			my_id_buffer_index_id=p.component_system_bindgroup_id;
		else
			my_id_buffer_index_id=p.component_ids[driver_id].system_bindgroup_id;
		
		var pos=this.id_buffer_stride*my_id_buffer_index_id+this.identify_matrix_length;
		this.scene.webgpu.device.queue.writeBuffer(this.id_buffer,pos,new Float32Array(my_id_data));

		return;
	};
	this.set_method_information_data=function(method_data,method_id)
	{
		if((method_id<0)||(method_id>=this.max_method_number))
			return;
		if(!(Array.isArray(method_data)))
			return;
		var max_number=this.method_buffer_stride;
		max_number-=Int32Array.BYTES_PER_ELEMENT*4;
		max_number/=Float32Array.BYTES_PER_ELEMENT;
		
		var my_method_data=new Array(max_number);
		var i=0,ni=method_data.length;
		for(ni=(ni<max_number)?ni:max_number;i<ni;i++)
			my_method_data[i]=method_data[i];
		for(;i<max_number;i++)
			my_method_data[i]=0;
		this.scene.webgpu.device.queue.writeBuffer(this.method_buffer,
			this.method_buffer_stride*method_id+Int32Array.BYTES_PER_ELEMENT*4,
			new Float32Array(my_method_data));
		return;
	};
	this.set_system_bindgroup=function(target_id,method_id,component_id,driver_id)
	{
		if((target_id<0)||(target_id>=this.scene.render_target_array.length))
			return;
		if((component_id<0)||(component_id>=this.scene.component_array_sorted_by_id.length))
			return;
		
		var p=this.scene.component_array_sorted_by_id[component_id];
		driver_id=(typeof(driver_id)!="number")?-1:driver_id;
		
		var my_id_buffer_index_id;
		if((driver_id<0)||(driver_id>=p.component_ids.length))
			my_id_buffer_index_id=p.component_system_bindgroup_id;
		else
			my_id_buffer_index_id=p.component_ids[driver_id].system_bindgroup_id;

		this.scene.webgpu.render_pass_encoder.setBindGroup(0,this.system_bindgroup,
		[
			this.target_buffer_stride	*target_id,
			this.method_buffer_stride	*method_id,
			this.id_buffer_stride		*my_id_buffer_index_id
		]);
	}
	this.destroy=function()
	{
		if(this.target_buffer!=null){
			this.target_buffer.destroy();
			this.target_buffer=null;
		}
		if(this.method_buffer!=null){
			this.method_buffer.destroy();
			this.method_buffer=null;
		}
		if(this.id_buffer!=null){
			this.id_buffer.destroy();
			this.id_buffer=null;
		}
		if(this.system_buffer!=null){
			this.system_buffer.destroy();
			this.system_buffer=null;
		}
		if(this.camera_buffer!=null){
			this.camera_buffer.destroy();
			this.camera_buffer=null;
		}
	};
}