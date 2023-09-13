function construct_server_caller(my_render)
{
	this.render=my_render;
	
	this.destroy=function()
	{
		this.render=null;

		this.call_server								=null;
		
		this.create_render_request_string				=null;
		this.create_render_request_by_part_string		=null;
		this.create_render_request_by_component_string	=null;
		this.create_part_request_string					=null;
		this.create_part_request_by_component_string	=null;
		this.create_component_request_string			=null;
		
		this.call_server_engine							=null;
		this.call_server_render							=null;
		this.call_server_render_by_part					=null;
		
		this.call_server_render_by_component			=null;
		
		this.call_server_part							=null;
		this.call_server_part_by_component				=null;
		this.call_server_component						=null;
	};
	this.call_server=async function(request_url,response_type_string,server_request_parameter)
	/*
			server_request_parameter
			{
			    method: "POST", 						// *GET, POST, PUT, DELETE, etc.
			    mode: "cors", 							// no-cors, *cors, same-origin 
			    cache: "no-cache",						// *default, no-cache, reload, force-cache, only-if-cached
			    credentials: "same-origin",		 		// include, *same-origin, omit
			    headers: {
			      "Content-Type": "application/json"	//"text/plain"	"image/png"	etc
			    },
			    redirect: "follow", 					// manual, *follow, error
			    referrerPolicy: "no-referrer",			// no-referrer, *no-referrer-when-downgrade,
												    	// origin, origin-when-cross-origin, same-origin, 
												    	// strict-origin, strict-origin-when-cross-origin, unsafe-url
			    body: JSON.stringify(data), 			// body data type must match "Content-Type" header
			    										// example: uploaded_canvas.toDataURL()
			  });
	*/
	{
		var my_render;
		if((my_render=this.render).terminate_flag)
			return null;
		var server_promise=await fetch(request_url,
				(typeof(server_request_parameter)!="object")?new Object():
				(server_request_parameter==null)			?new Object():
				server_request_parameter);
		if(my_render.terminate_flag)
			return null;
		if(!(server_promise.ok)){
			alert("execute call_server error,status is "+server_promise.status);
			alert(request_url);
			return null;
		}
		try{
			switch((typeof(response_type_string)!="string")?"":response_type_string){
			case "arrayBuffer":
			case "arraybuffer":
				return await server_promise.arrayBuffer();
			case "blob":
				return await server_promise.blob();
			case "formData":
			case "formdata":
				return await server_promise.formData();
			case "text":
				return await server_promise.text();
			case "json":
				return await server_promise.json();
			default:
				return null;
			}
		}catch(e){
			if(!(my_render.terminate_flag)){
				alert("parse call_server response data fail:	"+request_url);
				alert(e.toString());
			}
			return null;
		}
	};
	this.create_render_request_string=function(render_id_or_render_name,render_parameter)
	{
		var ret_val=this.render.url_with_channel+"&command=render&method=event";
		if(typeof(render_id_or_render_name)=="string")
			ret_val+="&event_render_name="+encodeURIComponent(encodeURIComponent(render_id_or_render_name));
		else
			ret_val+="&event_render_id="+render_id_or_render_name.toString();
		for(var i=0,ni=render_parameter.length;i<ni;i++)
			ret_val+="&"+render_parameter[i][0].toString()+"="+render_parameter[i][1].toString();		
		return ret_val;
	};
	this.create_render_request_by_part_string=function(render_id_or_part_name,part_id_or_driver_id,render_parameter)
	{
		var ret_val=this.render.url_with_channel+"&command=render&method=event";
		if(typeof(render_id_or_part_name)=="string"){
			ret_val+="&event_part_name="+encodeURIComponent(encodeURIComponent(render_id_or_part_name));
			ret_val+="&event_driver_id="+part_id_or_driver_id;
		}else
			ret_val+="&event_render_id="+render_id_or_part_name.toString()+"&event_part_id="+part_id_or_driver_id.toString();
		for(var i=0,ni=render_parameter.length;i<ni;i++)
			ret_val+="&"+render_parameter[i][0].toString()+"="+render_parameter[i][1].toString();		
		return ret_val;
	};
	this.create_render_request_by_component_string=function(
			component_id_or_component_name,component_driver_id,render_parameter)
	{
		var ret_val=this.render.url_with_channel+"&command=render&method=event";
		if(typeof(component_id_or_component_name)=="string")
			ret_val+="&event_component_name="+encodeURIComponent(encodeURIComponent(component_id_or_component_name));
		else
			ret_val+="&event_component_id="+component_id_or_component_name.toString();
		if(typeof(component_driver_id)!="undefined")
			ret_val+="&event_driver_id="+component_driver_id.toString();
		for(var i=0,ni=render_parameter.length;i<ni;i++)
			ret_val+="&"+render_parameter[i][0].toString()+"="+render_parameter[i][1].toString();
		return ret_val;
	};
	this.create_part_request_string=function(render_id_or_part_name,part_id_or_driver_id,part_parameter)
	{
		var ret_val=this.render.url_with_channel+"&command=part&method=event";
		if(typeof(render_id_or_part_name)=="string"){
			ret_val+="&event_part_name="+encodeURIComponent(encodeURIComponent(render_id_or_part_name));
			ret_val+="&event_driver_id="+part_id_or_driver_id;
		}else
			ret_val+="&event_render_id="+render_id_or_part_name.toString()+"&event_part_id="+part_id_or_driver_id.toString();
		for(var i=0,ni=part_parameter.length;i<ni;i++)
			ret_val+="&"+part_parameter[i][0].toString()+"="+part_parameter[i][1].toString();		
		return ret_val;
	};
	this.create_part_request_by_component_string=function(
			component_id_or_component_name,component_driver_id,part_parameter)
	{
		var ret_val=this.render.url_with_channel+"&command=part&method=event";
		if(typeof(component_id_or_component_name)=="string")
			ret_val+="&event_component_name="+encodeURIComponent(encodeURIComponent(component_id_or_component_name));
		else
			ret_val+="&event_component_id="+component_id_or_component_name.toString();
		if(typeof(component_driver_id)!="undefined")
			ret_val+="&event_driver_id="+component_driver_id.toString();
		for(var i=0,ni=part_parameter.length;i<ni;i++)
			ret_val+="&"+part_parameter[i][0].toString()+"="+part_parameter[i][1].toString();
		return ret_val;
	};
	this.create_component_request_string=function(component_name_or_id,driver_id,component_parameter)
	{
		var ret_val=this.render.url_with_channel+"&command=component&method=event";
		if(typeof(component_name_or_id)=="string")
			ret_val+="&event_component_name="+encodeURIComponent(encodeURIComponent(component_name_or_id));
		else
			ret_val+="&event_component_id="+component_name_or_id.toString();
		if(typeof(driver_id)!="undefined")
			ret_val+="&event_driver_id="+driver_id.toString();
		for(var i=0,ni=component_parameter.length;i<ni;i++)
			ret_val+="&"+component_parameter[i][0].toString()+"="+component_parameter[i][1].toString();
		return ret_val;
	};
	this.call_server_engine=async function(
			engine_parameter,response_type_string,server_request_parameter)
	{
		if(this.render.terminate_flag)
			return null;
		var request_url=this.render.url_with_channel;
		for(var i=0,ni=engine_parameter.length;i<ni;i++)
			request_url+="&"+engine_parameter[i][0].toString()+"="+engine_parameter[i][1].toString();
		
		return await this.call_server(request_url,response_type_string,server_request_parameter);
	};
	this.call_server_render=async function(
		render_id_or_render_name,render_parameter,
		response_type_string,server_request_parameter)
	{
		var request_url=this.create_render_request_string(render_id_or_render_name,render_parameter);
		return await this.call_server(request_url,response_type_string,server_request_parameter);
	};
	this.call_server_render_by_part=async function(
		render_id_or_part_name,part_id_or_driver_id,render_parameter,
		response_type_string,server_request_parameter)
	{
		var request_url=this.create_render_request_by_part_string(
				render_id_or_part_name,part_id_or_driver_id,render_parameter);
		return await this.call_server(request_url,response_type_string,server_request_parameter);
	};
	this.call_server_render_by_component=async function(
		component_id_or_component_name,component_driver_id,render_parameter,
		response_type_string,server_request_parameter)
	{
		var request_url=this.create_render_request_by_component_string(
				component_id_or_component_name,component_driver_id,render_parameter);
		return await this.call_server(request_url,response_type_string,server_request_parameter);
	};
	this.call_server_part=async function(
			render_id_or_part_name,part_id_or_driver_id,part_parameter,
			response_type_string,server_request_parameter)
	{
		var request_url=this.create_part_request_string(
					render_id_or_part_name,part_id_or_driver_id,part_parameter);
		return await this.call_server(request_url,response_type_string,server_request_parameter);
	};
	this.call_server_part_by_component=async function(
			component_id_or_component_name,component_driver_id,part_parameter,response_type_string)
	{
		var request_url=this.create_part_request_by_component_string(
					component_id_or_component_name,component_driver_id,part_parameter);
		return await this.call_server(request_url,response_type_string,server_request_parameter);
	};
	this.call_server_component=async function(component_name_or_id,driver_id,
			component_parameter,response_type_string,server_request_parameter)
	{
		var request_url=this.create_component_request_string(
					component_name_or_id,driver_id,component_parameter);
		return await this.call_server(request_url,response_type_string,server_request_parameter);
	};
};
