<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">
<meta http-equiv="Pragma" content="no-cache">

<title></title>

<script type="text/javascript">
<%
{
	String str;
	out.println("var user_name=\""				+(((str=request.getParameter("user_name"				))==null)?"NoName":str)+"\";");
	out.println("var pass_word=\""				+(((str=request.getParameter("pass_word"				))==null)?"NoPassword":str)+"\";");	
	out.println("var scene_name=\""				+(((str=request.getParameter("scene_name"				))==null)?"test":str)+"\";");
	out.println("var link_name=\""				+(((str=request.getParameter("link_name"				))==null)?"":str)+"\";");
	out.println("var change_part=\""			+(((str=request.getParameter("change_part"				))==null)?"":str)+"\";");
	out.println("var change_component=\""		+(((str=request.getParameter("change_component"			))==null)?"":str)+"\";");
	out.println("var part_type=\""				+(((str=request.getParameter("part_type"				))==null)?"":str)+"\";");
	out.println("var sub_directory=\""			+(((str=request.getParameter("sub_directory"			))==null)?"":str)+"\";");
	out.println("var coordinate=\""				+(((str=request.getParameter("coordinate"				))==null)?"xyz":str)+"\";");
	out.println("var max_loading_number=\""		+(((str=request.getParameter("max_loading_number"		))==null)?"5":str)+"\";");
}

%>

</script>

<script type="text/javascript" src="../interface.jsp?function_name=construct_render_object"></script>

<script type="text/javascript">

var render,collector_stack_version=0,canvas_2d_context;

function body_onload()
{
//	document.oncontextmenu=function(){return false;}

	construct_render_object(
		document.getElementById("my_canvas"),
		user_name,pass_word,"chinese",scene_name,link_name,
		[
			["change_part",				change_part			],
			["change_component",		change_component	],
			["part_type",				part_type			],
			["sub_directory",			sub_directory		],
			["coordinate",				coordinate			],
			["max_loading_number",		max_loading_number	]
		],
		function(my_render)
		{
			render=my_render;
			document.title=render.title;
			
			render.debug_call_processor.turnoff_control_panel();
			
			render.append_routine_function(
					function(render)
					{
						if(render.collector_stack_version!=collector_stack_version){
							collector_stack_version=render.collector_stack_version;
							render.debug_call_processor.reload_control_panel();
						}
						return true;
					},"init in draw.jsp"
				);
			
			document.getElementById("my_processor_bar").setAttribute("hidden",true);
			document.getElementById("my_canvas").		removeAttribute("hidden");
		},
		500,
		true,
		function(	process_bar_caption,		process_bar_title,
					process_bar_current,		process_bar_max,
					process_bar_time_length,	process_bar_engine_time_length,
					process_bar_id)
		{
			var canvas=document.getElementById("my_processor_bar");
			canvas.width	=window.innerWidth *0.975;
			canvas.height	=window.innerHeight*0.925;
			if(typeof(canvas_2d_context)=="undefined"){
				canvas_2d_context=canvas.getContext("2d");
				if(canvas_2d_context==null){
					alert("canvas_2d_context==null");
					return true;
				}
			}
			
			canvas_2d_context.font			="bold 64px Arial";
			canvas_2d_context.textAlign		="center";
			canvas_2d_context.textBaseline	="middle";
			canvas_2d_context.strokeStyle	="rgb(255,0,0)";
			
			canvas_2d_context.beginPath();
			canvas_2d_context.moveTo(Math.round(canvas.width*process_bar_current/process_bar_max),0);
			canvas_2d_context.lineTo(Math.round(canvas.width*process_bar_current/process_bar_max),canvas.height);
			canvas_2d_context.stroke();

			var display_value=process_bar_current/process_bar_max;
			display_value=Math.round(1000*display_value);
			display_value=display_value/10;
			
			canvas_2d_context.strokeText(
				process_bar_caption+":"+(display_value.toString())+"%",
				canvas.width/2.0,
				canvas.height/2.0);

			return true;
		}
	);
}

function body_onunload()
{
	if(typeof(render)=="object")
		if(render!=null)
			if(typeof(render.terminate)=="function")
				render.terminate();
}

function body_onresize()
{
	if(typeof(render)=="object")
		if(render!=null)
			if(typeof(render.debug_call_processor)=="object")
				if(render.debug_call_processor!=null)
					if(typeof(render.debug_call_processor.resize)=="function")
						render.debug_call_processor.resize();
}

</script>

</head>

<body onload="body_onload();" onunload="body_onunload();" onresize="body_onresize();">

<div style="text-align:left"		align="center"						>
	<canvas id="my_processor_bar"	tabindex="0"						></canvas>
	<canvas id="my_canvas"			tabindex="1"	hidden="hidden"		></canvas>
	<iframe id="my_part_list"		tabindex="2"	hidden="hidden"		></iframe>
</div><br/>
<output id="debug"></output>

</body>

</html>
