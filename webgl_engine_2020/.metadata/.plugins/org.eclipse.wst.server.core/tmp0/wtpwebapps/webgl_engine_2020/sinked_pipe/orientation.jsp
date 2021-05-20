<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>

<html>
<head>
<meta charset="UTF-8">
<title>orientation</title>

<script type="text/javascript">

function init()
{
	window.addEventListener(
			"deviceorientation",
			function(event)
			{
				event.preventDefault();
				
				var url="sensor.jsp?operation=set_orientation_by_angles&sensor_data=";
				url+=event.alpha+","+event.beta+","+event.gamma;
				
				try{
					var my_ajax=new XMLHttpRequest();
					my_ajax.open("GET",url,true);
					my_ajax.send(null);
				}catch(e){
					;
				};
				document.getElementById("alpha").value	="alpha:"+event.alpha;
				document.getElementById("beta").value	="beta: "+event.beta;
				document.getElementById("gamma").value	="gamma:"+event.gamma;
			},
			true);
}

</script>

</head>

<body onload="init();">

<div align="center">

<h1>
<br/><br/><br/>
<output id="alpha"></output><br/>
<output id="beta"></output><br/>
<output id="gamma"></output><br/>
</h1>
</div>

</body>

</html>