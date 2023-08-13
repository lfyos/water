function construct_system_buffer(target_buffer_number,method_buffer_number,my_render)
{
	this.render					=my_render;

//	init method buffer:	binding point 0
	this.method_buffer_stride	=this.render.webgpu.adapter.limits.minUniformBufferOffsetAlignment;
	this.method_buffer_size		=4;
	this.method_buffer_number	=method_buffer_number;
	
	this.method_buffer	=this.render.webgpu.device.createBuffer(
		{
			size	:	this.method_buffer_stride*this.method_buffer_number,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
	for(var i=0;i<this.method_buffer_number;i++)
		this.render.webgpu.device.queue.writeBuffer(
			this.method_buffer,this.method_buffer_stride*i,new Int32Array([i]));

//	init target buffer:	binding point 1
	this.target_buffer_stride	=2048;
	this.target_buffer_size		=1216;
	this.target_buffer_number	=target_buffer_number;
	this.target_buffer	=this.render.webgpu.device.createBuffer(
		{
			size	:	this.target_buffer_stride*this.target_buffer_number,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});

//	init system buffer:	binding point 2
	this.system_buffer_stride	=this.render.webgpu.adapter.limits.minUniformBufferOffsetAlignment;
	this.system_buffer_size		=240;
	this.system_buffer_number	=1;
	this.system_buffer	=this.render.webgpu.device.createBuffer(
		{
			size	:	this.system_buffer_stride*this.system_buffer_number,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});

//	init system_bindgroup_id
	this.system_bindgroup_id	=0;
	
	this.destroy=function()
	{
		this.render				=null;
	
		if(this.method_buffer!=null){
			this.method_buffer.destroy();
			this.method_buffer=null;
		}
		if(this.target_buffer!=null){
			this.target_buffer.destroy();
			this.target_buffer=null;
		}
		if(this.system_buffer!=null){
			this.system_buffer.destroy();
			this.system_buffer=null;
		}
		this.set_system_buffer		=null;
		this.set_target_buffer		=null;
	};

	this.set_system_buffer=function()
	{
		var t=this.render.current_time;
		var nanosecond=t%1000;		t=Math.floor((t-nanosecond)/1000);
		var microsecond=t%1000;		t=Math.floor((t-microsecond)/1000);
		var da=new Date();			da.setTime(t);

		var int_data=[
			this.render.pickup.component_id,
			this.render.pickup.driver_id,
			
			this.render.pickup.render_id,
			this.render.pickup.part_id,
			
			this.render.pickup.body_id,
			this.render.pickup.face_id,
			this.render.pickup.vertex_id,
			this.render.pickup.loop_id,
			this.render.pickup.edge_id,
			this.render.pickup.point_id,
			
			this.render.highlight.component_id,
			this.render.highlight.body_id,
			this.render.highlight.face_id,
			
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
			this.render.pickup.depth,
			this.render.pickup.value,
			0,0
		];
		
		var camera_object_parameter=this.render.camera.camera_object_parameter;
		var component_location=this.render.component_location_data;
		for(var i=0,ni=camera_object_parameter.length;i<ni;i++)
			if(camera_object_parameter[i].light_camera_flag){
				var light_component_id	=camera_object_parameter[i].component_id;
				var light_distance		=camera_object_parameter[i].distance;
				var light_matrix		=component_location.get_component_location(light_component_id);
				var light_position		=this.render.computer.caculate_coordinate(light_matrix,0,0,light_distance);
				float_data.push(light_position[0],light_position[1],light_position[2],light_position[3]);
			}
		this.render.webgpu.device.queue.writeBuffer(this.system_buffer,
			0,					new Int32Array(int_data));
		this.render.webgpu.device.queue.writeBuffer(this.system_buffer,
			int_data.length*4,	new Float32Array(float_data));
	};

	this.set_target_buffer=function(target_id,project_matrix,target_parameter)
	{
		var int_data=[
			project_matrix.projection_type_flag?1:0,
			target_parameter.target_width,
			target_parameter.target_height,
			0
		];
		var matrix_array=[
			project_matrix.matrix,
			project_matrix.negative_matrix,
			project_matrix.projection_type_flag
				?(project_matrix.orthographic_matrix)
				:(project_matrix.frustem_matrix),
			project_matrix.projection_type_flag
				?(project_matrix.negative_orthographic_matrix)
				:(project_matrix.negative_frustem_matrix),
				
			project_matrix.screen_move_matrix,
			project_matrix.negative_screen_move_matrix,
			
			project_matrix.lookat_matrix,
			project_matrix.negative_lookat_matrix,
			
			project_matrix.camera_location,
			
			project_matrix.clip_plane_matrix
		];
		
		var vector_array=[
			project_matrix.left_plane,	
			project_matrix.right_plane,
			project_matrix.up_plane,
			project_matrix.down_plane,
			project_matrix.near_plane,
			project_matrix.far_plane,
			project_matrix.center_plane,
			
			project_matrix.clip_plane,
			
			project_matrix.original_far_center_point,
			project_matrix.original_center_point,
			project_matrix.original_near_center_point,
			project_matrix.original_eye_point,
			
			project_matrix.far_center_point,
			project_matrix.center_point,
			project_matrix.near_center_point,
			project_matrix.eye_point,
			
			project_matrix.left_down_near_point,
			project_matrix.left_up_near_point,
			project_matrix.right_down_near_point,
			project_matrix.right_up_near_point,
			
			project_matrix.left_down_center_point,
			project_matrix.left_up_center_point,
			project_matrix.right_down_center_point,
			project_matrix.right_up_center_point,
			
			project_matrix.left_down_far_point,
			project_matrix.left_up_far_point,
			project_matrix.right_down_far_point,
			project_matrix.right_up_far_point,
			
			project_matrix.to_right_direction,
			project_matrix.to_up_direction,
			project_matrix.to_me_direction,
			
			project_matrix.view_volume_box[0],
			project_matrix.view_volume_box[1]
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
				
		float_data.push(
			project_matrix.half_fovy_tanl,
			project_matrix.near_value_ratio,
			project_matrix.far_value_ratio,
			
			project_matrix.distance,
			project_matrix.near_value,
			project_matrix.far_value,

			0,0
		);
		this.render.webgpu.device.queue.writeBuffer(this.target_buffer,	
			this.target_buffer_stride*target_id,
			new Float32Array(float_data));
		this.render.webgpu.device.queue.writeBuffer(this.target_buffer,
			this.target_buffer_stride*target_id+float_data.length*Float32Array.BYTES_PER_ELEMENT,
			new Int32Array(int_data));
	};
}