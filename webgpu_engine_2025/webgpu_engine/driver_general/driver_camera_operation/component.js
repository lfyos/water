function construct_event_listener()
{
	this.alf="90";
	
	this.caculate_angle=function(event)
	{
		var angle_scale=event.shiftKey?1.0:-1.0;
		if(this.alf.length>3)
			this.alf=this.alf.substring(this.alf.length-3);
		while((this.alf.length>1)&&(this.alf.charAt(0)=='0'))
			this.alf=this.alf.substring(1);
		
		if(event.ctrlKey)
			if(event.altKey)
				return angle_scale*parseFloat(this.alf)/1000.0;
			else
				return angle_scale*parseFloat(this.alf)/100.0;
		else
			if(event.altKey)
				return angle_scale*parseFloat(this.alf)/10.0;
			else
				return angle_scale*parseFloat(this.alf);
	}

	this.pickupcontextmenu=function(event,component_id,scene)
	{
		return true;
	};
	this.pickupdblclick=function(event,component_id,scene)
	{
		return true;
	};
	this.pickupmousedown=function(event,component_id,scene)
	{
		return true;
	};
	this.pickupkeydown=function(event,component_id,scene)
	{
		return true;
	};
	this.pickupkeypress=function(event,component_id,scene)
	{
		return true;
	};
	this.pickupmouseup=function(event,component_id,scene)
	{
		var alf=this.caculate_angle(event);
		
		switch(event.button){
		default:
			break;
		case 0:
			scene.caller.call_server_component(component_id,"all",
				[["operation","body_face_direct"],["coordinate","global"],
				 ["type",event.shiftKey?"true":"false"]]);
			break;
		case 2:
			scene.caller.call_server_component(component_id,"all",
				[["operation","body_face_rotate"],["coordinate","global"],["alf",alf]]);
			break;
		}
		return true;
	};
	this.pickupkeyup=function(event,component_id,scene)
	{
		var alf=this.caculate_angle(event)*Math.PI/180.0;
	
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
			if((this.alf=this.alf+(event.keyCode-48).toString()).length>3)
				this.alf=this.alf.substring(this.alf.length-3);
			break;
		case 8:		//backspace
		case 37:	//left arrow
			scene.caller.call_server_component(component_id,"all",[["operation","retreat"]]);
			break;
		case 82://R
			scene.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","1"],						["y0","0"],["z0","0"],
				    	["x1",Math.cos(alf).toString()],["y1","0"],["z1",Math.sin(alf).toString()]
				]);
			break;
		case 76://L
			scene.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","1"],						 ["y0","0"],["z0","0"],
				    	["x1",Math.cos(-alf).toString()],["y1","0"],["z1",Math.sin(-alf).toString()]
				]);
			break;
		case 85://U
			scene.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","0"],["y0","0"],						["z0","1"],
				    	["x1","0"],["y1",Math.sin(-alf).toString()],["z1",Math.cos(-alf).toString()]
				]);
			break;
		case 68://D
			scene.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","0"],["y0","0"],						["z0","1"],
				    	["x1","0"],["y1",Math.sin(alf).toString()],	["z1",Math.cos(alf).toString()]
				]);
			break;
		case 83://S
			scene.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","1"],						["y0","0"],						["z0","0"],
				    	["x1",Math.cos(alf).toString()],["y1",Math.sin(alf).toString()],["z1","0"]
				]);
			break;
		case 84://T
			scene.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"camera"],
				    	["x0","1"],						 ["y0","0"],						["z0","0"],
				    	["x1",Math.cos(-alf).toString()],["y1",Math.sin(-alf).toString()],	["z1","0"]
				]);
			break;
		case 88://X
			scene.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"global"],
				    	["x0","0"],["y0","1"],						["z0","0"],
				    	["x1","0"],["y1",Math.cos(-alf).toString()],["z1",Math.sin(-alf).toString()]
				]);
			break;
		case 89://Y
			scene.caller.call_server_component(component_id,"all",
				[		["operation",	"rotate"],
						["coordinate",	"global"],
				    	["x0","0"],						 ["y0","0"],["z0","1"],
				    	["x1",Math.sin(-alf).toString()],["y1","0"],["z1",Math.cos(-alf).toString()]
				]);
			break;
		case 90://Z
			scene.caller.call_server_component(component_id,"all",
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

function construct_component_driver(component_ids,init_data,create_data,part_object,part_driver,render_driver,scene)
{
	var old_ep,ep=new construct_event_listener();
	if(typeof(old_ep=scene.component_event_processor[component_ids.component_id])=="object")
		ep=Object.assign(old_ep,ep);
	scene.component_event_processor[component_ids.component_id]=ep;
	
	scene.system_buffer.set_id_information_data(init_data,component_ids.component_id,component_ids.driver_id);
	
	this.draw_component=function(method_data,render_parameter,
			target_data,part_object,part_driver,render_driver,scene)
	{
		var p,rpe=scene.webgpu.render_pass_encoder;
		
		switch(method_data.method_id){
		case 0:
			rpe.setPipeline(render_driver.id_pipeline);
			p=part_object.buffer_object.face.region_data;
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			}
			break;
		case 2:
			rpe.setPipeline(render_driver.face_pipeline);
			p=part_object.buffer_object.face.region_data;
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			};
			
			rpe.setPipeline(render_driver.edge_pipeline);
			p=part_object.buffer_object.edge.region_data;
			for(var i=0,ni=p.length;i<ni;i++){
				rpe.setVertexBuffer(0,p[i].buffer);
				rpe.draw(p[i].item_number);
			}
			break;
		default:
			break;
		}
	};
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)  
	{
	}
	this.destroy=function()
	{
	}
};
