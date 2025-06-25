function construct_component_driver(component_ids,init_data,part_object,part_driver,render_driver,scene)
{
	this.main_target_id	=-1;
	this.component_ids	=component_ids;
	
	this.id_depth_texture=scene.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"depth24plus-stencil8",
			usage	:	GPUTextureUsage.RENDER_ATTACHMENT
		});
	this.id_texture_0=scene.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"rgba32sint",
			usage	:	GPUTextureUsage.COPY_SRC|GPUTextureUsage.COPY_DST|GPUTextureUsage.RENDER_ATTACHMENT
		});	
	this.id_texture_1=scene.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"rgba32sint",
			usage	:	GPUTextureUsage.COPY_SRC|GPUTextureUsage.COPY_DST|GPUTextureUsage.RENDER_ATTACHMENT
		});	
	this.id_buffer_0	=scene.webgpu.device.createBuffer(
		{
			size	:	Int32Array.BYTES_PER_ELEMENT*4,
			usage	:	GPUBufferUsage.MAP_READ|GPUBufferUsage.COPY_DST
		});	
	this.id_buffer_1	=scene.webgpu.device.createBuffer(
		{
			size	:	Int32Array.BYTES_PER_ELEMENT*4,
			usage	:	GPUBufferUsage.MAP_READ|GPUBufferUsage.COPY_DST
		});	
/////////////////////////////////////////////////////////////////////////////////

	this.value_depth_texture=scene.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"depth24plus-stencil8",
			usage	:	GPUTextureUsage.RENDER_ATTACHMENT
		});
	this.value_texture=scene.webgpu.device.createTexture(
		{
			size	:
			{
				width	:	1,
				height	:	1
			},
			format	:	"rgba32float",
			usage	:	GPUTextureUsage.COPY_SRC|GPUTextureUsage.COPY_DST|GPUTextureUsage.RENDER_ATTACHMENT
		});	
	this.value_buffer=scene.webgpu.device.createBuffer(
		{
			size	:	Float32Array.BYTES_PER_ELEMENT*4,
			usage	:	GPUBufferUsage.MAP_READ|GPUBufferUsage.COPY_DST
		});	

