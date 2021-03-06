function construct_camera_object(camera_component_id,my_component_location_data,my_computer)
{
	this.camera_object_parameter=new Array();
	for(var i=0,n=camera_component_id.length;i<n;i++){
		this.camera_object_parameter[i]=new Object();
		this.camera_object_parameter[i].component_id	=camera_component_id[i];
		this.camera_object_parameter[i].distance		=1.0;
		this.camera_object_parameter[i].half_fovy_tanl	=1.0;
		this.camera_object_parameter[i].near_value_ratio=0.10;
		this.camera_object_parameter[i].far_value_ratio	=10.0;
		this.camera_object_parameter[i].projection_type_flag=true;
	}
	
	this.camera_render_parameter=new Array();
	this.component_location_data=my_component_location_data;
	this.computer				=my_computer;
	
	this.modify_camera_data=function(render_buffer_id,camera_data_from_server)
	{
		var all_camera_data		=camera_data_from_server[0];
		var current_camera_data	=camera_data_from_server[1];
		
		for(var i=0,ni=all_camera_data.length;i<ni;i++){
			var camera_id			=all_camera_data[i][0];
			var parameter_type_id	=all_camera_data[i][1];
			var parameter_value		=all_camera_data[i][2];
			switch(parameter_type_id){
			case 0:
				this.camera_object_parameter[camera_id].distance=parameter_value;
				break;
			case 1:
				this.camera_object_parameter[camera_id].half_fovy_tanl=parameter_value;
				break;
			case 2:
				this.camera_object_parameter[camera_id].near_value_ratio=parameter_value;
				break;
			case 3:
				this.camera_object_parameter[camera_id].far_value_ratio=parameter_value;
				break;
			case 4:
				this.camera_object_parameter[camera_id].projection_type_flag=(parameter_value>0.5)?true:false;
				break;
			}
		}
		if(typeof(this.camera_render_parameter[render_buffer_id])=="undefined")
			this.camera_render_parameter[render_buffer_id]=new Object();
		
		for(var i=0,ni=current_camera_data.length;i<ni;i++){
			switch(current_camera_data[i][0]){
			case 0:
				this.camera_render_parameter[render_buffer_id].camera_id=current_camera_data[i][1];
				break;
			case 1:
				var view_volume_box=current_camera_data[i];
				view_volume_box=[	
					[	view_volume_box[1],	view_volume_box[2],	view_volume_box[3],	1],
					[	view_volume_box[4],	view_volume_box[5],	view_volume_box[6],	1]
				];
				this.camera_render_parameter[render_buffer_id].view_volume_box=view_volume_box;
				break;
			case 2:
				this.camera_render_parameter[render_buffer_id].mirror_change_matrix	=
					(current_camera_data[i].length<=1)?null:
						this.computer.project_to_plane_location(
							current_camera_data[i][1],current_camera_data[i][2],
							current_camera_data[i][3],current_camera_data[i][4],2.0);
				break;
			}
		}
	};
	this.compute_frustem_projection_matrix=function(render_buffer_id)
	{
		var camera_id			=this.camera_render_parameter[render_buffer_id].camera_id;
		
		var camera_distance		=this.camera_object_parameter[camera_id].distance;
		var near_value_ratio	=this.camera_object_parameter[camera_id].near_value_ratio;
		var far_value_ratio		=this.camera_object_parameter[camera_id].far_value_ratio;
		var half_fovy_tanl		=this.camera_object_parameter[camera_id].half_fovy_tanl;
				
		var near_value			=near_value_ratio*camera_distance;
		var far_value			=far_value_ratio*camera_distance;	
		
		var top_value			=near_value*half_fovy_tanl;

		var bottom_value		=(-top_value);
		var right_value			=top_value;
		var left_value			=(-right_value);
	
		return {
			matrix			:
			[
				2.0*near_value/(right_value-left_value),
				0,
				0,
				0,
				
				0,
				2.0*near_value/(top_value-bottom_value),
				0,
				0,
				
				(right_value+left_value)/(right_value-left_value),
				(top_value+bottom_value)/(top_value-bottom_value),
				(near_value+far_value)/(near_value-far_value),
				-1,
				
				0,
				0,
				2.0*near_value*far_value/(near_value-far_value),
				0
			],
			negative_matrix	:
			[
				(right_value-left_value)/(2.0*near_value),
				0.0,
				0.0,
				0.0,
				
				0.0,
				(top_value-bottom_value)/(2.0*near_value),
				0.0,
				0.0,
				
				0.0,
				0.0,
				0.0,
				(near_value-far_value)/(2.0*near_value*far_value),
				
				(right_value+left_value)/(2.0*near_value),
				(top_value+bottom_value)/(2.0*near_value),
				-1.0,
				(near_value+far_value)/(2.0*near_value*far_value)
			]
		};
	};
	this.compute_orthographic_projection_matrix=function(render_buffer_id)
	{
		var camera_id		=this.camera_render_parameter[render_buffer_id].camera_id;
		var camera_distance	=this.camera_object_parameter[camera_id].distance;
		var near_value_ratio=this.camera_object_parameter[camera_id].near_value_ratio;
		var far_value_ratio	=this.camera_object_parameter[camera_id].far_value_ratio;
		var half_fovy_tanl	=this.camera_object_parameter[camera_id].half_fovy_tanl;
				
		var near_value		=near_value_ratio*camera_distance;
		var far_value		=far_value_ratio*camera_distance;	
		
		var top_value		=camera_distance*half_fovy_tanl;
		var bottom_value	=(-top_value);
		var right_value		=top_value;
		var left_value		=(-right_value);

		return {
			matrix			:
			[
				2.0/(right_value-left_value),
				0,
				0,
				0,
				
				0,
				2.0/(top_value-bottom_value),
				0,
				0,
				
				0,
				0,
				2.0/(near_value-far_value),
				0,
				
				(right_value+left_value)/(left_value-right_value),
				(top_value+bottom_value)/(bottom_value-top_value),
				(near_value+far_value)/(near_value-far_value),
				1
			],
			negative_matrix	:
			[
				(right_value-left_value)/2.0,
				0.0,
				0.0,
				0.0,
				
				0.0,
				(top_value-bottom_value)/2.0,
				0.0,
				0.0,
				
				0.0,
				0.0,
				(near_value-far_value)/2.0,
				0.0,
				
				(right_value+left_value)/2.0,
				(top_value+bottom_value)/2.0,
				(near_value+far_value)/(-2.0),
				1.0
			]
		};
	};
	this.compute_screen_move_matrix=function(render_buffer_id)
	{
		var view_volume_box=this.camera_render_parameter[render_buffer_id].view_volume_box;
		var x0=view_volume_box[0][0],y0=view_volume_box[0][1],z0=view_volume_box[0][2];
		var x1=view_volume_box[1][0],y1=view_volume_box[1][1],z1=view_volume_box[1][2];
		var dx=x1-x0,dy=y1-y0,dz=z1-z0;

		return {
			matrix			:
			[
				2.0/dx,			0.0,			0.0,			0.0,
				0.0,			2.0/dy,			0.0,			0.0,
				0.0,			0.0,			2.0/dz,			0.0,
				-(x1+x0)/dx,	-(y1+y0)/dy,	-(z1+z0)/dy,	1.0
			],
			negative_matrix	:
			[
				dx/2.0,			0.0,			0.0,			0.0,
				0.0,			dy/2.0,			0.0,			0.0,
				0.0,			0.0,			dz/2.0,			0.0,
				(x1+x0)/2.0,	(y1+y0)/2.0,	(z1+z0)/2.0,	1.0
			]
		};
	};

	this.compute_lookat_matrix=function(render_buffer_id)
	{
		var camera_id			=this.camera_render_parameter[render_buffer_id].camera_id;
		var camera_component_id	=this.camera_object_parameter[camera_id].component_id;
		var camera_location		=this.component_location_data.get_component_location_routine(camera_component_id);
		var camera_distance		=this.camera_object_parameter[camera_id].distance;
		var mirror_change_matrix=this.camera_render_parameter[render_buffer_id].mirror_change_matrix;

		var lookat_matrix;
		
		do{
			if(typeof(mirror_change_matrix)!="undefined")
				if(mirror_change_matrix!=null)
					if(mirror_change_matrix.length>0){
						lookat_matrix=this.computer.matrix_multiplication(
										mirror_change_matrix,camera_location);
						lookat_matrix=this.computer.matrix_multiplication(lookat_matrix,
										this.computer.create_move_rotate_matrix(0,0,camera_distance,0,0,0));
						lookat_matrix=this.computer.matrix_multiplication(lookat_matrix,
							[
								1,	0,	0,	0,
								0,	-1,	0,	0,
								0,	0,	1,	0,
								0,	0,	0,	1
							]);
						break;
					}		
			lookat_matrix=this.computer.matrix_multiplication(camera_location,
					this.computer.create_move_rotate_matrix(0,0,camera_distance,0,0,0));
		}while(false);
		
		return {
			matrix			:	this.computer.matrix_negative(lookat_matrix),
			negative_matrix	:	lookat_matrix,
			camera_location	:	camera_location
		};
	};
	
	this.compute_camera_data=function(render_buffer_id)
	{	
		var camera_id						=this.camera_render_parameter[render_buffer_id].camera_id;
		var screen_move_matrix				=this.compute_screen_move_matrix(render_buffer_id);	
		var frustem_projection_matrix		=this.compute_frustem_projection_matrix(render_buffer_id);
		var orthographic_projection_matrix	=this.compute_orthographic_projection_matrix(render_buffer_id);
		var lookat_matrix					=this.compute_lookat_matrix(render_buffer_id);

		var project_matrix=new  Object();

		project_matrix.camera_id			=camera_id;
		project_matrix.camera_component_id	=this.camera_object_parameter[camera_id].component_id;
		
		project_matrix.projection_type_flag	=this.camera_object_parameter[camera_id].projection_type_flag;
		project_matrix.half_fovy_tanl		=this.camera_object_parameter[camera_id].half_fovy_tanl;
		project_matrix.near_value_ratio		=this.camera_object_parameter[camera_id].near_value_ratio;
		project_matrix.far_value_ratio		=this.camera_object_parameter[camera_id].far_value_ratio;
		
		project_matrix.distance				=this.camera_object_parameter[camera_id].distance;
		project_matrix.near_value			=(project_matrix.near_value_ratio)*(project_matrix.distance);
		project_matrix.far_value			=(project_matrix.far_value_ratio)*(project_matrix.distance);	
		
		project_matrix.screen_move_matrix	=screen_move_matrix.matrix;
		
		project_matrix.lookat_matrix		=lookat_matrix.matrix;
		project_matrix.negative_lookat_matrix=lookat_matrix.negative_matrix;
		project_matrix.camera_location		=lookat_matrix.camera_location;
		
				
		project_matrix.frustem_matrix=this.computer.matrix_multiplication(
				screen_move_matrix.matrix,frustem_projection_matrix.matrix);		
		project_matrix.frustem_matrix=this.computer.matrix_multiplication(
				project_matrix.frustem_matrix,lookat_matrix.matrix);
		
		project_matrix.negative_frustem_matrix=this.computer.matrix_multiplication(
				lookat_matrix.negative_matrix,frustem_projection_matrix.negative_matrix);
		project_matrix.negative_frustem_matrix=this.computer.matrix_multiplication(
				project_matrix.negative_frustem_matrix,screen_move_matrix.negative_matrix);
							
		project_matrix.orthographic_matrix=this.computer.matrix_multiplication(
				screen_move_matrix.matrix,orthographic_projection_matrix.matrix);	
		project_matrix.orthographic_matrix=this.computer.matrix_multiplication(
				project_matrix.orthographic_matrix,lookat_matrix.matrix);
		
		project_matrix.negative_orthographic_matrix=this.computer.matrix_multiplication(	
				lookat_matrix.negative_matrix,orthographic_projection_matrix.negative_matrix);
		project_matrix.negative_orthographic_matrix=this.computer.matrix_multiplication(
				project_matrix.negative_orthographic_matrix,screen_move_matrix.negative_matrix);
				
		if(this.camera_object_parameter[camera_id].projection_type_flag){
			project_matrix.matrix			=project_matrix.frustem_matrix;
			project_matrix.negative_matrix	=project_matrix.negative_frustem_matrix;
		}else{
			project_matrix.matrix			=project_matrix.orthographic_matrix;
			project_matrix.negative_matrix	=project_matrix.negative_orthographic_matrix;
		}

		project_matrix.original_far_center_point =this.computer.caculate_coordinate(project_matrix.camera_location,0,0,project_matrix.distance-project_matrix.far_value);
		project_matrix.original_center_point	 =this.computer.caculate_coordinate(project_matrix.camera_location,0,0,0);
		project_matrix.original_near_center_point=this.computer.caculate_coordinate(project_matrix.camera_location,0,0,project_matrix.distance-project_matrix.near_value);
		project_matrix.original_eye_point		 =this.computer.caculate_coordinate(project_matrix.camera_location,0,0,project_matrix.distance);

		project_matrix.far_center_point			=this.computer.caculate_coordinate(project_matrix.negative_lookat_matrix,0,0,-(project_matrix.far_value));
		project_matrix.center_point				=this.computer.caculate_coordinate(project_matrix.negative_lookat_matrix,0,0,-(project_matrix.distance));
		project_matrix.near_center_point		=this.computer.caculate_coordinate(project_matrix.negative_lookat_matrix,0,0,-(project_matrix.near_value));
		project_matrix.eye_point				=this.computer.caculate_coordinate(project_matrix.negative_lookat_matrix,0,0,0);
		
	 	project_matrix.center_point_depth		=this.computer.caculate_coordinate(project_matrix.matrix,project_matrix.center_point[0],project_matrix.center_point[1],project_matrix.center_point[2])[2];
		
		project_matrix.left_down_center_point	=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1,-1,project_matrix.center_point_depth);
		project_matrix.left_up_center_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1, 1,project_matrix.center_point_depth);
		project_matrix.right_down_center_point	=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1,-1,project_matrix.center_point_depth);
		project_matrix.right_up_center_point	=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1, 1,project_matrix.center_point_depth);

		project_matrix.left_down_near_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1,-1,-1);
		project_matrix.left_up_near_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1, 1,-1);
		project_matrix.right_down_near_point	=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1,-1,-1);
		project_matrix.right_up_near_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1, 1,-1);

		project_matrix.left_down_far_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1,-1, 1);
		project_matrix.left_up_far_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1, 1, 1);
		project_matrix.right_down_far_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1,-1, 1);
		project_matrix.right_up_far_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1, 1, 1);

		project_matrix.left_plane	=this.computer.create_plane_from_three_point(
				project_matrix.left_down_near_point,	project_matrix.left_up_near_point,
				project_matrix.left_up_far_point);		
		project_matrix.right_plane	=this.computer.create_plane_from_three_point(
				project_matrix.right_up_near_point,		project_matrix.right_down_near_point,
				project_matrix.right_down_far_point);
		project_matrix.up_plane		=this.computer.create_plane_from_three_point(
				project_matrix.left_up_near_point,		project_matrix.right_up_near_point,
				project_matrix.right_up_far_point);
		project_matrix.down_plane	=this.computer.create_plane_from_three_point(
				project_matrix.right_down_near_point,	project_matrix.left_down_near_point,
				project_matrix.left_down_far_point);
		project_matrix.near_plane	=this.computer.create_plane_from_three_point(
				project_matrix.left_down_near_point,	project_matrix.right_down_near_point,
				project_matrix.right_up_near_point);
		project_matrix.far_plane	=this.computer.create_plane_from_three_point(
				project_matrix.right_down_far_point,	project_matrix.left_down_far_point,
				project_matrix.left_up_far_point);
		
		project_matrix.center_plane	=this.computer.create_plane_from_two_point(
				project_matrix.center_point,			project_matrix.eye_point);
		
		return project_matrix;
	};
};
