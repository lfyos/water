function construct_camera_object(camera_number,my_component_location_data,my_computer)
{
	this.camera_object_parameter=new Array();
	
	for(var i=0;i<camera_number;i++)
		this.camera_object_parameter[i]={
			component_id			:	0,
			distance				:	1.0,
			half_fovy_tanl			:	1.0,
			near_value_ratio		:	0.10,
			far_value_ratio			:	10.0,
			projection_type_flag	:	false,
			light_camera_flag		:	false
		}
	this.component_location_data=my_component_location_data;
	this.computer				=my_computer;
	
	this.modify_camera_data=function(camera_data)
	{
		for(var i=0,ni=camera_data.length;i<ni;){
			var camera_id=camera_data[i++];
			var type_id=camera_data[i++];
			var p=this.camera_object_parameter[camera_id];
			
			switch(type_id){
			case 0:
				p.component_id			=camera_data[i++];						break;
			case 1:
				p.distance				=camera_data[i++];						break;
			case 2:
				p.half_fovy_tanl		=camera_data[i++];						break;
			case 3:
				p.near_value_ratio		=camera_data[i++];						break;
			case 4:
				p.far_value_ratio		=camera_data[i++];						break;
			case 5:
				p.projection_type_flag	=(camera_data[i++]>0.5)?true:false;		break;
			case 6:
				p.light_camera_flag		=(camera_data[i++]>0.5)?true:false;		break;
			}
		}
	}
	
	this.compute_frustem_projection_matrix=function(camera_render_parameter)
	{
		var camera_id			=camera_render_parameter.camera_id;
		
		var camera_distance		=this.camera_object_parameter[camera_id].distance;
		var near_value_ratio	=this.camera_object_parameter[camera_id].near_value_ratio;
		var far_value_ratio		=this.camera_object_parameter[camera_id].far_value_ratio;
		var half_fovy_tanl		=this.camera_object_parameter[camera_id].half_fovy_tanl;
				
		var near_value			=near_value_ratio*camera_distance;
		var far_value			=far_value_ratio*camera_distance;	
		
		var top_value			=camera_distance*half_fovy_tanl;
		var bottom_value		=(-top_value);
		
		var right_value			=top_value;
		var left_value			=(-right_value);
	
		return {
			matrix			:
			[
				2.0*camera_distance/(right_value-left_value),
				0,
				0,
				0,
				
				0,
				2.0*camera_distance/(top_value-bottom_value),
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
				(right_value-left_value)/(2.0*camera_distance),
				0.0,
				0.0,
				0.0,
				
				0.0,
				(top_value-bottom_value)/(2.0*camera_distance),
				0.0,
				0.0,
				
				0.0,
				0.0,
				0.0,
				(near_value-far_value)/(2.0*near_value*far_value),
				
				(right_value+left_value)/(2.0*camera_distance),
				(top_value+bottom_value)/(2.0*camera_distance),
				-1.0,
				(near_value+far_value)/(2.0*near_value*far_value)
			]
		};
	};
	this.compute_orthographic_projection_matrix=function(camera_render_parameter)
	{
		var camera_id		=camera_render_parameter.camera_id;
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
	this.compute_screen_move_matrix=function(camera_render_parameter)
	{
		var view_volume_box=camera_render_parameter.view_volume_box;
		var x0=view_volume_box[0][0],y0=view_volume_box[0][1],z0=view_volume_box[0][2];
		var x1=view_volume_box[1][0],y1=view_volume_box[1][1],z1=view_volume_box[1][2];
		var dx=x1-x0,dy=y1-y0,dz=z1-z0;

		return {
			matrix			:
			[
				2.0/dx,			0.0,			0.0,			0.0,
				0.0,			2.0/dy,			0.0,			0.0,
				0.0,			0.0,			1.0/dz,			0.0,
				-(x1+x0)/dx,	-(y1+y0)/dy,	-z0/dz,			1.0
			],
			negative_matrix	:
			[
				dx/2.0,			0.0,			0.0,			0.0,
				0.0,			dy/2.0,			0.0,			0.0,
				0.0,			0.0,			dz,				0.0,
				(x1+x0)/2.0,	(y1+y0)/2.0,	z0,				1.0
			]
		};
	};

	this.compute_lookat_matrix=function(camera_render_parameter)
	{
		var camera_id			=camera_render_parameter.camera_id;
		var camera_component_id	=this.camera_object_parameter[camera_id].component_id;
		var camera_location		=this.component_location_data.get_component_location(camera_component_id);
		var camera_distance		=this.camera_object_parameter[camera_id].distance;
		var lookat_matrix		=camera_render_parameter.camera_transformation_matrix;
		lookat_matrix			=this.computer.matrix_multiplication(lookat_matrix,camera_location);
		lookat_matrix			=this.computer.matrix_multiplication(lookat_matrix,[
										1,	0,	0,					0,
										0,	1,	0,					0,
										0,	0,	1,					0,
										0,	0,	camera_distance,	1
								]);
		return {
			matrix			:	this.computer.matrix_negative(lookat_matrix),
			negative_matrix	:	lookat_matrix,
			camera_location	:	camera_location
		};
	};

	this.compute_camera_data=function(camera_render_parameter)
	{	
		var camera_id						=camera_render_parameter.camera_id;
		var screen_move_matrix				=this.compute_screen_move_matrix(camera_render_parameter);	
		var frustem_projection_matrix		=this.compute_frustem_projection_matrix(camera_render_parameter);
		var orthographic_projection_matrix	=this.compute_orthographic_projection_matrix(camera_render_parameter);
		var lookat_matrix					=this.compute_lookat_matrix(camera_render_parameter);

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
		
		project_matrix.screen_move_matrix			=screen_move_matrix.matrix;
		project_matrix.negative_screen_move_matrix	=screen_move_matrix.negative_matrix;
		
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
		
	 	var center_point_depth					=this.computer.caculate_coordinate(project_matrix.matrix,
	 			project_matrix.center_point[0],project_matrix.center_point[1],project_matrix.center_point[2])[2];
		
		project_matrix.left_down_center_point	=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1,-1,center_point_depth);
		project_matrix.left_up_center_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1, 1,center_point_depth);
		project_matrix.right_down_center_point	=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1,-1,center_point_depth);
		project_matrix.right_up_center_point	=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1, 1,center_point_depth);

		project_matrix.left_down_near_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1,-1, 0);
		project_matrix.left_up_near_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1, 1, 0);
		project_matrix.right_down_near_point	=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1,-1, 0);
		project_matrix.right_up_near_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1, 1, 0);

		project_matrix.left_down_far_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1,-1, 1);
		project_matrix.left_up_far_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix,-1, 1, 1);
		project_matrix.right_down_far_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1,-1, 1);
		project_matrix.right_up_far_point		=this.computer.caculate_coordinate(project_matrix.negative_matrix, 1, 1, 1);

		project_matrix.to_me_direction			=this.computer.expand_operation(
			this.computer.sub_operation(project_matrix.eye_point,project_matrix.center_point),1.0);
		project_matrix.to_right_direction		=this.computer.expand_operation(
			this.computer.sub_operation(project_matrix.right_down_center_point,project_matrix.left_down_center_point),1.0);
		project_matrix.to_up_direction			=this.computer.expand_operation(
			this.computer.cross_operation(project_matrix.to_me_direction,project_matrix.to_right_direction),1.0);

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
				
		project_matrix.clip_plane			=camera_render_parameter.clip_plane;
		project_matrix.clip_plane_matrix	=camera_render_parameter.clip_plane_matrix;
		
		project_matrix.view_volume_box		=camera_render_parameter.view_volume_box;

		return project_matrix;
	};
};
