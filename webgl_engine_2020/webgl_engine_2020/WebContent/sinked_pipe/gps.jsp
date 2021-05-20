<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>

<html>
<head>
<meta charset="UTF-8">
<title>orientation</title>

<script type="text/javascript">

var version=0;

function process_position(position)
{
	var latitude	=position.coords.latitude;
	var longitude	=position.coords.longitude;
	var altitude	=position.coords.altitude;
	
	document.getElementById("version").value	="version:"+version;
	document.getElementById("longitude").value	="longitude:"+longitude;
	document.getElementById("latitude").value	="latitude: "+latitude;
	
	if(typeof(altitude)=="number")
		document.getElementById("altitude").value	="altitude: "+altitude;
	else{
		altitude=20;
		document.getElementById("altitude").value	="altitude is wrong";
	}

	var url="sensor.jsp?operation=";
	url+=((version++)==0)?"set_gps_origin":"set_origin_by_gps";
	url+="&sensor_data="+latitude+","+longitude+","+altitude;
	
	try{
		var my_ajax=new XMLHttpRequest();
		my_ajax.onreadystatechange=function()
		{
			try{
				if(my_ajax.readyState==4)
					if(my_ajax.status==200)
						document.getElementById("result").value="Result:"+my_ajax.responseText.trim();
			}catch(e){
				alert("error:"+e.toString());
			}
		};
		my_ajax.open("GET",url,true);
		my_ajax.send(null);
	}catch(e){
		;
	};
}

function init()
{
	navigator.geolocation.watchPosition(process_position);
}

</script>

</head>

<body onload="init();">

<div align="center">

<h1>
<br/><br/><br/>
<output id="longitude"></output><br/>
<output id="latitude"></output><br/>
<output id="altitude"></output><br/><br/>
<output id="version"></output><br/><br/>
<output id="result"></output><br/><br/>
</h1>
</div>

</body>

</html>