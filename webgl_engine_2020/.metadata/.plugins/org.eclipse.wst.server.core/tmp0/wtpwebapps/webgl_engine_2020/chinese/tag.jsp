<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>

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
	String movement_component_id=request.getParameter("component_id");
	String mount_call_processor_string;
	if((mount_call_processor_string=request.getParameter("windows"))==null)
		mount_call_processor_string="parent";
	if(mount_call_processor_string.compareTo("top")==0)
		mount_call_processor_string="window.top.";
	else if(mount_call_processor_string.compareTo("parent")==0)
		mount_call_processor_string="window.parent.";
	else if(mount_call_processor_string.compareTo("this")==0)
		mount_call_processor_string="";
	else
		mount_call_processor_string ="window.frames[\""+mount_call_processor_string+"\"].";
	
	out.print("var mcp="+mount_call_processor_string+
			"render.component_call_processor["+movement_component_id+"];");
	out.println("var id="+request.getParameter("movement_id")+";");
};
%>

function update()
{
	var node_name,sound_file_name,description;
	if((node_name=document.getElementById("node_name").value)=="")
		node_name="no_node_name";
	if((sound_file_name=document.getElementById("sound_file_name").value)=="")
		sound_file_name="no_sound_file";
	if((description=document.getElementById("description").value)=="")
		description="no_description";
	
	mcp.update_node_name_sound_description(id,node_name,sound_file_name,description);
}

function do_init()
{
	mcp.get_node_name_sound_description(id,
			function(response_data,render)
			{
				document.getElementById("node_name").value		=response_data.node_name;
				document.getElementById("sound_file_name").value=response_data.sound_file_name;
				document.getElementById("description").value	=response_data.description;
			});
}
</script>

</head>

<body onload="do_init();" style="white-space:nowrap;text-align:center;vertical-align:top">

<form>
<br/><br/>运动标记:<input type="text"	id="node_name"			onchange="update();"	style="width:300px;">
<br/><br/>配音文件:<input type="text"	id="sound_file_name"	onchange="update();"	style="width:300px;">
<br/><br/>运动描述:<textarea 			id="description"		onchange="update();"	style="width:300px;height:300px"></textarea>
</form>

<br/><br/><a href="#" title="结束" onclick="window.open(mcp.mount_edit_url(),'_self');">结束</a>

</body>
</html>