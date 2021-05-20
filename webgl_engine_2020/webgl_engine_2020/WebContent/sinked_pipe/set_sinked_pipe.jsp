<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">
<meta http-equiv="Pragma" content="no-cache">

<title>设置沉管相关信息</title>

<script type="text/javascript">

function call_proxy(operation_str,data_flag)
{
	var sensor_data="";

	sensor_data+=document.getElementById("p0x").value.toString()+",";
	sensor_data+=document.getElementById("p0y").value.toString()+",";
	sensor_data+=document.getElementById("p0z").value.toString()+",";
		
	sensor_data+=document.getElementById("pxx").value.toString()+",";
	sensor_data+=document.getElementById("pxy").value.toString()+",";
	sensor_data+=document.getElementById("pxz").value.toString()+",";
		
	sensor_data+=document.getElementById("pyx").value.toString()+",";
	sensor_data+=document.getElementById("pyy").value.toString()+",";
	sensor_data+=document.getElementById("pyz").value.toString();

	window.parent.render.call_server_component("radar_component","all",
			[["operation",operation_str],["sensor_data",sensor_data]]);
}

</script>

</head>

<body>

<div style="white-space:nowrap;text-align:center;vertical-align:top">


<h3>设置传感器源信息</h3>
<input  type="button" 	value="设置传感器组件"		onclick="call_proxy('sensor_component',false);">
<input  type="button" 	value="设置传感器坐标"		onclick="call_proxy('sensor_position',true);">

<hr/>
<h3>设置传感器目标信息</h3>
<input  type="button" 	value="设置观测目标组件"	onclick="call_proxy('target_component',false);">
<input  type="button" 	value="设置观测目标坐标"	onclick="call_proxy('target_position',true);">

<hr/>
<h3>设置修改组件</h3>
<input  type="button" 	value="设置位置修改组件"	onclick="call_proxy('modify_component',false);">
<hr/>


<h3>设置观测数据</h3>
<input  type="button" 	value="设置观测数据"		onclick="call_proxy('set_location_by_points',true);">
<hr/>

<h3>坐标</h3>

坐标P0:
<input type="number"	id="p0x"		value="0"	style="width:100px;">
<input type="number"	id="p0y"		value="0"	style="width:100px;">
<input type="number"	id="p0z"		value="0"	style="width:100px;"><br/>
	
坐标PX:
<input type="number"	id="pxx"		value="1"	style="width:100px;">
<input type="number"	id="pxy"		value="0"	style="width:100px;">
<input type="number"	id="pxz"		value="0"	style="width:100px;"><br/>
	
坐标PY:
<input type="number"	id="pyx"		value="0"	style="width:100px;">
<input type="number"	id="pyy"		value="1"	style="width:100px;">
<input type="number"	id="pyz"		value="0"	style="width:100px;">	

</div>

</body>
</html>