<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">
<meta http-equiv="Pragma" content="no-cache">

<title></title>

<script type="text/javascript">

var lfy_render;
<%
{
	String str;
	out.print  ("var my_user_name=\""		+(((str=request.getParameter("user_name"		 ))==null)?""	:str)+"\",");
	out.print  ("my_pass_word=\""			+(((str=request.getParameter("pass_word"		 ))==null)?""	:str)+"\",");
	out.print  ("my_language=\""			+(((str=request.getParameter("language"			 ))==null)?""	:str)+"\",");
	out.print  ("my_scene_name=\""			+(((str=request.getParameter("scene_name"		 ))==null)?""	:str)+"\",");
	out.println("my_link_name=\""			+(((str=request.getParameter("link_name"		 ))==null)?""	:str)+"\";");
	
	out.print  ("var my_change_part=\""		+(((str=request.getParameter("change_part"		 ))==null)?""	:str)+"\",");
	out.print  ("my_change_component=\""	+(((str=request.getParameter("change_component"	 ))==null)?""	:str)+"\",");
	out.print  ("my_part_type=\""			+(((str=request.getParameter("part_type"		 ))==null)?""	:str)+"\",");
	out.print  ("my_type_sub_directory=\""	+(((str=request.getParameter("type_sub_directory"))==null)?""	:str)+"\",");
	out.print  ("my_scene_sub_directory=\""	+(((str=request.getParameter("scene_sub_directory"))==null)?""	:str)+"\",");
	out.print  ("my_coordinate=\""			+(((str=request.getParameter("coordinate"		 ))==null)?"xyz":str)+"\",");
	out.print  ("my_max_loading_number=\""	+(((str=request.getParameter("max_loading_number"))==null)?"5"	:str)+"\";");
}
%>

async function body_onload()
{
	lfy_render=await (await import("./graphics_engine_interface")).main(
		["my_canvas"],
		{
			user_name			:	my_user_name,				//用户名	
			pass_word			:	my_pass_word,				//用户密码
			language			:	my_language,				//语言
			scene_name			:	my_scene_name,				//场景名称
			link_name			:	my_link_name,				//连接名称,用于多人协同操作一个场景
			
			change_part			:	my_change_part,				//part换名
			change_component	:	my_change_component,		//component换名
			part_type			:	my_part_type,				//part类型
			type_sub_directory	:	my_type_sub_directory,		//显示内容type
			scene_sub_directory	:	my_scene_sub_directory,		//显示内容scene
			
			coordinate			:	my_coordinate,				//坐标系选择
			max_loading_number	:	my_max_loading_number,		//同时下载数量
			
			multisample			:	4							//多重采样数
		},
		function(			//9.进度条绘制函数，如果不配置该函数，则使用系统内部提供的默认进度条绘制函数
				webgpu_canvas_id,				//绘制结束后，绘制结果拷贝到哪个canvas 
				process_bar_canvas,				//绘制进度条的画布canvas
				progress_bar_ctx,				//绘制进度条的2D上下文
				process_bar_caption,			//进度条当前进度标题，该标题和语言有关，目前系统中仅仅配置了中文和英文相关标题
				progress_bar_value,				//进度条当前进度，取值范围0.00~1.00
				process_bar_time_length,		//进度条当前进度经过的时间，单位是毫秒
				process_bar_engine_time_length,	//进度条所有进度经过的时间，单位是毫秒
				time_unit)						//和语言有关时间单位标题，比如中文是秒，英文是second
		{
			var p_separator=Math.round(process_bar_canvas.width*progress_bar_value);
			progress_bar_ctx.fillStyle="rgb(127,127,127)";
			progress_bar_ctx.fillRect(0,			0,	p_separator,				process_bar_canvas.height);
			progress_bar_ctx.fillStyle="rgb(255,255,255)";
			progress_bar_ctx.fillRect(p_separator,	0,	process_bar_canvas.width,	process_bar_canvas.height);
			
			if((progress_bar_value=(Math.round(1000.0*progress_bar_value)/10.0).toString()).indexOf(".")<0)
				progress_bar_value+=".0";
			var display_value=process_bar_caption+":"+progress_bar_value+"%,";
			display_value+=(Math.round(process_bar_time_length/1000.0)).toString()+time_unit+",";
			display_value+=(Math.round(process_bar_engine_time_length/1000.0)).toString()+time_unit;
			
			progress_bar_ctx.font			="bold 48px Arial";
			progress_bar_ctx.textBaseline	="middle";
			progress_bar_ctx.fillStyle		="rgb(192,192,192)";
			progress_bar_ctx.textAlign		="center";		
			progress_bar_ctx.fillText(display_value,	process_bar_canvas.width*0.5,	process_bar_canvas.height*0.5);
		});
		document.title=lfy_render.title;	//设置网页标题
}
function body_onresize()
{
	var canvas_object=document.getElementById("my_canvas");
	canvas_object.width	=window.innerWidth *0.95;
	canvas_object.height=window.innerHeight*0.95;
}
function body_onunload()
{
	if(typeof(lfy_render)=="object")
		if(lfy_render!=null)
			if(typeof(lfy_render.destroy)=="function")
				lfy_render.destroy();
}

</script>

</head>

<body onload="body_onresize();body_onload();" onresize="body_onresize();" onunload="body_onunload();" >

<div align="center">
<canvas id="my_canvas"		tabindex="0"	></canvas>
</div>

</body>

</html>
