function create_component_object(my_init_data,scene)
{
	this.interface_data=my_init_data;
	
	this.show_x =0;
	this.show_y =0;

	this.hightlight=[-1,-1,-1,-1];
	
	this.mousedown_flag	=false;
	this.mouse_x 		=scene.view.main_target_x;
	this.mouse_y 		=scene.view.main_target_y;
	this.mousedown_x	=scene.view.main_target_x;
	this.mousedown_y	=scene.view.main_target_y;

	this.x=0;
	this.y=0;
	this.id_base=0;
	this.update_flag=false;
	
	this.parameter_bak={
		x	:	this.show_x,
		y	:	this.show_y,
		dx	:	this.interface_data.dx,
		dy	:	this.interface_data.dy
	}

	this.pickupmousedown=function(event,component_id,scene)
	{
		if(scene.terminate_flag)
			return true;
			
		switch(event.button){
		case 0:
			this.mousedown_flag=true;
			this.mousedown_x=scene.view.main_target_x;
			this.mousedown_y=scene.view.main_target_y;			
			this.mouse_x 	=scene.view.main_target_x;
			this.mouse_y 	=scene.view.main_target_y;
			
			break;
		case 2:
			scene.caller.call_server_component(component_id,"all",[["operation","hide"]]);
			break;
		default:
			break;
		}
		return true;
	};
	this.pickupmousemove=function(event,component_id,scene)
	{
		if(scene.terminate_flag)
			return true;
		switch(event.button){
		case 0:
			if(!(this.mousedown_flag))
				break;
			this.show_x+=scene.view.main_target_x-this.mouse_x;
			this.show_y+=scene.view.main_target_y-this.mouse_y;
			this.mouse_x=scene.view.main_target_x;
			this.mouse_y=scene.view.main_target_y;
			
			break;
		case 2:
			break;
		default:
			break;
		}
		return true;
	}
	this.pickupmouseup=function(event,component_id,scene)
	{
		if(scene.terminate_flag)
			return true;
			
		switch(event.button){
		case 0:
			if(this.mousedown_flag){
				this.mousedown_flag=false;
				if(typeof(this.pickupmouseselect)=="function"){
					var dx=this.mousedown_x-scene.view.main_target_x;
					var dy=this.mousedown_y-scene.view.main_target_y;
					if((dx*dx+dy*dy)<(2.0*0.01*0.01)){
						this.x=scene.pickup.body_id/(1000.0*1000.0);
						this.y=scene.pickup.face_id/(1000.0*1000.0);
						
						this.pickupmouseselect(event,component_id,scene);
					}
				}
			}
			break;
		case 2:
			break;
		default:
			break;
		}
		return true;
	};
	this.pickupmousewheel=function(event,component_id,scene)
	{
		if(scene.terminate_flag)
			return true;
			
		var mouse_wheel_number=0;
		if(typeof(event.wheelDelta)=="number")
			mouse_wheel_number+=event.wheelDelta/200.0;		//for chrome,opera
		else if(typeof(event.detail)=="number")
			mouse_wheel_number-=event.detail/5;				//for firefox
		else
			return true;

		var skip_array=[1,10,100,1000];
	
		this.id_base-=mouse_wheel_number*skip_array[(event.ctrlKey?1:0)+(event.altKey?2:0)];
		this.id_base =(event.shiftKey||(this.id_base<0))?0:Math.round(this.id_base);
		
		this.update_flag=false;

		return true;
	};
}

function create_bind_group(init_data,render_driver,scene)
{
	this.is_busy_flag		=true;
	this.should_delete_flag	=false;
	
	this.create=async function(init_data,render_driver,scene)
	{
		this.texture=null;
		this.buffer=null;
		this.bindgroup=null;
		
		if(scene.terminate_flag)
			return;
			
		if(init_data.type){
			this.texture 		=	scene.webgpu.device.createTexture(
				{
					size:
					{
						width	:	init_data.canvas.canvas_width,
						height	:	init_data.canvas.canvas_height
					},
					format		:	"rgba16float",
					usage		:	 GPUTextureUsage.TEXTURE_BINDING 
									|GPUTextureUsage.COPY_DST
									|GPUTextureUsage.RENDER_ATTACHMENT
		    	});
		}else{
			var my_response 	=	await fetch(init_data.url,scene.fetch_parameter.call_server);
			if(scene.terminate_flag){
				this.is_busy_flag=false;
				return;
			}
		   	var my_imageBitmap	=	await createImageBitmap(await my_response.blob());
		   	if(scene.terminate_flag){
				this.is_busy_flag=false;
				return;
			}
			this.texture 		=	scene.webgpu.device.createTexture(
				{
					size:
					{
						width	:	my_imageBitmap.width,
						height	:	my_imageBitmap.height
					},
					format		:	"rgba16float",
					usage		:	 GPUTextureUsage.TEXTURE_BINDING 
									|GPUTextureUsage.COPY_DST
									|GPUTextureUsage.RENDER_ATTACHMENT
		    	});
			scene.webgpu.device.queue.copyExternalImageToTexture(
				{
					source	:	my_imageBitmap
				},
				{
					texture	:	this.texture
				},
				{
					width	:	my_imageBitmap.width,
					height	:	my_imageBitmap.height
				});
		}	
		this.buffer_size=Float32Array.BYTES_PER_ELEMENT*64;
		this.buffer=scene.webgpu.device.createBuffer(
		{
			size	:	this.buffer_size,
			usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
		});
	
		var resource_entries=[
			{	// system buffer
				binding		:	0,
				resource	:
				{
					buffer	:	this.buffer,
					size	:	this.buffer_size
				}
			},
			{	//texture
				binding		:	1,
				resource	:	this.texture.createView()
			},
			{
				//sampler
				binding		:	2,
				resource	:	scene.webgpu.device.createSampler(
					{
						addressModeU	:	"mirror-repeat",
						addressModeV	:	"mirror-repeat",
						magFilter		:	"linear",
						minFilter		:	"linear",
						mipmapFilter	:	"linear"
					})
			}
		];
		this.bindgroup=scene.webgpu.device.createBindGroup(
			{
				layout		:	render_driver.bindgroup_layout,
				entries		:	resource_entries
			});
		this.is_busy_flag=false;
		if(this.should_delete_flag)
			this.destroy();
	};
	
	this.destroy=function()
	{
		this.should_delete_flag=true;
		if(this.is_busy_flag)
			return;

		if(this.buffer!=null){
			this.buffer.destroy();
			this.buffer=null;
		}
		if(this.texture!=null){
			this.texture.destroy();
			this.texture=null;
		}
	};
	this.create(init_data,render_driver,scene);
};

