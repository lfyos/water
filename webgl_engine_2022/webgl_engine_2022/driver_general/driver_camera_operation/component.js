function construct_event_listener()
{
	this.alf="0090";

	this.pickupcontextmenu=function(event,component_id,render)
	{
		return true;
	};
	this.pickupdblclick=function(event,component_id,render)
	{
		return true;
	};
	this.pickupmousedown=function(event,component_id,render)
	{
		return true;
	};
	this.pickupkeydown=function(event,component_id,render)
	{
		return true;
	};
	this.pickupkeypress=function(event,component_id,render)
	{
		return true;
	};
	this.pickupmouseup=function(event,component_id,render)
	{
		var my_ep=render.component_event_processor[component_id];
		var alf=parseFloat(my_ep.alf);
			
		switch(event.button){
		default:
			break;
		case 0:
			render.caller.call_server_component(component_id,"all",
				[["operation","body_face_direct"],["coordinate","global"],
				 ["type",event.shiftKey?"true":"false"]]);
			break;
		case 2:
			if(event.ctrlKey)
				if(event.altKey)
					alf/=1000.0;
				else
					alf/=100.0;
			else
				if(event.altKey)	
					alf/=10.0;
				else
					alf/=1.0;
			render.caller.call_server_component(component_id,"all",
				[["operation","body_face_rotate"],["coordinate","global"],
				 ["type",event.shiftKey?"true":"false"],["alf",alf.toString()]]);
			break;
		}
		return true;
	};
	this.pickupkeyup=function(event,component_id,render)
	{
		var my_ep=render.component_event_processor[component_id];
			
		var alf=parseFloat(my_ep.alf);
		alf=(event.shiftKey||event.ctrlKey)?(-alf):alf;
		if(event.altKey)
			alf=alf/10.0;
		alf*=Math.PI/180.0;

		switch(event.keyCode){
		case 48:	//	0-9
		case 49:
		case 50:
		case 51:
		case 52:
		case 53:
		case 54:
		case 55:
		case 56:
		case 57:
			my_ep.alf=my_ep.alf+(event.keyCode-48).toString();
			my_ep.alf=my_ep.alf.substring(my_ep.alf.length-3);
			break;
		case 8:		//backspace
		case 37:	//left arrow
			render.caller.call_server_component(component_id,"all",[["operation","retreat"]]);
			break;
		case 82://R
			render.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","1"],						["y0","0"],["z0","0"],
				    	["x1",Math.cos(alf).toString()],["y1","0"],["z1",Math.sin(alf).toString()]
				]);
			break;
		case 76://L
			render.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","1"],						 ["y0","0"],["z0","0"],
				    	["x1",Math.cos(-alf).toString()],["y1","0"],["z1",Math.sin(-alf).toString()]
				]);
			break;
		case 85://U
			render.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","0"],["y0","0"],						["z0","1"],
				    	["x1","0"],["y1",Math.sin(-alf).toString()],["z1",Math.cos(-alf).toString()]
				]);
			break;
		case 68://D
			render.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","0"],["y0","0"],						["z0","1"],
				    	["x1","0"],["y1",Math.sin(alf).toString()],	["z1",Math.cos(alf).toString()]
				]);
			break;
		case 83://S
			render.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","1"],						["y0","0"],						["z0","0"],
				    	["x1",Math.cos(alf).toString()],["y1",Math.sin(alf).toString()],["z1","0"]
				]);
			break;
		case 84://T
			render.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","1"],						 ["y0","0"],						["z0","0"],
				    	["x1",Math.cos(-alf).toString()],["y1",Math.sin(-alf).toString()],	["z1","0"]
				]);
			break;
		case 88://X
			render.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"global"],
				    	["x0","0"],["y0","1"],						["z0","0"],
				    	["x1","0"],["y1",Math.cos(-alf).toString()],["z1",Math.sin(-alf).toString()]
				]);
			break;
		case 89://Y
			render.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"global"],
				    	["x0","0"],						 ["y0","0"],["z0","1"],
				    	["x1",Math.sin(-alf).toString()],["y1","0"],["z1",Math.cos(-alf).toString()]
				]);
			break;
		case 90://Z
			render.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"global"],
				    	["x0","1"],						 ["y0","0"],					  ["z0","0"],
				    	["x1",Math.cos(-alf).toString()],["y1",Math.sin(-alf).toString()],["z1","0"]
				]);
			break;
		default:
			break;
		}
		return true;
	};
};

