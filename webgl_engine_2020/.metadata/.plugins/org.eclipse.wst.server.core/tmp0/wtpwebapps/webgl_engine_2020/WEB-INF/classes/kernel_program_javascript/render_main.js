function redraw(render)
{
	var redraw_object=new Object();
	redraw_object.touch_time=0;
	
	function render_routine()
	{
		var my_current_time=(new Date()).getTime();
		var render_interval_length=my_current_time-redraw_object.touch_time;
		redraw_object.touch_time=my_current_time;
		
		render.do_render(render_interval_length);
		window.requestAnimationFrame(render_routine);
	};

	function touch_routine()
	{
		var my_current_time=(new Date()).getTime();
		if(!(render.can_do_render_request_flag)){
			redraw_object.touch_time=my_current_time;
			return;
		}
		var my_time_length=my_current_time-redraw_object.touch_time;
		var max_time_length=render.parameter.engine_touch_time_length/1000/1000;
		if(my_time_length<max_time_length)
			return;
		redraw_object.touch_time=my_current_time;
		
		var my_url=render.url;
		my_url+="?channel="+render.channel;
		my_url+="&command=touch";
		try{
			var my_ajax=new XMLHttpRequest();
			my_ajax.open("GET",my_url,true);
			my_ajax.send(null);
		}catch(e){
			;
		};
	};
	function data_load_routine()
	{
		var loaded_data_length=render.buffer_object.loaded_buffer_object_data_length;
		if(loaded_data_length>=render.parameter.total_buffer_object_data_length){
			render_routine();
			setInterval(touch_routine,render.parameter.engine_touch_time_length/1000/1000);
		}else{
			var my_current_time=(new Date()).getTime();
			var render_interval_length=my_current_time-redraw_object.touch_time;
			redraw_object.touch_time=my_current_time;
			render.do_render(render_interval_length);
			setTimeout(data_load_routine,render.parameter.engine_load_time_length/1000/1000);
		};
	};
	data_load_routine();
};

function render_initialization(initialization_url,render,user_initialization_function)
{
	try{
		var my_ajax=new XMLHttpRequest();
		my_ajax.onreadystatechange=function()
		{
			if(my_ajax.readyState!=4)
				return;
			if(my_ajax.status!=200){
				if(render.parameter.debug_mode_flag)
					alert("Loading system_initialization_function response status error: "+my_ajax.status.toString());
				else
					console.log("Loading system_initialization_function response status error: "+my_ajax.status.toString());
				return;
			};
			
			var response_data;
			try{
				response_data=eval(my_ajax.responseText);
			}catch(e){
				if(render.parameter.debug_mode_flag){
					alert("Parse response_fun_array error: "+e.toString());
					alert(my_ajax.responseText);
				}else{
					console.log("Parse response_fun_array error: "+e.toString());
					console.log(my_ajax.responseText);
				};
				return;
			};
			var component_number				=response_data[0];
			var sorted_component_name_id		=response_data[1];
			var	part_component_id_and_driver_id	=response_data[2];
			var response_fun_array				=response_data[3];
			var program_data					=response_data[4];
			
			render.process_part_component_id_and_driver_id(component_number,
					sorted_component_name_id,part_component_id_and_driver_id);
			
			for(var i=0,ni=program_data.length;i<ni;i++){
				var permanent_render_id	=program_data[i][0];
				var decode_function		=program_data[i][1];
				var draw_function		=program_data[i][2];
				var vertex_program		=program_data[i][3];
				var fragment_program	=program_data[i][4];
				var geometry_program	=program_data[i][5];
				var tess_control_Program=program_data[i][6];
				var tess_evalueprogram	=program_data[i][7];
				
				render.render_program.compile_program(i,
						permanent_render_id,
						decode_function,
						draw_function,
						vertex_program,
						fragment_program,
						geometry_program,
						tess_control_Program,
						tess_evalueprogram);
			};
			
			for(var i=0,ni=response_fun_array.length;i<ni;i++){
				if(typeof(response_fun_array[i])!="object")
					continue;
				
				var component_id=response_fun_array[i].component_id;
				var component_name=response_fun_array[i].component_name;
				var init_function=response_fun_array[i].initialization_function;
				
				if(typeof(init_function)!="function")
					continue;
				try{
					init_function(component_name,component_id,render);
				}catch(e){
					if(render.parameter.debug_mode_flag){
						alert("Execute initialization_function_array error: "+i.toString()+":"+e.toString());
						alert("component_name	:	"+component_name.toString());
						alert("component_id		:	"+component_id.toString());
						alert(my_ajax.responseText);
					}else{
						console.log("Execute initialization_function_array error: "+i.toString()+":"+e.toString());
						console.log("component_name	:	"+component_name.toString());
						console.log("component_id	:	"+component_id.toString());
						console.log(my_ajax.responseText);
					};
				};
			};
			
			if(typeof(user_initialization_function)=="function"){
				try{
					user_initialization_function(render);
				}catch(e){
					if(render.parameter.debug_mode_flag){
						alert("Execute user_initialization_function error: "+e.toString());
					}else{
						console.log("Execute user_initialization_function error: "+e.toString());
					};
				};
			};
			redraw(render);
			return;
		};
		my_ajax.open("GET",initialization_url,true);
		my_ajax.send(null);
	}catch(e){
		if(render.parameter.debug_mode_flag)
			alert("Loading system_initialization_function fail: "+e.toString());
		else
			console.log("Loading system_initialization_function fail: "+e.toString());
	};
};