function construct_component_driver(component_ids,init_data,create_data,part_object,part_driver,render_driver,scene)
{
	this.component_ids				=component_ids;
	
	var new_ep=new create_component_object(init_data,scene);
	var old_ep=scene.component_event_processor[this.component_ids.component_id];
	if((typeof(old_ep)=="object")&&(old_ep!=null))
		new_ep=Object.assign(old_ep,new_ep);
	scene.component_event_processor[this.component_ids.component_id]=new_ep;
	
	
	this.interface_component_id		=this.component_ids.component_id;
	this.image_bind_group			=new create_bind_group(init_data,render_driver,scene);
	this.save_parameter_number		=0;
	
	this.draw_component=function(method_data,render_parameter,
			target_data,part_object,part_driver,render_driver,scene)
	{
		var ep=scene.component_event_processor[this.component_ids.component_id];
		if(this.image_bind_group.is_busy_flag)
			return;
		
		if(ep.interface_data.type)
			if(typeof(ep.update_canvas_texture)=="function")
				if(ep.update_canvas_texture(
						scene.webgpu.canvas_2d,scene.webgpu.context_2d,
						ep.interface_data.canvas,this.component_ids.component_id,scene))
					scene.webgpu.device.queue.copyExternalImageToTexture(
						{
							source	:	scene.webgpu.canvas_2d
						},
						{
							texture	:	this.image_bind_group.texture
						},
						{
							width	:	ep.interface_data.canvas.canvas_width,
							height	:	ep.interface_data.canvas.canvas_height
						});
		if((target_data.main_display_target_flag)||(this.save_parameter_number<=0)){
			if(	  (ep.parameter_bak.x !=ep.show_x)
				||(ep.parameter_bak.y !=ep.show_y)
				||(ep.parameter_bak.dx!=ep.interface_data.dx)
				||(ep.parameter_bak.dy!=ep.interface_data.dy))
			{
				scene.caller.call_server_component(this.component_ids.component_id,"all",[["operation","parameter"],
					["x0",ep.parameter_bak.x =ep.show_x],
					["y0",ep.parameter_bak.y =ep.show_y],
					["dx",ep.parameter_bak.dx=ep.interface_data.dx],
					["dy",ep.parameter_bak.dy=ep.interface_data.dy]]);
			}
			var x0=ep.hightlight[0],y0=ep.hightlight[1];
			var x1=ep.hightlight[2],y1=ep.hightlight[3];
			scene.webgpu.device.queue.writeBuffer(
				this.image_bind_group.buffer,0,
				new Float32Array([
						ep.show_x,
						ep.show_y,
						ep.interface_data.dx,
						ep.interface_data.dy,
						ep.interface_data.depth,
						(x0<x1)?x0:x1,
						(y0<y1)?y0:y1,
						(x0>x1)?x0:x1,
						(y0>y1)?y0:y1
					]));
			this.save_parameter_number++;
		}
		
		var rpe=scene.webgpu.render_pass_encoder;
		rpe.setPipeline((method_data.method_id==0)
				?(render_driver.id_pipeline):(render_driver.color_pipeline));		
		rpe.setBindGroup(1,this.image_bind_group.bindgroup);
		var p=part_object.buffer_object.face.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		}
	};
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)  
	{
		var ep=scene.component_event_processor[this.component_ids.component_id];
		ep.show_x			=buffer_data_item[0];
		ep.show_y			=buffer_data_item[1];
		ep.interface_data.dx=buffer_data_item[2];
		ep.interface_data.dy=buffer_data_item[3];
	};
};
