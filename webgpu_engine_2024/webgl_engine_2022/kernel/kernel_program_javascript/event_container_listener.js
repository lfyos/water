function construct_scene_container_event_listener(my_canvas_id,my_canvas_array,my_scene_container)
{
	var canvas_id		=my_canvas_id;
	var canvas_object	=my_canvas_array[my_canvas_id];
	var scene_container	=my_scene_container
	
	function get_scene_event_listener(event)
	{
		if(scene_container.terminate_flag)
			return null;
		if(scene_container.current_scene_id<0)
			return null;
		if(scene_container.current_scene_id>=scene_container.scene_array.length)
			return null;
		
		var my_scene;	
		if(typeof(my_scene=scene_container.scene_array[scene_container.current_scene_id])!="object")
			return null;
		if(my_scene==null)
			return null;
		if(my_scene.terminate_flag)
			return null;
		
		var my_event_listener_array;
		if(!(Array.isArray(my_event_listener_array=my_scene.event_listener)))
			return null;
		if((canvas_id<0)||(canvas_id>=my_event_listener_array.length))
			return null;
		
		canvas_object.focus();
		
		my_scene.last_event_time=(new Date()).getTime();
		return my_event_listener_array[canvas_id];
	}
	
	function mousemove_fun(event)	
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.mousemove_event_listener(event);
	};
	function mousedown_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.mousedown_event_listener(event);
	};
	function mouseup_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.mouseup_event_listener(event);
	};
	function dblclick_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.dblclick_event_listener(event);
	};
	function mousewheel_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.mousewheel_event_listener(event);
	};
	function mouseleave_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.mouseleave_event_listener(event);
	};
	function mouseenter_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.mouseenter_event_listener(event);
	};
	function mouseout_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.mouseout_event_listener(event);
	};
	function mouseover_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.mouseover_event_listener(event);
	};
	function contextmenu_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.contextmenu_event_listener(event);
	};
	function touchstart_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.touchstart_event_listener(event);
	};
	function touchend_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.touchend_event_listener(event);
	};
	function touchmove_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.touchmove_event_listener(event);
	};
	function keydown_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.keydown_event_listener(event);
	};
	function keypress_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.keypress_event_listener(event);
	};
	function keyup_fun(event)
	{
		var my_event_listener;
		if((my_event_listener=get_scene_event_listener(event))!=null)
			my_event_listener.keyup_event_listener(event);
	};

	canvas_object.addEventListener("mousemove",			mousemove_fun,		false);
	canvas_object.addEventListener("mousedown",			mousedown_fun,		false);
	canvas_object.addEventListener("mouseup",			mouseup_fun,		false);
	canvas_object.addEventListener("dblclick",			dblclick_fun,		false);
	canvas_object.addEventListener("mousewheel",		mousewheel_fun,		false);
	canvas_object.addEventListener("DOMMouseScroll",	mousewheel_fun,		false);
	canvas_object.addEventListener("mouseleave",		mouseleave_fun,		false);
	canvas_object.addEventListener("mouseenter",		mouseenter_fun,		false);
	canvas_object.addEventListener("mouseout",			mouseout_fun,		false);
	canvas_object.addEventListener("mouseover",			mouseover_fun,		false);
	
	canvas_object.addEventListener("touchstart",		touchstart_fun,		false);
	canvas_object.addEventListener("touchend",			touchend_fun,		false);
	canvas_object.addEventListener("touchmove",			touchmove_fun,		false);
	
	canvas_object.addEventListener("keydown",			keydown_fun,		false);
	canvas_object.addEventListener("keypress",			keypress_fun,		false);
	canvas_object.addEventListener("keyup",				keyup_fun,			false);
	
	canvas_object.addEventListener("contextmenu", 		contextmenu_fun,	false);
	
	this.destroy=function()
	{
		this.destroy=null;
		
		canvas_object.removeEventListener("mousemove",		mousemove_fun);
		canvas_object.removeEventListener("mousedown",		mousedown_fun);
		canvas_object.removeEventListener("mouseup",		mouseup_fun);
		canvas_object.removeEventListener("dblclick",		dblclick_fun);
		canvas_object.removeEventListener("mousewheel",		mousewheel_fun);
		canvas_object.removeEventListener("DOMMouseScroll",	mousewheel_fun);
		canvas_object.removeEventListener("mouseleave",		mouseleave_fun);
		canvas_object.removeEventListener("mouseenter",		mouseenter_fun);
		canvas_object.removeEventListener("mouseout",		mouseout_fun);
		canvas_object.removeEventListener("mouseover",		mouseover_fun);
				
		canvas_object.removeEventListener("contextmenu", 	contextmenu_fun);
					
		canvas_object.removeEventListener("touchstart",		touchstart_fun);
		canvas_object.removeEventListener("touchend",		touchend_fun);
		canvas_object.removeEventListener("touchmove",		touchmove_fun);
					
		canvas_object.removeEventListener("keydown",		keydown_fun);
		canvas_object.removeEventListener("keypress",		keypress_fun);
		canvas_object.removeEventListener("keyup",			keyup_fun);
	}
}