function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	var old_ep,ep=new construct_event_listener();
	if(typeof(old_ep=render.component_event_processor[component_id])=="object")
		ep=Object.assign(old_ep,ep);
	render.component_event_processor[component_id]=ep;
	
	this.component_id=component_id;
	this.main_render_buffer_id=0;
	this.parameter_buffer=render.webgpu.device.createBuffer(
		{
			size	:	Float32Array.BYTES_PER_ELEMENT*4*render.system_buffer.target_buffer_number,
			usage	:	GPUBufferUsage.COPY_DST|GPUBufferUsage.VERTEX
		});

	this.save_buffer_data=function(render_data,project_matrix,part_object,render)
	{
		var x0				=part_object.material[0];
		var y0				=part_object.material[1];
		var scale			=part_object.material[2];
		var box_distance	=part_object.material[3];
		
		var view_distance	=render.computer.sub_operation(
									project_matrix.right_up_center_point,
									project_matrix.left_down_center_point);
			view_distance	=render.computer.distance(view_distance)*scale;
		var z0				=render.computer.sub_operation(
									project_matrix.near_center_point,
									project_matrix.center_point);
			z0				=render.computer.distance(z0)-view_distance/2.0;

		render.webgpu.device.queue.writeBuffer(this.parameter_buffer,
			Float32Array.BYTES_PER_ELEMENT*4*(render_data.render_buffer_id),
			new Float32Array([x0,y0,z0,view_distance/box_distance]));
	}

	this.draw_component=function(method_data,render_data,
			render_id,part_id,component_id,driver_id,component_render_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		var p,rpe=render.webgpu.render_pass_encoder;
		
		if(render_data.main_display_target_flag)
			this.main_render_buffer_id=render_data.render_buffer_id;
			
		switch(method_data.method_id){
		case 0:	
			rpe.setVertexBuffer(1,this.parameter_buffer,
					Float32Array.BYTES_PER_ELEMENT*4*this.main_render_buffer_id,
					Float32Array.BYTES_PER_ELEMENT*4);

			p=part_object.buffer_object.face.region_data;
			rpe.setPipeline(render_driver.id_pipeline);
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			}
			break;
		case 2:
			this.save_buffer_data(render_data,project_matrix,part_object,render);
			rpe.setVertexBuffer(1,this.parameter_buffer,
					Float32Array.BYTES_PER_ELEMENT*4*render_data.render_buffer_id,
					Float32Array.BYTES_PER_ELEMENT*4);
		
			p=part_object.buffer_object.face.region_data;
			rpe.setPipeline(render_driver.face_pipeline);
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			};
	
			p=part_object.buffer_object.edge.region_data;
			rpe.setPipeline(render_driver.edge_pipeline);
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			}
	
			p=part_object.buffer_object.point.region_data;
			rpe.setPipeline(render_driver.point_pipeline);
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			}
			break;
		default:
			break;
		}
	};
	this.append_component_parameter=function(
			component_id,		driver_id,		render_id,		part_id,
			buffer_data_item,	part_object,	part_driver,	render_driver,	render)
	{
	}
	this.destroy=function(render)
	{
		this.draw_component				=null;
		this.append_component_parameter	=null;
		
		if(render.component_event_processor[this.component_id]!=null){
			if(typeof(render.component_event_processor[this.component_id].destroy)=="function")
				render.component_event_processor[this.component_id].destroy(render);
			render.component_event_processor[this.component_id]=null;
		}
		if(this.parameter_buffer!=null){
			this.parameter_buffer.destroy();
			this.parameter_buffer=null;
		}
		this.save_buffer_data=null;
	}
};
