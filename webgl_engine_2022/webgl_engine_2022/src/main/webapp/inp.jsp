<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<html>
<head>

<script type="text/javascript"
	src="./interface.jsp?function_name=construct_render_object"></script>
<script type="text/javascript">

<%
{
	String str;
	out.println("var change_part=\""	+(((str=request.getParameter("part"		))==null)?"8":str)		+"\";");
	out.println("var display_value=\""	+(((str=request.getParameter("value"	))==null)?"8":str)		+"\";");
	out.println("var display_frame="	+(((str=request.getParameter("frame"	))==null)?"false":str)	+";");
}

%>

var render_object;

function body_onload()
{
	var my_canvas=document.getElementById("my_canvas");	
	my_canvas.width	=window.innerWidth *0.975;
	my_canvas.height=window.innerHeight*0.925;
	
	construct_render_object(
		document.getElementById("my_canvas"),		//绘图画布
		"NoName",									//用户名
		"NoPassword",								//用户密码
		"chinese",									//语言
		"test",										//场景名称
		"",											//连接名称，用于多人协同操作一个场景
		[											//初始化参数
			["change_part",				"display_part:inp_part_"+change_part],	//part 换名
			["max_loading_number",		10	]						//同时下载数量
		],
		function(my_render_object)					//创建场景成功后执行的函数
		{
			render_object=my_render_object;			//把场景变量保存到变量render_object中
			document.title=render_object.title;		//设置网页标题
			
			render_object.call_server_component("movement_manager","all",[
				["operation",			"design"],
				["move_method",			"design"],
				["design_operation",	"set_location"],
				["coordinate",			"component"],
				["component_name",		"display_component"],
				["mx", 0],
				["my", 0],
				["mz", 0],
				["rx",270],
				["ry",180],
				["rz", 0]		
			]);
			if(display_frame)
				render_object.system_call_processor.set_display_mode(
					true,true,true,true,true,true,false,true,false,false);
			render_object.system_call_processor.set_display_value(display_value);
		});
}

</script>

</head>

<body onload="body_onload();" onunload="render_object.terminate();">

	<div style="text-align: center" align="center">
		<canvas id="my_canvas" tabindex="0"></canvas>
	</div>

</body>

</html>
