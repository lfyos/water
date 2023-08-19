function init_component_event_processor()
{
	this.title="marker";
	
	this.mouseup=function(event,component_id,render)
	{
		var value;
		if(render.pickup.component_id<0)
			return true;
		if((value=prompt("输入标注文字"))==null)
			return true;
		if((value=value.trim()).length<=0)
			return true;
		render.caller.call_server_component(component_id,"all",[["operation","append"],
				["value",encodeURIComponent(encodeURIComponent(value))]]);
		return true;
	};
	this.pickupkeydown=function(event,component_id,render)
	{
		switch(event.keyCode){
		case 46://del
		case  8://backspace
			render.caller.call_server_component(component_id,"all",[["operation","delete"]]);
			break;
		}
		return true;
	};
	this.pickupdblclick=function(event,component_id,render)
	{
		return true;
	};
	this.pickupmouseup=function(event,component_id,render)
	{
		return true;
	};
	this.pickupmousedown=function(event,component_id,render)
	{
		switch(event.button){
		case 0:
			render.caller.call_server_component(component_id,"all",
				[["operation",(event.ctrlKey||event.shiftKey||event.altKey)?"locate":"swap_select"]]);
			break;
		case 1:
			break;
		case 2:
			render.caller.call_server_component(component_id,"all",[["operation","delete"]]);
			break;
		}
		return true;
	};
	this.pickupkeydown=function(event,component_id,render)
	{
		switch(event.keyCode){
		case 46://del
		case  8://backspace
			render.caller.call_server_component(component_id,"all",[["operation","delete"]]);
			break;
		}
		return true;
	};
	this.destroy=function()
	{
		this.title			=null;
	
		this.mouseup		=null;
		this.pickupkeydown	=null;
		this.pickupdblclick	=null;
		this.pickupmouseup	=null;
		this.pickupmousedown=null;
		this.pickupkeydown	=null;
	}
}
function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.component_id=component_id;
	this.marker_array=new Array();
	render.component_event_processor[component_id]=new init_component_event_processor();
	
	this.draw_component=function(method_data,render_data,
			render_id,part_id,data_buffer_id,component_id,driver_id,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		var p,rpe=render.webgpu.render_pass_encoder;
		
		for(var i=0,ni=this.marker_array.length;i<ni;i++){
			render.set_system_bindgroup(
				render_data.render_buffer_id,method_data.method_id,
				this.marker_array[i].marker_component_id,-1);
			
			rpe.setBindGroup(1,this.marker_array[i].bindgroup);

			switch(method_data.method_id){
			default:
				break;
			case 0:
				rpe.setPipeline(render_driver.face_id_pipeline);
				p=part_object.buffer_object.face.region_data;
				for(var j=0,nj=p.length;j<nj;j++){
					rpe.setVertexBuffer(0,p[j].buffer);
					rpe.draw(p[j].item_number);
				}
				
				rpe.setPipeline(render_driver.point_id_pipeline);
				p=part_object.buffer_object.point.region_data;
				for(var j=0,nj=p.length;j<nj;j++){
					rpe.setVertexBuffer(0,p[j].buffer);
					rpe.draw(p[j].item_number);
				}
				break;
			case 2:
				rpe.setPipeline(render_driver.face_pipeline);
				p=part_object.buffer_object.face.region_data;
				for(var j=0,nj=p.length;j<nj;j++){
					rpe.setVertexBuffer(0,p[j].buffer);
					rpe.draw(p[j].item_number);
				}
	
				rpe.setPipeline(render_driver.edge_pipeline);
				p=part_object.buffer_object.edge.region_data;
				for(var j=0,nj=p.length;j<nj;j++){
					rpe.setVertexBuffer(0,p[j].buffer);
					rpe.draw(p[j].item_number);
				}
	
				rpe.setPipeline(render_driver.point_pipeline);
				p=part_object.buffer_object.point.region_data;
				for(var j=0,nj=p.length;j<nj;j++){
					rpe.setVertexBuffer(0,p[j].buffer);
					rpe.draw(p[j].item_number);
				}
				break;
			}
		}
	}
	this.append_component_parameter=function(
		component_id,	driver_id,			render_id,			part_id,
		data_buffer_id,	buffer_data_item,	buffer_data_array,
		part_object,	part_driver,		render_driver,		render)
	{
		for(var i=0,ni=this.marker_array.length;i<ni;i++){
			this.marker_array[i].buffer.destroy();
			this.marker_array[i].texture.destroy();
			this.marker_array[i].buffer		=null;
			this.marker_array[i].texture	=null;
			this.marker_array[i].bindgroup	=null;
		}
		this.marker_array=new Array();
		for(var i=0,ni=buffer_data_item.length;i<ni;i++){
			var my_marker_component_id				=buffer_data_item[i][0];
			var my_marker_x							=buffer_data_item[i][1];
			var my_marker_y							=buffer_data_item[i][2];
			var my_marker_z							=buffer_data_item[i][3];
			var my_marker_text						=buffer_data_item[i][4].trim();
			
			var my_texture_width					=part_object.material[0].canvas_width;
			var my_texture_height					=part_object.material[0].canvas_height;
			
			render.webgpu.canvas_2d.width			=my_texture_width;
			render.webgpu.canvas_2d.height			=my_texture_height;

			render.webgpu.context_2d.font			=part_object.material[0].font;
			render.webgpu.context_2d.textBaseline	="middle";
			render.webgpu.context_2d.textAlign		="left";
			var real_texture_width					=render.webgpu.context_2d.measureText(my_marker_text).width;

			render.webgpu.context_2d.fillStyle		="rgb(0,0,0)";
			render.webgpu.context_2d.fillRect(0,0,real_texture_width,my_texture_height);
			render.webgpu.context_2d.fillStyle		="rgb(255,255,255)";
			render.webgpu.context_2d.fillText(my_marker_text,0,my_texture_height/2);
			
			var my_texture=render.webgpu.device.createTexture(
					{
						size:
						{
							width	:	real_texture_width,
							height	:	my_texture_height
						},
						format		:	"rgba16float",
						usage		:	 GPUTextureUsage.TEXTURE_BINDING 
										|GPUTextureUsage.COPY_DST
										|GPUTextureUsage.RENDER_ATTACHMENT
			    	});
	    	render.webgpu.device.queue.copyExternalImageToTexture(
					{
						source	:	render.webgpu.canvas_2d
					},
					{
						texture	:	my_texture
					},
					{
						width	:	real_texture_width,
						height	:	my_texture_height
					});

			var integer_buffer_data=[
					component_id,
					driver_id,
					i,
					render.component_array_sorted_by_id[component_id].component_ids[driver_id][3]	
			];
			var p0=part_object.material[0].face_normal_color;
			var p1=part_object.material[0].face_pickup_color;
			var p2=part_object.material[0].edge_normal_color;
			var p3=part_object.material[0].edge_pickup_color;
			var p4=part_object.material[0].point_normal_color;
			var p5=part_object.material[0].point_pickup_color;
			var float_buffer_data=[
				p0[0],			p0[1],			p0[2],			p0[3],
				p1[0],			p1[1],			p1[2],			p1[3],
				
				p2[0],			p2[1],			p2[2],			p2[3],
				p3[0],			p3[1],			p3[2],			p3[3],
				
				p4[0],			p4[1],			p4[2],			p4[3],
				p5[0],			p5[1],			p5[2],			p5[3],
				
				my_marker_x,	my_marker_y,	my_marker_z,	1.0,
				
				part_object.material[0].width_scale	*real_texture_width,
				part_object.material[0].height_scale*my_texture_height,
				part_object.material[0].edge_scale,
				
				part_object.material[0].point_size
			];
			
			var my_buffer_integer_size	=Int32Array.  BYTES_PER_ELEMENT*integer_buffer_data.length;	
			var my_buffer_float_size	=Float32Array.BYTES_PER_ELEMENT*float_buffer_data.length;
			var my_buffer_size			=my_buffer_float_size+my_buffer_integer_size;
			var my_buffer=render.webgpu.device.createBuffer({
						size	:	my_buffer_size,
						usage	:	GPUBufferUsage.UNIFORM|GPUBufferUsage.COPY_DST
					});
			render.webgpu.device.queue.writeBuffer(my_buffer,0,						new Int32Array(integer_buffer_data));
			render.webgpu.device.queue.writeBuffer(my_buffer,my_buffer_integer_size,new Float32Array(float_buffer_data));
			
			var resource_entries=[
					{	//buffer
						binding		:	0,
						resource	:
							{
								buffer	:	my_buffer,
								size	:	my_buffer_size
							}
					},
					{	//texture
						binding		:	1,
						resource	:	my_texture.createView()
					},
					{
						//sampler
						binding		:	2,
						resource	:	render.webgpu.device.createSampler(
							{
								addressModeU	:	"mirror-repeat",
								addressModeV	:	"mirror-repeat",
								magFilter		:	"nearest",
								minFilter		:	"nearest",
								mipmapFilter	:	"nearest"
							})
					}
			];
			var my_bindgroup=render.webgpu.device.createBindGroup(
			{
				layout	:	render_driver.bindgroup_layout,
				entries	:	resource_entries
			});
			
			this.marker_array[i]={
				marker_component_id	:	my_marker_component_id,
				texture				:	my_texture,
				buffer				:	my_buffer,
				bindgroup			:	my_bindgroup
			};
		};
	};

	this.destroy=function(render)
	{
		if(this.marker_array!=null){
			for(var i=0,ni=this.marker_array.length;i<ni;i++){
				this.marker_array[i].buffer.destroy();
				this.marker_array[i].texture.destroy();
				this.marker_array[i].buffer		=null;
				this.marker_array[i].texture	=null;
				this.marker_array[i].bindgroup	=null;
			}
			this.marker_array=null;
		}
		if(render.component_event_processor[this.component_id]!=null){
			render.component_event_processor[this.component_id].destroy(render);
			render.component_event_processor[this.component_id]=null;
		}
	}
};
