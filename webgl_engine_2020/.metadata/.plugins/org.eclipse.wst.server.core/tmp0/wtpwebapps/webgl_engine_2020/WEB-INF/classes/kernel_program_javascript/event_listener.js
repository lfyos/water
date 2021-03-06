function construct_event_listener(my_render)
{
	this.render=my_render;
	
	if(typeof(this.render.system_event_processor)!="object")
		this.render.system_event_processor=new Object();

	this.set_render_view=function(event)
	{
		var rect=this.render.canvas.getBoundingClientRect();
		var left=rect.left,top=rect.top,right=rect.right,bottom=rect.bottom;
		var width=right-left,height=bottom-top;
		var x=event.clientX-left,y=event.clientY-top;
		this.render.view.x =2.0*x/width-1.0;
		this.render.view.y =1.0-2.0*y/height;
		
		this.render.canvas.focus();
	};
	this.set_mobile_render_view=function(event)
	{
		if(event.touches.length>0){
			var btn=this.render.canvas;
			var x=event.touches[0].clientX-btn.offsetLeft;
			var y=btn.clientHeight-(event.touches[0].clientY-btn.offsetTop);	
			this.render.view.x=2.0*((x/btn.clientWidth )-0.5);
			this.render.view.y=2.0*((y/btn.clientHeight)-0.5);
		}
		this.render.canvas.focus();
	};
	this.caculate_pickup_event_processor=function()
	{
		if(this.render.pickup.component_id<0)
			return null;
		if(this.render.pickup.component_id>=(this.render.component_event_processor.length))
			return null;
		var ep=this.render.component_event_processor[this.render.pickup.component_id];
		
		return (typeof(ep)=="object")?ep:null;
	};
	this.caculate_component_event_processor=function (processor_component_object)
	{
		if(typeof(processor_component_object)!="object")
			return -1;
		var component_name=processor_component_object.component_name;
		var component_object=this.render.get_component_processor(component_name);
		if(typeof(component_object)!="object")
			return -1;
		if(component_object==null)
			return -1;
		if(typeof(component_object.component_id)!="number")
			return -1;
		if(component_object.component_id<0)
			return -1;
		if(component_object.component_id>=(this.render.component_event_processor.length))
			return -1;
		var ep=this.render.component_event_processor[component_object.component_id];
		return (typeof(ep)!="object")?-1:(ep==null)?-1:(component_object.component_id);
	};
	
	this.render.system_event_processor.pickupmousemove	=function(event,pickup_component_id,render)	{return false;};
	this.render.system_event_processor.mousemove		=function(event,render)						{return false;};
	this.mousemove_event_listener=function (event)
	{
		var ep,component_id;
		event.preventDefault();
		this.set_render_view(event);
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickupmousemove(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.mousemove(event,this.render))
			return;

		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickupmousemove)=="function")
				try{
					if(ep.pickupmousemove(event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickupmousemove processor error,component id is "+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickupmousemove processor error,component id is "+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.mouse))<0)
			return;
		
		this.render.event_component.mouse.component_name=component_id;
		ep=this.render.component_event_processor[component_id];
		if(typeof(ep.mousemove)=="function"){
			try{
				if(ep.mousemove(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute mousemove processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute mousemove processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};
	
	this.render.system_event_processor.pickupmousedown	=function(event,pickup_component_id,render)	{return false;};
	this.render.system_event_processor.mousedown		=function(event,render)						{return false;};
	this.mousedown_event_listener=function (event)
	{
		var ep,component_id;

		event.preventDefault();
		this.set_render_view(event);
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickupmousedown(
					event,this.render.pickup.component_id,this.render))
						return;
		
		if(this.render.system_event_processor.mousedown(event,this.render))
			return;

		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickupmousedown)=="function")
				try{
					if(ep.pickupmousedown(event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickupmousedown processor error,component id is "+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickupmousedown processor error,component id is "+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.mouse))<0)
			return;
		this.render.event_component.mouse.component_name=component_id;
		ep=this.render.component_event_processor[component_id];			
		if(typeof(ep.mousedown)=="function"){
			try{
				if(ep.mousedown(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute mousedown processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute mousedown processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}	
		}
	};
	
	this.render.system_event_processor.pickupmouseup	=function(event,pickup_component_id,render)	{return false;};
	this.render.system_event_processor.mouseup			=function(event,render)						{return false;};
	this.mouseup_event_listener=function (event)
	{
		var ep,component_id;
		
		event.preventDefault();
		this.set_render_view(event);
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickupmouseup(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.mouseup(event,this.render))
			return;
		
		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickupmouseup)=="function")
				try{
					if(ep.pickupmouseup(event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickupmouseup processor error,component id is "+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickupmouseup processor error,component id is "+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.mouse))<0)
			return;
		this.render.event_component.mouse.component_name=component_id;
		ep=this.render.component_event_processor[component_id];
		if(typeof(ep.mouseup)=="function"){
			try{
				if(ep.mouseup(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute mouseup processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute mouseup processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};
	this.render.system_event_processor.pickupdblclick	=function(event,pickup_component_id,render)	{return false;};
	this.render.system_event_processor.dblclick			=function(event,render)						{return false;};
	this.dblclick_event_listener=function (event)
	{
		var ep,component_id;
		
		event.preventDefault();
		this.set_render_view(event);
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickupdblclick(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.dblclick(event,this.render))
			return;
		
		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickupdblclick)=="function")
				try{
					if(ep.pickupdblclick(event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickupdblclick processor error,component id is "+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickupdblclick processor error,component id is "+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.mouse))<0)
			return;
		this.render.event_component.mouse.component_name=component_id;
		ep=this.render.component_event_processor[component_id];	
		if(typeof(ep.dblclick)=="function"){
			try{
				if(ep.dblclick(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute dblclick processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute dblclick processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};
	this.render.system_event_processor.pickupmousewheel	=function(event,pickup_component_id,render)	{return false;};
	this.render.system_event_processor.mousewheel		=function(event,render)						{return false;};
	this.mousewheel_event_listener=function (event)
	{
		var ep,component_id;
		event.preventDefault();
		this.set_render_view(event);
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickupmousewheel(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.mousewheel(event,this.render))
			return;
		
		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickupmousewheel)=="function")
				try{
					if(ep.pickupmousewheel(event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickupmousewheel processor error,component id is "+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickupmousewheel processor error,component id is "+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.mouse))<0)
			return;
		this.render.event_component.mouse.component_name=component_id;
		ep=this.render.component_event_processor[component_id];
						
		if(typeof(ep.mousewheel)=="function"){
			try{
				if(ep.mousewheel(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute mousewheel processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute mousewheel processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};
	this.render.system_event_processor.pickuptouchstart	=function(event,pickup_component_id,render)	{return false;};
	this.render.system_event_processor.touchstart		=function(event,render)						{return false;};
	this.touchstart_event_listener=function (event)
	{
		var ep,component_id;
		event.preventDefault();
		this.set_mobile_render_view(event);
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickuptouchstart(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.touchstart(event,this.render))
			return;
		
		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickuptouchstart)=="function")
				try{
					if(ep.pickuptouchstart(event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickuptouchstart processor error,component id is "+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickuptouchstart processor error,component id is "+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		};
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.touch))<0)
			return;
		this.render.event_component.touch.component_name=component_id;
		ep=this.render.component_event_processor[component_id];	
		if(typeof(ep.touchstart)=="function"){
			try{
				if(ep.touchstart(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute touchstart processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute touchstart processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};
	
	this.render.system_event_processor.pickuptouchend	=function(event,pickup_component_id,render)	{return false;};
	this.render.system_event_processor.touchend			=function(event,render)						{return false;};
	this.touchend_event_listener=function (event)
	{
		var ep,component_id;
		event.preventDefault();
		this.set_mobile_render_view(event);
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickuptouchend(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.touchend(event,this.render))
			return;

		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickuptouchend)=="function")
				try{
					if(ep.pickuptouchend(event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickuptouchend processor error,component id is "
								+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickuptouchend processor error,component id is "
								+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.touch))<0)
			return;
		
		this.render.event_component.touch.component_name=component_id;
		ep=this.render.component_event_processor[component_id];	
		if(typeof(ep.touchend)=="function"){
			try{
				if(ep.touchend(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute touchend processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute touchend processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};
	this.render.system_event_processor.pickuptouchmove	=function(event,render,pickup_component_id){return false;};
	this.render.system_event_processor.touchmove		=function(event,render){return false;};
	this.touchmove_event_listener=function (event)
	{
		var ep,component_id;
		event.preventDefault();
		this.set_mobile_render_view(event);
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickuptouchmove(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.touchmove(event,this.render))
			return;
		
		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickuptouchmove)=="function")
				try{
					if(ep.pickuptouchmove(
							event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickuptouchmove processor error,component id is "
								+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickuptouchmove processor error,component id is "
								+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.touch))<0)
			return;
		
		this.render.event_component.touch.component_name=component_id;
		ep=this.render.component_event_processor[component_id];	
		if(typeof(ep.touchmove)=="function"){
			try{
				if(ep.touchmove(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute touchmove processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute touchmove processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};
	
	this.render.system_event_processor.pickupkeydown	=function(event,render,pickup_component_id){return false;};
	this.render.system_event_processor.keydown			=function(event,render){return false;};
	this.keydown_event_listener=function (event)
	{
		var ep,component_id;
		event.preventDefault();
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickupkeydown(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.keydown(event,this.render))
			return;
		
		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickupkeydown)=="function")
				try{
					if(ep.pickupkeydown(event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickupkeydown processor error,component id is "+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickupkeydown processor error,component id is "+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.keyboard))<0)
			return;
		this.render.event_component.keyboard.component_name=component_id;
		ep=this.render.component_event_processor[component_id];	
		if(typeof(ep.keydown)=="function"){
			try{
				if(ep.keydown(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute keydown processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute keydown processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};
	
	this.render.system_event_processor.pickupkeypress	=function(event,render,pickup_component_id){return false;};
	this.render.system_event_processor.keypress			=function(event,render){return false;};
	this.keypress_event_listener=function (event)
	{
		var ep,component_id;
		event.preventDefault();

		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickupkeypress(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.keypress(event,this.render))
			return;
		
		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickupkeypress)=="function")
				try{
					if(ep.pickupkeypress(event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickupkeypress processor error,component id is "+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickupkeypress processor error,component id is "+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				};
		}
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.keyboard))<0)
			return;
		this.render.event_component.keyboard.component_name=component_id;
		ep=this.render.component_event_processor[component_id];	
		if(typeof(ep.keypress)=="function"){
			try{
				if(ep.keypress(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute keypress processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute keypress processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};
	
	this.render.system_event_processor.pickupkeyup	=function(event,render,pickup_component_id){return false;};
	this.render.system_event_processor.keyup		=function(event,render){return false;};
	this.keyup_event_listener=function (event)
	{
		var ep,component_id;
		event.preventDefault();
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickupkeyup(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.keyup(event,this.render))
			return;
		
		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickupkeyup)=="function")
				try{
					if(ep.pickupkeyup(event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickupkeyup processor error,component id is "
								+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickupkeyup processor error,component id is "
								+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.keyboard))<0)
			return;
		this.render.event_component.keyboard.component_name=component_id;
		ep=this.render.component_event_processor[component_id];	
		if(typeof(ep.keyup)=="function"){
			try{
				if(ep.keyup(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute keyup processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute keyup processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};
	
	this.render.system_event_processor.pickupcontextmenu=function(event,pickup_component_id,render)	{return false;};
	this.render.system_event_processor.contextmenu		=function(event,render)						{return false;};
	this.contextmenu_event_listener=function (event)
	{
		var ep,component_id;

		event.preventDefault();
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickupcontextmenu(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.contextmenu(event,this.render))
			return;

		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickupcontextmenu)=="function")
				try{
					if(ep.pickupcontextmenu(event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickupcontextmenu processor error,component id is "+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickupcontextmenuprocessor error,component id is "+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}
		
		if((component_id=this.caculate_component_event_processor(this.render.event_component.mouse))<0)
			return;

		this.render.event_component.mouse.component_name=component_id;
		ep=this.render.component_event_processor[component_id];
		if(typeof(ep.contextmenu)=="function"){
			try{
				if(ep.contextmenu(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute contextmenu processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute contextmenu processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}	
		}
	};
	
	this.render.system_event_processor.pickupgamepadconnected	=function(event,render,pickup_component_id){return false;};
	this.render.system_event_processor.gamepadconnected			=function(event,render){return false;};
	this.gamepadconnected_event_listener=function (event)
	{
		var ep,component_id;
		event.preventDefault();
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickupgamepadconnected(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.gamepadconnected(event,this.render))
			return;
		
		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickupgamepadconnected)=="function")
				try{
					if(ep.pickupgamepadconnected(
							event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickupgamepadconnected processor error,component id is "
								+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickupgamepadconnected processor error,component id is "
								+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}
		if((component_id=this.caculate_component_event_processor(this.render.event_component.gamepad))<0)
			return;
		this.render.event_component.gamepad.component_name=component_id;
		ep=this.render.component_event_processor[component_id];	
		if(typeof(ep.gamepadconnected)=="function"){
			try{
				if(ep.gamepadconnected(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute gamepadconnected processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute gamepadconnected processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};
	this.render.system_event_processor.pickupgamepaddisconnected	=function(event,render,pickup_component_id){return false;};
	this.render.system_event_processor.gamepaddisconnected			=function(event,render){return false;};
	this.gamepaddisconnected_event_listener=function (event)
	{
		var ep,component_id;
		event.preventDefault();
		
		if(this.render.pickup.component_id>=0)
			if(this.render.pickup.component_id<(this.render.component_event_processor.length))
				if(this.render.system_event_processor.pickupgamepaddisconnected(event,this.render.pickup.component_id,this.render))
					return;
		if(this.render.system_event_processor.gamepaddisconnected(event,this.render))
			return;
		
		if((ep=this.caculate_pickup_event_processor())!=null){
			if(typeof(ep.pickupgamepaddisconnected)=="function")
				try{
					if(ep.pickupgamepaddisconnected(
							event,this.render.pickup.component_id,this.render))
						return;
				}catch(e){
					if(this.render.parameter.debug_mode_flag){
						alert("Execute pickupgamepaddisconnected processor error,component id is "
								+this.render.pickup.component_id.toString());
						alert(e.toString());
					}else{
						console.log("Execute pickupgamepaddisconnected processor error,component id is "
								+this.render.pickup.component_id.toString());
						console.log(e.toString());
					}
					return;
				}
		}

		if((component_id=this.caculate_component_event_processor(this.render.event_component.gamepad))<0)
			return;
		this.render.event_component.gamepad.component_name=component_id;
		ep=this.render.component_event_processor[component_id];	
		if(typeof(ep.gamepaddisconnected)=="function"){
			try{
				if(ep.gamepaddisconnected(event,component_id,this.render))
					return;
			}catch(e){
				if(this.render.parameter.debug_mode_flag){
					alert("Execute gamepaddisconnected processor error,component id is "+component_id.toString());
					alert(e.toString());
				}else{
					console.log("Execute gamepaddisconnected processor error,component id is "+component_id.toString());
					console.log(e.toString());
				}
				return;
			}
		}
	};

	var cur=this;

	this.render.canvas.addEventListener(	"mousemove",
			function(event){cur.mousemove_event_listener			(event);},false);
	this.render.canvas.addEventListener(	"mousedown",
			function(event){cur.mousedown_event_listener			(event);},false);
	this.render.canvas.addEventListener(	"mouseup",
			function(event){cur.mouseup_event_listener				(event);},false);
	this.render.canvas.addEventListener(	"dblclick",
			function(event){cur.dblclick_event_listener				(event);},false);
	this.render.canvas.addEventListener(	"mousewheel",
			function(event){cur.mousewheel_event_listener			(event);},false);
	this.render.canvas.addEventListener(	"DOMMouseScroll",
			function(event){cur.mousewheel_event_listener			(event);},false);
	
	this.render.canvas.addEventListener(	"touchstart",
			function(event){cur.touchstart_event_listener			(event);},false);
	this.render.canvas.addEventListener(	"touchend",
			function(event){cur.touchend_event_listener				(event);},false);
	this.render.canvas.addEventListener(	"touchmove",
			function(event){cur.touchmove_event_listener			(event);},false);
	
	this.render.canvas.addEventListener(	"keydown",
			function(event){cur.keydown_event_listener				(event);},false);
	this.render.canvas.addEventListener(	"keypress",
			function(event){cur.keypress_event_listener				(event);},false);
	this.render.canvas.addEventListener(	"keyup",
			function(event){cur.keyup_event_listener				(event);},false);
	
	this.render.canvas.addEventListener(	"contextmenu", 
			function(event){cur.contextmenu_event_listener			(event);},false);

	window.addEventListener("gamepadconnected",
			function(event){cur.gamepadconnected_event_listener		(event);},false);
	window.addEventListener("gamepaddisconnected",
			function(event){cur.gamepaddisconnected_event_listener	(event);},false);

	this.render.canvas.focus();
};