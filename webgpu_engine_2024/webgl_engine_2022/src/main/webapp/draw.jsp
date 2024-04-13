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
	out.print  ("my_fast_load=\""			+(((str=request.getParameter("fast_load"		 ))==null)?""	:str)+"\",");
	out.print  ("my_type_sub_directory=\""	+(((str=request.getParameter("type_sub_directory"))==null)?""	:str)+"\",");
	out.print  ("my_scene_sub_directory=\""	+(((str=request.getParameter("scene_sub_directory"))==null)?""	:str)+"\",");
	out.print  ("my_scene_tmp_directory=\""	+(((str=request.getParameter("scene_tmp_directory"))==null)?""	:str)+"\",");
	out.print  ("my_client_sub_directory=\""+(((str=request.getParameter("client_sub_directory"))==null)?""	:str)+"\",");
	out.print  ("my_coordinate=\""			+(((str=request.getParameter("coordinate"		 ))==null)?""	:str)+"\";");
}
%>

async function body_onload()
{
	var servlet_url	="./webgpu_interface.servlet";
	var jsp_url		="./webgpu_interface.jsp";
	
	lfy_render=await (await import(jsp_url)).main(
		["my_canvas"],
		{
			user_name				:	my_user_name,				//用户名	
			pass_word				:	my_pass_word,				//用户密码
			language				:	my_language,				//语言
			scene_name				:	my_scene_name,				//场景名称
			link_name				:	my_link_name,				//连接名称,用于多人协同操作一个场景
			
			change_part				:	my_change_part,				//part换名
			change_component		:	my_change_component,		//component换名
			
			part_type				:	my_part_type,				//part类型
			fast_load				:	my_fast_load,				//快速装在scene信息
			
			type_sub_directory		:	my_type_sub_directory,		//显示内容type
			scene_sub_directory		:	my_scene_sub_directory,		//显示内容scene
			scene_tmp_directory		:	my_scene_tmp_directory,		//显示内容scene tmp directory
			client_sub_directory	:	my_client_sub_directory,	//显示内容client
			
			coordinate				:	my_coordinate,				//坐标系选择
			
			max_loading_number		:	5					,		//同时下载数量
			multisample				:	4							//多重采样数
		});
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

<div align	="center">
<canvas id	="my_canvas"	tabindex="0"	></canvas>
</div>

</body>

</html>
