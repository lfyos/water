function construct_uniform_block_object(my_gl)
{
	this.gl=my_gl;
	
	this.system_bind_point_id=this.gl.getParameter(this.gl.MAX_UNIFORM_BUFFER_BINDINGS)-1;
	this.target_bind_point_id=this.gl.getParameter(this.gl.MAX_UNIFORM_BUFFER_BINDINGS)-2;
	this.pass_bind_point_id  =this.gl.getParameter(this.gl.MAX_UNIFORM_BUFFER_BINDINGS)-3;
	
	this.system_buffer_object_stride=256;
	this.target_buffer_object_stride=2048;
	this.pass_buffer_object_stride	=256;
	
	this.system_buffer_object=this.gl.createBuffer();
	this.gl.bindBuffer(this.gl.UNIFORM_BUFFER,this.system_buffer_object);
	this.gl.bufferData(this.gl.UNIFORM_BUFFER,this.system_buffer_object_stride,this.gl.DYNAMIC_DRAW);
	
	this.target_buffer_object=this.gl.createBuffer();
	this.gl.bindBuffer(this.gl.UNIFORM_BUFFER,this.target_buffer_object);
	this.gl.bufferData(this.gl.UNIFORM_BUFFER,this.target_buffer_object_stride,this.gl.DYNAMIC_DRAW);

	this.pass_buffer_object=this.gl.createBuffer();
	this.gl.bindBuffer(this.gl.UNIFORM_BUFFER,this.pass_buffer_object);
	this.gl.bufferData(this.gl.UNIFORM_BUFFER,this.pass_buffer_object_stride,this.gl.DYNAMIC_DRAW);

	this.gl.bindBufferBase	(this.gl.UNIFORM_BUFFER,this.system_bind_point_id,	this.system_buffer_object);
	this.gl.bindBufferBase	(this.gl.UNIFORM_BUFFER,this.target_bind_point_id,	this.target_buffer_object);
	this.gl.bindBufferBase	(this.gl.UNIFORM_BUFFER,this.pass_bind_point_id,	this.pass_buffer_object);
	
	this.destroy=function()
	{
		this.gl.deleteBuffer(this.system_buffer_object);
		this.gl.deleteBuffer(this.target_buffer_object);
		this.gl.deleteBuffer(this.pass_buffer_object);

		this.gl							=null;
		this.system_bind_point_id		=null;
		this.target_bind_point_id		=null;
		this.pass_bind_point_id			=null;
		this.system_buffer_object		=null;
		this.target_buffer_object		=null;
		this.pass_buffer_object			=null;

		this.destroy					=null;
		this.bind_system				=null;
		this.get_target_project_matrix	=null;
		this.bind_target				=null;
		this.bind_pass					=null;
		
	};
	
	this.bind_system=function(pickup,current_time,canvas_width,canvas_height,
			camera_object_parameter,component_location,computer)
	{
		var t=current_time;
		var nanosecond=t%1000;		t=Math.floor((t-nanosecond)/1000);
		var microsecond=t%1000;		t=Math.floor((t-microsecond)/1000);
		var da=new Date();			da.setTime(t);

		var int_data=[
			pickup.component_id,
			pickup.render_id,
			pickup.part_id,
			pickup.body_id,
			pickup.face_id,
			pickup.vertex_id,
			pickup.loop_id,
			pickup.edge_id,
			pickup.point_id,
			
			da.getFullYear(),
			da.getMonth(),
			da.getDate(),
			da.getHours(),
			da.getMinutes(),
			da.getSeconds(),
			da.getMilliseconds(),
			
			microsecond,
			nanosecond,
			
			canvas_width,
			canvas_height
		];
		
		var float_data=[
			pickup.depth,
			pickup.value,
			0,0
		];
		
		for(var i=0,ni=camera_object_parameter.length;i<ni;i++)
			if(camera_object_parameter[i].light_camera_flag){
				var light_component_id	=camera_object_parameter[i].component_id;
				var light_distance		=camera_object_parameter[i].distance;
				var light_matrix		=component_location.get_component_location_routine(light_component_id);
				var light_position		=computer.caculate_coordinate(light_matrix,0,0,light_distance);
				float_data.push(light_position[0],light_position[1],light_position[2],light_position[3]);
			}
		
		this.gl.bindBuffer		(this.gl.UNIFORM_BUFFER,this.system_buffer_object);
		this.gl.bufferSubData	(this.gl.UNIFORM_BUFFER,0,					new Int32Array(int_data),	 0);
		this.gl.bufferSubData	(this.gl.UNIFORM_BUFFER,int_data.length*4,	new Float32Array(float_data),0);
	};
	
	this.get_target_project_matrix=function(target_id)
	{
		if(typeof(this.target_buffer_object[target_id])=="undefined")
			return null;
		else
			return this.target_buffer_object[target_id].project_matrix;
	};
	
	this.bind_target=function(project_matrix,clip_plane,clip_plane_matrix,
							target_width,target_height,draw_buffer_id)
	{
		var matrix_array=[
			project_matrix.matrix,
			project_matrix.negative_matrix,
			project_matrix.projection_type_flag
				?project_matrix.orthographic_matrix
				:project_matrix.frustem_matrix,
			project_matrix.projection_type_flag
				?project_matrix.negative_orthographic_matrix
				:project_matrix.negative_frustem_matrix,
				
			project_matrix.screen_move_matrix,
			
			project_matrix.lookat_matrix,
			project_matrix.negative_lookat_matrix,
			
			project_matrix.camera_location,
			
			clip_plane_matrix
		];
		
		var vector_array=[
			project_matrix.left_plane,	
			project_matrix.right_plane,
			project_matrix.up_plane,
			project_matrix.down_plane,
			project_matrix.near_plane,
			project_matrix.far_plane,
			project_matrix.center_plane,
			
			clip_plane,
			
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
			project_matrix.right_up_far_point
		];
		
		var float_data=[
			target_width/target_height,
			project_matrix.half_fovy_tanl,
			project_matrix.near_value_ratio,
			project_matrix.far_value_ratio,
			
			project_matrix.distance,
			project_matrix.near_value,
			project_matrix.far_value,
			
			project_matrix.center_point_depth
		];

		var int_data=[
			project_matrix.projection_type_flag?1:0,
			target_width,target_height,draw_buffer_id
		];

		var matrix_data=new Array();
		for(var i=0,ni=matrix_array.length;i<ni;i++)
			matrix_data=matrix_data.concat(matrix_array[i]);
		
		var vectory_data=new Array();
		for(var i=0,ni=vector_array.length;i<ni;i++)
			vectory_data=vectory_data.concat(vector_array[i]);

		float_data=matrix_data.concat(vectory_data.concat(float_data));
		
		this.gl.bindBuffer	 (this.gl.UNIFORM_BUFFER,this.target_buffer_object);
		this.gl.bufferSubData(this.gl.UNIFORM_BUFFER,0,					 new Float32Array(float_data),	0);
		this.gl.bufferSubData(this.gl.UNIFORM_BUFFER,float_data.length*4,new Int32Array (int_data),		0);
	};	
	
	this.bind_pass=function(method_id,pass_id,clear_flag,viewport,clear_color)
	{
		var float_data=[
			clear_color[0],
			clear_color[1],
			clear_color[2],
			clear_color[3]
		];
		var int_data=[
			method_id,
			pass_id,
			
			clear_flag,
			
			viewport[0],
			viewport[1],
			viewport[2],
			viewport[3]
		];
		this.gl.bindBuffer		(this.gl.UNIFORM_BUFFER,this.pass_buffer_object	);
		this.gl.bufferSubData	(this.gl.UNIFORM_BUFFER,0,					 	new Float32Array(float_data),0);
		this.gl.bufferSubData	(this.gl.UNIFORM_BUFFER,float_data.length*4,	new Int32Array(int_data),	 0);
	};
}
