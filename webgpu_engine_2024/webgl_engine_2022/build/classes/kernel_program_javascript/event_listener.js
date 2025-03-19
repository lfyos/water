function construct_event_listener(my_canvas_id,my_scene)
{
	this.canvas_id	=my_canvas_id;
	this.scene		=my_scene;
	this.canvas		=this.scene.webgpu.canvas[this.canvas_id];

	this.mouse_inside_canvas_flag	=false;
	this.mouse_down_flag			=false;
	this.mouse_down_flag_array		=[false,false,false,false,false];

	this.set_view=function(event)
	{
		var rect=this.canvas.getBoundingClientRect();
		var left=rect.left,top=rect.top,right=rect.right,bottom=rect.bottom;
		var x=event.clientX-left,y=event.clientY-top;
		var width=right-left,height=bottom-top;
		
		if((this.scene.view.x=2.0*x/width-1.0)<-1.0)
			this.scene.view.x=-1.0;
		else if(this.scene.view.x>1.0)
			this.scene.view.x=1.0;
		if((this.scene.view.y=1.0-2.0*y/height)<-1.0)
			this.scene.view.y=-1.0;
		else if(this.scene.view.y>1.0)
			this.scene.view.y=1.0;
		this.canvas.focus();
	};
	this.set_mobile_view=function(event)
	{
		if(event.touches.length>0){
			var btn=this.canvas;
			var x=event.touches[0].clientX-btn.offsetLeft;
			var y=btn.clientHeight-(event.touches[0].clientY-btn.offsetTop);	
			if((this.scene.view.x=2.0*((x/btn.clientWidth )-0.5))<-1.0)
				this.scene.view.x=-1.0;
			else if(this.scene.view.x >1.0)
				this.scene.view.x=1.0;
			if((this.scene.view.y=2.0*((y/btn.clientHeight)-0.5))<-1.0)
				this.scene.view.y=-1.0;
			else if(this.scene.view.y>1.0)
				this.scene.view.y=1.0;
		}
		this.canvas.focus();
	};
	
	this.scene.system_event_processor.systemmousemove	=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickupmousemove	=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.mousemove			=function(event,scene)						{return false;};
	this.mousemove_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		this.mouse_inside_canvas_flag=true;
		
		var ep,component_id;
		event.preventDefault();
		this.set_view(event);
		
		if(my_scene.system_event_processor.systemmousemove(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupmousemove(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupmousemove)=="function"){
						if(ep.pickupmousemove(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.mousemove(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.mouse.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.mouse.component_name=component_id;
		if(typeof(ep.mousemove)=="function")
			if(ep.mousemove(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemmousedown	=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickupmousedown	=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.mousedown		=function(event,scene)						{return false;};
	
	this.mousedown_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		this.mouse_inside_canvas_flag=true;
		this.mouse_down_flag_array[event.button]=true;
		this.mouse_down_flag=true;
		for(var i=0,ni=this.mouse_down_flag_array.length;i<ni;i++)
			this.mouse_down_flag|=this.mouse_down_flag_array[i];
		
		var ep,component_id;

		event.preventDefault();
		this.set_view(event);
		
		if(my_scene.system_event_processor.systemmousedown(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupmousedown(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupmousedown)=="function"){
						if(ep.pickupmousedown(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.mousedown(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.mouse.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.mouse.component_name=component_id;
		if(typeof(ep.mousedown)=="function")
			if(ep.mousedown(event,component_id,this.scene))
				return;	
	};
	this.scene.system_event_processor.systemmouseup	=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickupmouseup	=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.mouseup			=function(event,scene)						{return false;};
	this.mouseup_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		this.mouse_inside_canvas_flag=true;
		this.mouse_down_flag_array[event.button]=false;
		this.mouse_down_flag=false;
		for(var i=0,ni=this.mouse_down_flag_array.length;i<ni;i++)
			this.mouse_down_flag|=this.mouse_down_flag_array[i];
		
		var ep,component_id;
		
		event.preventDefault();
		this.set_view(event);
		
		if(my_scene.system_event_processor.systemmouseup(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupmouseup(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;	
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupmouseup)=="function"){
						if(ep.pickupmouseup(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.mouseup(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.mouse.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.mouse.component_name=component_id;
		if(typeof(ep.mouseup)=="function")
			if(ep.mouseup(event,component_id,this.scene))
				return;
	};
	
	this.scene.system_event_processor.systemdblclick	=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickupdblclick	=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.dblclick			=function(event,scene)						{return false;};
	this.dblclick_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
			
		this.mouse_inside_canvas_flag=true;
	
		var ep,component_id;
		
		event.preventDefault();
		this.set_view(event);
		
		if(my_scene.system_event_processor.systemdblclick(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupdblclick(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupdblclick)=="function"){
						if(ep.pickupdblclick(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.dblclick(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.mouse.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.mouse.component_name=component_id;
		if(typeof(ep.dblclick)=="function")
			if(ep.dblclick(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemmousewheel	=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickupmousewheel	=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.mousewheel		=function(event,scene)						{return false;};
	this.mousewheel_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		this.mouse_inside_canvas_flag=true;
		
		var ep,component_id;
		event.preventDefault();
		this.set_view(event);
		
		if(my_scene.system_event_processor.systemmousewheel(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupmousewheel(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupmousewheel)=="function"){
						if(ep.pickupmousewheel(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.mousewheel(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.mouse.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.mouse.component_name=component_id;		
		if(typeof(ep.mousewheel)=="function")
			if(ep.mousewheel(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemmouseover	=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickupmouseover	=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.mouseover		=function(event,scene)						{return false;};
	this.mouseover_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		this.mouse_inside_canvas_flag=true;
		
		var ep,component_id;
		event.preventDefault();
		this.set_view(event);
		
		if(my_scene.system_event_processor.systemmouseover(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupmouseover(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupmouseover)=="function"){
						if(ep.pickupmouseover(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.mouseover(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.mouse.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.mouse.component_name=component_id;		
		if(typeof(ep.mouseover)=="function")
			if(ep.mouseover(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemmouseout	=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickupmouseout	=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.mouseout			=function(event,scene)						{return false;};
	this.mouseout_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		this.mouse_inside_canvas_flag=false;

		var ep,component_id;
		event.preventDefault();
		this.set_view(event);
		
		if(my_scene.system_event_processor.systemmouseout(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupmouseout(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupmouseout)=="function"){
						if(ep.pickupmouseout(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.mouseout(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.mouse.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.mouse.component_name=component_id;		
		if(typeof(ep.mouseout)=="function")
			if(ep.mouseout(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemmouseenter	=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickupmouseenter	=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.mouseenter		=function(event,scene)						{return false;};
	this.mouseenter_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		this.mouse_inside_canvas_flag=true;
		
		var ep,component_id;
		event.preventDefault();
		this.set_view(event);
		
		if(my_scene.system_event_processor.systemmouseenter(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupmouseenter(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupmouseenter)=="function"){
						if(ep.pickupmouseenter(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.mouseenter(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.mouse.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.mouse.component_name=component_id;		
		if(typeof(ep.mouseenter)=="function")
			if(ep.mouseenter(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemmouseleave	=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickupmouseleave	=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.mouseleave		=function(event,scene)						{return false;};
	this.mouseleave_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		this.mouse_inside_canvas_flag=false;
		
		var ep,component_id;
		event.preventDefault();
		this.set_view(event);
		
		if(my_scene.system_event_processor.systemmouseleave(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupmouseleave(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupmouseleave)=="function"){
						if(ep.pickupmouseleave(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.mouseleave(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.mouse.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.mouse.component_name=component_id;		
		if(typeof(ep.mouseleave)=="function")
			if(ep.mouseleave(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemcontextmenu=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickupcontextmenu=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.contextmenu		=function(event,scene)						{return false;};
	this.contextmenu_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		var ep,component_id;
		
		event.preventDefault();
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		if(my_scene.system_event_processor.systemcontextmenu(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupcontextmenu(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupcontextmenu)=="function"){
						if(ep.pickupcontextmenu(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.contextmenu(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.mouse.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.mouse.component_name=component_id;
		if(typeof(ep.contextmenu)=="function")
			if(ep.contextmenu(event,component_id,this.scene))
				return;
	};

	this.scene.system_event_processor.systemtouchstart	=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickuptouchstart	=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.touchstart		=function(event,scene)						{return false;};
	this.touchstart_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;

		var ep,component_id;
		event.preventDefault();
		this.set_mobile_view(event);
		
		if(my_scene.system_event_processor.systemtouchstart(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickuptouchstart(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickuptouchstart)=="function"){
						if(ep.pickuptouchstart(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.touchstart(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.touch.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.touch.component_name=component_id;
		if(typeof(ep.touchstart)=="function")
			if(ep.touchstart(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemtouchend	=function(event,scene)						{return false;};
	this.scene.system_event_processor.pickuptouchend	=function(event,pickup_component_id,scene)	{return false;};
	this.scene.system_event_processor.touchend			=function(event,scene)						{return false;};
	this.touchend_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		var ep,component_id;
		event.preventDefault();
		this.set_mobile_view(event);
		
		if(my_scene.system_event_processor.systemtouchend(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickuptouchend(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickuptouchend)=="function"){
						if(ep.pickuptouchend(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.touchend(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.touch.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.touch.component_name=component_id;
		if(typeof(ep.touchend)=="function")
			if(ep.touchend(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemtouchmove	=function(event,scene){return false;};
	this.scene.system_event_processor.pickuptouchmove	=function(event,scene,pickup_component_id){return false;};
	this.scene.system_event_processor.touchmove		=function(event,scene){return false;};
	this.touchmove_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		var ep,component_id;
		event.preventDefault();
		this.set_mobile_view(event);
		
		if(my_scene.system_event_processor.systemtouchmove(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickuptouchmove(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickuptouchmove)=="function"){
						if(ep.pickuptouchmove(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.touchmove(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.touch.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.touch.component_name=component_id;
		if(typeof(ep.touchmove)=="function")
			if(ep.touchmove(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemkeydown	=function(event,scene){return false;};
	this.scene.system_event_processor.pickupkeydown	=function(event,pickup_component_id,scene){return false;};
	this.scene.system_event_processor.keydown			=function(event,scene){return false;};
	this.keydown_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		var ep,component_id;
		event.preventDefault();
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		if(my_scene.system_event_processor.systemkeydown(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupkeydown(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupkeydown)=="function"){
						if(ep.pickupkeydown(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.keydown(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.keyboard.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.keyboard.component_name=component_id;
		if(typeof(ep.keydown)=="function")
			if(ep.keydown(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemkeypress	=function(event,scene){return false;};
	this.scene.system_event_processor.pickupkeypress	=function(event,pickup_component_id,scene){return false;};
	this.scene.system_event_processor.keypress			=function(event,scene){return false;};
	this.keypress_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		var ep,component_id;
		event.preventDefault();
		my_scene.webgpu.current_canvas_id=this.canvas_id;

		if(my_scene.system_event_processor.systemkeypress(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupkeypress(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupkeypress)=="function"){
						if(ep.pickupkeypress(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.keypress(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.keyboard.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.keyboard.component_name=component_id;
		if(typeof(ep.keypress)=="function")
			if(ep.keypress(event,component_id,this.scene))
				return;
	};
	this.scene.system_event_processor.systemkeyup	=function(event,scene){return false;};
	this.scene.system_event_processor.pickupkeyup	=function(event,pickup_component_id,scene){return false;};
	this.scene.system_event_processor.keyup		=function(event,scene){return false;};
	this.keyup_event_listener=function (event)
	{
		var my_scene;
		if((my_scene=this.scene).terminate_flag)
			return;
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		var ep,component_id;
		event.preventDefault();
		my_scene.webgpu.current_canvas_id=this.canvas_id;
		
		switch(event.keyCode){
		case 48://0
		case 49://1
		case 50://2
		case 51://3
		case 52://4
		case 53://5
		case 54://6
		case 55://7
		case 56://8
		case 57://9
			my_scene.pickup_array[event.keyCode-48]=my_scene.pickup.fork();
			break;
		}
		if(my_scene.system_event_processor.systemkeyup(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if(my_scene.pickup.component_id>=0)
			if(my_scene.pickup.component_id<(my_scene.component_event_processor.length)){
				if(my_scene.system_event_processor.pickupkeyup(event,my_scene.pickup.component_id,this.scene))
					return;
				if(my_scene.terminate_flag)
					return;
				if(typeof(ep=my_scene.component_event_processor[my_scene.pickup.component_id])=="object")
					if(typeof(ep.pickupkeyup)=="function"){
						if(ep.pickupkeyup(event,my_scene.pickup.component_id,this.scene))
							return;
						if(my_scene.terminate_flag)
							return;
					}
			}
		if(my_scene.system_event_processor.keyup(event,this.scene))
			return;
		if(my_scene.terminate_flag)
			return;
		if((ep=my_scene.operate_component.get_component_event_processor(
			my_scene.event_component.keyboard.component_name))==null)
				return;
		component_id=my_scene.operate_component.last_operate_component_id;
		my_scene.event_component.keyboard.component_name=component_id;
		if(typeof(ep.keyup)=="function")
			if(ep.keyup(event,component_id,this.scene))
				return;
	};
	

	var cur=this;
	
	function mousemove_fun(event)	
	{	
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.mousemove_event_listener(event);
				}
	};
	function mousedown_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.mousedown_event_listener(event);
				}
	};
	function mouseup_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.mouseup_event_listener(event);
				}
	};
	function dblclick_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.dblclick_event_listener(event);	
				}
	};
	function mousewheel_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.mousewheel_event_listener(event);
				}
	};
	function mouseleave_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.mouseleave_event_listener(event);
				}
	};
	function mouseenter_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.mouseenter_event_listener(event);
				}
	};
	function mouseout_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.mouseout_event_listener(event);
				}
	};
	function mouseover_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.mouseover_event_listener(event);
				}
	};
	function contextmenu_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.contextmenu_event_listener(event);
				}
	};
	function touchstart_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.touchstart_event_listener(event);
				}
	};
	function touchend_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.touchend_event_listener(event);
				}
	};
	function touchmove_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.touchmove_event_listener(event);
				}
	};
	function keydown_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.keydown_event_listener(event);
				}
	};
	function keypress_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.keypress_event_listener(event);	
				}
	};
	function keyup_fun(event)
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag)){
					cur.scene.last_event_time=(new Date()).getTime();
					cur.keyup_event_listener(event);
				}
	};
	function beforeunload_fun()
	{
		if(cur!=null)
			if(cur.scene!=null)
				if(!(cur.scene.terminate_flag))
					if(typeof(cur.scene.destroy)=="function")
						cur.scene.destroy();
	};

	this.canvas.addEventListener("mousemove",		mousemove_fun,		false);
	this.canvas.addEventListener("mousedown",		mousedown_fun,		false);
	this.canvas.addEventListener("mouseup",			mouseup_fun,		false);
	this.canvas.addEventListener("dblclick",		dblclick_fun,		false);
	this.canvas.addEventListener("mousewheel",		mousewheel_fun,		false);
	this.canvas.addEventListener("DOMMouseScroll",	mousewheel_fun,		false);
	this.canvas.addEventListener("mouseleave",		mouseleave_fun,		false);
	this.canvas.addEventListener("mouseenter",		mouseenter_fun,		false);
	this.canvas.addEventListener("mouseout",		mouseout_fun,		false);
	this.canvas.addEventListener("mouseover",		mouseover_fun,		false);
	
	this.canvas.addEventListener("touchstart",		touchstart_fun,		false);
	this.canvas.addEventListener("touchend",		touchend_fun,		false);
	this.canvas.addEventListener("touchmove",		touchmove_fun,		false);
	
	this.canvas.addEventListener("keydown",			keydown_fun,		false);
	this.canvas.addEventListener("keypress",		keypress_fun,		false);
	this.canvas.addEventListener("keyup",			keyup_fun,			false);
	
	this.canvas.addEventListener("contextmenu", 	contextmenu_fun,	false);

	window.addEventListener(	 "beforeunload",	beforeunload_fun,	false);
	
	this.canvas.focus();
	
	this.destroy=function()
	{
		if(this.canvas!=null){
			window.removeEventListener("beforeunload",beforeunload_fun);
			
			this.canvas.removeEventListener("mousemove",		mousemove_fun);
			this.canvas.removeEventListener("mousedown",		mousedown_fun);
			this.canvas.removeEventListener("mouseup",			mouseup_fun);
			this.canvas.removeEventListener("dblclick",			dblclick_fun);
			this.canvas.removeEventListener("mousewheel",		mousewheel_fun);
			this.canvas.removeEventListener("DOMMouseScroll",	mousewheel_fun);
			this.canvas.removeEventListener("mouseleave",		mouseleave_fun);
			this.canvas.removeEventListener("mouseenter",		mouseenter_fun);
			this.canvas.removeEventListener("mouseout",			mouseout_fun);
			this.canvas.removeEventListener("mouseover",		mouseover_fun);
				
			this.canvas.removeEventListener("contextmenu", 		contextmenu_fun);
					
			this.canvas.removeEventListener("touchstart",		touchstart_fun);
			this.canvas.removeEventListener("touchend",			touchend_fun);
			this.canvas.removeEventListener("touchmove",		touchmove_fun);
					
			this.canvas.removeEventListener("keydown",			keydown_fun);
			this.canvas.removeEventListener("keypress",			keypress_fun);
			this.canvas.removeEventListener("keyup",			keyup_fun);
			
			this.canvas=null;
		}
		if(this.scene!=null)
			this.scene=null;
	}
};
