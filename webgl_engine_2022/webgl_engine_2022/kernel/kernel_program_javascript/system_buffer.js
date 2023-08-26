function construct_system_buffer(target_buffer_number,render)
{
	this.main_target_project_matrix	=null;
	this.main_target_view_parameter	=null;

//	init target buffer:	binding point 1
	this.target_buffer_stride	=2048;
	this.target_buffer_size		=1232;
	this.target_buffer_number	=target_buffer_number;
	this.target_buffer	=render.webgpu.device.createBuffer(
		{
			size	:	this.target_buffer_stride*this.target_buffer_number,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});

//	init system buffer:	binding point 2
	this.system_buffer_size		=384;
	this.system_buffer	=render.webgpu.device.createBuffer(
		{
			size	:	this.system_buffer_size,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});

	this.destroy=function()
	{
		if(this.target_buffer!=null){
			this.target_buffer.destroy();
			this.target_buffer=null;
		}
		if(this.system_buffer!=null){
			this.system_buffer.destroy();
			this.system_buffer=null;
		}
		if(this.id_buffer!=null){
			this.id_buffer.destroy();
			this.id_buffer=null;
		}
		if(this.system_bindgroup!=null){
			this.system_bindgroup=null;
		}
		if(this.id_buffer_init_flag!=null){
			this.id_buffer_init_flag=null;
		}
		this.set_system_buffer=null;
		this.set_target_buffer=null;
	};

	this.set_system_buffer=function(render)
	{
		var flag=((this.main_target_project_matrix==null)||(this.main_target_view_parameter==null));
			
		var t=render.current_time;
		var nanosecond=t%1000;		t=Math.floor((t-nanosecond)/1000);
		var microsecond=t%1000;		t=Math.floor((t-microsecond)/1000);
		var da=new Date();			da.setTime(t);

		var int_data=[
			render.pickup.component_id,
			render.pickup.driver_id,
			
			render.pickup.render_id,
			render.pickup.part_id,
			
			render.pickup.body_id,
			render.pickup.face_id,
			render.pickup.vertex_id,
			render.pickup.loop_id,
			render.pickup.edge_id,
			render.pickup.point_id,
			
			render.highlight.component_id,
			render.highlight.body_id,
			render.highlight.face_id,

			flag?0:(this.main_target_view_parameter.view_x0),
			flag?0:(this.main_target_view_parameter.view_y0),
			flag?1:(this.main_target_view_parameter.view_width),
			flag?1:(this.main_target_view_parameter.view_height),
			flag?1:(this.main_target_view_parameter.whole_view_width),
			flag?1:(this.main_target_view_parameter.whole_view_height),
			
			da.getFullYear(),
			da.getMonth(),
			da.getDate(),
			da.getHours(),
			da.getMinutes(),
			da.getSeconds(),
			da.getMilliseconds(),
			
			microsecond,
			nanosecond,
		];

		var float_data=[
			render.pickup.depth,
			render.pickup.value[0],
			render.pickup.value[1],
			render.pickup.value[2]
		];
		
		if(flag)
			float_data=float_data.concat(
				[	1,0,0,0,	0,1,0,0,	0,0,1,0,	0,0,0,1,
					1,0,0,0,	0,1,0,0,	0,0,1,0,	0,0,0,1
				]);
		else{
			float_data=float_data.concat(this.main_target_project_matrix.screen_move_matrix);
			float_data=float_data.concat(this.main_target_project_matrix.negative_screen_move_matrix);
		}
		
		var component_location=render.component_location_data;
		var camera_object_parameter=render.camera.camera_object_parameter;
		
		for(var i=0,ni=camera_object_parameter.length;i<ni;i++)
			if(camera_object_parameter[i].light_camera_flag){
				var light_component_id	=camera_object_parameter[i].component_id;
				var light_distance		=camera_object_parameter[i].distance;
				var light_matrix		=component_location.get_component_location(light_component_id);
				var light_position		=render.computer.caculate_coordinate(light_matrix,0,0,light_distance);
				float_data.push(light_position[0],light_position[1],light_position[2],light_position[3]);
			}
		render.webgpu.device.queue.writeBuffer(this.system_buffer,
			0,												new Int32Array(int_data));
		render.webgpu.device.queue.writeBuffer(this.system_buffer,
			int_data.length*Int32Array.BYTES_PER_ELEMENT,	new Float32Array(float_data));
	};

	this.set_target_buffer=function(render_data,project_matrix,render)
	{
		var target_id=render_data.render_buffer_id;
		
		if(render_data.main_display_target_flag){
			this.main_target_project_matrix	=project_matrix;
			this.main_target_view_parameter	=render_data.target_view_parameter;
		}
		var int_data=[
			render_data.target_view_parameter.view_x0,
			render_data.target_view_parameter.view_y0,
			render_data.target_view_parameter.view_width,
			render_data.target_view_parameter.view_height,
			render_data.target_view_parameter.whole_view_width,
			render_data.target_view_parameter.whole_view_height,
			
			project_matrix.projection_type_flag?1:0,
			1
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
		render.webgpu.device.queue.writeBuffer(this.target_buffer,	
			this.target_buffer_stride*target_id,
			new Float32Array(float_data));
		render.webgpu.device.queue.writeBuffer(this.target_buffer,
			this.target_buffer_stride*target_id+float_data.length*Float32Array.BYTES_PER_ELEMENT,
			new Int32Array(int_data));
	};
}