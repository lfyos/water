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
	
	this.call_server=async function(request_url,response_type_string,upload_data,server_request_parameter)
	{
		if((typeof(server_request_parameter)!="object")||(server_request_parameter==null))
			server_request_parameter=new Object();
				
		if((typeof(upload_data)!="undefined")&&(upload_data!=null))
			server_request_parameter.body=upload_data;
			
		if((typeof(response_type_string)=="undefined")||(response_type_string==null))
			response_type_string="text";
		
		var server_promise=await fetch(request_url,server_request_parameter);
		if(!(server_promise.ok)){
			if(this.render.terminate_flag)
				return null;

			alert("request call_server error,status is "+server_promise.status);
			alert(request_url);
			return null;
		}
		var response_data;
		try{
			switch(response_type_string){
			case "arrayBuffer":
				response_data= await server_promise.arrayBuffer();
				break;
			case "blob":
				response_data= await server_promise.blob();
				break;
			case "formData":
				response_data= await server_promise.formData();
				break;
			case "text":
				response_data= await server_promise.text();
				break;
			case "json":
				response_data= await server_promise.json();
				break;
			default:
				response_data= null;
				break;
			}
		}catch(e){
			if(this.render.terminate_flag)
				return null;
			response_data= null;
			
			alert("parse call_server response data fail:	"+request_url);
			alert(e.toString());
		}
		return response_data;
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
			engine_parameter,response_type_string,upload_data,server_request_parameter)
	{
		if(this.render.terminate_flag)
			return null;
		var request_url=this.render.url_with_channel;
		for(var i=0,ni=engine_parameter.length;i<ni;i++)
			request_url+="&"+engine_parameter[i][0].toString()+"="+engine_parameter[i][1].toString();
		
		return await this.call_server(request_url,response_type_string,upload_data,server_request_parameter);
	};
	this.call_server_render=async function(
		render_id_or_render_name,render_parameter,
		response_type_string,upload_data,server_request_parameter)
	{
		var request_url=this.create_render_request_string(render_id_or_render_name,render_parameter);
		return await this.call_server(request_url,response_type_string,upload_data,server_request_parameter);
	};
	this.call_server_render_by_part=async function(
		render_id_or_part_name,part_id_or_driver_id,render_parameter,
		response_type_string,upload_data,server_request_parameter)
	{
		var request_url=this.create_render_request_by_part_string(
				render_id_or_part_name,part_id_or_driver_id,render_parameter);
		return await this.call_server(request_url,response_type_string,upload_data,server_request_parameter);
	};
	this.call_server_render_by_component=async function(
		component_id_or_component_name,component_driver_id,render_parameter,
		response_type_string,upload_data,server_request_parameter)
	{
		var request_url=this.create_render_request_by_component_string(
				component_id_or_component_name,component_driver_id,render_parameter);
		return await this.call_server(request_url,response_type_string,upload_data,server_request_parameter);
	};
	this.call_server_part=async function(
			render_id_or_part_name,part_id_or_driver_id,part_parameter,
			response_type_string,upload_data,server_request_parameter)
	{
		var request_url=this.create_part_request_string(
					render_id_or_part_name,part_id_or_driver_id,part_parameter);
		return await this.call_server(request_url,response_type_string,upload_data,server_request_parameter);
	};
	this.call_server_part_by_component=async function(
			component_id_or_component_name,component_driver_id,part_parameter,
			response_type_string,upload_data)
	{
		var request_url=this.create_part_request_by_component_string(
					component_id_or_component_name,component_driver_id,part_parameter);
		return await this.call_server(request_url,response_type_string,upload_data,server_request_parameter);
	};
	this.call_server_component=async function(component_name_or_id,driver_id,
			component_parameter,response_type_string,upload_data,server_request_parameter)
	{
		var request_url=this.create_component_request_string(
					component_name_or_id,driver_id,component_parameter);
		return await this.call_server(request_url,response_type_string,upload_data,server_request_parameter);
	};
};
