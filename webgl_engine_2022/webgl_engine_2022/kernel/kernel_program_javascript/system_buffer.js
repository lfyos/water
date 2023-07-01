function construct_system_buffer(component_number,my_render)
{
	this.render					=my_render;
	
	this.ids_buffer_stride		=32;
	this.component_buffer_stride=64;
	this.method_buffer_stride	=4;
	this.method_buffer_number	=1024;
	this.render_buffer_stride	=2048;
	this.system_buffer_stride	=256;
	
	this.render_buffer_data		=new Array(1);	
	this.identify_matrix		=[	1,	0,	0,	0,		0,	1,	0,	0,		0,	0,	1,	0,		0,	0,	0,	1];
	
	this.ids_buffer=null;
	
	this.component_buffer=this.render.webgpu.device.createBuffer(
		{
			size	:	this.component_buffer_stride*component_number,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
	var buffer_data=new Array(this.identify_matrix.length*component_number);
	for(var k=0,i=0;i<component_number;i++)
		for(var j=0,nj=this.identify_matrix.length;j<nj;j++)
			buffer_data[k++]=this.identify_matrix[j];
	this.render.webgpu.device.queue.writeBuffer(this.component_buffer,0,new Float32Array(buffer_data));
				
	this.method_buffer	=this.render.webgpu.device.createBuffer(
		{
			size	:	this.method_buffer_stride*this.method_buffer_number,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
	buffer_data=new Array(this.method_buffer_number);
	for(var i=0;i<this.method_buffer_number;i++)
		buffer_data[i]=i;
	this.render.webgpu.device.queue.writeBuffer(this.method_buffer,0,new Int32Array(buffer_data));	
			
	this.render_buffer	=this.render.webgpu.device.createBuffer(
		{
			size	:	this.render_buffer_stride,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});	
	this.system_buffer	=this.render.webgpu.device.createBuffer(
		{
			size	:	this.system_buffer_stride,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
	this.system_bindgroup_layout=this.render.webgpu.device.createBindGroupLayout({
			entries:[
				{	//component ids
					binding		:	0,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	true
					}
				},
				{	//component location
					binding		:	1,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	true
					}
				},
				{	//pass buffer
					binding		:	2,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	true
					}
				},
				{	//target buffer
					binding		:	3,
					visibility	:	GPUShaderStage.VERTEX|GPUShaderStage.FRAGMENT,
					buffer		:	{
						type				:	"uniform",
						hasDynamicOffset	:	true
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
	this.system_bindgroup=null;

	this.destroy=function()
	{
		this.render=null;
		this.render_buffer_data	=null;
		
		if(this.ids_buffer!=null){
			this.ids_buffer.destroy();
			this.ids_buffer=null;
		}
		if(this.component_buffer!=null){
			this.component_buffer.destroy();
			this.component_buffer=null;
		}
		if(this.method_buffer!=null){
			this.method_buffer.destroy();
			this.method_buffer=null;
		}
		if(this.render_buffer!=null){
			this.render_buffer.destroy();
			this.render_buffer=null;
		}
		if(this.system_buffer!=null){
			this.system_buffer.destroy();
			this.system_buffer=null;
		}
		
		this.system_bindgroup_layout=null;
		this.system_bindgroup		=null;
		
		this.set_bindgroup			=null;
		this.set_system_buffer		=null;
		this.set_render_buffer		=null;
	};

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
			
			this.render.webgpu.canvas.width,
			this.render.webgpu.canvas.height
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
		this.render.webgpu.device.queue.writeBuffer(this.system_buffer,0,					new Int32Array(int_data));
		this.render.webgpu.device.queue.writeBuffer(this.system_buffer,int_data.length*4,	new Float32Array(float_data));
	};

	this.set_render_buffer=function(render_buffer_id,project_matrix)
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
			
			project_matrix.view_volume_box[0],
			project_matrix.view_volume_box[1]
		];
		
		var float_data=[
			project_matrix.half_fovy_tanl,
			project_matrix.near_value_ratio,
			project_matrix.far_value_ratio,
			
			project_matrix.distance,
			project_matrix.near_value,
			project_matrix.far_value
		];

		var int_data=[
			project_matrix.projection_type_flag?1:0
		];

		var matrix_data=new Array();
		for(var i=0,ni=matrix_array.length;i<ni;i++)
			matrix_data=matrix_data.concat(matrix_array[i]);
		
		var vectory_data=new Array();
		for(var i=0,ni=vector_array.length;i<ni;i++)
			vectory_data=vectory_data.concat(vector_array[i]);
			
		float_data=matrix_data.concat(vectory_data.concat(float_data));
		
		var begin_pointer,end_pointer;
		if(render_buffer_id<this.render_buffer_data.length){
			this.render_buffer_data[render_buffer_id]=[float_data,int_data];
			begin_pointer=render_buffer_id;
			end_pointer=render_buffer_id;
		}else{
			this.render_buffer_data[render_buffer_id]=[float_data,int_data];
			this.render_buffer.destroy();
			this.render_buffer	=this.render.webgpu.device.createBuffer(
					{
						size	:	this.render_buffer_stride*this.render_buffer_data.length,
						usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
					});
			begin_pointer=0;
			end_pointer=this.render_buffer_data.length-1;
			this.system_bindgroup=null;
		}
		for(var p,i=begin_pointer;i<=end_pointer;i++)
			if(typeof(p=this.render_buffer_data[i])!="undefined"){
				this.render.webgpu.device.queue.writeBuffer(this.render_buffer,	
					this.render_buffer_stride*i,				new Float32Array(	p[0]));
				this.render.webgpu.device.queue.writeBuffer(this.render_buffer,
					this.render_buffer_stride*i+p[0].length*4,	new Int32Array(		p[1]));
			}
	};
	
	this.set_bindgroup=function(bind_id,render_buffer_id,render_id,part_id,buffer_id,method_id)
	{
		if(this.render.webgpu.command_encoder==null)
			return this.identify_matrix;
		if(this.render.webgpu.render_pass_encoder==null)
			return this.identify_matrix;
		if(this.system_bindgroup==null){
			this.system_bindgroup=this.render.webgpu.device.createBindGroup({
				layout	:	this.system_bindgroup_layout,
				entries	:	[
					{
						binding		:	0,
						resource	:	{
							buffer	:	this.ids_buffer
						}
					},
					{
						binding		:	1,
						resource	:	{
							buffer	:	this.component_buffer
						}
					},
					{
						binding		:	2,
						resource	:	{
							buffer	:	this.method_buffer
						}
					},
					{
						binding		:	3,
						resource	:	{
							buffer	:	this.render_buffer
						}
					},
					{
						binding		:	4,
						resource	:	{
							buffer	:	this.system_buffer
						}
					}
				]
			});
		};
		var component_id	=this.render.part_component_id_and_driver_id[render_id][part_id][buffer_id][0];
		var driver_id		=this.render.part_component_id_and_driver_id[render_id][part_id][buffer_id][1];
		var ids_id			=this.render.component_render_id_and_part_id[component_id][driver_id][3];
		var loca=this.render.component_location_data.get_component_location(component_id);
		loca=(loca!=null)?loca:this.identify_matrix;
			
		if(this.render.component_location_data.component[component_id].uniform_modify_flag){
			this.render.component_location_data.component[component_id].uniform_modify_flag=false;
			this.render.webgpu.device.queue.writeBuffer(this.component_buffer,
							this.component_buffer_stride*component_id,new Float32Array(loca));
		}

		this.render.webgpu.render_pass_encoder.setBindGroup(
			bind_id,this.system_bindgroup,
			[
				this.ids_buffer_stride			*ids_id,
				this.component_buffer_stride	*component_id,
				this.method_buffer_stride		*method_id,
				this.render_buffer_stride		*render_buffer_id
			]);
		return loca;
	}
}