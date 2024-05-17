
function construct_server_caller(my_render)
{
	this.render=my_render;
	
	this.create_parameter_string=function(my_parameter)
	{
		var ret_val="";
		if(Array.isArray(my_parameter))
			for(var i=0,ni=my_parameter.length;i<ni;i++)
				if(Array.isArray(my_parameter[i])){
					var key		=my_parameter[i][0];
					var value	=my_parameter[i][1];
					switch(typeof(value)){
					case "string":
						ret_val+="&"+key.toString()+"="+value.trim();
						break;
					case "number":
					case "bigint":
					case "boolean":
					case "symbol":
						ret_val+="&"+key.toString()+"="+value.toString();
						break;
					case "object":
						ret_val+="&"+key.toString()+"="+JSON.stringify(value);
						break;
					case "undefined":
					case "function":
					default:
						break;
					}
				}
		else if(typeof(my_parameter)=="object"){
			for(var key in my_parameter){
				var value=my_parameter[key];
				switch(typeof(value)){
				case "string":
					ret_val+="&"+key+"="+value;
					break;
				case "number":
				case "bigint":
				case "boolean":
				case "symbol":
					ret_val+="&"+key+"="+value.toString();
					break;
				case "object":
					ret_val+="&"+key+"="+JSON.stringify(value);
					break;
				case "undefined":
				case "function":
				default:
					break;
				}
			}
		}
		return ret_val;
	}
	this.call_server=async function(request_url,response_type_string,server_request_parameter)
	{
		var my_render;
		if((my_render=this.render).terminate_flag)
			return null;
		
		var server_promise=await fetch(request_url,
				(typeof(server_request_parameter)!="object")?(my_render.fetch_parameter.call_server):
				(server_request_parameter==null)			?(my_render.fetch_parameter.call_server):
				(Object.assign(server_request_parameter,my_render.fetch_parameter.call_server)));
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
			
		return ret_val+this.create_parameter_string(render_parameter);
	};
	this.create_render_request_by_part_string=function(render_id_or_part_name,part_id_or_driver_id,render_parameter)
	{
		var ret_val=this.render.url_with_channel+"&command=render&method=event";
		if(typeof(render_id_or_part_name)=="string"){
			ret_val+="&event_part_name="+encodeURIComponent(encodeURIComponent(render_id_or_part_name));
			ret_val+="&event_driver_id="+part_id_or_driver_id;
		}else
			ret_val+="&event_render_id="+render_id_or_part_name.toString()+"&event_part_id="+part_id_or_driver_id.toString();
		
		return ret_val+this.create_parameter_string(render_parameter);
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
		
		return ret_val+this.create_parameter_string(render_parameter);
	};
	this.create_part_request_string=function(render_id_or_part_name,part_id_or_driver_id,part_parameter)
	{
		var ret_val=this.render.url_with_channel+"&command=part&method=event";
		if(typeof(render_id_or_part_name)=="string"){
			ret_val+="&event_part_name="+encodeURIComponent(encodeURIComponent(render_id_or_part_name));
			ret_val+="&event_driver_id="+part_id_or_driver_id;
		}else
			ret_val+="&event_render_id="+render_id_or_part_name.toString()+"&event_part_id="+part_id_or_driver_id.toString();
		
		return ret_val+this.create_parameter_string(part_parameter);
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
		
		return ret_val+this.create_parameter_string(part_parameter);
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
		
		return ret_val+this.create_parameter_string(component_parameter);
	};
	this.call_server_engine=async function(
			engine_parameter,response_type_string,server_request_parameter)
	{
		if(this.render.terminate_flag)
			return null;
		var request_url=this.render.url_with_channel+this.create_parameter_string(engine_parameter);
		
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
