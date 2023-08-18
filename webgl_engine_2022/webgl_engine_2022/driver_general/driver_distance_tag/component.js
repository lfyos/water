function init_component_event_processor(component_id,init_data,render)
{
	render.component_event_processor[component_id]=
	{
		tag_point_id				:	0,
		tag_menu_component_name		:	init_data,
		mousemove_flag				:	true,

		pickupdblclick		:	function(event,component_id,render)
		{
			return true;
		},
		pickupcontextmenu	:	function(event,component_id,render)
		{
			return true;
		},
		pickupmouseup		:	function(event,component_id,render)
		{
			return true;
		},
		pickupmousedown		:	function(event,component_id,render)
		{
			var pickup_tag_id,ep=render.component_event_processor[component_id];
			if((pickup_tag_id=render.pickup.body_id)<0)
				return true;
			switch(event.button){
			case 0:				
				switch((event.ctrlKey?1:0)+(event.shiftKey?2:0)){
				case 0:	
					switch(ep.tag_point_id){
					case 0:				
						render.caller.call_server_component(component_id,"all",[["operation","locate_component"],
							["id",pickup_tag_id],["type","point"],["p0","true"]]);
						ep.tag_point_id++;
						break;
					case 1:
						render.caller.call_server_component(component_id,"all",[["operation","locate_component"],
							["id",pickup_tag_id],["type","point"],["px","true"]]);
						ep.tag_point_id++;
						break;
					default:
						render.caller.call_server_component(component_id,"all",[["operation","locate_component"],
							["id",pickup_tag_id],["type","point"],["p0","true"],["px","true"]]);
						ep.tag_point_id=0;
						break;
					}
					break;	
				case 1:
					switch(ep.tag_point_id){
					case 0:				
						render.caller.call_server_component(component_id,"all",[["operation","locate_component"],
							["id",pickup_tag_id],["type","component"],["p0","true"]]);
						ep.tag_point_id++;
						break;
					case 1:
						render.caller.call_server_component(component_id,"all",[["operation","locate_component"],
							["id",pickup_tag_id],["type","component"],["px","true"]]);
						ep.tag_point_id++;
						break;
					default:
						render.caller.call_server_component(component_id,"all",[["operation","locate_component"],
							["id",pickup_tag_id],["type","component"],["p0","true"],["px","true"]]);
						ep.tag_point_id=0;
						break;
					}
					break;
				case 2:
					render.caller.call_server_component(component_id,"all",
						[["operation","swap_component"],["id",pickup_tag_id],["p0","true"]]);
					break;
				case 3:
					render.caller.call_server_component(component_id,"all",
						[["operation","swap_component"],["id",pickup_tag_id],["px","true"]]);
					break;
				}
				break;
			case 1:
				break;
			case 2:
				render.system_call_processor.set_menu_show(ep.tag_menu_component_name);
				break;
			}
			return true;
		},
		pickupkeydown		:	function(event,component_id,render)
		{
			var pickup_tag_id,ep=render.component_event_processor[component_id];
			if((pickup_tag_id=render.pickup.body_id)<0)
				return true;
			switch(event.keyCode){
			case 46://del
			case  8://backspace
				render.caller.call_server_component(
					component_id,"all",[["operation","clear"],["id",pickup_tag_id]]);
				break;
			}
			return true;
		},
		mousemove			:	function(event,component_id,render)
		{	
			var ep=render.component_event_processor[component_id];
			if(ep.mousemove_flag){
				ep.mousemove_flag=false;
				render.caller.call_server_component(component_id,"all",[["operation","touch"]]).
				then(
					function(response_data)
					{
						ep.mousemove_flag=true;
					});
			}
			return true;
		},
		mousedown			:	function(event,component_id,render)
		{
			switch(event.button){
			case 0:
				render.caller.call_server_component(component_id,"all",[["operation","mark"]]);
				break;
			case 1:
			case 2:
				break;
			}
			return true;
		},
		destroy				:	function()
		{
			this.tag_menu_component_name=null;
			this.pickupdblclick			=null;
			this.pickupcontextmenu		=null;
			this.pickupmouseup			=null;
			this.pickupmousedown		=null;
			this.pickupkeydown			=null;
			this.mousemove				=null;
			this.mousedown				=null;
		}
	};
}
function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.component_id=component_id;
	
	this.tag_array=new Array();
	init_component_event_processor(component_id,init_data,render);

	this.draw_component=function(method_data,render_data,
			render_id,part_id,data_buffer_id,component_id,driver_id,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		var p,rpe=render.webgpu.render_pass_encoder;
		
		for(var i=0,ni=this.tag_array.length;i<ni;i++){
			rpe.setBindGroup(1,this.tag_array[i].bindgroup);
			switch(method_data.method_id){
			case 0:
				rpe.setPipeline(render_driver.id_pipeline);
				p=part_object.buffer_object.face.region_data;
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
		for(var i=0,ni=this.tag_array.length;i<ni;i++){
			this.tag_array[i].buffer.destroy();
			this.tag_array[i].texture.destroy();
			this.tag_array[i].buffer	=null;
			this.tag_array[i].texture	=null;
			this.tag_array[i].bindgroup	=null;
		}
		this.tag_array=new Array();
		for(var i=0,ni=buffer_data_item.length;i<ni;i++){
			var my_tag_text							=buffer_data_item[i][0].trim();
			var my_p0_x								=buffer_data_item[i][1];
			var my_p0_y								=buffer_data_item[i][2];
			var my_p0_z								=buffer_data_item[i][3];
			var my_dx_x								=buffer_data_item[i][4];
			var my_dx_y								=buffer_data_item[i][5];
			var my_dx_z								=buffer_data_item[i][6];
			var my_dy_x								=buffer_data_item[i][7];
			var my_dy_y								=buffer_data_item[i][8];
			var my_dy_z								=buffer_data_item[i][9];
			var can_do_selection_flag				=(buffer_data_item[i][10]>0.5)?true:false;
			
			var my_texture_width					=part_object.material[0].canvas_width;
			var my_texture_height					=part_object.material[0].canvas_height;
			var my_height_adjust					=part_object.material[0].height_adjust;
			
			render.webgpu.canvas_2d.width			=my_texture_width;
			render.webgpu.canvas_2d.height			=my_texture_height;

			render.webgpu.context_2d.font			=part_object.material[0].font;
			render.webgpu.context_2d.textBaseline	="middle";
			render.webgpu.context_2d.textAlign		="left";
			var real_texture_width					=render.webgpu.context_2d.measureText(my_tag_text).width;

			render.webgpu.context_2d.fillStyle		="rgb(0,0,0)";
			render.webgpu.context_2d.fillRect(0,0,real_texture_width,my_texture_height);
			render.webgpu.context_2d.fillStyle		="rgb(255,255,255)";
			render.webgpu.context_2d.fillText(my_tag_text,0,my_texture_height/2);
			
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
			var system_id=render.component_array_sorted_by_id[component_id].component_ids[driver_id][3];
			var integer_buffer_data=[
					can_do_selection_flag?component_id	:-2,
					can_do_selection_flag?driver_id		:-2,
					can_do_selection_flag?i				:-2,
					can_do_selection_flag?system_id		:-2
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
				
				my_p0_x,		my_p0_y,		my_p0_z,		1.0,
				my_dx_x,		my_dx_y,		my_dx_z,		0.0,
				my_dy_x,		my_dy_y,		my_dy_z,		0.0,
				
				part_object.material[0].width_scale	*real_texture_width,
				part_object.material[0].height_scale*my_texture_height,
				my_height_adjust,
				0.0
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
			this.tag_array[i]={
				texture				:	my_texture,
				buffer				:	my_buffer,
				bindgroup			:	my_bindgroup
			};
		};
	};
	
	this.destroy=function(render)
	{
		if(render.component_event_processor[this.component_id]!=null){
			render.component_event_processor[this.component_id].destroy();
			render.component_event_processor[this.component_id]=null;
		}	
		if(this.tag_array!=null){
			for(var i=0,ni=this.tag_array.length;i<ni;i++){
				this.tag_array[i].buffer.destroy();
				this.tag_array[i].texture.destroy();
				this.tag_array[i].buffer	=null;
				this.tag_array[i].texture	=null;
				this.tag_array[i].bindgroup	=null;
			}
			this.tag_array=null;
		}
		this.append_component_parameter		=null;
		this.draw_component					=null;
	};
};
