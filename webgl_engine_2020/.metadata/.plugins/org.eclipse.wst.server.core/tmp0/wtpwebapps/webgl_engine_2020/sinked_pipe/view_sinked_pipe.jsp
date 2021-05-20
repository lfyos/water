<%@page language="java"	contentType="charset=UTF-8" pageEncoding="UTF-8"%>

<html>
<head>

<script type="text/javascript" 	src="../interface.jsp?function_name=construct_render_object"></script>
<script type="text/javascript">

<%
	out.println("var render,operation_str=\""+request.getParameter("operation")+"\";");
%>

function body_onload()
{
	document.oncontextmenu = function(){return false;}

	construct_render_object(
		document.getElementById("my_canvas"),		//绘图画布
		"NoName",									//用户名
		"NoPassword",								//用户密码
		"chinese",									//语言
		"cad",										//场景名称
		"sinked_pipe",								//连接名称，用于多人协同操作一个场景
		[											//初始化参数
			["sub_directory",			"sinked_pipe"],				//显示内容
			["coordinate",				"location.xyz.txt"],		//坐标系选择
			["change_part",				"display_part:radar_part"],
			["change_component",		"radar_component:display_component"],
			["part_type",				""	],
			["max_loading_number",		10	]
		],
		function(my_render)					//创建场景成功后执行的函数
		{
			render=my_render;				//把场景变量保存到变量render中
			document.title=render.title;	//设置网页标题
			
			render.system_call_processor.turnonoff_level_of_detail(false,false);
			render.system_call_processor.set_display_mode(
					false,true,true,true,true,true,false,false,false,false);
			render.system_call_processor.set_camera_change_type(true);
			
			if((operation_str==null)||(operation_str=="null"))
				render.user_call_processor.turnoff_control_panel();
			else
				render.user_call_processor.turnon_control_panel(operation_str);
		});
}

</script>

</head>

<body onload="body_onload();" onunload="render.terminate();" onresize="render.user_call_processor.resize();">

<div style="text-align:left"	align="center">
	<canvas id="my_canvas"		tabindex="0"					></canvas>
	<iframe id="my_part_list"	tabindex="1"	hidden="hidden"	></iframe>
</div>

</body>


</html>
