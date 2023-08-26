function init_component_event_processor(screen_rectangle_component_id,render)
{
	this.screen_rectangle_component_id=screen_rectangle_component_id;
	
	this.p0=[0.0,0.0,0.0,1.0];
	this.dp=[0.0,0.0,0.0,0.0];
	this.mouse_up_flag=true;
	this.change_type_flag=true;
			
	this.control_code=function(event)
	{
		return ["control",(event.shiftKey?1:0)+(event.ctrlKey?2:0)+(event.altKey?4:0)];
	};
	this.mousedown=function(event,component_id,render)
	{
		if(event.button!=0)
			return true;
		var ep=render.component_event_processor[component_id];
		ep.mouse_up_flag=false;
		ep.p0=[render.view.main_target_x,render.view.main_target_y,0.0,1.0];	
		ep.dp=[0.0,0.0,0.0,0.0];
		
		return true;
	};
	this.mousemove=function(event,component_id,render)
	{
		if(event.button!=0)
			return true;
		var ep=render.component_event_processor[component_id];
		var screen_rectangle_ep=render.component_event_processor[ep.screen_rectangle_component_id];
		if((typeof(screen_rectangle_ep)!="object")||(screen_rectangle_ep==null))
			return true;
		if(ep.mouse_up_flag)
			return true;
		ep.dp=[render.view.main_target_x-ep.p0[0],render.view.main_target_y-ep.p0[1],0.0,0.0];
		screen_rectangle_ep.data=[ep.p0[0],ep.p0[1],ep.p0[0]+ep.dp[0],ep.p0[1]+ep.dp[1]];
		return true;
	};
	this.mouseup=function(event,component_id,render)
	{
		if(event.button!=0)
			return true;
		var ep=render.component_event_processor[component_id];
		var screen_rectangle_ep=render.component_event_processor[screen_rectangle_component_id];
		if((typeof(screen_rectangle_ep)!="object")||(screen_rectangle_ep==null))
			return true;
		if(ep.mouse_up_flag)
			return true;
		ep.mouse_up_flag=true;
		
		screen_rectangle_ep.data=[ep.p0[0],ep.p0[1],ep.p0[0],ep.p0[1]];
		
		var dx=render.view.main_target_x-ep.p0[0];
		var dy=render.view.main_target_y-ep.p0[1];
		
		var my_promise;
				
		if((dx*dx+dy*dy)<(render.computer.min_value2()))
			my_promise=render.caller.call_server_component(component_id,0,[
						["operation",	"single"],
						["function",	render.event_component.mouse.function_id],
						ep.control_code(event)]);
		else
			my_promise=render.caller.call_server_component(component_id,0,[
						["operation",	"many"									],
						["function",	render.event_component.mouse.function_id],
						["x0",			ep.p0[0]								],
						["y0",			ep.p0[1]								],
						["x1",			render.view.main_target_x				],
						["y1",			render.view.main_target_y				],
						ep.control_code(event)]);
		my_promise.then(
			function(response_data)
			{
				render.caller.call_server_component("coordinate","all",[["operation","onoff"]]);
			});
		return true;
	};
			
	this.mousewheel=function(event,component_id,render)
	{
		var ep=render.component_event_processor[component_id],mouse_wheel_number=0;
		
		if(typeof(event.wheelDelta)=="number")
			mouse_wheel_number+=event.wheelDelta;//for chrome,opera
		else if(typeof(event.detail)=="number")
			mouse_wheel_number-=event.detail*40;//for firefox
		else
			return true;
		
		var p=render.camera.camera_object_parameter[ep.camera_id];
	
		if(ep.change_type_flag)
			p.distance		/=Math.exp(mouse_wheel_number/2000);
		else
			p.half_fovy_tanl/=Math.exp(mouse_wheel_number/2000);	

		render.caller.call_server_component(component_id,0,[["operation","scale"],	
			["distance",p.distance],["half_fovy_tanl",p.half_fovy_tanl],ep.control_code(event)]);
		
		return true;			
	};
};

function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	var screen_rectangle_component_id=init_data;
	var old_ep,ep=new init_component_event_processor(screen_rectangle_component_id,render);
	if(typeof(old_ep=render.component_event_processor[component_id])=="object")
		if(old_ep!=null)
			ep=Object.assign(old_ep,ep);
	render.component_event_processor[component_id]=ep;
	
	ep.camera_id		=0;
	ep.change_type_flag	=true;
	this.component_id	=component_id;

	this.draw_component=function(method_data,render_data,
			render_id,part_id,component_id,driver_id,component_render_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		if(render_data.main_display_target_flag)
			render.component_event_processor[component_id].camera_id=render_data.camera_id;
	}
	
	this.append_component_parameter=function(
			component_id,		driver_id,		render_id,		part_id,
			buffer_data_item,	part_object,	part_driver,	render_driver,	render)
	{
		render.component_event_processor[component_id].change_type_flag=(buffer_data_item>0)?true:false;
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
	}
};