function create_webgl_context(my_canvas)
{
	var webgl_context_parameter_array=
	[
		{ 	
			context_name:	"webgl2",
			alpha:true,
			depth:true,
			antialias: true,
    		stencil: true,
    		premultipliedAlpha:true,
    		preserveDrawingBuffer:true,
    		powerPreference:"high-performance",
			failIfMajorPerformanceCaveat:false
		},
		{ 	
			context_name:	"experimental-webgl2",
			alpha:true,
			depth:true,
			antialias: true,
    		stencil: true,
    		premultipliedAlpha:true,
    		preserveDrawingBuffer:true,
    		powerPreference:"high-performance",
			failIfMajorPerformanceCaveat:false
		}
	];	
	
	for(var i=0,ni=webgl_context_parameter_array.length;i<ni;i++){
		try{
			var my_gl=my_canvas.getContext(
					webgl_context_parameter_array[i].context_name,
					webgl_context_parameter_array[i]);
			if(my_gl!=null)
				return my_gl;
		}catch(e){
	    	;
		};
	}
	alert("Could not initialise WebGL2.0, enable WebGL2.0 please");
    return null;
}
function request_create_engine(create_engine_sleep_time_length_scale,
	create_engine_sleep_time_length,create_engine_max_sleep_time_length,
	request_url,my_gl,my_user_name,my_pass_word,my_canvas,my_url,my_language_name,
	my_user_initialization_function)
{
	var my_ajax;
	try{
		my_ajax=new XMLHttpRequest();
		my_ajax.onreadystatechange=function()
		{
			if(my_ajax.readyState!=4)
				return;
			if(my_ajax.status!=200){
				alert("Initialization response status error: "+my_ajax.status.toString());
				return;
			};
			var response_data;
			try{
				response_data=JSON.parse(my_ajax.responseText);
			}catch(e){
				alert("Web server error, or Create Too many scenes:  "+e.toString());
				alert(my_ajax.responseText);
				return;
			}
			if(typeof(response_data)=="boolean"){
				var new_create_engine_sleep_time_length=create_engine_sleep_time_length;
				new_create_engine_sleep_time_length*=create_engine_sleep_time_length_scale;
				if(new_create_engine_sleep_time_length>create_engine_max_sleep_time_length)
					new_create_engine_sleep_time_length=create_engine_max_sleep_time_length;
				setTimeout(
					function()
					{
						request_create_engine(create_engine_sleep_time_length_scale,
							new_create_engine_sleep_time_length,create_engine_max_sleep_time_length,
							request_url,my_gl,my_user_name,my_pass_word,my_canvas,my_url,
							my_language_name,my_user_initialization_function);
					},create_engine_sleep_time_length);
				return;
			};
			var render,initialization_url=response_data.pop();
			try{
				render=new construct_render_routine(
						my_gl,my_user_name,my_pass_word,my_canvas,
						my_url,my_language_name,response_data);
			}catch(e){
				alert("Construct scene from server data fail:  "+e.toString());
				alert(my_ajax.responseText);
				return;
			};
			try{
				render_initialization(initialization_url,render,my_user_initialization_function);
			}catch(e){
				alert("Execute scene initialization fail:  "+e.toString());
				alert(my_ajax.responseText);
				return;
			};
			return;
		};
		my_ajax.open("GET",request_url,true);
		my_ajax.send(null);
	}catch(e){
		alert("System Initialization fail!");
		alert(my_ajax.responseText);
		alert(e.toString());
	};
}

function render_main(create_engine_sleep_time_length_scale,
	create_engine_sleep_time_length,create_engine_max_sleep_time_length,
	my_canvas,my_url,my_user_name,my_pass_word,my_language_name,
	my_scene_name,my_link_name,my_initialization_parameter,my_user_initialization_function)
{
	my_user_name			=(typeof(my_user_name			)!="string")?"NoName"		:(my_user_name.trim());
	my_pass_word			=(typeof(my_pass_word			)!="string")?"NoPassword"	:(my_pass_word.trim());
	my_language_name		=(typeof(my_language_name		)!="string")?"english"		:(my_language_name.trim());
	my_scene_name			=(typeof(my_scene_name			)!="string")?""				:(my_scene_name.trim());
	my_link_name			=(typeof(my_link_name			)!="string")?""				:(my_link_name.trim());

	var my_gl;
	if((my_gl=create_webgl_context(my_canvas))==null)
    	return;
  
	var max_draw_buffers=my_gl.getParameter(my_gl.MAX_DRAW_BUFFERS); 
	var max_color_attatchments=my_gl.getParameter(my_gl.MAX_COLOR_ATTACHMENTS);
	if((max_draw_buffers<4)||(max_color_attatchments<4)){
		alert(	  "MAX_DRAW_BUFFERS is "		+(max_draw_buffers.toString())
				+",MAX_COLOR_ATTACHMENTS is "	+(max_color_attatchments.toString())
				+",they are to small!,they must be equal or greater than 4");
    	return;
	};
	var request_url=my_url+"?channel=creation&command=creation&language="+my_language_name;
	request_url+="&user_name=" +my_user_name	+"&pass_word="+my_pass_word;
	request_url+="&scene_name="+my_scene_name	+"&link_name="+my_link_name;
	
	if(typeof(my_initialization_parameter)=="object")
		if(typeof(my_initialization_parameter.length)=="number")
			for(var i=0,ni=my_initialization_parameter.length;i<ni;i++){
				var parameter_item	=my_initialization_parameter[i];
				var parameter_name	=parameter_item[0].toString().trim();
				var parameter_value	=parameter_item[1].toString().trim();
				request_url+="&"+parameter_name+"="+parameter_value;
			};
	request_create_engine(create_engine_sleep_time_length_scale,
		create_engine_sleep_time_length,create_engine_max_sleep_time_length,
		request_url,my_gl,my_user_name,my_pass_word,my_canvas,my_url,my_language_name,
		my_user_initialization_function);
};
