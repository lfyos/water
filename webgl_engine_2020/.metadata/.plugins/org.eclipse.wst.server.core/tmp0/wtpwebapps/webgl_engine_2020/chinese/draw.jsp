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

var render,collector_stack_version=0;

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
			
			render.user_call_processor.turnoff_control_panel();
			
			render.append_routine_function(
					function(render)
					{
						if(render.collector_stack_version!=collector_stack_version){
							collector_stack_version=render.collector_stack_version;
							render.user_call_processor.reload_control_panel();
						}
						return true;
					},"init in draw.jsp"
				);
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
			if(typeof(render.user_call_processor)=="object")
				if(render.user_call_processor!=null)
					if(typeof(render.user_call_processor.resize)=="function")
						render.user_call_processor.resize();
}

</script>

</head>

<body onload="body_onload();" onunload="body_onunload();" onresize="body_onresize();">

<div style="text-align:left"	align="center"						>
	<canvas id="my_canvas"		tabindex="0"						></canvas>
	<iframe id="my_part_list"	tabindex="1"	hidden="hidden"		></iframe>
</div><br/>
<output id="debug"></output>

</body>

</html>
