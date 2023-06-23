function construct_system_buffer(my_render)
{
	this.render					=my_render;
	
	this.system_buffer_stride	=256;
	this.target_buffer_stride	=2048;
	this.pass_buffer_stride		=256;
	
	this.target_buffer_data		=new Array(1);	
	this.pass_buffer_data		=new Array(1);

	this.system_buffer	=this.render.webgpu.device.createBuffer(
		{
			size	:	this.system_buffer_stride,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
	this.target_buffer	=this.render.webgpu.device.createBuffer(
		{
			size	:	this.target_buffer_stride,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});	
	this.pass_buffer	=this.render.webgpu.device.createBuffer(
		{
			size	:	this.pass_buffer_stride,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
	this.system_bindgroup_layout=this.render.webgpu.device.createBindGroupLayout({
			entries:[
				{	//component location
					binding		:	0,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	true
					}
				},
				{	//pass buffer
					binding		:	1,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	true
					}
				},
				{	//target buffer
					binding		:	2,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	true
					}
				},
				{	// system buffer
					binding		:	3,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	false
					}
				}
			]});
	this.system_bindgroup=null;

	this.destroy=function()
	{
		this.render=null;
		this.target_buffer_data	=null;	
		this.pass_buffer_data	=null;
		
		if(this.system_buffer!=null){
			this.system_buffer.destroy();
			this.system_buffer=null;
		}
		if(this.target_buffer!=null){
			this.target_buffer.destroy();
			this.target_buffer=null;
		}
		if(this.pass_buffer!=null){
			this.pass_buffer.destroy();
			this.pass_buffer=null;
		}
		
		this.system_bindgroup_layout=null;
		this.system_bindgroup		=null;
		
		this.set_bindgroup			=null;
		this.set_system_buffer		=null;
		this.set_target_buffer		=null;
		this.set_pass_buffer		=null;
	};
	this.set_bindgroup=function(bind_id,component_id,pass_id,target_id)
	{
		if(this.render.webgpu.command_encoder==null)
			return;
		if(this.render.webgpu.render_pass_encoder==null)
			return;
		if(this.system_bindgroup==null){
			this.system_bindgroup=this.render.webgpu.device.createBindGroup({
				layout	:	this.system_bindgroup_layout,
				entries	:	[
					{
						binding		:	0,
						resource	:	{
							buffer	:	this.render.component_location_data.component_buffer
						}
					},
					{
						binding		:	1,
						resource	:	{
							buffer	:	this.pass_buffer
						}
					},
					{
						binding		:	2,
						resource	:	{
							buffer	:	this.target_buffer
						}
					},
					{
						binding		:	3,
						resource	:	{
							buffer	:	this.system_buffer
						}
					}
				]
			});
		};
		
		this.render.webgpu.render_pass_encoder.setBindGroup(
			bind_id,this.system_bindgroup,
			[
				this.render.component_location_data.unit_size	*component_id,
				this.pass_buffer_stride							*pass_id,
				this.target_buffer_stride						*target_id
			]);
	}
	
	this.set_system_buffer=function()
	{
		var t=this.render.current_time;
		var nanosecond=t%1000;		t=Math.floor((t-nanosecond)/1000);
		var microsecond=t%1000;		t=Math.floor((t-microsecond)/1000);
		var da=new Date();			da.setTime(t);

		var int_data=[
			this.render.pickup.component_id,
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
			0,
			
			da.getFullYear(),
			da.getMonth(),
			da.getDate(),
			da.getHours(),
			da.getMinutes(),
			da.getSeconds(),
			da.getMilliseconds(),
			
			microsecond,
			nanosecond,
			
			this.render.canvas.width,
			this.render.canvas.height
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
				var light_matrix		=component_location.get_component_location_routine(light_component_id);
				var light_position		=this.render.computer.caculate_coordinate(light_matrix,0,0,light_distance);
				float_data.push(light_position[0],light_position[1],light_position[2],light_position[3]);
			}
		this.render.webgpu.device.queue.writeBuffer(this.system_buffer,0,					new Int32Array(int_data));
		this.render.webgpu.device.queue.writeBuffer(this.system_buffer,int_data.length*4,	new Float32Array(float_data));
	};

	this.set_target_buffer=function(target_id,
		project_matrix,clip_plane,clip_plane_matrix,target_width,target_height,draw_buffer_id)
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
			project_matrix.negative_screen_move_matrix,
			
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
		
		var begin_pointer,end_pointer;
		if(target_id<this.target_buffer_data.length){
			this.target_buffer_data[target_id]=[float_data,int_data];
			begin_pointer=target_id;
			end_pointer=target_id;
		}else{
			this.target_buffer_data[target_id]=[float_data,int_data];
			this.target_buffer.destroy();
			this.target_buffer	=this.render.webgpu.device.createBuffer(
					{
						size	:	this.target_buffer_stride*this.target_buffer_data.length,
						usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
					});
			begin_pointer=0;
			end_pointer=this.target_buffer_data.length-1;
			this.system_bindgroup=null;
		}
		for(var p,i=begin_pointer;i<=end_pointer;i++)
			if(typeof(p=this.target_buffer_data[i])!="undefined"){
				this.render.webgpu.device.queue.writeBuffer(this.target_buffer,	
					this.target_buffer_stride*i,				new Float32Array(	p[0]));
				this.render.webgpu.device.queue.writeBuffer(this.target_buffer,
					this.target_buffer_stride*i+p[0].length*4,	new Int32Array(		p[1]));
			}
	};
	this.set_pass_buffer=function(pass_id,method_id,clear_flag,viewport,clear_color)
	{
		var float_data=[
			clear_color[0],
			clear_color[1],
			clear_color[2],
			clear_color[3]
		];
		var int_data=[
			viewport[0],
			viewport[1],
			viewport[2],
			viewport[3],
			viewport[4],
			viewport[5],
			
			method_id,
			clear_flag,

			0,0,0
		];
		
		var begin_pointer,end_pointer;
		if(pass_id<this.pass_buffer_data.length){
			this.pass_buffer_data[pass_id]=[float_data,int_data];
			begin_pointer=pass_id;
			end_pointer=pass_id;
		}else{
			this.pass_buffer_data[pass_id]=[float_data,int_data];
			this.pass_buffer.destroy();
			this.pass_buffer	=this.render.webgpu.device.createBuffer(
					{
						size	:	this.pass_buffer_stride*this.pass_buffer_data.length,
						usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
					});
			begin_pointer=0;
			end_pointer=this.pass_buffer_data.length-1;
			
			this.system_bindgroup=null;
		}
		for(var p,i=begin_pointer;i<=end_pointer;i++)
			if(typeof(p=this.pass_buffer_data[i])!="undefined"){
				this.render.webgpu.device.queue.writeBuffer(this.pass_buffer,	
					this.pass_buffer_stride*i,				new Float32Array(	p[0]));
				this.render.webgpu.device.queue.writeBuffer(this.pass_buffer,
					this.pass_buffer_stride*i+p[0].length*4,new Int32Array(		p[1]));
			}
	}
}