function create_component_object()
{
	this.show_x =0;
	this.show_y =0;

	this.hightlight=[-1,-1,-1,-1];
	
	this.mousedown_flag=false;
	this.mouse_x =render.view.x;
	this.mouse_y =render.view.y;
	this.mousedown_x=render.view.x;
	this.mousedown_y=render.view.y;

	this.x=0;
	this.y=0;
	this.id_base=0;
	this.update_flag=false;
	
	this.set_center=function(dx,dy)
	{
		this.show_x =render.view.x-dx/2;
		this.show_y =render.view.y-dy/2;

		return;
	}
	this.pickupmousedown=function(event,component_id,render)
	{
		switch(event.button){
		case 0:
			this.mousedown_flag=true;
			this.mouse_x 	=render.view.x;
			this.mouse_y 	=render.view.y;
			this.mousedown_x=render.view.x;
			this.mousedown_y=render.view.y;
			break;
		case 2:
			render.caller.call_server_component(component_id,"all",[["operation","hide"]]);
			break;
		default:
			break;
		}
		return true;
	}
	this.pickupmousemove=function(event,component_id,render)
	{
		switch(event.button){
		case 0:
			if(!(this.mousedown_flag))
				break;
			this.show_x+=render.view.x-this.mouse_x;
			this.show_y+=render.view.y-this.mouse_y;
			this.mouse_x=render.view.x;
			this.mouse_y=render.view.y;
			break;
		case 2:
			break;
		default:
			break;
		}
		return true;
	}
	this.pickupmouseup=function(event,component_id,render)
	{
		switch(event.button){
		case 0:
			if(!(this.mousedown_flag))
				break;
			this.mousedown_flag=false;
			
			if(typeof(this.pickupmouseselect)!="function")
				break;
			var dx=this.mousedown_x-render.view.x;
			var dy=this.mousedown_y-render.view.y;
			if((dx*dx+dy*dy)>(2.0*0.01*0.01))
				break;
			this.x=		render.pickup.primitive_id	/(1000.0*1000.0);
			this.y=1.0-	render.pickup.vertex_id		/(1000.0*1000.0);
			
			this.pickupmouseselect(event,component_id,render);
			break;
		case 2:
			break;
		default:
			break;
		}
		return true;
	}
	this.pickupmousewheel=function(event,component_id,render)
	{
		var mouse_wheel_number=0;
		if(typeof(event.wheelDelta)=="number")
			mouse_wheel_number+=event.wheelDelta/200.0;		//for chrome,opera
		else if(typeof(event.detail)=="number")
			mouse_wheel_number-=event.detail/5;				//for firefox
		else
			return;

		var skip_array=[1,	5,	10,	50,	100,	500,	1000,	5000];
	
		this.id_base-=mouse_wheel_number*skip_array[(event.ctrlKey?1:0)+(event.shiftKey?2:0)+(event.altKey?4:0)];
		this.id_base=(this.id_base<0)?0:Math.round(this.id_base);
		this.update_flag=false;

		return true;
	}
	this.destroy=function(gl,ep,component_id)
	{
		this.pickupmousedown		=null;
		this.pickupmouseup			=null;
		this.pickupmousewheel		=null;
	}
}

function create_bind_group(init_data,render_driver,render)
{
	this.is_busy_flag		=true;
	this.should_delete_flag	=false;
	
	this.create=async function(init_data,render_driver,render)
	{
		if(init_data.type){
			this.texture 		=	render.webgpu.device.createTexture(
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
			var my_response 	=	await fetch(init_data.url);
		   	var my_imageBitmap	=	await createImageBitmap(await my_response.blob());
		    
			this.texture 		=	render.webgpu.device.createTexture(
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
			render.webgpu.device.queue.copyExternalImageToTexture(
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
		this.buffer=render.webgpu.device.createBuffer(
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
				resource	:	render.webgpu.device.createSampler(
					{
						addressModeU	:	"mirror-repeat",
						addressModeV	:	"mirror-repeat",
						magFilter		:	"linear",
						minFilter		:	"linear",
						mipmapFilter	:	"linear"
					})
			}
		];
		this.bindgroup=render.webgpu.device.createBindGroup(
			{
				layout		:	render_driver.bindgroup_layout,
				entries		:	resource_entries
			});
		this.is_busy_flag=false;
		if(this.should_delete_flag)
			this.destroy();
	};
	
	this.destroy=function ()
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

	this.create(init_data,render_driver,render);
};

function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	var new_ep=new create_component_object();
	var old_ep=render.component_event_processor[component_id];
	if(typeof(old_ep)=="object")
		new_ep=Object.assign(old_ep,new_ep);
	render.component_event_processor[component_id]=new_ep;
	
	this.init_data			=init_data;
	this.image_bind_group	=new create_bind_group(this.init_data,render_driver,render);
	this.ep					=render.component_event_processor[component_id];

	this.draw_component=function(method_data,render_data,
			render_id,part_id,data_buffer_id,component_id,driver_id,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		if(method_data.method_id!=1)
			return;
		if(this.image_bind_group.is_busy_flag)
			return;
		if(this.ep.update_flag){
			this.ep.update_flag=false;
			if(this.init_data.type)
				if(typeof(this.ep.update_canvas_texture)=="function"){
					render.webgpu.canvas_2d.width	=this.init_data.canvas.canvas_width;
					render.webgpu.canvas_2d.height	=this.init_data.canvas.canvas_height;
					render.webgpu.context_2d.width	=this.init_data.canvas.canvas_width;
					render.webgpu.context_2d.height	=this.init_data.canvas.canvas_height;

					if(this.ep.update_canvas_texture(
						render.webgpu.context_2d,this.init_data.canvas))
							return;
					render.webgpu.device.queue.copyExternalImageToTexture(
						{
							source	:	render.webgpu.canvas_2d
						},
						{
							texture	:	this.image_bind_group.texture
						},
						{
							width	:	this.init_data.canvas.canvas_width,
							height	:	this.init_data.canvas.canvas_height
						});
				}
		}
		
		var x0=this.ep.hightlight[0],y0=this.ep.hightlight[1];
		var x1=this.ep.hightlight[2],y1=this.ep.hightlight[3];
		render.webgpu.device.queue.writeBuffer(this.image_bind_group.buffer,0,
			new Float32Array(
				[	this.ep.show_x,			this.ep.show_y,
					this.init_data.dx,		this.init_data.dy,
					this.init_data.depth,
					(x0<x1)?x0:x1,			(y0<y1)?y0:y1,
					(x0>x1)?x0:x1,			(y0>y1)?y0:y1
				]));

		var rpe=render.webgpu.render_pass_encoder;
		rpe.setPipeline(render_driver.pipeline);		
		render.webgpu.render_pass_encoder.setBindGroup(1,this.image_bind_group.bindgroup);

		var p=part_object.buffer_object.face.region_data;
		for(var i=0,ni=p.length;i<ni;i++){
			rpe.setVertexBuffer(0,p[i].buffer);
			rpe.draw(p[i].item_number);
		}
	};
};