//////////////////////////////////////////////////////////////////////////

	this.caculate_view_volume_box=function(my_target_id,main_target_id,scene)
	{
		var my_render_data=scene.render_buffer_array[my_target_id];
		
		my_render_data.target_view_parameter.view_x0			=0;
		my_render_data.target_view_parameter.view_y0			=0;
		my_render_data.target_view_parameter.view_width			=1;
		my_render_data.target_view_parameter.view_height		=1;
		my_render_data.target_view_parameter.whole_view_width	=1;
		my_render_data.target_view_parameter.whole_view_height	=1;
		
		var p=scene.render_buffer_array[main_target_id].target_view_parameter;
		
		var view_x=0.5*(scene.view.x+1.0)*p.whole_view_width;
		var view_y=0.5*(scene.view.y+1.0)*p.whole_view_height;
		var center_x=p.view_x0+0.5*p.view_width;
		var center_y=p.view_y0+0.5*p.view_height;
		var pos_x=2.0*(view_x-center_x)/p.view_width;
		var pos_y=2.0*(view_y-center_y)/p.view_height;
		
		var aspect_value=p.view_width/p.view_height;
		var diff_value	=0.5/p.view_height;
		
		my_render_data.view_volume_box=[
			[aspect_value*pos_x-diff_value,	pos_y-diff_value,	-1,	1],
			[aspect_value*pos_x+diff_value,	pos_y+diff_value,	 1,	1]
		];
	}

	this.begin_scene_target=function(scene_target_array,render_data,
			target_part_object,target_part_driver,target_render_driver,scene)
	{
		if(this.main_target_id<0)
			return render_data.target_id;
		
		this.caculate_view_volume_box(render_data.target_id,this.main_target_id,scene);
		
		var my_pass_descriptor;
		if(scene_target_array.length>1)
			return render_data.target_id;
		
		if((typeof(scene_target_array[0])!="object")||(scene_target_array[0]==null)){
			my_pass_descriptor=
			{
				colorAttachments		: 
				[
					{
						view			:	this.id_texture_0.createView(),
						clearValue		:	{ r: -1, g: -1, b: -1, a: -1 },
						loadOp			:	"clear",
						storeOp			:	"store"
					},
					{
						view			:	this.id_texture_1.createView(),
						clearValue		:	{ r: -1, g: -1, b: -1, a: -1 },
						loadOp			:	"clear",
						storeOp			:	"store"
					}
				],
				depthStencilAttachment	:
				{
					view				:	this.id_depth_texture.createView(),
					depthClearValue		:	1.0,
					depthLoadOp			:	"clear",
					depthStoreOp		:	"store",
					
					stencilClearValue	:	0,
		   			stencilLoadOp		:	"clear",
		   			stencilStoreOp		:	"store"
				}
			};
			
			scene_target_array[0]={
				pass_descriptor	:	my_pass_descriptor,
				
				method_array	:
				[
					{
						method_id:	0
					}
				]
			};
		}
		if((typeof(scene_target_array[1])!="object")||(scene_target_array[1]==null)){
			my_pass_descriptor=
			{
				colorAttachments		: 
				[
					{
						view			:	this.value_texture.createView(),
						clearValue		:	{ r:0.0,g:0.0,b:0.0,a:1.0 },
						loadOp			:	"clear",
						storeOp			:	"store"
					}
				],
				depthStencilAttachment	:
				{
					view				:	this.value_depth_texture.createView(),
					depthClearValue		:	1.0,
					depthLoadOp			:	"clear",
					depthStoreOp		:	"store",
					
					stencilClearValue	:	0,
		   			stencilLoadOp		:	"clear",
		   			stencilStoreOp		:	"store"
				}
			};
			scene_target_array[1]={
				pass_descriptor	:	my_pass_descriptor,
				method_array	:
				[
					{
						method_id:	1
					}
				]
			};
		};
		return (scene.view.far_distance_pickup_flag)?(this.main_target_id):(render_data.target_id);
	}
	this.end_scene_target=function(	scene_target_array,render_data,
			target_part_object,target_part_driver,target_render_driver,scene)
	{	
		scene.webgpu.command_encoder.copyTextureToBuffer(
			{	//source
				texture	:	this.id_texture_0,
				origin	:	{
					x	:	0,
					y	:	0
				}
			},
			{	//destination
				buffer			:	this.id_buffer_0,
				offset			:	0,
	    		bytesPerRow		:	Int32Array.BYTES_PER_ELEMENT*16*4,
	    		rowsPerImage	:	1
			},
			{	//copysize
				width	:	1,
				height	:	1
			});
		scene.webgpu.command_encoder.copyTextureToBuffer(
			{	//source
				texture	:	this.id_texture_1,
				origin	:	{
					x	:	0,
					y	:	0
				}
			},
			{	//destination
				buffer			:	this.id_buffer_1,
				offset			:	0,
	    		bytesPerRow		:	Int32Array.BYTES_PER_ELEMENT*16*4,
	    		rowsPerImage	:	1
			},
			{	//copysize
				width	:	1,
				height	:	1
			});
		scene.webgpu.command_encoder.copyTextureToBuffer(
			{	//source
				texture	:	this.value_texture,
				origin	:	{
					x	:	0,
					y	:	0
				}
			},
			{	//destination
				buffer			:	this.value_buffer,
				offset			:	0,
	    		bytesPerRow		:	Float32Array.BYTES_PER_ELEMENT*16*4,
	    		rowsPerImage	:	1
			},
			{	//copysize
				width	:	1,
				height	:	1
			});
	}

	this.complete_render_target=async function(render_data,
		target_part_object,target_part_driver,target_render_driver,scene)
	{
		if(scene.terminate_flag)
			return;

		var my_buffer=this.id_buffer_0;
		var my_length=Int32Array.BYTES_PER_ELEMENT*4;
		try{
			await my_buffer.mapAsync(GPUMapMode.READ,0,my_length);
		}catch(e){
			return;
		}
		var p_id_0=new Int32Array(my_buffer.getMappedRange(0,my_length).slice());
		my_buffer.unmap();
		
		if(scene.terminate_flag)
			return;
		
		var my_buffer=this.id_buffer_1;
		var my_length=Int32Array.BYTES_PER_ELEMENT*4;
		try{
			await my_buffer.mapAsync(GPUMapMode.READ,0,my_length);
		}catch(e){
			return;
		}
		var p_id_1=new Int32Array(my_buffer.getMappedRange(0,my_length).slice());
		my_buffer.unmap();
		
		if(scene.terminate_flag)
			return;
		
		var my_buffer=this.value_buffer;
		var my_length=Float32Array.BYTES_PER_ELEMENT*4;
		try{
			await my_buffer.mapAsync(GPUMapMode.READ,0,my_length);
		}catch(e){
			return;
		}
		var p_value=new Float32Array(my_buffer.getMappedRange(0,my_length).slice());
		my_buffer.unmap();
		
		if(scene.terminate_flag)
			return;
	
		var my_system_bindgroup_id	=p_id_0[0];
		var my_body_id				=p_id_0[1];
		var my_face_id				=p_id_0[2];
		var my_scene_id				=p_id_0[3]>>4;
		var my_primitive_type_id	=p_id_0[3]%16;
		
		var my_loop_id				=p_id_1[0];
		var my_edge_id				=p_id_1[1];
		var my_primitive_id			=p_id_1[2];
		var my_vertex_id			=p_id_1[3];
		
		var my_value				=[p_value[0],p_value[1],p_value[2]];
		var my_depth				=p_value[3];

		scene.pickup.render_id			=-1;
		scene.pickup.part_id			=-1;
		scene.pickup.data_buffer_id		=-1;
		scene.pickup.component_id		=-1;
		scene.pickup.driver_id			=-1;
		
		scene.pickup.primitive_type_id	=my_primitive_type_id;
		
		scene.pickup.body_id			=-1;
		scene.pickup.face_id			=-1;
		
		scene.pickup.loop_id			=-1;
		scene.pickup.edge_id			=-1;
		scene.pickup.primitive_id		=-1;
		scene.pickup.vertex_id			=-1;
		
		scene.pickup.value				=[0,0,0];
		scene.pickup.depth				=1.0;
		
		if(scene.scene_id!=my_scene_id)
			return;
		if(my_system_bindgroup_id<0)
			return;
		if(my_system_bindgroup_id>=scene.system_bindgroup_id.length)
			return;
		var p=scene.system_bindgroup_id[my_system_bindgroup_id];
		if((p.render_id<0)||(p.part_id<0))
			return;
		if(p.render_id>=scene.part_array.length)
			return;
		if(p.part_id>=scene.part_array[p.render_id].length)
			return;
		
		scene.pickup.render_id		=p.render_id;
		scene.pickup.part_id		=p.part_id;
						
		scene.pickup.data_buffer_id	=p.data_buffer_id;
		scene.pickup.component_id	=p.component_id;
		scene.pickup.driver_id		=p.driver_id;
						
		scene.pickup.body_id		=my_body_id;
		scene.pickup.face_id		=my_face_id;
		scene.pickup.loop_id		=my_loop_id;
		scene.pickup.edge_id		=my_edge_id;
		scene.pickup.primitive_id	=my_primitive_id;
		scene.pickup.vertex_id		=my_vertex_id;
							
		scene.pickup.value			=my_value;
		scene.pickup.depth			=my_depth;
		
		return;
	}
	
	this.draw_component=function(method_data,render_parameter,
			target_data,part_object,part_driver,render_driver,scene)
	{
		
	}
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)  
	{
		this.main_target_id	=buffer_data_item;
	}
	this.destroy=function()
	{
		if(this.id_depth_texture!=null){
			this.id_depth_texture.destroy();
			this.id_depth_texture=null;
		}
		if(this.id_texture_0!=null){
			this.id_texture_0.destroy();
			this.id_texture_0=null;	
		}
		if(this.id_texture_1!=null){	
			this.id_texture_1.destroy();
			this.id_texture_1=null;
		}
		if(this.id_buffer_0!=null){	
			this.id_buffer_0.destroy();
			this.id_buffer_0=null;
		}
		if(this.id_buffer_1!=null){	
			this.id_buffer_1.destroy();
			this.id_buffer_1=null;
		}
		if(this.value_depth_texture!=null){
			this.value_depth_texture.destroy();
			this.value_depth_texture=null;
		}
		if(this.value_texture!=null){
			this.value_texture.destroy();
			this.value_texture=null;
		}
		if(this.value_buffer!=null){	
			this.value_buffer.destroy();
			this.value_buffer=null;
		}
	}
};