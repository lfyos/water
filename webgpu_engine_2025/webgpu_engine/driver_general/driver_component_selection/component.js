function init_component_event_processor(screen_rectangle_component_id,scene)
{
	this.screen_rectangle_component_id	=screen_rectangle_component_id;
	
	this.p0=[0.0,0.0,0.0,1.0];
	this.dp=[0.0,0.0,0.0,0.0];
	this.mouse_up_flag=true;
	this.change_type_flag=true;
			
	this.control_code=function(event)
	{
		return ["control",(event.shiftKey?1:0)+(event.ctrlKey?2:0)+(event.altKey?4:0)];
	};
	this.mousedown=function(event,component_id,scene)
	{
		if(event.button!=0)
			return true;
		var ep=scene.component_event_processor[component_id];
		ep.mouse_up_flag=false;
		ep.p0=[scene.view.main_target_x,scene.view.main_target_y,0.0,1.0];	
		ep.dp=[0.0,0.0,0.0,0.0];
		
		return true;
	};
	this.mousemove=function(event,component_id,scene)
	{
		if(event.button!=0)
			return true;
		var ep=scene.component_event_processor[component_id];
		var screen_rectangle_ep=scene.component_event_processor[ep.screen_rectangle_component_id];
		if((typeof(screen_rectangle_ep)!="object")||(screen_rectangle_ep==null))
			return true;
		if(ep.mouse_up_flag)
			return true;
		ep.dp=[scene.view.main_target_x-ep.p0[0],scene.view.main_target_y-ep.p0[1],0.0,0.0];
		screen_rectangle_ep.data=[ep.p0[0],ep.p0[1],ep.p0[0]+ep.dp[0],ep.p0[1]+ep.dp[1]];
		return true;
	};
	this.mouseup=function(event,component_id,scene)
	{
		if(event.button!=0)
			return true;
		var ep=scene.component_event_processor[component_id];
		var screen_rectangle_ep=scene.component_event_processor[screen_rectangle_component_id];
		if((typeof(screen_rectangle_ep)!="object")||(screen_rectangle_ep==null))
			return true;
		if(ep.mouse_up_flag)
			return true;
		ep.mouse_up_flag=true;
		
		screen_rectangle_ep.data=[ep.p0[0],ep.p0[1],ep.p0[0],ep.p0[1]];
		
		var dx=scene.view.main_target_x-ep.p0[0];
		var dy=scene.view.main_target_y-ep.p0[1];
		
		var my_promise;
				
		if((dx*dx+dy*dy)<(scene.computer.min_value2()))
			my_promise=scene.caller.call_server_component(component_id,0,[
						["operation",	"single"],
						["function",	scene.event_component.mouse.function_id],
						ep.control_code(event)]);
		else
			my_promise=scene.caller.call_server_component(component_id,0,[
						["operation",	"many"									],
						["function",	scene.event_component.mouse.function_id],
						["x0",			ep.p0[0]								],
						["y0",			ep.p0[1]								],
						["x1",			scene.view.main_target_x				],
						["y1",			scene.view.main_target_y				],
						ep.control_code(event)]);
		my_promise.then(
			function(response_data)
			{
				scene.system_call_processor.update_coordinate_display();
			});
		return true;
	};
			
	this.mousewheel=function(event,component_id,scene)
	{
		var ep=scene.component_event_processor[component_id],mouse_wheel_number=0;
		
		if(typeof(event.wheelDelta)=="number")
			mouse_wheel_number+=event.wheelDelta;//for chrome,opera
		else if(typeof(event.detail)=="number")
			mouse_wheel_number-=event.detail*40;//for firefox
		else
			return true;
		
		var p=scene.camera.camera_object_parameter[ep.camera_id];
	
		if(ep.change_type_flag)
			p.distance		/=Math.exp(mouse_wheel_number/2000);
		else
			p.half_fovy_tanl/=Math.exp(mouse_wheel_number/2000);
		p.should_update_buffer_data_flag=true;

		scene.caller.call_server_component(component_id,0,[["operation","scale"],	
			["distance",p.distance],["half_fovy_tanl",p.half_fovy_tanl],ep.control_code(event)]);
		
		return true;			
	};
};

function construct_component_driver(component_ids,init_data,create_data,part_object,part_driver,render_driver,scene)
{
	var screen_rectangle_component_id=init_data;
	var old_ep,ep=new init_component_event_processor(screen_rectangle_component_id,scene);
	if(typeof(old_ep=scene.component_event_processor[component_ids.component_id])=="object")
		if(old_ep!=null)
			ep=Object.assign(old_ep,ep);
	scene.component_event_processor[component_ids.component_id]=ep;
	
	ep.camera_id		=0;
	ep.change_type_flag	=true;
	this.component_ids	=component_ids;

	this.draw_component=function(method_data,render_parameter,
			target_data,part_object,part_driver,render_driver,scene)
	{
		if(target_data.main_display_target_flag){
			var p=scene.component_event_processor[this.component_ids.component_id];
			p.camera_id=target_data.camera_id;
		}
	}
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)  
	{
		var p=scene.component_event_processor[this.component_ids.component_id];
		p.change_type_flag=(buffer_data_item>0)?true:false;
	}
};
