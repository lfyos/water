<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">
<meta http-equiv="Pragma" content="no-cache">

<title>设计</title>

<script type="text/javascript">

<%
{
	String render_windows_name=request.getParameter("windows");
	String movement_component_name=request.getParameter("movement_component_name");
	String render_string;
	
	String mount_call_processor_string=request.getParameter("windows");
	mount_call_processor_string=((mount_call_processor_string==null)?"parent":mount_call_processor_string);
	if(mount_call_processor_string.compareTo("top")==0)
		mount_call_processor_string="window.top.";
	else if(mount_call_processor_string.compareTo("parent")==0)
		mount_call_processor_string="window.parent.";
	else if(mount_call_processor_string.compareTo("this")==0)
		mount_call_processor_string="";
	else
		mount_call_processor_string ="window.frames[\""+mount_call_processor_string+"\"].";
	
	out.print("var mcp="+mount_call_processor_string+"render.component_call_processor["
		+mount_call_processor_string+"render.get_component_to_id_object(\""
		+movement_component_name+"\").component_id];");
};
%>

function set_point_number(response_data,render)
{
	document.getElementById("point_number").value=response_data;
}
function create_coordinate_id()
{
	var my_coordinate_flag=0;
	if(document.getElementById("component_coordinate").checked)
		my_coordinate_flag=0;
	else if(document.getElementById("camera_coordinate").checked)
		my_coordinate_flag=1;
	else if(document.getElementById("view_coordinate").checked)
		my_coordinate_flag=2;
	else
		my_coordinate_flag=-1;
	return my_coordinate_flag;
}
function get_location()
{
	mcp.get_component_location(null,create_coordinate_id(),
		function(response_data,render)
		{
			document.getElementById("point_number").value=response_data.number;
			
			document.getElementById("mx").value=response_data.mx;
			document.getElementById("my").value=response_data.my;
			document.getElementById("mz").value=response_data.mz;
			
			document.getElementById("rx").value=response_data.rx;
			document.getElementById("ry").value=response_data.ry;
			document.getElementById("rz").value=response_data.rz;
		});
}
function set_location()
{
	mcp.set_component_location(null,create_coordinate_id(),
		document.getElementById("mx").value,document.getElementById("my").value,document.getElementById("mz").value,
		document.getElementById("rx").value,document.getElementById("ry").value,document.getElementById("rz").value);
}
function reset_move()
{	
	document.getElementById("mx").value=0;
	document.getElementById("my").value=0;
	document.getElementById("mz").value=0;
	set_location();
}
function reset_rotate()
{	
	document.getElementById("rx").value=0;
	document.getElementById("ry").value=0;
	document.getElementById("rz").value=0;
	set_location();
}
function add_movement_point()
{
	var str;
	mcp.add_movement_point(null,create_coordinate_id(),
		document.getElementById("mx").value,document.getElementById("my").value,document.getElementById("mz").value,
		document.getElementById("rx").value,document.getElementById("ry").value,document.getElementById("rz").value,
		parseFloat(document.getElementById("t").value)*1000*1000,
		[
			((str=document.getElementById("parameter0").value)!=null)?encodeURIComponent(encodeURIComponent(str)):null,
			((str=document.getElementById("parameter1").value)!=null)?encodeURIComponent(encodeURIComponent(str)):null,
			((str=document.getElementById("parameter2").value)!=null)?encodeURIComponent(encodeURIComponent(str)):null,
			((str=document.getElementById("parameter3").value)!=null)?encodeURIComponent(encodeURIComponent(str)):null
		],set_point_number);
}
function delete_movement_point()
{
	mcp.delete_movement_point(set_point_number);
}
function add_movement()
{
	mcp.add_movement(null,
		document.getElementById("starthide"		).checked,
		document.getElementById("terminatehide"	).checked,
		set_point_number);
}
function set_coordinate(component_flag,camera_flag,view_flag)
{
	document.getElementById("component_coordinate"	).checked=component_flag;
	document.getElementById("camera_coordinate"		).checked=camera_flag;
	document.getElementById("view_coordinate"		).checked=view_flag;
	get_location();
}

function set_state_radio(on,off)
{
	document.getElementById(on ).checked=true;
	document.getElementById(off).checked=false;
}

</script>

</head>

<body onload="set_coordinate(true,false,false);">

<form style="white-space:nowrap;text-align:center;vertical-align:top">

<h3>坐标系</h3>
<input type="checkbox"	id="component_coordinate" 		onclick="set_coordinate(true,false,false);"	>杆件&nbsp;
<input type="checkbox"	id="camera_coordinate" 			onclick="set_coordinate(false,true,false);"	>相机&nbsp;
<input type="checkbox"	id="view_coordinate" 			onclick="set_coordinate(false,false,true);"	>视口<br/>

<hr/><h3>杆件平移</h3>
X:<input type="number" 	id="mx"							onkeyup="set_location();"	onclick="set_location();"	style="width:100px;">
Y:<input type="number"	id="my"							onkeyup="set_location();"	onclick="set_location();"	style="width:100px;">
Z:<input type="number"	id="mz"							onkeyup="set_location();"	onclick="set_location();"	style="width:100px;"><br/>
<input type="button"	id="m_get"		value="提取"									onclick="get_location();"	>
<input type="button"	id="m"			value="复位"									onclick="reset_move();"		><br/>

<hr/><h3>杆件旋转</h3>
X:<input type="number"	id="rx"							onkeyup="set_location();"	onclick="set_location();"	style="width:100px;">
Y:<input type="number"	id="ry"							onkeyup="set_location();"	onclick="set_location();"	style="width:100px;">
Z:<input type="number"	id="rz"							onkeyup="set_location();"	onclick="set_location();"	style="width:100px;"><br/>
<input type="button"	id="r_get"		value="提取"									onclick="get_location();"	>
<input type="button"	id="r"			value="复位"									onclick="reset_rotate();"	><br/>


<hr/><h3>运动时间:
<input type="number"	id="t"			value="1000"	onkeyup="set_location();"	onclick="set_location();"	style="width:100px;">
</h3>

<br/>起始状态
<input type="radio"		id="starthide"		checked		onclick="set_location();set_state_radio('starthide','startshow');">隐藏
<input type="radio"		id="startshow"					onclick="set_location();set_state_radio('startshow','starthide');">显示

<br/>终止状态
<input type="radio"		id="terminatehide"				onclick="set_location();set_state_radio('terminatehide','terminateshow');">隐藏
<input type="radio"		id="terminateshow"	checked		onclick="set_location();set_state_radio('terminateshow','terminatehide');">显示


<hr/><h3>
当前运动点数:<output  		id="point_number"></output>
</h3>

<input  type="button"	id="add_point"		value="添加运动点"	onclick="add_movement_point();"		>
<input  type="button"	id="delete_point"	value="删除运动点"	onclick="delete_movement_point();"	><br/><br/>
<input  type="button"	id="add_move"		value="添加运动"	onclick="add_movement();"			>
<input  type="button"	id="load_location"	value="位置加载"	onclick="window.location.reload();"	>


<hr/><h3>杆件参数</h3>
参数1:<input type="text"	id="parameter0"		style="width:300px;"><br/>
参数2:<input type="text"	id="parameter1"		style="width:300px;"><br/>
参数2:<input type="text"	id="parameter2"		style="width:300px;"><br/>
参数3:<input type="text"	id="parameter3"		style="width:300px;">


</form>

</body>
</html>